//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import org.rakiura.cpn.Version;

/**
 * About box for JFern Toolbox.
 * 
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
class AboutBox extends JInternalFrame implements ActionListener {

  private static final long serialVersionUID = 3762537793427093045L;
	
  private static AboutBox instance = new AboutBox();

  /**
   * Returns a single instance of the AboutBox.
   */
  public static AboutBox getInstance(){
		return instance;
  }

  /**
   */
  AboutBox(){
		super("About JFern Toolbox", false, true, false, false);
		setLocation(50, 50);

		final JPanel pane = new JPanel();
		final BoxLayout grid = new BoxLayout(pane, BoxLayout.Y_AXIS);
		pane.setLayout(grid);
		pane.setBackground(Color.black);
		final JLabel lblImage = new JLabel(getImage("jfernbanner.jpg"));
		pane.add(lblImage);

		final JLabel lblText1 = new JLabel("JFern Toolbox", javax.swing.SwingConstants.CENTER);
		lblText1.setForeground(Color.yellow); 
		lblText1.setMaximumSize(lblImage.getMaximumSize());
		pane.add(lblText1);

		final JLabel lblText0 = new JLabel(" 3.0.0 alpha    (June 2003)", 
		                                                               javax.swing.SwingConstants.CENTER);
		lblText0.setMaximumSize(lblImage.getMaximumSize());
		lblText0.setForeground(Color.white);
		pane.add(lblText0);

		pane.add(new JLabel(" "));

		JLabel lblText2 = new JLabel("Copyright (C) 1998-2003 by Mariusz Nowostawski and others.",
		                                                        javax.swing.SwingConstants.CENTER);
		lblText2.setForeground(Color.lightGray);
		lblText2.setMaximumSize(lblImage.getMaximumSize());
		pane.add(lblText2);
		lblText2 = new JLabel("Special thanks to Martin Fleurke for his work on JFern Toolbox.",
		                                          javax.swing.SwingConstants.CENTER);
		lblText2.setForeground(Color.lightGray);
		lblText2.setMaximumSize(lblImage.getMaximumSize());
		pane.add(lblText2);
		pane.add(new JLabel ("  "));
		final JLabel lblText3 = new JLabel("Website:  http://www.sf.net/projects/jfern",
		                                                                javax.swing.SwingConstants.CENTER);
		lblText3.setForeground(Color.lightGray);
		lblText3.setMaximumSize(lblImage.getMaximumSize());
		pane.add(lblText3);

		getContentPane().add(pane);
		validate();
		pack();
  }

  public void actionPerformed(ActionEvent e) {
		this.dispose();
  }


  /**
   * Reads and returns an resource image.
   */
  public static ImageIcon getImage(String afile) {
		//byte[] buffer = null;
		final String file = "/org/rakiura/cpn/gui/image/" + afile;
		URL iconURL = AboutBox.class.getResource(file);
		if (iconURL != null) {
	  	  return new ImageIcon(iconURL);
		}
		System.err.println("Error: getImage()  "+file + " not found.");
	  	return null;
  }
}
