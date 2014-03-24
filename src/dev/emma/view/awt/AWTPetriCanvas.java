package emma.view.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.xml.transform.TransformerException;

import emma.petri.view.Drawable;
import emma.petri.view.PetriCanvas;
import emma.petri.view.PlaceFigure;
import emma.petri.view.TransitionFigure;
import emma.petri.view.XMLParser;
import emma.view.FigureHandler;
import emma.view.test.AWTController;

public class AWTPetriCanvas extends Canvas implements PetriCanvas{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7552175117642085050L;
	private Coordinate rect;
	private Coordinate toDisplay;
	private ToDraw draw;
	private AWTDrawable selectedDraw;
	private AWTNetFigure defaultSub;
	//Double buffering variables
	// variable permettant d'utiliser la mémoire VRAM
	private BufferStrategy strategy;
	private Graphics2D gBuffer;
	private double zoom;
	private Dimension size;
	
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
		NOTHING,ARC,SUBNET,SCOPE,TRANSITION,PLACE
	}
	
	public AWTPetriCanvas(FigureHandler c){
		super();
		this.size=new Dimension(1200,900);
		this.setSize(size);
		this.draw = ToDraw.NOTHING;
		this.defaultSub = new AWTNetFigure();
		this.selectedDraw = null;
		this.setBackground(Color.WHITE);
		if(c instanceof AWTController){
			this.addMouseMotionListener((MouseMotionListener)c);
			this.addMouseListener((MouseListener)c);
			this.addKeyListener((KeyListener) c);
		}
		c.setCanvas(this);
		//On desactive la methode d'affichage
		this.setIgnoreRepaint(true);
		//SET ZOOM;
		this.zoom=1;
	}
	
	//double buffering Paint
	@Override
	public void render() {
		this.draw(gBuffer);
		//On charge le buffer
		strategy.show();
    }

	public void draw(Graphics2D g) {
		g.clearRect(toDisplay.x1, toDisplay.y1, toDisplay.x2, toDisplay.y2);
		defaultSub.draw(g,zoom,toDisplay.x1, toDisplay.y1, toDisplay.x2, toDisplay.y2);
		//Le controleur peut imposer l'affichage d'un objet qui n'existe pas (prévisualisation)
		//On appelle alors dans ce cas la fonction de dessin en fonction de l'objet demandé
		switch(draw){
		case SUBNET:
			AWTDrawTools.drawSubnet(g,zoom,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case ARC:
			AWTDrawTools.drawArc(g,zoom,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case PLACE:
			AWTDrawTools.drawPlace(g,zoom,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case TRANSITION:
			AWTDrawTools.drawTransition(g,zoom,rect.x1, rect.y1, rect.x2, rect.y2);
			break;
		case SCOPE:
			AWTDrawTools.drawScope(g,zoom,rect.x1, rect.y1, rect.x2, rect.y2);
		case NOTHING:
		default:
			break;
		}
	}

	@Override
	public boolean addSubnet(int x, int y, int width, int height) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addSubnetFigure((int)(x/zoom),(int)(y/zoom),(int)(width/zoom),(int)(height/zoom));
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
		rect=new Coordinate((int)(posX/zoom),(int)(posY/zoom),(int)(width/zoom),(int)(height/zoom));
		render();
		return true;
	}

	@Override
	public boolean drawScope(int posX, int posY, int width, int height) {
		// TODO Auto-generated method stub
		draw = ToDraw.SCOPE;
		rect=new Coordinate((int)(posX/zoom),(int)(posY/zoom),(int)(width/zoom),(int)(height/zoom));
		render();
		return true;
	}
	
	@Override
	public boolean drawPlace(int posX, int posY) {
		int width = PlaceFigure.getDefaultWidth();
		int height = PlaceFigure.getDefaultHeight();
		draw = ToDraw.PLACE;
		rect = new Coordinate((int)(posX/zoom)-(width/2),(int)(posY/zoom)-(height/2),width,height);
		render();
		return true;
	}

	@Override
	public boolean drawTransition(int posX, int posY){
		int width = TransitionFigure.getDefaultWidth();
		int height = TransitionFigure.getDefaultHeight();
		draw = ToDraw.TRANSITION;
		rect = new Coordinate((int)(posX/zoom)-(width/2),(int)(posY/zoom)-(height/2),width,height);
		render();
		return true;
	}

	@Override
	public boolean drawArc(int x1, int y1, int x2, int y2) {
		draw=ToDraw.ARC;
		rect = new Coordinate((int)(x1/zoom),(int)(y1/zoom), (int)(x2/zoom),(int)(y2/zoom));
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
		boolean r = defaultSub.addPlaceFigure((int)(posX/zoom)-(PlaceFigure.getDefaultWidth()/2),(int)(posY/zoom)-(PlaceFigure.getDefaultHeight()/2));
		render();
		return r;
	}

	@Override
	public boolean addTransition(int posX, int posY) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addTransitionFigure((int)(posX/zoom)-(TransitionFigure.getDefaultWidth()/2),(int)(posY/zoom)-(TransitionFigure.getDefaultHeight()/2));
		render();
		return r;
	}

	@Override
	public boolean addArc(int originX, int originY, int endX, int endY) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addArcFigure((int)(originX/zoom),(int)(originY/zoom),(int)(endX/zoom),(int)(endY/zoom));
		render();
		return r;
	}
	
	@Override
	public boolean addScope(int x, int y, int width, int height) {
		draw=ToDraw.NOTHING;
		boolean r = defaultSub.addScopeFigure((int)(x/zoom),(int)(y/zoom),(int)(width/zoom),(int)(height/zoom));
		render();
		return r;
	}

	public void setVisible(boolean b){
		if(b){
			//On crée un double buffer en VRAM (video RAM)
			createBufferStrategy(2); 
			strategy = getBufferStrategy(); 
			gBuffer = (Graphics2D)strategy.getDrawGraphics();
		}
	}
	
	//On cherche un arc, si on trouve pas, on appelle la recherche d'un element (Subnet,P,T...)
	@Override
	public Drawable getDrawable(int x, int y) {
		Drawable d = defaultSub.getDrawable((int)(x/zoom),(int)(y/zoom));
		return (d==defaultSub)?null:d;
	}
	
	public boolean select(int x, int y){
		if(selectedDraw!=null){
			selectedDraw.unselect();
		}
		selectedDraw = (AWTDrawable)this.getDrawable(x,y);
		if(selectedDraw!=null){
			selectedDraw.select();
			render();
			return true;
		}
		render();
		return false;
	}
	
	@Override
	public boolean moveSelectionBy(int x, int y){
		if(this.selectedDraw!=null){
			if(this.selectedDraw.moveBy((int)(x/zoom),(int)(y/zoom))){
				render();
				return true;
			}
		}
		return false;
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
	public boolean importFile(int x, int y, File f) {
		/*if(selectedDraw instanceof SubnetFigure){
			SubnetFigure s = (SubnetFigure) selectedDraw;
			try {
				XMLParser parser = new XMLParser();
				SubnetFigure sub = parser.importAWTSubnetFigureFromXMLFile((int)(x/zoom),(int)(y/zoom), f, s);
				if(sub!=null){
					if(s.addFigure(sub)){
						minimize();
						render();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return false;
	}
	@Override
	public boolean saveFile(File f){
		if(selectedDraw!=null){
			if(selectedDraw instanceof AWTSubnetFigure){
				try {
					XMLParser parser = new XMLParser();
					parser.saveSubnetFigureToXMLFile((AWTSubnetFigure)selectedDraw, f);
					return true;
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			/*try {
				XMLParser parser = new XMLParser();
				parser.saveSubnetFigureToXMLFile(defaultSub, f);
				return true;
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		return false;
	}

	@Override
	public boolean zoom() {
		if(zoom<1){
			if(zoom+0.1<=1){
				zoom+=0.1;
			}
			else{
				zoom=1;
			}
			this.setSize((int)(zoom*size.width), (int)(zoom*size.height));
			render();
			return true;
		}
		return false;
	}

	@Override
	public boolean unzoom() {
		if(zoom>0.1){
			if(zoom-0.1>=0.1){
				zoom-=0.1;
			}
			else{
				zoom=0.1;
			}
			this.setSize((int)(zoom*size.width), (int)(zoom*size.height));
			render();
			return true;
		}
		return false;
	}

	@Override
	public boolean setZoom(double zoom) {
		if(zoom>=0.1 && zoom<=1){
			this.zoom=zoom;
			this.setSize((int)(zoom*size.width), (int)(zoom*size.height));
			render();
			return true;
		}
		return false;
	}
	@Override
	public double getZoom() {
		return zoom;
	}
	
	@Override
	public void setViewPort(int x, int y, int width, int height){
		toDisplay = new Coordinate(x,y,width,height);
		render();
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}

}
