package emma.petri.view;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import emma.petri.control.listener.*;
import emma.petri.model.Arc;
import emma.petri.model.InputArc;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;
import emma.view.swing.petri.NetFigure;
import emma.view.swing.petri.SubnetFigure;
import emma.view.swing.petri.SwingPetriFigure;

public class XMLParser {
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private DocumentBuilder docBuilder;
	private Transformer transformer;
	private Document doc;
	private Element layout;
	
	public XMLParser() throws ParserConfigurationException, TransformerConfigurationException{
		docBuilder = docFactory.newDocumentBuilder();
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	}
	
	private Element getSubnetElement(Subnet sub){
		Element elmt = doc.createElement("subnet");
		Iterator<SubnetListener> it = sub.getListeners().iterator();
		while(it.hasNext()){
			SubnetListener l = it.next();
			if(l instanceof SwingPetriFigure){
				SwingPetriFigure fig = (SwingPetriFigure) l;
				Element figElmt = doc.createElement("subnet_figure");
				figElmt.setAttribute("id", "swing"+sub.getID());
				figElmt.setAttribute("class", "swing");
				figElmt.setAttribute("x", String.valueOf(fig.getX()));
				figElmt.setAttribute("y", String.valueOf(fig.getY()));
				figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
				figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
				layout.appendChild(figElmt);
			}
		}
		elmt.setAttribute("id", String.valueOf(sub.getID()));
		elmt.setAttribute("name", sub.getName());
		Element scopes = doc.createElement("scopes");
		Iterator<Scope> itScope = sub.getScopes().iterator();
		while(itScope.hasNext()){
			scopes.appendChild(this.getScopeElement(itScope.next()));
		}
		elmt.appendChild(scopes);
		Element arcs = doc.createElement("arcs");
		Iterator<Arc> itArc = sub.getArcs().iterator();
		while(itArc.hasNext()){
			arcs.appendChild(this.getArcElement(itArc.next()));
		}
		elmt.appendChild(arcs);
		return elmt;
	}
	
	private Element getScopeElement(Scope s){
		Element elmt = doc.createElement("scope");
		Iterator<ScopeListener> it = s.getListeners().iterator();
		while(it.hasNext()){
			ScopeListener l = it.next();
			if(l instanceof SwingPetriFigure){
				SwingPetriFigure fig = (SwingPetriFigure) l;
				Element figElmt = doc.createElement("scope_figure");
				figElmt.setAttribute("id", "swing"+s.getID());
				figElmt.setAttribute("class", "swing");
				figElmt.setAttribute("x", String.valueOf(fig.getX()));
				figElmt.setAttribute("y", String.valueOf(fig.getY()));
				figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
				figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
				layout.appendChild(figElmt);
			}
		}
		elmt.setAttribute("id", String.valueOf(s.getID()));
		elmt.setAttribute("name", s.getName());
		
		Element places = doc.createElement("places");
		Iterator<Place> itPlace = s.getPlaces().iterator();
		while(itPlace.hasNext()){
			places.appendChild(this.getPlaceElement(itPlace.next()));
		}
		elmt.appendChild(places);
		Element transitions = doc.createElement("transitions");
		Iterator<Transition> itTransition = s.getTransitions().iterator();
		while(itTransition.hasNext()){
			transitions.appendChild(this.getTransitionElement(itTransition.next()));
		}
		elmt.appendChild(transitions);	
		return elmt;
	}
	
	
	private Element getPlaceElement(Place p){
		Element elmt = doc.createElement("place");
		Iterator<PlaceListener> it = p.getListeners().iterator();
		while(it.hasNext()){
			PlaceListener l = it.next();
			if(l instanceof SwingPetriFigure){
				SwingPetriFigure fig = (SwingPetriFigure) l;
				Element figElmt = doc.createElement("place_figure");
				figElmt.setAttribute("id", "swing"+p.getID());
				figElmt.setAttribute("class", "swing");
				figElmt.setAttribute("x", String.valueOf(fig.getX()));
				figElmt.setAttribute("y", String.valueOf(fig.getY()));
				figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
				figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
				layout.appendChild(figElmt);
			}
		}
		elmt.setAttribute("id", String.valueOf(p.getID()));
		elmt.setAttribute("name", p.getName());
		if(p.getData()!=null){
			Element data = doc.createElement("data");
			data.setAttribute("class", p.getType());
			data.appendChild(doc.createCDATASection(p.getData().toString()));
			elmt.appendChild(data);
		}
		elmt.setAttribute("input",p.isInput()?"true":"false");
		elmt.setAttribute("output",p.isOutput()?"true":"false");
		return elmt;
	}
	
