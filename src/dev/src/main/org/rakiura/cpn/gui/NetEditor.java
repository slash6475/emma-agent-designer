//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.cpn.Place;
import org.rakiura.draw.AbstractFigure;
import org.rakiura.draw.Command;
import org.rakiura.draw.Drawing;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.Tool;
import org.rakiura.draw.basic.BasicEditor;
import org.rakiura.draw.basic.DrawingApplication;
import org.rakiura.draw.command.ToolCommand;
import org.rakiura.draw.figure.EllipseFigure;
import org.rakiura.draw.figure.LineConnection;
import org.rakiura.draw.figure.LineFigure;
import org.rakiura.draw.figure.NodeFigure;
import org.rakiura.draw.figure.RectangleFigure;
import org.rakiura.draw.figure.RoundRectangleFigure;
import org.rakiura.draw.figure.TextFigure;
import org.rakiura.draw.tool.ConnectionTool;
import org.rakiura.draw.tool.CreationTool;

import bsh.Interpreter;


/**
 * Represents specialized editor for the CPN nets.
 *
 *@author <a href="mfleurke@infoscience.otago.ac.nz>Martin Fleurke</a>
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.68 $
 *@since 3.0
 */
public class NetEditor extends BasicEditor {
	
	TextInspectorArea inspectArea = new TextInspectorArea ();
	TextInspectorManager figureTextInspector = new TextInspectorManager (this);
	JTable figureAttributeTable;

	JTable tokenPropertyTable;
	private JList tokensList;
	JTree netTree;
	private JToggleButton defaultToolButton;

	Interpreter interpreter = new Interpreter ();

	private static String IMAGES = "/org/rakiura/cpn/gui/image/";

	/**
	 * {@inheritDoc}
	 * @param anOwner {@inheritDoc}
	 */
	public NetEditor(final DrawingApplication anOwner) {
		super(anOwner);
		setMenuFactory(new NetEditorMenuFactory (anOwner, this));
	}

	@SuppressWarnings("serial")
	public JToolBar createToolBar () {
		final JToolBar palette = new JToolBar ();
		final ButtonGroup group= new ButtonGroup ();
		palette.add (createToolbarTool (group, "Selection", "SEL1.gif", "SEL2.gif",
																		"SEL3.gif",	defaultTool()));
		palette.addSeparator();
		palette.add (createToolbarTool (group, "Arc", "arc.gif", "arc3.gif", "arc3.gif",
								 new ConnectionTool ("Arc Tool", new CPNArcFigure()) ) );
		palette.add (createToolbarTool (group, "Transition", "transition.gif", "transition3.gif", "transition3.gif",
								 new CreationTool ("Transition Creation Tool", new CPNTransitionFigure())));
		palette.add (createToolbarTool (group, "Place", "place.gif", "place3.gif", "place3.gif",
																		new CreationTool ("Place Creation Tool", new CPNPlaceFigure())));
		palette.add (createToolbarTool (group, "Net Declarations", "declaration.gif", "declaration3.gif", "declaration3.gif",
																		new Command ("Net Declaration", this) {
			protected void execute (DrawingView aView) {
				Drawing drawing = aView.drawing();
				if (drawing instanceof NetDrawing) {
					((NetDrawing)drawing).add(new CPNNetFigure(((NetDrawing)drawing).getNet()));
				}
			}
			public boolean isExecutable(DrawingView aView) {
				if (aView == null) return false;
				final FigureEnumeration fe = aView.drawing().figures();
				while (fe.hasMoreElements()) {
					Object nextElement = fe.nextElement();
					if (nextElement instanceof CPNNetFigure) {
						return false;
					}
				}
				return true;
			}
		})
		);
		palette.add (createToolbarTool (group, "SubNet", "subnet.gif", "subnet3.gif", "subnet3.gif",
																		new CreationTool ("Subnet Creation Tool", new CPNSubNetFigure(NetGenerator.INSTANCE.getNetInstance()))));
		palette.addSeparator();
		palette.add (createToolbarTool (group, "Text Figure", "TEXT1.gif", "TEXT2.gif", "TEXT3.gif",
																		new CreationTool ("TextFigure Tool", new TextFigure("Some Text goes here"))));
		palette.add (createToolbarTool (group, "Node Figure", "DIAMOND1.gif", "DIAMOND2.gif", "DIAMOND3.gif",
																		new CreationTool ("Node Tool", new NodeFigure())));
		palette.add (createToolbarTool (group, "Line", "LINE1.gif", "LINE2.gif", "LINE3.gif",
																		new CreationTool ("LineFigure Tool", new LineFigure())));
		palette.add (createToolbarTool (group, "Rectangle", "RECT1.gif", "RECT2.gif", "RECT3.gif",
																		new CreationTool("Rectangle Tool", new RectangleFigure())));
		palette.add (createToolbarTool (group, "Round Rectangle", "RRECT1.gif", "RRECT2.gif", "RRECT3.gif",
																		new CreationTool("Round Rectangle Tool", new RoundRectangleFigure())));
		palette.add (createToolbarTool (group, "Ellipse", "ELLIPSE1.gif", "ELLIPSE2.gif", "ELLIPSE3.gif",
																		new CreationTool("Ellipse Tool", new EllipseFigure())));
		palette.add (createToolbarTool (group, "Line Connection", "OCONN1.gif", "OCONN2.gif", "OCONN3.gif",
																		new ConnectionTool("LineConnection Tool", new LineConnection())));
		return palette;
	}

