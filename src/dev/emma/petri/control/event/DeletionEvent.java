package emma.petri.control.event;

import java.util.EventObject;

import emma.petri.model.PetriElement;

public class DeletionEvent extends EventObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3661202200093936092L;

	public DeletionEvent(PetriElement source) {
		super(source);
	}

}
