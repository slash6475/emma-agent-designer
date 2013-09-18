//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import java.awt.Rectangle;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetElement;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.cpn.XmlUtil;
import org.rakiura.draw.Drawing;
import org.rakiura.draw.Figure;
import org.rakiura.draw.FigureEnumeration;
import org.rakiura.draw.figure.LineConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents an utility class for layout XML serialization.
 *
 * <br><br>
 * XMLLayoutManager.java created on 28/06/2003 13:27:39<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
public final class XMLLayoutManager {

	private final static String CPNDRAWING = "cpndrawing";
	private final static String SUBNETDRAWING = "subnetdrawing";
	private final static String FIGURE = "figure";
	private final static String NAME = "name";
	private final static String NETELEMENTID = "netelementid";
//	private final static String NETFILE = "netfile";
	private final static String PROPERTY = "property";
	private final static String VALUE = "value";
	private final static String HSIZE = "height";
	private final static String WSIZE = "width";
	private final static String TYPE = "type";
	private final static String X = "x";
	private final static String Y = "y";

	private final static String EOL = System.getProperty("line.separator");

	StringBuffer errors = new StringBuffer ();

	/**
	 * Reads the layout from an XML file. This method will read the layout, and
	 * then it will apply it to the provided NetDrawing instance. This method
	 * expects the NetDrawing to have a valid net handle.
	 * @param drawing net drawing with a valid net handle.
	 * @param aLayoutFile input layout file.
	 * @return empty string if there is no errors, or String message with
	 * all the errors.
	 * @see #loadLayout(NetDrawing, Document)
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static String loadLayout (final NetDrawing drawing, File aLayoutFile)
		throws SAXException, ParserConfigurationException {
		final Document doc = XmlUtil.parseFile (aLayoutFile);
		return loadLayout(drawing, doc);
	}

	/**
	 * This method will read the layout, and
	 * then it will apply it to the provided NetDrawing instance. This method
	 * expects the NetDrawing to have a valid net handle.
	 * @param drawing net drawing with a valid net handle.
	 * @param aLayoutDocument input layout document
	 * @return empty string if there is no errors, or String message with
	 * all the errors
	 * @see #loadLayout(NetDrawing, File)
	 */
	public static String loadLayout (final NetDrawing drawing, Document aLayoutDocument) {
		final XMLLayoutManager man = new XMLLayoutManager ();
		man.processXML (aLayoutDocument, drawing);
		return man.getErrors();
	}

	/**
	 * Draws a netDrawing from an XML layout file. This method will read the
	 * layout, and apply it to the given netdrawing.
	 * This method re-creates a valid net handle based on the layout.
	 * @param aLayoutFile input layout file.
	 * @param aNetDrawing the drawing to draw to. Should be a new, empty drawing.
	 * @return empty string if there is no errors, or String message with
	 * all the errors.
	 */
	public static String loadDrawingfromLayout (NetDrawing aNetDrawing, File aLayoutFile) {
		Document doc;
		try {
			doc = XmlUtil.parseFile (aLayoutFile);
		}
		catch (SAXException ex) {
			return "Error in parsing Lay-out file: "+ ex.getMessage();
		}catch (ParserConfigurationException ex) {
			return "Error in parsing Lay-out file: "+ ex.getMessage();
		}
		final XMLLayoutManager man = new XMLLayoutManager ();
		man.processXMLwithoutNet (doc, aNetDrawing);
		return man.getErrors();
	}

	/**
	 * Saves a given NetDrawing as XML layout file. Any errors that have occurred
	 * during the process will be returned.
	 * @param aDrawing net drawing with the figures to be serialized.
	 * @param aLayoutFile the output XML layout file. If there is data in this
	 * file it will be overwritten.
	 * @throws IOException when something goes wrong with I/O.
	 * @return empty string if there is no errors, or String message with
	 * all the errors.
	 */
	public static String saveLayout (final Drawing aDrawing, final File aLayoutFile)
		throws IOException {
		XMLLayoutManager man = new XMLLayoutManager();
		Document doc = man.buildXML ( aDrawing, aLayoutFile.getName() );
		FileOutputStream out = new FileOutputStream(aLayoutFile);
		XmlUtil.writeXML( doc, out);
		out.flush ();
		out.close ();
		return man.getErrors();
	}


