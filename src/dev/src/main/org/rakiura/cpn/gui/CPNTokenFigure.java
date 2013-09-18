//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import org.rakiura.draw.ParentFigure;
import org.rakiura.draw.figure.CompositeFigure;
import org.rakiura.draw.util.BoxHandleKit;

/**
 * Represents a single token from a Place multiset. This is a circle or a text 
 * representation of the token.
 * 
 * <br><br>
 * CPNTokenFigure.java created on 6/07/2003 18:03:24<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 
 */
public class CPNTokenFigure extends CompositeFigure {
	

	private static final long serialVersionUID = 3617290108257317170L;
	
	/** The actual token handle. */
	private Object token;

	/**
	 * Creates new child text figure.
	 * @param t
	 */
	public CPNTokenFigure (Object t) {
		this.token = t;
	}

	public Object getToken() {
		return this.token;
	}
	
	public boolean canBeParent (
			@SuppressWarnings("unused")
			final ParentFigure aFig) {
		return false;
	}

	public Vector handles() {
		Vector handles = new Vector();
		BoxHandleKit.addHandles(this, handles);
		return handles;
	}

	public void basicDisplayBox(final Point origin, final Point corner) {
		//move all elements to new origin
		Rectangle box = displayBox();
		int movex, movey;
		movex = origin.x - box.x;
		movey = origin.y - box.y;
		basicMoveBy (movex, movey);
	}
			
}
