package emma.tools;

import java.util.HashSet;

public class BoundedSet<E> extends HashSet<E> {
	private int maxSize;
	
	public BoundedSet (int size){
		super();
		this.maxSize = size;
	}
	
	@Override
	public boolean add(E obj){
		return (this.size() < maxSize)?super.add(obj):false;
	}
}