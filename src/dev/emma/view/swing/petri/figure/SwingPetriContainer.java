package emma.view.swing.petri.figure;

import emma.view.swing.petri.DrawableContainer;
import emma.view.swing.petri.FixedDesktopPane;

public abstract class SwingPetriContainer extends SwingPetriFigure implements DrawableContainer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6494997392400428800L;

	public SwingPetriContainer(String name, int width, int height,
			boolean maximizable, boolean iconifiable, Figure parent) {
		super(name, width, height, true, maximizable, iconifiable,parent);
		this.setContentPane(new FixedDesktopPane(this));
	}
	
}
