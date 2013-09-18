//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import org.rakiura.cpn.NetElement;
import org.rakiura.draw.Figure;
import org.rakiura.draw.ParentFigure;

/**
 * Represents an abstract figure for NetElements.
 *
 * <br><br>
 * CPNAbstractFigure.java created on 27/06/2003 15:47:49<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.11 $
 */
public interface CPNAbstractFigure extends Figure, ParentFigure {

	/**
	 * Returns the name decoration figure.
	 * @return name decoration figure.
	 */
	CPNNameFigure getNameFigure();

	/**
	 * Sets the new name figure. Used after serialization of children and parents.
	 * @param newName a new name
	 */
	void setNameFigure (CPNNameFigure newName);

	/**
	 * Returns the wrapped net element.
	 * @return wrapped net element itself.
	 */
	NetElement getNetElement ();

	/**
	 * Sets this net element. It will recreate the name TextFigure.
	 * @param aNetElement new net element.
	 */
	void setNetElement (final NetElement aNetElement);

	/**
	 * Returns the current inspect status for this figure.
	 * @return current inspect status.
	 */
	int getInspectStatus();

	/**
	 * Sets the current inspect status to a given value.
	 * @param aStatus new value of current inspect status.
	 */
	void setInspectStatus (int aStatus);

	/**
	 * Makes sure that the figure does not listen to a netElement anymore. Use it
	 * if a figure is not displayed anymore. e.g. when the drawing is closed.
	 * @see #reReference
	 */
	void deReference();

	/**
	 * Makes sure that the figure does listen to its netElement again. Use it if
	 * a figure is displayed again. e.g. when a drawing is redisplayed.
	 * @see #deReference
	 */
	void reReference();

} ///////////// EOF////////