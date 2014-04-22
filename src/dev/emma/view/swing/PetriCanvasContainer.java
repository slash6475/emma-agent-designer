package emma.view.swing;

import javax.swing.JRootPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import emma.view.swing.petri.NetFigure;

public class PetriCanvasContainer extends DesktopFrame{

	public PetriCanvasContainer(SwingController control) {
		super("", true, false, true, false);
		
		this.setContentPane(new NetFigure(control));
		
		this.addInternalFrameListener(new InternalFrameAdapter(){
			@Override
            public void internalFrameActivated(InternalFrameEvent e) {
                moveToBack();
            }
        });
		
		this.setSize(600, 440);
		this.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        this.remove(((BasicInternalFrameTitlePane)((BasicInternalFrameUI) this.getUI()).getNorthPane()));
        this.setBorder(null);
	}

	private static final long serialVersionUID = 8919330969230765576L;

}
