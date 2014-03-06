package emma.view.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import emma.model.resources.*;
import emma.petri.view.ArcFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.TransitionFigure;

public class AWTDrawTools {
	private static final BasicStroke normalStroke = new BasicStroke();
	private static final BasicStroke pointilleStroke = new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,3.0f,new float[] {2.0f,4.0f},2.0f);
	private static final int contour=3;
	private static final Color tColor = new Color(10,120,255);
	private static final Color tActionColor = Color.orange;
	private static AffineTransform tx = new AffineTransform();
	private static Polygon arrowHead=null;
	private static Color classicColor=Color.black;
	private static Color selectedArcColor = new Color(192,0,255); 
	private static Color selectedColor = Color.gray;
	private static final Color sColor = Color.red;
	private static final Font tokensFont = new Font("Serif", Font.PLAIN, 18);
	private static final Color tokensColor = Color.blue;
	private static final Color pNullColor = new Color(172,172,172);
	private static final Color pSysColor = Color.red;
	private static final Color pLocalColor = Color.yellow;
	private static final Color pAgentColor = tColor;
	private static final Color pActionColor = Color.orange;
	private static final Font nameFont = new Font("Serif", Font.PLAIN, 12);
	private static final Font exprFont = new Font("Serif", Font.PLAIN, 10);
	public static void init(){
		arrowHead = new Polygon();
		arrowHead.addPoint(-10,-5);
		arrowHead.addPoint(0,0);
		arrowHead.addPoint(-10,5);
	}
	
	public static void drawArc(Graphics2D g, int x1, int y1, int x2, int y2){
		drawArc(g,x1,y1,x2,y2, false);
	}
	
	public static void drawArc(Graphics2D g, int x1, int y1, int x2, int y2, boolean virtual){
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
	    g.drawLine(x1, y1, x2, y2);
	}
	
	public static void drawArc(Graphics2D g, ArcFigure a){
		g.setColor((a.isSelected())?selectedArcColor:classicColor);
		tx.setToIdentity();
	    double angle = Math.atan2(a.getY2()-a.getY1(), a.getX2()-a.getX1());
	    tx.translate(a.getX2(), a.getY2());
	    tx.rotate((angle));
	    Graphics2D g2 = (Graphics2D)g.create();
	    g2.setTransform(tx);
	    g2.fill(arrowHead);
	    g2.dispose();
	    g.drawLine(a.getX1(), a.getY1(), a.getX2(), a.getY2());
	    if(a.getNotationsVisibility()){
	    	String expr = a.getArc().getExpression();
	    	g.setFont(exprFont);
	    	g.drawString((expr.equals(""))?"[empty]":expr,(a.getX1()+a.getX2())/2-10,(a.getY1()+a.getY2())/2+10);
	    }
	}
	
	public static void drawPlace(Graphics2D g, int x, int y){
		drawPlace(g, x, y,false);
	}
	
	public static void drawPlace(Graphics2D g, int x, int y, boolean virtual){
		if(virtual){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		int diameter=PlaceFigure.getDefaultWidth();
		g.setColor(pNullColor);
		g.fillOval(x, y, diameter, diameter);
		g.setColor(classicColor);
		g.drawOval(x, y, diameter, diameter);
	}
	
	public static void drawPlace(Graphics2D g, PlaceFigure p){
		int diameter=p.getWidth();
		if(p.isSelected()){
			drawCircSelection(g,p.getX(),p.getY(),diameter);
		}
		Resource res = p.getPlace().getData();
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
		if(p.isVirtual()){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		g.fillOval(p.getX(), p.getY(), diameter, diameter);
		g.setColor(classicColor);
		g.drawOval(p.getX(), p.getY(), diameter, diameter);
		if(p.getNotationsVisibility()){
			g.setFont(nameFont);
			g.drawString(p.getName(), p.getX(), p.getY()-contour);
		}
		if(p.getPlace().getTokens().size()>0){
			//On print le nb de tokens
			g.setColor(tokensColor);
			g.setFont(tokensFont);
			g.drawString(String.valueOf(p.getPlace().getTokens().size()),p.getX()+10,p.getY()+22);
		}
	}
	public static void drawTransition(Graphics2D g, int x, int y, int width, int height){
		drawTransition(g, x, y, width, height,false);
	}
	public static void drawTransition(Graphics2D g, int x, int y, int width, int height, boolean virtual){
		if(virtual){
			g.setStroke(pointilleStroke);
		}
		else{
			g.setStroke(normalStroke);
		}
		g.setColor(tColor);
		g.fillRect(x, y, width, height);
		g.setColor(classicColor);
		g.drawRect(x, y, width, height);
	}
	
	public static void drawTransition(Graphics2D g, TransitionFigure t){
		if(t.isSelected()){
			drawRectSelection(g,t.getX(),t.getY(),t.getWidth(),t.getHeight());
		}
		g.setColor(tColor);
		g.setStroke(normalStroke);
		g.fillRect(t.getX(),t.getY(),t.getWidth(),t.getHeight());
		g.setColor(classicColor);
		g.drawRect(t.getX(),t.getY(),t.getWidth(),t.getHeight());
		if(t.getNotationsVisibility()){
			g.setFont(nameFont);
			g.setColor(classicColor);
			g.drawString(t.getName(), t.getX()-(2*contour), t.getY()-contour);
			g.setFont(exprFont);
			g.drawString((t.getTransition().getCondition().equals(""))?"true":t.getTransition().getCondition(), t.getX(), t.getY()+t.getHeight()+(5*contour));
		}
	}
	
	public static void drawSubnet(Graphics2D g, int x, int y, int width, int height){
		g.setColor(sColor);
		g.setStroke(normalStroke);
		g.drawRect(x, y, width, height);
	}
	
	public static void drawSubnet(Graphics2D g, SubnetFigure s){
		if(s.isSelected()){
			drawRectSelection(g,s.getX(),s.getY(),s.getWidth(),s.getHeight());
		}
		g.setColor(sColor);
		g.setStroke(normalStroke);
		g.drawRect(s.getX(),s.getY(),s.getWidth(),s.getHeight());
		g.setFont(nameFont);
		if(s.getNotationsVisibility()) g.drawString(s.getName(),(s.getX()+s.getWidth()/2)-10, s.getY()-contour);
	}

	public static void drawRectSelection(Graphics2D g, int x, int y, int width, int height) {
		g.setColor(selectedColor);
		g.setStroke(pointilleStroke);
		g.drawRect(x-contour, y-contour, width+(2*contour), height+(2*contour));
	}
	
	public static void drawCircSelection(Graphics2D g, int x, int y, int diameter) {
		g.setColor(selectedColor);
		g.setStroke(pointilleStroke);
		g.drawOval(x-contour, y-contour, diameter+(2*contour), diameter+(2*contour));
	}

	public static void drawVirtualLink(Graphics2D g, AWTDrawable d1, AWTDrawable d2) {
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
			ancY1 = d1.getHeight()/2;
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
		g.drawLine(d1.getX()+ancX1, d1.getY()+ancY1,d2.getX()+ancX2, d2.getY()+ancY2);
	}
}

