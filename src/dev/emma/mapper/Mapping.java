package emma.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.tools.Pair;

public class Mapping extends HashMap<Node,List<Resource>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4614938823972938264L;

	public Mapping(){
		super();
	}
	
	public boolean contains(Node n, Resource r){
		return this.get(n).contains(r);
	}
	
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
	public boolean add(Pair<Node,Resource> p){
		return add(p.getFirst(),p.getSecond());
	}
}
