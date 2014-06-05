package emma.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import emma.model.nodes.Node;
import emma.model.resources.tomap.ResourceToMap;
import emma.petri.model.Scope;

/**
 * Class representing a Mapping
 * A mapping is, for each node, 
 * the list of resource to put on.
 * @author pierrotws
 *
 */
public class Mapping extends HashMap<Node,List<Entry<ResourceToMap,Integer>>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4614938823972938264L;

	private HashMap<Entry<Scope,Integer>,Node> scope;
	private HashMap<Entry<ResourceToMap,Integer>,Node> res;
	
	public Mapping(){
		super();
		this.scope=new HashMap<>();
		this.res=new HashMap<>();
	}
	
	/**
	 * 
	 * @param n a node key
	 * @param r a resource
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean contains(Node n, ResourceToMap r){
		return this.get(n).contains(r);
	}
	
	/**
	 * 
	 * @param n the node 
	 * @param r a resource to add to n
	 * @return true if adding r to n succeed, false otherwise
	 */
	public boolean add(ResourceToMap r, int multiplicity, Node n){
		Entry<ResourceToMap,Integer> e = new SimpleEntry<>(r,multiplicity);
		this.res.put(e, n);
		if(this.containsKey(n)){
			return this.get(n).add(e);
		}
		else{
			List<Entry<ResourceToMap,Integer>> list = new ArrayList<>();
			list.add(e);
			return (this.put(n, list)==list);
		}
	}
	
	/**
	 * 
	 * @param n a scope key
	 * @param multiplicity an integer key
	 * @param r a node
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean add(Scope s, int multiplicity, Node n){
		return (scope.put(new SimpleEntry<>(s,multiplicity), n)==n);
	}
	
	public HashMap<Entry<Scope,Integer>,Node> getScopeMapping(){
		return scope;
	}
}
