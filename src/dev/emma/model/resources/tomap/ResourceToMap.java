package emma.model.resources.tomap;

import java.awt.Color;

import emma.model.resources.Resource;

public interface ResourceToMap extends Resource {
	public boolean setImport(boolean i);
	public boolean isImported();
	public Color getColor();
	public int getIndex();
	public void setIndex(int index);
	public String getAddress();
	public void setAddress(String address);
	public String getPath();
}
