package emma.petri.view;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import emma.view.awt.AWTArcFigure;
import emma.view.awt.AWTPlaceFigure;
import emma.view.awt.AWTSubnetFigure;
import emma.view.awt.AWTTransitionFigure;

public class XMLParser {
	private static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static DocumentBuilder docBuilder;
	private static Transformer transformer;
	private Document doc;
	private Hashtable<String,SubnetFigure> subTable;
	private Hashtable<String,PlaceFigure> placeTable;
	private Hashtable<String,TransitionFigure> transTable;
	private Hashtable<String, ScopeFigure> scopeTable;
	
	public static void init() throws ParserConfigurationException, TransformerConfigurationException{
		docBuilder = docFactory.newDocumentBuilder();
		transformer = transformerFactory.newTransformer();
	}
	
	private Element getScopeElement(ScopeFigure s, int offsetX, int offsetY){
		Element elmt = doc.createElement("scope");
		elmt.setAttribute("id", String.valueOf(s.getID()));
		elmt.setAttribute("name", s.getName());
		elmt.setAttribute("x", String.valueOf(s.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(s.getY()-offsetY));
		
		return elmt;
	}
	
	
	private Element getPlaceElement(PlaceFigure p, int offsetX, int offsetY){
		Element elmt = doc.createElement("place");
		elmt.setAttribute("id", String.valueOf(p.getID()));
		elmt.setAttribute("name", p.getName());
		elmt.setAttribute("x", String.valueOf(p.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(p.getY()-offsetY));
		Element data = doc.createElement("data");
		elmt.appendChild(data);
		data.setAttribute("class", p.getPlace().getType());
		if(p.getPlace().getData()!=null)data.appendChild(doc.createCDATASection(p.getPlace().getData().toString()));
		elmt.setAttribute("input",p.getPlace().isInput()?"true":"false");
		elmt.setAttribute("output",p.getPlace().isOutput()?"true":"false");
		return elmt;
	}
	
	private Element getTransitionElement(TransitionFigure t, int offsetX, int offsetY){
		Element elmt = doc.createElement("transition");
		elmt.setAttribute("id", String.valueOf(t.getID()));
		elmt.setAttribute("x", String.valueOf(t.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(t.getY()-offsetY));
		elmt.setAttribute("place",String.valueOf(t.getTransition().getPlace().getID()));
		elmt.setAttribute("condition",t.getTransition().getCondition());
		return elmt;
	}
	
	private Element getArcElement(ArcFigure a){
		Element elmt = doc.createElement("arc");
		//TODO
		elmt.setAttribute("id", String.valueOf(a.getID()));
		if(a.getArc() instanceof emma.petri.model.InputArc){
			elmt.setAttribute("class", "input");
		}
		else{
			elmt.setAttribute("class", "output");
		}
		elmt.setAttribute("place", String.valueOf(a.getPlaceFigure().getID()));
		elmt.setAttribute("transition", String.valueOf(a.getTransitionFigure().getID()));
		elmt.setAttribute("expression", a.getArc().getExpression());
		return elmt;
	}

	private Element getSubnetElement(SubnetFigure sub, int offsetX, int offsetY){
		Element elmt = doc.createElement("subnet");
		Element places = doc.createElement("places");
		Element transitions = doc.createElement("transitions");
		Element arcs = doc.createElement("arcs");
		Element subnets = doc.createElement("subnets");
		elmt.setAttribute("id", String.valueOf(sub.getID()));
		elmt.setAttribute("name", sub.getName());
		elmt.setAttribute("x", String.valueOf(sub.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(sub.getY()-offsetY));
		elmt.setAttribute("width", String.valueOf(sub.getWidth()));
		elmt.setAttribute("height", String.valueOf(sub.getHeight()));
		/*
		Iterator<SubnetFigure> is = sub.getSubnetFigures().iterator();
		elmt.appendChild(subnets);
		while(is.hasNext()){
			subnets.appendChild(getSubnetElement(is.next(),offsetX,offsetY));
		}
		Iterator<PlaceFigure> ip = sub.getPlaceFigures().iterator();
		elmt.appendChild(places);
		while(ip.hasNext()){
			places.appendChild(getPlaceElement(ip.next(),offsetX,offsetY));
		}
		Iterator<TransitionFigure> it = sub.getTransitionFigures().iterator();
		elmt.appendChild(transitions);
		while(it.hasNext()){
			transitions.appendChild(getTransitionElement(it.next(),offsetX,offsetY));
		}
		Iterator<ArcFigure> ia = sub.getArcFigures().iterator();
		elmt.appendChild(arcs);
		while(ia.hasNext()){
			arcs.appendChild(getArcElement(ia.next()));
		}*/
		return elmt;
	}
	
	public void saveSubnetFigureToXMLFile(SubnetFigure sub, File toFile) throws TransformerException{
		doc = docBuilder.newDocument();
		doc.appendChild(getSubnetElement(sub,sub.getX(),sub.getY()));
		DOMSource source = new DOMSource(doc);
		// write the content into xml file
		StreamResult result = new StreamResult(toFile);
		transformer.transform(source, result);
	}
	
	public SubnetFigure importAWTSubnetFigureFromXMLFile(int offsetX, int offsetY, File fromFile, SubnetFigure parent) throws SAXException, IOException{
		Document doc = docBuilder.parse(fromFile);
		this.subTable=new Hashtable<String,SubnetFigure>();
		this.placeTable=new Hashtable<String,PlaceFigure>();
		this.transTable=new Hashtable<String,TransitionFigure>();
		doc.getDocumentElement().normalize();
		return getAWTSubnetByElement(doc.getDocumentElement(),offsetX,offsetY,parent);
	}
	
	private static int parseInt(String toParse){
		try{
			return Integer.parseInt(toParse);
		}
		catch(NumberFormatException e){
		}
		return 0;
	}
	
	private SubnetFigure getAWTSubnetByElement(Element elmt, int offsetX, int offsetY, SubnetFigure parent){
		/*int x = offsetX+parseInt(elmt.getAttribute("x"));
		int y = offsetY+parseInt(elmt.getAttribute("y"));
		int width=parseInt(elmt.getAttribute("width"));
		int height=parseInt(elmt.getAttribute("height"));
		AWTSubnetFigure s = new AWTSubnetFigure(x,y,width,height,parent);
		this.subTable.put(elmt.getAttribute("id"), s);
		s.setName(elmt.getAttribute("name"));
		NodeList childs = elmt.getChildNodes();
		//On quadruple la boucle pour assurer que l'on effectue l'insertion dans l'ordre
		//On décale la boucle. Ainsi si le fichier est ecrit dans l'ordre, on ne parcours qu'une fois les boucles;
		int i,dec;
		dec=0;
		for(i=0; i<childs.getLength(); i++){
			Node n = childs.item(i);
			NodeList subchilds = n.getChildNodes();
			if(n.getNodeName().equals("subnets")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						//s.addFigure(getAWTSubnetByElement((Element)subchilds.item(j),offsetX,offsetY,s));
						break;
					}
				}
			}
		}
		dec=i+1;
		for(i=dec; i<childs.getLength()+dec; i++){
			Node n = childs.item(i%4);
			NodeList subchilds = n.getChildNodes();
			if(n.getNodeName().equals("places")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						PlaceFigure p = getAWTPlaceByElement((Element)subchilds.item(j),offsetX,offsetY,s); 
						if(p!=null){
							//s.addFigure(p);
						}
						break;
					}
				}
			}
		}
		dec=i+1;
		for(i=dec; i<childs.getLength()+dec; i++){
			Node n = childs.item(i%4);
			NodeList subchilds = n.getChildNodes();
			if(n.getNodeName().equals("transitions")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						TransitionFigure t = getAWTTransitionByElement((Element)subchilds.item(j),offsetX,offsetY,s);
						if(t!=null){
							//s.addFigure(t);
						}
						break;
					}
				}
			}
		}
		dec=i+1;
		for(i=dec; i<childs.getLength()+dec; i++){
			Node n = childs.item(i%4);
			NodeList subchilds = n.getChildNodes();
			if(n.getNodeName().equals("arcs")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						s.addFigure(getAWTArcByElement((Element)subchilds.item(j),s));
						break;
					}
				}
			}
		}
		return s;*/ return null;
	}
	
	private PlaceFigure getAWTPlaceByElement(Element elmt, int offsetX, int offsetY, SubnetFigure s){
		int x = offsetX+parseInt(elmt.getAttribute("x"));
		int y = offsetY+parseInt(elmt.getAttribute("y"));
		PlaceFigure p=null;
		String input = elmt.getAttribute("input");
		String output = elmt.getAttribute("output");
		//p = new AWTPlaceFigure(x,y,s);
		Element d = (Element)elmt.getElementsByTagName("data").item(0);
			if(d!=null){
				if(d.getAttribute("class").equals("local")){
					p.getPlace().setType(emma.model.resources.L.class);
					p.getPlace().getData().post(d.getTextContent());
				}
				else if(d.getAttribute("class").equals("agent")){
					p.getPlace().setType(emma.model.resources.A.class);
					p.getPlace().getData().post(d.getTextContent());
				}
				else if(d.getAttribute("class").equals("system")){
					p.getPlace().setType(emma.model.resources.S.class);
				}
			}
		/*p.setName(elmt.getAttribute("name"));
		placeTable.put(elmt.getAttribute("id"), p);
		if(!input.equals("")){
			p.getPlace().setInput((input.equals("false"))?false:true);
		}
		if(!output.equals("")){
			p.getPlace().setOutput((output.equals("false"))?false:true);
		}*/
		return p;
	}
	
	private ArcFigure getAWTArcByElement(Element elmt, SubnetFigure s){
		ArcFigure a=null;
		String place = elmt.getAttribute("place");
		String trans = elmt.getAttribute("transition");
		String expr = elmt.getAttribute("expression");
		if(!(place.equals("") || trans.equals(""))){
			a = new AWTArcFigure(placeTable.get(place), transTable.get(trans),(elmt.getAttribute("class").equals("output"))?false:true);
			if(!expr.equals("")){
				a.getArc().setExpression(expr);
			}
		}
		return a;
	}
	
	private TransitionFigure getAWTTransitionByElement(Element elmt, int offsetX, int offsetY, SubnetFigure s){
		TransitionFigure t=null;
		int x = offsetX+parseInt(elmt.getAttribute("x"));
		int y = offsetY+parseInt(elmt.getAttribute("y"));
		String place = elmt.getAttribute("place");
		String cond = elmt.getAttribute("cond");
		if(!place.equals("")){
			//t = new AWTTransitionFigure(x, y, s, placeTable.get(place));
			if(!cond.equals("")){
				//t.getTransition().setCondition(cond);
			}
			transTable.put(elmt.getAttribute("id"), t);
		}
		return t;
	}
}
