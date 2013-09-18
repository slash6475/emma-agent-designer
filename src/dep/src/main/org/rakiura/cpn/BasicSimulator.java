// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Basic implementation of the simulator. This is a very simple,
 * single threaded simulator. To use it, simply write:
 *<pre>
 *  Net net = //obtain the reference to the net object
 *  Simulator sim = new BasicSimulator(net);
 *   // to run it in step mode, just use:
 *   boolean enabledTransitions = true;
 *   while (enabledTransitions) {
 *       enabledTransitions = sim.step();
 *       // perform some analysis or
 *       // user-interactions here
 *   }
 *
 *  //to run it continuously, use:
 *  sim.run();
 *<pre>
 *
 *<br><br>
 * BasicSimulator.java<br>
 * Created: Tue Sep 26 16:36:38 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.13 $
 *@since 1.0
 */
public class BasicSimulator implements Simulator {

	/** Handle to the net. */
	private Net net;
	
	/** Static random seed. */
	private final static Random random = new Random(System.currentTimeMillis());

	/** Array of transitions. */
	private Transition[] transitions;

	/** List of enabled bindings for a step. */
	private List enabled;

	private boolean stop = false;

	/**
	 * Creates a new <code>BasicSimulator</code> instance.
	 * @param aNet a <code>Net</code> value.
	 */
	public BasicSimulator(final Net aNet) {
		this.net = aNet;
		processNetStructure();
	}

	/**
	 * Runs a single step of the simulation. All enabled transitions that can
	 * fire simultaneously will be picked from all enabled transitions
	 * (see {@link #occurrence}, and fired in sequence.<br>
	 * This method returns <code>true</code> if there are
	 * still more enabled transitions, and <code>false</code> if there
	 * is no enabled transition left.
	 * @return a <code>boolean</code> value, <code>true</code>
	 *  if there are still more enabled transitions, and <code>false</code>
	 *  if there is no enabled transition left.
	 */
	public boolean step() {
		// initial evaluation of net inscriptions
		processNetStructure();
		// lets make the firing of enabled transitions a random process
		Collections.shuffle(this.enabled, random);
		for (int t=0; t < this.enabled.size(); t++) {
			if ( ((Transition)this.enabled.get(t)).isEnabled() ) { //double check, enablement might have changed by firing other transitions.
				((Transition) this.enabled.get(t)).fire();
			}
		}
		// re-evaluation
		processNetStructure();
		return (this.enabled.size() > 0);
	}

	/**
	 * Runs the simulator in continuous mode.
	 * It will step through the net until no transition
	 * is enabled. This method implementation is equivalent
	 * of <code> while (step()); </code> code.
	 */
	public void run() {
		while (step() && !this.stop) {/*run the step*/}
		this.stop = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		this.stop = true;
	}

	/**
	 * Returns the handle to the net.
	 *@return a <code>Net</code> value
	 */
	public Net net() {
		return this.net;
	}

	/**
	 * Returns the list of all enabled transitions. This method
	 * returns all enabled transitions with the appropriate
	 * marking for each of them.
	 *@return a <code>List</code> of all enabled transitions.
	 */
	public List occurrence(){
		return this.enabled;
	}

	/**
	 * Prepares the net internal representation for simulation.
	 */
	void processNetStructure(){
		this.transitions = this.net.getAllTransitions();
		this.enabled = enabledTransitionList();
	}

	/**
	 * Prepares the list of all enabled transitions.
	 *@return a <code>List</code> value with enabled transitions.
	 */
	private List enabledTransitionList(){
		final List list = new ArrayList(this.transitions.length);
		for (int i = 0; i < this.transitions.length; i++) {
			if (this.transitions[i].isEnabled()) list.add(this.transitions[i]);
		}
		return list;
	}


} // BasicSimulator
//////////////////// end of file ////////////////////
