package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Scope extends PetriElement{

	private static int q=1;
	private Subnet parent;
	private Set<Place> places;
	private Set<Transition> transitions;
	private String name;
	
	public Scope(Subnet parent){
		this(parent,"scp"+q);
	}
	
	public Scope(Subnet parent, String name) {
		super();
		q++;
		this.name=name;
		this.places=new HashSet<Place>();
		this.transitions=new HashSet<Transition>();
		this.parent=parent;
		parent.add(this);
	}

	public Subnet getParent(){
		return parent;
	}
	public boolean add(Transition transition) {
		// TODO Auto-generated method stub
		return transitions.add(transition);
	}
	
	public boolean add(Place place){
		return places.add(place);
	}

	public boolean remove(Place place) {
		return places.remove(place);
	}
	
	public boolean remove(Transition transition){
		return transitions.remove(transition);
	}

	@Override
	protected void deleteLinks(PetriElement caller) {
		if(caller!=parent){
			parent.remove(this);
		}
		Iterator<Place> ip = places.iterator();
		while(ip.hasNext()){
			ip.next().delete(this);
		}
		Iterator<Transition> it = transitions.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}

	public Set<Place> getPlaces() {
		// TODO Auto-generated method stub
		return places;
	}
	
	public Set<Transition> getTransitions() {
		// TODO Auto-generated method stub
		return transitions;
	}
}
