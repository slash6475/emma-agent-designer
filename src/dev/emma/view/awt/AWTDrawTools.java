package emma.view.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import emma.petri.view.ArcFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.ScopeFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.TransitionFigure;

public class AWTDrawTools {
	private static final BasicStroke normalStroke = new BasicStroke();
	private static final BasicStroke pointilleStroke = new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,3.0f,new float[] {2.0f,4.0f},2.0f);
	private static final int contour=3;
	private static final Color tColor = new Color(10,120,255);
	private static final Color actionColor = Color.orange;
	private static final Color fillScopeColor= new Color(240,240,240);
	private static AffineTransform tx = new AffineTransform();
	private static Polygon arrowHead=null;
	private static Color classicColor=Color.black;
	private static Color selectedArcColor = new Color(192,0,255); 
	private static Color selectedColor = Color.gray;
	private static final Color sColor = Color.black;
	private static final Font tokensFont = new Font("Serif", Font.PLAIN, 18);
	private static final Color tokensColor = Color.blue;
	private static final Color pNullColor = new Color(172,172,172);
	private static final Color pSysColor = Color.red;
	private static final Color pLocalColor = Color.yellow;
	private static final Font nameFont = new Font("Serif", Font.PLAIN, 12);
	private static final Font exprFont = new Font("Serif", Font.PLAIN, 10);
	public static void init(){
		arrowHead = new Polygon();
		arrowHead.addPoint(-10,-5);
		arrowHead.addPoint(0,0);
		arrowHead.addPoint(-10,5);
	}
	
	public static void drawArc(Graphics2D g, double zoom, int x1, int y1, int x2, int y2){
		drawArc(g,zoom,x1,y1,x2,y2, false);
	}
	
	public static void drawArc(Graphics2D g, double zoom,int x1, int y1, int x2, int y2, boolean virtual){
		if(virtual){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		g.setColor(classicColor);
		tx.setToIdentity();
	    double angle = Math.atan2(y2-y1, x2-x1);
	    tx.translate(x2, y2);
	    tx.rotate((angle));
	    Graphics2D g2 = (Graphics2D)g.create();
	    g2.setTransform(tx);
	    g2.fill(arrowHead);
	    g2.dispose();
	    g.drawLine((int)(zoom*x1), (int)(zoom*y1), (int)(zoom*x2), (int)(zoom*y2));
	}
	
	public static void drawArc(Graphics2D g, double zoom, ArcFigure a){
		g.setStroke(normalStroke);
		g.setColor((a.isSelected())?selectedArcColor:classicColor);
		tx.setToIdentity();
	    double angle = Math.atan2(a.getY2()-a.getY1(), a.getX2()-a.getX1());
	    tx.translate(a.getX2(), a.getY2());
	    tx.rotate((angle));
	    Graphics2D g2 = (Graphics2D)g.create();
	    g2.setTransform(tx);
	    g2.fill(arrowHead);
	    g2.dispose();
	    g.drawLine((int)(zoom*a.getX1()), (int)(zoom*a.getY1()), (int)(zoom*a.getX2()), (int)(zoom*a.getY2()));
	    if(a.getNotationsVisibility()){
	    	String expr = a.getArc().getExpression();
	    	g.setFont(exprFont);
	    	g.drawString((expr.equals(""))?"[empty]":expr,(int)(zoom*((a.getX1()+a.getX2())/2-10)),(int)(zoom*((a.getY1()+a.getY2())/2+10)));
	    }
	}
	
	public static void drawPlace(Graphics2D g, double zoom, int x, int y, int width, int height){
		drawPlace(g, zoom,x, y, width, height, false);
	}
	
	public static void drawPlace(Graphics2D g, double zoom, int x, int y, int width, int height, boolean virtual){
		if(virtual){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		g.setColor(pNullColor);
		g.fillOval((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
		g.setColor(classicColor);
		g.drawOval((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
	}
	
	public static void drawPlace(Graphics2D g, double zoom, PlaceFigure p){
		int diameter=p.getWidth();
		if(p.isSelected()){
			drawCircSelection(g,zoom,p.getX(),p.getY(),diameter);
		}
		emma.model.resources.Resource res = p.getPlace().getData();
		if(res==null){
			g.setColor(pNullColor);
		}
		else if(res instanceof emma.model.resources.Local){
			g.setColor(pLocalColor);
		}
		else if(res instanceof emma.model.resources.Agent){
			g.setColor(tColor);
		}
		else if(res instanceof emma.model.resources.System){
			g.setColor(pSysColor);
		}
		g.setStroke(normalStroke);
		g.fillOval((int)(zoom*p.getX()), (int)(zoom*p.getY()), (int)(zoom*diameter), (int)(zoom*diameter));
		g.setColor(classicColor);
		g.drawOval((int)(zoom*p.getX()), (int)(zoom*p.getY()), (int)(zoom*diameter), (int)(zoom*diameter));
		if(p.getNotationsVisibility()){
			g.setFont(nameFont);
			if(p.getPlace().getType()!="NULL"){
				g.drawString(p.getPlace().getType()+'/'+p.getName(), (int)(zoom*p.getX()), (int)(zoom*(p.getY()-contour)));
			}
		}
		if(p.getPlace().getTokens().size()>0){
			//On print le nb de tokens
			g.setColor(tokensColor);
			g.setFont(tokensFont);
			g.drawString(String.valueOf(p.getPlace().getTokens().size()),(int)(zoom*(p.getX()+10)),(int)(zoom*(p.getY()+22)));
		}
	}
	public static void drawTransition(Graphics2D g, double zoom, int x, int y, int width, int height){
		drawTransition(g, zoom,x, y, width, height,false);
	}
	public static void drawTransition(Graphics2D g, double zoom, int x, int y, int width, int height, boolean virtual){
		if(virtual){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		g.setColor(tColor);
		g.fillRect((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
		g.setColor(classicColor);
		g.drawRect((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
	}
	
	public static void drawTransition(Graphics2D g, double zoom, TransitionFigure t){
		if(t.isSelected()){
			drawRectSelection(g,zoom,t.getX(),t.getY(),t.getWidth(),t.getHeight());
		}
		g.setColor(tColor);
		g.setStroke(normalStroke);
		g.fillRect((int)(zoom*t.getX()),(int)(zoom*t.getY()),(int)(zoom*t.getWidth()),(int)(zoom*t.getHeight()));
		g.setColor(classicColor);
		g.drawRect((int)(zoom*t.getX()),(int)(zoom*t.getY()),(int)(zoom*t.getWidth()),(int)(zoom*t.getHeight()));
		if(t.getNotationsVisibility()){
			g.setColor(classicColor);
			g.setFont(exprFont);
			g.drawString((t.getTransition().getCondition().equals(""))?"true":t.getTransition().getCondition(), (int)(zoom*t.getX()), (int)(zoom*(t.getY()+t.getHeight()+(5*contour))));
		}
	}
	
	public static void drawSubnet(Graphics2D g, double zoom, int x, int y, int width, int height){
		g.setColor(sColor);
		g.setStroke(pointilleStroke);
		g.drawRect((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
	}
	
	public static void drawScope(Graphics2D g, double zoom, int x, int y, int width, int height) {
		g.setColor(fillScopeColor);
		g.setStroke(normalStroke);
		g.fillRect((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
		g.setColor(sColor);
		g.drawRect((int)(zoom*x), (int)(zoom*y), (int)(zoom*width), (int)(zoom*height));
	}
	
	public static void drawSubnet(Graphics2D g, double zoom,SubnetFigure s){
		if(s.isSelected()){
			drawRectSelection(g,zoom,s.getX(),s.getY(),s.getWidth(),s.getHeight());
		}
		drawSubnet(g,zoom,s.getX(),s.getY(),s.getWidth(),s.getHeight());
		g.setFont(nameFont);
		if(s.getNotationsVisibility()) g.drawString(s.getName(),(int)(zoom*((s.getX()+s.getWidth()/2)-10)), (int)(zoom*(s.getY()-contour)));
	}

	public static void drawScope(Graphics2D g, double zoom, ScopeFigure s){
		if(s.isSelected()){
			drawRectSelection(g,zoom,s.getX(),s.getY(),s.getWidth(),s.getHeight());
		}
		drawScope(g,zoom,s.getX(),s.getY(),s.getWidth(),s.getHeight());
		g.setFont(nameFont);
		if(s.getNotationsVisibility()) g.drawString(s.getName(),(int)(zoom*((s.getX()+s.getWidth()/2)-10)), (int)(zoom*(s.getY()-contour)));
	}
	public static void drawRectSelection(Graphics2D g, double zoom, int x, int y, int width, int height) {
		g.setColor(selectedColor);
		g.setStroke(pointilleStroke);
		g.drawRect((int)(zoom*(x-contour)), (int)(zoom*(y-contour)), (int)(zoom*(width+(2*contour))), (int)(zoom*(height+(2*contour))));
	}
	
	public static void drawCircSelection(Graphics2D g, double zoom, int x, int y, int diameter) {
		g.setColor(selectedColor);
		g.setStroke(pointilleStroke);
		g.drawOval((int)(zoom*(x-contour)), (int)(zoom*(y-contour)), (int)(zoom*(diameter+(2*contour))), (int)(zoom*(diameter+(2*contour))));
	}

	public static void drawVirtualLink(Graphics2D g, double zoom, AWTDrawable d1, AWTDrawable d2) {
		int angle = (int)(Math.toDegrees(Math.atan2(d1.getX()-d2.getX(),d1.getY()-d2.getY())-(Math.PI/2d))+360)%360;
		int ancX1, ancY1, ancX2, ancY2;
		if(angle>=315 || angle<45){
			ancX2 = d2.getWidth();
			ancY2 = d2.getHeight()/2;
			ancX1 = 0;
			ancY1 = d1.getHeight()/2;
		}
		else if(angle>= 45 && angle < 135){
			ancX2 = d2.getWidth()/2;
			ancY2 = 0;
			ancX1 = d1.getWidth()/2;
			ancY1 = d1.getHeight();
		}
		else if (angle>=135 && angle<225){
			ancX2 = 0;
			ancY2 = d2.getHeight()/2;
			ancX1 = d1.getWidth();
			ancY1 = d1.getHeight()/2;
		}
		else{
			ancX2 = d2.getWidth()/2;
			ancY2 = d2.getHeight();
			ancX1 = d1.getWidth()/2;
			ancY1 = 0;
		}
		g.setStroke(pointilleStroke);
		g.drawLine((int)(zoom*(d1.getX()+ancX1)), (int)(zoom*(d1.getY()+ancY1)),(int)(zoom*(d2.getX()+ancX2)), (int)(zoom*(d2.getY()+ancY2)));
	}
}