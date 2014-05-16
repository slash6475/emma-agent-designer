package emma.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.tools.Pair;

/**
 * Class representing a Mapping
 * A mapping is, for each node, 
 * the list of resource to put on.
 * @author pierrotws
 *
 */
public class Mapping extends HashMap<Node,List<Resource>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4614938823972938264L;

	public Mapping(){
		super();
	}
	/**
	 * 
	 * @param n a node key
	 * @param r a resource
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean contains(Node n, Resource r){
		return this.get(n).contains(r);
	}
	
	/**
	 * 
	 * @param n the node 
	 * @param r a resource to add to n
	 * @return true if adding r to n succeed, false otherwise
	 */
	public boolean add(Node n, Resource r){
		if(this.containsKey(n)){
			return this.get(n).add(r);
		}
		else{
			List<Resource> list = new ArrayList<>();
			list.add(r);
			return (this.put(n, list)==list);
		}
	}
	/**
	 * 
	 * @param the pair (n,r) n the node, r a resource to add to n.
	 * @return true if adding r to n succeed, false otherwise
	 */
	public boolean add(Pair<Node,Resource> p){
		return add(p.getFirst(),p.getSecond());
	}
}
