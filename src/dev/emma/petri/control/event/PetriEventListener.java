package emma.petri.control.event;

import java.util.EventListener;

public interface PetriEventListener extends EventListener {
	public void handle(PetriEvent e);
}
