package emma.mapper.mapobj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.mapper.MappingNotFoundException;
import emma.model.resources.tomap.ResourceToMap;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Transition;

public class MapperScope {
	private Scope s;
	private int multiplicity;
	private Set<MapperNode> authorizedNodes;
	private HashMap<Scope,Integer> cost;
	
	public static Set<MapperScope> getMapperScopes(Set<MapperNode> mNodes, Set<Scope> scopes) throws MappingNotFoundException{
		Set<MapperScope> mScopes = new HashSet<>();
		Iterator<Scope> itScope = scopes.iterator();
		while(itScope.hasNext()){
			MapperScope mScope = new MapperScope(mNodes, itScope.next());
			mScope.feedCostMap(scopes);
			mScopes.add(mScope);
		}
		return mScopes;
	}
	
	public MapperScope(Set<MapperNode> mNodes,Scope s) throws MappingNotFoundException{
		this.s=s;
		this.cost=new HashMap<>();
		this.evaluateMultiplicity(mNodes,s.getMultiplicity());
		authorizedNodes=new HashSet<>();
		Iterator<MapperNode> itNode = mNodes.iterator();
		while(itNode.hasNext()){
			MapperNode m = itNode.next();
			System.out.println("Check authorization :");
			if(m.isAuthorized(this)){
				System.out.println("\tScope "+s.getName()+" is authorized on "+m.getNode().getIp());
				authorizedNodes.add(m);
				Iterator<Place> itP = s.getPlaces().iterator();
				while(itP.hasNext()){
					ResourceToMap r= itP.next().getData();
					if(!r.isImported()){
						m.addResourceToMap(r);
					}
				}
			}
			else{
				System.out.println("\tScope "+s.getName()+" is not authorized on "+m.getNode().getIp());
			}
		}
	}
	
	private void feedCostMap(Set<Scope> scopes){
		Iterator<Scope> itScope = scopes.iterator();
		Iterator<Transition> itt = s.getTransitions().iterator();
		while(itScope.hasNext()){
			cost.put(itScope.next(), 0);
		}
		while(itt.hasNext()){
			Transition t = itt.next();
			Iterator<OutputArc> ita = t.getOutputArcs().iterator();
			while(ita.hasNext()){
				OutputArc a = ita.next();
				//TODO : Calculate real cost !!! (frequency * payload)
				int c = 1;
				c+=cost.get(a.getPlace().getParent());
				cost.put(a.getPlace().getParent(),c);
			}
		}
	}
	
	//TODO true evaluation
	private void evaluateMultiplicity(Set<MapperNode> mNodes, String m) throws MappingNotFoundException{
		try{
			multiplicity = Integer.parseInt(m);
		} catch(NumberFormatException e){
			throw new MappingNotFoundException("Multiplicity '"+m+"' cannot be parsed :"+e.getMessage());
		}
	}

	public Scope getScope(){
		return this.s;
	}
	
	public int getMultiplicity(){
		return this.multiplicity;
	}
	
	public Set<MapperNode> getAuthorizedNodes(){
		return this.authorizedNodes;
	}
	
	public int getCost(MapperScope s){
		return cost.get(s.getScope());
	}
}