	/**
	 * Adds the right pane. This pane is dedicated to tokens handling.
	 */
	public JComponent createRightComponent() {
		final JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightPanel.setOneTouchExpandable(true);

		final JComponent listPanel = createTokensList ();
		final JComponent propertiesPanel = createTokenPropertyTable ();

		rightPanel.setTopComponent (listPanel);
		rightPanel.setBottomComponent (propertiesPanel);
		return rightPanel;
	}

	/**
	 * Adds the contents of the content pane. Override if you want your own layout.
	 * @return the Left Panel
	 */
	public JComponent createLeftComponent() {
		final JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftPanel.setOneTouchExpandable (true);

		final JComponent treePanel = createNetTree ();
		final JComponent attributesPanel = createAttributesTable ();

		leftPanel.setTopComponent (treePanel);
		leftPanel.setBottomComponent (attributesPanel);
		return leftPanel;
	}

	/**
	 * Returns the inspect area
	 * @return the inspect area
	 */
	public JComponent createBottomComponent() {
		return this.inspectArea;
	}

	/**
	 * Called when the selection has changed. The selected Figure is inspected.
	 * If there is a big or empty selection, the attributespanel is set to default.
	 * @param aView The view that has changed
	 */
	public void selectionChanged (DrawingView aView) {
		super.selectionChanged (aView);
		this.inspectArea.simulateOkButton();
		this.inspectArea.makeDisabled ();
		if (aView == null) return;
		updateState(aView);
	}

	public void setCurrentView (DrawingView newView) {
		super.setCurrentView(newView);
		updateNetTree();
	}

	/**
	 * Takes care of setting the tool to be used to the given tool.
	 * @param t the new tool.
	 */
	public void setTool(Tool t) {
		super.setTool(t);
		if (t == super.defaultTool()) {
			this.defaultToolButton.setSelected(true);
		}
	}

	/**
	 * removes the view from the set of drawingviews that this editor can edit.
	 * @param thisView the drawingview to be removed
	 */
	public void removeDrawingView(DrawingView thisView) {
		super.removeDrawingView(thisView);
		if (this.getCurrentView() == null) {
			updateNetTree();
		}
	}

	/**
	 *
	 * Inspects the figure.
	 * Called by a figure that wants to be inspected. If the figure type is known,
	 * the properties of the figure are displayed in the figureAttributeTable, and the node is
	 * selected in the net tree.
	 * @param aFigure the figure that needs inspection
	 */
	public void inspectFigure (final Figure aFigure) {
		if (aFigure instanceof TextFigure && ((TextFigure) aFigure).acceptsTyping()) {
			this.figureTextInspector.setFigure(aFigure);
			if (aFigure instanceof CPNAnnotationFigure) {
				this.inspectArea.setCallback (this.figureTextInspector,
																			((CPNAnnotationFigure)aFigure).getAnnotation());
			} else {
				this.inspectArea.setCallback (this.figureTextInspector,
																			((TextFigure) aFigure).getText());
			}
		}
	}

	/**
	 * Called when the selection has changed. The selected Figure is shown in the
	 * inspection panels.
	 * If there is a big or empty selection, the attributespanel is set to default.
	 * @param aView The view that has changed
	 */
	public void updateState (DrawingView aView) {
		if (aView != null && aView.selectionCount() == 1) {
			Figure aFigure = (Figure) aView.selection().firstElement();
			if (aFigure instanceof CPNPlaceFigure) {
				this.figureAttributeTable.setModel (new CPNPlaceTableModel ((CPNPlaceFigure) aFigure));
				this.tokensList.setModel (new TokensListModel(((CPNPlaceFigure) aFigure).getPlace()));
			} else if (aFigure instanceof CPNTransitionFigure) {
				this.figureAttributeTable.setModel (new CPNTransitionTableModel(this, (CPNTransitionFigure) aFigure));
			} else if (aFigure instanceof CPNAbstractNetFigure) {
				this.figureAttributeTable.setModel(new CPNNetTableModel(this, (CPNAbstractNetFigure) aFigure));
				if (aFigure instanceof CPNNetFigure) {
					updateNetTree ();
				}
			} else if (aFigure instanceof CPNArcFigure) {
				this.figureAttributeTable.setModel (new CPNArcTableModel(this, (CPNArcFigure) aFigure));
				} else this.figureAttributeTable.setModel (new DefaultTableModel());
		} else {
			this.figureAttributeTable.setModel (new DefaultTableModel());
		}
	}


