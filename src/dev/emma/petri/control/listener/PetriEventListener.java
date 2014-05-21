package emma.petri.control.listener;

import java.util.EventListener;

import emma.petri.control.event.DeletionEvent;

public interface PetriEventListener extends EventListener {
	void notity(DeletionEvent e);
}
