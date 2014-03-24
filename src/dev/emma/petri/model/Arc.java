package emma.petri.model;

/**
 * Classe décrivant un Arc de Petri d'un point de vue "modèle". Il existe deux classes dérivées : InputArc et OutputArc 
 * @author  pierrotws
 */
public abstract class Arc extends PetriElement{
	
	private Transition transition;
	private Place place;
	private String expr;
	/**
	 * Constructeur 
	 * @param p : la place de l'arc. t : la transition de l'arc.
	 */
	public Arc(Place p,Transition t){
		super();
		place=p;
		transition=t;
		expr="";
	}
	
	/** 
	* Retourne la transition de l'arc.
	* @return transition de l'arc.
	*/
	public Transition getTransition(){
		return transition;
	}

	/** 
	* Retourne la place de l'arc.
	* @return place de l'arc.
	*/
	public Place getPlace(){
		return place;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Arc){
			Arc f = (Arc) o;
			if(f.getClass() == this.getClass()){
				if(f.getPlace() == this.getPlace()){
					if(f.getTransition() == this.getTransition()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void setExpression(String expression){
		this.expr=expression;
	}
	
	public String getExpression(){
		return expr;
	}
}
