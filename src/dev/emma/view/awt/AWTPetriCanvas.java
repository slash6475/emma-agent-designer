package emma.view.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.xml.transform.TransformerException;

import emma.petri.view.Drawable;
import emma.petri.view.PetriCanvas;
import emma.petri.view.PlaceFigure;
import emma.petri.view.TransitionFigure;
import emma.petri.view.XMLParser;

public class AWTPetriCanvas extends Canvas implements PetriCanvas{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7552175117642085050L;
	private Coordinate rect;
	private ToDraw draw;
	private AWTDrawable selectedDraw;
	private AWTNetFigure defaultSub;
	//Double buffering variables
	// variable permettant d'utiliser la mémoire VRAM
	private Thread renderThread;
	private BufferStrategy strategy;
	private Graphics2D gBuffer;
	private boolean rendering;
	
	private class Coordinate{
		public int x1,y1,x2,y2;
		public Coordinate(int x1,int y1,int x2, int y2){
			this.x1=x1;
			this.y1=y1;
			this.x2=x2;
			this.y2=y2;
		}
	}
	
	private enum ToDraw{
		NOTHING,ARC,SUBNET,TRANSITION,PLACE
	}
	
	public AWTPetriCanvas(AWTController c){
		super();
		AWTDrawTools.init();
		draw = ToDraw.NOTHING;
		defaultSub = new AWTNetFigure();
		selectedDraw = null;
		this.setBackground(Color.WHITE);
		if(c!=null){
			this.addMouseMotionListener(c);
			this.addMouseListener(c);
			this.addKeyListener(c);
			c.set(this);
		}
		//On desactive la methode d'affichage
		setIgnoreRepaint(true);
		rendering=false;
	}
	
	//double buffering Paint
	@Override
	public void render() {
		draw(gBuffer);
		//On charge le buffer
		strategy.show();
    }

	public void draw(Graphics2D g) {
		g.clearRect(defaultSub.getX(), defaultSub.getY(), defaultSub.getWidth(), defaultSub.getHeight());
		defaultSub.draw(g);
		//Le controleur peut imposer l'affichage d'un objet qui n'existe pas (prévisualisation)
		//On appelle alors dans ce cas la fonction de dessin en fonction de l'objet demandé
		switch(draw){
		case SUBNET:
			AWTDrawTools.drawSubnet(g,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case ARC:
			AWTDrawTools.drawArc(g,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case PLACE:
			AWTDrawTools.drawPlace(g, rect.x1, rect.y1);
			break;
		case TRANSITION:
			AWTDrawTools.drawTransition(g, rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case NOTHING:
		default:
			break;
		}
	}

	@Override
	public boolean addSubnet(int x, int y, int width, int height) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addSubnetFigure(x,y,width,height);
		render();
		return r;
	
	}

	@Override
	public boolean removeDrawable(Drawable d) {
		if(d!=null){
			if(d==selectedDraw){
				selectedDraw=null;
			}
			d.delete();
			render();
			return true;
		}
		return false;
	}

	@Override
	public boolean drawSubnet(int posX, int posY, int width, int height) {
		draw = ToDraw.SUBNET;
		rect=new Coordinate(posX,posY,width,height);
		render();
		return true;
	}

	@Override
	public boolean drawPlace(int posX, int posY) {
		// TODO Auto-generated method stub
		draw = ToDraw.PLACE;
		rect = new Coordinate(posX-(PlaceFigure.getDefaultWidth()/2),posY-(PlaceFigure.getDefaultHeight()/2),0,0);
		render();
		return true;
	}

	@Override
	public boolean drawTransition(int posX, int posY) {
		// TODO Auto-generated method stub
		draw = ToDraw.TRANSITION;
		rect = new Coordinate(posX-(TransitionFigure.getDefaultWidth()/2),posY-(TransitionFigure.getDefaultHeight()/2),TransitionFigure.getDefaultWidth(),TransitionFigure.getDefaultHeight());
		render();
		return true;
	}

	@Override
	public boolean drawArc(int x1, int y1, int x2, int y2) {
		draw=ToDraw.ARC;
		rect = new Coordinate(x1, y1, x2, y2);
		render();
		return true;
	}

	@Override
	public void removeDraw() {
		if(draw!=ToDraw.NOTHING){
			draw = ToDraw.NOTHING;
			render();
		}
	}

	@Override
	public boolean addPlace(int posX, int posY){
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addPlaceFigure(posX-(PlaceFigure.getDefaultWidth()/2),posY-(PlaceFigure.getDefaultHeight()/2));
		render();
		return r;
	}

	@Override
	public boolean addTransition(int posX, int posY) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addTransitionFigure(posX-(TransitionFigure.getDefaultWidth()/2),posY-(TransitionFigure.getDefaultHeight()/2));
		render();
		return r;
	}

	@Override
	public boolean addArc(int originX, int originY, int endX, int endY) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addArcFigure(originX, originY, endX, endY);
		render();
		return r;
	}

	public void setVisible(boolean b){
		if(b && !rendering){
				//On crée un double buffer en VRAM (video RAM)
				createBufferStrategy(2); 
				strategy = getBufferStrategy(); 
				gBuffer = (Graphics2D)strategy.getDrawGraphics();
				//On execute un thread pour l'affichage double buffer
				renderThread = new Thread(){
					public void run(){
						while(rendering){
							try {
								render(); 
						        sleep(10);
						    } catch ( Exception e ) {} 
						}
			       }
				};
				renderThread.start();
		}
		else if(!b && rendering){
			rendering=false;
		}
	}
	
	//On cherche un arc, si on trouve pas, on appelle la recherche d'un element (Subnet,P,T...)
	@Override
	public Drawable getDrawable(int x, int y) {
		Drawable d = defaultSub.getDrawable(x, y);
		return (d==defaultSub)?null:d;
	}
	
	public boolean select(int x, int y){
		if(selectedDraw!=null){
			selectedDraw.select(false);
		}
		selectedDraw = (AWTDrawable)this.getDrawable(x,y);
		if(selectedDraw!=null){
			selectedDraw.select(true);
			render();
			return true;
		}
		render();
		return false;
	}
	
	@Override
	public void moveSelectionBy( int x, int y){
		if(this.selectedDraw!=null){
			this.selectedDraw.moveBy(x, y);
			render();
		}
	}
	
	@Override
	public Drawable getSelection(){
		return selectedDraw;
	}

	@Override
	public boolean switchNotationsVisibilityOfSelection() {
		if(this.selectedDraw!=null){
			if(selectedDraw.switchNotationsVisibility()){
				render();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean virtualizeSelection(){
		if(this.selectedDraw!=null){
			if(selectedDraw.isVirtualizeable()){
				if(selectedDraw.createVirtualization()){
					render();
					return true;
				}
			}
			System.out.println("WARNING: trying to virtualize unvirtualizeable element");
		}
		return false;
	}
	
	public boolean saveSelection(File f){
		if(selectedDraw!=null){
			if(selectedDraw instanceof AWTSubnetFigure){
				try {
					XMLParser.saveSubnetFigureToXMLFile((AWTSubnetFigure)selectedDraw, f);
					return true;
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			try {
				XMLParser.saveSubnetFigureToXMLFile(defaultSub, f);
				return true;
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean importFile(int x, int y, File f) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean saveFile(File f){
		if(selectedDraw!=null){
			if(selectedDraw instanceof AWTSubnetFigure){
				try {
					XMLParser.saveSubnetFigureToXMLFile((AWTSubnetFigure)selectedDraw, f);
					return true;
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			try {
				XMLParser.saveSubnetFigureToXMLFile(defaultSub, f);
				return true;
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
