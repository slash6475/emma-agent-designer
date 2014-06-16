package emma.mapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.resources.UnmappedResource;

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
	private PBMap<MapperNode,MapperScope,Integer> scopeLiterals;
	private PBMap<MapperNode,UnmappedResource, Integer> resLiterals;
	private HashMap<Scope,String> addresses;
	/**
	 * Create a PBSolver
	 * @param nodes
	 * @param scopes
	 * @throws MappingNotFoundException if PBSolver trivially determines that the mapping is impossible,
	 * (for example, the list of possible nodes for a scope is empty), or PBSolver was unable to create a PBOFile.
	 */
	public PBSolver(Set<MapperNode> nodes, Set<MapperScope> scopes) throws MappingNotFoundException{
		this.counter = 0;
		this.nodes = nodes;
		this.scopes = scopes;
		this.pboFile =  "MAP_"+System.currentTimeMillis();
		this.scopeLiterals = new PBMap<>();
		this.resLiterals = new PBMap<>();
		this.addresses=new HashMap<>();
		Iterator<MapperScope> itScope = scopes.iterator();
		while(itScope.hasNext()){
			MapperScope s = itScope.next();
			if(s.getAddress()!=null){
				addresses.put(s.getScope(), s.getAddress());
			}
			Iterator<MapperNode> itNode = s.getAuthorizedNodes().iterator();
			while(itNode.hasNext()){
				MapperNode n = itNode.next();
				counter++;
				scopeLiterals.put(n,s,counter);
				Iterator<Place> itP = s.getScope().getPlaces().iterator();
				while(itP.hasNext()){
					UnmappedResource r = itP.next().getData();
					if(!r.isImported()){
						counter++;
						resLiterals.put(n,r,counter);
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
				MapperNode n = itCap.next();
				if(n.getDisponibilityTypes().size()>0){
					constraint+=n.getDisponibilityTypes().size()+1;
				}
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
						out.print("1 x"+scopeLiterals.get(node,s)+" ");
					}
					out.println("= "+s.getMultiplicity()+";");
				}
				itNode = s.getAuthorizedNodes().iterator();
				StringBuffer equiv= new StringBuffer();
				StringBuffer nequiv= new StringBuffer();
				int lit;
				while(itNode.hasNext()){
					MapperNode n = itNode.next();
					lit = scopeLiterals.get(n, s);
					equiv.append("1 x"+lit);
					nequiv.append("1 ~x"+lit);
					Iterator<Place> itP = s.getScope().getPlaces().iterator();
					while(itP.hasNext()){
						UnmappedResource r = itP.next().getData();
						if(!r.getClass().getSimpleName().equals("S")){
							lit = resLiterals.get(n, r);
							equiv.append(" x"+lit);
							nequiv.append(" ~x"+lit);
						}
					}
					equiv.append(" ");
					nequiv.append(" ");
				}
				out.println("* Scope & resources integrity constraints");
				out.println(equiv.toString()+nequiv.toString()+"= "+(s.getAuthorizedNodes().size()*s.getMultiplicity())+";");
			}
			itNode = nodes.iterator();
			while(itNode.hasNext()){
				MapperNode n = itNode.next();
				out.println("* Node ["+n.getNode().getIp()+"]:");
				out.println(this.getResourceQuantityConstraint(n));
			}
		} catch (FileNotFoundException e) {
			throw new MappingNotFoundException(e.getMessage());
		} finally{
			if(out!=null){
				out.close();
			}
			
		}
	}
	
	private String getResourceQuantityConstraint(MapperNode n){
		Iterator<String> itType = n.getNode().getResourceRoots().iterator();
		StringBuffer qty = new StringBuffer();
		StringBuffer size = new StringBuffer();
		while(itType.hasNext()){
			StringBuffer qtyLine = new StringBuffer();
			String type = itType.next();
			Iterator<UnmappedResource> itRes = n.getResourcesToMap(type).iterator();
			qty.append("* Resource Quantity Constraint : type '"+type+"'\n");
			while(itRes.hasNext()){
				UnmappedResource r = itRes.next();
				int lit = resLiterals.get(n,r);
				qtyLine.append("1 x");
				qtyLine.append(lit);
				qtyLine.append(" ");
				size.append(r.getSize());
				size.append(" x");
				size.append(lit);
				size.append(" ");
			}
			if(!qtyLine.toString().equals("")){
				qty.append(qtyLine.toString());
				qty.append("<= ");
				qty.append(n.getDisponibility(type));
				qty.append(";\n");
			}
		}
		if(!size.toString().equals("")){
			qty.append("* Resource Size Constraint\n");
			qty.append(size.toString());
			qty.append("<= ");
			qty.append(n.getNode().getAvailableMemorySpace());
			qty.append(";\n");
		}
		return qty.toString();
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
							if(coef > 0){
								str.append(" "+coef+" x"+scopeLiterals.get(n1,s1)+" x"+scopeLiterals.get(n2,s2));
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
									str.append("1 x"+scopeLiterals.get(n1,s1)+" x"+scopeLiterals.get(n2,s2)+" ");
								}
							}
						}
					}
				}
			}
		}
		if(!str.toString().equals("")){
			str.append("= 0;");	
		}
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
		Mapping m = new Mapping(addresses);
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
						if(scopeLiterals.containsValue(literal)){
							m.add(scopeLiterals.getSecondKeyByValue(literal).getScope(), scopeLiterals.getFirstKeyByValue(literal).getNode());
						}
					}
				}
				m.finalize();
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