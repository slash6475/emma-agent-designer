package emma.model.resources;

public class L implements Resource {
	private String name = "";
	
	public L(String name){
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
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
