package emma.mapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
import emma.model.resources.tomap.ResourceToMap;
import emma.petri.model.Place;

/**
 * This class creates a PBO file representing the mapping problem,
 * call a pseudo-boolean solver and catch the result to determine a mapping
 * @author pierrotws
 *
 */
public class PBSolver {
	//Les literaux sont des entiers (1 à ...)
	private int counter;
	private String pboFile;
	//La collection des noeuds;
	private Set<MapperNode> nodes;
	//La collection de scopes;
	private Set<MapperScope> scopes;
	private PBMap<MapperNode,MapperScope,Integer,Integer> scopeLiterals;
	private PBMap<MapperNode,ResourceToMap,Integer, Integer> resLiterals;
	/**
	 * Create a PBSolver
	 * @param nodes
	 * @param scopes
	 * @throws MappingNotFoundException if PBSolver trivially determines that the mapping is impossible,
	 * (for example, the list of possible nodes for a scope is empty), or PBSolver was unable to create a PBOFile.
	 */
	public PBSolver(Set<MapperNode> nodes, Set<MapperScope> scopes) throws MappingNotFoundException{
		this.counter=0;
		this.nodes = nodes;
		this.scopes = scopes;
		this.pboFile =  "MAP_"+System.currentTimeMillis();
		this.scopeLiterals = new PBMap<>();
		this.resLiterals = new PBMap<>();
		Iterator<MapperScope> itScope = scopes.iterator();
		while(itScope.hasNext()){
			MapperScope s = itScope.next();
			Iterator<MapperNode> itNode = s.getAuthorizedNodes().iterator();
			while(itNode.hasNext()){
				MapperNode n = itNode.next();
				for(int i=1;i<=s.getMultiplicity();i++){
					counter++;
					scopeLiterals.put(n,s,i,counter);
				}
				Iterator<Place> itP = s.getScope().getPlaces().iterator();
				while(itP.hasNext()){
					ResourceToMap r = itP.next().getData();
					if(!r.isImported()){
						for(int i=1;i<=s.getMultiplicity();i++){
							counter++;
							resLiterals.put(n,r,i,counter);
						}
					}
				}
			}
		}
		//Generate the PBO File
		this.generatePBOFile();		
	}
	
