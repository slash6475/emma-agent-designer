package emma.petri.control.event;

import emma.petri.model.Transition;

public class TransitionStoppedEvent extends TransitionEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5712208243337305229L;

	public TransitionStoppedEvent(final Transition transition) {
		super(transition);
	}
}