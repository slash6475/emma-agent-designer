// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;


/**
 * Represents a protocol to a simulator.
 *
 *<br><br>
 * Simulator.java<br>
 * Created: Tue Sep 26 16:23:07 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.7 $
 *@since 1.0
 */
public interface Simulator {

	/**
	 * Runs the simulation in step mode.
	 *@return true if there is some enabled transitions.
	 */
	boolean step();

	/**
	 * Runs the simulation in a continuous mode.
	 */
	void run();

	/**
	 * stops the simulator if running in continuous mode.
	 */
	void stop();

	/**
	 *@return the net on which this simulator operates.
	 */
	Net net();

} // Simulator
//////////////////// end of file ////////////////////
