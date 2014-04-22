package emma.petri.control.listener;

import emma.petri.control.event.NameChangedEvent;

public interface ScopeListener extends PetriEventListener {
	void notify(NameChangedEvent e);
}
