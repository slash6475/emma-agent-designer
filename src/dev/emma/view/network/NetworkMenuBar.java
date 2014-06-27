package emma.view.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class NetworkMenuBar extends JMenuBar{
	private static final long serialVersionUID = -7426795005618601412L;
	
	public NetworkMenuBar(final NetworkManager parent){
		super();
		JMenu netwk = new JMenu("Network");
		JMenuItem refresh = new JMenuItem("Refresh");
		refresh.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.refresh();
			}
		});
		netwk.add(refresh);
		this.add(netwk);
		
		JMenu launcher = new JMenu("Resources");
		JMenuItem gen = new JMenuItem("Generate");
		gen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.generate();
			}
		});
		launcher.add(gen);
		
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.load();
			}
		});
		launcher.add(load);
		
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.save();
			}
		});
		launcher.add(save);
		this.add(launcher);
	}
}