	void newInterpreterShell (Net net) {
		this.interpreter = new Interpreter();
		interpreterShell (net);
	}

	void interpreterShell (Net net) {
		try {	this.interpreter.set("net", net); } catch (Exception e) {e.printStackTrace();}
		this.inspectArea.setCallback ("Java shell", "Execute", new TextInspectorCallback () {
			public void cancel() {/* */}
			public void commit(String text) {
				try {
					NetEditor.this.interpreter.eval (text);
				} catch (Exception e) {
					CompilationExceptionInternalFrame.handle (
														 getOwner().getDesktop(),
														 "Java Source: ", text, e, false);
				}
			}
		}, "");
	}

	void interpreterShellAddToken (final CPNPlaceFigure placeFigure) {
		this.inspectArea.setCallback ("Edit token expression", "Add token", new TextInspectorCallback () {
			public void cancel() {/*do nothing on cancel*/}
			public void commit(String text) {
				try {
					Object o = NetEditor.this.interpreter.eval (text);
					if (o == null) {
						JOptionPane.showInternalMessageDialog(getOwner().getDesktop(), 
								"Token: '" + text +"' evaluated to: '" + o + "'\n cannot be added to the place " 
								+ placeFigure.getPlace().getName(),
								"Adding NULL token", JOptionPane.ERROR_MESSAGE );
						return;
					}
					placeFigure.getPlace().addToken (o);
				} catch (Exception e) {
					CompilationExceptionInternalFrame.handle (
														 getOwner().getDesktop(),
														 "Java Source: ", text, e, false);
				}
			}
		}, "");
	}

	/** Removes selected token. */
	void removeSelectedToken (Place aPlace) {
		final Object t = this.tokensList.getSelectedValue();
		if (t != null) {
			aPlace.removeToken(t);
		} else {
			JOptionPane.showInternalMessageDialog(getOwner().getDesktop(),
					"Select token to be removed first, please.",
		 "Removing token: no token selected", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Helper for toolbar tool creation. */
	private AbstractButton createToolbarTool (ButtonGroup group, String caption,
			String icon1, String icon2, String icon3, Tool tool) {
		ToolCommand toolAction = new ToolCommand (caption,
				new ImageIcon (getOwner().getIconKit().loadImageResource(IMAGES + icon1)), this, tool);
		final JToggleButton button = new JToggleButton(toolAction);
		if (tool == defaultTool()) this.defaultToolButton = button;
		button.setPressedIcon(new ImageIcon(getOwner().getIconKit().loadImageResource(IMAGES + icon2)));
		button.setSelectedIcon(new ImageIcon(getOwner().getIconKit().loadImageResource(IMAGES + icon3)));
		if (button.getIcon() != null)	button.setText(""); //an icon-only button
		group.add(button);
		button.setToolTipText(caption);
		return button;
	}

	/** Helper for toolbar tool creation. */
	private AbstractButton createToolbarTool (ButtonGroup group, String caption,
			String icon1, String icon2, String icon3, final Command cmd) {
		final JToggleButton button = new JToggleButton(cmd);
		button.setIcon (new ImageIcon(getOwner().getIconKit().loadImageResource(IMAGES + icon1)));
		button.setPressedIcon(new ImageIcon(getOwner().getIconKit().loadImageResource(IMAGES + icon2)));
		button.setSelectedIcon(new ImageIcon(getOwner().getIconKit().loadImageResource(IMAGES + icon3)));
		if (button.getIcon() != null)	button.setText(""); //an icon-only button
		group.add (button);
		button.setToolTipText (caption);
		return button;
	}

	private JComponent createNetTree () {
		this.netTree = new JTree (new DefaultMutableTreeNode("Click on CPNNetFigure..."));
		this.netTree.setRootVisible (true);
		final JScrollPane panel = new JScrollPane(this.netTree);

		this.netTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node =
						(DefaultMutableTreeNode) NetEditor.this.netTree.getLastSelectedPathComponent();
				if (node == null || !node.isLeaf()) return;
				try {
					final Object nodeInfo = node.getUserObject();
					if (nodeInfo instanceof Figure) {
						getCurrentView().clearSelection();
						getCurrentView().addToSelection((Figure) nodeInfo);
						inspectFigure ((AbstractFigure) nodeInfo);
					} else if (nodeInfo instanceof String) {
						if (!((String) nodeInfo).trim().toLowerCase().equals("nodes") && !((String) nodeInfo).startsWith("Netdrawing") ) {
							((NetViewer) getCurrentView()).loadLayout (nodeInfo.toString());
						}
					}
				}
				catch (java.lang.NullPointerException npe) {
					updateNetTree();
				}
			}
		}
		);
		return panel;
	}

