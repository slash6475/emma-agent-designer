// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package emma.petri.control.event;

import emma.petri.model.Transition;

public abstract class TransitionEvent extends PetriEvent {
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 861887516695795823L;

	/**
	 * Creates a new <code>TransitionEvent</code> instance.
	 * @param aTransition a <code>Transition</code> value
	 */
	public TransitionEvent(final Transition transition) {
		super(transition);
	}

	/**
	* Returns the transition, which is the source of this event.
	*@return the transition, source of the event.
	*/
	public Transition getTransition() {
		return (Transition) this.getSource();
	}
}