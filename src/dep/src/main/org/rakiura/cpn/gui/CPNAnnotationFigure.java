// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.Color;

import org.rakiura.cpn.Arc;
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.Transition;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.ParentFigure;
import org.rakiura.draw.figure.CompositeFigure;
import org.rakiura.draw.figure.TextFigure;
import org.rakiura.draw.DrawingView;

/**
 * CPN Annotation Figure.
 * Figure to display annotation by a symbol or its annotation text.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.19 $
 *@since 3.0
 */
public class CPNAnnotationFigure extends TextFigure implements CPNDecoration {

	private static final long serialVersionUID = 3257565088054718520L;
	
	/* All static constants. */
	public final static int RESERVED = 0;
	public final static int IMPORT = 1;
	public final static int DECLARATION = 2;
	public final static int IMPLEMENTS = 3;
	public final static int TRANSITION_GUARD = 4;
	public final static int ACTION = 5;
	public final static int EXPRESSION = 6;
	public final static int ARC_GUARD = 7;

	private final static String[] annotationSymbols =
		{ "UNDEFINED", "I", "#", "~", "G", "A", "E", "G" };

	/** The actual annotation text, not a symbol. */
	private String annotationText = "";

	/** The type of this annotation. */
	private int annotationType;

	/** Parent ID. */
	private String parentFigureID;

	/** The actual parent handle. */
	private CPNAbstractFigure parent;

	/** Flag if this annotation is in editing mode. */
	private boolean editingMode = false;

	/** Shows in what stage of inspection we are */
	private int inspectStatus = 0;

	public CPNAnnotationFigure(){
		addFigureChangeListener (this);
	}

	public CPNAnnotationFigure(int aType, CPNAbstractFigure aParent) {
		if (aParent == null) throw new RuntimeException();
		setParent (aParent);
		this.parentFigureID = aParent.getID();
		this.annotationType = aType;
		updateAnnotationText ();
		this.setText (annotationSymbols[this.annotationType]);
		setReadOnly (true);
		this.setTextColor(Color.RED);
		addFigureChangeListener (this);
	}

	/**
	 * Should the annotation text be shown or a symbol representing the annotation?
	 * @param show if true show text, else show symbol.
	 */
	public void showAnnotation(boolean show) {
		if (show) {
			setTextColor(Color.DARK_GRAY);
			setReadOnly(false);
			updateAnnotationText ();
			this.setText(this.annotationText);
			this.editingMode = true;
		} else {
			this.editingMode = false;
			setTextColor(Color.RED);
			this.setText(CPNAnnotationFigure.annotationSymbols[this.annotationType]);
			updateDisplay();
			setReadOnly(true);

		}
	}

	/**
	 * Sets the annotation text to the value of the annotation provided.
	 * @param annotation the new annotation text
	 */
	public void setAnnotation (String annotation) {
		this.annotationText = annotation;
		if (this.editingMode == true) {
			setText(annotation); //causes figurechanged if annotation is different from current text.
		}
		updateDisplay();
		if (this.parent == null) {// no parent, so no need for setting annotation for netelement.
			//is the case e.g. on initialization from layout.
			return;
		}

		switch (this.annotationType) {
			case DECLARATION:
					((Net) this.parent.getNetElement()).setDeclarationText(annotation);
					break;
			case IMPORT:
					((Net) this.parent.getNetElement()).setImportText(annotation);
					break;
			case IMPLEMENTS:
					((Net) this.parent.getNetElement()).setImplementsText(annotation);
					break;
			case ACTION:
					((Transition) this.parent.getNetElement()).setActionText(annotation);
					break;
			case TRANSITION_GUARD:
					((Transition) this.parent.getNetElement()).setGuardText(annotation);
					break;
			case EXPRESSION:
					if (this.parent.getNetElement() != null)
						((Arc) this.parent.getNetElement()).setExpressionText(annotation);
					break;
			case ARC_GUARD:
					if (this.parent.getNetElement() != null)
						((InputArc) this.parent.getNetElement()).setGuardText (annotation);
					break;
		}
	}

	public String getParentFigureID () { return this.parentFigureID; }
	public void setParentFigureID (String aID) { this.parentFigureID = aID; }
	public void setEditingMode(boolean editingMode) { this.editingMode = editingMode;}
	public boolean getEditingMode() { return this.editingMode; }

