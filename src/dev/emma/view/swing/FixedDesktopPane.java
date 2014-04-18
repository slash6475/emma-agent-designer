package emma.view.swing;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

public class FixedDesktopPane extends JDesktopPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5301180545449790388L;

	public FixedDesktopPane(){
		super();
		this.setDesktopManager(new FixedDesktopManager());
	}
	
	
	private class FixedDesktopManager extends DefaultDesktopManager{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7862742183628072173L;
		
		@Override
		public void beginDraggingFrame(JComponent f) {
		}

		@Override
		public void beginResizingFrame(JComponent f, int direction) {
		}
		
		@Override
		public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
			if(f instanceof DesktopFrame){
				boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
				if (!inBounds((DesktopFrame) f, newX, newY, newWidth, newHeight)) {
					Container parent = f.getParent();
					Dimension parentSize = parent.getSize();
					int boundedX = (int) Math.min(Math.max(0, newX), parentSize.getWidth() - newWidth);
					int boundedY = (int) Math.min(Math.max(0, newY), parentSize.getHeight() - newHeight);
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

		protected boolean inBounds(DesktopFrame f, int newX, int newY, int newWidth, int newHeight) {
			if (newX < 0 || newY < 0) return false;
			if (newX + newWidth > f.getDesktopPane().getWidth()) return false;
			if (newY + newHeight > f.getDesktopPane().getHeight()) return false;
			return true;
		}
	}
}
