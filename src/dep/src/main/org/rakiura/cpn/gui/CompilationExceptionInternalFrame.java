//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.rakiura.draw.LineNumbersDecorator;

/**
 * Internal frame for compilation exceptions.
 * Represents a frame for displaying exceptions and problems when compiling
 * JFern net from Java sources.
 *
 * <br><br>
 * Created on 20/05/2003<br>
 *
 *@author Mariusz Nowostawski
 *@version 4.0.0 $Revision: 1.8 $
 *@since 3.0
 */
class CompilationExceptionInternalFrame extends JInternalFrame {


	private static final long serialVersionUID = 3258689927171486771L;

	/**
	 * Returns a frame to show source code in, with line numbers shown.
	 * @param aTitle The title to display for the frame
	 * @param aSource the string/ source code to display in the frame.
	 */
	private CompilationExceptionInternalFrame (String aTitle, String aSource) {
		super (aTitle, true, true, true, true);

		final JTextArea text = new JTextArea(aSource, 25, 50);
		final JScrollPane pane = new JScrollPane(text);
		final LineNumbersDecorator lineNumbers = new LineNumbersDecorator(text);
		pane.setRowHeaderView(lineNumbers);
		getContentPane().add(pane);
		pack();
	}

	/**
	 * Returns an internal frame to show an exception and the sourcecode that
	 * caused it with it in a split pane. Line numbers are added as well to
	 * easily backtrace the error.
	 * @param aTitle The title to display for the frame
	 * @param aSource the string/text to display with linenumbers
	 * @param anException the exception that was caused by the source and should
	 * be displayed in the frame as well
	 * @param displayFullException display the stacktrace of the exception also, or just the message?
	 */
	private CompilationExceptionInternalFrame (String aTitle, String aSource,
			Exception anException, boolean displayFullException) {
		super (aTitle, true, true, true, true);

		final JTextArea text = new JTextArea(aSource, 25, 50);
		final JScrollPane pane = new JScrollPane(text);
		final LineNumbersDecorator lineNumbers = new LineNumbersDecorator(text);
		pane.setRowHeaderView(lineNumbers);

		StringWriter out = new StringWriter ();
		if (displayFullException) anException.printStackTrace (new PrintWriter (out));
		else out.write(anException.getMessage());
		final JTextArea msg = new JTextArea (out.getBuffer().toString(), 15, 50);
		msg.setFont (new Font ("Courier", Font.PLAIN, 12));
		final JScrollPane exPane = new JScrollPane (msg);
		JSplitPane main = new JSplitPane (JSplitPane.VERTICAL_SPLIT);
		main.setTopComponent(exPane);
		main.setBottomComponent(pane);
		getContentPane().add(main);
		pack();
	}

	/**
	 * Cretes an internal frame to show an exception and the sourcecode that
	 * caused it with it in a split pane. Line numbers are added as well to
	 * easily backtrace the error.
	 * @param aDesktop the desktop to add the internal frame to.
	 * @param aTitle The title to display for the frame
	 * @param aSource the string/text to display with linenumbers
	 * @param anException the exception that was caused by the source and should
	 * be displayed in the frame as well
	 */
	public static void handle (JDesktopPane aDesktop, String aTitle,
																									String aSource, Exception anException) {
		final JInternalFrame frame = new CompilationExceptionInternalFrame (
							aTitle, aSource, anException, true);
		aDesktop.add (frame);
		frame.setVisible (true);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {/* ignore */}
	}

	/**
	 * Cretes an internal frame to show an exception and the sourcecode that
	 * caused it with it in a split pane. Line numbers are added as well to
	 * easily backtrace the error.
	 * @param aDesktop the desktop to add the internal frame to.
	 * @param aTitle The title to display for the frame
	 * @param aSource the string/text to display with linenumbers
	 * @param anException the exception that was caused by the source and should
	 * be displayed in the frame as well
	 * @param displayFullException display the stacktrace of the exception also, or just the message?
	 */
	public static void handle (JDesktopPane aDesktop, String aTitle,
														 String aSource, Exception anException, boolean displayFullException) {
		final JInternalFrame frame = new CompilationExceptionInternalFrame (
				aTitle, aSource, anException, displayFullException);
		aDesktop.add (frame);
		frame.setVisible (true);
		try {
			frame.setSelected(true);
			} catch (java.beans.PropertyVetoException e) {/* ignore */}
	}

	/**
	 * Creates a frame to show source code in, with line numbers shown.
	 * @param aDesktop the desktop to add the internal frame to
	 * @param aTitle The title to display for the frame
	 * @param aSource the string/ source code to display in the frame.
	 */
	public static void handle (JDesktopPane aDesktop, String aTitle, String aSource) {
		final JInternalFrame frame = new CompilationExceptionInternalFrame (
											aTitle, aSource);
		aDesktop.add (frame);
		frame.setVisible (true);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) { /* ignore */}
	}


}
