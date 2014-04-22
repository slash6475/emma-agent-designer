package emma.petri.control.listener;

import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;

public interface PlaceListener extends PetriEventListener {
	void notify(NameChangedEvent e);
	void notify(StateChangedEvent e);
}