package emma.model.resources;

/**
 * Class representing Local resources 
 * @author pierrotws
 *
 */
public class L extends AbstractResource {
	
	public int value;
	
	public L(String name){
		super(name);
		this.value=0;
	}
	
	@Override
	public String get() {
		return String.valueOf(value);
	}

	@Override
	public void post(String s) {
		try{
			value=Integer.parseInt(s);
			notifier.fireListener(this);
		} catch(NumberFormatException e){
			
		}
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		notifier.fireListener(this);
		
	}
}
