package emma.view.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import emma.petri.view.PetriCanvas;
import emma.view.FigureHandler;
import emma.view.PetriViewer;
import emma.view.awt.AWTPetriCanvas;
import emma.view.img.Resources;

public class PetriSwingWindow extends JFrame implements PetriViewer{
	
	private static final long serialVersionUID = 6098483712279657780L;
	private ButtonGroup group;
	private JCheckBoxMenuItem playstop;
	private JButton cursor;
	private JButton play;
	private JButton transition;
	private JButton place;
	private JButton subnet;
	private JButton arrow;
	private JRadioButtonMenuItem simuMode;
	private JRadioButtonMenuItem playMode;
	private AWTPetriCanvas canvas;
	private FigureHandler control;
	
	public PetriSwingWindow(FigureHandler control){
		super();
		setTitle("Petri (Test) Viewer");
		setSize(640, 480);
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(buildMenuBar());
		add(buildToolBar(),BorderLayout.NORTH);
		canvas=null;
		this.control=control;
	}
	private JMenuBar buildMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu files = new JMenu("Files");
		menuBar.add(files);
		JMenu simu = new JMenu("Simulation");
		//Modes
		group = new ButtonGroup();
		simuMode = new JRadioButtonMenuItem("Simulation Mode", true);
		simuMode.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED)
					simulationMode(true);
			}
		});
		group.add(simuMode);
		simu.add(simuMode);
		playMode = new JRadioButtonMenuItem("Player Mode", false);
		playMode.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED)	
					simulationMode(false);
			}
		});
		group.add(playMode);
		simu.add(playMode);
		simu.add(new JSeparator());
		//Play/Stop
		playstop = new JCheckBoxMenuItem("Play/Stop");
		playstop.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				control.placeSelect();
			}
		});
		simu.add(playstop);
		menuBar.add(simu);
		return menuBar;
	}
	
	private JToolBar buildToolBar(){
		JToolBar toolbar = new JToolBar();
		//Ajout du curseur
		cursor = new JButton();
		cursor.setIcon(new ImageIcon(Resources.getPath("cursor", 24)));
		cursor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.cursorSelect();
			}
		});
		toolbar.add(cursor);
		//Ajout du play/stop
		play = new JButton();
		play.setIcon(new ImageIcon(Resources.getPath("play",24)));
		play.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				playstop.doClick();
			}
		});
		toolbar.add(play);		
		toolbar.add(new JToolBar.Separator(new Dimension(96,24)));
		//Ajout du sous-reseau
		subnet = new JButton();
		subnet.setIcon(new ImageIcon(Resources.getPath("subnet", 24)));
		subnet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.subnetSelect();
			}
		});
		toolbar.add(subnet);
		//Ajout de la fleche
		arrow = new JButton();
		arrow.setIcon(new ImageIcon(Resources.getPath("arrow", 24)));
		arrow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.arrowSelect();
			}
		});
		toolbar.add(arrow);
		//Ajout de la place
		place = new JButton();
		place.setIcon(new ImageIcon(Resources.getPath("place", 24)));
		place.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.placeSelect();
			}
		});
		toolbar.add(place);
		//Ajout de la transition
		transition = new JButton();
		transition.setIcon(new ImageIcon(Resources.getPath("transition",24)));
		transition.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				control.transitionSelect();
			}
		});
		toolbar.add(transition);
		return toolbar;
	}
	private void enableCreationButtons(boolean enable){
		subnet.setEnabled(enable);
		transition.setEnabled(enable);
		arrow.setEnabled(enable);
		place.setEnabled(enable);
	}
	protected void simulationMode(boolean simuMode){
		enableCreationButtons(simuMode);
	}
	
	@Override
	public void setCanvas(PetriCanvas p){
		setCanvas((AWTPetriCanvas)p,BorderLayout.CENTER);
	}
	public void setCanvas(AWTPetriCanvas p, String pos){
		if(canvas!=null){
			this.remove(canvas);
		}
		this.add(p);
		canvas=p;
	}
	
	@Override
	public PetriCanvas getCanvas() {
		return canvas;
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		canvas.setVisible(b);
	}
}
