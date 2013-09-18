// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Point;

import org.rakiura.cpn.BasicNet;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetElement;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.figure.RoundRectangleFigure;
import org.rakiura.draw.figure.TextFigure;


/**
 * Figure representing a CPN Net.
 *
 *@author Martin Fleurke<br>
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.38 $
 *@since 3.0
 */
public final class CPNNetFigure extends RoundRectangleFigure
	implements CPNAbstractNetFigure {


	private static final long serialVersionUID = 3689349935678371127L;


	/** The size of the Net figure, the height/width of the graphical rectangle.*/
	final private static int DEFAULT_SIZE = 50;


	/** List of all layout files. */
	protected String[] layouts;

	/** A child figure that displays the name of this Place. */
	private CPNNameFigure name;

	/** A subfigure that displays the import code text of this Net.*/
	protected CPNAnnotationFigure imports;

	/** A subfigure that displays the declaration code text of this Net.*/
	protected CPNAnnotationFigure declaration;

	/** A subfigure that displays the implements code text of this Net.*/
	protected CPNAnnotationFigure implement;

	/** Index in what stage of inspection we are. */
	private int inspectStatus = 0;

	/**
	 * Net that is represented by this figure.  */
	private transient Net net;


	/**
	* Creates a new NetFigure with a new empty BasicNet.
	* The name and ID are set to a new unique ID.
	* @see #CPNNetFigure(Net)
	*/
	public CPNNetFigure() {
		this(new BasicNet());
		this.net.setTypeText("hlnet");
	}

	/**
	 * Creates a new NetFigure from the provided Net.
	 * Name and annotations of the Net are displayed.
	 * The display of the name can be switched off. The annotations can be
	 * displayed as symbols (default) or as the actual text.
	 *
	 * @param aNet a {@link org.rakiura.cpn.Net Net} that is to be displayed by
	 * this CPNAbstractNetFigure
	 * @see #setNameVisible
	 */
	public CPNNetFigure(final Net aNet) {
		super (new Point(0, 0), new Point(DEFAULT_SIZE , DEFAULT_SIZE));
		this.net = aNet;
		super.setID(this.net.getID());
		setFillColor(java.awt.Color.white);
		this.imports = new CPNAnnotationFigure(CPNAnnotationFigure.IMPORT, this);
		this.imports.moveBy (-13, -26);
		this.declaration = new CPNAnnotationFigure(CPNAnnotationFigure.DECLARATION, this);
		this.implement = new CPNAnnotationFigure(CPNAnnotationFigure.IMPLEMENTS, this);
		this.implement.moveBy (13, -10);
		this.name = new CPNNameFigure(this);
		this.name.moveBy (-28,-38);
	}

	public CPNAnnotationFigure getImportsFigure() { return this.imports; }
	public CPNAnnotationFigure getDeclarationFigure() {	return this.declaration; }
	public CPNAnnotationFigure getImplementFigure() { return this.implement; }
	public CPNNameFigure getNameFigure () { return this.name; }

	public void setImportsFigure (CPNAnnotationFigure aFig) {
		if (this.imports != null) removeAnnotationFigure(this.imports);
		this.imports = aFig;
	}

	public void setDeclarationFigure (final CPNAnnotationFigure aFig) {
		if (this.declaration != null &&
				this.declaration != aFig) removeAnnotationFigure(this.declaration);
		this.declaration = aFig;
	}

	public void setImplementFigure (final CPNAnnotationFigure aFig) {
		if (this.implement != null &&
				this.implement != aFig) removeAnnotationFigure(this.implement);
		this.implement = aFig;
	}

	public void setNameFigure (final CPNNameFigure aFig) {
		if (this.name != null &&
				this.name != aFig) removeAnnotationFigure(this.name);
		this.name = aFig;
	}

	/**
	 * Inspects the figure. If alternate is false, it will show the actual
	 * annotations respectively as text
	 * @param aView the DrawingView of this figure
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is called, false if no figure could be inspected.
	 */
	public boolean inspect (DrawingView aView, boolean alternate) {
		if (alternate) {
			resetAnnotationLocation(this.name, -28, -38);
			resetAnnotationLocation(this.implement, 13, -10);
			resetAnnotationLocation(this.declaration, 0, 0);
			resetAnnotationLocation(this.imports, -13, -26);
			this.implement.updateDisplay();
			this.imports.updateDisplay();
			this.declaration.updateDisplay();
			return true;
		}
		switch (getInspectStatus()) {
			case 0:
				this.implement.setVisible(false);
				this.declaration.setVisible(false);
				this.imports.setVisible(false);
				setInspectStatus (1);
				break;
			case 1:
				this.implement.setVisible(false);
				this.declaration.setVisible(true);
				this.declaration.updateDisplay();
				this.imports.setVisible(false);
				setInspectStatus (2);
				break;
			case 2:
				this.implement.setVisible(false);
				this.declaration.setVisible(false);
				this.imports.setVisible(true);
				this.imports.updateDisplay();
				setInspectStatus (3);
				break;
			case 3:
				this.implement.setVisible(true);
				this.implement.updateDisplay();
				this.declaration.setVisible(false);
				this.imports.setVisible(false);
				this.setInspectStatus (4);
				break;
			case 4:
				this.implement.setVisible(false);
				this.declaration.setVisible(false);
				this.imports.setVisible(false);
				setInspectStatus (5);
				break;
			case 5:
				this.implement.setVisible(true);
				this.declaration.setVisible(true);
				this.imports.setVisible(true);
				this.implement.updateDisplay();
				this.declaration.updateDisplay();
				this.imports.updateDisplay();
				setInspectStatus (0);
				break;
		}
			return true;
	}

	public void setNameVisible(boolean visible) {
		this.name.setVisible(visible);
	}

	public NetElement getNetElement() {
		return this.net;
	}

	public Net getNet () {
		return (Net) getNetElement ();
	}

	/**
	 * Sets the reference to the net of this netfigure. Does NOT update the
	 * netfigure according to the new net.
	 * @param aNetElement the net that is represented by this net figure
	 */
	public void setNetElement(final NetElement aNetElement) {
		this.net = (Net) aNetElement;
		setID (this.net.getID ());
	}

	public int getInspectStatus() {
		return this.inspectStatus;
	}

	public void setInspectStatus(int aStatus) {
		this.inspectStatus = aStatus;
	}

	public void setID(final String newID) {
		super.setID(newID);
		this.declaration.setParentFigureID(newID);
		this.implement.setParentFigureID(newID);
		this.imports.setParentFigureID(newID);
		this.name.setParentFigureID(newID);
	}

	/**
	 * Clones this net. Clone has same name, but different ID
	 * @return clone of this net.
	 */
	public Object clone() {
		final CPNNetFigure clone = (CPNNetFigure) super.clone();
		clone.removeChildren();  //?not necessary?
		clone.imports = new CPNAnnotationFigure(CPNAnnotationFigure.IMPORT, clone);
		clone.declaration =  new CPNAnnotationFigure(CPNAnnotationFigure.DECLARATION, clone);
		clone.implement = new CPNAnnotationFigure(CPNAnnotationFigure.IMPLEMENTS, clone);
		clone.name = new CPNNameFigure (clone);
		clone.implement.moveBy (13, -6);
		clone.imports.moveBy (-13, -6);
		clone.name.moveBy (-28, -38);
		return clone;
	}

	public String toString() {
		return "(" + this.net + " ID:  " + getID() + ")";
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
			newLoc.x = thisLoc.x + basicOffsetX + CPNNetFigure.DEFAULT_SIZE/2;
			newLoc.y = thisLoc.y + basicOffsetY + CPNNetFigure.DEFAULT_SIZE/2;
			t.moveBy( newLoc.x - textLoc.x, newLoc.y - textLoc.y);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		//nothing to dereference
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		//nothing to dereference
	}
} ///EOF/////////