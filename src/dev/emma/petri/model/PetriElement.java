package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.PetriEvent;
import emma.petri.control.event.PetriEventListener;

public abstract class PetriElement {
	
	private static int q=1;
	private int id;
	private Set<PetriEventListener> listeners;
	
	public PetriElement(){
		this.id=q++;
		this.listeners = new HashSet<PetriEventListener>();
	}
	
	public int getID(){
		return id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public boolean addListener(PetriEventListener l){
		return this.listeners.add(l);
	}
	
	public boolean removeListener(PetriEventListener l) {
		return listeners.remove(l);
	}
	
	public void delete(){
		this.delete(this);
	}
	
	protected abstract void deleteLinks(PetriElement caller);
	
	protected final void delete(PetriElement caller){
		listeners.clear();
		this.deleteLinks(caller);
	}
	
	protected void notify(PetriEvent e){
		Iterator<PetriEventListener> it = this.listeners.iterator();
		while(it.hasNext()){
			it.next().handle(e);
		}
	}
}
