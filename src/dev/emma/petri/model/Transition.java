package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.listener.TransitionListener;

public class Transition extends PT{
	
	private String test;
	private Place place;
	private Set<TransitionListener> tls;
	
	public Transition(Scope s, Place p){
		super(s);
		s.add(this);
		this.place=p;
		place.setType(emma.model.resources.tomap.A.class);
		test="true";
		tls = new HashSet<TransitionListener>();
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
	
	public void addListener(TransitionListener l){
		tls.add(l);
	}
	
	public Set<TransitionListener> getListeners(){
		return tls;
	}
	
	@Override
	protected void notifyDeletion() {
		// TODO Auto-generated method stub
		DeletionEvent e = new DeletionEvent(this);
		Iterator<TransitionListener> it = tls.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}
}
