package emma.view.swing.petri;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public abstract class SwingPetriSimpleElement extends SwingPetriFigure{

	/**
	 * 
	 */
	private static final long serialVersionUID = -694951586942013143L;
	public SwingPetriSimpleElement(String name, int x, int y, int width, int height, final SwingPetriContainer parent) {
		super(name, x, y, width, height, false, false, false,parent);
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				SwingPetriSimpleElement.this.moveBy(e.getX(), e.getY());
			}
		});
		((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
		this.setContentPane(new PetriContainer());
		this.getContentPane().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				parent.getController().selectPT(SwingPetriSimpleElement.this);
			}
		});
	}
	
	@SuppressWarnings("serial")
	private class PetriContainer extends JComponent{
		public PetriContainer(){
			super();
			this.addKeyListener(new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent e) {
					if(SwingPetriSimpleElement.this.isFocused()){
						switch(e.getKeyCode()){
						case KeyEvent.VK_UP:
							SwingPetriSimpleElement.this.moveBy(0, -1);
							break;
						case KeyEvent.VK_DOWN:
							SwingPetriSimpleElement.this.moveBy(0,1);
							break;
						case KeyEvent.VK_LEFT:
							SwingPetriSimpleElement.this.moveBy(-1,0);
							break;
						case KeyEvent.VK_RIGHT:
							SwingPetriSimpleElement.this.moveBy(1,0);
							break;
						}
					}
				}
			});
			this.addMouseMotionListener(new MouseMotionAdapter(){
				@Override
				public void mouseDragged(MouseEvent e) {
					SwingPetriSimpleElement.this.moveBy(e.getX(),e.getY());
				}
			});
		}
	}
	
	public abstract java.awt.Point getCenterPoint();
	
	@Override
	public boolean isScopeContainer() {
		return false;
	}

	@Override
	public boolean isSubnetContainer() {
		return false;
	}
}
