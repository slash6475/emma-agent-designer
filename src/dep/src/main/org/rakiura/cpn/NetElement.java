// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Represents an abstract node element.
 *
 *<br><br>
 * NetElement.java<br>
 * Created: Mon Sep 25 12:01:13 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.10 $
 *@since 1.0
 */
public interface NetElement extends java.io.Serializable, Cloneable {

	/**
	 * sets the id of the netelement. Use with care, e.g. only when you want to
	 * regenerate all id's. ID's have to be unique.
	 * @param newID the new, unique, ID.
	 */
	void setID(String newID);

	/**
	 * Returns this element's ID.
	 *@return this element's ID.
	 */
	String getID();

	/**
	 * Returns the name of this node. Names are useful for easy
	 * referencing net nodes.
	 *@return name of this node.
	 */
	String getName();

	/**
	 * Sets the name of this net element.
	 * Names are useful for easy referencing
	 * net nodes.
	 *@param aName a new name for this node.
	 *@return old name of this node.
	 */
	String setName(final String aName);


	/**
	 * Visitor pattern.
	 *@param aVisitor visitor
	 *@return this net element
	 */
	NetElement apply(final NetVisitor aVisitor);


} // NetElement
//////////////////// end of file ////////////////////
