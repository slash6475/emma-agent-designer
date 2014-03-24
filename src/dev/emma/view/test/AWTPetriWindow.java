package emma.view.test;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import emma.petri.view.PetriCanvas;
import emma.view.FigureHandler;

public class AWTPetriWindow extends Frame {
	
	private static final long serialVersionUID = 6098483712279657780L;
	private FigureHandler control;
	
	public AWTPetriWindow(AWTController control){
		super();
		this.control=control;
		this.setTitle("Petri (Test) Viewer");
		this.setLayout(new BorderLayout());
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
		this.add(new Toolbar(control),BorderLayout.NORTH);
		this.add(new CanvasScroll(control),BorderLayout.CENTER);
		this.add(new BorderPanel(control),BorderLayout.EAST);
	}
	/*
	private MenuBar buildMenuBar(){
		MenuBar menuBar = new MenuBar();
		Menu files = new Menu("Files");
		menuBar.add(files);
		Menu simu = new Menu("Simulation");
		menuBar.add(simu);
		
		return menuBar;
	}*/
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		control.getCanvas().setVisible(b);
	}
	
	public PetriCanvas getCanvas(){
		return control.getCanvas();
	}
}