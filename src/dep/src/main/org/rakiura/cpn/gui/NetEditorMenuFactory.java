//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.rakiura.compiler.DynamicCompiler;
import org.rakiura.cpn.Marking;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.cpn.Place;
import org.rakiura.draw.Command;
import org.rakiura.draw.DrawingEditor;
import org.rakiura.draw.DrawingView;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.MenuFactory;
import org.rakiura.draw.basic.BasicEditorMenuFactory;
import org.rakiura.draw.basic.DrawingApplication;
import org.rakiura.draw.util.CommandMenu;

/**
 * Represents Net Editor menu factory.
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.35 $
 */
@SuppressWarnings("all")
class NetEditorMenuFactory extends BasicEditorMenuFactory implements MenuFactory {

	public NetEditorMenuFactory (DrawingApplication anApp, DrawingEditor anEditor) {
		super (anApp, anEditor);
	}


	public JMenuBar createMenuBar() {
		final JMenuBar mainMenu = super.createMenuBar();

		final CommandMenu netMenu = new CommandMenu ("Petri Net", this.editor);
		netMenu.add ( new Command ("New Java shell", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				final Net net = ((NetDrawing) aView.drawing()).getNet ();
				((NetEditor) getEditor ()).newInterpreterShell (net);
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		netMenu.add (new Command ("Java shell", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				final Net net = ((NetDrawing) aView.drawing()).getNet ();
				((NetEditor) getEditor ()).interpreterShell (net);
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		netMenu.addSeparator();
		netMenu.add ( new Command ("Re-generate all IDs", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				Net net = ((NetDrawing) aView.drawing()).getNet ();
				net.regenerateIDs();
				resyncIDs((NetDrawing) aView.drawing());
				net.removeAllLayouts();
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		netMenu.add (new Command ("Show Java Source", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				final Net net = ((NetDrawing) aView.drawing()).getNet ();
				final String buf = NetGenerator.createJavaSource(net);
					CompilationExceptionInternalFrame.handle (
													 aView.editor().getOwner().getDesktop(),
													 "Java Source: " + net.getName(), buf);
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		netMenu.add (new Command ("ReSync Net handle", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				final NetDrawing drawing = (NetDrawing) aView.drawing();
				final Net oldNet = drawing.getNet ();
				final Marking oldMark = oldNet.getMarking();
				final String[] l = drawing.getNet().getLayouts();
				if (l.length > 0 ) { 
					//@TODO: Make something nice to select a layout index
					((NetViewer) aView).saveLayout(l[0]);
				} else {
					((NetViewer) aView).saveLayout();
				}
				String buf = "";
				boolean resultsInException = false; 
				try {
					buf = NetGenerator.createJavaSource(oldNet);
					final Net newNet = (Net)(new DynamicCompiler()).compileClass(buf).newInstance();
					newNet.setNetDir(oldNet.getNetDir());
					newNet.setCpnLang(oldNet.getCpnLang());
					newNet.setMarking(oldMark);
					drawing.initDrawingFromNet(newNet); //old drawing is cleared and new drawing is drawn
					if (newNet.getLayouts().length > 0) {
						((NetViewer) aView).loadLayout (newNet.getLayouts()[0]);
					}
				} catch (Exception e) {
					CompilationExceptionInternalFrame.handle (
							aView.editor().getOwner().getDesktop(),
							"Java Source: " + oldNet.getName(), buf, e);
					resultsInException = true;
				}
				if (!resultsInException){
					JOptionPane.showMessageDialog(aView.editor().getOwner().getDesktop(), "Resync successful");
				}
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		netMenu.addSeparator();

		netMenu.add ( new Command ("Fuse selected Places", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				if (aView.selectionCount() < 2) return;
				Iterator selected = aView.selection().iterator();
				Place boss = ((CPNPlaceFigure)selected.next()).getPlace();
				while (selected.hasNext()) {
					try {
						Place joiner = ((CPNPlaceFigure)selected.next()).getPlace();
						boss.addPlace(joiner);
					}
					catch (Exception ex) {
						System.err.println("Something went wrong in fusing places. error = "+ex.getMessage());
					}
				}
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				if (aView.selectionCount() >= 2) {
					return (aView.selection().firstElement() instanceof CPNPlaceFigure);
				}
				return false;
			}
		});

		netMenu.addSeparator();

		netMenu.add (new Command ("Load Net layout", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void execute (DrawingView aView) {
				if (aView instanceof NetViewer) {
					((NetViewer) aView).loadLayout();
				}
			}
		});
		netMenu.add (new Command("Save Net layout", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void execute (DrawingView aView) {
				if (aView instanceof NetViewer) {
					((NetViewer) aView).saveLayout();
				}
			}
		});
		netMenu.add (new Command("Random Net layout", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void execute (DrawingView aView) {
				if (aView instanceof NetViewer) {
					((NetViewer) aView).randomLayout();
				}
			}
		});
		mainMenu.add (netMenu);

		// end PetriNet menu


		// Simulation menu

		final CommandMenu simMenu = new CommandMenu ("Simulation", this.editor);
		simMenu.add ( new Command ("Add new token", this.editor) {
			public void execute (DrawingView aView) {
				((NetEditor) getEditor ()).interpreterShellAddToken (
											 (CPNPlaceFigure) aView.selection().firstElement());
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				if (aView.selectionCount() == 1) {
					return (aView.selection().firstElement() instanceof CPNPlaceFigure);
				}
				return false;
			}
		});
		simMenu.add ( new Command ("Remove token", this.editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void execute (DrawingView aView) {
				((NetEditor) getEditor ()).removeSelectedToken (
							((CPNPlaceFigure) aView.selection().firstElement()).getPlace());
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				if (aView.selectionCount() == 1 &&
					 (aView.selection().firstElement() instanceof CPNPlaceFigure)) {
						return ((CPNPlaceFigure) aView.selection().firstElement()).getPlace().getTokens().size() > 0;
				}
				return false;
			}
		});
		simMenu.addSeparator();
		simMenu.add (new Command ("Reset simulator", this.editor) {
			public void execute (DrawingView aView) {
				((NetDrawing) aView.drawing() ).newSimulator ();
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		simMenu.addSeparator ();
		simMenu.add (new Command ("Run simulation", this.editor) {
			public void execute (final DrawingView aView) {
				final SwingWorker worker = new SwingWorker() {
					public Object construct() {
						((NetDrawing) aView.drawing()).getSimulator().run ();
						return null;
					}
				};
				worker.start();  //required for SwingWorker 3
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		simMenu.add (new Command ("Stop simulation", this.editor) {
			public void execute (DrawingView aView) {
				((NetDrawing) aView.drawing()).getSimulator().stop ();
			}
			public boolean isExecutable (DrawingView aView) {
				if (aView == null) return false;
				return (aView.drawing() instanceof NetDrawing);
			}
		});
		simMenu.add (new Command ("Single Step", this.editor) {
				public void execute (final DrawingView aView) {
					final SwingWorker worker = new SwingWorker() {
						public Object construct() {
							((NetDrawing) aView.drawing()).getSimulator().step();
							return null;
						}
					};
					worker.start();  //required for SwingWorker 3
				}
				public boolean isExecutable (DrawingView aView) {
					if (aView == null) return false;
					return (aView.drawing() instanceof NetDrawing);
				}
			});
		simMenu.addSeparator();
		simMenu.add (new Command ("Set Delay", this.editor) {
			public void execute (DrawingView aView) {
				JTextField inputField = new JTextField(""+CPNPlaceFigure.DELAY, 10);
				JOptionPane.showConfirmDialog(simMenu, inputField, "Type Delay", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
				try {
					Integer integ = new Integer(inputField.getText());
					CPNPlaceFigure.DELAY = integ.intValue();
					CPNTransitionFigure.DELAY = integ.longValue();
				}
				catch (NumberFormatException ex) {/**/}
			}
			public boolean isExecutable (DrawingView aView) {
				return true;
			}
		});
		mainMenu.add (simMenu);

		// Help menu
		final CommandMenu helpMenu = new CommandMenu ("Help", this.editor);
				helpMenu.add ( new Command ("About...", this.editor) {
					public void execute (DrawingView aView) {
						AboutBox frame = AboutBox.getInstance();
						NetEditorMenuFactory.this.app.getDesktop().remove(frame);
						NetEditorMenuFactory.this.app.getDesktop().add(frame);
						frame.setVisible(true);
						try {
							frame.setSelected(true);
						} catch (java.beans.PropertyVetoException e) {/**/}
					}
					public boolean isExecutable (DrawingView aView) {
						return true;
					}
				});
		mainMenu.add(Box.createHorizontalGlue());
		mainMenu.add(helpMenu);

		return mainMenu;
	}

	static void resyncIDs(NetDrawing aNetDrawing) {
		FigureEnumeration fe = aNetDrawing.figures();
		while (fe.hasMoreElements()) {
			Figure f = fe.nextFigure();
			if (f instanceof CPNAbstractFigure) {
				((CPNAbstractFigure)f).setID(((CPNAbstractFigure)f).getNetElement().getID());
				if (f instanceof CPNSubNetFigure) {
					resyncIDs( ((CPNSubNetFigure)f).getSubNetDrawing() );
				}
			}
		}
	}

} ///////////////////// EOF ///////////////////////