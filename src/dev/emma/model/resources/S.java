package emma.model.resources;

public class S extends Resource {
	
	public S(String name){
		super(name);
	}
	
	@Override
	public String get() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public boolean isDymamic() {
		return false;
	}

}
