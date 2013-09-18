// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import java.util.EventObject;

import org.rakiura.cpn.Place;


/**
 * Represents an abstract Place event object.
 * 
 *<br><br>
 * PlaceEvent.java<br>
 * Created: Thu Apr 11 16:07:42 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.3 $
 *@since 2.0
 */
public abstract class PlaceEvent extends EventObject {
  
  /**
   * Creates a new <code>PlaceEvent</code> instance.
   *@param aPlace a <code>Place</code> value
   */
  public PlaceEvent(final Place aPlace) {
    super(aPlace);
  }

  /**
   * Returns the place, the source of this event.
   *@return a <code>Place</code> which is the 
   * source of this event.
   */
  public Place getPlace() {
    return (Place) getSource();
  }
  

} // PlaceEvent
//////////////////// end of file ////////////////////
