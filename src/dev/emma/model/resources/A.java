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
		return "";
	}

	@Override
	public void put(String s) {
		notifier.fireListener(this);
	}

	@Override
	public void delete() {
		notifier.fireListener(this);
	}
	
	public int getSize(){
		return 20;
	}

	@Override
	public boolean hasInputRight() {
		return false;
	}
}
