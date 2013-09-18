// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Transition;
import org.rakiura.cpn.Simulator;

/**
 * A Petri net simulator and executor. This simulator can be used to simulate
 * a net with the notion of time, or to execute a net for real time purposes. 
 * Check also TimedSimulator for simulated time petri nets.<br>
 * The simulation / execution happens in its own thread. As long as only the
 * methods provided in this class are used to manipulate the net,
 * the execution AND manipulation of the Petri net are thread safe.<br>
 * If this class is used for simulation, you are supposed not to start any new
 * threads (to keep the notion of simulation time valid) and you can use the
 * method {@link #addTimedToken addTimedToken} to add tokens that are only
 * enabled after a certain time.<br>
 * In the case you use this class for execution, you are encouraged to create
 * new threads to execute actions and to put tokens back into the net if the
 * execution is done, with the method {@link #addTokens}.<br>
 * The simulation/execution thread will run until there are no enabled
 * transitions left. The thread will sleep until new tokens are added to the
 * net.<Br>
 * A history of fired transitions is stored.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Dept. of Information Science, University of Otago, Dunedin</p>
 * @author  <a href="mfleurke@infoscience.otago.ag.nz">Martin Fleurke</a>
 * @version $Revision: 1.3 $ $Date: 2009/03/14 04:02:23 $
 *
 */
public class ThreadedSimulator extends Thread implements Simulator {

	private Net cpn;
	private boolean canDoStep = true;
	private boolean stop = false;
	private boolean pause = false;
	private HashMap tokensToAdd = new HashMap(); //unsynchronized!
	private ArrayList history = new ArrayList();

	/** Array of transitions. */
	private Transition[] transitions;

	/** List of enabled bindings for a step. */
	private List enabledTransitions;

	/** Simulated clock. */
	private long time = 0;

	/** Timestamps holder. */
	private final Map tokens2Time = new HashMap();

	/** Timed tokens hash. */
	private final Map timedTokens2Place = new HashMap();

	private HashSet timeListeners = new HashSet();

	private final Object pauseMonitor = new Object();

	/**
	 * this object is locked by the thread if the simulator is still running the
	 * simulation.
	 * Can be used to check if the simulation is still running.<br>
	 * <code>synchronized(executeMonitor) { .. }</code>
	 */
	public final Object executeMonitor = new Object();

	/**
	 * Creates a new threaded simulator.
	 * @param cpn the Petri net to simulate
	 */
	public ThreadedSimulator(Net cpn) {
		this.cpn = cpn;
	}

	/**
	 * returns the net that is executed by this simulator.
	 * <b>Warning:</b> any attempt to change the net and its marking while the
	 * simulator is still running can raise exceptions and/or produce unexpected behaviour.
	 * @return the Coloured Petri net that is set for this simulator to (be) execute(d)
	 */
	public Net net () {
		return this.cpn;
	}

	/**
	 * Adds a {@link TimeListener time listener} to this simulator. Time listeners
	 * are informed when the simulation time changes.
	 * @param listener a time listener.
	 * @see #removeTimeListener
	 */
	public void addTimeListener(TimeListener listener) {
		this.timeListeners.add(listener);
	}

	/**
	 * Removes a {@link TimeListener time listener} from this simulator.
	 * @param listener the listener
	 */
	public void removeTimeListener(TimeListener listener) {
		this.timeListeners.remove(listener);
	}

	/**
	 * Returns the current simulation time.
	 * @return the current simulation time.
	 */
	public long getTime() {
		return this.time;
	}

	/**
	 * This method will execute the Petri net until stopSimulation is called or
	 * no transitions are left. During the running, a lock is on {@link #executeMonitor}.
	 * If you need to stop this simulator, you can request a lock on the object
	 * as well, to be sure that the simulator has finished its last step.
	 * @see #stopSimulation()
	 */
	public void run() {
		//set name of thread
		Thread.currentThread().setName(""+Thread.currentThread().getName()+"_Running_"+this.cpn.getName());
		// Debug information 
		System.out.println(""+Thread.currentThread().getName()+" started running");
		try {
			synchronized(this.executeMonitor) {
				//execute thread
				while (!this.stop){
					if (this.pause) {
						synchronized(this.pauseMonitor) { this.pauseMonitor.wait();	}
						if (this.stop) break;
					}
					this.canDoStep = step();
					if (!this.canDoStep && !this.stop) { //wait for new tokens
						try {//get ownership/monitor of object, so we can wait for a notify
							synchronized(this.tokensToAdd) { this.tokensToAdd.wait(); }
							//when waiting, lock on tokensToAdd is released.
						}	catch (InterruptedException ex) {/*do nothing*/}
					}
				} //end while

			} //release executeMonitor
		} catch (Exception e) {
			System.err.println("Exception occurred. Execution history = "+this.history);
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+" finished running");
	}

