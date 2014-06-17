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
	
	@Override
	public void put(String s) {
		try{
			value=Integer.parseInt(s);
			notifier.fireListener(this);
		} catch(NumberFormatException e){
			
		}
	}

	@Override
	public void delete() {
		notifier.fireListener(this);
	}
	
	public int getSize(){
		return 4;
	}
}
