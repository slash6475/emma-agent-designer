// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import java.util.EventListener;


/**
 * Represents a Place event listener. This listener will be notified
 * each time a token is added or removed from a place.
 * 
 *<br><br>
 * PlaceListener.java<br>
 * Created: Thu Apr 11 15:44:50 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.2 $ 
 *@since 2.0
 */
public interface PlaceListener extends EventListener {
    
  /**
   * Notifies the listener that the marking of the place has changed. 
   * It may be caused by either adding or removing a token from 
   * a Place.
   *@param anEvent a <code>PlaceEvent</code> value
   */
  void notify(final PlaceEvent anEvent);

  /**
   * Notifies the listener that the marking of the place has changed. 
   * It is fired when tokens are removed from the place.
   * @param anEvent a <code>TokensRemovedEvent</code> value
   */
  void notify(final TokensRemovedEvent anEvent);

  /**
   * Notifies the listener that the marking of the place has changed. 
   * It is fired when tokens are added to the place.
   * @param anEvent a <code>TokensAddedEvent</code> value
   */
  void notify(final TokensAddedEvent anEvent);

} // PlaceListener
//////////////////// end of file ////////////////////
