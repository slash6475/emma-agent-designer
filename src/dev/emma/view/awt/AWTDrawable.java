package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.Drawable;

public interface AWTDrawable extends Drawable{
	public void draw(Graphics2D g);
	public AbstractTableModel getProperties();
}
