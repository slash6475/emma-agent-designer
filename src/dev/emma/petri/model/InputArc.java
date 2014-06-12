package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.listener.InputArcListener;


/**
 * Dérive de la classe abstraite Arc Modélise un arc place -> transition
 * @author  pierrotws
 */
public class InputArc extends Arc {
	
	/**
	 * Constructeur
	 * @param p : la place de l'arc. t : la transition de l'arc.
	 */
	private Set<InputArcListener> ials;
	public InputArc(Place p, Transition t){
		super(p,t);
		p.addArc(this);
		t.addArc(this);
		ials = new HashSet<InputArcListener>();
	}
	
	@Override
	public int hashCode(){
		return (10000*getPlace().hashCode()) + getTransition().hashCode();
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
	
	public void addListener(InputArcListener l){
		ials.add(l);
	}
	
	public Set<InputArcListener> getListeners(){
		return ials;
	}

	@Override
	protected void notifyDeletion() {
		DeletionEvent e = new DeletionEvent(this);
		Iterator<InputArcListener> it = ials.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}

	@Override
	public boolean isInput() {
		return true;
	}
}
