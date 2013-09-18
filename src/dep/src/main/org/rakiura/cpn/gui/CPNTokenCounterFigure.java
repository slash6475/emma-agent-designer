//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Color;
import java.awt.Font;

import org.rakiura.draw.Figure;
import org.rakiura.draw.ParentFigure;
import org.rakiura.draw.figure.CompositeFigure;
import org.rakiura.draw.figure.TextFigure;
import org.rakiura.draw.DrawingView;

/**
 * Represents counter for tokens with place figure as parent.
 *
 * <br><br>
 * CPNTokenCounterFigure.java created on 6/07/2003 16:09:02<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
public class CPNTokenCounterFigure extends TextFigure implements CPNDecoration {

	private static final long serialVersionUID = 3544675079087994679L;

	/** Parent ID. */
	private String parentFigureID;

	/** The actual parent handle. */
	private CPNPlaceFigure parent;

	public CPNTokenCounterFigure() {/*public constructor*/}

	/** The actual annotation text, not a symbol. */
	private transient String tokenText = "";

	private transient String nrOfTokens = "0";

	/** Shows in what stage of inspection we are */
	private int inspectStatus = 0;

	/**
	 * Creates new child text figure for the name of a given CPN net figure.
	 * @param aFig the placeFigure that can have tokens
	 */
	public CPNTokenCounterFigure (CPNPlaceFigure aFig) {
		setReadOnly (true);
		this.parent = aFig;
		this.parentFigureID = aFig.getID ();
		TextFigure.setCurrentFontStyle(Font.BOLD);
		setFont(TextFigure.createCurrentFont());
		setTextColor(Color.blue);
		updateTokensText();
		setText (""+aFig.getPlace().getTokens().size());
		updateDisplay();
		TextFigure.setCurrentFontStyle(Font.PLAIN);
		setParent (aFig);
	}

	/**
	 * Should the tokens text be shown or the number of tokens?
	 * @param show if true show text, else show number.
	 */@SuppressWarnings("unqualified-field-access")
	
	public void showTokensText(boolean show) {
		if (show) {
			updateTokensText ();
			this.setText(this.tokenText);
		} else {
			TextFigure.setCurrentFontStyle(Font.BOLD);
			if (parent != null) this.setText(""+this.parent.getPlace().getTokens().size());
			TextFigure.setCurrentFontStyle(Font.PLAIN);
			updateDisplay();
		}
	}

	public boolean canBeParent (final ParentFigure aFig) {
		return this.parent == aFig;
	}

	public String getParentFigureID () { return this.parentFigureID; }
	public void setParentFigureID (String aID) { this.parentFigureID = aID; }

	public boolean setParent (ParentFigure newParent) {
		this.parent = (CPNPlaceFigure) newParent;
		return super.setParent(newParent);
	}

	public void reconnectToParent (CompositeFigure d) {
		if (this.parent != null) return;
		if (this.parentFigureID == null) return;
		Figure f = d.getFigure(this.parentFigureID);
		if (f == null || !(f instanceof CPNPlaceFigure)) return;
		setParent((CPNPlaceFigure) f);
		((CPNPlaceFigure) f).setTokenCounterFigure (this);
	}


	public Object clone() {
		final CPNTokenCounterFigure clone = (CPNTokenCounterFigure) super.clone();
		clone.parent = null;
		clone.parentFigureID = null;
		return clone;
	}

	public void setTokenNumber(String number) {
		this.nrOfTokens = number;
		if (this.inspectStatus == 0) setText(this.nrOfTokens);
	}

	public void updateTokensText () {
		if (this.parent != null) {
			this.tokenText = this.parent.getTokensText();
		}
		if (this.inspectStatus == 1) setText(this.tokenText);
		updateDisplay();
	}

	/**
	 * changes visibility of figure if necessary.
	 */
	private void updateDisplay() {
		boolean shouldBeVisible = !this.tokenText.trim().equals("");
		if ( isVisible() ^ shouldBeVisible ) {
			setVisible(shouldBeVisible); //causes figurechanged!
		}
	}

	/**
	 * Inspects the figure. If alternate is false, it will show the actual
	 * annotations respectively as text. If alternate is true, you can edit the text.
	 * @param view the DrawingView of this figure
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is done, false if no figure could be inspected.
	 */
	public boolean inspect(DrawingView view, boolean alternate) {
		if (alternate) {
			return false;
		}
		if (this.inspectStatus == 0) {
			showTokensText(true);
			this.inspectStatus++;
		} else {
			showTokensText(false);
			this.inspectStatus = 0;
		}
		return true;
	}

	/**
	 * Returns the current inspect status for this figure.
	 * @return current inspect status.
	 */
	public int getInspectStatus() {
		return this.inspectStatus;
	}

	/**
	 * Sets the current inspect status to a given value.
	 * @param aStatus new value of current inspect status.
	 */
	public void setInspectStatus(int aStatus) {
		this.inspectStatus = aStatus;
	}
}
