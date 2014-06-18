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
	
	public int getSize(){
		return 20;
	}

	@Override
	public boolean hasInputRight() {
		return false;
	}
}
