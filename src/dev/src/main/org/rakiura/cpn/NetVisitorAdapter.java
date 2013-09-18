// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/

/**
 * Utility class for graph traversal. All methods implementations 
 * are empty in this adapter, the only default implementation is
 * provided for depth-first subnets traversal, i.e. all subnets will be
 * visited by this net visitor in depth first manner. To provide different 
 * subnet traversal one needs to override {@link #net(Net) net(Net)} method.
 * 
 *<br><br>
 * NetVisitorAdapter.java<br>
 * Created: Tue Sep 26 16:55:01 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.8 $
 *@since 1.0
 */
@SuppressWarnings("all")
public abstract class NetVisitorAdapter implements NetVisitor {
	
  public void place (Place place) {}
  
  public void transition (Transition transition) {}
  
  public void inputArc (InputArc arc) {}
  
  public void outputArc (OutputArc arc) {}
  
  public void net (Net net) {
  	final NetElement[] n = net.getNetElements ();
  	for (int i = 0; i < n.length; i++) {
  		n[i].apply (this);
  	}
  }

} // NetVisitorAdapter
//////////////////// end of file ////////////////////
