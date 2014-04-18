package emma.view.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class PetriWindow extends JFrame {
	
	private static final long serialVersionUID = 6098483712279657780L;
	
	private FixedDesktopPane pane;
	
	public PetriWindow(SwingController control){
		super();
		this.setTitle("Petri (Test) Viewer");
		this.pane = new FixedDesktopPane();
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
		pane.add(new PetriViewerContainer(control));
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
	}
}