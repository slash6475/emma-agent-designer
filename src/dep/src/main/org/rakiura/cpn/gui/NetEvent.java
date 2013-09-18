// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.util.EventObject;

/**
 * Represents a (Sub)Net event.
 * Fired whe e.g. the net gets first tokens, when 
 * transition is active/inactive.
 *
 *@author  <a href="mfleurke@infoscience.otago.ac.nz">Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.3 $
 */
public class NetEvent extends EventObject {


	private static final long serialVersionUID = 4049077124872746801L;

	public NetEvent(final NetDrawing aNetDrawing) {
		super(aNetDrawing);
	}

	public NetDrawing getNetDrawing() {
		return (NetDrawing) this.getSource();
	}

} // NetEvent
//////////////////// end of file ////////////////////