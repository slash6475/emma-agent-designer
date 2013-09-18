// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import kawa.standard.Scheme;

import org.rakiura.compiler.CompilationException;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.draw.Drawing;
import org.rakiura.draw.DrawingEditor;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.basic.DrawingApplication;

/**
 * Implements CPN editor and simulation toolbox. This editor allows the user to
 * design Petri Net, dump it into XML, compile it into executable
 * Fernlet, and run the compiled net in the debugging window (see
 * ({@link NetViewer}.
 *
 *<br><br>
 * NetToolboxApplication.java<br>
 * Created: Thu Apr 18 13:59:47 2002<br>
 *
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@author <a href="phwang@infoscience.otago.ac.nz">Peter Hwang</a>
 *@author <a href="martinfleurke@users.sf.net">Martin Fleurke</a>
 *@version 4.0.0 $Revision: 1.57 $ $Date: 2007/05/30 23:28:39 $
 *@since 3.0
 */
public class NetToolboxApplication extends DrawingApplication  {


	private static final long serialVersionUID = 3256725069777810998L;

	/**/
	public NetToolboxApplication(final String aTitle) {
		super(aTitle);
		getCurrentEditor().selectionChanged (null);
		this.validate();
	}


	/**
	 * Opening an existing net.
	 */
	public void onOpen() {
		final File file = getOpenFile();
		if (file == null) return;
		NetEditViewer nev;
		if (SER_FILTER.accept(file)) {
			final Drawing d = readFromObjectInput (file);
			if (d == null) {
				//error has occured and has been shown
				System.err.println("[ERROR] Failed loading " + file);
				return;
			}
			nev = new NetEditViewer (d);
			nev.drawing().setName(file.getName());
			nev.drawing().setFile(file);
		} else if(JLF_FILTER.accept(file)) {
			final NetDrawing d = new NetDrawing();//NetGenerator.INSTANCE.getNetInstance());
			String error = XMLLayoutManager.loadDrawingfromLayout(d, file);
			if (error.length() > 0 ) {
				JOptionPane.showInternalMessageDialog(
						getDesktop(),	"Error when creating net from layout file: "+ file + "\n" + error,
					"Re-creating net error", JOptionPane.ERROR_MESSAGE);
			}
			nev = new NetEditViewer (d);
		} else {
			// assume XML net file then.
			try {
				nev = new NetEditViewer ( NetGenerator.createNet(file) );
				nev.drawing().setFile(file);
			} catch (CompilationException ce) {
				String javaSource = NetGenerator.createJavaSource (file);
				CompilationExceptionInternalFrame.handle (getDesktop (),
												"Error!  Compiling:  " + file, javaSource, ce);
				return;
			} catch (Exception e) {
				JOptionPane.showInternalMessageDialog(
					getDesktop(),	"Error when loading net from XML file: "+ file + "\n" + e.getMessage(),
					"Loading net: Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace ();
				return;
			}
		}
		//replace the editor of the viewer with the shared editor of this DrawingApplication
		nev.setEditor(getCurrentEditor());
		//enable the tools if this is the first view to use the editor
		getCurrentEditor().setCurrentView (nev);
		//add the view to a frame, make sure someone listens for the frame to be
		//closed to do the deReferencing and add the frame to the desktop
		final JInternalFrame frame = nev.getInternalFrame();
		addInternalFrame (nev, frame);
	}

	/**
	 * Opens a new DrawingViewFrame.
	 */
	public void onOpenNew() {
		NetEditViewer editViewer = new NetEditViewer (NetGenerator.INSTANCE.getNetInstance());
		editViewer.setEditor (getCurrentEditor());
		JInternalFrame window = editViewer.getInternalFrame ();
		addInternalFrame (editViewer, window);
		getCurrentEditor().setCurrentView (editViewer);
		try {
			window.setMaximum(true);
		} catch (PropertyVetoException ex) {/* ignore */}
	}

	public void onSave (final DrawingView aView) {
		if (aView.drawing().getFile().getName().endsWith("ser")) {
			saveAsObjectOutput (aView.drawing().getFile(), aView.drawing());
		} else {
			final String[] l = ((NetDrawing) aView.drawing()).getNet().getLayouts();
			if (l.length > 0) {
				((NetViewer) aView).saveLayout(l[0]);
			} else {
				((NetViewer) aView).saveLayout();
			}
			saveToXML(aView.drawing().getFile(), aView);
		}
	}

	public void onSaveAs (final DrawingView aView) {
		this.chooser.setSelectedFile(null);
		final File file = getSaveFile ();
		if (file != null) {
			aView.drawing().setFile(file);
			// this is ugly, needs to be fixed somehow
			getDesktop().getSelectedFrame().setTitle(file.getName() + "    [" + file + "]");
			// save the file in appropriate file format
			if (file.getName().endsWith(".ser"))
				saveAsObjectOutput(file, aView.drawing());
			else {
				final String[] l = ((NetDrawing) aView.drawing()).getNet().getLayouts();
				if (l.length > 0) {
					((NetViewer) aView).saveLayout(l[0]);
				} else {
					((NetViewer) aView).saveLayout();
				}
				saveToXML (file, aView);
			}
		}
	}

	protected DrawingEditor createEditor () {
		return new NetEditor (this);
	}

	protected File getOpenFile () {
		this.chooser.resetChoosableFileFilters();
		this.chooser.setDialogTitle("Open Drawing");
		this.chooser.addChoosableFileFilter(SER_FILTER);
		this.chooser.addChoosableFileFilter(JLF_FILTER);
		this.chooser.addChoosableFileFilter(XML_FILTER);
		this.chooser.setSelectedFile(null);
		int returnVal = this.chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return this.chooser.getSelectedFile();
		}
		return null;
	}

