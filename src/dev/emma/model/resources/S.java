package emma.model.resources;

/**
 * Class representing System resources
 * The only "static" resource
 * (determined by hardware and not by software)
 * @author pierrotws
 *
 */
public class S extends AbstractResource {
	
	public S(String name){
		super(name);
	}
	
	@Override
	public String get() {
		return "";
	}

	@Override
	public void post(String s) {
		// TODO Auto-generated method stub
		notifier.fireListener(this);
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		notifier.fireListener(this);
	}
	
	public int getSize(){
		return 0;
	}
}
