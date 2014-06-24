package emma.view.swing;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileNameFilter extends FileFilter {
	private String ext;
	private String desc;
	
	public FileNameFilter(String extensions){
		this(extensions,extensions.toUpperCase()+" Files");
	}
	public FileNameFilter(String extensions, String desc){
		this.ext="(.*)\\.("+extensions+")";
		this.desc=desc;
	}
	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory() || pathname.getName().matches(this.ext);
	}
	@Override
	public String getDescription() {
		return this.desc;
	}
}
