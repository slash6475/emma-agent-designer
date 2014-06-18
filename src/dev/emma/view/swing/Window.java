package emma.view.swing;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import emma.view.swing.petri.DrawableContainer;
import emma.view.swing.petri.FixedDesktopPane;
import emma.view.swing.petri.PetriViewer;
import emma.view.swing.petri.SwingController;

public class Window extends JFrame implements DrawableContainer{
	
	private static final long serialVersionUID = 6098483712279657780L;
	
	private FixedDesktopPane pane;
	private int petriCount;
	public Window(){
		super();
		this.petriCount=0;
		this.setTitle("EMMA Agent Design");
		this.pane = new FixedDesktopPane(this);
		this.pane.setBackground(this.getBackground());
		this.setContentPane(pane);
		this.setSize(800, 600);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
		this.setJMenuBar(new MenuBar(this));
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
	}

	@Override
	public void addPainting(Graphics g) {
	}
	
	public void addPetriViewer(){
		petriCount++;
		SwingController control = new SwingController();
		PetriViewer v = new PetriViewer(control);
		v.setName("PetriViewer #"+petriCount);
		pane.add(v);
	}
	
	public void addPetriViewer(File list){
		String name = list.getParentFile().getName();
		SwingController control = new SwingController();
		PetriViewer v = new PetriViewer(control);
		v.setName(name);
		pane.add(v);
		control.importProject(list);
	}
}