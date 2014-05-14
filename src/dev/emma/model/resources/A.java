package emma.model.resources;


public class A extends Resource {
	
	public A(String name){
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
		return true;
	}
}
