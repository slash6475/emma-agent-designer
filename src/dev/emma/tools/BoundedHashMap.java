package emma.tools;

import java.util.HashMap;

/**
 * HashMap with a max Size
 * @author pierrotws
 *
 * @param <K> Key class
 * @param <V> Value class
 */
public class BoundedHashMap<K, V> extends HashMap<K, V> implements BoundedCollection{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7621301656339486127L;
	private int maxSize;
	
	/**
	 * Unique constructor
	 * @param size max size of this HashMap
	 */
	public BoundedHashMap (int size){
		super();
		this.maxSize = size;
	}
	
	@Override
	public V put(K key, V value){
		if(this.size() < maxSize){
			return super.put(key, value);
		}
		return null;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

}
