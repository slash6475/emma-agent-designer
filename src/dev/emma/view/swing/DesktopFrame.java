package emma.view.swing;

import javax.swing.JInternalFrame;

public class DesktopFrame extends JInternalFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2091931482578334177L;

	public DesktopFrame(String name,boolean resizable, boolean closable, boolean maximizable,
			boolean iconifiable){
		super(name,resizable,closable,maximizable,iconifiable);
		this.setVisible(true);
	}
	public DesktopFrame(String name){
		this(name,true,true,true,true);
	}
}
