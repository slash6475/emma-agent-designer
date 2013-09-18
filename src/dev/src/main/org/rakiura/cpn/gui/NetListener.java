// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.util.EventListener;
import org.rakiura.cpn.gui.NetEvent;

/**
 * Represents a net listener.
 *
 *<br><br>
 * NetListener.java<br>
 *
 *@author  <a href="mfleurke@infoscience.otago.ac.nz">Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.1 $
 */
public interface NetListener extends EventListener {

	void notify(final NetEvent anEvent);
}