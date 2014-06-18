package emma.petri.model.resources;

import java.awt.Color;

public class S extends emma.model.resources.S implements UnmappedResource {
	
	private String expr;
	
	public S(String name){
		super(name);
		this.expr="";
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
	public String get(){
		return expr;
	}
	
	@Override
	public void put(String str){
		this.expr=str;
	}
	
	@Override
	public Color getColor() {
		return Color.red;
	}
}
