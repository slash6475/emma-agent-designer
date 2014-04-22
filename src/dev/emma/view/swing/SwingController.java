package emma.view.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import emma.view.ControlMode;
import emma.view.FigureHandler;
import emma.view.PropertiesView;
import emma.view.swing.petri.Figure;
import emma.view.swing.petri.PlaceFigure;
import emma.view.swing.petri.SwingPetriFigure;
import emma.view.swing.petri.SwingPetriSimpleElement;
import emma.view.swing.petri.TransitionFigure;

public class SwingController implements FigureHandler{

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
	//private JFileChooser fileChooser;
	private Figure fig;
	private Point origin;
	private SwingPetriSimpleElement lastSelected;
	private Figure focusFigure;
	
	public SwingController(){
		mode=ControlMode.SELECT;
		lastSelected=null;
		focusFigure=null;
		//fileChooser = new JFileChooser();
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
				//importFile();
			}
        });
        save.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
				//saveFile();
			}
        });
        del.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//p.removeDrawable(p.getSelection());
			}
        });
    }
	
	private void addFigure(ControlMode m){
		switch(m){
		case INSERT_PLACE:
			fig.addPlace(origin.x,origin.y);
			break;
		case INSERT_TRANSITION:
			fig.addTransition(origin.x,origin.y);
			break;
		case INSERT_SUBNET:
			fig.addSubnet(origin.x,origin.y);
			break;
		case INSERT_SCOPE:
			fig.addScope(origin.x,origin.y);
			break;
		default:
			return;
		}
	}
	
	public void addFigure(Figure f, int x, int y){	
		switch(mode){
		case INSERT_PLACE:
			f.addPlace(x,y);
			break;
		case INSERT_TRANSITION:
			f.addTransition(x,y);
			break;
		case INSERT_SUBNET:
			f.addSubnet(x,y);
			break;
		case INSERT_SCOPE:
			f.addScope(x,y);
			break;
		default:
			return;
		}
		mode=ControlMode.SELECT;
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
		importsub.setEnabled(f.isSubnetContainer());
		save.setEnabled(f.isSubnetContainer() || f.isScopeContainer());
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
		if(focusFigure!=null){
			focusFigure.leaveFocus();
		}
		focusFigure = f;
		focusFigure.getFocus();
		properties.setProperties(focusFigure.getProperties());
	}
}