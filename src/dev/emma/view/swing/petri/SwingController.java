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
	
	public SwingController(){
		mode=ControlMode.SELECT;
		lastSelected=null;
		focusFigure=null;
		try {
			parser = new XMLParser();
			xml=true;
		} catch (TransformerConfigurationException
				| ParserConfigurationException e1) {
			e1.printStackTrace();
			xml=false;
		}
		
		fileChooser = new JFileChooser();
		contextMenu = new JPopupMenu();
		place = new JMenuItem("Add place");
        trans = new JMenuItem("Add transition");
        scope = new JMenuItem("Add scope");
        sub = new JMenuItem("Add subnet");
        importsub = new JMenuItem("Import Subnet");
        save = new JMenuItem("Save");
        del = new JMenuItem("Delete");
        contextMenu.add(place);
        contextMenu.add(trans);
        contextMenu.add(scope);
        contextMenu.add(sub);
        contextMenu.addSeparator();
        contextMenu.add(importsub);
        contextMenu.add(save);
        contextMenu.addSeparator();
        contextMenu.add(del);
        place.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_PLACE);
			}
        });
        trans.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_TRANSITION);
			}
        });
        scope.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_SCOPE);
			}
        });
        sub.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addFigure(ControlMode.INSERT_SUBNET);
			}
        });
        importsub.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				importFile();
			}
        });
        save.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
        });
        del.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				fig.delete();
			}
        });
    }
	private void saveFile(){
		if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			String path = fileChooser.getSelectedFile().toString();
			if(fig.isScopeContainer()){
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
			else{
				File file = new File(path);
				if(file.mkdir()){
					try {
						parser.saveProjectToXMLFile(((NetFigure)fig).getNet(), file);
					} catch (TransformerException | FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void importFile() {
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try {
				parser.importSubnetFigureFromXMLFile(origin.x, origin.y,(NetFigure)fig, fileChooser.getSelectedFile());
			} catch (CorruptedFileException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
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