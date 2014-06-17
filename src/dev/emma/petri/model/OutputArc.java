package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.listener.OutputArcListener;

/**
 * Dérive de la classe abstraite Arc Modélise un arc transition -> place
 * @author  pierrotws
 */
public class OutputArc extends Arc {
	
	private Set<OutputArcListener> oals;
	private String expression;
	/**
	 * Constructeur
	 * @param p : la place de l'arc. t : la transition de l'arc.
	 */
	public OutputArc(Place p, Transition t) throws ArcException{
		super(p,t);
		if(!p.hasInputRight()){
			throw new ArcException("Place "+p.getName()+" has not input right");
		}
		expression="";
		oals = new HashSet<OutputArcListener>();
		p.addArc(this);
		t.addArc(this);
	}

	@Override
	public int hashCode(){
		return (10000*getTransition().hashCode()) + getPlace().hashCode();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		if(!(caller instanceof Place)){
			getPlace().removeArc(this);
		}
		if(!(caller instanceof Transition)){
			getTransition().removeArc(this);
		}
	}
	
	public void addListener(OutputArcListener l){
		oals.add(l);
	}
	
	public Set<OutputArcListener> getListeners(){
		return oals;
	}
	
	@Override
	protected void notifyDeletion() {
		DeletionEvent e = new DeletionEvent(this);
		Iterator<OutputArcListener> it = oals.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}

	@Override
	public boolean isInput() {
		return false;
	}
	
	public void setExpression(String expression){
		this.expression=expression;
	}
	
	public String getExpression(){
		return this.expression;
	}
}
