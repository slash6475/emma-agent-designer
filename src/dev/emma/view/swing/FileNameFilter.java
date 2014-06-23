package emma.view.swing;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileNameFilter extends FileFilter {
	private String ext;
	private String desc;
	
	public FileNameFilter(String suffix){
		this(suffix,suffix.toUpperCase()+" Files");
	}
	public FileNameFilter(String suffix, String desc){
		this.ext="."+suffix;
		this.desc=desc;
	}
	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory() || pathname.getName().endsWith(this.ext);
	}
	@Override
	public String getDescription() {
		return this.desc;
	}
}