	/**
	 * Utility method to generate XML document out of a collection of CPN figures
	 * from the provided NetDrawing. Any errors are stored and can be retrieved
	 * by calling #getErrors().
	 * @return created XML document.
	 * @param aDrawing the drawing (lay-out) to save to xml
	 * @param aName the name of the drawing
	 * @see #getErrors()
	 */
	private Document buildXML(final Drawing aDrawing, final String aName) {
		Document doc;
		Element root;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			root = doc.createElement (CPNDRAWING);
			root.setAttribute(NAME, aName);
					// do not save the net name with the layout for now
					//root.setAttribute(NETFILE, aDrawing.getFile().getName());
			doc.appendChild (root);
		} catch (ParserConfigurationException e) {
			addError("[ XML Serializer ]  Error when initializing XML parser.");
			addError("Reason: "+e.getMessage());
			return null;
		}
		//the real figures processing
		buildXMLelements(aDrawing, root);
		return doc;
	}


	/**
	 * Adds the figures of the drawing to the root element as subelements in XML.
	 * @param aDrawing the drawing that contains the figures to be exported to XML.
	 * @param root the element to which the figures are added.
	 */
	private void buildXMLelements(final Drawing aDrawing, final Element root) {
		//the real figures processing
		final FigureEnumeration e = aDrawing.figures();
		while (e.hasMoreElements()) {
			final Figure f = e.nextFigure ();
			try {
				processFigure (root, f);
			} catch (Exception ex) {
				addError("Unable to process this figure: " + f);
				addError("Reason: "+ ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Saves a Figure to a document element. The location and all basic properties
	 * of the figure are stored in the document element.
	 * @param aRoot The root element in which the figure plus properties are to
	 * be stored.
	 * @param aFigure the figure to be stored in the document element.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 */
	private void processFigure (final Element aRoot, final Figure aFigure)
			throws InvocationTargetException, IllegalAccessException, IntrospectionException {
		if (aFigure == null) return;
		final Element e = aRoot.getOwnerDocument().createElement(FIGURE);
		if (aFigure instanceof CPNAbstractFigure) {
			e.setAttribute(NETELEMENTID, ((CPNAbstractFigure) aFigure).getID());
		}  //previous line is not really necessary
		processXYSize (e, aFigure);
		processProperties(e, aFigure);
		e.setAttribute(TYPE, aFigure.getClass().getName());
		if (aFigure instanceof CPNSubNetFigure) {
			Element sub = e.getOwnerDocument().createElement(SUBNETDRAWING);
			sub.setAttribute(NAME, ((CPNSubNetFigure)aFigure).getNameFigure().getText());
			buildXMLelements( ((CPNSubNetFigure)aFigure).getSubNetDrawing() , sub);
			e.appendChild(sub);
		}
		aRoot.appendChild (e);
	}

	private void processXYSize (Element anElement, Figure aFigure) {
		final Rectangle r = aFigure.displayBox();
		anElement.setAttribute (X, Integer.toString(r.x));
		anElement.setAttribute (Y, Integer.toString(r.y));
		anElement.setAttribute (HSIZE, Integer.toString(r.height));
		anElement.setAttribute (WSIZE, Integer.toString(r.width));
	}

	private void processProperties (Element anElement, Object aBean)
			throws InvocationTargetException, IllegalAccessException, IntrospectionException {
		final BeanInfo info = Introspector.getBeanInfo (aBean.getClass());
		final PropertyDescriptor[] p= info.getPropertyDescriptors ();
		for (int i = 0; i < p.length; i++) {
			if (p[i].getPropertyType() == null) continue;
			if ((p[i].getPropertyType().isPrimitive() ||
					 p[i].getPropertyType().equals(String.class))	&&
					 p[i].getWriteMethod() != null && p[i].getReadMethod() != null) {
				processProperty (anElement, aBean, p[i]);
			}
		}
	}

	private void processProperty(Element anElement, Object aBean, PropertyDescriptor desc)
		throws InvocationTargetException, IllegalAccessException {
		final Method reader = desc.getReadMethod();
		final Element prop = anElement.getOwnerDocument().createElement(PROPERTY);
		prop.setAttribute (NAME, desc.getName());
		final Object value = reader.invoke(aBean, new Object[0]);
		prop.setAttribute (VALUE, value.toString());
		anElement.appendChild (prop);
	}

	/**
	 * Applies a lay-out to a netdrawing. If a net-element is not represented in
	 * the lay-out, it is not drawn in the drawing either. If a lay-out figure
	 * refers to a netelement that is not in the net, it will not be drawn.
	 * Errors of loading the lay-out are stored in #errors and can be retrieved by
	 * calling #getErrors()
	 * @param doc the lay-out document
	 * @param drawing the drawing with the net to be laid out
	 * @see #getErrors()
	 */
	private void processXML (final Document doc, final NetDrawing drawing) {
		final NodeList list = doc.getDocumentElement().getChildNodes();
		processXMLnodelist(list, drawing);
	}

	/**
	 * Applies a lay-out to a netdrawing. If a net-element is not represented in
	 * the lay-out, it is not drawn in the drawing either. If a lay-out figure
	 * refers to a netelement that is not in the net, it will not be drawn.
	 * Errors of loading the lay-out are stored in #errors and can be retrieved by
	 * calling #getErrors()
	 * @param list the nodes (figures) of the lay-out document
	 * @param drawing the drawing with the net to be laid out
	 * @see #getErrors()
	 */
	private void processXMLnodelist (final NodeList list, final NetDrawing drawing) {
		final Net net = drawing.getNet();
		drawing.removeAll();
		final String[] layouts = net.getLayouts();
		for (int i = 0; i < list.getLength(); i++) {
			try {
				Element currentElt;
				try {
					currentElt = (Element) list.item(i);
				} catch (ClassCastException e) {
					//if nodes are separated with return, then the return could be here... skip it!
					continue;
				}
				if (currentElt.getNodeName().equalsIgnoreCase(FIGURE)) {
					final Figure fig = createFigure (currentElt);
					if (fig == null) continue;
					if (fig instanceof CPNAbstractFigure) {
						if (fig instanceof CPNNetFigure) {
							((CPNNetFigure) fig).setNetElement (net);
						}	else {
							final NetElement netElement =
									net.forID (((CPNAbstractFigure) fig).getID());
							if (netElement != null) {
								((CPNAbstractFigure) fig).setNetElement (netElement);
							} else {
								addError("Cannot find netelement to set for layout figure: " + fig + " of class "+fig.getClass());
								continue;
							}
						}
						if (fig instanceof CPNSubNetFigure) {
							final NodeList drawinglist = currentElt.getChildNodes();
							for (int j = 0; j < drawinglist.getLength(); j++) {
								Node node = drawinglist.item(j);
								if (node.getNodeName().equalsIgnoreCase(SUBNETDRAWING)) {
									Net subnet = ((CPNSubNetFigure)fig).getNet();
									NetDrawing subnetdrawing = new NetDrawing(NetGenerator.INSTANCE.getNetInstance());
									subnetdrawing.setNet(subnet);
									((CPNSubNetFigure)fig).setNetDrawing(subnetdrawing);
									processXMLnodelist(node.getChildNodes(), subnetdrawing);
									break; //only need one
								}
							}
						}
					}
					drawing.addFigure (fig);
				} else {
					addError("Unknown XML element:"+ currentElt.getNodeName());
					continue;
				}
			} catch (Exception e) {
				addError("Cannot create figure: " + list.item(i).getAttributes().toString());
				addError("Reason: "+e.getMessage());
				e.printStackTrace();
			}
		}
		for (int i = 0; i < layouts.length; i++) { //update the layouts, which were cleared up by adding figures
			net.addLayout(layouts[i]);
		}
		reconnect_rewire(drawing);
	}

	/**
	 * Creates a netdrawing from a lay-out file. The net elements are generated
	 * from the figures. Errors of loading the lay-out are stored in #errors
	 * @see #getErrors()
	 * @param doc the lay-out document
	 * @param drawing the drawing to add the figures to.
	 */
	private void processXMLwithoutNet (final Document doc, final NetDrawing drawing) {
		final NodeList list = doc.getDocumentElement().getChildNodes();
		processXMLnodesWithoutNet(list, drawing);
	}

	/**
	 * Creates a netdrawing from a lay-out file. The net elements are generated
	 * from the figures. Errors of loading the lay-out are stored in #errors
	 * @see #getErrors()
	 * @param list the list of figure nodes
	 * @param drawing the drawing to add the figures to.
	 */
	private void processXMLnodesWithoutNet (final NodeList list, final NetDrawing drawing) {
		//final List lineFigs = new ArrayList ();
		for (int i = 0; i < list.getLength(); i++) {
			try {
				Element currentElt;
				try {
					currentElt = (Element) list.item(i);
				} catch (ClassCastException e) {
					//if nodes are separated with return, then the return could be here... skip it!
					continue;
				}
				if (currentElt.getNodeName().equalsIgnoreCase(FIGURE)) {
					Figure fig = createFigure (currentElt);
					if (fig instanceof CPNSubNetFigure) {
						final NodeList drawinglist = currentElt.getChildNodes();
						for (int j = 0; j < drawinglist.getLength(); j++) {
							Node node = drawinglist.item(j);
							if (node.getNodeName().equalsIgnoreCase(SUBNETDRAWING)) {
								String ID = ((CPNSubNetFigure)fig).getID();
								NetDrawing subnetdrawing = new NetDrawing(NetGenerator.INSTANCE.getNetInstance());
								((CPNSubNetFigure)fig).setNetDrawing(subnetdrawing);
								org.rakiura.cpn.Node net = (org.rakiura.cpn.Node) subnetdrawing.getNet();
								net.setID(ID);
								((CPNSubNetFigure)fig).setNetElement(net);
								processXMLnodesWithoutNet(node.getChildNodes(), subnetdrawing);
								break; //only need one
							}
						}
					}
					drawing.addFigure (fig);
				} else {
					addError("Unknown XML element:"+ currentElt.getNodeName());
					continue;
				}
			} catch (Exception e) {
				addError("Cannot create figure: " + list.item(i).getAttributes().toString());
				addError("Reason: "+e.getMessage());
			}
		}
		reconnect_rewire(drawing);
		drawing.generateNetElementsBasedOnFigures ();
	}

	/**
	 * Assures that parents and children are connected and CPNArcs know the
	 * drawing and lineconnections are connected.
	 * @param drawing the drawing with figures to be rewired and reconnected.
	 */
	private void reconnect_rewire(NetDrawing drawing) {
		final FigureEnumeration fe = drawing.figures();
		final int errorcheck = drawing.figureCount();
		while (fe.hasMoreElements()) {
			final Figure f = fe.nextFigure ();
			if (f instanceof CPNDecoration) {
				try {
					((CPNDecoration) f).reconnectToParent(drawing);
				} catch (Exception ex) {
					addError("Cannot reconnect Decoration figure "+ f);
					addError("Reason: "+ ex.getMessage());
				}
			} else if (f instanceof LineConnection) {
				try {
					//this goes wrong if it is an arc in subnet connected to a SubnetPlace,
					//because the Place to reconnect to is not known here.
					((LineConnection) f).rewireConnections(drawing);
				}
				catch (Exception ex) {
					addError("Cannot reconnect LineConnection figure "+ f);
					addError("Reason: "+ ex.getMessage());
				}
				if (f instanceof CPNArcFigure) {
					((CPNArcFigure)f).setNetDrawing(drawing);
				}
			} else if (f instanceof SubnetPlace) {
				try {
					((SubnetPlace)f).reconnectToArc(drawing);
				}
				catch (Exception ex) {
					addError("Cannot reconnect SubnetPlace figure "+ f);
					addError("Reason: "+ ex.getMessage());
				}
			}
		}
		if (errorcheck != drawing.figureCount()) System.err.println("Error in program code! during reconnection and rewiring the number of figures changed!");
	}

	/**
	 * Creates a new Figure instance from an element in a document.
	 * @param anElement the element that represents a figure
	 * @return the figure that was created
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Figure createFigure (final Element anElement)
		throws ClassNotFoundException, IllegalAccessException,
					InstantiationException {
		final String className = anElement.getAttribute(TYPE);
		final Class clazz = Class.forName(className);
		final Figure fig = (Figure) clazz.newInstance();
		final int x = Integer.parseInt(anElement.getAttribute(X));
		final int y = Integer.parseInt(anElement.getAttribute(Y));
		final int hsize = Integer.parseInt(anElement.getAttribute(HSIZE));
		final int wsize = Integer.parseInt(anElement.getAttribute(WSIZE));
		fig.displayBox(new Rectangle (x,y, wsize, hsize));
		final NodeList list = anElement.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Element elt;
			try {
				elt = (Element) list.item(i);
			} catch (ClassCastException e) {
				//if nodes are separated with return, then the return could be here... skip it!
				continue;
			}
			if (elt.getNodeName().equalsIgnoreCase(PROPERTY)) {
				try {
					setProperty (fig, elt);
				} catch (Exception e) {
					addError("Cannot set property: "+elt.getAttributes()+"\n for Figure: "+fig);
					e.printStackTrace();
				}
			}
		}
		return fig;
	}

	/**
	 * Sets a property to a figure. The property is read from the document element.
	 * The property is set to the figure.
	 * @param aFig The figure that needs to have a property set.
	 * @param eProp the property
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void setProperty (Object aFig, Element eProp)
		throws IntrospectionException, IllegalAccessException,
					 InvocationTargetException {
		final String name = eProp.getAttribute(NAME);
		String value = eProp.getAttribute(VALUE);
		if (value == null || value.trim().equals("")) value = " ";
		final BeanInfo info = Introspector.getBeanInfo (aFig.getClass());
		final PropertyDescriptor[] props = info.getPropertyDescriptors();
		PropertyDescriptor desc = null;
		for (int i = 0; i < props.length; i++) {
			if (props[i].getName().equals(name)) {
				desc = props[i];
				break;
			}
		}
		if (desc == null) return;
		final Class clazz = desc.getPropertyType();
		final Method writer = desc.getWriteMethod();
		if (clazz.equals(String.class)) {
			writer.invoke(aFig, new Object[] {value});
		} else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
			writer.invoke(aFig, new Object[] {new Integer(value)});
		} else if (clazz.equals(Float.class) || clazz.equals(Float.TYPE)) {
			writer.invoke(aFig, new Object[] {new Float(value)});
		} else if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
			writer.invoke(aFig, new Object[] {new Boolean(value)});
		}
	}

	/**
	 * Adds an error message to this XMLLayout manager
	 * @see #getErrors()
	 * @param text the error message to be stored
	 */
	private void addError (String text) {
		this.errors.append(EOL).append(text);
	}

	/**
	 * Returns a string with the errors encountered during loading / saving a
	 * lay-out.
	 * @return the errors encountered during loading / saving lay-outs.
	 */
	private String getErrors () {
		return this.errors.toString();
	}
} //////////////EOF ///////////////////