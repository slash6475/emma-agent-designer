package emma.petri.control.event;

import java.util.EventObject;

public class StateChangedEvent extends EventObject{

	public StateChangedEvent(Object source) {
		super(source);
	}

}
