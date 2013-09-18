//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import org.rakiura.draw.figure.CompositeFigure;

/**
 * Represents a child figure which is a decoration for CPN figure.
 *
 * <br><br>
 * CPNDecoration.java created on 7/07/2003 08:53:01<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
public interface CPNDecoration {

	/**
	 * Reconnects this decoration to its parent, based on the ParentFigureID property.
	 *
	 * @param d composite figure this decoration and its parent are part of.
	 */
	public void reconnectToParent (CompositeFigure d);

	/**
	 * Returns this decoration parent ID. Used when reconnecting.
	 * @return this decoration parent id.
	 */
	public String getParentFigureID ();

	/**
	 * Sets this decoration parent ID. Used when this decoration data is read from
	 * XML serialization.
	 * @param aID this decoration parent ID.
	 */
	public void setParentFigureID (String aID);

}
