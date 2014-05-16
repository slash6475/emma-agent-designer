package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.listener.SubnetListener;

/**
 * Sous réseau de Petri
 * 
 * @author pierrotws
 *
 */
public class Subnet extends PetriElement{
	
	private static int q=0;
	private Set<Scope> scopes;
	private Set<Arc> arcs;
	private Set<SubnetListener> subls;
	private String name;
	private Net parent;
	
	public Subnet(Net parent){
		this(parent,"sub"+q);
	}
	
	public Subnet(Net parent, String name){
		super();
		q++;
		this.parent=parent;
		scopes=new HashSet<Scope>();
		arcs=new HashSet<Arc>();
		this.name=name;
		parent.add(this);
		this.subls = new HashSet<SubnetListener>();
	}
	
	public Set<Scope> getScopes(){
		return scopes;
	}
	
	@Override
	protected void deleteLinks(PetriElement caller) {
		Iterator<Scope> it = scopes.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
		arcs.clear();
		scopes.clear();
		subls.clear();
	}
	

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
		NameChangedEvent e = new NameChangedEvent(this);
		Iterator<SubnetListener> it = subls.iterator();
		while(it.hasNext()){
			it.next().notify(e);
		}
	}
	

	public boolean add(Scope scope) {
		return scopes.add(scope);
	}
	
	public boolean add(Arc a){
		return arcs.add(a);
	}
	
	public boolean remove(Scope scope) {
		return scopes.remove(scope);
	}

	public boolean remove(Arc a){
		return arcs.remove(a);
	}
	
	public Net getParent(){
		return parent;
	}
	
	public void addListener(SubnetListener l){
		subls.add(l);
	}

	public Set<SubnetListener> getListeners(){
		return subls;
	}
	
	public Set<Arc> getArcs() {
		return arcs;
	}
}
