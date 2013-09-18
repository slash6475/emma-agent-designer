// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.draw.Command;
import org.rakiura.draw.Drawing;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.basic.BasicDrawingView;
import org.rakiura.draw.basic.DrawingViewFrame;
import org.rakiura.draw.command.BringToFrontCommand;
import org.rakiura.draw.command.SendToBackCommand;
import org.w3c.dom.Document;


/**
 * Represents a view, a visualization of a single Net.
 *
 *<br><br>
 * NetViewer.java<br>
 * Created: Mon Apr 15 14:43:46 2002<br>
 *
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@author <a href="phwang@infoscience.otago.ac.nz">Peter Hwang</a>
 *@author <a href="mfleurke@infoscience.otago.ac.nz">Martin Fleurke</a>
 *@version $Revision: 1.68 $ $Date: 2007/03/18 03:32:21 $
 *@since 2.0
 */
public class NetViewer extends BasicDrawingView implements ActionListener {

	private static final long serialVersionUID = 3257850986847679027L;
	
	/** Initial width of this view. */
	@SuppressWarnings("hiding")
	public static int WIDTH = 300; //590;
	/** Initial height of this view. */
	@SuppressWarnings("hiding")
	public static int HEIGHT = 300; //520;

	/** Randomizer. Shared by different CPN Figures. */
	public static final Random random = new Random(System.currentTimeMillis());

	final static boolean SAVE = true;
	final static boolean OPEN = false;

	private final static String SELECT_LAYOUT = "selectLayout";
	private final static String LOAD_LAYOUT = "loadLayout";
	private final static String SAVE_LAYOUT = "saveLayout";
	private final static String RANDOM_LAYOUT = "randomlayout";

	JPopupMenu popup;
	private ButtonGroup group = new ButtonGroup();

	public NetViewer() {
		super(WIDTH, HEIGHT);
		createPopup();
	}
	
	/**
	 * Creates a netfigure view from the drawing. The drawing is not laid-out
	 * in any way. If you want a netdrawing view with proper lay-out, use #NetViewer(Net)
	 * @param aDrawing the drawing to be viewed by this viewer.
	 * @see #NetViewer(Net)
	 */
	public NetViewer(final Drawing aDrawing) {
		this();
		setDrawing(aDrawing);
	}

	/**
	 * Creates a netfigure view that is laid out according to the
	 * lay-out-file-references in the net.
	 * @param aNet the net to create a Drawing+view for.
	 */
	public NetViewer (Net aNet) {
		this();
		init (aNet);
	}

	/**
	 * Creates a netfigure view that is laid out according to the layout document.
	 * @param aNet the net to be drawn
	 * @param aLayout the layout to be used
	 */
	public NetViewer (Net aNet, Document aLayout) {
		this();
		NetDrawing d = new NetDrawing();
		d.setNet (aNet);
		final String errors =	XMLLayoutManager.loadLayout(d, aLayout);
		if (!errors.trim().equals("")) {
			int option = JOptionPane.showConfirmDialog(this, "Layout contains errors. Use layout anyway?", "WARNING: BAD LAYOUT", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if (  option == JOptionPane.YES_OPTION ) { //yes
				JOptionPane.showMessageDialog(this, "Error when applying layout:\n" +
					errors,	"Applying layout: Error", JOptionPane.ERROR_MESSAGE);
			} else {
				d = new NetDrawing(aNet);
				randomLayout(d);
			}
		}
	}

	/**
	 * initializes the viewer by drawing the net according to the layout files
	 * referenced in the net.
	 * @param aNet the net to be drawn.
	 */
	private void init (final Net aNet) {
		createPopup();
		if (aNet.getLayouts().length == 0) {	
			setDrawing(new NetDrawing(aNet));
			return;
		}
		final String[] layouts = aNet.getLayouts();
		NetDrawing d = new NetDrawing();
		d.setNet (aNet);
		for (int i = 0; i < layouts.length; i++) { // add all layouts to the popup menu.
			addGroupButton(layouts[i]);
		}
		for (int i = 0; i < layouts.length; i++) { // find one which works, hopefully the first one!
			try {
				final String errors =
							 XMLLayoutManager.loadLayout(d, new File(aNet.getNetDir(), layouts[i]));
				if (errors.trim().equals("")) {
					setDrawing(d);
					return;
				} else {
					int option = JOptionPane.showConfirmDialog(this,
							"Layout contains errors. Use layout anyway?\n(press 'no' to try an other layout,"+
							"\n'cancel' if you don't want to load any layout", "WARNING: BAD LAYOUT", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
					if (  option == JOptionPane.YES_OPTION ) { //yes
						JOptionPane.showMessageDialog(this, "Error when loading layout from XML file: "+ layouts[i] + "\n" + errors,
								"Loading layout: Error", JOptionPane.ERROR_MESSAGE);
					} else if (option == JOptionPane.CANCEL_OPTION) { //cancel
						d = new NetDrawing(aNet);
						randomLayout(d);
						break;
					} else { //no
						aNet.removeLayout(layouts[i]);
						d = new NetDrawing(aNet);
						randomLayout(d);
						continue;
					}
				}
			}catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error when loading layout from XML file: "+ layouts[i] + "\n" + e.getMessage() +"\nLayout removed from net",
																			"Loading layout: Error", JOptionPane.ERROR_MESSAGE);
				aNet.removeLayout(layouts[i]);
			}
		}
	}

