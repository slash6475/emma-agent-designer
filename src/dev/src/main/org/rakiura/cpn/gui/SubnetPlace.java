package org.rakiura.cpn.gui;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.figure.CompositeFigure;
import org.rakiura.draw.figure.EllipseFigure;

/**
 * This is a fake Place to shadow a real place in a parent net.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.12 $
 *@since 3.0
 */
public class SubnetPlace extends EllipseFigure implements FigureChangeListener {

	static final long serialVersionUID = 8482790000499759021L;

	private static final int SIZE = 30;
	private HashSet realArcIDs = new HashSet();
	private HashSet realArcs = new HashSet();

	/**
	 * constructor for loadLayout & stuff
	 */
	public SubnetPlace() {
		/* default constructor */
	}

	/**
	 * A Place that shadows a real place figure outside a subnet.
	 * @param aPlaceFigureID the id of the place to shadow. Could be used to
	 * prevent creation of multiple SubnetPlaces that refer to the same real place.
	 * And it is used for the toString method.
	 */
	public SubnetPlace(String aPlaceFigureID) {
		setID(aPlaceFigureID);
		this.basicDisplayBox(new Point(0, 0), new Point(SIZE, SIZE));
		this.setFillColor(java.awt.Color.yellow);
		moveBy(NetViewer.random.nextInt(NetViewer.WIDTH - 70),
					 NetViewer.random.nextInt(NetViewer.HEIGHT - 70));
	}

	public String toString () {
		return "ShadowPlace: " + getID();
	}

	/**
	 * Clones this object.
	 * Clone is disconnected from a real place and arc.
	 * @return A clone of this CPNPlaceFigure.
	 */
	public Object clone() {
		final SubnetPlace clone = (SubnetPlace) super.clone();
		clone.realArcIDs = new HashSet();
		clone.realArcs = new HashSet();
		return clone;
	}

	public void addRealArc(CPNArcFigure arcFig) {
		this.realArcs.add(arcFig);
		arcFig.addFigureChangeListener(this);
		this.realArcIDs.add(arcFig.getID());
	}

	/**
	 * Called by the real arc if that arc has changed.
	 * If the arc has reconnected to some other place, we remove ourselves.
	 * @param e the event
	 */
	public void figureChanged(FigureChangeEvent e){
		CPNArcFigure arealArc = (CPNArcFigure) e.getFigure();
		Figure start = arealArc.startFigure();
		Figure end = arealArc.endFigure();
		if (start != null && end != null && (start != this && end != this)) {
			//reconnected, but not to us! remove ourselves!
			arealArc.removeFigureChangeListener(this);
			listener().figureRequestRemove(new FigureChangeEvent(this));
		} else {
			//id of realArc might have changed...
			if (! this.realArcIDs.contains(arealArc.getID()) ) {
				this.realArcIDs.clear();
				Iterator i = this.realArcs.iterator();
				while (i.hasNext()) {
					this.realArcIDs.add(((CPNArcFigure)i.next()).getID() );
				}
			}
		}
	}

	/**
	 * The real Arc has been removed, so we can remove ourselves as well.
	 * @param e the event
	 */
	public void figureRemoved(FigureChangeEvent e){
		listener().figureRequestRemove(new FigureChangeEvent(this));
	}

	public void figureInvalidated(FigureChangeEvent e){/* nothing */};
	public void figureRequestRemove(FigureChangeEvent e){/* nothing */}
	public void figureRequestUpdate(FigureChangeEvent e){/* nothing */}

	private void readObject(ObjectInputStream s)
			throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		Iterator i = this.realArcs.iterator();
		while (i.hasNext()) {
			((Figure)i.next()).addFigureChangeListener(this);
		}
	}

	/**
	 * Releases the figures resources. This method is usually called after it has
	 * permanently been removed from a drawing. It stops listening to its
	 * connectionfigures.
	 */
	public void release() {
		super.release();
		Iterator i = this.realArcs.iterator();
		while (i.hasNext()) {
			((Figure)i.next()).removeFigureChangeListener(this);
		}
	}

	/**
	 * Sets the realArcID's by reading them from the comma-separated string
	 * @param buf the comma-separated string of ID's
	 */
	public void setRealArcIDs(String buf) {
		this.realArcIDs.clear();
		final StringTokenizer t = new StringTokenizer (buf, ",", false);
		while (t.hasMoreTokens()) {
			this.realArcIDs.add(t.nextToken());
		}
	}

	public String getRealArcIDs() {
		final StringBuffer buf = new StringBuffer ();
		Iterator i = this.realArcIDs.iterator();
		while (i.hasNext()) {
			String id =(String) i.next();
			if (buf.length() != 0) buf.append(",");
			buf.append(id);
		}
		return buf.toString();
	}

	public void reconnectToArc(CompositeFigure drawing) {
		Iterator i = this.realArcIDs.iterator();
		while (i.hasNext()) {
			Figure fig = drawing.getFigure( (String)i.next() );
			fig.addFigureChangeListener(this);
			this.realArcs.add(fig);
		}
	}

	public String getID() {
		if (this.realArcs.isEmpty()) return super.getID();
		return ((CPNArcFigure)this.realArcs.iterator().next()).getArc().getPlace().getID();
	}
}