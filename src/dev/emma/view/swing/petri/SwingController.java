package emma.view.swing.petri;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import emma.petri.control.Player;
import emma.petri.view.ControlMode;
import emma.petri.view.CorruptedFileException;
import emma.petri.view.FigureHandler;
import emma.petri.view.PropertiesView;
import emma.petri.view.XMLParser;
import emma.view.swing.FileNameFilter;
import emma.view.swing.petri.figure.Figure;
import emma.view.swing.petri.figure.NetFigure;
import emma.view.swing.petri.figure.PlaceFigure;
import emma.view.swing.petri.figure.SubnetFigure;
import emma.view.swing.petri.figure.TransitionFigure;

public class SwingController implements FigureHandler{
	private boolean xml;
	private ControlMode mode;
	private PropertiesView properties;
	private JPopupMenu contextMenu;
	private JMenuItem place;
	private JMenuItem trans;
	private JMenuItem scope;
	private JMenuItem sub;
	private JMenuItem importsub;
	private JMenuItem save;
	private JMenuItem del;
	private JFileChooser fileChooser;
	private Figure fig;
	private Point origin;
	private SwingPetriSimpleElement lastSelected;
	private Figure focusFigure;
	private XMLParser parser;
	private Player player;
	private FileNameFilter epnf;
	private FileNameFilter lst;
	
