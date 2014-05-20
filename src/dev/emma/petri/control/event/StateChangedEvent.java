package emma.petri.control.event;

import java.util.EventObject;

public class StateChangedEvent extends EventObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7249410396836839763L;

	public StateChangedEvent(Object source) {
		super(source);
	}
}
