package emma.petri.control.event;

import java.util.EventObject;

import emma.petri.model.PetriElement;

public abstract class PetriEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7326116917395418482L;

	public PetriEvent(PetriElement source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

}