	/**
	 * Handles key down events. Cursor keys are handled
	 * by the view the other key events are delegated to the
	 * currently active tool.<br>
	 * Current active keys: <br>
	 * &lt;ctrl&gt;+[ : Send to back<br>
	 * &lt;ctrl&gt;+] : Bring to front<br>
	 * &lt;ctrl&gt;+a : select all<br>
	 * @param e the Key Event
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if ((code == KeyEvent.VK_CLOSE_BRACKET) && (e.isControlDown()) ){
			Command cmd = new BringToFrontCommand("Bring to Front", editor());
			cmd.actionPerformed(new ActionEvent(this, 0, ""));
		} else if ((code == KeyEvent.VK_OPEN_BRACKET) && (e.isControlDown()) ){
			Command cmd = new SendToBackCommand("SendToBack", editor());
			cmd.actionPerformed(new ActionEvent(this, 0, ""));
		} else if ((code == KeyEvent.VK_A) && (e.isControlDown()) ){
			FigureEnumeration fe = drawing().figures();
			while (fe.hasMoreElements()) {
				addToSelection(fe.nextFigure());
			}
		} else
			super.keyPressed(e);
	}

	/**
	 * Creates a Frame with the NetViewer as the only component inside and returns it.
	 * Note: if you intend to give this NetViewer a different editor
	 * and/or use the editor for other DrawingViews, make sure you keep a handle
	 * to this NetViewer and that you are notified
	 * when the frame is disposed, so you can call deReference() of the NetViewer.
	 * You can add a {@link org.rakiura.draw.basic.DrawingViewFrameListener} to
	 * the frame to be sure of de- and rereferencing, with
	 * <code>aFrame.addWindowListener(new DrawingViewFrameListener(DrawingView));</code>
	 * @return A JFrame with the NetViewer inside.
	 */
	public JFrame getFrame() {
		final JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.pack();
		return frame;
	}

	/**
	 * Creates an InternalFrame with the NetViewer as the only component inside
	 * and returns it.
	 * Note: You should add an {@link org.rakiura.draw.basic.InternalDrawingViewFrameListener InternalDrawingViewFrameListener}
	 * to the frame so the drawing is properly dereferenced when it is not used
	 * anymore.
	 * @return A JInetrnalFrame with the NetViewer inside.
	 * @see org.rakiura.draw.basic.InternalDrawingViewFrameListener
	 */
	public JInternalFrame getInternalFrame() {
		return new DrawingViewFrame(this);
	}

	/**
	 * Creates a popup menu for the drawing to load/save layouts
	 */
	void createPopup() {
		this.popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Load Net layout");
		menuItem.addActionListener(this);
		menuItem.setActionCommand(LOAD_LAYOUT);
		this.popup.add(menuItem);
		menuItem = new JMenuItem("Save Net layout");
		menuItem.addActionListener(this);
		menuItem.setActionCommand(SAVE_LAYOUT);
		this.popup.add(menuItem);
		menuItem = new JMenuItem("Random Net layout");
		menuItem.addActionListener(this);
		menuItem.setActionCommand(RANDOM_LAYOUT);
		this.popup.add(menuItem);
		this.popup.addSeparator();

		//Add listener to components that can bring up popup menus.
		MouseListener popupListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					NetViewer.this.popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
		this.addMouseListener(popupListener);
	}

	void addGroupButton (final String fileName) {
		final JRadioButtonMenuItem rbMenuItem =
			new JRadioButtonMenuItem(fileName);
		this.group.add(rbMenuItem);
		this.popup.add(rbMenuItem);

		final ButtonModel m = this.group.getSelection();
		if (m != null) {
			this.group.setSelected(m, false);
		}
		this.group.setSelected(rbMenuItem.getModel(), true);
		rbMenuItem.setActionCommand(SELECT_LAYOUT);
		rbMenuItem.addActionListener(this);
	}

