package emma.mapper;

import java.util.HashMap;
import java.util.Map.Entry;
public class PBMap<K1, K2, V> extends HashMap<Entry<K1,K2>, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8068828312572830683L;

	private HashMap<V,K1> inv1;
	private HashMap<V,K2> inv2;
	public PBMap(){
		super();
		this.inv1=new HashMap<>();
		this.inv2=new HashMap<>();
	}

	public V put(K1 key1, K2 key2, V value){
		inv1.put(value, key1);
		inv2.put(value, key2);
		return this.put(new SimpleEntry<K1,K2>(key1,key2), value);
	}
	
	public V get(K1 key1, K2 key2){
		return this.get(new SimpleEntry<K1,K2>(key1,key2));
	}
	
	public boolean containsKey(K1 key1, K2 key2){
		return this.containsKey(new SimpleEntry<K1,K2>(key1,key2));
	}
	
	public K1 getFirstKeyByValue(V v){
		return inv1.get(v);
	}
	
	public K2 getSecondKeyByValue(V v){
		return inv2.get(v);
	}
}
