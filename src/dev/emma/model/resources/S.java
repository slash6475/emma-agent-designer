package emma.model.resources;

/**
 * Class representing System resources
 * The only "static" resource
 * (determined by hardware and not by software)
 * @author pierrotws
 */
public class S extends AbstractResource {
	
	private int value;
	
	public S(String name){
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
		return 0;
	}

	@Override
	public boolean hasInputRight(){
		return false;
	}
}
