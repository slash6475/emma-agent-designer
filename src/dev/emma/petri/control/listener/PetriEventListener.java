package emma.petri.control.listener;

import java.util.EventListener;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;

public interface PetriEventListener extends EventListener {
	
	void notify(DeletionEvent e);
	void notify(ActivationEvent e);	
	void notify(DesactivationEvent e);
}
