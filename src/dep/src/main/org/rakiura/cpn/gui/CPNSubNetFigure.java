// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.Transition;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.basic.DrawingApplication;
import org.rakiura.draw.basic.DrawingViewFrameListener;
import org.rakiura.draw.figure.ChopBoxConnector;
import org.rakiura.draw.figure.ChopEllipseConnector;
import org.rakiura.draw.figure.RectangleFigure;
import org.rakiura.draw.figure.TextFigure;
import java.io.File;

/**
 * Figure representing a CPN sub net.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.53 $
 *@since 3.0
 */
public class CPNSubNetFigure extends RectangleFigure
		implements CPNAbstractNetFigure, NetListener {

	/** SUID. */
	static final long serialVersionUID = -1802136575075550621L;

	/**
	 * The time to display the rectangle in a different color
	 * if it is involved in an event. */
	//final private static long DELAY = 50;
	final private static int DEFAULT_SIZE = 20;

	/** The Subnet Drawing */
	private NetDrawing aSubNetDrawing;

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
	 * Creates a new SubNetFigure with a new Net to listen to and a new NetDrawing.
	 * The name and ID are set to a new unique ID.
	 *
	public CPNSubNetFigure() {
		this(new BasicNet(), null, null);
		this.net.setTypeText("SUBNET");
	}*/

	/**
	 * Creates a new subnetfigure for the subnetdrawing. Use this for example if
	 * you are adding a net+drawing to an other net as a subnet. The subnetfigure
	 * will have the name, drawing and annotations of the subnetdrawing and the
	 * net therein.
	 * @param aNetDrawing the netdrawing
	 */
	public CPNSubNetFigure(NetDrawing aNetDrawing) {
		this(aNetDrawing.getNet());
		setNetDrawing(aNetDrawing);
	}

	/**
	 * Creates a new SubNetFigure with the provided SubNet to listen to.
	 * This constructor is useful if you are creating a new drawing for a given net.
	 * Except for the aNet argument, all arguments should be null or they should
	 * represent the super figure that contains this subnetfigure and all fake
	 * connection figures are drawn in the subnet and the supernet.
	 * The transition is a green rectangle with the size equal to {@link #size size}
	 * Above the rectangle the name is displayed in bold.
	 * The display of the name can be switched off.
	 *
	 *@param aNet a {@link org.rakiura.cpn.Net Net} that is
	 *   to be displayed by this CPNSubNetFigure
	 *@param superPlacesMap if the subnetfigure is created from a subnet, this
	 * map should contain the places of the supernet it belongs to, so that all
	 * fake arcs and places can be drawn properly. Otherwise it should be null.
	 *@param parentDrawing The drawing that contains this subnet figure. Used to
	 * draw the fake arcs. Use Null if there are no fake arcs to draw.
	 *
	 *@see #setNameVisible
	 */
	public CPNSubNetFigure(final Net aNet, Map superPlacesMap, NetDrawing parentDrawing) {
		this(aNet);
		//create the drawing
		createNetDrawing(superPlacesMap, parentDrawing);
	}

	/**
	 * Creates a new SubNetFigure from the provided Net. Creates no netdrawing!
	 * @param aNet the net.
	 */
	protected CPNSubNetFigure(final Net aNet) {
		super (new Point(0, 0), new Point(DEFAULT_SIZE * 3, DEFAULT_SIZE));
		this.net = aNet;
		super.setID(this.net.getID());
		setFillColor(java.awt.Color.green);
		this.name = new CPNNameFigure (this);
		this.imports = new CPNAnnotationFigure(CPNAnnotationFigure.IMPORT, this);
		this.declaration = new CPNAnnotationFigure(CPNAnnotationFigure.DECLARATION, this);
		this.implement = new CPNAnnotationFigure(CPNAnnotationFigure.IMPLEMENTS, this);
		this.name.moveBy(-25, -25);
		this.imports.moveBy(10, -8);
		//create the declaration
		this.declaration.moveBy(-10, -8);
		//create the implement
		this.implement.moveBy(0, -8);
	}

	public CPNAnnotationFigure getImportsFigure() { return this.imports; }
	public CPNAnnotationFigure getDeclarationFigure() {	return this.declaration; }
	public CPNAnnotationFigure getImplementFigure() { return this.implement; }
	public CPNNameFigure getNameFigure () { return this.name; }

	public void setImportsFigure (CPNAnnotationFigure aFig) {
		if (this.imports != null) removeAnnotationFigure(this.imports);
		this.imports = aFig;
	}

	public void setDeclarationFigure (CPNAnnotationFigure aFig) {
		if (this.declaration != null &&
				this.declaration != aFig) removeAnnotationFigure(this.declaration);
		this.declaration = aFig;
	}

	public void setImplementFigure (CPNAnnotationFigure aFig) {
		if (this.implement != null &&
				this.implement != aFig) removeAnnotationFigure(this.implement);
		this.implement = aFig;
	}

	public void setNameFigure (CPNNameFigure aFig) {
		if (this.name != null &&
				this.name != aFig) removeAnnotationFigure(this.name);
		this.name = aFig;
	}

	/**
	 * Returns the net that is represented by this figure.
	 * @return a {@link org.rakiura.cpn.Net Net} represented by this figure.
	 */
	public Net getNet() {
		return this.net;
	}


	/**
	 * Clones this net. Clone has same name, but different ID
	 * @return clone of this net.
	 *
	public Object clone() {
		final CPNSubNetFigure clone = (CPNSubNetFigure) super.clone();
		clone.setNetElement (this.net.clone());
		clone.name = new CPNNameFigure (clone);
		clone.imports = new CPNAnnotationFigure(CPNAnnotationFigure.IMPORT, clone);
		clone.declaration = new CPNAnnotationFigure(CPNAnnotationFigure.DECLARATION, clone);
		clone.implement = new CPNAnnotationFigure(CPNAnnotationFigure.IMPLEMENTS, clone);
		clone.name.moveBy(-25, -25);
		clone.imports.moveBy(10, -8);
		clone.declaration.moveBy(-10, -8);
		clone.implement.moveBy(0, -8);
		clone.createNetDrawing(null, null);
		return clone;
	}*/

	public NetElement getNetElement() {
		return this.net;
	}

	/**
	 * Sets the subnet element for this subnet. It does not create a new drawing to
	 * reflect the new subnet, nor does it set the net to the drawing.
	 * @param aNetElement the subnet.
	 */
	public void setNetElement(final NetElement aNetElement) {
		this.net = (Net) aNetElement;
		setID (this.net.getID ());
	}

	public void setID(final String newID) {
		super.setID(newID);
		this.name.setParentFigureID(getID());
		this.imports.setParentFigureID(getID());
		this.implement.setParentFigureID(getID());
		this.declaration.setParentFigureID(getID());
	}

	/**
	 * Sets the netdrawing to the new netdrawing and dereferences the old drawing. The net reference is not changed!
	 * @param newNetDrawing a net drawing. It should reflect the net element that
	 * is represented by this figure.
	 */
	public void setNetDrawing(final NetDrawing newNetDrawing) {
		if (this.aSubNetDrawing != null) {
			this.aSubNetDrawing.removeNetListener(this);
			this.aSubNetDrawing.deReference();
		}
		this.aSubNetDrawing = newNetDrawing;
		this.aSubNetDrawing.addNetListener(this);
	}

	public void setNameVisible(boolean visible) {
		this.name.setVisible(visible);
	}

	public int getInspectStatus() {
		return this.inspectStatus;
	}

	public void setInspectStatus(int aStatus) {
		this.inspectStatus = aStatus;
	}

	public String toString() {
		return "" + this.net + " ID:  " + getID();
	}

	/**
	 * Adds the arc to the subnet. The caller of this method has to make sure the
	 * arc is not part of an other drawing and a fake Arc is drawn in the original drawing
	 * @param arcFigure An arcFigure to add to the drawing and to connect to a transition
	 * @param placeFigure the placeFigure to virtually connect to
	 * @param type the type of the arc (input/output)
	 */
	public void addArc(CPNArcFigure arcFigure, CPNPlaceFigure placeFigure, int type) {
		//CPNArcFigure calls this if it wants to connect to this subnet
		CPNTransitionFigure selectedTransitionFigure = selectTransitionFigure(this.aSubNetDrawing);
		//create/reuse fake place in this drawing
		SubnetPlace fakePlace = (SubnetPlace) this.aSubNetDrawing.getFigure(placeFigure.getID());
		if (fakePlace == null) {
			fakePlace = new SubnetPlace(placeFigure.getID());
			this.aSubNetDrawing.add(fakePlace);
		}
		//add arc to new drawing and connect
		this.aSubNetDrawing.add(arcFigure);
		if (type == CPNArcFigure.INPUT_ARC) {
			arcFigure.connectStart(new ChopEllipseConnector(fakePlace));
			arcFigure.connectEnd(new ChopBoxConnector(selectedTransitionFigure));
		} else {
			arcFigure.connectStart(new ChopBoxConnector(selectedTransitionFigure));
			arcFigure.connectEnd(new ChopEllipseConnector(fakePlace));
		}
		arcFigure.updateConnection();
		fakePlace.addRealArc(arcFigure); //add reference to arc after arc is created and has proper id
	}

	/**
	 * @todo make it possible to connect to subnetfigures as well as transitions..
	 * @param theSubNetDrawing thesubnetdrawing
	 * @return transitionfigure
	 */
	private CPNTransitionFigure selectTransitionFigure(NetDrawing theSubNetDrawing) {
		List transitionOrSubnetFigures = new LinkedList();
		FigureEnumeration fe = theSubNetDrawing.figures();
		while (fe.hasMoreElements()) {
			Object nextElement = fe.nextElement();
//			if (nextElement instanceof CPNTransitionFigure || nextElement instanceof CPNSubNetFigure) {
			//to go deeper into subnethierarchy makes it more complicated to draw fake arcs & stuff. not yet implemented, so if you reactivate the previous line, an arc wil get drawn to an invisible transition that is only present in an other page/subnet...
			if (nextElement instanceof CPNTransitionFigure) {
				transitionOrSubnetFigures.add(nextElement);
			}
		}
		//select transition to connect to:
		if (transitionOrSubnetFigures.size() == 0) {
			CPNTransitionFigure f = new CPNTransitionFigure(new Transition());
			this.aSubNetDrawing.add(f);
			return f;
		} else if (transitionOrSubnetFigures.size() == 1) {
			Figure selectedFigure = (Figure) transitionOrSubnetFigures.get(0);
			if (selectedFigure instanceof CPNTransitionFigure) {
				return (CPNTransitionFigure) selectedFigure;
			}
			return selectTransitionFigure(((CPNSubNetFigure)selectedFigure).getSubNetDrawing());
		} else { //more transitions: let user pick one
			//popup: to which transition do you want to connect?
			SelectTransitionInSubNetDialog dialog =
					new SelectTransitionInSubNetDialog(transitionOrSubnetFigures);
			dialog.setVisible(true);
			Figure selectedFigure = (Figure) transitionOrSubnetFigures.get(dialog.getSelectedIndex());
			dialog.dispose();
			if (selectedFigure instanceof CPNTransitionFigure) {
				return (CPNTransitionFigure) selectedFigure;
			}
			return selectTransitionFigure(((CPNSubNetFigure)selectedFigure).getSubNetDrawing());
		}
	}

	/**
	 * Event notification.
	 * @param anEvent a <code>NetEvent</code> value
	 */
	public void notify(final NetEvent anEvent) {
		NetDrawing nd = anEvent.getNetDrawing();
		if (nd.isFiring()) {
			setFillColor(java.awt.Color.orange);
		} else if (nd.isActive()) {
			setFillColor(java.awt.Color.pink);
		} else if (nd.isEnabled()) {
			setFillColor(java.awt.Color.white);
		} else setFillColor(java.awt.Color.green);
		changed();
	}

	/**
	 * Returns this subnet drawing handle.
	 * @return this subnet drawing.
	 */
	public NetDrawing getSubNetDrawing () {
		return this.aSubNetDrawing;
	}

	/**
	 *
	 * @param superPlacesMap null, or if creating a drawing with fake arks and
	 * places, the set of places from the drawig that contains this subnetfigure.
	 * @param parentDrawing null, or if creating a drawing with fake arks and
	 * places, the drawing that contains this subnetfigure.
	 */
	private void createNetDrawing(Map superPlacesMap, NetDrawing parentDrawing) {
		this.aSubNetDrawing = new NetDrawing (getNet (), superPlacesMap, this, parentDrawing);
		final FigureEnumeration fe = this.aSubNetDrawing.figures();
		while (fe.hasMoreElements()) {
			final Object nextElement = fe.nextElement();
			if (nextElement instanceof CPNNetFigure) {
				final CPNNetFigure nf = (CPNNetFigure) nextElement;
				nf.setVisible(false);
				final FigureEnumeration cfe = nf.children();
				while (cfe.hasMoreElements()) {
					cfe.nextFigure().setVisible(false);
				}
			}
		}
		if (getNet().getAllTransitions().length == 0) {
			//add primary transition to subnet:
			final CPNTransitionFigure tf = new CPNTransitionFigure();
			this.aSubNetDrawing.add(tf);
			tf.moveBy(NetViewer.random.nextInt(350), NetViewer.random.nextInt(300));
		}
		this.aSubNetDrawing.addNetListener(this);
	}

	public boolean inspect(DrawingView view, boolean alternate) {
		if (alternate) {
			//reset everything
			resetAnnotationLocation(this.name, -25, -25);
			resetAnnotationLocation(this.imports, 10, -8);
			resetAnnotationLocation(this.declaration, -10, -8);
			resetAnnotationLocation(this.implement, 0, -8);
			this.implement.updateDisplay();
			this.imports.updateDisplay();
			this.declaration.updateDisplay();
			notify(new NetEvent(this.aSubNetDrawing));
			return true;
		}
		DrawingApplication ownerThatCanOpenNewFrames = view.editor().getOwner();
		if (ownerThatCanOpenNewFrames != null) {
			NetEditViewer subnetviewer = new NetEditViewer(getSubNetDrawing());
			subnetviewer.drawing().setName(this.getNet().getName());
			subnetviewer.drawing().setFile(new File("SUBNET"));
			subnetviewer.setEditor(view.editor());
			//note: if internal frame is closed, it is dereferenced, so figures do
			//not listen to their netelements anymore. This is important for the
			//next time you open a drawing.
			ownerThatCanOpenNewFrames.addInternalFrame (subnetviewer, subnetviewer.getInternalFrame());
			return true;
		}
		NetViewer subnetviewer = new NetViewer(getSubNetDrawing());
		subnetviewer.drawing().setName(this.getNet().getName());
		subnetviewer.drawing().setFile(new File("SUBNET"));
		subnetviewer.setEditor(view.editor());
		Frame f = subnetviewer.getFrame();
		//add listener for dereferencing
		f.addWindowListener(new DrawingViewFrameListener(subnetviewer));
		f.setVisible(true);
		return true;
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
		newLoc.x = thisLoc.x + basicOffsetX + CPNSubNetFigure.DEFAULT_SIZE/2;
		newLoc.y = thisLoc.y + basicOffsetY + CPNSubNetFigure.DEFAULT_SIZE/2;
		t.moveBy( newLoc.x - textLoc.x, newLoc.y - textLoc.y);
	}

	public void drawFrame(Graphics g) {
		Rectangle r = displayBox();
		g.drawRect(r.x, r.y, r.width-1, r.height-1);
		g.drawRect(r.x+4, r.y+4, r.width-9, r.height-9);
	}

	public void displayBox (Point s, Point e) {
		super.displayBox (s, new Point (s.x + 3*DEFAULT_SIZE, s.y + DEFAULT_SIZE));
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		this.aSubNetDrawing.removeNetListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		if (this.aSubNetDrawing != null) this.aSubNetDrawing.addNetListener(this);
	}

} /////// EOF //////////