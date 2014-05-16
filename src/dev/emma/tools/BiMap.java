package emma.tools;

import java.util.HashMap;

/**
 * Bidirectional HashMap<K,V> and <V,K>
 * @author pierrotws
 *
 * @param <K> Key Class
 * @param <V> Value Class
 */
public class BiMap<K, V> extends HashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2953967339304610715L;
	private HashMap<V, K> inverse;
	
	public BiMap(){
		super();
		this.inverse=new HashMap<>();
	}
	
	@Override
	public void clear(){
		super.clear();
		inverse.clear();
	}
	
	@Override
	public V put(K key, V value){
		this.inverse().put(value, key);
		return super.put(key, value);
	}
	/**
	 * 
	 * @return the inverse HashMap<V,K>
	 */
	public HashMap<V, K> inverse(){
		return inverse;
	}
}
