package emma.petri.control.event;

import emma.petri.model.Transition;

public class TransitionStartedEvent extends TransitionEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3267854755944899319L;

	public TransitionStartedEvent(final Transition transition) {
		super(transition);
	}
}