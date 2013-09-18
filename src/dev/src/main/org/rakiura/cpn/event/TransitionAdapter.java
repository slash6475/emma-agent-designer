// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;


/**
 * Adapter for {@link TransitionListener}.
 * 
 *<br><br>
 * TransitionAdapter.java<br>
 * Created: Wed Apr 17 10:51:10 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.3 $
 *@since 2.0
 */
@SuppressWarnings("all")
public class TransitionAdapter implements TransitionListener {
  
  public void notify(final TransitionEvent anEvent) {}
  public void notify(final TransitionStartedEvent anEvent) {}
  public void notify(final TransitionFinishedEvent anEvent) {}
  public void notify(final TransitionStateChangedEvent anEvent) {}
  
  
} // TransitionAdapter
//////////////////// end of file ////////////////////
