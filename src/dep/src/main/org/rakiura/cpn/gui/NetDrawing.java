//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rakiura.cpn.AbstractArc;
import org.rakiura.cpn.BasicSimulator;
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Simulator;
import org.rakiura.cpn.Transition;
import org.rakiura.cpn.event.PlaceEvent;
import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TokensAddedEvent;
import org.rakiura.cpn.event.TokensRemovedEvent;
import org.rakiura.cpn.event.TransitionEvent;
import org.rakiura.cpn.event.TransitionFinishedEvent;
import org.rakiura.cpn.event.TransitionListener;
import org.rakiura.cpn.event.TransitionStartedEvent;
import org.rakiura.cpn.event.TransitionStateChangedEvent;
import org.rakiura.draw.ChildFigure;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.ParentFigure;
import org.rakiura.draw.basic.BasicDrawing;
import org.rakiura.draw.figure.ChopBoxConnector;
import org.rakiura.draw.figure.ChopEllipseConnector;

/**
 * Represents Net Drawing.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.53 $
 *@since 3.0
 */
public class NetDrawing extends BasicDrawing implements PlaceListener, TransitionListener, NetListener {

	private static final long serialVersionUID = 3979266932702523956L;

	private transient Net net;
	private transient Simulator simulator;

	public NetDrawing() {
	}
	
	/**
	 * Creates a new Net Drawing from the given net element
	 * @param aNet The net that has to be drawn.
	 */
	public NetDrawing(final Net aNet) {
		if (aNet == null)
			throw new RuntimeException("Net cannot be null");
		this.net = aNet;
		setName(aNet.getName());
		initDrawingFromNet(aNet);
		newSimulator();
	}

	/**
	 * Creates a new Net Drawing from the given (SUB)net element. It also draws
	 * the fake arcs and places
	 * @param aNet The subnet to be drawn
	 * @param superPlacesMap The places to which the arcs that cross the boundary
	 * between supernet and subnet can connect to.
	 * @param parentFigure the subnetfigure that contains this netdrawing
	 * @param parentDrawing the drawing that contains the subnetfigure 'parentFigure'.
	 */
	public NetDrawing(
		final Net aNet,
		final Map superPlacesMap,
		final CPNSubNetFigure parentFigure,
		final NetDrawing parentDrawing) {
		if (aNet == null) throw new RuntimeException("Net cannot be null.");
		this.net = aNet;
		setName(aNet.getName());
		initDrawingFromNet(aNet, superPlacesMap, parentFigure, parentDrawing);
		newSimulator();
	}

	public void newSimulator() {
		this.simulator = new BasicSimulator(this.net);
	}

	public Simulator getSimulator() {
		return this.simulator;
	}

	/**
	 * Initializes a drawing from the given net object. This method will automatically
	 * generate all the figures needed to represent net elements from the given net.
	 * For changing only the net handle, use setNet(Net) method instead.
	 * @param aNet the net object
	 * @see #setNet
	 */
	public void initDrawingFromNet(Net aNet) {
		initDrawingFromNet(aNet, null, null, null);
	}

	public void setNet(Net aNet) {
		this.net = aNet;
		setName(aNet.getName());
		initDrawingFromNet(aNet);
		newSimulator();
	}

	public Net getNet() {
		return this.net;
	}

	/**
	 * This method just adds the figure to the drawing,
	 * it does not update the net handle nor adds children.
	 * @param aFigure figure to be added to the drawing, without
	 * changes to the net handle.
	 */
	public void addFigure(final Figure aFigure) {
		super.add(aFigure);
		if (aFigure instanceof CPNPlaceFigure) {
			Place p = ( (CPNPlaceFigure)aFigure).getPlace();
			p.addPlaceListener(this);
			if (p.getTokens().size() > 0) {
				this.placesWithTokens.add(p);
				this.active = true;
			}
		} else if (aFigure instanceof CPNTransitionFigure) {
			Transition t = ((CPNTransitionFigure) aFigure).getTransition();
			t.addTransitionListener(this);
			if (t.isEnabled()) {
				this.enabled = true;
				this.enabledTransitions.add(t);
			}
		} else if (aFigure instanceof CPNSubNetFigure) {
			NetDrawing nd = ((CPNSubNetFigure) aFigure).getSubNetDrawing();
			nd.addNetListener(this);
			if (nd.isEnabled()) {
				this.enabled = true;
				this.enabledTransitions.add(nd);
			}
			if (nd.isActive()) {
				this.active = true;
				this.placesWithTokens.add(nd);
			}
		}
	}

