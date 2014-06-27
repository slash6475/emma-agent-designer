package emma.view.swing.petri;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import emma.view.swing.petri.figure.NetFigure;

public class PetriCanvasContainer extends DesktopFrame{

	private static final long serialVersionUID = 8919330969230765576L;

	private NetFigure net;

	public PetriCanvasContainer(SwingController control) {
		super("", true, false, true, false);
		this.net = new NetFigure(control);
		this.setContentPane(net);
		this.addInternalFrameListener(new InternalFrameAdapter(){
			@Override
            public void internalFrameActivated(InternalFrameEvent e) {
                moveToBack();
            }
        });
		this.setSize(600, 440);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		JComponent np = ((BasicInternalFrameTitlePane)((BasicInternalFrameUI) this.getUI()).getNorthPane());
		Dimension d = new Dimension(this.getWidth(),5);
		np.setSize(d);
		np.setPreferredSize(d);
		np.setMaximumSize(d);
		this.remove(np);
	}
	public NetFigure getNetFigure(){
		return net;
	}
}
