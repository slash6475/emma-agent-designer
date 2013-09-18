// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import org.rakiura.cpn.Transition;


/**
 * Represents an event fired just before the transition is fired.
 * 
 *<br><br>
 * TransitionStartedEvent.java<br>
 * Created: Thu Apr 11 17:37:22 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.4 $ $Date: 2005/01/11 03:10:36 $
 *@since 2.0
 */
public class TransitionStartedEvent extends TransitionEvent {
  

	private static final long serialVersionUID = 3832617370375435315L;

	public TransitionStartedEvent(final Transition aTransition) {
		super(aTransition);
	}
  
} // TransitionStartedEvent
//////////////////// end of file ////////////////////
