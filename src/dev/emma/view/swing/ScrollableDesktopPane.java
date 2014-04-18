package emma.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class ScrollableDesktopPane extends JScrollPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7479651357248744725L;
	
	private JDesktopPane desktopPane;
	private IFrameListener componentListener;

	public ScrollableDesktopPane() {
		
		componentListener = new IFrameListener();
		this.desktopPane = new JDesktopPane();
		desktopPane.addContainerListener(new ContainerListener() {
			@Override
			public void componentAdded(ContainerEvent event) {
				Component c = event.getChild();
				if (c instanceof JInternalFrame){
					c.addComponentListener(componentListener);
					resizeDesktop();
				}
			}

			@Override
			public void componentRemoved(ContainerEvent event) {
				Component removedComponent = event.getChild();
				if (removedComponent instanceof JInternalFrame){
					((JInternalFrame)removedComponent).removeComponentListener(componentListener);
				}
			}
		});
		setViewportView(desktopPane);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	private class IFrameListener implements ComponentListener{
		@Override
		public void componentResized(ComponentEvent e) {
			resizeDesktop();
		}
		
		@Override
		public void componentMoved(ComponentEvent e) {
			resizeDesktop();
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}
	
	@Override
	public Component add(Component c){
		return desktopPane.add(c);
	}
 


	/**
	 * returns all internal frames placed upon the desktop
	 *
	 * @return a JInternalFrame array containing references to the internal frames
	 */
	public JInternalFrame[] getAllFrames() {
		return desktopPane.getAllFrames();
	}

	/**
	 * sets the preferred size of the desktop
	 *
	 * @param dim a Dimension object representing the desired preferred size
	 */
	public void setDesktopSize(Dimension dim) {
		desktopPane.setPreferredSize(dim);
		desktopPane.revalidate();
	}


	/**
	 * resizes the desktop based upon the locations of its
	 * internal frames. This updates the desktop scrollbars in real-time.
	 */
	public void resizeDesktop() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				Rectangle viewPort = getViewport().getViewRect();

				int maxX = viewPort.width + viewPort.x, maxY = viewPort.height + viewPort.y;
				int minX = viewPort.x, minY = viewPort.y;

				// determine the min/max extents of all internal frames

				JInternalFrame frame = null;
				JInternalFrame[] frames = getAllFrames();

				for (int i=0; i < frames.length; i++) {

					frame = frames[i];

					if (frame.getX() < minX) { // get minimum X
						minX = frame.getX();
					}
					if ((frame.getX() + frame.getWidth()) > maxX)
					{
						maxX = frame.getX() + frame.getWidth();
					}

					if (frame.getY() < minY) { // get minimum Y
						minY = frame.getY();
					}
					if ((frame.getY() + frame.getHeight()) > maxY)
					{
						maxY = frame.getY() + frame.getHeight();
					}
				}

				// Don't count with frames that get off screen from the left side ant the top
				if (minX < 0) minX = 0;
				if (minY < 0) minY = 0;

				setVisible(false); // don't update the viewport
				// while we move everything (otherwise desktop looks 'bouncy')

				if (minX != 0 || minY != 0) {
					// have to scroll it to the right or up the amount that it's off screen...
					// before scroll, move every component to the right / down by that amount

					for (int i=0; i < frames.length; i++) {
						frame = frames[i];
						frame.setLocation(frame.getX()-minX, frame.getY()-minY);
					}

					// have to scroll (set the viewport) to the right or up the amount
					// that it's off screen...
					JViewport view = getViewport();
					view.setViewSize(new Dimension((maxX-minX),(maxY-minY)));
					view.setViewPosition(new Point((viewPort.x-minX),(viewPort.y-minY)));
					setViewport(view);

				}

				// resize the desktop
				setDesktopSize(new Dimension(maxX-minX, maxY-minY));

				setVisible(true); // update the viewport again

			}
		});
	}
}