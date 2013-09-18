// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.draw.Command;
import org.rakiura.draw.Drawing;
import org.rakiura.draw.command.DeleteCommand;

/**
 * Represents an editable view, a visualization of a single Net.
 * This view extends the {@link NetViewer NetViewer} and provides
 * some extra editing capabilities not present in its super class, 
 * i.e. handling <code>Delete</code> command. Pop-up menu is
 * not handled here - loading and saving layouts should be done via menu
 * of the editor.
 *
 *<br><br>
 * NetEditViewer.java<br>
 * Created: Fri May 2 14:35:46 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version $Revision: 1.16 $ $Date: 2007/01/10 03:58:08 $
 *@since 3.0
 */
public class NetEditViewer extends NetViewer implements ActionListener {


	private static final long serialVersionUID = 3546081371606299705L;

	/** Creates new NetEditViewer with newly created empty Net. */
	public NetEditViewer() {
	}

	/** Creates new NetEditViewer with a given drawing. */
	public NetEditViewer(final Drawing aDrawing) {
		super(aDrawing);
	}

	/**
	 * Creates a new editor viewer with a given net.
	 */
	public NetEditViewer(final Net aNet) {
		super(aNet);
	}

	/**
	 * Handles key down events. Cursor keys are handled
	 * by the view the other key events are delegated to the
	 * currently active tool.
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if ((code == KeyEvent.VK_BACK_SPACE) || (code == KeyEvent.VK_DELETE)) {
			Command cmd = new DeleteCommand("Delete", editor());
			cmd.actionPerformed(new ActionEvent(this, 0, ""));
		} else
			super.keyPressed(e);
	}

	//Don't create a popup window.
	void createPopup() {
		/* empty */
	}
	
	void addGroupButton(final String fileName) {
		/* empty */
	}

} // NetEditViewer
//////////////////// end of file ////////////////////
