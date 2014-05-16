package emma.tools;

import java.util.HashSet;
/**
 * HashSet with a max Size
 * @author pierrotws
 *
 * @param <V>
 */
public class BoundedSet<V> extends HashSet<V> implements BoundedCollection{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8020022887018743036L;
	private int maxSize;
	
	public BoundedSet (int size){
		super();
		this.maxSize = size;
	}
	
	@Override
	public boolean add(V obj){
		return (this.size() < maxSize)?super.add(obj):false;
	}
	
	@Override
	public int getMaxSize() {
		return maxSize;
	}
}
