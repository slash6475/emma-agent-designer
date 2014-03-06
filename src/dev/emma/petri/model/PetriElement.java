package emma.petri.model;

import java.util.HashSet;
import java.util.Set;

import emma.petri.control.event.PetriEventListener;

public abstract class PetriElement {
	
	private static int q=1;
	private int id;
	private String name;
	private Set<PetriEventListener> listeners;
	private Subnet parent;
	private boolean isVirtualized;

	public PetriElement(Subnet parent){
		this("elmt"+q,parent);
	}
	
	public PetriElement(String name, Subnet parent){
		id=q++;
		this.name=name;
		this.parent=parent;
		this.listeners = new HashSet<PetriEventListener>();
		isVirtualized = false;
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public Subnet getParent(){
		return parent;
	}
	public void setName(String name){
		this.name=name;
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
	
	public final void delete(PetriElement caller){
		listeners.clear();
		listeners=null;
		this.deleteLinks(caller);
	}
	
	public boolean isVirtualized(){
		return isVirtualized;
	}
	
	public final boolean isVirtualizeable(){
		return (this instanceof Virtualizeable);
	}
	
	public boolean isVirtual(){
		return false;
	}
	
	public final boolean virtualize(){
		if(this.isVirtualizeable()){
			if(!isVirtualized){
				isVirtualized=true;
				return true;
			}
		}
		return false;
	}
	
	public final boolean unvirtualize(){
		if(isVirtualized){
			isVirtualized=false;
			((Virtualizeable) this).removeVirtualisation();
		}
		return false;
	}
}
