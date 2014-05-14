package emma.mapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Transition;
import emma.tools.PairedBiMap;

public class PBSolver {
	//Les literaux sont des entiers (1 à ...)
	private int counter;
	private String pboFile;
	//La collection des noeuds;
	private Collection<Node> nodes;
	//La collection de scopes;
	private Collection<Scope> scopes;
	
	private HashMap<Node,Capacity> capacities;
	private PairedBiMap<Node,Node,Integer> distanceMatrix;
	private PairedBiMap<Scope, Scope, Integer> costMatrix;
	private HashMap<Scope,List<Node>> authorizedNodes;
	private PairedBiMap<Node,String,List<Resource>> nodeResToMapLists;
	
	private PairedBiMap<Node,Scope,Integer> scopeLiterals;
	private PairedBiMap<Node,Resource, Integer> resLiterals;
	
	public PBSolver(Collection<Node> nodes, Collection<Scope> scopes) throws MappingNotFoundException{
		this.counter=0;
		this.nodes = nodes;
		this.scopes = scopes;
		this.pboFile =  "MAP_"+System.currentTimeMillis();
		this.capacities = new HashMap<>();
		this.distanceMatrix= new PairedBiMap<>();
		this.authorizedNodes=new HashMap<>();
		this.costMatrix = new PairedBiMap<>();
		this.nodeResToMapLists = new PairedBiMap<>();
		this.scopeLiterals = new PairedBiMap<>();
		this.resLiterals = new PairedBiMap<>();
		//Put data in HashMaps
		this.feedNodesData();
		this.feedScopesData();
		//Generate the PBO File
		this.generatePBOFile();
	
	}
	