	/**
	 * Stops the execution of the simulator when the current step is finished.
	 */
	public void stopSimulation() {
		this.stop = true;
		synchronized(this.tokensToAdd) { //make sure simulator goes on to stop permanently
			this.tokensToAdd.notifyAll();
		}
		synchronized(this.pauseMonitor) { //make sure simulator goes on to stop permanently
			this.pauseMonitor.notifyAll();
		}
	}

	/**
	 * The simulation thread will pause before the next step is executed. You can
	 * resume the simulation with {@link #resumeSimulation}.
	 * @return true if the simulation was not paused yet, false otherwise
	 */
	public boolean pauseSimulation() {
		if (!this.pause) {
			this.pause = true;
			return true;
		}
		return false;
	}

	/**
	 * The simulation thread will resume with the next step after it was paused
	 * with {@link #resumeSimulation}.
	 * @return true if the simulation was paused and is now resumed, false otherwise
	 */
	public boolean resumeSimulation() {
		if (this.pause) {
			this.pause = false;
			synchronized (this.pauseMonitor) {
				this.pauseMonitor.notifyAll();
			}
			return true;
		}
		return false;
	}

	/**
	 * returns the handle to this simulators history. The first fired transition
	 * is at place 0, the rest is in chonological order starting from there.
	 * @return a copy of the history.
	 */
	public ArrayList getHistory() {
		return new ArrayList(this.history);
	}

	/**
	 * Returns if there are still (timed) tokens to be added to the net.
	 * @return true if there are (timed) tokens not added yet, false otherwise.
	 */
	public boolean hasTokens() {
		return ( !this.tokensToAdd.isEmpty() || !this.tokens2Time.isEmpty());
	}

	/**
	 * safe way of adding tokens
	 * @param tokens the token
	 * @param place the name of the place
	 */
	public void addTokens(Collection tokens, String place) {
		if (tokens == null) return;
		synchronized(this.tokensToAdd) {
			this.tokensToAdd.put(tokens, place);
			this.canDoStep = true;
			this.tokensToAdd.notifyAll();
		}
	}

	/**
	 * adds a token to the net that is activated only after a certain simulation
	 * time has passed.
	 * @param token the token to add
	 * @param place the place to add the token to
	 * @param relativeTime the time to disable the token. Any number < 0 will be interpreted as 0
	 */
	public void addTimedToken(Object token, String place, long relativeTime) {
		if (relativeTime < 0) relativeTime = 0;
		synchronized(this.tokensToAdd) {
			this.tokens2Time.put(token, new Long(this.time+relativeTime));
			this.timedTokens2Place.put(token, place);
			this.tokensToAdd.notifyAll();
		}
	}

