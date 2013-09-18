//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.rakiura.cpn.AbstractArc;
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.Node;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureChangeEvent;
import org.rakiura.draw.FigureChangeListener;
import org.rakiura.draw.figure.ChopBoxConnector;
import org.rakiura.draw.figure.ChopEllipseConnector;
import org.rakiura.draw.figure.EllipseFigure;
import org.rakiura.draw.figure.LineConnection;
import org.rakiura.draw.figure.PolyLineFigure;
import org.rakiura.draw.figure.RectangleFigure;
import org.rakiura.draw.figure.TextFigure;


/**
 * Represents a graphical representation for an arc.
 * The graphic ignores the name of the arc.
 *@author Martin Fleurke
 *@version 4.0.0 $Revision: 1.66 $
 *@since 3.0
 */
public final class CPNArcFigure extends LineConnection
	implements CPNAbstractFigure {


	private static final long serialVersionUID = 3258690988078741300L;

	/** Defines an Input Arc*/
	public static final int INPUT_ARC = 1;

	/** Defines an Output Arc*/
	public static final int OUTPUT_ARC = 2;

	/** Defines an arc that is not yet an input or output arc */
	public static final int UNDEFINED_ARC = 0;

	/** The subfigure that represents the guard text (in case of an Input Arc) */
	private CPNAnnotationFigure guard;
	/** The subfigure that represents the expression text */

	private CPNAnnotationFigure expression;

	/** The Arc that is represented by this figure.
	 Either of class InputArc or Output Arc.*/
	private transient AbstractArc arc;

	private transient Place placeToConnectToWhenRecreatedInSubnet;

	/** ID of the wrapped arc. Needed because this figure has no arc on creation.*/
	private String id;
	/** Name of wrapped arc. */
	private String name;

	/** NetDrawing handle for proper handling of additon and removal of arcs. */
	private NetDrawing netDrawing;

	/** Shows in what stage of inspection we are */
	private int inspectStatus = 0;

	/**
	 * Creates a new arc. Its type is UNDEFINED_ARC until it is connected.
	 * The type of the arc can be requested by getting the attribute
	 * <code>ArcType</code>.
	 * The arc has an arrow tip on one end. The name of the arc can not be set.
	 *
	 *@see org.rakiura.draw.figure.AttributeFigure#getAttribute getAttribute
	 */
	public CPNArcFigure () {
		super(PolyLineFigure.ARROW_TIP_END);
		this.guard = new CPNAnnotationFigure(CPNAnnotationFigure.ARC_GUARD, this);
		this.expression = new CPNAnnotationFigure(CPNAnnotationFigure.EXPRESSION, this);
		this.guard.moveBy(0, -16);
		this.expression.moveBy(0, 0);
		setAttribute("ArcType", 	new Integer(CPNArcFigure.UNDEFINED_ARC));
	}

	/**
	 * Creates a new input or output arc figure for the provided arc.
	 * The arc must be previously connected to a Transition and a Place,
	 * and the figures that own that Place and Transition should be provided as
	 * well, so this arc can be connected to those figures.
	 * The type of the arc can be requested by getting the attribute
	 * <code>ArcType</code>.
	 * @throws RuntimeExeption if anArc is not an Input or Output arc
	 * @param anArc the Input or Output arc
	 * @param aPlaceFigure the figure with the Place where the Arc is connected to
	 * @param aTransitionFigure the figure with the Transition where the arc is connected to
	 * @see org.rakiura.cpn.Transition
	 * @see org.rakiura.cpn.Place
	 * @see org.rakiura.cpn.InputArc
	 * @see org.rakiura.cpn.OutputArc
	 */
	public CPNArcFigure(final AbstractArc anArc,
											final EllipseFigure aPlaceFigure,
											final RectangleFigure aTransitionFigure) {
		super(PolyLineFigure.ARROW_TIP_END);
		this.arc = anArc;
		this.id = anArc.getID ();
		super.setID (this.id);
		this.name = anArc.getName();
		if (anArc instanceof InputArc) {
			super.connectStart (new ChopEllipseConnector(aPlaceFigure));
			super.connectEnd(new ChopBoxConnector(aTransitionFigure));
			this.setAttribute("ArcType", new Integer(CPNArcFigure.INPUT_ARC));
			this.guard = new CPNAnnotationFigure (CPNAnnotationFigure.ARC_GUARD, this);
			this.guard.moveBy(0, -16);
		} else if (anArc instanceof OutputArc) {
			super.connectStart(new ChopBoxConnector(aTransitionFigure));
			super.connectEnd(new ChopEllipseConnector(aPlaceFigure));
			this.setAttribute("ArcType", new Integer(CPNArcFigure.OUTPUT_ARC));
			this.guard = null;
		} else throw new RuntimeException("AbstractArc was not an Input arc " +
						"or an Output Arc. Can't create CPNArcFigure from an abstract arc");
		this.expression = new CPNAnnotationFigure (CPNAnnotationFigure.EXPRESSION, this);
		this.expression.moveBy(0, 0);
		this.updateConnection ();
	}

	public CPNNameFigure getNameFigure () {
		return null; // not used!
	}

	public void setNameFigure (CPNNameFigure newName) {
		// not used
	}

	/**
	 * Sets the net(Drawing) handle for this arc figure.
	 * @param aNetDrawing netdrawing handle
	 */
	void setNetDrawing (final NetDrawing aNetDrawing) {
		this.netDrawing = aNetDrawing;
	}


	/**
	 * Returns the guard figure.
	 * @return the guard annotation figure.
	 */
	public CPNAnnotationFigure getGuardFigure() {
			return this.guard;
	}

	/**
	 * replaces the guard annotation figure. The old one is removed from the
	 * drawing as well.
	 * @param newGuard the new guard annotation figure.
	 */
	public void setGuardFigure (CPNAnnotationFigure newGuard) {
		if (this.guard != null &&
				this.guard != newGuard) removeAnnotationFigure(this.guard);
		this.guard = newGuard;
	}

	/**
	 * Returns the expression figure.
	 * @return the expression annotation figure.
	 */
	public CPNAnnotationFigure getExpressionFigure() {
			return this.expression;
	}

	/**
	 * Replaces the expression figure (the old one is removed from the drawing)
	 * @param newEx the new expression Figure
	 */
	public void setExpressionFigure (CPNAnnotationFigure newEx) {
		if (this.expression != null &&
				this.expression != newEx) removeAnnotationFigure(this.expression);
		this.expression = newEx;
	}

	/**
	 * returns the arc that this arcFigure represents.
	 * @return the Input- or OutputArc, or null if it is an Undefined Arc.
	 */
	public AbstractArc getArc() {
		if (this.arc == null) {
			//update arc, where arc is recreated.
			Figure start = this.startFigure();
			Figure end = this.endFigure();
			if (start != null && end != null) handleConnect(start, end);
		}
		return this.arc;
	}

	/**
	 * Tests whether two figures can be connected.
	 * An Input Arc will only connect to a transition from a place, an output arc
	 * will only connect to a place from a transition. An undefined arc will
	 * connect as an input arc and as an output arc
	 * The type of the arc is an attribute called "ArcType".
	 * @param start the figure that should be connected at the start of the arc
	 * @param end the figure that should be connected at the end of the arc.
	 * @return true if the connection is allowed according to the above.
	 * @see org.rakiura.draw.figure.AttributeFigure#getAttribute getAttribute
	 */
	public boolean canConnect(final Figure start, final Figure end) {
		if(start instanceof CPNPlaceFigure &&
			 (end instanceof CPNTransitionFigure ||
			 end instanceof CPNSubNetFigure)) {
			return true;
		}
		return (end instanceof CPNPlaceFigure &&
						 (start instanceof CPNTransitionFigure ||
						 start instanceof CPNSubNetFigure));
	}

	/**
	 * Handles the disconnection of a connection.
	 * @param start not used
	 * @param end not used
	 */
	protected void handleDisconnect(Figure start, Figure end) {
		//remove the arc object from the nets it might be part of.
		if (this.arc != null) {
			this.placeToConnectToWhenRecreatedInSubnet = this.arc.getPlace(); //in case we cancel disconnect
			this.netDrawing.getNet().remove (this.arc); //null after synchronization
			this.arc.release();
			this.arc = null;
		}
		this.setAttribute("ArcType", new Integer(CPNArcFigure.UNDEFINED_ARC));
	}

	/**
	 * Handles the creation of an arc object in the arc Figure and adds it to the
	 * net (in case of a reconnection after a disconnect). Or if the connection
	 * is to a subnet, it will move itself to the subnet and create a fake ark to
	 * take its place.
	 * If not subnet: If there is already an arc object, then it must have been created by
	 * the constructor of this arcFigure and the invokator of the constructor is
	 * most likely adding the figure and thus the arc to a NetDrawing. So we don't do a thing.
	 * @param startFig the figure to which the beginning of the line should connect
	 * @param endFig the figure to which the end of the line should connect.
	 */
	protected void handleConnect(final Figure startFig, final Figure endFig) {
		if (startFig instanceof CPNSubNetFigure) {
			connectToSubNet( (CPNSubNetFigure)startFig , (CPNPlaceFigure)endFig, CPNArcFigure.OUTPUT_ARC);
			return;
		} else if (endFig instanceof CPNSubNetFigure) {
			connectToSubNet((CPNSubNetFigure)endFig , (CPNPlaceFigure)startFig, CPNArcFigure.INPUT_ARC);
			return;
		}
		if (this.arc == null) {
			//create new arc, set startpoint/endpoint
			if (startFig instanceof CPNPlaceFigure) { //must be input arc
				createNewArcForFigure( ((CPNPlaceFigure) startFig), ((CPNTransitionFigure)endFig) , CPNArcFigure.INPUT_ARC);
			} else if (startFig instanceof SubnetPlace) { //must be input arc
				createNewArcForFigure( null, ((CPNTransitionFigure)endFig) , CPNArcFigure.INPUT_ARC);
			} else if (startFig instanceof CPNTransitionFigure) { //must be output arc connected to transition
				if (endFig instanceof CPNPlaceFigure) {
					createNewArcForFigure( ((CPNPlaceFigure) endFig), ((CPNTransitionFigure)startFig) , CPNArcFigure.OUTPUT_ARC);
				} else {
					createNewArcForFigure( null, ((CPNTransitionFigure)startFig) , CPNArcFigure.OUTPUT_ARC);
				}
			}
		}
	}

	/**
	 * creates a new Arc for the figure and sets the properties of the arc or the figure accordingly.
	 * @param placeFigure the CPNplaceFigure to which the arc has to connect, or null if it is a SubnetPlace
	 * @param transitionFigure the transition to connect to
	 * @param arcType INPUT_ARC or OUTPUT_ARC
	 */
	private void createNewArcForFigure(CPNPlaceFigure placeFigure, CPNTransitionFigure transitionFigure, int arcType) {
		if (arcType == CPNArcFigure.INPUT_ARC) {
			if (placeFigure == null) {
				this.arc = new InputArc (this.placeToConnectToWhenRecreatedInSubnet, transitionFigure.getTransition());
			} else {
				this.arc = new InputArc (placeFigure.getPlace(), transitionFigure.getTransition());
			}
			//set inputarc annotations to the new arc object:
			if (this.guard != null) {
				String annotation = this.guard.getAnnotation();
				if (annotation.length()==0) {
					//no annotation yet, so probably new arcfigure. get annotation of new arc
					this.guard.setAnnotation( ((InputArc)this.arc).getGuardText() );
				} else { 				//set the arc to match the figures annotations
					((InputArc) this.arc).setGuardText(annotation);
				}
			}
		} else { //assume output arc
			if (placeFigure != null) {
				this.arc = new OutputArc (transitionFigure.getTransition(), placeFigure.getPlace());
			} else {
				this.arc = new OutputArc (transitionFigure.getTransition(),	this.placeToConnectToWhenRecreatedInSubnet);
			}
		}
		setAttribute("ArcType", new Integer(arcType));
		if (this.id != null) {
			((Node) this.arc).setID (this.id);
			this.arc.setName (this.name);
		} else {
			setID(this.arc.getID());
			this.name = this.arc.getName();
		}

		//set arc expression to the new arc object:
		if (this.expression != null ) {
			String annotation = this.expression.getAnnotation();
			if (annotation.length() == 0) {
				//no annotation yet, so probably new arcfigure. get annotation of new arc
				this.expression.setAnnotation( this.arc.getExpressionText() );
			}
			else { //set the arc to match the figures annotations
				this.arc.setExpressionText(annotation);
			}
		}

		//add the arc to a net object
		if (this.netDrawing != null) { //?? is null after serialization?
			this.netDrawing.getNet().add (this.arc);
		}
	}

	/**
	 * Creates a fake arc and adds it to the drawing and creates a real arc in the subnetdrawing.
	 * @param subnetFigure the subnetfigure the arc has to be added to
	 * @param placeFigure the placefigure that the arc was / is connecting to.
	 * @param arcType INPUT_ARC or OUTPUT_ARC, should match the type of this.arc
	 */
	private void connectToSubNet(CPNSubNetFigure subnetFigure, CPNPlaceFigure placeFigure, int arcType) {
		CPNArcFigure realArc = (CPNArcFigure)this.clone();
		if (this.id != null) realArc.setID(this.id);
		realArc.setPlaceForRecreationInSubnet(placeFigure.getPlace());
		subnetFigure.addArc(realArc, placeFigure,	arcType);
		SubnetArc fakeArc = new SubnetArc(realArc, this.netDrawing);
		this.netDrawing.add(fakeArc);
		if (arcType == CPNArcFigure.OUTPUT_ARC) {
			fakeArc.connectStart(new ChopBoxConnector(subnetFigure));
			fakeArc.connectEnd(new ChopEllipseConnector(placeFigure));
		} else {
			fakeArc.connectStart (new ChopEllipseConnector(placeFigure));
			fakeArc.connectEnd (new ChopBoxConnector(subnetFigure));
		}
		fakeArc.updateConnection();
		this.listener().figureRequestRemove(new FigureChangeEvent(this));
	}

	/**
	 * If you clone an arc, it will lose its connections. The type will be undefined.
	 * Clone has different ID & name
	 * @return the clone
	 */
	public Object clone() {
		final CPNArcFigure clone = (CPNArcFigure) super.clone();
		clone.id = null;
		clone.guard = new CPNAnnotationFigure(CPNAnnotationFigure.ARC_GUARD, clone);
		clone.expression = new CPNAnnotationFigure(CPNAnnotationFigure.EXPRESSION, clone);
		clone.guard.moveBy(0, -16);
		clone.expression.moveBy(0, 0);
		clone.arc = null;
		clone.netDrawing = null;
		clone.setAttribute("ArcType", new Integer(CPNArcFigure.UNDEFINED_ARC));
		return clone;
	}

	/**
	 * Inspects the figure. If alternate is false, it will show/hide the
	 * annotations.
	 * @param view the DrawingView of this figure.
	 * @param alternate alternate inspection or not?
	 * @return true if inspection is done, false if no figure could be inspected.
	 */
	public boolean inspect(DrawingView view, boolean alternate) {
		if (alternate) {
			//reset everything
			if (this.guard != null) {
				resetAnnotationLocation(this.guard, 0, -16);
				this.guard.updateDisplay();
			}
			resetAnnotationLocation(this.expression, 0, 0);
			this.expression.updateDisplay();
			return true;
		} else if (this.inspectStatus == 0) {
			if (this.arc instanceof InputArc) this.guard.setVisible(false);
			this.expression.setVisible(false);
			this.inspectStatus++;
		} else if (this.inspectStatus == 1) {
			if (this.arc instanceof InputArc) {
				this.guard.setVisible(true);
				this.guard.updateDisplay();
			}
			this.expression.setVisible(false);
			this.inspectStatus++;
		} else {
			if (this.arc instanceof InputArc) {
				this.guard.setVisible(true);
				this.guard.updateDisplay();
			}
			this.expression.setVisible(true);
			this.expression.updateDisplay();
			this.inspectStatus = 0;
		}
		return true;
	}

	/**
	 * Releases the figures resources. This method is usually called after it has
	 * permanently been removed from a drawing. The Arc informs the net that
	 * it has been removed
	 */
	public void release() {
		super.release(); //disconnects (removes arc from net etc), notifies listeners
		if (this.guard != null) this.guard.removeFigureChangeListener(this);
		if (this.expression != null) this.expression.removeFigureChangeListener(this);
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		if (this.guard != null) this.guard.addFigureChangeListener(this);
		if (this.expression != null) this.guard.addFigureChangeListener(this);
	}

	/**
	 * Arc has no visual name, so this maethod is empty.
	 * @param visible not used.
	 */
	@SuppressWarnings("all")
	public void setNameVisible(boolean visible) {
	}

	public void setName(final String newName) {
		this.name = newName;
	}

	public String getName() {
		return this.name;
	}

	public NetElement getNetElement() {
		return this.arc;
	}

	public void setNetElement(final NetElement aNetElement) {
		if (this.arc != null) {
			if (this.netDrawing != null) this.netDrawing.getNet().remove (this.arc);
			this.arc.release();
		}
		this.arc = (AbstractArc) aNetElement;
		if (aNetElement instanceof InputArc) {
			setAttribute("ArcType", new Integer(CPNArcFigure.INPUT_ARC));
		} else setAttribute("ArcType", new Integer(CPNArcFigure.OUTPUT_ARC));
		setID(this.arc.getID());
	}

	public String toString () {
	if (this.arc != null)
		return this.arc.toString();
	return "Arc: null";
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
		Point thisLoc = this.center();
		Point textLoc = t.displayBox().getLocation();
		Point newLoc = new Point();
		newLoc.x = thisLoc.x + basicOffsetX;
		newLoc.y = thisLoc.y + basicOffsetY;
		t.moveBy( newLoc.x - textLoc.x, newLoc.y - textLoc.y);
	}

	public void setID(final String newID) {
		super.setID(newID);
		this.id = newID;
		this.expression.setParentFigureID(this.id);
		if (this.guard != null) {
			this.guard.setParentFigureID(this.id);
		}
	}

	/**
	 * used in connecting arc to subnet
	 * @param aPlace the place to which the arc should connect later on.
	 */
	private void setPlaceForRecreationInSubnet(Place aPlace) {
		this.placeToConnectToWhenRecreatedInSubnet = aPlace;
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		//nothing to dereference.
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		//nothing to rereference
	}

} ///////////// EOF //////////////