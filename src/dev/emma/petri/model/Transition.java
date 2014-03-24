package emma.petri.model;

import java.util.Iterator;

public class Transition extends PT{
	
	private String test;
	private Place place;
	
	public Transition(Scope s, Place p){
		super(s);
		s.add(this);
		this.place=p;
		test="true";
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		Iterator<InputArc> it = getInputArcs().iterator();
		Iterator<OutputArc> it2 = getOutputArcs().iterator();
		if(!(caller instanceof Subnet)){
			getParent().remove(this);
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		getInputArcs().clear();
		while(it2.hasNext()){
			it2.next().delete(this);
		}
		getOutputArcs().clear();
		place.delete();
	}
	
	public void setCondition(String cond){
		this.test=cond;
	}
	public String getCondition(){
		return test;
	}
	
	public Place getPlace(){
		return place;
	}
}
