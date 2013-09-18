// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.event;

/**/
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.Place;


/**
 * Represents an Place event. This even is generated
 * when tokens are added to a place.
 * 
 *<br><br>
 * TokensAddedEvent.java<br>
 * Created: Thu Apr 11 16:13:43 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.3 $ 
 *@since 2.0
 */
public class TokensAddedEvent extends PlaceEvent {
  
  private static final long serialVersionUID = 3257564018624770103L;
	
  /**/
  private Multiset multiset;
  
  /**
   * Creates a new <code>TokensAddedEvent</code> instance.
   * @param aPlace a <code>Place</code> value.
   * @param aTokens a <code>Multiset</code> containing all added tokens.
   */
  public TokensAddedEvent(final Place aPlace, final Multiset aTokens) {
    super(aPlace);
    this.multiset = aTokens;
  }

  /**
   * Returns the tokens which where added to the place.
   * @return a <code>Multiset</code> value
   */
  public Multiset getTokens() {
    return this.multiset;
  }
  
} // TokensAddedEvent
//////////////////// end of file ////////////////////