	/** Action listener handler.
	 * @param e the action event. Should contain a ..._LAYOUT command.
	 * According to the command, a lay-out is saved/loaded/created.
	 */
	public void actionPerformed(ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals(LOAD_LAYOUT)) {
			loadLayout();
		} else if (command.equals(SAVE_LAYOUT)) {
			saveLayout();
		} else if (command.equals(SELECT_LAYOUT)) {
			loadLayout (((AbstractButton) e.getSource()).getText());
		} else if (command.equals(RANDOM_LAYOUT)) {
			randomLayout();
		}
	}

	/**
	* Loads previously saved layout and makes it active. */
	public void loadLayout() {
		final File file = chooseFile(OPEN);
		if (file == null) return;
		((NetDrawing) drawing()).getNet().setNetDir(file.getParentFile());
		loadLayout(file.getName());
	}

	/**
	* Loads previously saved layout and makes it active. Note, this method expect
	* a name of the layout file relative to the current working directory, see
	* getTopDir() method.
	* @param layoutFile relative layout file name.
	* */
	public void loadLayout (final String layoutFile) {
		try {
			NetDrawing newDrawing = new NetDrawing (NetGenerator.INSTANCE.getNetInstance());
			final Net net = ((NetDrawing) drawing()).getNet();
			newDrawing.setNet (net);
			final String errors =
					XMLLayoutManager.loadLayout (newDrawing, new File (net.getNetDir(), layoutFile));
			if (!errors.trim().equals("")) {
				int option = JOptionPane.showConfirmDialog(this, "Layout contains errors. Use layout anyway?", "WARNING: BAD LAYOUT", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				if (  option == JOptionPane.YES_OPTION ) {
					JOptionPane.showMessageDialog(this,
										"Error when loading layout from XML file: "+ layoutFile + "\n" + errors,
										"Loading layout: Error", JOptionPane.ERROR_MESSAGE);
				} else {
					((NetDrawing) drawing()).getNet().removeLayout(layoutFile);
					return;
				}
			}
			newDrawing.getNet().addLayout (layoutFile);
			newDrawing.setFile(drawing().getFile());
			setDrawing(newDrawing); //sets the size of this netviewer according to the drawing
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
							"Error when loading layout from XML file: "+ layoutFile + "\n" + e.getMessage(),
							"Loading layout: Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace() ;
			((NetDrawing) drawing()).getNet().removeLayout(layoutFile);
		}
	}

	/**
	 * Saves the current net layout to a file. */
	public void saveLayout() {
		File file = chooseFile(SAVE);
		if (file == null) return;
		if (!FILTER.accept(file)) {
			file = new File(file.getAbsolutePath() + ".jlf");
		}
		((NetDrawing) drawing()).getNet().setNetDir(file.getParentFile());
		saveLayout (file.getName());
	}

	/**
	 * Saves the current net layout to a file.
	 * @param layoutFile  relative layout file name.
	 */
	public void saveLayout(final String layoutFile) {
		try {
			final Net net = ((NetDrawing) drawing()).getNet();
			final String errors = XMLLayoutManager.saveLayout(drawing(), new File (net.getNetDir(), layoutFile));
			if (!errors.trim().equals("")) {
				JOptionPane.showMessageDialog( this, "Error when saving layout to XML file: "+ layoutFile + "\n" + errors, "Saving layout: Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				net.addLayout (layoutFile);
			}
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog( this, "Error when saving layout to XML file: "+ layoutFile + "\n" + ioe.getMessage(), "Saving layout: Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** File filter for the layout files. */
	private static final FileFilter FILTER = new FileFilter() {
		/**
		 * @param aFile file to be tested
		 * @return <code>true</code> when the file name ends with jlf
		 */
		public boolean accept(final File aFile) {
			if (aFile.isDirectory() || aFile.getName().endsWith(".jlf")) {
				return true;
			}
			return false;
		}
		/** @return filter description */
		public String getDescription() {
			return "JFern Layout files";
		}
	};

	/**
	* Shows the file chooser and allows the user to choose a file.
	* The filechooser will be the editors' drawingapplications' filechooser if present.
	* The default directory will be set to the directory of the drawing's file if present, otherwise to the user dir.
	* @param isSave Should the file selection dialog have a load or save button?
	* @return the chosen file
	*/
	private File chooseFile(final boolean isSave) {
		JFileChooser chooser;
		try {
			chooser = this.editor().getOwner().chooser;
			chooser.setCurrentDirectory(this.drawing().getFile().getParentFile());
		} catch (Exception e) {
			File f = this.drawing().getFile();
			if (f != null) {
				chooser = new JFileChooser(this.drawing().getFile().getParentFile());
			} else chooser = new JFileChooser();
		}
		chooser.addChoosableFileFilter(FILTER);
		int returnVal;
		if (isSave) {
			chooser.setDialogTitle("Saving Layout File");
			returnVal = chooser.showSaveDialog(this);
		} else {
			chooser.setDialogTitle("Opening Layout File");
			returnVal = chooser.showOpenDialog(this);
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Puts all figures randomly on the drawing. Useful if you want to display a
	 * net that has no layout.
	 */
	public void randomLayout() {
		randomLayout(drawing());
	}

	public static void randomLayout(Drawing aDrawing) {
		FigureEnumeration fe = aDrawing.figures();
		while (fe.hasMoreElements()) {
			Figure f = fe.nextFigure();
			if (f instanceof CPNPlaceFigure || f instanceof CPNTransitionFigure || f instanceof CPNSubNetFigure ) {
				Point p = f.center();
				f.moveBy(random.nextInt(350)-p.x, random.nextInt(300)-p.y);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deReference(){
		Drawing d = this.drawing();
		if (d instanceof NetDrawing) {
			((NetDrawing)d).deReference();
		}
		super.deReference();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reReference() {
		Drawing d = this.drawing();
		if (d instanceof NetDrawing) {
			((NetDrawing)d).reReference();
		}
		super.reReference();
	}

} // NetViewer
//////////////////// end of file ////////////////////