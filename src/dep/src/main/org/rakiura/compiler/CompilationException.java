
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
 * Signals an error during dynamic compilation.<P>
 *
 * The <A HREF="../../compiler/CompilationException.java">source code</A> is available.
 *
 * @author	Graham Kirby (<A HREF="mailto:graham@dcs.st-and.ac.uk">graham@dcs.st-and.ac.uk</A>)
 * @version 1.2 2-Nov-98
 */
public class CompilationException extends Exception {

	/**
	 * Constructs a CompilationException with the specified detail message.
	 * A detail message is a String that describes this particular exception. 
	 *
	 * @param s		the detail message
	 */
	public CompilationException( String s ) { super( s ); }
}
