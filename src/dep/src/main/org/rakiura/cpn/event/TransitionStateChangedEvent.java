// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import org.rakiura.cpn.Transition;


/**
 * Represents transition state change event. 
 * This event is being notify that the state
 * of the transition (from enabled to disabled
 * or vice versa) has occured.
 * 
 *<br><br>
 * TransitionStateChangedEvent.java<br>
 * Created: Sun Apr 21 10:07:06 2002<br>
 *
 * @author  <a href="mariusz@rakiura.org">Mariusz</a>
 * @version $Revision: 1.3 $ $Date: 2005/01/11 03:10:35 $
 */
public class TransitionStateChangedEvent extends TransitionEvent {
  
	private static final long serialVersionUID = 3258132444744922932L;

	public TransitionStateChangedEvent(final Transition aTransition) {
		super(aTransition);
	}
  
} // TransitionStateChangedEvent
//////////////////// end of file ////////////////////
