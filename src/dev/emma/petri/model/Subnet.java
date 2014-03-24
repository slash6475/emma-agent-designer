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
	
	private static int q=1;
	private Set<Scope> scopes;
	private String name;
	private Net parent;
	
	public Subnet(Net parent, String name){
		super();
		q++;
		this.parent=parent;
		scopes=new HashSet<Scope>();
		this.name=name;
		parent.add(this);
	}
	public Subnet(Net parent){
		this(parent,"sub"+q);
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
		scopes.clear();
	}
	

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean add(Scope scope) {
		return scopes.add(scope);
	}
	
	public boolean remove(Scope scope) {
		return scopes.remove(scope);
	}
	
	public Net getParent(){
		return parent;
	}
}
