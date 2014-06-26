package emma.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

public class WindowMenuBar extends JMenuBar{

	private static final long serialVersionUID = -1391148230196504990L;
	
	private Window parent;
	private JFileChooser fileChooser;
	
	public WindowMenuBar(Window caller){
		super();
		this.parent = caller;
		this.fileChooser = new JFileChooser();
		for(FileFilter f : this.fileChooser.getChoosableFileFilters()){
			this.fileChooser.removeChoosableFileFilter(f);
		}
		this.fileChooser.addChoosableFileFilter(new FileNameFilter("lst","Project Files (.lst)"));
		JMenu petri = new JMenu("Petri");
		JMenuItem newp = new JMenuItem("New Project");
		newp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addPetriViewer();
			}
		});
		petri.add(newp);
		JMenuItem openp = new JMenuItem("Open Project");
		openp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(parent)==JFileChooser.APPROVE_OPTION){
					parent.addPetriViewer(fileChooser.getSelectedFile());
				}
			}
		});
		petri.add(openp);
		this.add(petri);
		
		JMenu network = new JMenu("Network");
		JMenuItem newn = new JMenuItem("New Network Manager");
		newn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addNetworkManager();
			}
		});
		network.add(newn);
		this.add(network);
	}
}