	/**
	 * Called when adding a figure to drawing which is not part of the net yet.
	 * It Updates the net handle and adds all children.
	 * @param aFigure the figure to be added
	 * @return the figure that was added (if it was not in the drawing yet)
	 */
	public Figure add(final Figure aFigure) {
		if (!this.fFigures.contains(aFigure)) {
			boolean doCleanLayouts = false;
			if (aFigure instanceof CPNPlaceFigure) {
				Place p = ((CPNPlaceFigure) aFigure).getPlace();
				this.net.add( p );
				p.addPlaceListener(this);
				if (p.getTokens().size() > 0) {
					this.placesWithTokens.add(p);
					this.active = true;
				}
				doCleanLayouts = true;
			} else if (aFigure instanceof CPNArcFigure) {
				((CPNArcFigure) aFigure).setNetDrawing(this);
				AbstractArc a = ((CPNArcFigure) aFigure).getArc();
				if (a != null) this.net.add( a );
				doCleanLayouts = true;
			} else if (aFigure instanceof CPNTransitionFigure) {
				Transition t = ((CPNTransitionFigure) aFigure).getTransition();
				this.net.add(t);
				t.addTransitionListener(this);
				if (t.isEnabled()) {
					this.enabled = true;
					this.enabledTransitions.add(t);
				}
				doCleanLayouts = true;
			} else if (aFigure instanceof CPNSubNetFigure) {
				this.net.add(((CPNSubNetFigure) aFigure).getNet());
				NetDrawing nd = ((CPNSubNetFigure) aFigure).getSubNetDrawing();
				nd.addNetListener(this);
				if (nd.isEnabled()) {
					this.enabled = true;
					this.enabledTransitions.add(nd);
				}
				if (nd.isActive()) {
					this.active = true;
					this.placesWithTokens.add(nd);
				}
				doCleanLayouts = true;
			}
			if (doCleanLayouts) this.net.removeAllLayouts();
			super.add(aFigure);
			addChildrenOf(aFigure);
		}
		return aFigure;
	}

	/**
	 * Adds the children of a figure to the drawing if they are not there yet.
	 * @param aFigure the figure with children. (should be a ParentFigure)
	 */
	public void addChildrenOf(Figure aFigure) {
		if (aFigure instanceof ParentFigure) {
			final FigureEnumeration e = ((ParentFigure) aFigure).children();
			while (e.hasMoreElements()) {
				final Figure f = e.nextFigure();
				if (!containsFigure(f)) super.add (f);
			}
		}
	}

