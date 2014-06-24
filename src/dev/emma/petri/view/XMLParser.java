package emma.petri.view;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import emma.petri.control.listener.*;
import emma.petri.model.Arc;
import emma.petri.model.ArcException;
import emma.petri.model.InputArc;
import emma.petri.model.Net;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;
import emma.tools.ClassFounder;
import emma.view.swing.petri.figure.ArcFigure;
import emma.view.swing.petri.figure.NetFigure;
import emma.view.swing.petri.figure.PlaceFigure;
import emma.view.swing.petri.figure.ScopeFigure;
import emma.view.swing.petri.figure.SubnetFigure;
import emma.view.swing.petri.figure.TransitionFigure;

public class XMLParser {
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private DocumentBuilder docBuilder;
	private Transformer transformer;
	private Document doc;
	private Element layout;
	private HashMap<String,PlaceFigure> placeFigures;
	private HashMap<String,TransitionFigure> transitionFigures;
	private HashMap<String,Place> places;
	private HashMap<String,Transition> transitions;
	private HashMap<String,Element> layouts;
	
	public XMLParser() throws ParserConfigurationException, TransformerConfigurationException{
		docBuilder = docFactory.newDocumentBuilder();
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		layouts = new HashMap<>();
		placeFigures = new HashMap<>();
		transitionFigures = new HashMap<>();
		places = new HashMap<>();
		transitions = new HashMap<>();
	}
	
