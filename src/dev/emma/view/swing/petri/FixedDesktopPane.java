package emma.view.swing.petri;

import java.awt.Graphics;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

import emma.view.swing.petri.figure.PlaceFigure;

public class FixedDesktopPane extends JDesktopPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5301180545449790388L;

	private DrawableContainer parent;
	
	public FixedDesktopPane(DrawableContainer f){
		super();
		this.parent = f;
		this.setDesktopManager(new DefaultDesktopManager(){
			private static final long serialVersionUID = 1L;
			@Override
			public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
				FixedDesktopPane.this.setBoundsForFrame(f, newX, newY, newWidth, newHeight);
			}
		});
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		parent.addPainting(g);
	}
	
	public boolean inBounds(DesktopFrame f, int newX, int newY, int newWidth, int newHeight) {
		if (newX < 0 || newY < 0) return false;
		if (newX + newWidth > this.getWidth()) return false;
		if (newY + newHeight > this.getHeight()) return false;
		return true;
	}
	
	public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
		boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
		//BAD correction of the oversize of the PlaceFigure.
		if(f instanceof PlaceFigure){
			System.out.println("Set bounds for PlaceFigure");
			if(!inBounds((DesktopFrame) f, newX-10, newY-10, newWidth-10, newHeight-10)){
				int boundedX = (int) Math.min(Math.max(-10, newX), this.getWidth() - newWidth+10);
				int boundedY = (int) Math.min(Math.max(-10, newY), this.getHeight() - newHeight+10);
				f.setBounds(boundedX, boundedY, newWidth, newHeight);
			}
			else{
				f.setBounds(newX, newY, newWidth, newHeight);
			}
		}
		else if(f instanceof DesktopFrame){
			if (!inBounds((DesktopFrame) f, newX, newY, newWidth, newHeight)) {
				int boundedX = (int) Math.min(Math.max(0, newX), this.getWidth() - newWidth);
				int boundedY = (int) Math.min(Math.max(0, newY), this.getHeight() - newHeight);
				f.setBounds(boundedX, boundedY, newWidth, newHeight);
			}
			else {
				f.setBounds(newX, newY, newWidth, newHeight);
			}
		}
		else{
			didResize=false;
		}
		if(didResize) {
			f.validate();
		}
	}
}
