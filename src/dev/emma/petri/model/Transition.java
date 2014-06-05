package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.model.resources.tomap.A;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.listener.TransitionListener;

public class Transition extends PT{
	
	private Place place;
	private A res;
	private Set<TransitionListener> tls;
	
	public Transition(Scope s, Place p){
		super(s);
		s.add(this);
		this.place=p;
		this.place.setType(A.class);
		this.res=(A)this.place.getData();
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
		this.res.setCondition(cond);
	}
	public String getCondition(){
		return this.res.getCondition();
	}
	
	public String getFullCondition(){
		return this.res.getFullCondition();
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
		DeletionEvent e = new DeletionEvent(this);
		Iterator<TransitionListener> it = tls.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}
	
	@Override
	public boolean addArc(OutputArc arc){
		boolean ret = super.addArc(arc);
		if(ret){
			this.notifyOutputArcExpression();
		}
		return ret;
	}
	
	@Override
	public boolean removeArc(OutputArc arc){
		boolean ret = super.removeArc(arc);
		if(ret){
			this.notifyOutputArcExpression();
		}
		return ret;
	}
	
	@Override
	public boolean removeArc(InputArc arc){
		boolean ret = super.removeArc(arc);
		if(ret){
			this.notifyInputArcExpression();
		}
		return ret;
	}
	
	public void notifyInputArcExpression(){
		this.res.computePRE(getInputArcs());
	}
	
	public void notifyOutputArcExpression(){
		this.res.computeWITH_DO(getOutputArcs());
	}
}