	private Element getTransitionElement(Transition t){
		Element elmt = doc.createElement("transition");
		Iterator<TransitionListener> it = t.getListeners().iterator();
		while(it.hasNext()){
			TransitionListener l = it.next();
			if(l instanceof SwingPetriFigure){
				SwingPetriFigure fig = (SwingPetriFigure) l;
				Element figElmt = doc.createElement("transition_figure");
				figElmt.setAttribute("id", "swing"+t.getID());
				figElmt.setAttribute("class", "swing");
				figElmt.setAttribute("x", String.valueOf(fig.getX()));
				figElmt.setAttribute("y", String.valueOf(fig.getY()));
				figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
				figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
				layout.appendChild(figElmt);
			}
		}
		elmt.setAttribute("id", String.valueOf(t.getID()));
		elmt.setAttribute("place",String.valueOf(t.getPlace().getID()));
		elmt.setAttribute("condition",t.getCondition());
		return elmt;
	}
	
	private Element getArcElement(Arc a){
		Element elmt = doc.createElement("arc");
		elmt.setAttribute("id", String.valueOf(a.getID()));
		if(a instanceof InputArc){
			elmt.setAttribute("class", "input");
			Iterator<InputArcListener> it = ((InputArc)a).getListeners().iterator();
			while(it.hasNext()){
				InputArcListener l = it.next();
				if(l instanceof SwingPetriFigure){
					SwingPetriFigure fig = (SwingPetriFigure) l;
					Element figElmt = doc.createElement("inputarc_figure");
					figElmt.setAttribute("id", "swing"+a.getID());
					figElmt.setAttribute("class", "swing");
					figElmt.setAttribute("x", String.valueOf(fig.getX()));
					figElmt.setAttribute("y", String.valueOf(fig.getY()));
					figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
					figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
					layout.appendChild(figElmt);
				}
			}
		}
		else{
			elmt.setAttribute("class", "output");
			Iterator<OutputArcListener> it = ((OutputArc)a).getListeners().iterator();
			while(it.hasNext()){
				OutputArcListener l = it.next();
				if(l instanceof SwingPetriFigure){
					SwingPetriFigure fig = (SwingPetriFigure) l;
					Element figElmt = doc.createElement("outputarc_figure");
					figElmt.setAttribute("id", "swing"+a.getID());
					figElmt.setAttribute("class", "swing");
					figElmt.setAttribute("x", String.valueOf(fig.getX()));
					figElmt.setAttribute("y", String.valueOf(fig.getY()));
					figElmt.setAttribute("width", String.valueOf(fig.getWidth()));
					figElmt.setAttribute("height", String.valueOf(fig.getHeight()));
					layout.appendChild(figElmt);
				}
			}
		}
		elmt.setAttribute("place", String.valueOf(a.getPlace().getID()));
		elmt.setAttribute("transition", String.valueOf(a.getTransition().getID()));
		elmt.setAttribute("expression", a.getExpression());
		return elmt;
	}
	
	public void saveSubnetToXMLFile(Subnet sub, File toFile) throws TransformerException{
		doc = docBuilder.newDocument();
		Element save = doc.createElement("save");
		layout = doc.createElement("layout");
		save.appendChild(this.getSubnetElement(sub));
		save.appendChild(layout);
		doc.appendChild(save);
		DOMSource source = new DOMSource(doc);
		// write the content into xml file
		StreamResult result = new StreamResult(toFile);
		transformer.transform(source, result);
	}
	/*
	public SubnetFigure importSubnetFigureFromXMLFile(NetFigure parent, File fromFile) throws SAXException, IOException{
		Document doc = docBuilder.parse(fromFile);
		this.subTable=new Hashtable<String,SubnetFigure>();
		this.placeTable=new Hashtable<String,PlaceFigure>();
		this.transTable=new Hashtable<String,TransitionFigure>();
		Element save = doc.getDocumentElement().normalize();
		return getAWTSubnetByElement(doc.getDocumentElement(),offsetX,offsetY,parent);
	}
	/*
	private SubnetFigure getSubnetByElement(Element elmt, int offsetX, int offsetY, SubnetFigure parent){
		int x = offsetX+parseInt(elmt.getAttribute("x"));
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
		return s;
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
		}/
		return p;
	}
	
	private ArcFigure getAWTArcByElement(Element elmt, SubnetFigure s){
		ArcFigure a=null;
		String place = elmt.getAttribute("place");
		String trans = elmt.getAttribute("transition");
		String expr = elmt.getAttribute("expression");
		if(!(place.equals("") || trans.equals(""))){
			//a = new AWTArcFigure(placeTable.get(place), transTable.get(trans),(elmt.getAttribute("class").equals("output"))?false:true);
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
	}*/
}
