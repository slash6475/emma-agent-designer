// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Basic abstract implementation of the NetElement. This is the most
 * primitive abstract building block of the Net.
 *
 *<br><br>
 * Node.java<br>
 * Created: Mon Sep 25 11:02:54 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.13 $
 *@since 1.0
 */
public abstract class Node implements NetElement {

	/**/
	private static long newID = 0;
	/**/
	private String id;
	/**/
	private String name;

	/**
	 * Creates new node.
	 */
	public Node() {
		this.id = newUniqueID();
		this.name = "_" + this.id;
	}

	/**
	 * Creates new node.
	 *@param aName this node name.
	 */
	public Node(final String aName) {
		this.id = newUniqueID();
		this.name = aName;
	}

	/**
	 * Returns ID for this node.
	 *@return ID for this node.
	 */
	public String getID() {
		return this.id;
	}
	
	/** Sets the ID for this Node. */
	public void setID (final String aID) {
		this.id = aID;
	}

	/**
	 * Sets the name of this node.
	 *@return previous name of this node. */
	public String setName(String name) {
		final String old = this.name;
		this.name = name;
		return old;
	}

	/**
	 * Returns the name of this node.
	 *@return the name of this node. */
	public String getName() {
		return this.name;
	}

	/**
	 * Clonning.
	 */
	public Object clone() {
		try {
			final Node n = ((Node) super.clone());
			n.id = Node.newUniqueID();
			n.name = "_" + n.id;
			return n;
		} catch (CloneNotSupportedException cne) {
			cne.printStackTrace();
			return null;
		}
	}

	/**
	 * Generates a new unique identifier for this node.
	 *@return new unique ID.
	 */
	public static String newUniqueID() {
		newID++;
		checkMax();
		return "id" + newID;
	}

	private static void checkMax() {
		if (newID == Long.MAX_VALUE)
			throw new RuntimeException(
				"Problem with the Unique Identifier. You have reached Long.MAX_VALUE limit. Congratulations!\n"
					+ "Send your application description to: mariusz@rakiura.org and we will help.");
	}

} // Node
//////////////////// end of file ////////////////////
