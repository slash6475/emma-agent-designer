// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rakiura.cpn.event.PlaceEvent;
import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TokensAddedEvent;
import org.rakiura.cpn.event.TokensRemovedEvent;
import org.rakiura.cpn.event.TransitionFinishedEvent;
import org.rakiura.cpn.event.TransitionListener;
import org.rakiura.cpn.event.TransitionStartedEvent;
import org.rakiura.cpn.event.TransitionStateChangedEvent;

/**
 * Represents a transition in JFern Petri net model.
 * This class implements a simple transition, and the behaviour
 * can be overwritten by custom subclasses.<br>
 * The input and output arcs are stored in HashSet containers.
 *
 *<br><br>
 * Transition.java<br>
 * Created: Mon Sep 25 19:45:05 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.47 $
 *@since 1.0
 */
public class Transition extends Node implements PlaceListener {

	private static final long serialVersionUID = 3257004367155377974L;
	
	/**/
	private List listeners = new ArrayList();
	/**/
	List inputs = new ArrayList(10);
	/**/
	private List outputs = new ArrayList(10);

	/**/
	private boolean enabled = false;
	/**/
	private boolean uptodate = false;
	/**/
	//private CpnContext binding = null;
	/**/
	private CpnContext context = new CpnContext();

	/**/
	private String actionText = "";
	/**/
	private String guardText = "";

	/**
	 * The type of the transition / its use.
	 * e.g. a transition for control only or a transition to execute something.
	 */
	private String type = "";

	/**
	 * Specification of the type.
	 * e.g. and-split, or-split (if type = routing),
	 * name of the action to be taken (if type = task).
	 */
	private String specification = "";

	// strings for 'type'
	/** type = a transition for routing of tokens only */
	public static final String ROUTING = "routing";

	/** type = a transition for execution of a task only  */
	public static final String TASK = "task";

	// strings for 'specification' if type = routing.
	/** description to denote that this routing transition is an AND-split*/
	public static final String ANDSPLIT = "AND-split";

	/** description to denote that this routing transition is an AND-join*/
	public static final String ANDJOIN = "AND-join";

	/** description to denote that this routing transition is an OR-split*/
	public static final String ORSPLIT = "OR-split";

	/** description to denote that this routing transition is an OR-join*/
	public static final String ORJOIN = "OR-join";

	/** description to denote that this routing transition is an AND/OR-split*/
	public static final String ANDORSPLIT = "AND/OR-split";


	/** Default guard. */
	private Guard guard = new Guard() {
		public boolean evaluate() {
			return Transition.this.inputs.size() > 0;
		}
	};

	/** Default action. */
	private Action action = new Action();

	/**
	 * Creates a new <code>BasicTransition</code> instance.
	 * Name and ID are set to a new unique ID.
	 */
	public Transition() {/*default*/}

	/**
	 * Creates a new <code>BasicTransition</code> instance.
	 * It will give the transition a new unique ID as well.
	 *@param aName a <code>String</code> value
	 */
	public Transition(final String aName) {
		super(aName);
	}

	/**
	 * Adds an input Arc. This method is not for the user to call
	 * directly, it is called by newly created arcs which plug
	 * themselves automatically to the appropriate transitions.
	 * The transition will register itself as a placelisterner to the arc's place.
	 * @param anArc the input arc to add to the transition
	 * @see #removeInput
	 *@return this Transition.
	 */
	public Transition addInput(final InputArc anArc) {
		this.inputs.add(anArc);
		anArc.getPlace().addPlaceListener(this);
		anArc.setContext(new CpnContext(this.context));
		return this;
	}

	/**
	 * Removes an input Arc. This method is not for the user to call
	 * directly, it is called by arcs which unplug
	 * themselves automatically from the appropriate transitions.
	 * The transition removes itself as placelistener from the arc's place.
	 * @param anArc the arc to remove.
	 * @see #addInput(InputArc)
	 *@return this Transition.
	 */
	public Transition removeInput(final InputArc anArc) {
		this.inputs.remove(anArc);
		anArc.getPlace().removePlaceListener(this);
		return this;
	}

