package emma.petri.model;


/**
 * Dérive de la classe abstraite Arc Modélise un arc place -> transition
 * @author  pierrotws
 */
public class InputArc extends Arc {
	
	/**
	 * Constructeur
	 * @param p : la place de l'arc. t : la transition de l'arc.
	 */
	public InputArc(Place p, Transition t){
		super(p,t);
		p.addInputArc(this);
		t.addInputArc(this);
	}
	
	@Override
	public int hashCode(){
		return (10000*getPlace().hashCode()) + getTransition().hashCode();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		if(!(caller instanceof Place)){
			getPlace().removeInputArc(this);
		}
		if(!(caller instanceof Transition)){
			getTransition().removeInputArc(this);
		}
	}
}
