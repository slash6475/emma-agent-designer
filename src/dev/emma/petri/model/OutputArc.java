package emma.petri.model;

import java.util.HashSet;
import java.util.Set;

import emma.petri.control.listener.OutputArcListener;

/**
 * Dérive de la classe abstraite Arc Modélise un arc transition -> place
 * @author  pierrotws
 */
public class OutputArc extends Arc {
	
	private Set<OutputArcListener> oals;

	/**
	 * Constructeur
	 * @param p : la place de l'arc. t : la transition de l'arc.
	 */
	public OutputArc(Place p, Transition t){
		super(p,t);
		oals = new HashSet<OutputArcListener>();
		p.addOutputArc(this);
		t.addOutputArc(this);
	}

	@Override
	public int hashCode(){
		return (10000*getTransition().hashCode()) + getPlace().hashCode();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		if(!(caller instanceof Place)){
			getPlace().removeOutputArc(this);
		}
		if(!(caller instanceof Transition)){
			getTransition().removeOutputArc(this);
		}
	}
	
	public void addListener(OutputArcListener l){
		oals.add(l);
	}
	
	public Set<OutputArcListener> getListeners(){
		return oals;
	}
}
