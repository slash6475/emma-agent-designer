package emma.model.resources;

/**
 * Class representing Local resources 
 * @author pierrotws
 *
 */
public class L extends AbstractResource {
	
	private int value;
	
	public L(String name){
		super(name);
		this.value=0;
	}
	
	@Override
	public String get() {
		return String.valueOf(value);
	}

	@Override
	public String toString(){
		return "\""+this.get()+"\"";
	}
	
	
	public int getSize(){
		return 4;
	}
	
	protected void setValue(int value){
		notifier.fireListener(this);
		this.value=value;
	}
}
