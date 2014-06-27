package emma.view.swing.petri;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.Painter;

//Set background does not work with Nimbus theme, so we use a dedicated painter
public class UniformPainter<T> implements Painter<T>{
	private Color color;
	public UniformPainter(Color color) {
		this.color=color;
	}
	@Override
	public void paint(Graphics2D g, T object, int width, int height) {
		g.setColor(color);
		g.fillRect(0, 0, width, height);
	}
}
