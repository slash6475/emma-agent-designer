// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TokensAddedEvent;
import org.rakiura.cpn.event.TokensRemovedEvent;


/**
 * Represents a Place in the JFern Petri Net. Place is one of the elementary
 * nodes in the Petri Net graph. Place contains tokens which are kept
 * inside a single multiset structure.
 * A place can also be fused with other places by calling the method {@link #addPlace}.
 * The fused places can be in different nets, but the nets have to be executed
 * by only one simulator, to prevent ConcurrentModification errors.
 * If different places are fused, only one will keep a reference to all the others.
 * This fusionplace is the 'boss'. All other places have a reference to the boss.
 * You can get the id's of all the places that are fused together from a boss-place
 * with {@link #getFusedPlacesIDs }.
 *
 *<br><br>
 * Place.java<br>
 * Created: Mon Sep 25 21:23:17 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@author <a href="mfleurke@infoscience.otago.ac.nz">Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.34 $
 *@since 2.0
 */
public class Place extends Node {

	private static final long serialVersionUID = 3257850965423108657L;

	/** the container for the tokens */
	private Multiset multiset;

	/** the list of listeners for place events*/
	private List listeners = new ArrayList();

	/** the place that is the leader of a group of fused places. The leader keeps
	 * the references to all other places, while the other places keep a reference
	 * to the leader in this variable. */
	private Place fusionPlace = null;

	/** list of InputArcs (arcs that go out of the place)*/
	private List inputs = new ArrayList(10);

	/** list of OutputArcs (arcs that go into this place*/
	private List outputs = new ArrayList(10);

	/** list of places with which this place is fused. The list always contains
	 * itself. If this place is a leader of a fusion, it also lists the other places
	 * of the fusion */
	private List places = new ArrayList();

	/**
	 * Creates a new <code>Place</code> instance.
	 * Name and ID are set to a new unique ID
	 */
	public Place() {
		this.multiset = new Multiset();
		this.places.add(this);
	}

	/**
	 * Creates a new <code>Place</code> instance
	 * with a given Place name.
	 * @param aName this place name.
	 */
	public Place(final String aName) {
		super(aName);
		this.multiset = new Multiset();
		this.places.add(this);
	}

	/**
	 * Creates a new <code>Place</code> instance,
	 * initialized with a given multiset.
	 * @param aMultiset an initial <code>multiset</code>
	 */
	public Place(final Collection aMultiset) {
		this.multiset = new Multiset(aMultiset);
		this.places.add(this);
	}

	/**
	 * Adds an input Arc. This method is not for the user to call
	 * directly, it is called by newly created arcs which plug
	 * themselves automatically to the appropriate place.
	 * @param anArc the arc to add
	 * @return this place.
	 */
	public Place addInput(final InputArc anArc){
		this.inputs.add(anArc);
		return this;
	}

	/**
	 * Removes an input Arc. This method is not for the user to call
	 * directly, it is called by arcs which unplug
	 * themselves automatically from the appropriate place.
	 * @param anArc the InputArc to remove.
	 * @return this place.
	 */
	public Place removeInput(final InputArc anArc){
		this.inputs.remove (anArc);
		return this;
	}

	/**
	 * Adds an output Arc. This method is not for the user to call
	 * directly, it is called by newly created arcs which plug
	 * themselves automatically to the appropriate place.
	 * @param anArc the arc to add
	 * @return this Place.
	 */
	public Place addOutput(final OutputArc anArc){
		this.outputs.add(anArc);
		return this;
	}

	/**
	 * Removes an output Arc. This method is not for the user to call
	 * directly, it is called by arcs which unplug
	 * themselves automatically from the appropriate place.
	 * @param anArc the OutputArc to remove
	 * @return this Place.
	 */
	public Place removeOutput(final OutputArc anArc){
		this.outputs.remove (anArc);
		return this;
	}

	/**
	 * Returns list of all {@link InputArc input arcs} from this place.
	 * <b>Note:</b> {@link InputArc input arcs} go from this place to a given
	 * transition, so in a sense, from the place point of view
	 * could be treated as outgoing arcs.
	 * @return set of all {@link InputArc input arcs} from this place.
	 */
	public List inputArcs(){
		return this.inputs;
	}

	/**
	 * Returns list of all {@link OutputArc output arcs} to this place.
	 * <b>Note:</b> {@link OutputArc output arcs} go from a given transition
	 * to this place, so in a sense, from the place point of view
	 * could be treated as incoming arcs.
	 * @return set of all {@link OutputArc output arcs} to this place.
	 */
	public List outputArcs(){
		return this.outputs;
	}

	/**
	 * Returns list of all {@link InputArc input arcs} from this place and the
	 * places to which this place is fused, if any.
	 * <b>Note:</b> {@link InputArc input arcs} go from this place to a given
	 * transition, so in a sense, from the place point of view
	 * could be treated as outgoing arcs.
	 * @return set of all {@link InputArc input arcs} from this place.
	 */
	public List fusedInputArcs() {
		if (this.places.size() <= 0 ) throw new RuntimeException ("Bad number of fused places");

		ListIterator li = this.places.listIterator();
		Place next = (Place) li.next();
		List allInputArcs = next.inputArcs();
		while (li.hasNext()) {
			next = (Place) li.next();
			allInputArcs.addAll(next.inputArcs());
		}
		return allInputArcs;
	}

