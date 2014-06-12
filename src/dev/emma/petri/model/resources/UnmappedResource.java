package emma.petri.model.resources;

import java.awt.Color;

import emma.model.resources.Resource;

public interface UnmappedResource extends Resource {
	public boolean setImport(boolean i);
	public boolean isImported();
	public Color getColor();
}