	private void generatePBOFile() throws MappingNotFoundException {
		PrintWriter out = null;
		Iterator<MapperScope> itScope;
		Iterator<MapperNode> itNode;
		try {
			out = new PrintWriter(pboFile+".pbo");
			//|vars| = litéraux scopes + litéraux res. |constraints| = 2*|S| /*cond 2 + cond 3*/ + |J_n^s|*|N|
			int constraint= 2*scopes.size();
			Iterator<MapperNode> itCap = nodes.iterator();
			while(itCap.hasNext()){
				constraint+=itCap.next().getDisponibilityTypes().size();
			}
			String topConstraint = this.getTopologyConstraint();
			if(!topConstraint.equals("")){
				constraint++;
			}
			out.println("* #variable= "+(scopeLiterals.size()+resLiterals.size())+" #constraint= "+constraint);
			String min = getFunctionToMin();
			if(!min.equals("")){
				out.println("min:"+min+";");
			}
			Iterator<Integer> itlit = scopeLiterals.values().iterator();
			while(itlit.hasNext()){
				Integer lit = itlit.next();
				out.println("* x"+lit+" => N:'"+scopeLiterals.getFirstKeyByValue(lit).getNode().getIp()+"', S:'"+scopeLiterals.getSecondKeyByValue(lit).getScope().getName()+"'");
			}
			itlit = resLiterals.values().iterator();
			while(itlit.hasNext()){
				Integer lit = itlit.next();
				out.println("* x"+lit+" => N:'"+resLiterals.getFirstKeyByValue(lit).getNode().getIp()+"', R:'"+resLiterals.getSecondKeyByValue(lit).getName()+" ("+resLiterals.getSecondKeyByValue(lit).getClass().getSimpleName()+")'");
			}
			//CONSTRAINTS
			if(!topConstraint.equals("")){
				out.println("* TOPOLOGY constraint : 2 interconnected scopes should be mapped on 2 interconnected nodes");
				out.println(topConstraint);
			}
			
			itScope = scopes.iterator();
			while(itScope.hasNext()){
				MapperScope s = itScope.next();
				out.println("* Scope ["+s.getScope().getName()+"]:");
				itNode = s.getAuthorizedNodes().iterator();
				if(!itNode.hasNext()){
					out.println("* ERROR DETECTED: authorizedNode List for scope \""+s.getScope().getName()+"\" is empty!");
					throw new MappingNotFoundException("ERROR DETECTED: authorizedNode List for scope \""+s.getScope().getName()+"\" is empty!");
				}
				else{
					while(itNode.hasNext()){
						MapperNode node = itNode.next();
						for(int i=1; i<=s.getMultiplicity();i++){
							out.print("1 x"+scopeLiterals.get(node,s,i)+" ");
						}
					}
					//MULTIPLICITY OF THE SCOPE
					out.println("= "+s.getMultiplicity()+";");
				}
				itNode = s.getAuthorizedNodes().iterator();
				StringBuffer equiv= new StringBuffer();
				StringBuffer nequiv= new StringBuffer();
				int lit;
				while(itNode.hasNext()){
					MapperNode n = itNode.next();
					for(int i=1; i<=s.getMultiplicity();i++){
						lit = scopeLiterals.get(n, s,i);
						equiv.append("1 x"+lit);
						nequiv.append("1 ~x"+lit);
						Iterator<Place> itP = s.getScope().getPlaces().iterator();
						while(itP.hasNext()){
							ResourceToMap r = itP.next().getData();
							if(!r.getClass().getSimpleName().equals("S")){
								lit = resLiterals.get(n, r, i);
								equiv.append(" x"+lit);
								nequiv.append(" ~x"+lit);
							}
						}
						equiv.append(" ");
						nequiv.append(" ");
					}
				}
				out.println("* Scope & resources integrity constraints");
				out.println(equiv.toString()+nequiv.toString()+"= "+(s.getAuthorizedNodes().size()*s.getMultiplicity())+";");
			}
			itNode = nodes.iterator();
			while(itNode.hasNext()){
				MapperNode n = itNode.next();
				out.println("* Node ["+n.getNode().getIp()+"]:");
				Iterator<String> itType = n.getNode().getResourceRoots().iterator();
				while(itType.hasNext()){
					StringBuffer str = new StringBuffer();
					String type = itType.next();
					Iterator<ResourceToMap> itRes = n.getResourcesToMap(type).iterator();
					while(itRes.hasNext()){
						ResourceToMap r = itRes.next();
						for(int i=1; resLiterals.containsKey(n,r,i); i++){
							str.append("1 x"+resLiterals.get(n,r,i)+" ");
						}
					}
					if(!str.toString().equals("")){
						str.append("<= "+n.getDisponibility(type)+";");
						out.println(str.toString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new MappingNotFoundException(e.getMessage());
		} finally{
			if(out!=null){
				out.close();
			}
		}
	}

	private String getFunctionToMin(){
		StringBuffer str = new StringBuffer();
		Iterator<MapperScope> itScope = scopes.iterator();
		Iterator<MapperNode> itNode;
		while(itScope.hasNext()){
			MapperScope s1 = itScope.next();
			itNode = s1.getAuthorizedNodes().iterator();
			while(itNode.hasNext()){
				MapperNode n1 = itNode.next();
				Iterator<MapperScope> itScope2 = scopes.iterator();
				while(itScope2.hasNext()){
					MapperScope s2 = itScope2.next();
					if(s1!=s2){
						Iterator<MapperNode> itNode2 = s2.getAuthorizedNodes().iterator();
						while(itNode2.hasNext()){
							MapperNode n2 = itNode2.next();
							int coef = n1.getDistance(n2)*s1.getCost(s2);
							System.out.println("COEF ["+s1.getScope().getName()+","+n1.getNode().getIp()+"] ["+s2.getScope().getName()+","+n2.getNode().getIp()+"] -> "+coef);
							if(coef > 0){
								for(int i=1; i<=s1.getMultiplicity();i++){
									for(int j=1; j<=s2.getMultiplicity();j++){
										str.append(" "+coef+" x"+scopeLiterals.get(n1,s1,i)+" x"+scopeLiterals.get(n2,s2,j));
									}
								}
							}
						}
					}
				}
			}
		}
		return str.toString();
	}
	
	private String getTopologyConstraint(){
		StringBuffer str = new StringBuffer();
		Iterator<MapperScope> itScope = scopes.iterator();
		while(itScope.hasNext()){
			MapperScope s1 = itScope.next();
			Iterator<MapperNode> itNode = s1.getAuthorizedNodes().iterator();
			while(itNode.hasNext()){
				MapperNode n1 = itNode.next();
				Iterator<MapperScope> itScope2 = scopes.iterator();
				while(itScope2.hasNext()){
					MapperScope s2 = itScope2.next();
					if(s1!=s2){
						Iterator<MapperNode> itNode2 = s2.getAuthorizedNodes().iterator();
						while(itNode2.hasNext()){
							MapperNode n2 = itNode2.next();
							if(n1.getDistance(n2)==-1){
								if(s1.getCost(s2)>0){
									for(int i=1; i<=s1.getMultiplicity();i++){
										for(int j=1; j<=s2.getMultiplicity();j++){
											str.append("1 x"+scopeLiterals.get(n1,s1,i)+" x"+scopeLiterals.get(n2,s2,j)+" ");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		str.append("= 0;");
		return str.toString();
	}

	/**
	 * This method calls an extern solver, giving in parameter the path of the PBO 
	 * file generated by the constructor of this class, and read its output (then save the output in a .sol file)
	 * @return the mapping corresponding to the solution found by the extern solver
	 * @throws MappingNotFoundException if the extern solver has found no solution
	 */
	public Mapping solve() throws MappingNotFoundException{
		PrintWriter out = null;
		Mapping m = new Mapping();
		String l;
		Process solver;
		try{
			out = new PrintWriter(pboFile+".sol");
			solver = Runtime.getRuntime().exec("java -jar lib/sat4j-pb.jar "+pboFile+".pbo");
			BufferedReader reader = new BufferedReader(new InputStreamReader(solver.getInputStream()));
			do{
				l = reader.readLine();
				out.println(l);
			}while(!l.startsWith("s"));
			if(l.equals("s OPTIMUM FOUND") || l.equals("s SATISFIABLE")){
				do{
					l = reader.readLine();
					out.println(l);
				}while(!l.startsWith("v"));
				String[] lits = l.split(" ");
				for(String lit : lits){
					if(lit.charAt(0)=='x'){
						Integer literal = Integer.parseInt(lit.substring(1));
						if(resLiterals.containsByValue(literal)){
							m.add(resLiterals.getFirstKeyByValue(literal).getNode(),resLiterals.getSecondKeyByValue(literal));
						}
					}
				}
			}
			else{
				out.println("No solution found");
				throw new MappingNotFoundException("No solution found");
			}
		} catch (IOException e){
			throw new MappingNotFoundException(e.getMessage());
		} finally{
			if(out!=null){
				out.close();
			}
		}
		return m;
	}
}		