	/**
	 * Removes a figure. The children are removed as well.
	 * Removes netelements from the net. Does not remove a child on its own if
	 * the parent is an CPNabstractFigure
	 * @param aFigure the figure to be removed
	 * @return the figure that is (to be) removed.
	 */
	public Figure remove(final Figure aFigure) {
		if (aFigure instanceof ChildFigure &&
			((ChildFigure)aFigure).parent() instanceof CPNAbstractFigure) return aFigure;
		boolean doCleanLayouts = false;
		if (aFigure instanceof CPNPlaceFigure) {
			Place p = ((CPNPlaceFigure) aFigure).getPlace();
			p.removePlaceListener(this);
			this.net.remove(p);
			if (this.placesWithTokens.remove(p) ) {
				this.active = !this.placesWithTokens.isEmpty();
			}
			doCleanLayouts = true;
		} else if (aFigure instanceof CPNArcFigure) {
			doCleanLayouts = true;
			//arc removes itself
		} else if (aFigure instanceof CPNTransitionFigure) {
			Transition t = ((CPNTransitionFigure) aFigure).getTransition();
			t.removeTransitionListener(this);
			this.net.remove(t);
			if (this.enabledTransitions.remove(t) ) this.enabled = !this.enabledTransitions.isEmpty();
			doCleanLayouts = true;
		} else if (aFigure instanceof CPNSubNetFigure) {
			NetDrawing nd = ((CPNSubNetFigure) aFigure).getSubNetDrawing();
			nd.removeNetListener(this);
			this.net.remove(((CPNSubNetFigure) aFigure).getNet());

			if (this.enabledTransitions.remove(nd) ) this.enabled = !this.enabledTransitions.isEmpty();
			if (this.placesWithTokens.remove(nd) ) this.enabled = !this.placesWithTokens.isEmpty();
			doCleanLayouts = true;
		}
		super.remove(aFigure);
		if (doCleanLayouts) this.net.removeAllLayouts();
		if (aFigure instanceof ParentFigure) {
			final FigureEnumeration e = ((ParentFigure) aFigure).children();
			while (e.hasMoreElements()) {
				final Figure f = e.nextFigure();
				if (containsFigure(f)) super.remove (f);
			}
		}
		return aFigure;
	}

	/**
	 * Regenerates all net elements represented on the drawing by figures.
	 * This is utility method which processes all figures in a current net drawing,
	 * re-creating and updating the Net handle. Used after reading a serialized
	 * drawing, or, after reading pure net layout, without having the net handle correctly
	 * generated out of net file.
	 */
	public void generateNetElementsBasedOnFigures () {
		this.net = NetGenerator.INSTANCE.getNetInstance();
		this.net.clear();
		final FigureEnumeration fe = this.figures();
		while (fe.hasMoreElements()) {
			Figure fig = fe.nextFigure();
			if (fig instanceof CPNAbstractFigure) {
				CPNAbstractFigure absFig = (CPNAbstractFigure) fig;
				if (absFig instanceof CPNTransitionFigure) {
					final Transition t = new Transition(absFig.getNameFigure().getText());
					t.setID(absFig.getID());
					absFig.setNetElement(t);
					this.net.add(t);
				} else if (absFig instanceof CPNPlaceFigure) {
					final Place p = new Place(absFig.getNameFigure().getText());
					p.setID(absFig.getID());
					absFig.setNetElement(p);
					this.net.add(p);
				} else if (absFig instanceof CPNSubNetFigure) {
					final Net n = (Net) absFig.getNetElement();
					this.net.add(n);
				} else if (absFig instanceof CPNNetFigure) {
					this.net.setDeclarationText(((CPNNetFigure)absFig).getDeclarationFigure().getAnnotation());
					this.net.setImplementsText(((CPNNetFigure)absFig).getImplementFigure().getAnnotation());
					this.net.setImportText(((CPNNetFigure)absFig).getImportsFigure().getAnnotation());
					this.net.setName(((CPNNetFigure)absFig).getNameFigure().getText());
					((CPNNetFigure)absFig).setNetElement(this.net);
				} else if (absFig instanceof CPNArcFigure) {
					final CPNArcFigure arcFig = (CPNArcFigure) fig;
					arcFig.setNetDrawing(this);
					final AbstractArc a = arcFig.getArc();
					a.setID(arcFig.getID());
					if (a instanceof InputArc) {
						a.setPlace(((CPNPlaceFigure) arcFig.startFigure()).getPlace());
						a.setTransition(((CPNTransitionFigure) arcFig.endFigure()).getTransition());
					} else {
						a.setPlace(((CPNPlaceFigure) arcFig.endFigure()).getPlace());
						a.setTransition(((CPNTransitionFigure) arcFig.startFigure()).getTransition());						
					}
					System.out.println("");
					this.net.add(a); //needed if the netdrawing in the arc is null after serialization.
				}
			}
		}
	}

