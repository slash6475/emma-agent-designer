// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

/**
 * A Dialog Box to select a transition of a subnet that an arc can connect to.
 * <p>Description: A Dialog Box to select a transition of a subnet that an arc can connect to.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NZDIS</p>
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.7 $
 *@since 3.0
 */

public class SelectTransitionInSubNetDialog extends JDialog {

	private static final long serialVersionUID = 3832906559079789616L;
	
	private JPanel panel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JList jTransitionList = new JList();
	private JButton okButton = new JButton();
	private int selection;

	public SelectTransitionInSubNetDialog(List transitions) {
		super((JFrame)null, "Select Transition to Connect to:", true);
		this.jTransitionList.setListData(transitions.toArray());
		try {
			jbInit();
			pack();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.panel1.setLayout(this.borderLayout1);
		this.okButton.setToolTipText("");
		this.okButton.setActionCommand("okButton");
		this.okButton.setText("Select");
		this.okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		this.jTransitionList.setBorder(BorderFactory.createEtchedBorder());
		this.jTransitionList.setToolTipText("Select the Transition to Connect the Arc With");
		this.jTransitionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().add(this.panel1);
		this.panel1.add(this.jTransitionList, BorderLayout.CENTER);
		this.panel1.add(this.okButton, BorderLayout.SOUTH);
	}

	void okButton_actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
		this.selection = this.jTransitionList.getSelectedIndex();
		this.setVisible(false);
	}

	public int getSelectedIndex() {
		return this.selection;
	}
}