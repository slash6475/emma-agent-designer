package emma.petri.control.event;

import emma.petri.model.Place;

public abstract class PlaceEvent extends PetriEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5054264542758013756L;


	public PlaceEvent(Place source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	public Place getPlace(){
		return (Place) getSource();
	}
}