	/**
	 * Returns list of all {@link OutputArc output arcs} to this place and the
	 * places to which this place is fused, if any.
	 * <b>Note:</b> {@link OutputArc output arcs} go from a given transition
	 * to this place, so in a sense, from the place point of view
	 * could be treated as incoming arcs.
	 * @return set of all {@link OutputArc output arcs} to this place.
	 */
	public List fusedOutputArcs() {
		if (this.places.size() <= 0 ) throw new RuntimeException("Bad number of fused places");

		ListIterator li = this.places.listIterator();
		Place next = (Place) li.next();
		List allOutputArcs = next.outputArcs();
		while (li.hasNext()) {
			next = (Place) li.next();
			allOutputArcs.addAll(next.outputArcs());
		}
		return allOutputArcs;
	}

	/**
	 * Returns all the tokens for this place.
	 * Returned is a new Multiset with the tokens in this place.
	 * To change the number of tokens in the place, use the methods
	 * {@link #addToken addToken}{@link #addTokens (Collection)} 
	 * and {@link #removeToken removeToken}{@link #removeTokens (Collection)}
	 * @return this place's collection of tokens
	 */
	public Multiset getTokens() {
		return new Multiset(this.multiset);
	}

	/**
	 * Adds given tokens to this place.
	 * @param aMultiset a <code>Collection</code> of tokens
	 * @see #addToken
	 */
	public void addTokens(final Collection aMultiset) {
		if (aMultiset.size() == 0) return;
		if (this.fusionPlace == null) {
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).addTokensQuietly(aMultiset);
			}
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).fireTokensAddedEvent(aMultiset);
			}
		}
		else this.fusionPlace.addTokens(aMultiset);
	}

	/**
	 * Adds a token to this place.
	 * @param aToken the token to be added.
	 */
	public void addToken(final Object aToken) {
		addTokens(new Multiset(aToken));
	}

	/**
	 * removes all tokens in the given collection from the place (if present).
	 * @param aMultiset the collection of tokens to be removed.
	 */
	public void removeTokens(final Collection aMultiset) {
		if (aMultiset.size() == 0) return;
		if (this.fusionPlace == null) {
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).removeTokensQuietly(aMultiset);
			}
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).fireTokensRemovedEvent(aMultiset);
			}
		}
		else this.fusionPlace.removeTokens(aMultiset);
	}

	/**
	 * Removes given token from this place.
	 *@param aToken token to be removed.
	 *@return <code>true</code> if the token was succesfully removed,
	 * <code>false</code> if the token was not present in the place and
	 * was not removed.
	 */
	public boolean removeToken(final Object aToken) {
		if (this.fusionPlace == null) {
			boolean returnVal = getTokens().contains(aToken);
			removeTokens(new Multiset(aToken));
			return returnVal;
		}
		return this.fusionPlace.removeToken(aToken);
	}

	/**
	 * Removes all the tokens from this place.
	 */
	public void clearTokens() {
		if (this.fusionPlace == null) {
			Multiset tokens = getTokens();
			if (tokens.size() == 0) return;
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).clearTokensQuietly();
			}
			for (int i = 0; i < this.places.size(); i++) {
				((Place) this.places.get(i)).fireTokensRemovedEvent(tokens);
			}
		}
		else this.fusionPlace.clearTokens();
	}

	/**
	 * Fuses the new Place with this place. All added places
	 * to this fusion place will share same marking (same tokens multiset)
	 * as this fusion place. If the added place already has marking, it will be
	 * reset to the existing fused places marking.
	 *@param aPlace a <code>Place</code> value to be added
	 *  to this fusion place.
	 */
	public void addPlace(final Place aPlace) {
		if (this.fusionPlace == null) {

			//add tokens to new place, and notify if tokens are new.
			final Multiset current = aPlace.getTokens();
			aPlace.clearTokens();
			final List intersection = new ArrayList(current);
			intersection.retainAll(this.getTokens());
			final List tokensToBeAdded = new ArrayList(this.getTokens());
			tokensToBeAdded.removeAll(intersection);
			if (tokensToBeAdded.size() > 0) {
				aPlace.addTokens(tokensToBeAdded);
			}

			//take over the fused places of the other place:
			ListIterator li = aPlace.places.listIterator();
			while (li.hasNext()) {
				Place next = (Place) li.next();
				this.places.add(next); //add the fused place to this place
				next.fusionPlace = this; //set the fusion leader to this place
				if (next != aPlace) li.remove(); //always keep a reference to itself in
				//the places list, but remove references to the other places, since they
				//are taken over by this place.
			}
		} else this.fusionPlace.addPlace(aPlace);
	}

	/**
	 * Removes a given place from this fusion place.
	 *@param aPlace a <code>Place</code> value
	 *@return <code>true</code> if a place was present in this fusion place
	 * and was removed succesfully, <code>false</code> otherwise.
	 */
	public boolean removePlace(final Place aPlace) {
		if (this.places.remove(aPlace)) {
			aPlace.fusionPlace = null;
			return true;
		}
		return false;
	}

	/**
	 * Returns an array of id's of places of which this place is a boss in a fusion.
	 * The id of this place itself is included. This means that if this is not a top
	 * fusion place, or if this place has not fused with anything, the number of
	 * ID's in the returned array is one.
	 * @return an array of id's of places with which this place is fused.
	 */
	public String[] getFusedPlacesIDs() {
		ArrayList ids = new ArrayList();
		final Iterator iter = this.places.iterator();
		while (iter.hasNext()) {
			Place next = (Place) iter.next();
			ids.add(next.getID());
		}
		return (String[]) ids.toArray(new String[ids.size()]);
	}

	/**
	 * Returns the boss of the fusion places to which this place is fused,
	 * or null if it is not fused / is the boss itself.
	 * @return the boss of the fusion places to which this place is fused,
	 * or null if it is not fused / is the boss itself.
	 */
	public Place getTopFusionPlace() {
		return this.fusionPlace;
	}

	/**
	 * Adds given tokens to this place without notifying listeners.
	 *@param aMultiset a <code>multiset</code> of tokens.
	 */
	void addTokensQuietly(final Collection aMultiset) {
		this.multiset.addAll(aMultiset);
	}

	/**
	 * Adds given token to this place without notifying listeners.
	 *@param aToken token to be added.
	 */
	void addTokenQuietly(final Object aToken) {
		this.multiset.add(aToken);
	}

	/**
	 * Removes all tokens from the given multiset from this place without
	 * notifying listeners.
	 *@param aMultiset multiset containing tokens to be removed.
	 */
	void removeTokensQuietly(final Collection aMultiset) {
		this.multiset.removeAll(aMultiset);
	}

	/**
	 * Removes given token from this place without notifying listeners.
	 * @param aToken token to be removed.
	 * @return true if the token could be removed, false otherwise.
	 */
	boolean removeTokenQuietly(final Object aToken) {
		return this.multiset.remove(aToken);
	}

	/**
	 * clears token from this place without notifying listeners
	 */
	void clearTokensQuietly() {
		this.multiset.clear();
	}

	/**
	 * Visitor pattern.
	 *@param aVisitor a <code>NetVisitor</code> value
	 *@return a <code>NetElement</code> value
	 */
	public NetElement apply(final NetVisitor aVisitor) {
		aVisitor.place(this);
		return this;
	}


	/**
	 * Registers a given PlaceListener with this place.
	 *@param aListener a <code>PlaceListener</code> to
	 * be registered with this Place.
	 */
	public void addPlaceListener(final PlaceListener aListener) {
		this.listeners.add(aListener);
	}

	/**
	 * Deregisters a given PlaceListener from this place.
	 *@param aListener a <code>PlaceListener</code> to
	 * be removed with this Place.
	 */
	public void removePlaceListener(final PlaceListener aListener) {
		this.listeners.remove(aListener);
	}

	/**
	 * Notifies all listeners about tokens being removed from this place.
	 *@param tokens the tokens that were removed from the place.
	 */
	void fireTokensRemovedEvent(Collection tokens) {
		int size = this.listeners.size();
		if ( size == 0) return;
		final TokensRemovedEvent anEvent = new TokensRemovedEvent(this, new Multiset(tokens));
		final PlaceListener[] l = (PlaceListener[]) this.listeners.toArray(new PlaceListener[size]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(anEvent);
		}
	}

	/**
	 * Notifies all listeners about tokens being added to this place.
	 *@param tokens the tokens that were added.
	 */
	void fireTokensAddedEvent(Collection tokens) {
		int size = this.listeners.size();
		if ( size == 0) return;
		final TokensAddedEvent anEvent = new TokensAddedEvent(this, new Multiset(tokens));
		final PlaceListener[] l = (PlaceListener[])	this.listeners.toArray(new PlaceListener[size]);
		for (int i = 0; i < l.length; i++) {
			l[i].notify(anEvent);
		}
	}

	/**
	 *  Cloning.  Delegates clonnig to the parent class.
	 * TODO: make a better clonning method.
	 * @return the cloned copy of this place.
	 */
	public Object clone() {
		final Place p = ((Place) super.clone());
		return p;
	}

	/**
	 * Returns human readable representiation of this place.
	 *@return a <code>String</code> value.
	 */
	public String toString() {
		final String res = "Place: " + getName() + " ";
		return res + this.multiset.toString();
	}

	/**
	 * Indicates whether some other place is "equal to" this place.
	 * A place is equal to an other place if it is the same place or if it is
	 * fused with that place.
	 * @param place the reference place with which to compare.
	 * @return <code>true</code> if this Place is the same, or fused with the place
	 *          argument; <code>false</code> otherwise.
	 */
	public boolean equals(Place place) {
		if (this.fusionPlace == null) {
			return this.places.contains(place);
		}
		return this.fusionPlace.equals(place);
	}

} // Place
//////////////////// end of file ////////////////////