//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Font;

import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.ParentFigure;
import org.rakiura.draw.figure.CompositeFigure;
import org.rakiura.draw.figure.TextFigure;

/**
 * Represents Name decoration for CPN figures..
 *
 * <br><br>
 * CPNNameFigure.java created on 6/07/2003 15:42:18<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
public class CPNNameFigure extends TextFigure implements CPNDecoration {

	private static final long serialVersionUID = 3258415036296082482L;

	/** Parent ID. */
	private String parentFigureID;

	/** The actual parent handle. */
	private CPNAbstractFigure parent;

	public CPNNameFigure () {
		addFigureChangeListener (this);
	}

	/**
	 * Creates new child text figure for the name of a given CPN net figure.
	 * @param aFig
	 */
	public CPNNameFigure (CPNAbstractFigure aFig) {
		setParent (aFig);
		TextFigure.setCurrentFontStyle(Font.BOLD);
		setFont(TextFigure.createCurrentFont());
		setText (aFig.getNetElement().getName());
		setVisible(true);
		TextFigure.setCurrentFontStyle(Font.PLAIN);
		addFigureChangeListener (this);
	}

	public boolean canBeParent (final ParentFigure aFig) {
		return this.parent == aFig;
	}

	public String getParentFigureID () { return this.parentFigureID; }
	public void setParentFigureID (String aID) {this.parentFigureID = aID;}

	public boolean setParent (ParentFigure newParent) {
		this.parent = (CPNAbstractFigure) newParent;
		if (this.parent != null) this.parentFigureID = this.parent.getID();
		else this.parentFigureID = null;
		return super.setParent(newParent);
	}

	/**
	 * reconnects to parent if parent == null && parentID &#33;= null
	 * @param d the drawing to which the parent belongs.
	 */
	public void reconnectToParent (CompositeFigure d) {
		if (this.parent != null) return;//only set parent if there is no parent set, otherwise te location gets screwed up.
		if (this.parentFigureID == null) return;
		final Figure f = d.getFigure(this.parentFigureID);
		if (f == null || !(f instanceof CPNAbstractFigure) ) return;
		setParent((CPNAbstractFigure) f);
		((CPNAbstractFigure) f).setNameFigure (this);
	}

	public void figureChanged(FigureChangeEvent e) {
		if (e.getFigure() != this) { //parent must have changed
			super.figureChanged(e);
		} else if (this.parent != null ) { //i have changed myself
			this.parent.getNetElement().setName (getText ());
		}
	}

	public Object clone() {
		final CPNNameFigure clone = (CPNNameFigure) super.clone();
		clone.parent = null;
		clone.parentFigureID = null;
		clone.addFigureChangeListener(clone);
		return clone;
	}

} ///////////////EOF////////////////