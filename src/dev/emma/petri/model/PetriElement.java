package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;
import emma.petri.control.listener.PetriEventListener;

public abstract class PetriElement {
	
	private static int q=1;
	private int id;
	private Set<PetriEventListener> listeners;
	
	public PetriElement(){
		this.id=q++;
		this.listeners=new HashSet<>();
	}
	
	public int getID(){
		return id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public void delete(){
		this.delete(this);
	}
	
	protected abstract void deleteLinks(PetriElement caller);
	
	protected final void delete(PetriElement caller){
		this.deleteLinks(caller);
		this.notifyDeletion();
	}
	
	protected void addPetriEventListener(PetriEventListener e){
		this.listeners.add(e);
	}
	
	public void activate(){
		ActivationEvent a = new ActivationEvent(this);
		Iterator<PetriEventListener> it = listeners.iterator();
		while(it.hasNext()){
			it.next().notify(a);
		}
	}
	
	public void desactivate(){
		DesactivationEvent d = new DesactivationEvent(this);
		Iterator<PetriEventListener> it = listeners.iterator();
		while(it.hasNext()){
			it.next().notify(d);
		}
	}
	
	protected void notifyDeletion(){
		DeletionEvent e = new DeletionEvent(this);
		Iterator<PetriEventListener> it = listeners.iterator();
		while(it.hasNext()){
			it.next().notify(e);
		}
	}
}
