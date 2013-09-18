/* Copyright (C) 1998 Graham Kirby
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Library General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Library General Public License for more details.
 * 
 * To receive a copy of the GNU Library General Public License, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
 * USA.
 */

package org.rakiura.compiler;

/**
 * Provides an interface for classes that load class byte code from various sources such
 * as the file system or the network.<P>
 *
 * The <A HREF="../../compiler/ClassByteLoader.java">source code</A> is available.
 *
 * @author	Graham Kirby (<A HREF="mailto:graham@dcs.st-and.ac.uk">graham@dcs.st-and.ac.uk</A>)
 * @version 1.2 2-Nov-98
 */
public interface ClassByteLoader {

	/**
	 * Returns the byte code for the given class.
	 *
	 * @param name	a string specifying the fully qualified name of the class to be loaded
	 * @return		an array containing the byte code for the given class, or null if it is not found
	 */
	public byte[] get (String name);
}
