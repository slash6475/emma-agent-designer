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
	private PetriCanvasContainer canvas;
	
	public PetriViewer(SwingController c){
		super("",true, true, true,true);
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
		this.canvas = new PetriCanvasContainer(control);
		control.setFigure(this.canvas.getNetFigure());
		this.add(this.canvas);
		try {
			this.canvas.setMaximum(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setName(String name){
		super.setName(name);
		this.setTitle(name);
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