	/**
	 * Adds an output Arc. This method is not for the user to call
	 * directly, it is called by newly created arcs which plug
	 * themselves automatically to the appropriate transitions.
	 * @param anArc the output arc to add.
	 * @see #removeOutput
	 *@return this Transition.
	 */
	public Transition addOutput(final OutputArc anArc) {
		this.outputs.add(anArc);
		anArc.setContext(new CpnContext(this.context));
		return this;
	}

	/**
	 * Removes an output Arc. This method is not for the user to call
	 * directly, it is called by arcs which unplug
	 * themselves automatically from the appropriate transitions.
	 * @param anArc the arc to remove from the transition
	 *@return this Transition.
	 * @see #addOutput
	 */
	public Transition removeOutput(final OutputArc anArc) {
		this.outputs.remove (anArc);
		return this;
	}

	/**
	 * Returns list of all input arcs.
	 *@return set of all input arcs for this transition.
	 */
	public List inputArcs() {
		return this.inputs;
	}

	/**
	 * Returns list of all output arcs.
	 *@return set of all output arcs for this transition.
	 */
	public List outputArcs() {
		return this.outputs;
	}

	/**
	 * Sets the guard for this transition.
	 *@param aGuard a <code>Guard</code> value
	 *@return an old (previous)  <code>Guard</code> value
	 */
	public Guard setGuard(final Guard aGuard) {
		final Guard old = this.guard;
		this.guard = aGuard;
		return old;
	}

	/**
	 * Sets the action for this transition.
	 *@param anAction an <code>Action</code> value
	 *@return an old (previous) <code>Action</code> value
	 */
	public Action setAction(final Action anAction) {
		final Action old = this.action;
		this.action = anAction;
		return old;
	}

	/**
	 * Returns the action for this transition.
	 *@return current <code>Action</code> value
	 */
	public Action action() {
		return this.action;
	}

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

	/**
	 * Cloning.
	 * @return the cloned copy of this transition.
	 */
	public Object clone() {
		final Transition t = ((Transition) super.clone());
		return t;
	}

	/**
	 * Expression evaluation and unification.
	 * Executes the expressions and tries to perform
	 * unification procedure. If a binding in which this transition
	 * is enabled is found, this transition is enabled and this
	 * transition context (i.e. varPool inside context) keeps the
	 * valid binding.
	 * @return <code>true</code> if the transition is enabled,
	 * <code>false</code> otherwise.
	 */
	private boolean unify() {
		if (!this.uptodate) {
			final boolean oldState = this.enabled;
			for (int i = 0; i < this.inputs.size(); i++) {
				final InputArc arc = ((InputArc) this.inputs.get(i));
				final Multiset minput = arc.getPlace().getTokens();
				arc.getContext().setMultiset(minput);
				arc.expression();
			}

			this.context.getVarPool().clear();
			try {
				this.enabled = findBinding(0);
			}
			catch (Exception ex) {
				this.enabled = false;
				System.err.println("Exception in finding binding for transition "+getName());
				ex.printStackTrace();
			}

			this.uptodate = true;
			if (oldState != this.enabled) {
				fireTransitionStateChangedEvent();
			}
		}
		return this.enabled;
	}

	/**
	 * Finds the first binding which enables this transition.
	 *@param index current index of the input arc which is
	 * being iterated through
	 * @return <code>true</code> if there is a binding that enables the transition,
	 * <code>false</code> otherwise.
	 */
	private final boolean findBinding(final int index) {
		// if this transition does not have input arcs at all, just check the guard
		if (this.inputs.size() == 0) {
			return this.guard.evaluate();
		}
		final int newIndex = index + 1;
		final CpnContext c = ((Arc) this.inputs.get(index)).getContext();
		final List allPossible = c.getPossibleBindings();
		if (allPossible.size() == 0) {
//The following code allows an OR-join, but then you have to add guards on
//every arc to check if a variable != null. So therefore we do not allow this.
///Instead, an OR-join can be done via a Place
//			c.setBinding(new HashMap());
//			if (((InputArc) this.inputs.get(index)).guard()) {
//				if (newIndex == this.inputs.size()) {
//					return this.guard.evaluate();
//				} else {
//					return findBinding(newIndex);
//				}
//			} else
			return false;
		}

		final Iterator iter = allPossible.iterator();
		while (iter.hasNext()) {
			final Map abinding = (Map) iter.next();
			c.setBinding(abinding);
			this.context.getVarPool().putAll(abinding);
			if (!((InputArc) this.inputs.get(index)).guard()) continue;
			if (newIndex == this.inputs.size()) {
				this.context.setMultiset(new Multiset(this.context.getVarPool().values()));
				if (this.guard.evaluate()) {
					// first one best
					return true;
				}
			} else {
				return findBinding(newIndex);
			}
		}
		return false;
	}

