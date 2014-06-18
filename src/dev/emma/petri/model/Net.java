package emma.petri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.listener.NetListener;

/**
 * Modélise un Petri réseau de Petri
 * C'est un ensemble de sous-réseau
 * @author pierrotws
 *
 */
public class Net extends PetriElement {
	
	private Set<Subnet> subs;
	private Set<NetListener> nls;
	
	public Net() {
		super();
		subs = new HashSet<Subnet>();
		nls = new HashSet<NetListener>();
	}

	@Override
	protected void deleteLinks(PetriElement caller) {
		Iterator<Subnet> it = subs.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
	}

	public boolean add(Subnet subnet) {
		return subs.add(subnet);
	}

	public void addListener(NetListener l) {
		nls.add(l);
	}

	public Set<Subnet> getSubnets() {
		return subs;
	}
	
	public Set<NetListener> getListeners(){
		return nls;
	}
}