	protected File getSaveFile() {
		this.chooser.resetChoosableFileFilters();
		this.chooser.addChoosableFileFilter(SER_FILTER);
		this.chooser.addChoosableFileFilter(XML_FILTER);
		this.chooser.setDialogTitle("Save Drawing as...");
		int returnVal = this.chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = this.chooser.getSelectedFile();
			if (this.chooser.getFileFilter() == SER_FILTER && !file.getName().endsWith("ser")) {
				try {
					file = new File(file.getCanonicalPath()+".ser");
					return file;
				} catch (IOException e) {
					JOptionPane.showInternalMessageDialog(this, e.getMessage(), "Error when saving", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			} else if (!XML_FILTER.accept(file)) {
				try {
					if (!file.getCanonicalPath().endsWith(".jnf")) {
						file = new File(file.getCanonicalPath() + ".jnf");
					} else {
						file = new File(file.getCanonicalPath());
					}
				} catch (IOException e) {
					JOptionPane.showInternalMessageDialog(this, e.getMessage(), "Error when saving", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				return file;
			} else return file;
		}
		return null;
	}


	/** File filter for the net files. */
	private static final FileFilter XML_FILTER = new FileFilter() {
		public boolean accept(final File aFile) {
			if (aFile.isDirectory()
				|| aFile.getName().toLowerCase().endsWith(".xrn")
				|| aFile.getName().toLowerCase().endsWith(".xml")
				|| aFile.getName().toLowerCase().endsWith(".jnf")) {
				return true;
			}
			return false;
		}
		public String getDescription() {	return "JFern XML files (*.xml, *.jnf)"; }
	};

	private static final FileFilter SER_FILTER = new FileFilter() {
		public boolean accept(final File aFile) {
			if (aFile.isDirectory()
				|| aFile.getName().toLowerCase().endsWith(".ser")) {
				return true;
			}
			return false;
		}
		public String getDescription() {	return "Drawing serialized files (*.ser)"; }
	};

	private static final FileFilter JLF_FILTER = new FileFilter() {
		public boolean accept(final File aFile) {
			if (aFile.isDirectory()
				|| aFile.getName().toLowerCase().endsWith(".jlf")) {
				return true;
			}
			return false;
		}
		public String getDescription() { return "Net layout files (*.jlf)"; }
	};



	private void saveToXML(File file, DrawingView aView) {
		Net net = ((NetDrawing) aView.drawing()).getNet ();
		if (net == null)  {
			aView.editor().showStatus ("Saving to XML aborted, Net is empty!!");
			return;
			}
		String log = "";
		aView.editor().showStatus ("Saving to XML file...");

		aView.editor().showStatus ("Saving to XML file '" + file.getName() + "'");
		log = log + "\n" + "Saving to XML file '" + file.getName() + "'";
		try {
			NetGenerator.saveAsXML(net, file);
			aView.editor().showStatus ("Done.");
		} catch (IOException ex) {
			aView.editor().showStatus ("Saving to XML file failed");
			log = log + "\n" + ex.getMessage();
			JOptionPane.showInternalMessageDialog(
				getDesktop(), log
						+ "\n\n"
						+ "Error when saving net to XML file: "
						+ file
						+ "\n"
						+ ex.getMessage(),
					"Saving net: Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}



	public static void main(String[] args){
		new NetToolboxApplication("JFern Net Toolbox");
	}


} // NetToolboxApplication