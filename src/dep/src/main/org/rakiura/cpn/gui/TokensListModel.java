//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.rakiura.cpn.Place;
import org.rakiura.cpn.event.PlaceEvent;
import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TokensAddedEvent;
import org.rakiura.cpn.event.TokensRemovedEvent;

/**
 * Represents a token list model for JList GUI components.
 * 
 * <br><br>
 * TokensListModel.java created on 30/05/2003 10:28:41<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 
 */
class TokensListModel implements ListModel, PlaceListener {

	private Object[] tokens;
	private Place place;
	private List listeners = new ArrayList ();
		
	public TokensListModel (Place aPlace) {
		this.place = aPlace;
		this.place.addPlaceListener (this);
		this.tokens = this.place.getTokens().toArray();
	}
	
	public int getSize() {
		return this.tokens.length;
	}
	
	public Object getElementAt(int index) {
		return this.tokens[index];
	}
	
	public void addListDataListener(ListDataListener l) {
		this.listeners.add (l);			
	}
	
	public void removeListDataListener(ListDataListener l) {
		this.listeners.remove (l);
	}

	// Place listener methods

	public void notify(PlaceEvent anEvent) {
		throw new RuntimeException ("This method should not be called!");
	}
	
	public void notify(TokensRemovedEvent anEvent) {
		this.tokens = anEvent.getPlace().getTokens().toArray();
		final ListDataEvent e = new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,this.tokens.length);
		final Iterator i = this.listeners.iterator();
		while (i.hasNext()) {
			((ListDataListener) i.next()).contentsChanged (e);
		}
	}
	
	public void notify(TokensAddedEvent anEvent) {
		this.tokens = anEvent.getPlace().getTokens().toArray();
		final ListDataEvent e = new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,this.tokens.length);
		final Iterator i = this.listeners.iterator();
		while (i.hasNext()) {
			((ListDataListener) i.next()).contentsChanged (e);
		}
	}
		
} // end of TokensListModel
