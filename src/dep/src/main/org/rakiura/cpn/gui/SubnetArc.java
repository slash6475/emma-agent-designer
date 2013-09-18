// This is copyrighted source file, part of Rakiura JRio package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JRio package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.figure.ArrowTip;
import org.rakiura.draw.figure.LineConnection;
import org.rakiura.draw.figure.PolyLineFigure;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.rakiura.draw.figure.CompositeFigure;

/**
 * Title: Fake Arc to shadow real arc in super net
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.10 $
 *@since 3.0
 */
public class SubnetArc extends LineConnection {

	static final long serialVersionUID = -5613250809604810974L;

	private CPNArcFigure realArc;
	private CompositeFigure owner;
	private boolean allowRemove = false;
	private String realArcID;

	/**
	 * Constructor to use on load layout & serialization
	 */
	public SubnetArc() {
		this.setAttribute("ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_END));
		this.setEndDecoration(new ArrowTip(0.40, 15, 12, false)); //angle, outerradius, innerradius, filled
	}

	/**
	 * Constructs the fake arc that shadows the real arc in the subnetdrawing.
	 * @param realArc
	 * @param owner
	 */
	public SubnetArc(final CPNArcFigure realArc, final CompositeFigure owner) {
		this.realArc = realArc;
		this.realArcID = realArc.getID();
		this.setAttribute("ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_END));
		this.setEndDecoration(new ArrowTip(0.40, 15, 12, false)); //angle, outerradius, innerradius, filled
		realArc.addFigureChangeListener(this);
		this.owner = owner; //would be nice to use 'container' for this, but the default 'add/remove to container' methods prescribe that a figure can have more than one container, which is something we don't want.
	}

	/**
	 * Tests whether two figures can be connected.
	 */
	public boolean canConnect(final Figure start, final Figure end) {
		return false; //so figure can not connect to other figures and stays connected to original figures?
	}

	public String toString () {
		if (this.realArc != null)	return this.realArc.toString();
		return "Arc: null";
	}

	/**
	 * Announces the removal of a figure this figure is listening to.
	 * Removes this arc as well and if necessary the real arc.
	 * @param e the event
	 */
	public void figureRemoved(FigureChangeEvent e) {
		this.allowRemove = true;
		if (e.getFigure() == this.realArc) { //arc is removed or has reconnected to other place
			//remove ourselves
			super.figureRemoved(e);
		} else { //figure (!=arc) to which we are connected is removed, so remove arc, which will call this method again
			if (this.realArc.listener() != null) {
				this.realArc.listener().figureRequestRemove(new FigureChangeEvent(this.realArc));
			}
		}
	}

	/**
	 * Announces the change of a figure this figure is listening to.
	 * Arc listens to real arc. if realarc is not connected to subnetplace, then remove ourselves!
	 * @param e the event
	 */
	public void figureChanged(FigureChangeEvent e) {
		super.figureChanged(e);
		if (e.getFigure() == this.realArc) {
			this.realArcID = e.getFigure().getID(); //id change may have caused the event
			Figure start = this.realArc.startFigure();
			Figure end = this.realArc.endFigure();
			if (start != null && end != null && !(start instanceof SubnetPlace || end instanceof SubnetPlace)) {
				//reconnected, but not to SubnetPlace, so we don't need a subnetarc anymore! remove ourselves!
				this.allowRemove = true;
				this.realArc.removeFigureChangeListener(this);
				listener().figureRequestRemove(new FigureChangeEvent(this));
			}
		}
	}

	/**
	 * is called wen we are removed from the drawing. But this subnet arc is only
	 * allowed to be removed if the real Arc is removed. So this method will
	 * make sure that the arc will reconnect to the subfigure so that a new fake
	 * arc is created
	 */
	public void release() {
		//don't remove if not requested/caused by real arc
		if (this.allowRemove == false) {
			this.owner.add(this);
		} else {
			super.release();
			if (this.realArc != null) this.realArc.removeFigureChangeListener(this);
		}
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		if (this.realArc != null)
			this.realArc.addFigureChangeListener(this);
	}

	public void rewireConnections (final CompositeFigure aContainer) {
		super.rewireConnections(aContainer);
		this.owner = aContainer;
		Figure f = this.start().owner();
		if (f instanceof CPNSubNetFigure) {
			this.realArc = (CPNArcFigure) ((CPNSubNetFigure)f).getSubNetDrawing().getFigure(this.realArcID);
		} else {
			f = this.end().owner();
			if (f instanceof CPNSubNetFigure) {
				this.realArc = (CPNArcFigure) ((CPNSubNetFigure)f).getSubNetDrawing().getFigure(this.realArcID);
			}
		}
		this.realArc.addFigureChangeListener(this);
	}

	public void setRealArcID(String ID) {
		this.realArcID = ID;
	}

	public String getRealArcID() {
		return this.realArcID;
	}
}