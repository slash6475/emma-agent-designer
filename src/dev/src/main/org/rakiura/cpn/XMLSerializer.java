//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Class that arranges saving a JFERN net to XML. Utility class.
 * A net can be saved to an XML document, stream or file.
 *
 * @author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 * @author <a href="martinfleurke@users.sf.net">Martin Fleurke</a>
 * @version 4.0.0 $Revision: 1.26 $
 * @since 3.0
 */
public final class XMLSerializer extends NetVisitorAdapter {

	private static final long serialVersionUID = 3258413936767414320L;
	
	/** Handle to the root of our XML document. */
	private Element root;
	private Document doc;

	/** Hidden constructor. Used internally as via the visitor pattern to traverse all net nodes.
	 *  @param aRoot use null create a new document for the serializer, or suplly
	 * the document the serializer should work on
	 *  */
	private XMLSerializer(final Element aRoot) {
		if (aRoot == null) {
			try {
				this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			} catch (ParserConfigurationException e) {
				System.err.println("[ XML Serializer ]  Error when initializing XML parser.");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			this.doc = aRoot.getOwnerDocument ();
			this.root = aRoot;
		}
	}

	/**
	 * Dumps a given collection of net elements into a specified file as XML.
	 * @param aNet the net to process to XML
	 * @param anOutputfile output file
	 * @throws IOException when something goes wrong with I/O.
	 */
	public static final void writeXML (final Net aNet,  final File anOutputfile) throws IOException {
		Document doc = buildXML ( aNet );
		FileOutputStream out = new FileOutputStream(anOutputfile);
		XmlUtil.writeXML( doc, out);
		out.flush ();
		out.close ();
	}

	/**
	 * Dumps a given collection of net elements into a specified stream as XML.
	 * The stream will be flushed and closed.
	 * @param aNet the net to process to XML
	 * @param anOut an OutputStream to put it in.
	 * @throws IOException when something goes wrong with I/O.
	 */
	public static final void writeXML (final Net aNet, final OutputStream anOut) throws IOException {
		final Document doc = buildXML ( aNet );
		XmlUtil.writeXML( doc, anOut );
		anOut.flush ();
		anOut.close ();
	}

	/**
	 * Utility method to generate XML document out of a Net.
	 * Note: all the IDs of net elements are taken directly from net elements,
	 * only the IDs for annotations are generated automatically by this serializer.
	 * @param net the net to create the Document for
	 * @return the docment representing the net.
	 */
	public static final Document buildXML(final Net net) {
		XMLSerializer ser = new XMLSerializer (null);
		net.apply (ser);
		return ser.doc;
	}

	public void setRootElement (Element aRoot) {
		this.root = aRoot;
	}

	public void net (Net net) {
		final Element oldRoot = this.root;
		prepareNetHeader (net );
		super.net (net);
		this.root = oldRoot;
	}

	public void transition (final Transition aTransition) {
		final Element transitionElement = this.doc.createElement(TRANSITION);
		transitionElement.setAttribute ( ID, aTransition.getID() );
		transitionElement.appendChild(createAnnotation(this.doc, NAME, aTransition.getName()));
		String tmp = aTransition.getActionText();
		if (tmp != null && !tmp.trim().equals("")) {
			transitionElement.appendChild(createAnnotation(this.doc, ACTION, tmp));
		}
		tmp = aTransition.getGuardText();
		if (tmp != null && !tmp.trim().equals("")) {
			transitionElement.appendChild(createAnnotation(this.doc, GUARD, tmp));
		}
		tmp = aTransition.getTypeText();
		if (tmp != null && !tmp.trim().equals("")) {
			transitionElement.appendChild(createAnnotation(this.doc, TYPE, tmp));
		}
		tmp = aTransition.getSpecificationText();
		if (tmp != null && !tmp.trim().equals("")) {
			transitionElement.appendChild(createAnnotation(this.doc, SPECIFICATION, tmp));
		}
		this.root.appendChild(transitionElement);
	}

	public void place (final Place aPlace) {
		final Element placeElement = this.doc.createElement ( PLACE );
		placeElement.setAttribute ( ID, aPlace.getID() );
		placeElement.appendChild(createAnnotation(this.doc, NAME, aPlace.getName()));
		this.root.appendChild  ( placeElement );

		String [] fuseswith = aPlace.getFusedPlacesIDs();
		if (fuseswith.length > 1) {
			final Element fuseElement = this.doc.createElement ( FUSIONS );
			fuseElement.setAttribute ( ID, arrayToString(fuseswith) );
			this.root.appendChild  ( fuseElement );
		}
	}

	public void inputArc (final InputArc anArc) {
		final Element arcElement  = this.doc.createElement(ARC);
		arcElement.setAttribute(ID, anArc.getID());
		arcElement.setAttribute(SOURCE, anArc.getPlace().getID());
		arcElement.setAttribute(TARGET, anArc.getTransition().getID());
		arcElement.setAttribute(TYPE, "ordinary");
		arcElement.appendChild(createAnnotation(this.doc, NAME, anArc.getName()));
		final String tmp = anArc.getExpressionText();
		if (tmp != null && !tmp.trim().equals("")) {
			arcElement.appendChild(createAnnotation(this.doc, EXPRESSION, tmp));
		}
		final String tmp2 = anArc.getGuardText();
		if (tmp2 != null && !tmp2.trim().equals("")) {
			arcElement.appendChild(createAnnotation(this.doc, GUARD, tmp2));
		}
		this.root.appendChild(arcElement);
	}

	public void outputArc (final OutputArc anArc) {
		final Element arcElement  = this.doc.createElement(ARC);
		arcElement.setAttribute(ID, anArc.getID());
		arcElement.setAttribute(SOURCE, anArc.getTransition().getID());
		arcElement.setAttribute(TARGET, anArc.getPlace().getID());
		arcElement.setAttribute(TYPE, "ordinary");
		arcElement.appendChild(createAnnotation(this.doc, NAME, anArc.getName()));
		final String tmp = anArc.getExpressionText();
		if (tmp != null && !tmp.trim().equals("")) {
			arcElement.appendChild(createAnnotation(this.doc, EXPRESSION, tmp));
		}
		this.root.appendChild(arcElement);
	}

	/** Prepares the net headers with annotations like: name, import, implements, declaration.
	 * @param net the net to prepare a header for.
	 * */
	private final void prepareNetHeader (final Net net) {
		final Element netRoot = this.doc.createElement(NET);
		netRoot.setAttribute(ID, net.getID() );
		if (net.getTypeText() != null && !net.getTypeText().trim().equals(""))	{
			netRoot.setAttribute (TYPE, net.getTypeText()  );
		} else {
			netRoot.setAttribute(TYPE, "hlnet" );
		}
		netRoot.setAttribute(CPNLANG, net.getCpnLang());

		String tmp = net.getName();
		if (tmp != null && !tmp.trim().equals(""))
			netRoot.appendChild(createAnnotation (this.doc, NAME, tmp));
		tmp = net.getImportText();
		if (tmp != null && !tmp.trim().equals(""))
			netRoot.appendChild(createAnnotation (this.doc, IMPORT, tmp));
		tmp = net.getImplementsText();
		if (tmp != null && !tmp.trim().equals(""))
			netRoot.appendChild(createAnnotation (this.doc, IMPLEMENTS, tmp));
		tmp = net.getDeclarationText();
		if (tmp != null && !tmp.trim().equals(""))
			netRoot.appendChild(createAnnotation (this.doc, DECLARATION, tmp));
		String[] l = net.getLayouts();
		for (int i = 0; i < l.length; i++) {
			final Element e = this.doc.createElement(LAYOUT);
			e.setAttribute(FILE, l[i]);
			netRoot.appendChild(e);
		}
		if (this.root == null) {
			this.doc.appendChild (netRoot);
		} else {
			this.root.appendChild (netRoot);
		}
		this.root = netRoot;
	}

	/** Creates annotation Element with a new UID, and given type attribute and Text body. */
	 private final static Element createAnnotation (final Document aDoc, final String aType,
																																										 final String aBody) {
		final Element annotation = aDoc.createElement (ANNOTATION);
		annotation.setAttribute (ID, nextAnnotationUID() );
		annotation.setAttribute (TYPE, aType);
		final Element textElement = aDoc.createElement (TEXT);
		final Text importText = aDoc.createTextNode ( aBody );
		textElement.appendChild (importText);
		annotation.appendChild (textElement);
		return annotation;
	 }

	/**
	 * Returns unique dummy ID for annotations.
	 * @return new unique dummy ID for annotations.
	 */
	private static String nextAnnotationUID() {
		return "a" + COUNTER++;
	}
	private static long COUNTER = 1;

	/**
	 * concatenates the strings in the array with comma's.
	 * @param array an array of strings to be concatenated
	 * @return the contents of the array in one string with comma's between the different parts.
	 */
	public static String arrayToString(String[] array) {
		if (array.length < 1) return "";
		String string = "";
		for (int i = 0; i < array.length-1; i++) {
			string = string + array[i] +",";
		}
		return string+array[array.length-1];
	}

	/**
	 * Divides a string into substrings.
	 * Division is done on the places where a ',' is found. The comma is not included in the output.
	 * @param string the string to divide and to put in an array of substrings
	 * @return an array of substrings.
	 */
	public static String[] stringToArray(String string) {
		final StringTokenizer st = new StringTokenizer (string, ",", false);
		final List<String> l = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			l.add (st.nextToken().trim());
		}
		return l.toArray(new String[l.size()]);
	}

	/* Constants for XML tags and attributes. */
	public static final String ACTION = "action";
	public static final String ANNOTATION = "annotation";
	public static final String ARC = "arc";
	public static final String DECLARATION = "declaration";
	public static final String EXPRESSION = "expression";
	public static final String FILE = "file";
	public static final String FUSIONS = "fusions";
	public static final String GUARD = "guard";
	public static final String ID = "id";
	public static final String IMPLEMENTS = "implements";
	public static final String IMPORT = "import";
	public static final String LAYOUT = "layout";
	public static final String NAME = "name";
	public static final String NET = "net";
	public static final String PLACE = "place";
	public static final String SOURCE = "source";
	public static final String SPECIFICATION = "specification";
	public static final String TARGET = "target";
	public static final String TEXT = "text";
	public static final String TRANSITION = "transition";
	public static final String TYPE = "type";
	
	public static final String CPNLANG = "cpnlang";
	
	public static final String LANG_NATIVE = "native";
	public static final String LANG_BSH = "bsh";
	public static final String LANG_CLOJURE = "clojure";
	public static final String LANG_KAWA = "kawa";
	

} //////////////////// EOF///////////////////////////////////