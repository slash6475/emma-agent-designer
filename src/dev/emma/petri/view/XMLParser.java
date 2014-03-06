package emma.petri.view;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import emma.view.awt.AWTPlaceFigure;
import emma.view.awt.AWTSubnetFigure;

public class XMLParser {
	private static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static DocumentBuilder docBuilder;
	private static Transformer transformer;
	
	public static void init() throws ParserConfigurationException, TransformerConfigurationException{
		docBuilder = docFactory.newDocumentBuilder();
		transformer = transformerFactory.newTransformer();
	}
	
	private static Element getPlaceElement(Document doc,PlaceFigure p, int offsetX, int offsetY){
		Element elmt = doc.createElement("place");
		Element virtualized = doc.createElement("virtualizedBy");
		Element input = doc.createElement("input");
		Element output = doc.createElement("output");
		elmt.setAttribute("id", String.valueOf(p.getID()));
		elmt.setAttribute("name", p.getName());
		elmt.setAttribute("x", String.valueOf(p.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(p.getY()-offsetY));
		elmt.appendChild(virtualized);
		if(p instanceof VirtualPlaceFigure){
			Element virtualize = doc.createElement("virtualize");
			elmt.appendChild(virtualize);
			elmt.setAttribute("class", "virtual");
			virtualize.appendChild(doc.createTextNode(String.valueOf(((VirtualPlaceFigure)p).getLinkedPlaceFigure().getID())));
		}
		else{
			Element data = doc.createElement("data");
			Element val = doc.createElement("value");
			elmt.appendChild(data);
			data.appendChild(val);
			data.setAttribute("class", p.getPlace().getType());
			if(p.getPlace().getData()!=null)val.appendChild(doc.createCDATASection(p.getPlace().getData().toString()));
		}
		elmt.appendChild(input);
		elmt.appendChild(output);
		input.appendChild(doc.createTextNode((p.getPlace().isInput())?"true":"false"));
		output.appendChild(doc.createTextNode((p.getPlace().isOutput())?"true":"false"));
		return elmt;
	}
	
	private static Element getTransitionElement(Document doc,TransitionFigure t, int offsetX, int offsetY){
		Element elmt = doc.createElement("transition");
		Element place = doc.createElement("place");
		Element cond = doc.createElement("condition");
		elmt.setAttribute("id", String.valueOf(t.getID()));
		elmt.setAttribute("name", t.getName());
		elmt.setAttribute("x", String.valueOf(t.getX()-offsetX));
		elmt.setAttribute("y", String.valueOf(t.getY()-offsetY));
		elmt.appendChild(place);
		elmt.appendChild(cond);
		//place.appendChild(doc.createTextNode(String.valueOf(t.getTransition().getPlace().getID())));
		cond.appendChild(doc.createCDATASection(t.getTransition().getCondition()));
		return elmt;
	}
	
	private static Element getArcElement(Document doc,ArcFigure a){
		Element elmt = doc.createElement("arc");
		Element place = doc.createElement("place");
		Element transition = doc.createElement("transition");
		Element expr = doc.createElement("expression");
		//TODO
		elmt.setAttribute("id", String.valueOf(a.getID()));
		if(a.getArc() instanceof emma.petri.model.InputArc){
			elmt.setAttribute("class", "input");
		}
		else{
			elmt.setAttribute("class", "output");
		}
		elmt.appendChild(place);
		elmt.appendChild(transition);
		elmt.appendChild(expr);
		place.appendChild(doc.createTextNode(String.valueOf(a.getPlaceFigure().getID())));
		transition.appendChild(doc.createTextNode(String.valueOf(a.getTransitionFigure().getID())));
		expr.appendChild(doc.createCDATASection(a.getArc().getExpression()));
		return elmt;
	}

	private static Element getSubnetElement(Document doc,SubnetFigure sub, int offsetX, int offsetY){
		Element elmt = doc.createElement("subnet");
		Element places = doc.createElement("places");
		Element transitions = doc.createElement("transitions");
		Element arcs = doc.createElement("arcs");
		Element subnets = doc.createElement("subnets");
		
		if(sub instanceof NetFigure){
			elmt.setAttribute("class", "net");
		}
		else{
			elmt.setAttribute("id", String.valueOf(sub.getID()));
			elmt.setAttribute("name", sub.getName());
			elmt.setAttribute("x", String.valueOf(sub.getX()-offsetX));
			elmt.setAttribute("y", String.valueOf(sub.getY()-offsetY));
			elmt.setAttribute("width", String.valueOf(sub.getWidth()));
			elmt.setAttribute("height", String.valueOf(sub.getHeight()));
		}
		
		Iterator<SubnetFigure> is = sub.getSubnetFigures().iterator();
		elmt.appendChild(subnets);
		while(is.hasNext()){
			subnets.appendChild(getSubnetElement(doc,is.next(),offsetX,offsetY));
		}
		Iterator<PlaceFigure> ip = sub.getPlaceFigures().iterator();
		elmt.appendChild(places);
		while(ip.hasNext()){
			places.appendChild(getPlaceElement(doc,ip.next(),offsetX,offsetY));
		}
		Iterator<TransitionFigure> it = sub.getTransitionFigures().iterator();
		elmt.appendChild(transitions);
		while(it.hasNext()){
			transitions.appendChild(getTransitionElement(doc,it.next(),offsetX,offsetY));
		}
		Iterator<ArcFigure> ia = sub.getArcFigures().iterator();
		elmt.appendChild(arcs);
		while(ia.hasNext()){
			arcs.appendChild(getArcElement(doc,ia.next()));
		}
		return elmt;
	}
	
	public static void saveSubnetFigureToXMLFile(SubnetFigure sub, File toFile) throws TransformerException{
		Document doc = docBuilder.newDocument();
		doc.appendChild(getSubnetElement(doc,sub,sub.getX(),sub.getY()));
		DOMSource source = new DOMSource(doc);
		// write the content into xml file
		StreamResult result = new StreamResult(toFile);
		transformer.transform(source, result);
	}
	
	public static SubnetFigure importAWTSubnetFigureFromXMLFile(int offsetX, int offsetY, File fromFile, SubnetFigure parent) throws SAXException, IOException{
		Document doc = docBuilder.parse(fromFile);
		doc.getDocumentElement().normalize();
		
		return null;
	}
	
	private static int parseInt(String toParse){
		try{
			return Integer.parseInt(toParse);
		}
		catch(NumberFormatException e){
		}
		return 0;
	}
	
	private static SubnetFigure getAWTSubnetByElement(Element elmt, int offsetX, int offsetY, SubnetFigure parent){
		int x = offsetX+parseInt(elmt.getAttribute("x"));
		int y = offsetY+parseInt(elmt.getAttribute("y"));
		int width=parseInt(elmt.getAttribute("width"));
		int height=parseInt(elmt.getAttribute("height"));
		AWTSubnetFigure s = new AWTSubnetFigure(x,y,width,height,parent);
		s.setName(elmt.getAttribute("name"));
		if(s==null) return null;
		NodeList childs = elmt.getChildNodes();
		for(int i=0; i<childs.getLength(); i++){
			Node n = childs.item(i);
			NodeList subchilds = n.getChildNodes();
			if(n.getNodeName().equals("subnets")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						s.addFigure(getAWTSubnetByElement((Element)subchilds.item(j),offsetX,offsetY,s));
					}
				}
			}
			else if(n.getNodeName().equals("places")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						s.addFigure(getAWTPlaceByElement((Element)subchilds.item(j),offsetX,offsetY,s));
					}
				}
			}
			else if(n.getNodeName().equals("transitions")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						//s.addFigure(getAWTTransitionByElement((Element)subchilds.item(j),offsetX,offsetY,p,s));
					}
				}
			}
			else if(n.getNodeName().equals("arcs")){
				for(int j=0; j<subchilds.getLength(); j++){
					if(subchilds.item(j).getNodeType()==Node.ELEMENT_NODE){
						//s.addFigure(getAWTArcByElement((Element)subchilds.item(j),PlaceFigure p, TransitionFigure t,s));
					}
				}
			}
		}
		return s;
	}
	
	private static PlaceFigure getAWTPlaceByElement(Element elmt, int offsetX, int offsetY, SubnetFigure s){
		int x = offsetX+parseInt(elmt.getAttribute("x"));
		int y = offsetY+parseInt(elmt.getAttribute("y"));
		
		return null;
	}
	
	private static ArcFigure getAWTArcByElement(Element elmt, PlaceFigure p, TransitionFigure t, SubnetFigure s){
		return null;
	}
	
	private static TransitionFigure getAWTTransitionFigure(Element elmt, int offsetX, int offsetY, PlaceFigure p, SubnetFigure s){
		return null;
	}
}