	public SwingController(){
		this.mode=ControlMode.SELECT;
		this.lastSelected=null;
		this.focusFigure=null;
		try {
			parser = new XMLParser();
			xml=true;
		} catch (TransformerConfigurationException | ParserConfigurationException e1) {
			System.out.println(e1.getMessage());
			xml=false;
		}
		this.fileChooser = new JFileChooser();
		for(FileFilter f : this.fileChooser.getChoosableFileFilters()){
			this.fileChooser.removeChoosableFileFilter(f);
		}
		this.epnf=new FileNameFilter("epnf", "Subnet Files (.epnf)");
		this.lst=new FileNameFilter("", "Project (new directory)");
		this.contextMenu = new JPopupMenu();
		this.place = new JMenuItem("Add place");
		this.trans = new JMenuItem("Add transition");
		this.scope = new JMenuItem("Add scope");
		this.sub = new JMenuItem("Add subnet");
		this.importsub = new JMenuItem("Import Subnet");
		this.save = new JMenuItem("Save");
		this.del = new JMenuItem("Delete");
		this.contextMenu.add(place);
		this.contextMenu.add(trans);
		this.contextMenu.add(scope);
		this.contextMenu.add(sub);
		this.contextMenu.addSeparator();
		this.contextMenu.add(importsub);
		this.contextMenu.add(save);
		this.contextMenu.addSeparator();
		this.contextMenu.add(del);
		this.place.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_PLACE);
			}
        });
		this.trans.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_TRANSITION);
			}
        });
		this.scope.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_SCOPE);
			}
        });
		this.sub.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_SUBNET);
			}
        });
		this.importsub.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				importFile();
			}
        });
		this.save.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(fig.isSubnetContainer());
			}
        });
		this.del.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				fig.delete();
			}
        });
    }
	private void saveFile(boolean project){
		if(project){
			fileChooser.addChoosableFileFilter(lst);
			if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				String path = fileChooser.getSelectedFile().toString();
				File file = new File(path);
				if(file.mkdir()){
					try {
						parser.saveProjectToXMLFile(((NetFigure)fig).getNet(), file);
					} catch (TransformerException | FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			fileChooser.removeChoosableFileFilter(lst);
		}
		else{
			fileChooser.addChoosableFileFilter(epnf);
			if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				String path = fileChooser.getSelectedFile().toString();
				if(!path.endsWith(".epnf")){
					path=path.concat(".epnf");
				}
				File file = new File(path);
				try {
					parser.saveSubnetToXMLFile(((SubnetFigure)fig).getSubnet(), file);
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			}
			fileChooser.removeChoosableFileFilter(epnf);
		}
	}
	
	private void importFile() {
		fileChooser.addChoosableFileFilter(epnf);
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try {
				parser.importSubnetFigureFromXMLFile(origin.x, origin.y,(NetFigure)fig, fileChooser.getSelectedFile());
			} catch (CorruptedFileException | SAXException | IOException e) {
				System.out.println(e.getMessage());
			}
		}
		fileChooser.removeChoosableFileFilter(epnf);
	}
	
	public void importProject(File list){
		parser.importProject((NetFigure)fig,list);
	}
	
	private boolean addFigure(ControlMode m, Figure f, int x, int y){
		switch(m){
		case INSERT_PLACE:
			return f.addPlace(x,y);
		case INSERT_TRANSITION:
			return f.addTransition(x,y);
		case INSERT_SUBNET:
			return f.addSubnet(x,y);
		case INSERT_SCOPE:
			return f.addScope(x,y);
		default:
			return false;
		}
	}
	
	private void addFigure(ControlMode m){
		this.addFigure(m,fig, origin.x, origin.y);
	}
	
	public void addFigure(Figure f, int x, int y){	
		if(this.addFigure(mode,f,x,y)){
			mode=ControlMode.SELECT;
		}
	}
	@Override
	public void transitionSelect() {
		mode=ControlMode.INSERT_TRANSITION;
	}

	@Override
	public void placeSelect() {
		mode=ControlMode.INSERT_PLACE;
	}

	@Override
	public void arrowSelect() {
		lastSelected=null;
		mode=ControlMode.INSERT_ARC;
	}

	@Override
	public void subnetSelect() {
		mode=ControlMode.INSERT_SUBNET;
	}

	@Override
	public void scopeSelect() {
		mode=ControlMode.INSERT_SCOPE;
	}

	@Override
	public void setPropertiesView(PropertiesView borderPanel) {
		this.properties=borderPanel;
	}
	
	public void showPopup(Figure f, int x, int y, int actionX, int actionY){
		this.fig=f;
		this.origin=new Point(actionX,actionY);
		place.setEnabled(f.isPlaceContainer());
		trans.setEnabled(f.isTransitionContainer());
		scope.setEnabled(f.isScopeContainer());
		sub.setEnabled(f.isSubnetContainer());
		importsub.setEnabled(f.isSubnetContainer() && xml);
		save.setEnabled((f.isScopeContainer() || f.isSubnetContainer())&& xml);
		del.setEnabled(f instanceof SwingPetriFigure);
		contextMenu.show((Component) f, x, y);
	}
	
	public void showPopup(Figure f, int x, int y){
		this.showPopup(f,x,y,x,y);
	}

	public void selectPT(SwingPetriSimpleElement e) {
		if(mode==ControlMode.INSERT_ARC){
			if(lastSelected==null){
				lastSelected=e;
			}
			else{
				if(lastSelected instanceof PlaceFigure && e instanceof TransitionFigure){
					((TransitionFigure)e).createInputArc((PlaceFigure)lastSelected);
					lastSelected=null;
				}
				else if(lastSelected instanceof TransitionFigure && e instanceof PlaceFigure){
					((TransitionFigure)lastSelected).createOutputArc((PlaceFigure)e);
					lastSelected=null;
				}
				else{
					lastSelected=e;
				}
			}
		}
	}
	
	public void putFocusOn(Figure f){
		if(f!=focusFigure){
			if(focusFigure!=null){
				focusFigure.leaveFocus();
			}
			focusFigure = f;
			focusFigure.getFocus();
			properties.setProperties(focusFigure.getProperties());
		}
	}
	
	public void setFigure(Figure f){
		this.fig=f;
	}
	
	public void setNetFigure(NetFigure netFigure) {
		player = new Player(netFigure.getNet());
		this.setFigure(netFigure);
	}
	
	@Override
	public void playPause() {
		player.playPause();
	}
}