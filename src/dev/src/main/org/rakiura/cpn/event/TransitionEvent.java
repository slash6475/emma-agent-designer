// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import java.util.EventObject;

import org.rakiura.cpn.Transition;

/**
 * Represents a transition event.
 * 
 *<br><br>
 * TransitionEvent.java<br>
 * Created: Thu Apr 11 17:35:28 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.3 $
 *@since 2.0
 */
public abstract class TransitionEvent extends EventObject {
  
  /**
   * Creates a new <code>TransitionEvent</code> instance.
   * @param aTransition a <code>Transition</code> value
   */
  public TransitionEvent(final Transition aTransition) {
    super(aTransition);
  }

  /**
   * Returns the transition, which is the source of this event.
   *@return the transition, source of the event.
   */
  public Transition getTransition() {
    return (Transition) this.getSource();
  }
  
} // TransitionEvent
//////////////////// end of file ////////////////////
