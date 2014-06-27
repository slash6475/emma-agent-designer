package emma.petri.model.resources;

import emma.petri.model.Transition;

import java.awt.Color;

public class A extends emma.model.resources.A implements UnmappedResource {
	
	private boolean isImported;
	private Transition transition;
	private String test;
	private boolean outputRight;
	
	public A(String name){
		super(name);
		this.test="true";
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
	
	public String getCondition(){
		return this.test;
	}
	
	public void setCondition(String str){
		this.test=str;
	}
	
	public void setTransition(Transition t){
		this.transition=t;
	}
	
	public Transition getTransition(){
		return this.transition;
	}

	@Override
	public boolean hasInputRight() {
		return false;
	}
	
	@Override
	public boolean hasOutputRight() {
		return outputRight;
	}

	@Override
	public boolean setInputRight(boolean aValue) {
		return false;
	}

	@Override
	public boolean setOutputRight(boolean aValue) {
		this.outputRight=aValue;
		return true;
	}
}
