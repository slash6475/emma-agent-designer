package emma.petri.model;

import java.util.HashSet;
import java.util.Set;

public abstract class PT extends PetriElement {

	private Set<InputArc> inArcs;
	private Set<OutputArc> outArcs;
	private Scope parent;
	
	public PT(Scope s){
		super();
		this.parent = s;
		this.inArcs = new HashSet<InputArc>();
		this.outArcs = new HashSet<OutputArc>();
	}
	
	public boolean addArc(OutputArc arc){
		return outArcs.add(arc);
	}
	
	public boolean removeArc(OutputArc arc){
		return outArcs.remove(arc);
	}
	
	public Set<OutputArc> getOutputArcs(){
		return outArcs;
	}
	
	public boolean addArc(InputArc arc){
		return inArcs.add(arc);
	}
	
	public boolean removeArc(InputArc arc){
		return inArcs.remove(arc);
	}
	
	public Set<InputArc> getInputArcs(){
		return inArcs;
	}
	
	public Scope getParent(){
		return parent;
	}
}
