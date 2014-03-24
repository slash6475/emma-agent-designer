package emma.view.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import emma.view.FigureHandler;
import emma.view.img.Resources;

public class Toolbar extends JPanel {
	
	private static final long serialVersionUID = 970347359282346612L;

	public Toolbar(final FigureHandler control){
		super();
		JButton cursor = new JButton();
		cursor.setIcon(new ImageIcon(Resources.getPath("cursor", 24)));
		cursor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.cursorSelect();
			}
		});
		this.add(cursor);
		//Ajout du play/stop
		JButton play = new JButton();
		play.setIcon(new ImageIcon(Resources.getPath("play",24)));
		play.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//playstop.doClick();
			}
		});
		this.add(play);
		//Ajout du sous-reseau
		JButton subnet = new JButton();
		subnet.setIcon(new ImageIcon(Resources.getPath("subnet", 24)));
		subnet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.subnetSelect();
			}
		});
		this.add(subnet);
		
		JButton scope = new JButton();
		scope.setIcon(new ImageIcon(Resources.getPath("scope", 24)));
		scope.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.scopeSelect();
			}
		});
		this.add(scope);
		//Ajout de la fleche
		JButton arrow = new JButton();
		arrow.setIcon(new ImageIcon(Resources.getPath("arrow", 24)));
		arrow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.arrowSelect();
			}
		});
		this.add(arrow);
		//Ajout de la place
		JButton place = new JButton();
		place.setIcon(new ImageIcon(Resources.getPath("place", 24)));
		place.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.placeSelect();
			}
		});
		this.add(place);
		//Ajout de la transition
		JButton transition = new JButton();
		transition.setIcon(new ImageIcon(Resources.getPath("transition",24)));
		transition.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.transitionSelect();
			}
		});
		this.add(transition);
	}
}
