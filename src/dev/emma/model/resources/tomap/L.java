package emma.model.resources.tomap;

import java.awt.Color;

public class L extends emma.model.resources.L implements ResourceToMap {

	public boolean isImported;
	
	public L(String name){
		super(name);
		this.isImported=false;
	}
	
	@Override
	public boolean setImport(boolean i) {
		this.isImported=i;
		return true;
	}

	@Override
	public boolean isImported() {
		return this.isImported;
	}

	@Override
	public Color getColor() {
		return Color.yellow;
	}

	
}
