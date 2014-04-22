package emma.petri.control.event;

import java.util.EventObject;

public class NameChangedEvent extends EventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3605902696799673577L;

	public NameChangedEvent(Object source) {
		super(source);
	}

}
