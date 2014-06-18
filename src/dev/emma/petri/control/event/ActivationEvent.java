package emma.petri.control.event;

import java.util.EventObject;

import emma.petri.model.PetriElement;

public class ActivationEvent extends EventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2818449367565014578L;

	public ActivationEvent(PetriElement source) {
		super(source);
	}

}
