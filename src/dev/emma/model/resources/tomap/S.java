package emma.model.resources.tomap;

import java.awt.Color;

public class S extends emma.model.resources.S implements ResourceToMap {
	public int index;
	public String address;
	
	public S(String name){
		super(name);
		this.index=0;
		this.address="";
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

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index=index;	
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		this.address=address;
	}

	@Override
	public String getPath() {
		return this.address+"/S/"+getName();
	}
}
