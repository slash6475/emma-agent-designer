// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**
 * Implements an adapter for {@link PlaceListener}.
 * 
 *<br><br>
 * PlaceAdapter.java<br>
 * Created: Wed Apr 17 10:55:33 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.2 $ 
 *@since 2.0
 */
@SuppressWarnings("all")
public class PlaceAdapter implements PlaceListener {
  
  public void notify(final PlaceEvent anEvent) {}
  public void notify(final TokensRemovedEvent anEvent) {}
  public void notify(final TokensAddedEvent anEvent) {}

} // PlaceAdapter
//////////////////// end of file ////////////////////
