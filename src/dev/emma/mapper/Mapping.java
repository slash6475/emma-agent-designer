package emma.mapper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import emma.mapper.mapobj.resources.MappedResource;
import emma.model.nodes.Node;
import emma.petri.model.Place;
import emma.petri.model.Scope;

/**
 * Class representing a Mapping
 * A mapping is, for each node, 
 * the list of resource to put on.
 * @author pierrotws
 *
 */
public class Mapping extends HashMap<Node,List<MappedResource>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4614938823972938264L;

	private HashMap<Scope,List<Node>> mappedScopes;
	
	private HashMap<Scope,String> uniqueAdresses;
	
	public Mapping(HashMap<Scope,String> addresses){
		super();
		this.mappedScopes=new HashMap<>();
		this.uniqueAdresses=addresses;
	}
	
	/**
	 * 
	 * @param n a node key
	 * @param r a resource
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean contains(Node n, MappedResource r){
		return this.get(n).contains(r);
	}
	
	public void finalize(){
		Iterator<Scope> itScope = mappedScopes.keySet().iterator();
		while(itScope.hasNext()){
			Scope scope = itScope.next();
			Iterator<Node> itNode = mappedScopes.get(scope).iterator();
			while(itNode.hasNext()){
				Node n = itNode.next();
				if(!this.containsKey(n)){
					this.put(n, new LinkedList<MappedResource>());
				}
				List<MappedResource> list = this.get(n);
				Iterator<Place> itPlace = scope.getPlaces().iterator();
				while(itPlace.hasNext()){
					Place p = itPlace.next();
					try {
						list.add((MappedResource)Class.forName("emma.mapper.mapobj.resources."+p.getType()).getConstructor(Class.forName("emma.petri.model.resources."+p.getType()),Mapping.class).newInstance(p.getData(),this));
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException
							| ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 
	 * @param s a scope key
	 * @param n a node
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean add(Scope s, Node n){
		if(this.containsKey(n)){
			this.put(n, new LinkedList<MappedResource>());
		}
		if(mappedScopes.containsKey(s)){
			return mappedScopes.get(s).add(n);
		}
		else{
			List<Node> nl = new LinkedList<Node>();
			nl.add(n);
			return (mappedScopes.put(s, nl)==n);
		}
	}
	
	public HashMap<Scope,List<Node>> getScopeMapping(){
		return mappedScopes;
	}
	
	public HashMap<Scope, String> getScopeAddresses(){
		return this.uniqueAdresses;
	}
}
