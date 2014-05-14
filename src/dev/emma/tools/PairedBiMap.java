package emma.tools;

public class PairedBiMap<A,B,C> extends BiMap<Pair<A,B>,C>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4400334208759619213L;
	
	public PairedBiMap(){
		super();
	}
	
	public C get(A a, B b){
		return super.get(new Pair<A, B>(a,b));
	}
	
	public C put(A a, B b, C c){
		return super.put(new Pair<A,B>(a,b), c);
	}
	
	public boolean containsKey(A a, B b){
		return super.containsKey(new Pair<A,B>(a,b));
	}
	
}
