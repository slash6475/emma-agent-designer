package emma.petri.model.resources;

import emma.petri.model.Transition;

import java.awt.Color;

public class A extends emma.model.resources.A implements UnmappedResource {
	
	private boolean isImported;
	private Transition transition;
	private String test;
	
	public A(String name, Transition t){
		super(name);
		this.test="1";
		this.transition=t;
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
	
	public Transition getTransition(){
		return this.transition;
	}
}
