package emma.petri.model;

public class Token<T> {
	private static int q = 0;

	private int id;
	private T value;
	
	public Token(T value){
		id=q++;
		this.value=value;
	}
	
	public void set(T value){
		this.value=value;
	}
	
	public T get(){
		return value;
	}
	
	public int getID(){
		return id;
	}
}
