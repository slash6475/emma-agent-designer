// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Point;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.Transition;
import org.rakiura.cpn.event.TransitionEvent;
import org.rakiura.cpn.event.TransitionFinishedEvent;
import org.rakiura.cpn.event.TransitionListener;
import org.rakiura.cpn.event.TransitionStartedEvent;
import org.rakiura.cpn.event.TransitionStateChangedEvent;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.figure.RectangleFigure;
import org.rakiura.draw.figure.TextFigure;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

/**
 * Figure representing a CPN getTransition().
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.60 $
 *@since 3.0
 */
public final class CPNTransitionFigure extends RectangleFigure
	implements CPNAbstractFigure, TransitionListener {


	private static final long serialVersionUID = 3257567308569458231L;

	/** A child figure that displays the name of this Place. */
	private CPNNameFigure name;

	/** A subfigure that displays the action code text of this getTransition().*/
	private CPNAnnotationFigure action;

	/** A subfigure that displays the guard code text of this getTransition().*/
	private CPNAnnotationFigure guard;

	/** The transition wrapped by this figure. */
	private transient Transition transition;

	/** Index in what stage of inspection we are. */
	private int inspectStatus = 0;

	/** The size of the transition, the height of the graphical rectangle.
	 The width of the rectangle is three times the size. */
	final private static int DEFAULT_SIZE = 20;

	private Color oldStateColor = Color.GREEN;
	private Color oldFireColor = Color.GREEN;

	/** The time to display the rectangle in a different color if it is involved
	 in an event. */
	public static long DELAY = 1000;

	/**
		* Creates a new TransitionFigure with a new Transition to listen to.
		* The name and ID are set to a new unique ID.
		* Initilaly no attributes are set to a value.
		* @see #CPNTransitionFigure(Transition)
		*/
	public CPNTransitionFigure() { this(new Transition()); }

	/**
	 * Creates a new TransitionFigure with the provided Transition to listen to.
	 * Name and annotations of the transition are displayed.
	 * The transition is a green rectangle with the size equal to {@link #size size}
	 * Above the rectangle the name is displayed in bold.
	 * The display of the name can be switched off. The guard and action can be
	 * displayed as a symbol (default) or as the actual text.
	 *
	 *@param aTransition a {@link org.rakiura.cpn.Transition Transition} that is
	 *   to be displayed by this CPNTransitionFigure
	 */
	public CPNTransitionFigure(final Transition aTransition) {
		super (new Point(0, 0), new Point(DEFAULT_SIZE * 3, DEFAULT_SIZE));
		this.transition = aTransition;
		super.setID(this.transition.getID());
		setFillColor(Color.green);
		this.name = new CPNNameFigure (this);
		this.name.moveBy(-25, -25);
		this.guard = new CPNAnnotationFigure (CPNAnnotationFigure.TRANSITION_GUARD, this);
		this.guard.moveBy (10, -8);
		this.action = new CPNAnnotationFigure (CPNAnnotationFigure.ACTION, this);
		this.action.moveBy (-10, -8);
		this.transition.addTransitionListener (this);
	}

	public void displayBox (Point s, Point e) {
		super.displayBox (s, new Point (s.x + 3*DEFAULT_SIZE, s.y + DEFAULT_SIZE));
	}

	public CPNNameFigure getNameFigure () {
		return this.name;
	}
	public void setNameFigure (CPNNameFigure newName) {
		if (this.name != null && this.name != newName) removeAnnotationFigure(this.name);
		this.name = newName;
	}

	public CPNAnnotationFigure getGuardFigure(){
		return this.guard;
	}
	public void setGuardFigure(final CPNAnnotationFigure newGuard) {
		if (this.guard != null &&
				this.guard != newGuard) removeAnnotationFigure(this.guard);
		this.guard = newGuard;
	}

	public CPNAnnotationFigure getActionFigure() {
		return this.action;
	}
	public void setActionFigure(final CPNAnnotationFigure newAction) {
		if (this.action != null &&
				this.action != newAction) removeAnnotationFigure(this.action);
		this.action = newAction;
	}

	/**
	 * Returns the transition that is represented by this figure.
	 * WARNING: The actual byte code of the transition may not be equal to the textual
	 * representation of the annotations.
	 * @return a {@link org.rakiura.cpn.Transition Transition} represented by this figure.
	 */
	public Transition getTransition() {
		return (Transition) getNetElement ();
	}

	public NetElement getNetElement () {
		return this.transition;
	}

	public void setNetElement(final NetElement aNetElement) {
		this.transition.removeTransitionListener (this);
		this.transition = (Transition) aNetElement;
		setID (this.transition.getID ());
		this.transition.addTransitionListener(this);
	}

	public void setID(final String newID) {
		super.setID(newID);
		this.name.setParentFigureID(getID());
		this.guard.setParentFigureID(getID());
		this.action.setParentFigureID(getID());
	}


	/**
	 * Event notification. This method throws runtime exception.
	 * @param anEvent a <code>TransitionEvent</code> value
	 */
	public void notify(final TransitionEvent anEvent) {
		throw new RuntimeException(
			"Unknown event type: " + anEvent.getClass() + " " + anEvent);
	}

	/**
	 * Event notification. This method makes the transition orange.
	 * @param anEvent a <code>TransitionStartedEvent</code> value
	 */
	public void notify(final TransitionStartedEvent anEvent) {
		this.oldFireColor = getFillColor();
		setFillColor(Color.orange);
		changed();
		try {
			if (DELAY > 0) Thread.sleep(DELAY);
		} catch (InterruptedException e) {/*ignore*/}
	}

	/**
	 * Event notification. This method turns the transition into its previous
	 * colour again.
	 * @param anEvent a <code>TransitionFinishedEvent</code> value
	 */
	public void notify(final TransitionFinishedEvent anEvent) {
		this.setFillColor(this.oldFireColor);
		changed();
	}

	/**
	 * Event notification. This method makes the transition white if it is enabled,
	 * or back to the previous color otherwise.
	 * @param anEvent a <code>TransitionStateChangedEvent</code> value
	 */
	public void notify(final TransitionStateChangedEvent anEvent) {
		if (anEvent.getTransition().isEnabled()) {
			this.oldStateColor = getFillColor();
			setFillColor(Color.white);
		}	else setFillColor(this.oldStateColor);
		changed();
	}

	/**
	 * Clones this getTransition(). Clone has same name, but different ID
	 * @return clone of this getTransition().
	 */
	public Object clone () {
		final CPNTransitionFigure clone = (CPNTransitionFigure) super.clone();
		clone.setNetElement(new Transition());
		clone.name = new CPNNameFigure (clone);
		clone.name.moveBy(-25,-25);
		clone.guard = new CPNAnnotationFigure (CPNAnnotationFigure.TRANSITION_GUARD, clone);
		clone.guard.moveBy(10,-8);
		clone.action = new CPNAnnotationFigure (CPNAnnotationFigure.ACTION, clone);
		clone.action.moveBy (-10,-8);
		return clone;
	}


	/**
	 * Inspects the figure. If alternate is false, it will show/hide the
	 * annotations.
	 * @param aView the DrawingView of this figure
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is done, false if no figure could be inspected.
	 */
	public boolean inspect(DrawingView aView, boolean alternate) {
		if (alternate) {
			//reset everything
			resetAnnotationLocation(this.name, -25, -25);
			resetAnnotationLocation(this.guard, 10, -8);
			resetAnnotationLocation(this.action, -10, -8);
			this.guard.updateDisplay();
			this.action.updateDisplay();
			if (this.transition.isEnabled()) setFillColor(java.awt.Color.white);
			else setFillColor(java.awt.Color.green);
			changed();
			return true;
		}
		switch (getInspectStatus()) {
			case 0:
				this.guard.setVisible(true);
				this.guard.updateDisplay();
				this.action.setVisible(false);
				setInspectStatus (1);
				break;
			case 1:
				this.guard.setVisible(false);
				this.action.setVisible(true);
				this.action.updateDisplay();
				setInspectStatus (2);
				break;
			case 2:
				this.guard.setVisible(false);
				this.action.setVisible(false);
				setInspectStatus (3);
				break;
			case 3:
				this.guard.setVisible(true);
				this.action.setVisible(true);
				this.guard.updateDisplay();
				this.action.updateDisplay();
				setInspectStatus (0);
				break;
		}
		return true;
	}


	/**
	 * Releases the figures resources. This method is usually called after it has
	 * permanently been removed from a drawing. It stops listening to its
	 * getTransition().
	 */
	public void release() {
		super.release();
		if (getTransition() != null)
			getTransition().removeTransitionListener(this);
	}

	public int getInspectStatus() {
		return this.inspectStatus;
	}

	public void setInspectStatus(int aStatus) {
		this.inspectStatus = aStatus;
	}

	public String toString() {
		return "" + this.transition + " ID:  " + getID();
	}

	/**
	 * Removes one of the annotation figures, both from this figure and from the drawing
	 * @param child the childfigure / annotationfigure
	 */
	private void removeAnnotationFigure(ChildFigure child) {
		removeChild(child);
		child.setParent(null);
		FigureChangeListener fcl = child.listener();
		if (fcl != null) {
			fcl.figureRequestRemove(new FigureChangeEvent(child, null));
		}
	}

	private void resetAnnotationLocation(final TextFigure t, final int basicOffsetX,
																			 final int basicOffsetY) {
		Point thisLoc = this.displayBox().getLocation();
		Point textLoc = t.displayBox().getLocation();
		Point newLoc = new Point();
		newLoc.x = thisLoc.x + basicOffsetX + 3*(CPNTransitionFigure.DEFAULT_SIZE/2);
		newLoc.y = thisLoc.y + basicOffsetY + CPNTransitionFigure.DEFAULT_SIZE/2;
		t.moveBy( newLoc.x - textLoc.x, newLoc.y - textLoc.y);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		this.transition.removeTransitionListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		if (this.transition != null) this.transition.addTransitionListener(this);
	}

	public void drawFrame(Graphics g) {
		super.drawFrame(g);
		if (this.transition == null) return;
		Rectangle r = displayBox();
		String type = this.transition.getTypeText();
		if (type.startsWith(Transition.ROUTING) ) {
			String spec = this.transition.getSpecificationText();
			if (spec.startsWith(Transition.ANDJOIN)) {
				g.drawLine(r.x+10, r.y, r.x+10, r.y+r.height-1);
				g.drawLine(r.x, r.y, r.x+10, r.y+r.height/2);
				g.drawLine(r.x+10, r.y+(r.height-1)/2, r.x, r.y+r.height-1);
			} else if (spec.startsWith(Transition.ANDSPLIT)) {
				g.drawLine(r.x+r.width-10, r.y, r.x+r.width-10, r.y+r.height-1);
				g.drawLine(r.x+r.width-1, r.y, r.x+r.width-10, r.y+(r.height-1)/2);
				g.drawLine(r.x+r.width-10, r.y+(r.height)/2, r.x+r.width-1, r.y+r.height-1);
			} else if (spec.startsWith(Transition.ORSPLIT)) {
				g.drawLine(r.x+r.width-10, r.y, r.x+r.width-10, r.y+r.height-1);
				g.drawLine(r.x+r.width-10, r.y, r.x+r.width-1, r.y+(r.height-1)/2);
				g.drawLine(r.x+r.width-1, r.y+r.height/2, r.x+r.width-10, r.y+r.height-1);
			} else if (spec.startsWith(Transition.ORJOIN)) {
				g.drawLine(r.x+10, r.y, r.x+10, r.y+r.height-1);
				g.drawLine(r.x+10, r.y, r.x, r.y+(r.height)/2);
				g.drawLine(r.x, r.y+(r.height-1)/2, r.x+10, r.y+r.height-1);
			} else if (spec.startsWith(Transition.ANDORSPLIT)) {
				g.drawLine(r.x+r.width-10, r.y, r.x+r.width-10, r.y+r.height-1);
				g.drawLine(r.x+r.width-10, r.y+r.height/3, r.x+r.width-1, r.y+(r.height/3));
				g.drawLine(r.x+r.width-10, r.y+2*r.height/3, r.x+r.width-1, r.y+(2*r.height/3));
			}
		} else if (type.startsWith(Transition.TASK) ) {
			g.drawLine(r.x, r.y+r.height-3, r.x+r.width-1, r.y+r.height-3);
		}
	}
}//////////////////EOF/////////////