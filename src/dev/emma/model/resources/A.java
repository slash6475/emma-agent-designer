package emma.model.resources;

/**
 * Class representing Agent resources 
 * @author pierrotws
 *
 */

public class A extends AbstractResource {
	
	public A(String name){
		super(name);
	}
	
	@Override
	public String get() {
		return "TODO -> Agent get function";
	}

	@Override
	public void post(String s) {
		// TODO Auto-generated method stub
		notifier.fireListener(this);
		
	}

	@Override
	public void delete() {
		notifier.fireListener(this);
	}
	
	public int getSize(){
		return 1;
	}
}