	/**
	 * Based on the given data, it recreates, reconnects and draws all the arcs.
	 * @param transitions CPNTransitionFigure array with all the transition figures.
	 * @param placesMap Place->CPNPlaceFigure map.
	 * @param superPlacesMap same as placesMap.
	 * @param parentFigure subnet figure.
	 * @param parentDrawing parent drawing.
	 */
	public void drawArcs (CPNTransitionFigure[] transitions, Map placesMap,
		Map superPlacesMap, CPNSubNetFigure parentFigure, NetDrawing parentDrawing) {
		for (int i = 0; i < transitions.length; i++) {
			CPNTransitionFigure jt = transitions[i];
			drawInputArcs (jt, placesMap, superPlacesMap, parentFigure, parentDrawing);
			drawOutputArcs(jt, placesMap, superPlacesMap, parentFigure, parentDrawing);
		}
	}

	private void writeObject (ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
	}

	private void readObject (ObjectInputStream s)
		throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		generateNetElementsBasedOnFigures ();
	}

	/**
	 * initializes a subnetdrawing from the given subnet object
	 * @param aNet the subnet
	 * @param superPlacesMap a map of the CPNPlaceFigures in the supernet drawing
	 * @param parentFigure the CPNSubNetFigure this drawing belongs to
	 * @param parentDrawing the drawing the parentFigure belongs to.
	 */
	private void initDrawingFromNet (Net aNet, Map superPlacesMap,
		 CPNSubNetFigure parentFigure, NetDrawing parentDrawing)
	{
		final Map placesMap = new HashMap();
		final ArrayList transitionFigures = new ArrayList();
		// clear the drawing
		removeAll();
		// add the net figure
		final CPNNetFigure cpnNetFigure = new CPNNetFigure(aNet);
		addFigure(cpnNetFigure);
		addChildrenOf(cpnNetFigure);
		cpnNetFigure.moveBy(8, 15);

		// add places, trans, arcs and subnets
		final ArrayList subnets = new ArrayList();
		NetElement[] netElements = aNet.getNetElements();
		for (int i=0; i < netElements.length; i++) {
			if (netElements[i] instanceof Place) {
				final CPNPlaceFigure jp = new CPNPlaceFigure((Place) netElements[i]);
				placesMap.put(netElements[i], jp); //collect all the places
				addFigure(jp); //draw the places
				addChildrenOf(jp);
			}
			else if (netElements[i] instanceof Transition) {
				final CPNTransitionFigure jt = new CPNTransitionFigure((Transition)netElements[i]);
				addFigure(jt); //draw the transitions
				addChildrenOf(jt);
				transitionFigures.add(jt);
			}
			else if (netElements[i] instanceof Net) subnets.add(netElements[i]);
		}

		Iterator iterator = subnets.iterator();
		while (iterator.hasNext()) {
			Net next = (Net) iterator.next();
			final CPNSubNetFigure js =
				new CPNSubNetFigure(next, placesMap, this);
			addFigure(js);
			addChildrenOf(js);
		}

		//draw the arcs
		drawArcs ((CPNTransitionFigure[]) transitionFigures.toArray(
				new CPNTransitionFigure[transitionFigures.size()]), placesMap, superPlacesMap,
			parentFigure, parentDrawing);
		this.net = aNet;
	}

	private void drawInputArcs(
		final CPNTransitionFigure jt,
		final Map placesMap,
		final Map superPlacesMap,
		final CPNSubNetFigure parentFigure,
		final NetDrawing parentDrawing) {
		//draw input arcs
		final Iterator inA = jt.getTransition().inputArcs().iterator();
		while (inA.hasNext()) {
			final InputArc arc = (InputArc) inA.next();
			CPNPlaceFigure jp = (CPNPlaceFigure) placesMap.get(arc.getPlace());
			if (jp == null) { //we have an arc between subnet and supernet
				//get place to connect to:
				jp = (CPNPlaceFigure) superPlacesMap.get(arc.getPlace());
				//create new ArcFigure with stubs for connection points
				final CPNArcFigure arcFig =
					new CPNArcFigure( arc, new CPNPlaceFigure(), new CPNTransitionFigure());
				//create fake arc
				SubnetArc fakeArc =	new SubnetArc (arcFig, parentDrawing);
				fakeArc.connectStart(new ChopEllipseConnector(jp));
				fakeArc.connectEnd(new ChopBoxConnector(parentFigure));
				fakeArc.updateConnection();
				parentDrawing.add(fakeArc);
				//create fake place
				final SubnetPlace sp = new SubnetPlace(jp.getID());
				this.add(sp);
				//connect new Arc Figure to the proper figures
				arcFig.connectStart(new ChopEllipseConnector(sp));
				arcFig.connectEnd(new ChopBoxConnector(jt));
				arcFig.updateConnection();
				this.add(arcFig);
				sp.addRealArc(arcFig);
			} else {
				final CPNArcFigure arcFig = new CPNArcFigure(arc, jp, jt);
				this.add(arcFig);
			}
		}
	}

	private void drawOutputArcs(
		final CPNTransitionFigure jt,
		final Map placesMap,
		final Map superPlacesMap,
		final CPNSubNetFigure parentFigure,
		final NetDrawing parentDrawing) {
		//draw output arcs
		final Iterator outA = jt.getTransition().outputArcs().iterator();
		while (outA.hasNext()) {
			final OutputArc arc = (OutputArc) outA.next();
			CPNPlaceFigure jp = (CPNPlaceFigure) placesMap.get(arc.getPlace());
			if (jp == null) { //we have an arc between subnet and supernet
				//get place to connect to:
				jp = (CPNPlaceFigure) superPlacesMap.get(arc.getPlace());
				//create new ArcFigure with stubs for connection points
				final CPNArcFigure arcFig =	new CPNArcFigure(
						arc, new CPNPlaceFigure(), new CPNTransitionFigure());
				//create fake arc
				SubnetArc fakeArc =	new SubnetArc(arcFig, parentDrawing);
				fakeArc.connectStart(new ChopBoxConnector(parentFigure));
				fakeArc.connectEnd(new ChopEllipseConnector(jp));
				fakeArc.updateConnection();
				parentDrawing.add(fakeArc);
				//create fake place
				final SubnetPlace sp = new SubnetPlace(jp.getID());
				this.add(sp);
				//connect real arc to the proper figures
				arcFig.connectEnd(new ChopEllipseConnector(sp));
				arcFig.connectStart(new ChopBoxConnector(jt));
				arcFig.updateConnection();
				this.add(arcFig);
				sp.addRealArc(arcFig);
			} else {
				final CPNArcFigure arcFig = new CPNArcFigure(arc, jp, jt);
				this.add(arcFig);
			}
		}
	}
	///////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	/** there are tokens in the net */
	private boolean active=false;
	/** there is a transition firing */
	private boolean firing=false;
	/** there are transitions that can fire */
	private boolean enabled=false;
	/**/
	private List listeners = new ArrayList();
	private Set placesWithTokens = new HashSet();
	private Set enabledTransitions = new HashSet();

	/** is there a transition firing and / or are there tokens in the net
	 * @return true if firingh or tokens, false otherwise*/
	public boolean isActive() { return (this.active || this.firing);}
	/** is there a transition firing
	 @return if firing true, false otherwise */
	public boolean isFiring() { return this.firing;}
	/** are there transitions that can fire
	 @return true if there are enabled transitions, false otherwise */
	public boolean isEnabled() { return this.enabled;}

	/**
	 * Notifies the listener that the marking of the place has changed.
	 * It may be caused by either adding or removing a token from
	 * a Place.
	 *@param anEvent a <code>PlaceEvent</code> value
	 */
	public void notify(final PlaceEvent anEvent) {
		boolean oldActive = this.active;
		Place p = anEvent.getPlace();
		if (p.getTokens().size() > 0) {
			this.placesWithTokens.add(p);
			this.active = true;
		} else {
			this.placesWithTokens.remove(p);
			this.active = !this.placesWithTokens.isEmpty();
		}
		if (oldActive != this.active) {
			fireNetEvent();
		}
	}
	public void notify(final TokensRemovedEvent anEvent) {
		this.notify((PlaceEvent)anEvent);}
	public void notify(final TokensAddedEvent anEvent){
		this.notify((PlaceEvent)anEvent);
	}

	/**
	 * Event notification. This method throws runtime exception.
	 * @param anEvent a <code>TransitionEvent</code> value
	 */
	public void notify(final TransitionEvent anEvent) {
		throw new RuntimeException(
				"Unknown event type: " + anEvent.getClass() + " " + anEvent);
	}
	public void notify(final TransitionStartedEvent anEvent){
		this.firing = true;
		fireNetEvent();
	}
	public void notify(final TransitionFinishedEvent anEvent){
		this.firing = false;
		fireNetEvent();
	}
	public void notify(final TransitionStateChangedEvent anEvent){
		boolean oldEnabled = this.enabled;
		if (anEvent.getTransition().isEnabled()) {
			this.enabled = true;
			this.enabledTransitions.add(anEvent.getTransition());
		} else {
			this.enabledTransitions.remove(anEvent.getTransition());
			this.enabled = !this.enabledTransitions.isEmpty();
		}
		if (this.enabled != oldEnabled) {
			fireNetEvent();
		}
	}

	public void notify(final NetEvent anEvent){
		boolean oldEnabled = this.enabled;
		boolean oldFiring = this.firing;
		boolean oldActive = this.active;
		if (anEvent.getNetDrawing().isEnabled()) {
			this.enabled = true;
			this.enabledTransitions.add(anEvent.getNetDrawing());
		} else {
			this.enabledTransitions.remove(anEvent.getNetDrawing());
			this.enabled = !this.enabledTransitions.isEmpty();
		}
		if (anEvent.getNetDrawing().isActive()) {
			this.active = true;
			this.placesWithTokens.add(anEvent.getNetDrawing());
		} else {
			this.placesWithTokens.remove(anEvent.getNetDrawing());
			this.enabled = !this.placesWithTokens.isEmpty();
		}
		this.firing = anEvent.getNetDrawing().isFiring();
		if ( (this.enabled != oldEnabled) || ( oldFiring != this.firing) || (oldActive != this.active) ) {
			fireNetEvent();
		}
	}


	/**
	 * Notifies all listeners just after this net state changed.
	 */
	private void fireNetEvent() {
		final NetEvent event =
			new NetEvent(this);
		final NetListener[] l = (NetListener[])
			this.listeners.toArray(new NetListener[this.listeners.size()]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(event);
		}
	}

	/**
	 * Registers a given NetListener with this net.
	 *@param aListener a <code>NetListener</code> to
	 * be registered with this NetDrawing's Net.
	 */
	public void addNetListener(final NetListener aListener) {
		this.listeners.add(aListener);
	}

	/**
	 * Deregisters a given NetListener from this net.
	 *@param aListener a <code>NetListener</code> to
	 * be removed from this NetDrawing's Net.
	 */
	public void removeNetListener(final NetListener aListener) {
		this.listeners.remove(aListener);
	}

	/**
	 * Decouples the drawing from the net. Used if the drawing is not used
	 * anymore, but still listens to the net and netelements and this should not be the case.
	 */
	public void deReference(){
		final FigureEnumeration fe = this.figures();
		while (fe.hasMoreElements()) {
			Figure fig = fe.nextFigure();
			if (fig instanceof CPNAbstractFigure) {
				((CPNAbstractFigure)fig).deReference();
			}
		}
	}

	public void reReference() {
		final FigureEnumeration fe = this.figures();
		while (fe.hasMoreElements()) {
			Figure fig = fe.nextFigure();
			if (fig instanceof CPNAbstractFigure) {
				((CPNAbstractFigure)fig).reReference();
			}
		}
	}
}
//////////////////////////////EOF////////////////////////////////