	/**
	 * Checks if this transition is enabled.
	 *@return a <code>boolean</code> value, <code>true</code> if this
	 * transition is enabled, <code>false</code>
	 * otherwise.
	 */
	public boolean isEnabled() {
		return unify();
	}

	/**
	 * Sets the guard text.
	 *@param aText guard text.
	 */
	public void setGuardText(final String aText) {
		this.guardText = aText;
	}

	/**
	 * Returns the guard text.
	 *@return this transition guard text.
	 */
	public String getGuardText() {
		return this.guardText;
	}

	/**
	 * Sets the action text.
	 *@param aText action text.
	 */
	public void setActionText(final String aText) {
		this.actionText = aText;
	}

	/**
	 * Returns the action text.
	 *@return this transition action text.
	 */
	public String getActionText() {
		return this.actionText;
	}

	/**
	 * Sets the type description of this transition. Example: control, task, ..<Br>
	 * The setting of the type has no effect on the transition, but proper use
	 * allows the user to use it for analysis etc. without having to interpret the
	 * java code.
	 * @param type the type
	 */
	public void setTypeText(String type) {
		this.type = type;
	}

	/**
	 * Returns the type description of this transition.
	 * Example: routing, task, ...
	 * @return the type
	 * @see #setTypeText(String)
	 */
	public String getTypeText() {
		return this.type;
	}

	/**
	 * Sets the specification text of the type.
	 * Examples: AND-split, OR-SPLIT, 'taksname', ...
	 * The setting of this descriptive text has no influence on the behaviour of
	 * the transition, but if used properly, it can help in analysis etc.
	 * @param specification the specification text.
	 * @see #setTypeText
	 */
	public void setSpecificationText(String specification) {
		this.specification = specification;
	}

	/**
	 * returns the specification text of the type. Examples: AND-split, OR-SPLIT,
	 * 'taksname', ...
	 * @return the specification text
	 * @see #getTypeText
	 * @see #getSpecificationText
	 */
	public String getSpecificationText() {
		return this.specification;
	}

	/**
	 * Fires this transition and executes an action code. Same
	 * as {@link #fire(boolean) fire(true)}.
	 *@see #fire(boolean)
	 *@return this <code>Transition</code>
	 */
	public Transition fire() {
		fire(true);
		return this;
	}

	/**
	 * Fire this transition. If this transition is enabled,
	 * the tokens will be taken from input places according
	 * to the current binding, action will be executed, and
	 * tokens will be placed into output places according to
	 * the binding.
	 *@param withAction if <code>true</code> action code will
	 * be executed, if <code>false</code> action code execution
	 * will be skipped.
	 *@return this <code>Transition</code>
	 *@since 3.0
	 */
	public Transition fire(final boolean withAction) {
		if (this.listeners.size() > 0) {
			fireTransitionStartedEvent();
		}
		//remove all prepared tokens from input places
		final Iterator iter = this.inputs.iterator();
		while (iter.hasNext()) {
			final InputArc arc = ((InputArc) iter.next());
			arc.getPlace().removeTokens(arc.getContext().getBinding().values());
		}
		if (withAction) {
			try {
				this.action.execute();
			}
			catch (Exception ex) {
				System.err.println("Exception in executing action of transition "+getName());
				ex.printStackTrace();
			}
		}

		//put the appropriate multisets into output places
		final Iterator oter = this.outputs.iterator();
		while (oter.hasNext()) {
			final OutputArc arc = ((OutputArc) oter.next());
			arc.getContext().setMultiset(new Multiset(this.context.getVarPool().values()));
			arc.getContext().setBinding(this.context.getVarPool());
			try {
				arc.getPlace().addTokens(arc.expression());
			}
			catch (Exception ex) {
				System.err.println("Exception in generating tokens on outputarc "+arc.getName()+" of transition "+getName());
				ex.printStackTrace();
			}
		}

		if (this.listeners.size() > 0) {
			fireTransitionFinishedEvent();
		}

		return this;
	}

