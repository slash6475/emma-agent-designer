package emma.petri.model.resources;

import java.awt.Color;

public class L extends emma.model.resources.L implements UnmappedResource {

	private boolean isImported;
	private boolean inputRight;
	private boolean outputRight;

	public L(String name){
		super(name);
		this.isImported=false;
		this.inputRight=true;
		this.outputRight=true;
	}
	
	@Override
	public void put(String str){
		try{
			this.setValue((int)Double.parseDouble(str));
		} catch(NumberFormatException e){
			
		}
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
