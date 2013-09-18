// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.io.File;

import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TransitionListener;

/**
 * Represents a Colour Petri Net. Represents a complete graph
 * of a given CPN.
 *
 *<br><br>
 * Net.java<br>
 * Created: Tue Sep 26 15:33:55 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.26 $
 *@since 3.0
 */
public interface Net extends NetElement {

	/**
	 * Returns the instance of this net.
	 * @return a net instance.
	 */
	Net getInstance();
	
	/**
	 * Removes all the listeners, nodes, places, and arcs.
	 */
	void clear();
	
	/** ID for places and transitions and arcs regenerations. */
	void regenerateIDs();
	
	/** Returns the inscription language type. */
	String getCpnLang();
	
	/** Sets the inscription language type. */
	void setCpnLang(String cpnLang);
	
	/**
	 * Returns a new list of this net layout file names.
	 * @return the list of layout file names.
	 */
	String[] getLayouts();

	/**
	 * Adds new layout filename for this net.
	 * @param aFilename a layout filename to be registered with this net.
	 */
	void addLayout (String aFilename);

	/**
	 * Removes a given layout file.
	 * @param aFilename filename of the layout to be removed.
	 */
	void removeLayout (String aFilename);

	/**
	 * Removes all layouts handles for this net.
	 */
	void removeAllLayouts ();

	/**
	 * Sets the current net directory. This is a directory for jfern nets,
	 * layouts and markings.
	 * @param aFile new working directory.
	 */
	public void setNetDir (final File aFile);

	/**
	 * Returns current working directory.
	 * @return this net current directory.
	 */
	public File getNetDir ();

	/**
	 * Returns Declaration text.
	 * @return declaration text.  */
	String getDeclarationText ();

	/**
	 * Returns Imports text.
	 * @return import text. */
	String getImportText ();

	/**
	 * Returns Implements text.
	 * @return implements text. */
	String getImplementsText ();

	/**
	 * Returns Type text.
	 * @return type of this CPN Net. */
	String getTypeText ();

	/**
	 * Sets Declaration text.
	 * @param aText declaration text.
	 */
	void setDeclarationText (final String aText);

	/**
	 * Sets Import text.
	 * @param aText import text.
	 */
	void setImportText (final String aText);

	/**
	 * Sets implements text.
	 * @param aText implements text. */
	void setImplementsText (final String aText);

	/**
	 * Sets Type of this CPN Net.
	 * @param aText Type text. */
	void setTypeText(final String aText);

	/**
	 * Adds given PlaceListener to all Places in this net.
	 *@param aListener a <code>PlaceListener</code> to be
	 *  registered with all the places in this net.
	 */
	void addPlaceListener(final PlaceListener aListener);

	/**
	 * Adds given TransitionListener to all Transitions in this net.
	 *@param aListener a <code>TransitionListener</code> to be
	 *  registered with all the transitions in this net.
	 */
	void addTransitionListener(final TransitionListener aListener);

	/**
	 * Removes given PlaceListener from all Places in this net.
	 *@param aListener a <code>PlaceListener</code> to be
	 *  registered from all places in this net.
	 */
	void removePlaceListener(final PlaceListener aListener);

	/**
	 * Removes given TransitionListener from all Transitions in this net.
	 *@param aListener a <code>TransitionListener</code> to be
	 *  removed from all the transitions in this net.
	 */
	void removeTransitionListener(final TransitionListener aListener);

	/**
	 * Adds a node to the net.
	 *@param aNode NetElement to be added to the net.
	 *@return this net.
	 */
	Net add(final NetElement aNode);

	/**
	 * Removes the specified net element from this net.
	 *@param aNode NetElement to be removed.
	 *@return null if there was no such element in the net, or the
	 * removed node.
	 */
	NetElement remove(final NetElement aNode);

	/**
	 * Looks up a net element by its ID.
	 *@param anId ID of the net element.
	 *@return the net element object or null if not found.
	 */
	NetElement forID(final String anId);

	/**
	 * Looks up a node by its name.
	 *@param aName Name of the net element.
	 *@return the net element object or null if not found.
	 */
	NetElement forName(final String aName);

	/**
	 * Returns all the transitions from this net.
	 * Note: this method will return all transitions, including all transitions
	 * from this net subnets, and subnet subnets, and so on. To obtain only top
	 * level transitions for this net, excluding subnets' transitions,
	 * use {@link #getNetElements() getNetElements()} instead.
	 *@return list with all the transitions.
	 */
	Transition[] getAllTransitions();

	/**
	 * Returns all the places from this net.
	 * Note: this method will return all the places, including all the places
	 * from this net subnets, and subnet subnets, and so on. To obtain only top
	 * level places for this net, excluding subnets' places,
	 * use {@link #getNetElements() getNetElements()} instead.
	 *@return list with all the places.
	 */
	Place[] getAllPlaces();

	/**
	 * Returns all the input arcs from this net.
	 * Note: this method will return all the input arcs, including all the input arcs
	 * from this net subnets, and subnet subnets, and so on. To obtain only top
	 * level arcs for this net, excluding subnets' input arcs,
	 * use {@link #getNetElements() getNetElements()} instead.
	 *@return list with all the input arcs.
	 */
	InputArc[] getAllInputArcs ();

	/**
	 * Returns all the output arcs from this net.
	 * Note: this method will return all the output arcs, including all the output arcs
	 * from this net subnets, and subnet subnets, and so on. To obtain only top
	 * level arcs for this net, excluding subnets' output arcs,
	 * use {@link #getNetElements() getNetElements()} instead.
	 *@return list with all the output arcs.
	 */
	OutputArc[] getAllOutputArcs ();

	/**
	 * Returns all the subnets.
	 * Note: this method will return all the subnets, including all the subnets
	 * from this net subnets, and subnet subnets, and so on. To obtain only top
	 * level subnets for this net, excluding subnets' subnets,
	 * use {@link #getNetElements() getNetElements()} instead.
	 *@return list with all the subnets.
	 */
	Net[] getAllSubnets ();

	/**
	 * Returns all net elements of this net.
	 * Note: the subnets are returned as individual subnet objects, so
	 * all transitions and places of subnets are not returned by
	 * this call.
	 * @return all net elements of this net.
	 */
	NetElement[] getNetElements();

	/**
	 * Returns the current marking of the net.
	 *@return current marking of the net.
	 */
	Marking getMarking();

	/**
	 * Sets new marking for the entire net. If some places are not
	 * referenced in the new marking, the existing marking is left
	 * unchanged.
	 *@see #resetMarking
	 *@param aMarking new Marking for the entire net.
	 *@return previous marking of the net.
	 */
	Marking setMarking(final Marking aMarking);

	/**
	 * Resets the marking in the entire net.
	 *@return previous marking of the net.
	 */
	Marking resetMarking();

	/**
	 * Rehash names and IDs of this Petri net elements. This method can be used
	 * to dynamically rehash the internal net datastructures to reflect any
	 * dynamic name changes. It also re-assigns UIDs to places and transitions.
	 */
	void rehash();

} // Net
//////////////////// end of file ////////////////////
