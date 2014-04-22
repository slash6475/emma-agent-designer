package emma.view.swing.petri;

import emma.view.swing.FixedDesktopPane;

public abstract class SwingPetriContainer extends SwingPetriFigure{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6494997392400428800L;

	public SwingPetriContainer(String name, int x, int y, int width, int height,
			boolean maximizable, boolean iconifiable, Figure parent) {
		super(name, x, y, width, height, true, maximizable, iconifiable,parent);
		this.setContentPane(new FixedDesktopPane());
		
	}
}
