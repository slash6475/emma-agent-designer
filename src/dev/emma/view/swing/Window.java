package emma.view.swing;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import emma.view.swing.petri.DrawableContainer;
import emma.view.swing.petri.FixedDesktopPane;
import emma.view.swing.petri.PetriViewer;
import emma.view.swing.petri.SwingController;

public class Window extends JFrame implements DrawableContainer{
	
	private static final long serialVersionUID = 6098483712279657780L;
	
	private FixedDesktopPane pane;
	
	public Window(SwingController control){
		super();
		this.setTitle("Petri (Test) Viewer");
		this.pane = new FixedDesktopPane(this);
		this.pane.setBackground(this.getBackground());
		this.setContentPane(pane);
		this.setSize(640, 480);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
		pane.add(new PetriViewer(control));
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
	}

	@Override
	public void addPainting(Graphics g) {
	}
}