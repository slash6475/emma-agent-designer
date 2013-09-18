// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Represents an abstract Arc in the JFern Petri Net.
 * 
 *<br><br>
 * Arc.java<br>
 * Created: Mon Sep 25 11:49:12 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.11 $
 *@since 1.0
 */
public interface Arc extends NetElement {

  /** 
   * Returns the transition for this arc.
   * @return transition for this arc.
   * @since 4.0
   */
  Transition getTransition();
  
  /**
   * Sets the transition for this arc.
   * @param t transition for this arc.
   * @since 4.0
   */
  void setTransition(Transition t);

  /** 
   * Returns the place for this arc.
   *@return place for this arc
   *@since 4.0
   */
  Place getPlace();

  /**
   * Sets the place for this arc.
   * @param p place for this arc.
   * @since 4.0
   */
  void setPlace(Place p);
  
  /**
   * Unplugs this arc from corresponding Place and Transition.
   * @since 3.0
   */
  void release();

  /**
   * Sets this arc expression text.
   * @param anExpression expression text.
   */
  void setExpressionText(final String anExpression);

  /**
   * Returns this arc expression text.
   * @return expression text. 
   */
  String getExpressionText();
  
  /**
   * Returns the context for this arc.
   *@return context for this arc.
   */
  CpnContext getContext();

} // Arc
//////////////////// end of file ////////////////////
