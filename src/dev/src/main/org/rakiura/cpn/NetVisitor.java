// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Utility for network analysis and graph traversal. This is based on
 * the object oriented switch, and follows strictly the Visitor design
 * pattern.  Appropriate nodes of the net, i.e. in particular places,
 * transitions, but also arcs  will call appropriate methods from this
 * interface. 
 * 
 *<br><br>
 * NetVisitor.java<br>
 * Created: Tue Sep 26 16:49:12 2000<br>
 *
 *
 *@see NetVisitorAdapter
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.3 $
 *@since 1.0
 */
public interface NetVisitor extends java.io.Serializable {
  
  /** Place callback. */
  void place(final Place aPlace);

  /** Transition callback. */
  void transition(final Transition aTransition);

  /** InputArc callback. */
  void inputArc(final InputArc anArc);

  /** OutputArc callback. */
  void outputArc(final OutputArc anArc);

  /** Net callback. */
  void net(final Net aNet);
  
} // NetVisitor
//////////////////// end of file ////////////////////
