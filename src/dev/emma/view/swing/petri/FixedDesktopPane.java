package emma.view.swing.petri;

import java.awt.Graphics;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

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
		if(f instanceof DesktopFrame){
			boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
			if (!inBounds((DesktopFrame) f, newX, newY, newWidth, newHeight)) {
				int boundedX = (int) Math.min(Math.max(0, newX), this.getWidth() - newWidth);
				int boundedY = (int) Math.min(Math.max(0, newY), this.getHeight() - newHeight);
				f.setBounds(boundedX, boundedY, newWidth, newHeight);
			}
			else {
				f.setBounds(newX, newY, newWidth, newHeight);
			}
			if(didResize) {
				f.validate();
			}
		}
	}
}