	public void reconnectToParent (CompositeFigure d) {
		if (this.parent != null) return;//only set parent if there is no parent set, otherwise te location gets screwed up.
		if (this.parentFigureID == null) return;
		Figure f = d.getFigure(this.parentFigureID);
		if (f == null || !(f instanceof CPNAbstractFigure)) {
			System.err.println("Reconnecting annotationfigure "+this);
			System.err.println("Reconnecting failed, no parent figure found or not of type CPNAbstractFigure!");
			System.err.print(" Tried: "+this.parentFigureID);
			System.err.println(" f = "+f);
			return;
		}
		if(!d.containsFigure(this)) d.add (this);  //annotation of arc may not be visible yet?
		setParent((CPNAbstractFigure) f);
		switch (this.annotationType) {
			case DECLARATION:
					((CPNAbstractNetFigure) this.parent).setDeclarationFigure (this);
					break;
			case IMPORT:
					((CPNAbstractNetFigure) this.parent).setImportsFigure (this);
					break;
			case IMPLEMENTS:
					((CPNAbstractNetFigure) this.parent).setImplementFigure (this);
					break;
			case ACTION:
					((CPNTransitionFigure) this.parent).setActionFigure(this);
					break;
			case TRANSITION_GUARD:
					((CPNTransitionFigure) this.parent).setGuardFigure(this);
					break;
			case EXPRESSION:
					((CPNArcFigure) this.parent).setExpressionFigure(this);
					break;
			case ARC_GUARD:
					((CPNArcFigure) this.parent).setGuardFigure(this);
					break;
		}
	}

	public boolean setParent (ParentFigure newParent) {
		this.parent = (CPNAbstractFigure) newParent;
		if (this.parent != null) this.parentFigureID = this.parent.getID();
		else this.parentFigureID = null;
		return super.setParent(newParent);
	}

	/**
	 * Returns a new string with the current annotation text
	 * @return the annotation text
	 */
	public String getAnnotation() {
		return this.annotationText;
	}

	public int getAnnotationType() {
		return this.annotationType;
	}

	public void setAnnotationType(int annotationType) {
		this.annotationType = annotationType;
	}

	public boolean canBeParent (final ParentFigure aFig) {
		return this.parent == aFig;
	}

	public void figureChanged(FigureChangeEvent e) {
		if (e.getFigure() != this) { //parent must have changed...
			super.figureChanged(e);
		} else if (this.editingMode) { //i have changed && editingmode is true, so the change in text should also chgange the annotation
			//of course we do not know if the text has changed, so if someone just wanted to set this figure to invisible, this event is raised as well and he/she fails because setAnnotation will set it to true again. :) Which is not that bad
			setAnnotation (getText ());
		}
	}

	public Object clone() {
		final CPNAnnotationFigure clone = (CPNAnnotationFigure) super.clone();
		clone.parent = null;
		clone.parentFigureID = null;
		clone.addFigureChangeListener(clone);
		return clone;
	}

	public void updateAnnotationText () {
		switch (this.annotationType) {
			case DECLARATION:
				if (this.parent != null && this.parent.getNetElement() != null)
					this.annotationText = ((Net) this.parent.getNetElement()).getDeclarationText();
				break;
			case IMPORT:
				if (this.parent != null && this.parent.getNetElement() != null)
					this.annotationText = ((Net) this.parent.getNetElement()).getImportText();
				break;
			case IMPLEMENTS:
				if (this.parent != null && this.parent.getNetElement() != null)
					this.annotationText = ((Net) this.parent.getNetElement()).getImplementsText();
				break;
			case ACTION:
				this.annotationText = ((Transition) this.parent.getNetElement()).getActionText();
				break;
			case TRANSITION_GUARD:
				this.annotationText = ((Transition) this.parent.getNetElement()).getGuardText();
				break;
			case EXPRESSION:
				if (this.parent != null) {
					final Arc arc = (Arc) this.parent.getNetElement();
					if (arc != null)
						this.annotationText = arc.getExpressionText();
				}
				break;
			case ARC_GUARD:
				if (this.parent != null) {
					final InputArc inarc = (InputArc) this.parent.getNetElement();
					if (inarc != null)
						this.annotationText = inarc.getGuardText();
				}
				break;
		}
		updateDisplay();
	}

	/**
	 * Inspects the figure. If alternate is false, it will show the actual
	 * annotations respectively as text. If alternate is true, you can edit the text.
	 * @param view the DrawingView of this figure
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is done, false if no figure could be inspected.
	 */
	@SuppressWarnings("unqualified-field-access")
	public boolean inspect(DrawingView view, boolean alternate) {
		if (!alternate) {
			showAnnotation(true);
			return super.inspect(view, false);
		}
		if (this.inspectStatus == 0) {
			showAnnotation(true);
			this.inspectStatus++;
		} else {
			showAnnotation(false);
			inspectStatus = 0;
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

	/**
	 * changes visibility of figure if necessary.
	 */
	public void updateDisplay() {
		boolean shouldBeVisible = !this.annotationText.trim().equals("");
		if ( isVisible() ^ shouldBeVisible ) {
			setVisible(shouldBeVisible); //causes figurechanged!
		}
	}

}////////////EOF//////////