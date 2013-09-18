// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Implements an abstract Arc.
 * 
 *<br><br>
 * AbstractArc.java<br>
 * Created: Mon Sep 25 21:27:28 2000<br>
 *
 * 
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.6 $
 *@since 1.0
 */
public abstract class AbstractArc extends Node implements Arc {

  /**/
  private Place place;
  /**/
  private Transition transition;
  /**/
  private CpnContext context = new CpnContext();


  /**/
  protected AbstractArc() {/*defalut*/}
  

  /**
   * Creates a new <code>AbstractArc</code> instance.
   * @param aFrom a <code>Place</code> value
   * @param aTo a <code>Transition</code> value
   */
  public AbstractArc(final Place aFrom, final Transition aTo){
    this.place = aFrom;
    this.transition = aTo;
  }

  /**
   * Creates a new <code>AbstractArc</code> instance.
   * @param aFrom a <code>Transition</code> value
   * @param aTo a <code>Place</code> value
   */
  public AbstractArc(final Transition aFrom, final Place aTo){
    this.place = aTo;
    this.transition = aFrom;
  }

  /**
   * Returns the transition for this arc.
   * @return a <code>Transition</code> value.
   */
  public Transition getTransition(){ return this.transition; }
  
  public void setTransition(Transition t) { this.transition = t; }

  /**
   * Returns the place for this arc.
   * @return a <code>Place</code> value
   */
  public Place getPlace(){ return this.place; }
  
  public void setPlace(Place p) { this.place = p; }

  /**
   * Returns the current context for this node.
   *@return CPN context for this node.
   */
  public CpnContext getContext() {
    return this.context;
  }
  
  /**
   * Sets the current context for this node.
   *@param aContext current context for this node.
   */
  public void setContext(final CpnContext aContext) {
    this.context = aContext;
  }


} // AbstractArc
//////////////////// end of file ////////////////////
