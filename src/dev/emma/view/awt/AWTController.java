package emma.view.awt;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import emma.petri.view.Drawable;
import emma.petri.view.Orientation;
import emma.view.ControlMode;
import emma.view.FigureHandler;

public class AWTController implements MouseListener,MouseMotionListener,KeyListener, FigureHandler{
	
	private AWTPetriCanvas p;
	private Point origin;
	private Point end;
	private ControlMode mode;
	private boolean resizeable;
	private boolean resizing;
	private Orientation orientation;
	private JPopupMenu contextMenu;
	private JMenuItem place;
	private JMenuItem trans;
	private JMenuItem importsub;
	private JMenuItem save;
	private JMenuItem del;
	private JMenuItem prop;
	private JFileChooser fileChooser;
	
	public AWTController(){
		p=null;
		resizeable=false;
		resizing=false;
		
		fileChooser = new JFileChooser();
		//CONTEXT MENU
		contextMenu = new JPopupMenu();
		place = new JMenuItem("Add place");
        trans = new JMenuItem("Add transition");
        importsub = new JMenuItem("Import Subnet");
        save = new JMenuItem("Save");
        del = new JMenuItem("Delete");
        prop = new JMenuItem("Properties");
        contextMenu.add(place);
        contextMenu.add(trans);
        contextMenu.addSeparator();
        contextMenu.add(importsub);
        contextMenu.add(save);
        contextMenu.addSeparator();
        contextMenu.add(del);
        contextMenu.addSeparator();
        contextMenu.add(prop);
        place.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				p.addPlace(origin.x, origin.y);
			}
        });
        trans.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				p.addTransition(origin.x, origin.y);
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
				p.removeDrawable(p.getSelection());
			}
        });
        //END OF CONTEXT MENU
	}
	
	@Override
	public void keyTyped(KeyEvent e){
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_V:
			break;
		case KeyEvent.VK_S:
			saveFile();
			break;
		case KeyEvent.VK_DELETE:
			p.removeDrawable(p.getSelection());
			break;
		case KeyEvent.VK_UP:
			p.moveSelectionBy(0,-1);
			break;
		case KeyEvent.VK_DOWN:
			p.moveSelectionBy(0,1);
			break;
		case KeyEvent.VK_LEFT:
			p.moveSelectionBy(-1,0);
			break;
		case KeyEvent.VK_RIGHT:
			p.moveSelectionBy(1,0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e){
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getButton()==MouseEvent.BUTTON1){
			if(e.getClickCount()>1) mouseDoubleClicked(e);
			else{
				if(mode==ControlMode.SELECT){
					p.select(e.getX(), e.getY());
				}
				else if(mode==ControlMode.INSERT_PLACE){
					p.addPlace(e.getX(),e.getY());
				}
				else if(mode==ControlMode.INSERT_TRANSITION){
					p.addTransition(e.getX(), e.getY());
				}
			}
		}
		else{
			mode=ControlMode.SELECT;
			p.select(e.getX(), e.getY());
			this.showPopup(e);
		}
	}
	
	public void mouseDoubleClicked(MouseEvent e){
		if(mode==ControlMode.SELECT){
			p.switchNotationsVisibilityOfSelection();
		}
	}

	@Override
	public void mousePressed(MouseEvent e){
		if(e.getButton()==MouseEvent.BUTTON1){
			origin = e.getPoint();
	        if(resizeable){
	        	resizing=true;
	        }
		}
	}

	
	@Override
	public void mouseReleased(MouseEvent e){
		if(e.getButton()==MouseEvent.BUTTON1){
			if(mode == ControlMode.INSERT_ARC){
				p.addArc(origin.x, origin.y, e.getX(), e.getY());
			}
			// TODO Auto-generated method stub
			else if(mode == ControlMode.INSERT_SUBNET){
				int x,y,width,height;
				end = e.getPoint();
				if(origin.x>end.x){
		        	x=end.x;
		        	width=origin.x-end.x;
		        }
		        else{
		        	x=origin.x;
		        	width=end.x-origin.x;
		        }
		        if(origin.y>end.y){
		        	y=end.y;
		        	height=origin.y-end.y;
		        }
		        else{
		        	y=origin.y;
		        	height=end.y-origin.y;
		        }
		        if(width>AWTPlaceFigure.getDefaultWidth() && height>AWTPlaceFigure.getDefaultHeight()){
		        	p.addSubnet(x,y,width,height);
		        }
			}
			if(resizing){
				resizing=false;
				resizeable=false;
				p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		p.removeDraw();
	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Drawable selection = p.getSelection();
		if(mode == ControlMode.SELECT){
			if(selection!=null){
				if(selection instanceof AWTSubnetFigure){
					if(x>selection.getX()-5 && x<selection.getX()+5){
						if(y>selection.getY()-5 && y<selection.getY()+5){
							p.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
							orientation=Orientation.NW;
							resizeable=true;
						}
						else if(y>selection.getY()+selection.getHeight()-5 && y<selection.getY()+selection.getHeight()+5){
							p.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
							orientation=Orientation.SW;
							resizeable=true;
						}
						else{
							if(resizeable){
								resizeable=false;
								p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							}
							
						}
					}
					else if(x>selection.getX()+selection.getWidth()-5 && x<selection.getX()+selection.getWidth()+5){
						if(y>selection.getY()-5 && y<selection.getY()+5){
							p.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
							orientation=Orientation.NE;
							resizeable=true;
						}
						else if(y>selection.getY()+selection.getHeight()-5 && y<selection.getY()+selection.getHeight()+5){
							p.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
							orientation=Orientation.SE;
							resizeable=true;
						}
						else{
							if(resizeable){
								resizeable=false;
								p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							}
							
						}
					}
					else{
						if(resizeable){
							resizeable=false;
							p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			}
		}
		//En mode Insert Place ou Transition, on dessine une figure
		//sous le curseur
		else if(mode == ControlMode.INSERT_PLACE){
			p.drawPlace(x, y);
		}
		else if(mode == ControlMode.INSERT_TRANSITION){
			p.drawTransition(x, y);
		}
	}
	
	public void set(AWTPetriCanvas petriCanvas){
		this.p = petriCanvas;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Drawable selection = p.getSelection();
		if(mode == ControlMode.SELECT){
			if(selection!=null){
				int x = e.getX();
				int y = e.getY();
				if(resizing){
					int xS, yS, widthS, heightS;
					AWTSubnetFigure f = (AWTSubnetFigure) selection;
					switch(orientation){
					case NW:
						xS =(x>f.getMaxX())?f.getMaxX():x;
						yS =(y>f.getMaxY())?f.getMaxY():y;
						widthS = f.getX()+f.getWidth()-xS;
						heightS= f.getY()+f.getHeight()-yS;
						break;
					case NE:
						xS = f.getX();
						yS =(y>f.getMaxY())?f.getMaxY():y;
						widthS = (x<f.getMinX2())?f.getMinX2()-xS:x-xS;
						heightS= f.getY()+f.getHeight()-yS;
						break;
					case SW:
						xS =(x>f.getMaxX())?f.getMaxX():x;
						yS = f.getY();
						widthS = f.getX()+f.getWidth()-xS;
						heightS=(y<f.getMinY2())?f.getMinY2()-yS:y-yS;
						break;
					default:
					case SE:
						xS = f.getX();
						yS = f.getY();
						widthS = (x<f.getMinX2())?f.getMinX2()-xS:x-xS;
						heightS=(y<f.getMinY2())?f.getMinY2()-yS:y-yS;
						break;
					}
					f.resize(xS, yS, widthS, heightS);
					p.render();
				}
				//Sinon, c'est juste un déplacement
				else{
					Drawable d = p.getSelection();
					if(x>=d.getX() && x<=d.getX()+d.getWidth()){
						if(y>=d.getY() && y<=d.getY()+d.getHeight()){
							p.moveSelectionBy(x-origin.x, y-origin.y);
							origin=e.getPoint();
						}
					}
				}
			}
		}
		else if(mode == ControlMode.INSERT_SUBNET){
			int x,y,width,height;
			end = e.getPoint();
			if(origin.x>end.x){
				x=end.x;
				width=origin.x-end.x;
			}
			else{
	        	x=origin.x;
	        	width=end.x-origin.x;
	        }
	        if(origin.y>end.y){
	        	y=end.y;
	        	height=origin.y-end.y;
	        }
	        else{
	        	y=origin.y;
	        	height=end.y-origin.y;
	        }
	        p.drawSubnet(x, y, width, height);
		}
		else if(mode == ControlMode.INSERT_ARC){
			p.drawArc(origin.x, origin.y, e.getX(), e.getY());
		}
	}
	
	@Override
	public void playStopSelect() {
		// TODO Auto-generated method stub
		if(mode == ControlMode.PLAY){
			stopSelect();
		}
		else{
			playSelect();
		}
	}

	@Override
	public void transitionSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.INSERT_TRANSITION;
	}

	@Override
	public void placeSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.INSERT_PLACE;
	}

	@Override
	public void arrowSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.INSERT_ARC;
	}

	@Override
	public void subnetSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.INSERT_SUBNET;
	}

	@Override
	public void cursorSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.SELECT;
	}

	@Override
	public void playSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.PLAY;
	}

	@Override
	public void stopSelect() {
		// TODO Auto-generated method stub
		mode = ControlMode.SELECT;
	}
	
	private void importFile(){
		int ret = fileChooser.showOpenDialog(p);
		if(ret==JFileChooser.APPROVE_OPTION){
			p.importFile(origin.x,origin.y,fileChooser.getSelectedFile());
		}
	}

	private void saveFile(){
		int ret = fileChooser.showSaveDialog(p);
		if(ret==JFileChooser.APPROVE_OPTION){
			p.saveFile(fileChooser.getSelectedFile());
		}
	}
	
	private void showPopup(MouseEvent e) {
		if(p.getSelection()!=null){
			if(p.getSelection() instanceof AWTSubnetFigure){
				place.setEnabled(true);
				trans.setEnabled(true);
				importsub.setEnabled(true);
				save.setEnabled(true);
				del.setEnabled(true);
			}
			else{
				place.setEnabled(false);
				trans.setEnabled(false);
				importsub.setEnabled(false);
				save.setEnabled(false);
				if(p.getSelection() instanceof AWTPlaceFigure){
					AWTPlaceFigure pl = (AWTPlaceFigure)p.getSelection();
					if(pl.getPlace().getData() instanceof emma.model.resources.A){
						del.setEnabled(false);
					}
					else{
						del.setEnabled(true);
					}
				}
				else{
					del.setEnabled(true);
				}
			}
		}
		else{
			place.setEnabled(false);
			trans.setEnabled(false);
			importsub.setEnabled(true);
			save.setEnabled(true);
			del.setEnabled(false);
		}
		origin=e.getPoint();
		contextMenu.show(p,e.getX(),e.getY());
	}
}
