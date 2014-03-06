// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package emma.petri.control.event;

import java.util.Set;

import emma.petri.model.Place;
import emma.petri.model.Token;

@SuppressWarnings("rawtypes")
public class TokensRemovedEvent extends PlaceEvent {
  
	/**
	 * 
	 */
	private static final long serialVersionUID = -724553833452637275L;
	private Set<Token> set;
  
	public TokensRemovedEvent(final Place place, final Set<Token> tokens) {
		super(place);
		this.set = tokens;
	}

	public Set<Token> getTokens() {
		return this.set;
	} 
}