package emma.petri.model.resources;

import java.awt.Color;

public class S extends emma.model.resources.S implements UnmappedResource {
	
	private String expr;
	private boolean inputRight;
	private boolean outputRight;
	
	public S(String name){
		super(name);
		this.expr="";
		this.inputRight=false;
		this.outputRight=true;
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

	
	@Override
	public boolean hasInputRight() {
		return inputRight;
	}
	
	@Override
	public boolean hasOutputRight() {
		return outputRight;
	}

	@Override
	public boolean setInputRight(boolean aValue) {
		this.inputRight=aValue;
		return true;
	}

	@Override
	public boolean setOutputRight(boolean aValue) {
		this.outputRight=aValue;
		return true;
	}
}
