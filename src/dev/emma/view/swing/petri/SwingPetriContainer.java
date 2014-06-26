package emma.view.swing.petri;


import java.awt.Color;

import javax.swing.UIDefaults;

import emma.view.swing.petri.figure.Figure;

public abstract class SwingPetriContainer extends SwingPetriFigure implements DrawableContainer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6494997392400428800L;

	public SwingPetriContainer(String name, int width, int height,
			boolean maximizable, boolean iconifiable, Figure parent, final Color c) {
		super(name, width, height, true, maximizable, iconifiable,parent);
		this.setContentPane(new FixedDesktopPane(this){
			private static final long serialVersionUID = 1L;
			@Override
			public void updateUI(){
				UIDefaults map = new UIDefaults();
				map.put("DesktopPane[Enabled].backgroundPainter", new UniformPainter<Object>(c));
				putClientProperty("Nimbus.Overrides", map);
				super.updateUI();
			}
		});
	}
	
}
