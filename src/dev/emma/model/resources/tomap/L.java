package emma.model.resources.tomap;

import java.awt.Color;

public class L extends emma.model.resources.L implements ResourceToMap {

	public boolean isImported;
	public int index;
	public String address;
	
	public L(String name){
		super(name);
		this.isImported=false;
		this.index=0;
		this.address="";
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
		return this.address+"/L/"+getName();
	}
	
}
