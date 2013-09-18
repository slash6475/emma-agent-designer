//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**
 * Represents a callback for the TextInspector component.
 * 
 * <br><br>
 * TextInspectorCallback.java created on 29/05/2003 12:33:54<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.1 $
 */
interface TextInspectorCallback {
	
	void cancel ();
	void commit (final String text);

}
