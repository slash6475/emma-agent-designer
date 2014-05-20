package emma.model.resources.tomap;

import java.awt.Color;

public class A extends emma.model.resources.A implements ResourceToMap {
	public boolean isImported;
	public A(String name){
		super(name);
		this.isImported=false;
	}

	@Override
	public boolean setImport(boolean i) {
		this.isImported=i;
		return false;
	}

	@Override
	public boolean isImported() {
		return this.isImported;
	}

	@Override
	public Color getColor() {
		return Color.blue;
	}
	
}
