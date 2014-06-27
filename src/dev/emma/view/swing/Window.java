package emma.view.swing;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.SocketException;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import emma.control.coap.CoapProxy;
import emma.view.network.NetworkManager;
import emma.view.swing.petri.DrawableContainer;
import emma.view.swing.petri.FixedDesktopPane;
import emma.view.swing.petri.PetriViewer;
import emma.view.swing.petri.SwingController;

public class Window extends JFrame implements DrawableContainer{
	private static final long serialVersionUID = 6098483712279657780L;
	private static Logger logger = Logger.getLogger(Window.class);
	private FixedDesktopPane pane;
	private int petriCount;
	private CoapProxy coapProxy;
	
	public Window(){
		super();
		try {
			//Unique coapProxy for NetworkManager frames
			this.coapProxy = new CoapProxy();
		} catch (SocketException e) {
			logger.warn(e.getMessage());
		}
		this.petriCount=0;
		this.setTitle("EMMA Framework");
		this.pane = new FixedDesktopPane(this);
		this.pane.setBackground(this.getBackground());
		this.setContentPane(pane);
		this.setSize(800, 600);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				logger.debug("End of the program");
				System.exit(0);
			}
		});
		this.setJMenuBar(new WindowMenuBar(this));
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
	}

	@Override
	public void addPainting(Graphics g) {}
	
	public void addNetworkManager(){
		pane.add(new NetworkManager(this.coapProxy.getNetwork()));
	}
	//Add a new Petri Viewer
	public void addPetriViewer(){
		petriCount++;
		SwingController control = new SwingController();
		PetriViewer v = new PetriViewer(control);
		v.setName("PetriViewer #"+petriCount);
		pane.add(v);
	}
	//Open previously saved Petri Viewer
	public void addPetriViewer(File list){
		String name = list.getParentFile().getName();
		SwingController control = new SwingController();
		PetriViewer v = new PetriViewer(control);
		v.setName(name);
		pane.add(v);
		control.importProject(list);
	}
}