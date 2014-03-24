package emma.view.test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import emma.view.FigureHandler;
import emma.view.awt.AWTPetriCanvas;

public class CanvasScroll extends ScrollPane {

	private static final long serialVersionUID = -211453138961388457L;

	private AWTPetriCanvas canvas;
	//Double buffering variables
	// variable permettant d'utiliser la mémoire VRAM
	public CanvasScroll(FigureHandler c){
		this.canvas= new AWTPetriCanvas(c);
		this.add(canvas);
		this.setIgnoreRepaint(true);
		this.addComponentListener(new ComponentListener(){
			@Override
			public void componentResized(ComponentEvent e) {
				setViewPort();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentShown(ComponentEvent e) {
				setViewPort();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
			
		});
		this.getVAdjustable().addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				setViewPort();
			}
		});
		this.getHAdjustable().addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				setViewPort();
			}
		});
	}
	private void setViewPort(){
		Point p = this.getScrollPosition();
		Dimension d = this.getViewportSize();
		canvas.setViewPort(p.x, p.y, d.width, d.height);
	}
}