	/**
	 * called to really add the added tokens to the net.
	 */
	private void addAddedTokens() {
		synchronized(this.tokensToAdd) {
			if (!this.tokensToAdd.isEmpty()) {
				Set entrySet = this.tokensToAdd.entrySet();
				Iterator iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Map.Entry entry  = (Map.Entry) iterator.next();
					try {
						((Place)this.cpn.forName((String)entry.getValue())).addTokens((Collection)entry.getKey());
					} catch (Exception e) {
						System.err.println("Failed to add token "+entry.getKey()+"to place"+entry.getValue());
						System.err.println("Reason: "+e.getMessage());
					}
				}
				this.tokensToAdd.clear();
				this.canDoStep = true;
			}
		} //tokensToAdd released
	}

	private void addAddedTimedTokens() {
		synchronized(this.tokensToAdd) {
			if (!this.tokens2Time.isEmpty()) {
				Set entryset = this.tokens2Time.entrySet();
				Iterator esi = entryset.iterator();
				while (esi.hasNext() ) {
					Map.Entry entry = (Map.Entry) esi.next();
					if (this.time == ((Long)entry.getValue()).longValue() ) {
						Object token = entry.getKey();
						Place place = (Place)this.cpn.forName(
								(String)this.timedTokens2Place.get(token) );
						if (place == null) {
							System.err.println("token could not be added to place: place "
																 +(String)this.timedTokens2Place.get(token)
																 +" not found.");
						} else {
							this.timedTokens2Place.remove(token);
							esi.remove();
							place.addToken(token);
							this.canDoStep = true;
						}
					}
				}
			}
		} //tokensToAdd released
	}

	/**
	 * Runs a single step of the simulation. <br>
	 * All enabled transitions that can fire simultaneously will be picked,
	 * and fired. <Br>
	 * The fired transitions' names will be added to the history.<br>
	 * This method returns <code>true</code> if there are
	 * still more enabled transitions, and <code>false</code> if there
	 * is no enabled transition left. If there are timed tokens, the simulation
	 * time will be increased to the next time where new tokens are enabled.
	 * Untimed tokens are all added, timed tokens of the current (or the new)
	 * time are added.
	 * @return a <code>boolean</code> value, <code>true</code>
	 *  if there are still more enabled transitions, and <code>false</code>
	 *  if there is no enabled transition left.
	 */
	public boolean step() {
		// initial evaluation of net inscriptions
		processNetStructure();

		for (int t=0; t < this.enabledTransitions.size(); t++) {
			if ( ((Transition)this.enabledTransitions.get(t)).isEnabled() ) {
				this.history.add(((Transition)this.enabledTransitions.get(t)).getName());
				((Transition) this.enabledTransitions.get(t)).fire();
			}
		}
		Thread.yield(); //allow other threads to add tokens
		addAddedTokens();
		addAddedTimedTokens();
		// re-evaluation
		processNetStructure();
		if (this.enabledTransitions.size() <= 0) {
			addTokensAndTimeTillEnabled();
			//add timed tokens & increase time
			//time increases till all timedTokens are out of stock or
			//till net is enabled again
		}
		return (this.enabledTransitions.size() > 0);
	}

	/**
	 * Adds added tokens to the net straight away if the simulation is not running.
	 * Use this method to display added tokens straight away when the simulation
	 * is not running, but executed step by step.
	 */
	public void addTokensNow() {
		if (!this.isAlive()) {
			addAddedTokens();
			addAddedTimedTokens();
		}
	}

	/**
	 * Prepares the net internal representation for simulation.
	 */
	private void processNetStructure(){
		this.transitions = this.cpn.getAllTransitions();
		this.enabledTransitions = enabledTransitionList();
	}

	/**
	 * Prepares the list of all enabled transitions.
	 *@return a <code>List</code> value with enabled transitions.
	 */
	private List enabledTransitionList(){
		final List list = new ArrayList(this.transitions.length);
		for (int i = 0; i < this.transitions.length; i++) {
			if (this.transitions[i].isEnabled()) list.add( this.transitions[i]);
		}
		return list;
	}

	/**
	 * Increases time and adds timed tokens till enabled or out of tokens.
	 */
	private void addTokensAndTimeTillEnabled() {
		while (!this.tokens2Time.isEmpty() ) {
			while (!this.tokens2Time.containsValue(new Long(this.time)) ) {
				this.time++; //increase time to where there are new enabled tokens
				informListeners();
			}
			addAddedTimedTokens();
			// re-evaluation
			processNetStructure();
			if (this.enabledTransitions.size() > 0) {
				break;
			}
		}
	}

	/**
	 * Informs listeners one by one.
	 */
	private void informListeners() {
		final TimeListener[] l = (TimeListener[]) this.timeListeners.toArray(
				new TimeListener[this.timeListeners.size()] );
		for (int i = 0; i < l.length; i++) {
			l[i].timeChanged(this.time);
		}
	}

	/**
	 * Represents an object that can listen to changes in the simulation time of
	 * this Simulator. Every time the simulator changes its time, it will warn
	 * all its TimeListeners by invoking #timeChanged in the thread of the
	 * simulator. All listeners should implement that method to do something
	 * useful. If the action takes too long, it should create a new thread not to
	 * hold up the simulator.
	 * @author <a href="mfleurke@infoscience.otago.ac.nz">Martin Fleurke</a>
	 */
	public interface TimeListener {

		/**
		 * called when the simulator changes its time.
		 * @param time the new time value.
		 */
		void timeChanged(long time);
	}

} ///////////////////EOF///////////////