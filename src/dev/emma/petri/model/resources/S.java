package emma.petri.model.resources;

import java.awt.Color;

public class S extends emma.model.resources.S implements UnmappedResource {
	
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

	@Override
	public Color getColor() {
		return Color.red;
	}
}
