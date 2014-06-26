package emma.view.swing.petri;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class SimpleElementBorder extends TitledBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3078523189750621480L;

	public SimpleElementBorder(Border border, String title,
			int titleJustification, int titlePosition) {
		super(border, title, titleJustification, titlePosition);
		
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
		super.paintBorder(c, g, x, y+10, width, height);
	}

}
