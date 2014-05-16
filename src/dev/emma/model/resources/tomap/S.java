package emma.model.resources.tomap;

public class S extends emma.model.resources.S implements ResourceToMap {
	
	public S(String name){
		super(name);
	}

	@Override
	public boolean setImport(boolean i) {
		return false;
	}

	@Override
	public boolean isImported() {
		return true;
	}
}
