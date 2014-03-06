package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Sous réseau de Petri
 * 
 * @author pierrotws
 *
 */
public class Subnet extends PetriElement{
	
	private Set<Place> places;
	private Set<Transition> transitions;
	private Set<Subnet> subnets;
	
	public Subnet(){
		this(null);
	}
	
	public Subnet(Subnet s){
		super(s);
		places=new HashSet<Place>();
		transitions= new HashSet<Transition>();
		subnets = new HashSet<Subnet>();
	}
	
	public Set<Subnet> getSubnets(){
		return subnets;
	}
	
	public Set<Place> getPlaces(){
		return places;
	}
	
	public Set<Transition> getTransitions(){
		return transitions;
	}
	
	public boolean add(Place p){
		return this.places.add(p);
	}
	
	public boolean add(Transition t){
		return this.transitions.add(t);
	}
	
	public boolean add(Subnet s){
		return this.subnets.add(s);
	}
	
	public boolean remove(Place p){
		return this.places.remove(p);
	}
	
	public boolean remove(Transition t){
		return this.transitions.remove(t);
	}

	public boolean remove(Subnet s){
		return this.subnets.remove(s);
	}
	
	@Override
	protected void deleteLinks(PetriElement caller) {
		Iterator<Place> it = places.iterator();
		Iterator<Transition> it2 = transitions.iterator();
		Iterator<Subnet> it3 = subnets.iterator();
		if(caller==this){
			if(getParent()!=null)
				getParent().remove(this);
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		places.clear();
		while(it2.hasNext()){
			it2.next().delete(this);
		}
		transitions.clear();
		while(it.hasNext()){
			it3.next().delete(this);
		}
		subnets.clear();
	}
}
