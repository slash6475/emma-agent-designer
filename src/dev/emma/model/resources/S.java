package emma.model.resources;

public class S extends Resource {
	private String name = "";
	
	public S(String name){
		this.name = name;
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
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
