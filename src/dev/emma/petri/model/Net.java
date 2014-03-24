package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Net extends PetriElement {

	private Set<Subnet> subnets;
	
	public Net(){
		subnets=new HashSet<Subnet>();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller) {
		Iterator<Subnet> it = subnets.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}

	}
	
	public boolean add(Subnet s){
		return subnets.add(s);
	}
	
	public boolean remove(Subnet s){
		return subnets.remove(s);
	}
}
