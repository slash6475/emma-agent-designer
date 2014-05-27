package emma.view.swing.petri;

import java.beans.PropertyVetoException;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class PetriViewer extends DesktopFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6184331620535082345L;
	
	private SwingController control;
	private Toolbar toolbar;
	private PropertiesPanel borderPanel;
	public PetriViewer(SwingController c){
		super("Petri Viewer",true, false, true,true);
		FixedDesktopPane dPane = new FixedDesktopPane(this);
		this.setContentPane(dPane);
		this.setSize(600, 440);
		this.control=c;
		this.toolbar=new Toolbar(control);
		toolbar.addInternalFrameListener(new FListener());
		this.add(toolbar);
		this.borderPanel=new PropertiesPanel(control);
		this.borderPanel.addInternalFrameListener(new FListener());
		this.add(borderPanel);
		PetriCanvasContainer canvCont= new PetriCanvasContainer(control);
		this.add(canvCont);
		try {
			canvCont.setMaximum(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	private class FListener extends InternalFrameAdapter{
		@Override
		public void internalFrameIconified(InternalFrameEvent e){
			e.getInternalFrame().moveToFront();
		}
	}
	
	public void manageFrames(){
		this.toolbar.moveToFront();
		this.borderPanel.moveToFront();
	}
}