	public void updateNetTree () {
		final DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.netTree.getModel().getRoot();
		root.removeAllChildren();
		root.setUserObject("Click on CPNNetFigure...");
		try {
			root.setUserObject("Netdrawing: " + getCurrentView().drawing().getName ());
			final String[] layouts = ((NetDrawing) getCurrentView().drawing()).getNet().getLayouts();
			if (layouts.length > 0) {
				final DefaultMutableTreeNode rLay = new DefaultMutableTreeNode("Layouts");
				for (int i = 0; i < layouts.length; i++) {
					final DefaultMutableTreeNode n = new DefaultMutableTreeNode (layouts[i]);
					rLay.add (n);
				}
				root.add (rLay);
			}
			final DefaultMutableTreeNode rEle = new DefaultMutableTreeNode("Net Nodes");
			root.add (rEle);
			final DefaultMutableTreeNode rAnno = new DefaultMutableTreeNode("Annotation Nodes");
			root.add (rAnno);
			updateNetTree (rEle, rAnno, getCurrentView().drawing().figures());
		}
		catch (java.lang.NullPointerException e) {/* ignore */}
	}

	private void updateNetTree (DefaultMutableTreeNode rootNet, DefaultMutableTreeNode rootAnnotations , FigureEnumeration fe) {
		while (fe.hasMoreElements()) {
			Object fig = fe.nextElement();
			if (fig instanceof CPNNetFigure ) continue;
			if (fig instanceof CPNSubNetFigure ) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode (fig);
				DefaultMutableTreeNode nodeNet = new DefaultMutableTreeNode ("Net Nodes");
				DefaultMutableTreeNode nodeAnnotations = new DefaultMutableTreeNode ("Annotation Nodes");
				rootNet.add (node);
				updateNetTree (nodeNet, nodeAnnotations, ((CPNSubNetFigure) fig).getSubNetDrawing().figures());
				node.add(nodeNet);
				node.add(nodeAnnotations);
				rootNet.add(node);
			} else if (fig instanceof CPNAbstractFigure) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode (fig);
				rootNet.add (node);
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode (fig);
				rootAnnotations.add (node);
			}
		}
		this.netTree.updateUI();
	}

	private JComponent createAttributesTable () {
		final TableModel dataModel = new DefaultTableModel ();
		this.figureAttributeTable = new JTable(dataModel);
		final ListSelectionModel rowSM = this.figureAttributeTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting())
			return;
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
			//no rows are selected
		} else {
			if (NetEditor.this.figureAttributeTable.getModel() instanceof RowSelectionListener) {
				((RowSelectionListener) NetEditor.this.figureAttributeTable.getModel()).rowSelected(lsm.getMinSelectionIndex());
			}
		}
			}
		});

		final JScrollPane attributesPanel = new JScrollPane (this.figureAttributeTable);
		this.figureAttributeTable.setPreferredScrollableViewportSize (new Dimension (150, 200));
		return attributesPanel;
	}


	private JComponent createTokensList () {
		this.tokensList = new JList ();
		final JScrollPane listPanel = new JScrollPane (this.tokensList);
		listPanel.setPreferredSize (new Dimension (150, 200));
		return listPanel;
	}

	private JComponent createTokenPropertyTable () {
		final TableModel dataModel = new DefaultTableModel ();
		this.tokenPropertyTable = new JTable (dataModel);
		final ListSelectionModel rowSM = this.tokenPropertyTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting())
			return;
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
			//no rows are selected
		} else {
			if (NetEditor.this.tokenPropertyTable.getModel() instanceof RowSelectionListener) {
				((RowSelectionListener) NetEditor.this.tokenPropertyTable.getModel()).rowSelected(lsm.getMinSelectionIndex());
			}
		}
			}
		});

		final JScrollPane attributesPanel = new JScrollPane ();
		this.tokenPropertyTable.setPreferredScrollableViewportSize (new Dimension (150, 200));
		return attributesPanel;
	}


}