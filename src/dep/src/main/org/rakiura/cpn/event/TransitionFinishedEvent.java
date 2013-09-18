// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import org.rakiura.cpn.Transition;


/**
 * Represents an event fired just after the transition finished firing.
 * 
 *<br><br>
 * TransitionFinishedEvent.java<br>
 * Created: Thu Apr 11 17:37:22 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.4 $ $Date: 2005/01/11 03:10:36 $
 *@since 2.0
 */
public class TransitionFinishedEvent extends TransitionEvent {
  
 	private static final long serialVersionUID = 3618700816051941938L;

 	public TransitionFinishedEvent(final Transition aTransition) {
 		super(aTransition);
 	}
  
} // TransitionFinishedEvent
//////////////////// end of file ////////////////////
