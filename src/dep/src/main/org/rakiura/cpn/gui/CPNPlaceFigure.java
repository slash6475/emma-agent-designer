// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.Color;
import java.awt.Point;

import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.event.PlaceEvent;
import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TokensAddedEvent;
import org.rakiura.cpn.event.TokensRemovedEvent;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.figure.EllipseFigure;
import org.rakiura.draw.figure.TextFigure;

/**
 * Represents graphically a CPN place.
 * Figure representing a place in a CPN.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.62 $
 *@since 3.0
 */
public class CPNPlaceFigure extends EllipseFigure
	implements CPNAbstractFigure, PlaceListener {


	private static final long serialVersionUID = 3257570607121313848L;

	/** A child figure that displays the name of this Place. */
	private CPNNameFigure name;

	/** Child figure for number of tokens. */
	private CPNTokenCounterFigure count;

	/**
	 * Place that is represented by this figure.  */
	private transient Place place;

	private transient String tokensText="";

	/** Index in what stage of inspection we are. */
	private int inspectStatus = 0;

	/** The delay of changing color if tokens are added or removed. */
	public static int DELAY = 1000;

	/** The DEFAULT_SIZE of the circle representing the place. */
	final private static int DEFAULT_SIZE = 30;


	/**
	 * Creates new CPNPlaceFigure with a default size and colour.
	 */
	public CPNPlaceFigure() { this (new Place());	}


	/**
	 * Creates a new PlaceFigure with the provided Place to listen to.
	 * Name and annotations of the place are displayed.
	 * The place is a yellow circle with the DEFAULT_SIZE equal to 30pt.
	 * Above the circle the name is displayed in bold. The number of tokens is
	 * displayed in the circle. The contents of tokens is displayed on the right
	 * side of the circle. The display of the name can be switched off.
	 * @see #setNameVisible(boolean)
	 * @param aPlace a {@link org.rakiura.cpn.Place Place} that is to be displayed by this CPNPlaceFigure
	 */
	public CPNPlaceFigure (final Place aPlace) {
		super(new Point(0, 0), new Point(DEFAULT_SIZE, DEFAULT_SIZE));
		setFillColor(java.awt.Color.yellow);
		this.place = aPlace;
		super.setID(aPlace.getID());
		this.name = new CPNNameFigure (this);
		this.name.moveBy(-16, -30);
		updateTokensText();
		this.count = new CPNTokenCounterFigure (this);
		this.count.moveBy (-10, -8);
		aPlace.addPlaceListener (this);
	}

	public void displayBox (Point s, Point e) {
		// we need to do that to enable one-click-no-drag creation
		super.displayBox (s, new Point (s.x + DEFAULT_SIZE, s.y + DEFAULT_SIZE));
	}

	/**
	 * Returns the place that is represented by this figure.
	 * @return a {@link org.rakiura.cpn.Place Place}
	 * @see #getNetElement
	 */
	public Place getPlace() {
		return (Place) getNetElement();
	}

	/**
	 * Event notification. This method makes the circle ORANGE
	 * for a moment, and updates the tokens count.
	 * @param anEvent a <code>PlaceEvent</code> value
	 */
	public void notify (final PlaceEvent anEvent) {
		this.count.setTokenNumber("" + anEvent.getPlace().getTokens().size());

		updateTokensText();
		Color oldColor = this.getFillColor();
		setFillColor(Color.ORANGE);
		changed();

		try {
			if (DELAY > 0) Thread.sleep(DELAY);
		} catch (InterruptedException e) {/*ignore*/}

		setFillColor(oldColor);
		changed();
	}

	/**
	 * Notification mechanism.
	 * This method will invoke {@link #notify(PlaceEvent) notify(PlaceEvent)}.
	 * @param anEvent a TokensAddedEvent.
	 */
	public void notify (final TokensAddedEvent anEvent) {
		notify ((PlaceEvent) anEvent);
	}

	/**
	 * Notification mechanism.
	 * This method will invoke {@link #notify(PlaceEvent) notify(PlaceEvent)}.
	 * @param anEvent a TokensRemovedEvent.
	 */
	public void notify (final TokensRemovedEvent anEvent) {
		notify ((PlaceEvent) anEvent);
	}

	/**
	 * Clones this object. Clone has same name, but different ID
	 * @return A clone of this CPNPlaceFigure.
	 */
	public Object clone () {
		final CPNPlaceFigure clone = (CPNPlaceFigure) super.clone();
		clone.setNetElement(new Place());
		clone.name = new CPNNameFigure (clone);
		clone.name.moveBy(-16, -30);
		clone.count = new CPNTokenCounterFigure (clone);
		clone.count.moveBy (-10, -8);
		return clone;
	}

	/**
	 * Inspects the figure. If alternate is false, it will show/hide the
	 * tokenscount and name respectively. If alternate is true, it will reset its display.
	 * @param aView the DrawingView of this figure
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is done, false if no figure could be inspected.
	 */
	public boolean inspect (DrawingView aView, boolean alternate) {
		if (alternate) {
			//reset everything
			resetAnnotationLocation(this.name, -16, -30);
			resetAnnotationLocation(this.count, -10, -8);
			int nrOfTokens = this.place.getTokens().size();
			if (nrOfTokens > 0) {
				this.count.setTokenNumber("" + nrOfTokens);
			} else this.count.setTokenNumber("0");
			updateTokensText();
		} else {
			switch (getInspectStatus()) {
				case 0 :
					getNameFigure().setVisible(true);
					setInspectStatus(1);
					break;
				case 1 :
					getNameFigure().setVisible(false);
					setInspectStatus(0);
					break;
			}
		}
		return true;
	}

	/**
	 * Updates the text that shows the contents of the tokens.
	 * Should only be called if <code>place != null</code>,
	 * otherwise null pointer exception will be thrown.
	 */
	protected void updateTokensText() {
		final Multiset m = getPlace().getTokens();
		final Object[] o = m.toArray();
		String text = "";
		for (int i = 0; i < o.length; i++) {
			text += o[i] + "\n";
		}
		this.tokensText = text;
		if (this.count != null) this.count.updateTokensText();
	}

	/**
	 * Releases the figures resources. This method is usually called after it has
	 * permanently been removed from a drawing. It stops listening to its
	 * place.
	 */
	public void release() {
		super.release();
		getPlace().removePlaceListener (this);
	}

	public CPNNameFigure getNameFigure () {
		return this.name;
	}
	public void setNameFigure (CPNNameFigure newName) {
		if (this.name != null && this.name != newName) removeAnnotationFigure(this.name);
		this.name = newName;
	}

	public void setNameVisible(boolean visible) {
		this.name.setVisible(visible);
	}

	public CPNTokenCounterFigure getTokenCounterFigure () {
		return this.count;
	}

	public String getTokensText() {
		return this.tokensText;
	}

	public void setTokenCounterFigure (CPNTokenCounterFigure newCounter) {
		if (this.count != null && this.count != newCounter) removeAnnotationFigure(this.count);
		this.count = newCounter;
	}

	public NetElement getNetElement() {
		return this.place;
	}

	public void setNetElement(final NetElement aNetElement) {
		this.place.removePlaceListener (this);
		this.place = (Place) aNetElement;
		setID (this.place.getID ());
		this.place.addPlaceListener(this);
	}

	public void setID(final String newID) {
		super.setID(newID);
		this.count.setParentFigureID(newID);
		this.name.setParentFigureID(newID);
	}

	public int getInspectStatus() {
		return this.inspectStatus;
	}

	public void setInspectStatus(int aStatus) {
		this.inspectStatus = aStatus;
	}

	public String toString() {
		return "" + this.place + " ID:  " + getID();
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
		newLoc.x = thisLoc.x + basicOffsetX + CPNPlaceFigure.DEFAULT_SIZE/2;
		newLoc.y = thisLoc.y + basicOffsetY + CPNPlaceFigure.DEFAULT_SIZE/2;
		t.moveBy( newLoc.x - textLoc.x, newLoc.y - textLoc.y);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		this.place.removePlaceListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		if (this.place != null) {
			this.place.addPlaceListener(this);
			this.count.setTokenNumber("" + this.place.getTokens().size());
			updateTokensText();
		}
	}

} ////////////////////EOF////////////////