	/**
	 * Registers a given TransitionListener with this place.
	 *@param aListener a <code>TransitionListener</code> to
	 * be registered with this Transition.
	 *@since 2.0
	 */
	public void addTransitionListener(final TransitionListener aListener) {
		this.listeners.add(aListener);
	}

	/**
	 * Deregisters a given TransitionListener from this place.
	 *@param aListener a <code>TransitionListener</code> to
	 * be removed with this Transition.
	 *@since 2.0
	 */
	public void removeTransitionListener(final TransitionListener aListener) {
		this.listeners.remove(aListener);
	}

	/**
	 * Notifies all listeners just before this transition fire.
	 */
	public void fireTransitionStartedEvent() {
		final TransitionStartedEvent event = new TransitionStartedEvent(this);
		final TransitionListener[] l = (TransitionListener[])
			this.listeners.toArray(new TransitionListener[this.listeners.size()]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(event);
		}
	}

	/**
	 * Notifies all listeners just after the transition finished firing.
	 */
	public void fireTransitionFinishedEvent() {
		final TransitionFinishedEvent event = new TransitionFinishedEvent(this);
		final TransitionListener[] l = (TransitionListener[])
		 this.listeners.toArray(new TransitionListener[this.listeners.size()]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(event);
		}
	}

	/**
	 * Notifies all listeners just after this transition state changed.
	 */
	private void fireTransitionStateChangedEvent() {
		final TransitionStateChangedEvent event =
			new TransitionStateChangedEvent(this);
		final TransitionListener[] l = (TransitionListener[])
			this.listeners.toArray(new TransitionListener[this.listeners.size()]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(event);
		}
	}

	public void notify(final PlaceEvent anEvent) {
		// ignore
	}
	public void notify(final TokensRemovedEvent anEvent) {
		// binding re-evaluation needed.
		this.uptodate = false;
	}
	public void notify(final TokensAddedEvent anEvent) {
		// binding re-evaluation needed.
		this.uptodate = false;
	}

	/**
	 * Visitor pattern.
	 *@param aVisitor a <code>NetVisitor</code> value
	 *@return this <code>NetElement</code> value
	 */
	public NetElement apply(final NetVisitor aVisitor) {
		aVisitor.transition(this);
		return this;
	}

	/** Returns this transition handle. Used in Guard and Action inner classes. */
	public Transition getThis() {
		return this;
	}
	
	/**
	 * @return a <code>String</code> value
	 */
	public String toString() {
		return "Transition: " + getName();
	}

	/**
	 * Represents this transition guard.
	 */
	public abstract class Guard implements Context {

		/**
		 * Guard function.
		 *@return <code>true</code> if this guard evaluates
		 * to enabled transition/arc;
		 * <code>false</code> otherwise.
		 */
		public abstract boolean evaluate();

		public void var(final String aVariable) {
			getContext().var(aVariable);
		}
		public void var(final int aNumber) {
			getContext().var(aNumber);
		}
		public void var(final String aVariable, final Class atype) {
			getContext().var(aVariable, atype);
		}
		public void var(final int aNumber, final Class atype) {
			getContext().var(aNumber, atype);
		}

		public Object get(final String aVariable) {
			return getContext().get(aVariable);
		}
		public Multiset getMultiset() {
			return getContext().getMultiset();
		}
		public Transition getThisTransition() {
			return getThis();
		}

	} // Guard

	/**
	 * Represents this transition action.
	 */
	public class Action implements Context {

		/**
		 * Transition action.
		 */
		public void execute() {/*do nothing by default*/
		}

		public void var(final String aVariable) {
			getContext().var(aVariable);
		}
		public void var(final int aNumber) {
			getContext().var(aNumber);
		}
		public void var(final String aVariable, final Class atype) {
			getContext().var(aVariable, atype);
		}
		public void var(final int aNumber, final Class atype) {
			getContext().var(aNumber, atype);
		}

		public Object get(final String aVariable) {
			return getContext().get(aVariable);
		}
		public Multiset getMultiset() {
			return getContext().getMultiset();
		}
		public Transition getThisTransition() {
			return getThis();
		}
		public CpnContext getCpnContext() {
			return getContext();
		}
	} // Action


} // Transition
//////////////////// end of file ////////////////////