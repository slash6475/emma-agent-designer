package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.listener.ScopeListener;

public class Scope extends PetriElement{

	private static int q=1;
	private Subnet parent;
	private Set<Place> places;
	private Set<Transition> transitions;
	private String name;
	private Set<ScopeListener> scls;
	private String target;
	
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
		this.target="1";
		parent.add(this);
		scls = new HashSet<ScopeListener>();
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
		this.name = name;
		NameChangedEvent e = new NameChangedEvent(this);
		Iterator<ScopeListener> it = scls.iterator();
		while(it.hasNext()){
			it.next().notify(e);
		}
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

	public void addListener(ScopeListener l){
		scls.add(l);
	}
	
	public Set<ScopeListener> getListeners(){
		return scls;
	}
	
	public boolean setTarget(String target){
		if(!target.equals("*") && !target.startsWith("G:")){
			try{
				if(Integer.parseInt(target)<1){
					return false;
				}
			} catch(NumberFormatException e){
				return false;
			}
		}
		this.target=target;
		return true;
	}
	
	public String getTarget(){
		return target;
	}
	
	@Override
	protected void notifyDeletion() {
		// TODO Auto-generated method stub
		DeletionEvent e = new DeletionEvent(this);
		Iterator<ScopeListener> it = scls.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}
}