	private Element getSubnetElement(Subnet sub){
		Element elmt = doc.createElement("subnet");
		Iterator<SubnetListener> it = sub.getListeners().iterator();
		while(it.hasNext()){
			SubnetListener l = it.next();
			if(l instanceof SubnetFigure){
				SubnetFigure fig = (SubnetFigure) l;
				Element figElmt = doc.createElement("subnet_figure");
				figElmt.setAttribute("id",String.valueOf(sub.getID()));
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
			if(l instanceof ScopeFigure){
				ScopeFigure fig = (ScopeFigure) l;
				Element figElmt = doc.createElement("scope_figure");
				figElmt.setAttribute("id", String.valueOf(s.getID()));
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
		elmt.setAttribute("target", s.getTarget());
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
			if(l instanceof PlaceFigure){
				PlaceFigure fig = (PlaceFigure) l;
				Element figElmt = doc.createElement("place_figure");
				figElmt.setAttribute("id", String.valueOf(p.getID()));
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
			data.appendChild(doc.createCDATASection(p.getData().get()));
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
			if(l instanceof TransitionFigure){
				TransitionFigure fig = (TransitionFigure) l;
				Element figElmt = doc.createElement("transition_figure");
				figElmt.setAttribute("id", String.valueOf(t.getID()));
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
				if(l instanceof ArcFigure){
					ArcFigure fig = (ArcFigure) l;
					Element figElmt = doc.createElement("inputarc_figure");
					figElmt.setAttribute("id", String.valueOf(a.getID()));
					figElmt.setAttribute("class", "swing");
					Element pointsElmt = doc.createElement("points");
					Iterator<Point> points = fig.getPoints().iterator();
					while(points.hasNext()){
						Point pt = points.next();
						Element ptElmt = doc.createElement("point");
						ptElmt.setAttribute("x", String.valueOf(pt.x));
						ptElmt.setAttribute("y", String.valueOf(pt.y));
						pointsElmt.appendChild(ptElmt);
					}
					figElmt.appendChild(pointsElmt);
					layout.appendChild(figElmt);
				}
			}
		}
		else{
			elmt.setAttribute("class", "output");
			Iterator<OutputArcListener> it = ((OutputArc)a).getListeners().iterator();
			while(it.hasNext()){
				OutputArcListener l = it.next();
				if(l instanceof ArcFigure){
					ArcFigure fig = (ArcFigure) l;
					Element figElmt = doc.createElement("outputarc_figure");
					figElmt.setAttribute("id", String.valueOf(a.getID()));
					figElmt.setAttribute("class", "swing");
					Element pointsElmt = doc.createElement("points");
					Iterator<Point> points = fig.getPoints().iterator();
					while(points.hasNext()){
						Point pt = points.next();
						Element ptElmt = doc.createElement("point");
						ptElmt.setAttribute("x", String.valueOf(pt.x));
						ptElmt.setAttribute("y", String.valueOf(pt.y));
						pointsElmt.appendChild(ptElmt);
					}
					figElmt.appendChild(pointsElmt);
					layout.appendChild(figElmt);
				}
			}
		}
		elmt.setAttribute("place", String.valueOf(a.getPlace().getID()));
		elmt.setAttribute("transition", String.valueOf(a.getTransition().getID()));
		if(a.isOutput()){
			elmt.setAttribute("expression", ((OutputArc)a).getExpression());
		}
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
		StreamResult result = new StreamResult(toFile);
		transformer.transform(source, result);
	}
	
	public void saveProjectToXMLFile(Net net, File file) throws TransformerException, FileNotFoundException{
		Iterator<Subnet> it = net.getSubnets().iterator();
		String path = file.getPath()+"/";
		PrintWriter out = new PrintWriter(path+"epnf.lst");
		while(it.hasNext()){
			Subnet sub = it.next();
			String str = sub.getName()+"_"+sub.getID()+".epnf";
			this.saveSubnetToXMLFile(sub, new File(path+str));
			out.println(str);
		}
		out.close();
	}
	
	public SubnetFigure importSubnetFigureFromXMLFile(int x, int y, NetFigure parent, File fromFile) throws CorruptedFileException, SAXException, IOException{
		return this.importSubnetFigureFromXMLFile(x, y, parent, fromFile, false);
	}
	
	public SubnetFigure importSubnetFigureFromXMLFile(int x, int y, NetFigure parent, File fromFile, boolean offsetPosition) throws CorruptedFileException, SAXException, IOException{
		Element layout=null;
		Element subnet=null;
		Document doc = docBuilder.parse(fromFile);
		Element save = doc.getDocumentElement();
		save.normalize();
		NodeList childs = save.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n = childs.item(i);
			if(n.getNodeName().equals("subnet")){
				subnet=(Element)n;
			}
			else if(n.getNodeName().equals("layout")){
				layout=(Element)n;
			}
		}
		if(subnet==null) throw new CorruptedFileException("subnet not found");
		if(layout==null) throw new CorruptedFileException("layout not found");
		NodeList layoutsList = layout.getChildNodes();
		for(int i=0; i<layoutsList.getLength();i++){
			Node n = layoutsList.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element e = (Element)n;
				if(e.getAttribute("class").equals("swing")){
					layouts.put(e.getAttribute("id"), e);
				}
			}
		}
		SubnetFigure s = getSubnetFigureByElement(x,y,subnet,parent,offsetPosition);
		layouts.clear();
		placeFigures.clear();
		transitionFigures.clear();
		return s;
	}
	
	private int parseInt(String strInt) throws CorruptedFileException{
		try{
			return Integer.parseInt(strInt);
		} catch(NumberFormatException e){
			throw new CorruptedFileException("attribute '"+strInt+"' is not a correct integer");
		}
	}
	
	private boolean parseBoolean(String strBool) throws CorruptedFileException{
		try{
			return Boolean.parseBoolean(strBool);
		} catch(NumberFormatException e){
			throw new CorruptedFileException("attribute '"+strBool+"' is not a correct boolean");
		}
	}
	
	private SubnetFigure getSubnetFigureByElement(int x, int y,Element elmt, NetFigure parent,boolean offsetPosition) throws CorruptedFileException{
		String name = elmt.getAttribute("name");
		Element fig = null;
		if(layouts.containsKey(elmt.getAttribute("id"))){
			fig = layouts.get(elmt.getAttribute("id"));
		}
		else{
			throw new CorruptedFileException("subnetfigure layout for '"+name+"' was not found");
		}
		if(offsetPosition){
			x+=this.parseInt(fig.getAttribute("x"));
			y+=this.parseInt(fig.getAttribute("y"));
		}
		int width=this.parseInt(fig.getAttribute("width"));
		int height=this.parseInt(fig.getAttribute("height"));
		SubnetFigure s=new SubnetFigure(name,x,y,width,height,parent);
		NodeList childs = elmt.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("scopes")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("scope")){
						getScopeFigureByElement((Element)scopes.item(j),s);
					}
				}
				break;
			}
		}
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("arcs")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("arc")){
						getArcFigureByElement((Element)scopes.item(j),s);
					}
				}
				break;
			} 
		}
		return s;
	}
	
	private ScopeFigure getScopeFigureByElement(Element elmt, SubnetFigure parent) throws CorruptedFileException{
		String name = elmt.getAttribute("name");
		Element fig = null;
		if(layouts.containsKey(elmt.getAttribute("id"))){
			fig = layouts.get(elmt.getAttribute("id"));
		}
		else{
			throw new CorruptedFileException("scopefigure layout for '"+name+"' was not found");
		}
		int x = this.parseInt(fig.getAttribute("x"));
		int y = this.parseInt(fig.getAttribute("y"));
		int width=this.parseInt(fig.getAttribute("width"));
		int height=this.parseInt(fig.getAttribute("height"));
		ScopeFigure s=new ScopeFigure(name,x,y,width,height,parent);
		NodeList childs = elmt.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("places")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("place")){
						Element placeElmt = (Element)scopes.item(j);
						placeFigures.put(placeElmt.getAttribute("id"), getPlaceFigureByElement(placeElmt,s));
					}
				}
			}
			else if(n.getNodeName().equals("transitions")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("transition")){
						Element transElmt = (Element)scopes.item(j);
						transitionFigures.put(transElmt.getAttribute("id"),getTransitionFigureByElement(transElmt,s));
					}
				}
			}
		}
		return s;
	}
	
	private PlaceFigure getPlaceFigureByElement(Element elmt, ScopeFigure parent) throws CorruptedFileException{
		String name = elmt.getAttribute("name");
		Element fig = null;
		if(layouts.containsKey(elmt.getAttribute("id"))){
			fig = layouts.get(elmt.getAttribute("id"));
		}
		else{
			throw new CorruptedFileException("placefigure layout for '"+name+"' was not found");
		}
		int x = this.parseInt(fig.getAttribute("x"));
		int y = this.parseInt(fig.getAttribute("y"));
		PlaceFigure pFig= new PlaceFigure(name, x, y, parent);
		Place p = pFig.getPlace();
		p.setInput(this.parseBoolean(elmt.getAttribute("input")));
		p.setOutput(this.parseBoolean(elmt.getAttribute("output")));
		NodeList datalist = elmt.getElementsByTagName("data");
		if(datalist.getLength()>0){
			Element data = (Element)datalist.item(0);
			try {
				if(p.setData(ClassFounder.getUnmappedResourceClass(data.getAttribute("class")))){
					p.getData().put(data.getTextContent());
				}
			} catch (ClassNotFoundException e) {
				throw new CorruptedFileException("Data type for place '"+name+"' is incorrect : Class '"+data.getAttribute("class")+"' not found");
			}
		}
		return pFig;
	}
	
	private TransitionFigure getTransitionFigureByElement(Element elmt, ScopeFigure parent) throws CorruptedFileException{
		String name = elmt.getAttribute("name");
		Element fig = null;
		if(layouts.containsKey(elmt.getAttribute("id"))){
			fig = layouts.get(elmt.getAttribute("id"));
		}
		else{
			throw new CorruptedFileException("transitionfigure layout for '"+name+"' was not found");
		}
		int x = this.parseInt(fig.getAttribute("x"));
		int y = this.parseInt(fig.getAttribute("y"));
		String pId = elmt.getAttribute("place");
		if(!placeFigures.containsKey(pId)){
			throw new CorruptedFileException("place for transition '"+name+"' was not found");
		}
		TransitionFigure tFig= new TransitionFigure(name, x, y, placeFigures.get(pId), parent);
		tFig.getTransition().setCondition(elmt.getAttribute("condition"));
		return tFig;
	}
	
	private ArcFigure getArcFigureByElement(Element elmt, SubnetFigure parent) throws CorruptedFileException{
		ArcFigure a=null;
		String place = elmt.getAttribute("place");
		String trans = elmt.getAttribute("transition");
		String classe = elmt.getAttribute("class");
		if(placeFigures.containsKey(place) && transitionFigures.containsKey(trans)){
			PlaceFigure p = placeFigures.get(place);
			TransitionFigure t = transitionFigures.get(trans);
			if(classe.equals("input")){
				parent.addInputArc(p,t);
				a = parent.getArcFigure(p, t, true);
			}
			else if(classe.equals("output")){
				parent.addOutputArc(p,t);
				a = parent.getArcFigure(p, t, false);
				((OutputArc)a.getArc()).setExpression(elmt.getAttribute("expression"));
			}
			else{
				throw new CorruptedFileException("class '"+classe+"' for arc was not found");
			}
			if(layouts.containsKey(elmt.getAttribute("id"))){
				Element fig = layouts.get(elmt.getAttribute("id"));
				NodeList nl = fig.getElementsByTagName("points");
				if(nl.getLength()>0){
					NodeList pts = ((Element)nl.item(0)).getElementsByTagName("point");
					for(int j=0; j<pts.getLength();j++){
						Element pt = (Element)pts.item(j);
						a.addPoint(new Point(this.parseInt(pt.getAttribute("x")),this.parseInt(pt.getAttribute("y"))));
					}
				}
			}
		}
		else{
			throw new CorruptedFileException("arc relies unknow place and/or transition");
		}
		return a;
	}
    
	public void importProject(NetFigure fig, File list) {
		String path = list.getParentFile().getPath()+"/";
		if(fig.isSubnetContainer()){
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(list));
				while ((sCurrentLine = br.readLine()) != null) {
					try {
						this.importSubnetFigureFromXMLFile(0,0,fig,new File(path+sCurrentLine),true);
					} catch (CorruptedFileException | SAXException e) {
						e.printStackTrace();
					}
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public Net getNetFromXMLFile(File file) throws CorruptedFileException, SAXException, IOException{
		Net net=new Net();
		if(file.getName().endsWith("epnf")){
			this.getSubnetFromXMLFile(net,file);
		}
		else if(file.getName().endsWith("lst")){
			String path = file.getParentFile().getPath()+"/";
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(file));
				while ((sCurrentLine = br.readLine()) != null) {
					this.getSubnetFromXMLFile(net,new File(path+sCurrentLine));
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		else{
			throw new CorruptedFileException(file.getName()+": file extension is incorrect (.epnf & .lst only)");
		}
		return net;
	}
	
	private void getSubnetFromXMLFile(Net parent, File fromFile) throws CorruptedFileException, SAXException, IOException{
		Element subnet=null;
		Document doc = docBuilder.parse(fromFile);
		Element save = doc.getDocumentElement();
		save.normalize();
		NodeList childs = save.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n = childs.item(i);
			if(n.getNodeName().equals("subnet")){
				subnet=(Element)n;
				break;
			}
		}
		if(subnet==null) throw new CorruptedFileException("subnet not found");
		this.getSubnetByElement(subnet,parent);
		places.clear();
		transitions.clear();
	}
	
	private Subnet getSubnetByElement(Element elmt, Net parent) throws CorruptedFileException{
		Subnet s=new Subnet(parent);
		NodeList childs = elmt.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("scopes")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("scope")){
						getScopeByElement((Element)scopes.item(j),s);
					}
				}
				break;
			}
		}
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("arcs")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("arc")){
						getArcByElement((Element)scopes.item(j),s);
					}
				}
				break;
			} 
		}
		return s;
	}
	
	private Scope getScopeByElement(Element elmt, Subnet parent) throws CorruptedFileException{
		Scope s=new Scope(parent);
		NodeList childs = elmt.getChildNodes();
		for(int i=0; i<childs.getLength();i++){
			Node n=childs.item(i);
			if(n.getNodeName().equals("places")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("place")){
						Element placeElmt = (Element)scopes.item(j);
						places.put(placeElmt.getAttribute("id"), getPlaceByElement(placeElmt,s));
					}
				}
			}
			else if(n.getNodeName().equals("transitions")){
				NodeList scopes = n.getChildNodes();
				for(int j=0; j<scopes.getLength();j++){
					if(scopes.item(j).getNodeName().equals("transition")){
						Element transElmt = (Element)scopes.item(j);
						transitions.put(transElmt.getAttribute("id"),getTransitionByElement(transElmt,s));
					}
				}
			}
		}
		return s;
	}
	
	private Place getPlaceByElement(Element elmt, Scope parent) throws CorruptedFileException{
		Place p= new Place(parent);
		p.setInput(this.parseBoolean(elmt.getAttribute("input")));
		p.setOutput(this.parseBoolean(elmt.getAttribute("output")));
		NodeList datalist = elmt.getElementsByTagName("data");
		if(datalist.getLength()>0){
			Element data = (Element)datalist.item(0);
			try {
				if(p.setData(ClassFounder.getUnmappedResourceClass(data.getAttribute("class")))){
					p.getData().put(data.getTextContent());
				}
			} catch (ClassNotFoundException e) {
				throw new CorruptedFileException("Data type for place is incorrect : Class '"+data.getAttribute("class")+"' not found");
			}
		}
		return p;
	}
	
	private Transition getTransitionByElement(Element elmt, Scope parent) throws CorruptedFileException{
		String pId = elmt.getAttribute("place");
		if(!places.containsKey(pId)){
			throw new CorruptedFileException("place for transition was not found");
		}
		Transition t= new Transition(parent,places.get(pId));
		t.setCondition(elmt.getAttribute("condition"));
		return t;
	}
	
	private Arc getArcByElement(Element elmt, Subnet parent) throws CorruptedFileException{
		Arc a=null;
		String place = elmt.getAttribute("place");
		String trans = elmt.getAttribute("transition");
		String classe = elmt.getAttribute("class");
		if(places.containsKey(place) && transitions.containsKey(trans)){
			Place p = places.get(place);
			Transition t = transitions.get(trans);
			if(classe.equals("input")){
				try {
					a = new InputArc(p,t);
				} catch (ArcException e) {
					throw new CorruptedFileException(e.getMessage());
				}
			}
			else if(classe.equals("output")){
				try{
					a = new InputArc(p,t);
				} catch (ArcException e) {
					throw new CorruptedFileException(e.getMessage());
				}
				((OutputArc)a).setExpression(elmt.getAttribute("expression"));
			}
			else{
				throw new CorruptedFileException("class '"+classe+"' for arc was not found");
			}
		}
		else{
			throw new CorruptedFileException("arc relies unknow place and/or transition");
		}
		return a;
	}
}
