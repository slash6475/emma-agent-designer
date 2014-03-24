package emma.tools;

import java.util.HashMap;

public class BoundedHashMap<E, E2> extends HashMap<E, E2>{
	private int maxSize;
	
	public BoundedHashMap (int size){
		super();
		this.maxSize = size;
	}
	
	@Override
	
	public E2 put(E key, E2 value){
		if(this.size() < maxSize){
			E2 e = super.put(key, value);
			return value;
		}
		return null;
		//return (this.size() < maxSize)?super.put(key, value):null;
	}

}
