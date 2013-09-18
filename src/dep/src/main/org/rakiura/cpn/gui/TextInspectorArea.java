//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Represents a net editor component for inspecting and editing text values.
 *
 * <br><br>
 * TextInspectorArea.java created on 29/05/2003 10:32:39<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.5 $
 */
class TextInspectorArea extends JPanel {

	private static final long serialVersionUID = 3907213758119162167L;
	
	JTextArea inspectText;
	TextInspectorCallback callback;
	private JLabel label;
	private JButton okButton;
	private JButton cancelButton;

	/**
	 * Creates and adds all the components of this text inspector panel.
	 */
	public TextInspectorArea () {
		setLayout (new BorderLayout ());
		JComponent t = createTextArea ();
		JComponent h = createHeader ();
		JComponent f = createFooter ();
		add (t, BorderLayout.CENTER);
		add (h, BorderLayout.NORTH);
		add (f, BorderLayout.SOUTH);
		makeDisabled ();
	}

	/**
	 * Sets a given callback with default label caption and OK button caption.
	 *
	 * @param c callback
	 */
	public void setCallback (final TextInspectorCallback c, final String initialText) {
		setCallback ("Edit text", "Set text", c, initialText);
	}

	/**
	 * Sets a given captions for label and OK button, and registers a given callback.
	 *
	 * @param aLabel label caption
	 * @param anApprove OK button caption
	 * @param c callback
	 */
	public void setCallback (final String aLabel, final String anApprove,
	                                             final TextInspectorCallback c, final String initialText) {
		this.callback = c;
		this.label.setText (aLabel);
		this.inspectText.setText (initialText);
		this.okButton.setText (anApprove);
		makeEnabled ();
	}

	public void makeDisabled () {
		this.inspectText.setText ("");
		this.label.setText ("Text inspector inactive");
		this.inspectText.setEditable (false);
		this.inspectText.setEnabled (false);
		this.label.setEnabled (false);
		this.cancelButton.setEnabled (false);
		this.okButton.setEnabled (false);
	}

	public void simulateOkButton () {
		if (this.okButton.isEnabled()) {
			this.okButton.doClick();
		}
	}



	private JComponent createHeader () {

		this.label = new JLabel ("Text inspector inactive");
		final JPanel p = new JPanel (new BorderLayout ());
		p.add (this.label, BorderLayout.WEST);
		return p;
	}

	private JComponent createFooter () {
		this.okButton = new JButton ("OK");
		this.okButton.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					if (TextInspectorArea.this.callback != null) {
						TextInspectorArea.this.callback.commit (TextInspectorArea.this.inspectText.getText());
						TextInspectorArea.this.callback = null;
						makeDisabled();
					}
				}
			});
		this.cancelButton = new JButton ("Cancel");
		this.cancelButton.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					if (TextInspectorArea.this.callback != null) {
						TextInspectorArea.this.callback.cancel ();
						TextInspectorArea.this.callback = null;
						makeDisabled();
					}
				}
			});
		final JPanel p = new JPanel (new BorderLayout ());
		final JPanel buttons = new JPanel ();
		buttons.add (this.okButton);
		buttons.add (this.cancelButton);
		p.add (buttons, BorderLayout.EAST);
		return p;
	}

	private JComponent createTextArea () {
		this.inspectText = new JTextArea(4, 50);
		this.inspectText.setEditable (false);
		this.inspectText.setEnabled (false);
		return new JScrollPane(this.inspectText);
	}

	private void makeEnabled () {
		this.inspectText.setEditable (true);
		this.inspectText.setEnabled (true);
		this.label.setEnabled (true);
		this.cancelButton.setEnabled (true);
		this.okButton.setEnabled (true);
		this.inspectText.requestFocusInWindow();
	}


}