	private void generatePBOFile() throws MappingNotFoundException {
		PrintWriter out = null;
		String str;
		Iterator<Scope> itScope;
		Iterator<Node> itNode;
		try {
			out = new PrintWriter(pboFile+".pbo");
			//|vars| = litéraux scopes + litéraux res. |constraints| = 2*|S| /*cond 2 + cond 3*/ + |J_n^s|*|N|
			int constraint= 2*scopes.size();
			Iterator<Capacity> itCap = capacities.values().iterator();
			while(itCap.hasNext()){
				constraint+=itCap.next().getDynamicTypes().size();
			}
			String topConstraint = this.getTopologyConstraint();
			if(!topConstraint.equals("")){
				constraint++;
			}
			out.println("* #variable= "+(scopeLiterals.size()+resLiterals.size())+" #constraint= "+constraint);
			str = getFunctionToMin();
			if(!str.equals("")){
				out.println("min:"+str+";");
			}
			
			
			//CONSTRAINTS
			
			if(!topConstraint.equals("")){
				out.println("* TOPOLOGY constraint : 2 interconnected scopes should be mapped on 2 interconnected nodes");
				out.println(topConstraint+"= 0;");
			}
			
			itScope = scopes.iterator();
			while(itScope.hasNext()){
				Scope s = itScope.next();
				out.println("* Scope ["+s.getID()+","+s.getName()+"]:");
				itNode = authorizedNodes.get(s).iterator();
				if(!itNode.hasNext()){
					out.println("* ERROR DETECTED: authorizedNode List for scope \""+s.getName()+"\" is empty!");
					throw new MappingNotFoundException("ERROR DETECTED: authorizedNode List for scope is empty!");
				}
				else{
					out.print("1 x"+scopeLiterals.get(itNode.next(),s));
					while(itNode.hasNext()){
						out.print(" 1 x"+scopeLiterals.get(itNode.next(),s));
					}
					//MULTIPLICITY OF THE SCOPE
					out.println(" = "+1+";");
				}
				itNode = authorizedNodes.get(s).iterator();
				StringBuffer equiv= new StringBuffer();
				StringBuffer nequiv= new StringBuffer();
				int lit;
				while(itNode.hasNext()){
					Node n = itNode.next();
					lit = scopeLiterals.get(n, s);
					equiv.append("1 x"+lit);
					nequiv.append("1 ~x"+lit);
					Iterator<Place> itP = s.getPlaces().iterator();
					while(itP.hasNext()){
						Resource r = itP.next().getData();
						if(!r.getClass().getSimpleName().equals("S")){
							lit = resLiterals.get(n, r);
							equiv.append(" x"+lit);
							nequiv.append(" ~x"+lit);
						}
					}
					equiv.append(" ");
					nequiv.append(" ");
				}
				out.println(equiv.toString()+nequiv.toString()+"= "+authorizedNodes.get(s).size()+";");
			}
			itNode = nodes.iterator();
			while(itNode.hasNext()){
				Node n = itNode.next();
				out.println("* Node ["+n.getIp()+"]:");
				Capacity c = capacities.get(n);
				Iterator<String> itType = c.getDynamicTypes().iterator();
				while(itType.hasNext()){
					String type = itType.next();
					Iterator<Resource> itRes = nodeResToMapLists.get(n, type).iterator();
					while(itRes.hasNext()){
						out.print("1 x"+resLiterals.get(n, itRes.next())+" ");
					}
					out.println("<= "+c.getDisponibility(type)+";");
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
		Iterator<Scope> itScope = scopes.iterator();
		Iterator<Node> itNode;
		while(itScope.hasNext()){
			Scope s1 = itScope.next();
			itNode = authorizedNodes.get(s1).iterator();
			while(itNode.hasNext()){
				Node n1 = itNode.next();
				Iterator<Scope> itScope2 = scopes.iterator();
				while(itScope2.hasNext()){
					Scope s2 = itScope2.next();
					if(s1!=s2){
						Iterator<Node> itNode2 = authorizedNodes.get(s2).iterator();
						while(itNode2.hasNext()){
							Node n2 = itNode2.next();
							int coef = distanceMatrix.get(n1,n2)*costMatrix.get(s1,s2);
							System.out.println("COEF ["+s1.getName()+","+n1.getIp()+"] ["+s2.getName()+","+n2.getIp()+"] -> "+coef);
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
		Iterator<Scope> itScope = scopes.iterator();
		while(itScope.hasNext()){
			Scope s1 = itScope.next();
			Iterator<Node> itNode = authorizedNodes.get(s1).iterator();
			while(itNode.hasNext()){
				Node n1 = itNode.next();
				Iterator<Scope> itScope2 = scopes.iterator();
				while(itScope2.hasNext()){
					Scope s2 = itScope2.next();
					if(s1!=s2){
						Iterator<Node> itNode2 = authorizedNodes.get(s2).iterator();
						while(itNode2.hasNext()){
							Node n2 = itNode2.next();
							if(distanceMatrix.get(n1,n2)==-1){
								if(costMatrix.get(s1,s2)>0){
									str.append("1 x"+scopeLiterals.get(n1,s1)+" x"+scopeLiterals.get(n2,s2)+" ");
								}
							}
						}
					}
				}
			}
		}
		return str.toString();
	}
	
	private void feedNodesData() {
		Iterator<Node> nodeit = nodes.iterator();
		while(nodeit.hasNext()){
			Node n1 = nodeit.next();
			capacities.put(n1, new Capacity(n1));
			distanceMatrix.put(n1,n1, 0);
			Iterator<Node> it2 = nodes.iterator();
			while(it2.hasNext()){
				Node n2 = it2.next();
				if(n1!=n2){
					Entry<Node,Node> route = n1.getRoute(n2.getIp());
					if(route == null){
						distanceMatrix.put(n1, n2, -1);
					}
					else{
						int dist = 1;
						while(route.getKey()!=route.getValue() && route!=null){
							dist++;
							route = route.getValue().getRoute(n2.getIp());
						}
						if(route==null){
							distanceMatrix.put(n1, n2, -1);
						}
						else{
							distanceMatrix.put(n1,n2, dist);
						}
					}
				}
			}
		}
	}

	private void feedScopesData() {
		Iterator<Scope> its = scopes.iterator();
		while(its.hasNext()){
			Scope s = its.next();
			Iterator<Scope> its2 = scopes.iterator();
			while(its2.hasNext()){
				Scope s2 = its2.next();
				costMatrix.put(s,s2,0);
			}
			List<Node> l = new ArrayList<>();
			Iterator<Node> nodeit = nodes.iterator();
			while(nodeit.hasNext()){
				Node n = nodeit.next();
				if(capacities.get(n).isAuthorized(s)){
					l.add(n);
					counter++;
					scopeLiterals.put(n,s, counter);
					Iterator<Place> itP = s.getPlaces().iterator();
					while(itP.hasNext()){
						Resource r = itP.next().getData();
						if(r.isDymamic()){
							counter++;
							resLiterals.put(n, r, counter);
							List<Resource> list;
							if(!nodeResToMapLists.containsKey(n,r.getClass().getSimpleName())){
								list = new ArrayList<>();
								nodeResToMapLists.put(n,r.getClass().getSimpleName(), list);
							}
							else{
								list = nodeResToMapLists.get(n, r.getClass().getSimpleName());
							}
							list.add(r);
						}
					}
				}
			}
			authorizedNodes.put(s,l);
			Iterator<Transition> itt = s.getTransitions().iterator();
			while(itt.hasNext()){
				Transition t = itt.next();
				Iterator<OutputArc> ita = t.getOutputArcs().iterator();
				while(ita.hasNext()){
					OutputArc a = ita.next();
					//TODO : Calculate real cost !!! (frequency * payload)
					int cost = 1;
					cost+=costMatrix.get(s,a.getPlace().getParent());
					costMatrix.put(s,a.getPlace().getParent(), cost);
				}
			}
		}
	}

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
						if(resLiterals.inverse().containsKey(literal)){
							m.add(resLiterals.inverse().get(literal));
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
	
	private class Capacity {
		private HashMap<String,List<String>> staticLists;
		private HashMap<String,Integer> dynamicCapa;
		
		public Capacity(Node n){
			this.staticLists = new HashMap<>();
			this.dynamicCapa = new HashMap<>();
			Iterator<String> itType = n.getResourceRoots().iterator();
			while(itType.hasNext()){
				String type = itType.next();
				Iterator<Resource> itRes = n.getResourceRoot(type).iterator();
				if(itRes.hasNext()){
					Resource r = itRes.next();
					if(r.isDymamic()){
						int capa = 0;
						if(r.getName()=="") capa++;
						while(itRes.hasNext()){
							if(itRes.next().getName()=="") capa++;
						}
						dynamicCapa.put(type, capa);
					}
					else{
						List<String> list = new ArrayList<>();
						list.add(r.getName());
						while(itRes.hasNext()){
							list.add(itRes.next().getName());
						}
						staticLists.put(type, list);
					}
				}
			}
		}
		
		public boolean isAuthorized(Scope s){
			HashMap<String,Integer> needs = new HashMap<>();
			Iterator<Place> it = s.getPlaces().iterator();
			while(it.hasNext()){
				Resource r = it.next().getData();
				String type = r.getClass().getSimpleName();
				String name = r.getName();
				if(!this.hasType(type)){
					return false;
				}
				if(!r.isDymamic()){
					if(!staticLists.get(type).contains(name)){
						return false;
					}
				}
				else{
					int qty = 1;
					if(needs.containsKey(type)){
						qty += needs.get(type);
					}
					needs.put(type, qty);
				}
			}
			Iterator<String> itType = needs.keySet().iterator();
			while(itType.hasNext()){
				String type = itType.next();
				if(needs.get(type)>dynamicCapa.get(type)){
					return false;
				}
			}
			return true;
		}
		
		public int getDisponibility(String type){
			if(dynamicCapa.containsKey(type)){
				return dynamicCapa.get(type);
			}
			else if(staticLists.containsKey(type)){
				return staticLists.get(type).size();
			}
			return 0;
		}
		
		public boolean hasType(String type){
			return (dynamicCapa.containsKey(type) || staticLists.containsKey(type));
		}
		
		public Set<String> getDynamicTypes(){
			return dynamicCapa.keySet();
		}
	}
}		