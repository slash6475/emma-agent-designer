// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.rakiura.compiler.CompilationException;
import org.rakiura.compiler.DynamicCompiler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class to read XML net description and generates
 * the JFern Java source file. The generated source file needs to be
 * compiled, and can be executed/manipulated as any ordinary
 * JFern Petri Net (see {@link Net Net} and {@link BasicNet BasicNet}).
 * Note: this class is not publicly visible anymore.
 *
 *<br><br>
 * NetGenerator.java<br>
 * Created: Tue Oct 30 22:31:11 2001<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@author  <a href="phwang@infoscience.otago.ac.nz">Peter Hwang</a>
 *@version 4.0.0 $Revision: 1.59 $ $Date: 2009/08/29 10:55:51 $
 *@since 1.2
 */
public class NetGenerator {
	
	public static NetGenerator INSTANCE = new NetGenerator();
	
	
	/** The generated Java source. */
	protected StringBuffer netSource;

	/** CPNLanguage helper handle. */
	CpnLanguage cpnLang = null;
	
	/** The name of the net. */
	protected String className = null;
	/** The interface this net implements. */
	protected String interfaceImpl = null;
	/** The type of this net. */
	protected String netType = null;
	protected String netID = null;
	protected String netCPNLangType = "native";
	protected Set netLayouts = new HashSet ();

	/** Imports as strings. */
	protected List classImports = new ArrayList();
	/** Declarations as strings. */
	protected List classDeclarations = new ArrayList();

	/**/
	protected List netSubnets = new ArrayList();  // NetGenerator list
	/**/
	protected List netPlaces = new ArrayList();  // strings

	protected List fusions = new ArrayList(); //string[]
	/**/
	protected Map netTransitions = new HashMap();
	/**/
	protected Map netInputArcs = new HashMap();
	/**/
	protected Map netOutputArcs = new HashMap();

	/** transition ID -> action body string. */
	protected Map tranActions = new HashMap();
	/** transition ID -> guard body string */
	protected Map tranGuards = new HashMap();
	/** transition ID -> type body string. */
	protected Map tranTypes = new HashMap();
	/** transition ID -> specs body string */
	protected Map tranSpecs = new HashMap();


	/** arc ID -> guard body string */
	protected Map inputArcGuards = new HashMap();
	/** arc ID -> expression body string */
	protected Map inputArcExpressions = new HashMap();
	/** Place node ID -> Name String */
	/** arc ID -> expression body string */
	protected Map outputArcExpressions = new HashMap();
	/**/
	protected Map placeNodeName = new HashMap();
	/** transition node ID -> Name String */
	protected Map transitionNodeName = new HashMap();


	/**/
	public static final String placePrefix = "_place_";
	/**/
	public static final String transitionPrefix = "_transition_";
	/**/
	public final String arcPrefix = "_arc_";
	/**/
	public static final String EOL = System.getProperty("line.separator");



	protected NetGenerator () {
		this.netSource = new StringBuffer (250);
		this.netSource.append("/* This is JFern generated file.     Do not edit. */").append(EOL);
		this.netSource.append("/* project page see http://sf.net/projects/jfern  */").append(EOL).append(EOL);
	}
	
	protected NetGenerator (final StringBuffer aNetSource) {
		this.netSource = aNetSource;
	}

	public NetGenerator getInstance() {
		return new NetGenerator();
	}
	
	public Net getNetInstance() {
		return new BasicNet();
	}
	
	/**
	 * Takes Petri net XML file, compiles it, and returns a Net object reference.
	 *@param aXMLSourceFile input file with XML net representation.
	 *@return net handle, a newly instantiated Net object.
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CompilationException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Net createNet (final File aXMLSourceFile)
			 throws FileNotFoundException, ParserConfigurationException,
							SAXException, CompilationException, InstantiationException,
							IllegalAccessException {
		final Net net = INSTANCE.getInstance().generateNet (aXMLSourceFile);
		net.setNetDir (aXMLSourceFile.getParentFile());
		return net;
	}

	/**
	 * Takes Petri net XML string, compiles it, and returns a Net object reference.
	 * @param aXMLSource input string with XML net representation.
	 * @return Net, a newly instantiated Net object, or <code>null</code> if the
	 * net could not be created.
	 */
	public static Net createNet (final String aXMLSource) {
		return INSTANCE.getInstance().generateNet (aXMLSource);
	}

	/**
	 * Takes a Net and generates a java source string from it. If the source is
	 * compiled, it instantiates a new Net with the same properties as the argument.
	 * @param net the net to create a java source from.
	 * @return the java source of the net.
	 * @see #createJavaSource(File)
	 */
	public static String createJavaSource (final Net net) {
		return INSTANCE.getInstance().generateNetSource(XMLSerializer.buildXML(net));
	}

	/**
	 * Takes an xml representation of a net and generates a java source from it.
	 * Generation of the net from the java source and the xml source should
	 * give an equal Net object
	 * @param xmlFile the xml source of a net
	 * @return java source of the net
	 */
	public static String createJavaSource (final File xmlFile) {
		try {
			return INSTANCE.getInstance().generateNetSource(XmlUtil.parseFile(xmlFile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves a net object in XML format in a specified file.
	 * @param aNet the net to be saved to XML
	 * @param anXMLOutputFile output file.
	 * @throws IOException when something goes wrong with I/O.
	 */
	public static final void saveAsXML (final Net aNet, final File anXMLOutputFile)
			throws IOException {
		XMLSerializer.writeXML (aNet, anXMLOutputFile);
	}
	
	
	
	
	
	
	
	

	/**
	 * For tests and debugging only.
	 * @param args a <code>String[]</code> value
	 * @exception Exception if an error occurs
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("JFern XML-based net generator.");
		System.out.println("Copyright 2000-2009 by Mariusz Nowostawski and others.");

		if (args.length != 1) {
			System.out.println("   Usage:   java org.rakiura.cpn.NetGenerator input.xml");
		}

		NetGenerator generator = new NetGenerator();
		generator.generateNetSourceFromFile(new File(args[0]));

		java.io.PrintWriter writer = new java.io.PrintWriter(
				new java.io.FileOutputStream(generator.className + ".java"));
		writer.println(generator.netSource.toString());
		writer.flush();
		writer.close();
	}

	public Net generateNet (final File aFile) throws FileNotFoundException,
				ParserConfigurationException, SAXException, CompilationException,
				InstantiationException, IllegalAccessException {
		final Net n = this.generateNet(new FileReader(aFile));
		return n;
	}


	/**
	 * Takes Petri net XML data and returns a Net object reference.
	 * @param aReader the input reader with the XML data to be parsed
	 * @return Net, a newly instantiated Net object
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws CompilationException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public Net generateNet(final Reader aReader)
			 throws ParserConfigurationException, SAXException,
							CompilationException, InstantiationException,
							IllegalAccessException {
		
		String javaNet = null;
		Class classNet = null;
		Net aNet = null;

		javaNet = generateNetSource(aReader);
		classNet = new DynamicCompiler().compileClass(javaNet);
		aNet = (Net) classNet.newInstance();

		return aNet;
	}

	/**
	 * Takes Petri net XML data and returns a Net object reference.
	 * @param aData String input containing the XML data to be parsed.
	 * @return Net, a newly instantiated Net object, or <code>null</code> if the
	 * net cannot be created for some reason.
	 */
	public Net generateNet(final String aData){
		String javaNet = null;
		Class classNet = null;
		Net aNet = null;
		try {
			javaNet = generateNetSource(aData);
			classNet = new DynamicCompiler().compileClass(javaNet);
			aNet = (Net) classNet.newInstance();
		} catch (Exception ce) {
			System.err.println("ERROR: Compiling the net from XML failed! ");
			ce.printStackTrace();
		}

		return aNet;
	}


	/**
	 * Reads an input stream from the filename, and generates the net Java source.
	 * @param aFile a <code>File</code> value
	 * @return String with the generated Java source
	 * @exception ParserConfigurationException if an error occurs
	 * @exception SAXException if an error occurs
	 */
	public String generateNetSourceFromFile (final File aFile)
		throws ParserConfigurationException, SAXException {
		final Document doc = XmlUtil.parseFile(aFile);
		parseXML(doc.getDocumentElement());
		generateStandardImports();
		generateNetSource();
		return this.netSource.toString();
	}


	/**
	 * Reads an input stream from the given reader, and generates the net Java source.
	 * @param aReader the input reader with the XML data to be parsed
	 * @return String with the generated Java source
	 * @exception ParserConfigurationException if an error occurs
	 * @exception SAXException if an error occurs
	 */
	public String generateNetSource (final Reader aReader)
		throws ParserConfigurationException, SAXException {
		final Document doc = XmlUtil.parse (aReader);
		parseXML (doc.getDocumentElement());
		generateStandardImports();
		generateNetSource();
		return this.netSource.toString();
	}


	/**
	 * Reads an XML input, and generates the net Java source.
	 * @param anInput String input containing the XML data to be parsed
	 * @return String with the generated Java source
	 * @exception ParserConfigurationException if an error occurs
	 * @exception SAXException if an error occurs
	 */
	public String generateNetSource(final String anInput)
		throws ParserConfigurationException, SAXException {
		final Document doc = XmlUtil.parse(anInput);
		parseXML(doc.getDocumentElement());
		generateStandardImports();
		generateNetSource();
		return this.netSource.toString();
	}

	/**
	 * Reads an XML document, and generates the net Java source.
	 * @param anInput XML data to be parsed
	 * @return String with the generated Java source
	 */
	public String generateNetSource(final Document anInput) {
		parseXML(anInput.getDocumentElement());
		generateStandardImports();
		generateNetSource();
		return this.netSource.toString();
	}

	/**/
	protected void parseXML(final Element e) {
		this.netType = e.getAttribute(XMLSerializer.TYPE);
		this.netID = e.getAttribute(XMLSerializer.ID);
		this.netCPNLangType = e.getAttribute(XMLSerializer.CPNLANG).trim().toLowerCase();
		if (this.netCPNLangType.equals(XMLSerializer.LANG_BSH)) this.cpnLang = CpnBshLanguage.getInstance();
		else if(this.netCPNLangType.equals(XMLSerializer.LANG_CLOJURE)) this.cpnLang = CpnClojureLanguage.getInstance();
		else if(this.netCPNLangType.equals(XMLSerializer.LANG_KAWA)) this.cpnLang = CpnKawaLanguage.getInstance();
		else this.cpnLang = CpnLanguage.getInstance();
		processNetAnnotations(e);
		processNetLayouts(e);
		processPlaces(e);
		processFusions(e);
		processTransitions(e);
		processArcs(e);
		processSubNets(e);
	}

	protected void processNetLayouts (Element e) {
		final NodeList list = e.getElementsByTagName(XMLSerializer.LAYOUT);
		for (int i = 0; i < list.getLength(); i++) {
		 final String layout = ((Element) list.item(i)).getAttribute(XMLSerializer.FILE);
		 this.netLayouts.add (layout);
	}
	}

	protected void processNetAnnotations (Element e) {
		Element name = null;
		final List imports = new ArrayList();
		final List declarations = new ArrayList();
		Element eImpl = null;

		final NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);

			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.ANNOTATION)) {
				Element en = (Element) n;
				String type = en.getAttribute(XMLSerializer.TYPE);
				if (type.toLowerCase().trim().equals(XMLSerializer.IMPORT)){
					imports.add (en);
				} else if (type.toLowerCase().trim().equals(XMLSerializer.DECLARATION)) {
					declarations.add (en);
				} else if (type.toLowerCase().trim().equals(XMLSerializer.NAME)) {
					name = en;
				} else if (type.toLowerCase().trim().equals(XMLSerializer.IMPLEMENTS)) {
					eImpl = en;
				}
			}
		}


		processImports(imports);
		processName(name);
		processInterfaceImpl(eImpl);
		processDeclarations(declarations);
	}


	protected void processName(Element e) {
		this.className = "NoNamedNet";
		if (e != null){
			NodeList children = e.getElementsByTagName(XMLSerializer.TEXT);
			if (children.getLength() > 0) {
				this.className = children.item(0).getChildNodes().item(0).getNodeValue().trim();
			}
		}
	}

	protected void processInterfaceImpl(Element e) {
		if (e != null){
			NodeList children = e.getElementsByTagName(XMLSerializer.TEXT);
			if (children.getLength() > 0) {
				this.interfaceImpl = children.item(0).getChildNodes().item(0).getNodeValue().trim();
			}
		}
	}


	protected void processImports(List list) {
		for (int i = 0; i < list.size(); i++) {
			NodeList children = ((Element)list.get(i)).getElementsByTagName(XMLSerializer.TEXT);
			//assume there is only one <text> tag;
			if (children.getLength() > 0) {
				String text = children.item(0).getChildNodes().item(0).getNodeValue().trim();
				this.classImports.add(text);
			}
		}
	}

	protected void processDeclarations(final List list) {
		for (int i = 0; i < list.size(); i++) {
			NodeList children = ((Element) list.get(i)).getElementsByTagName(XMLSerializer.TEXT);
			if (children.getLength() > 0) {
				String text = children.item(0).getChildNodes().item(0).getNodeValue();
				this.classDeclarations.add(text);
			}
		}
	}


	protected void processPlaces(Element e){
		final NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.PLACE)) {
				Element en = (Element)n;
				String id = en.getAttribute(XMLSerializer.ID).trim();
				this.netPlaces.add(id);
				NodeList a = en.getElementsByTagName(XMLSerializer.ANNOTATION);
				for (int j = 0; j < a.getLength(); j++){
					if (a.item(j) instanceof Element) {
						if (((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.NAME)) {
							NodeList children = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children.getLength() > 0) {
								final NodeList nl = children.item(0).getChildNodes();
								String text = "";
								if (nl != null && nl.getLength() > 0) {
									text = nl.item(0).getNodeValue();
								}
								this.placeNodeName.put(id, text);
							}
						}
					}
				}
			}
		}
	}

	protected void processFusions(Element e){
		final NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.FUSIONS)) {
				Element en = (Element)n;
				String id = en.getAttribute(XMLSerializer.ID).trim();
				this.fusions.add(XMLSerializer.stringToArray(id));
			}
		}
	}

	protected void processTransitions(final Element e) {
		final NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.TRANSITION)) {
				final Element en = (Element) n;
				final String id = en.getAttribute(XMLSerializer.ID).trim();
				this.netTransitions.put(id, en);
				// lets process the transition annotations (guards/actions)
				final NodeList a = en.getElementsByTagName(XMLSerializer.ANNOTATION);
				for (int j = 0; j < a.getLength(); j++) {
					if (a.item(j) instanceof Element) {
						if ((((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.ACTION)) ||
								(((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.EXPRESSION))) {
							final NodeList children = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children.getLength() > 0) {
								String text = children.item(0).getChildNodes().item(0).getNodeValue();
								this.tranActions.put(id, text);
							}
						} else if (((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.GUARD)) {
							final NodeList children = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children.getLength() > 0 ) {
								final String text = children.item(0).getChildNodes().item(0).getNodeValue();
								this.tranGuards.put(id, text);
							}
						} else if (((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.NAME)) {
							final NodeList children2 = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children2.getLength()>0) {
								final String text = children2.item(0).getChildNodes().item(0).getNodeValue();
								this.transitionNodeName.put(id, text);
							}
						} else if (((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.TYPE)) {
							final NodeList children2 = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children2.getLength()>0) {
								final String text = children2.item(0).getChildNodes().item(0).getNodeValue();
								this.tranTypes.put(id, text);
							}
						} else if (((Element) a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.SPECIFICATION)) {
							final NodeList children2 = ((Element) a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
							//assume there is only one <text> tag;
							if (children2.getLength()>0) {
								final String text = children2.item(0).getChildNodes().item(0).getNodeValue();
								this.tranSpecs.put(id, text);
							}
						}
					}
				}
			}
		}
	}


 protected void processArcs(Element e) {
		final NodeList list = e.getChildNodes();
		for (int i=0; i<list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.ARC)){
				Element en = (Element)n;
				String id = en.getAttribute(XMLSerializer.ID).trim();
				String source = en.getAttribute(XMLSerializer.SOURCE).trim();
				String target = en.getAttribute(XMLSerializer.TARGET).trim();

				if(this.netTransitions.keySet().contains(target)) {//we have an input arc
					this.netInputArcs.put(id, new String[]{source, target});
					// lets process the transition annotations (guards/actions)
					NodeList a = en.getElementsByTagName(XMLSerializer.ANNOTATION);
					for(int j=0; j<a.getLength(); j++){
						if(a.item(j) instanceof Element)
							if(((Element)a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.GUARD)){
								NodeList children = ((Element)a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
								//assume there is only one <text> tag;
								if(children.getLength()>0) {
									String text = children.item(0).getChildNodes().item(0).getNodeValue();
									this.inputArcGuards.put(id, text);
								}
							} else if(((Element)a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.EXPRESSION)){
								NodeList children = ((Element)a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
								//assume there is only one <text> tag;
								if(children.getLength()>0) {
									String text = children.item(0).getChildNodes().item(0).getNodeValue();
									this.inputArcExpressions.put(id, text);
								}
							}
					}
				} else { //we have an output arc
					this.netOutputArcs.put(id, new String[]{source, target});
					// lets process the transition annotations (guards/actions)
					NodeList a = en.getElementsByTagName(XMLSerializer.ANNOTATION);
					for(int j=0; j<a.getLength(); j++){
						if(a.item(j) instanceof Element)
							if(((Element)a.item(j)).getAttribute(XMLSerializer.TYPE).trim().equals(XMLSerializer.EXPRESSION)){
								NodeList children = ((Element)a.item(j)).getElementsByTagName(XMLSerializer.TEXT);
								//assume there is only one <text> tag;
								if(children.getLength()>0) {
									String text = children.item(0).getChildNodes().item(0).getNodeValue();
									this.outputArcExpressions.put(id, text);
								}
							}
					}
				}
			}
		}
	}

	protected void processSubNets  (Element e) {
		final NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName().toLowerCase().trim().equals(XMLSerializer.NET)) {
				NetGenerator gen = new NetGenerator (this.netSource);
				gen.parseXML((Element)n);
				this.netSubnets.add (gen);
			}
		}
	}


	public void generateNetSource() {
		// imports, should only be set in Top level class.
		for (int i = 0; i < this.classImports.size(); i++) {
			this.netSource.append((String) this.classImports.get(i)).append(EOL);
		}
		// name and the base class
		this.netSource.append(EOL).append("/** Net source file. */").append(EOL).append(EOL);
		if (this.interfaceImpl != null) {
			this.netSource.append("public class ")
					.append(this.className).append(" extends BasicNet implements ")
					.append(this.interfaceImpl).append(" {").append(EOL).append(EOL);
		} else {
			this.netSource.append("public class ").append(this.className)
					.append(" extends BasicNet {").append(EOL).append(EOL);
		}

		// class declarations
		for(int i=0; i < this.classDeclarations.size(); i++){
			this.netSource.append((String)this.classDeclarations.get(i)).append(EOL);
		}

		// places declaration
		this.netSource.append(EOL).append("  /* Places declaration. */").append(EOL);
		for (int i=0; i < this.netPlaces.size(); i++) {
			this.netSource.append("  private Place ").append(placePrefix)
						.append((String)this.netPlaces.get(i))
						.append(" = new Place(\"")
						.append((String) this.netPlaces.get(i))
						.append("\");").append(EOL);
		}

		// transitions declaration
		this.netSource.append(EOL).append("  /* Transitions declaration. */").append(EOL);
		final Iterator tit = this.netTransitions.keySet().iterator();
		while (tit.hasNext()) {
			final String id = (String) tit.next();
			this.netSource.append("  private Transition ").append(transitionPrefix)
					.append(id)
					.append(" = new Transition(\"")
					.append(id)
					.append("\");").append(EOL);
		}

		// input arcs declarations
		this.netSource.append(EOL).append("  /* Input Arcs declaration. */").append(EOL);
		final Iterator ait = this.netInputArcs.keySet().iterator();
		while (ait.hasNext()) {
			final String id = (String) ait.next();
			final String[] srcdst = (String[]) this.netInputArcs.get(id);
			this.netSource.append("  private InputArc ").append(arcPrefix)
					.append(id)
					.append(" = new InputArc(")
					.append(placePrefix).append(srcdst[0])
					.append(", ")
					.append(transitionPrefix).append(srcdst[1])
					.append(");").append(EOL);
		}

		// output arcs declarations
		this.netSource.append(EOL).append("  /* Output Arcs declaration. */").append(EOL);
		final Iterator oit = this.netOutputArcs.keySet().iterator();
		while (oit.hasNext()) {
			final String id = (String) oit.next();
			final String[] srcdst = (String[]) this.netOutputArcs.get(id);
			this.netSource.append("  private OutputArc ").append(arcPrefix)
					.append(id)
					.append(" = new OutputArc(")
					.append(transitionPrefix).append(srcdst[0])
					.append(", ")
					.append(placePrefix).append(srcdst[1])
					.append(");").append(EOL);
		}

		// net constructor, register all places and transitions
		this.netSource.append(EOL)
				.append("  /* The default " + this.className + " constructor. */").append(EOL);
		this.netSource.append("  public ").append(this.className).append("() {").append(EOL);
		this.netSource.append("    setName(\"").append(this.className).append("\");").append(EOL);
		this.netSource.append("    setID(\"").append(this.netID).append("\");").append(EOL);
		this.netSource.append("    setCpnLang(\"").append(this.netCPNLangType).append("\");").append(EOL);
		
		this.netSource.append("    setTypeText(\"").append(this.netType).append("\");")
				.append(EOL);
		if (this.interfaceImpl != null) {
			this.netSource.append("    setImplementsText(\"").append(this.interfaceImpl)
					.append("\");").append(EOL);
		} else {
			this.netSource.append("    setImplementsText(\"\");").append(EOL);
		}
		final StringBuffer importBuf = new StringBuffer ();
		for (int i = 0; i < this.classImports.size(); i++) {
			importBuf.append((String) this.classImports.get(i)).append(EOL);
		}
		this.netSource.append("    setImportText(\"")
				.append(slashQuotes(importBuf.toString())).append("\");").append(EOL);
		final StringBuffer declarationBuf = new StringBuffer ();
		for (int i = 0; i < this.classDeclarations.size(); i++) {
			declarationBuf.append((String) this.classDeclarations.get(i)).append(EOL);
		}
		this.netSource.append("    setDeclarationText(\"")
				.append(slashQuotes(declarationBuf.toString())).append("\");").append(EOL);

		final Iterator layoutIterator =  this.netLayouts.iterator();
		while (layoutIterator.hasNext()) {
			this.netSource.append("    addLayout(\"").append(layoutIterator.next().toString())
					.append("\");").append(EOL);
		}
		this.netSource.append(EOL);

		addNetElementsInConstructor (this.netPlaces.iterator(), placePrefix);
		addNetElementsInConstructor (this.netTransitions.keySet().iterator(), transitionPrefix);
		addNetElementsInConstructor (this.netInputArcs.keySet().iterator(), arcPrefix);
		addNetElementsInConstructor (this.netOutputArcs.keySet().iterator(), arcPrefix);

		// add all subnets of this net
		for (int i = 0; i < this.netSubnets.size(); i++) {
			this.netSource.append("    add(new ")
						 .append(((NetGenerator) this.netSubnets.get(i)).className)
						 .append("());").append(EOL);
		}

		// transition actions
		this.netSource.append(EOL).append("    /* Transition actions. */").append(EOL);
		final Iterator trait = this.tranActions.keySet().iterator();
		while (trait.hasNext()) {
			final String id = (String) trait.next();
			final String body = (String) this.tranActions.get(id);
			this.netSource.append(EOL).append("    ").append(transitionPrefix)
					.append(id)
					.append(".setAction(").append(transitionPrefix).append(id).append(".new Action() {").append(EOL)
					.append("       public void execute() {").append(EOL)
					.append(cpnLang.bodyTransitionAction(body)).append(EOL)
					.append("\n       }").append(EOL)
					.append("    });").append(EOL);
			// add the action text
			this.netSource.append("    ").append(transitionPrefix)
					.append(id)
					.append(".setActionText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// transition guards
		this.netSource.append(EOL).append("    /* Transition guards. */").append(EOL);
		final Iterator trgit = this.tranGuards.keySet().iterator();
		while (trgit.hasNext()) {
			final String id = (String) trgit.next();
			final String body = (String) this.tranGuards.get(id);
			this.netSource.append(EOL).append("    ").append(transitionPrefix)
					.append(id)
					.append(".setGuard(").append(transitionPrefix).append(id).append(".new Guard() {").append(EOL)
					.append("       public boolean evaluate() {").append(EOL)
					.append(cpnLang.bodyTransitionGuard(body)).append(EOL)
					.append("       }").append(EOL)
					.append("    });").append(EOL);
			this.netSource.append("    ").append(transitionPrefix)
					.append(id)
					.append(".setGuardText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// transition types
		this.netSource.append(EOL).append("    /* Transition types. */").append(EOL);
		final Iterator trtit = this.tranTypes.keySet().iterator();
		while (trtit.hasNext()) {
			final String id = (String) trtit.next();
			final String body = (String) this.tranTypes.get(id);
			this.netSource.append("    ").append(transitionPrefix)
					.append(id)
					.append(".setTypeText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// transition type specifications
		this.netSource.append(EOL).append("    /* Transition type specifications. */").append(EOL);
		final Iterator trsit = this.tranSpecs.keySet().iterator();
		while (trsit.hasNext()) {
			final String id = (String) trsit.next();
			final String body = (String) this.tranSpecs.get(id);
			this.netSource.append("    ").append(transitionPrefix)
					.append(id)
					.append(".setSpecificationText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// input arc guards
		this.netSource.append(EOL).append("    /* Input Arcs guards. */").append(EOL);
		final Iterator iagit = this.inputArcGuards.keySet().iterator();
		while (iagit.hasNext()) {
			final String id = (String) iagit.next();
			final String body = (String) this.inputArcGuards.get(id);
			this.netSource.append(EOL).append("    ").append(arcPrefix)
					.append(id)
					.append(".setGuard(").append(arcPrefix).append(id).append(".new Guard() {").append(EOL)
					.append("       public boolean evaluate() {").append(EOL)
					.append(cpnLang.bodyInputArcGuard(body)).append(EOL)
					.append("       }").append(EOL)
					.append("    });").append(EOL);
			this.netSource.append("    ").append(arcPrefix)
					.append(id)
					.append(".setGuardText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// input arc expressions
		this.netSource.append(EOL).append("    /* Input Arcs expressions. */").append(EOL);
		final Iterator iaeit = this.inputArcExpressions.keySet().iterator();
		while (iaeit.hasNext()) {
			final String id = (String) iaeit.next();
			final String body = (String) this.inputArcExpressions.get(id);
			this.netSource.append(EOL).append("    ").append(arcPrefix)
					.append(id)
					.append(".setExpression(").append(arcPrefix).append(id).append(".new Expression() {").append(EOL)
					.append("       public void evaluate() {").append(EOL)
					.append(cpnLang.bodyInputArcExpression(body)).append(EOL)
					.append("       }").append(EOL)
					.append("    });").append(EOL);
			this.netSource.append("    ").append(arcPrefix)
					.append(id)
					.append(".setExpressionText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// output arc expressions
		this.netSource.append(EOL).append("    /* Output Arcs expressions. */").append(EOL);
		final Iterator oaeit = this.outputArcExpressions.keySet().iterator();
		while (oaeit.hasNext()) {
			final String id = (String) oaeit.next();
			final String body = (String) this.outputArcExpressions.get(id);
			this.netSource.append(EOL).append("    ").append(arcPrefix)
					.append(id)
					.append(".setExpression(").append(arcPrefix).append(id).append(".new Expression() {").append(EOL)
					.append("       public Multiset evaluate() {").append(EOL)
					.append(cpnLang.bodyOutputArcExpression(body)).append(EOL)
					.append("       }").append(EOL)
					.append("    });").append(EOL);
			this.netSource.append("    ").append(arcPrefix)
					.append(id)
					.append(".setExpressionText(\"").append(slashQuotes(body)).append("\");").append(EOL);
		}

		// node Names and IDs
		this.netSource.append(EOL).append(EOL).append("    /* Place Node Names */").append(EOL);
		final Iterator nnit = this.placeNodeName.keySet().iterator();
		while (nnit.hasNext()) {
			final String id = (String) nnit.next();
			final String body = (String) this.placeNodeName.get(id);
			this.netSource.append(EOL).append("    ").append(placePrefix)
					.append(id)
					.append(".setName(\"")
					.append(body)
					.append("\");");
			this.netSource.append(EOL).append("    ").append(placePrefix)
					.append(id)
					.append(".setID(\"")
					.append(id)
					.append("\");");
		}

		this.netSource.append(EOL).append(EOL).append("    /* Transition Node Names */").append(EOL);
		final Iterator trit = this.transitionNodeName.keySet().iterator();
		while (trit.hasNext()) {
			final String id = (String) trit.next();
			final String body = (String) this.transitionNodeName.get(id);
			this.netSource.append(EOL).append("    ").append(transitionPrefix)
					.append(id)
					.append(".setName(\"")
					.append(body)
					.append("\");");
			this.netSource.append(EOL).append("    ").append(transitionPrefix)
					.append(id)
					.append(".setID(\"")
					.append(id)
					.append("\");");
		}

		this.netSource.append(EOL).append(EOL).append("    /* OutputArcs Names and IDs */").append(EOL);
		generateIDentry (this.netOutputArcs, arcPrefix);
		this.netSource.append(EOL).append(EOL).append("    /* InputArcs Names and IDs */").append(EOL);
		generateIDentry (this.netInputArcs, arcPrefix);

		this.netSource.append(EOL).append(EOL).append("    /* Fusion of Places */").append(EOL);
		//
		Iterator fusionsIt = this.fusions.iterator();
		while (fusionsIt.hasNext()) {
			String[] ids = (String[]) fusionsIt.next();
			//assert (ids.length > 1);
			for (int i = 1; i < ids.length; i++) {
				this.netSource.append(EOL).append("    "+placePrefix+ids[0]+".addPlace( (Place)forID(\""+ids[i]+"\") );");
			}
		}

		this.netSource.append("  }").append(EOL);

		// inner classes:   add all subnets source

		for (int i = 0; i < this.netSubnets.size(); i++) {
			this.netSource.append(EOL).append(EOL)
			  .append("// Inner net declaration").append(EOL);
			((NetGenerator) this.netSubnets.get(i)).generateNetSource();
		}

		// class end
		this.netSource.append(EOL).append("}").append(EOL);
	}

	protected void generateStandardImports() {
		this.netSource.append("import org.rakiura.cpn.Net;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.Place;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.Transition;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.InputArc;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.OutputArc;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.BasicNet;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.Multiset;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.CpnContext;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.Marking;").append(EOL);
		this.netSource.append("import org.rakiura.cpn.CpnClojureLanguage;").append(EOL);
	}

	protected void generateIDentry(Map nodeMap, String nodePrefix) {
		final Iterator iter = nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			final String id = (String) iter.next();
			this.netSource.append(EOL).append("    ").append(nodePrefix)
					.append(id)
					.append(".setName(\"_")
					.append(id)
					.append("\");");
			this.netSource.append(EOL).append("    ").append(nodePrefix)
					.append(id)
					.append(".setID(\"")
					.append(id)
					.append("\");");
		}
	}

	protected void addNetElementsInConstructor (final Iterator iter, final String prefix) {
		while (iter.hasNext()) {
			final String id = (String) iter.next();
			this.netSource.append("    add(")
					.append(prefix)
					.append(id)
					.append(");").append(EOL);
		}
	}

	/**/
	public static String slashQuotes(final String s){
		if (s.trim().equals("")) return s;
		/* quotes */
		String result = "";
		StringTokenizer st = new StringTokenizer(s, "\"", false);
		while(st.hasMoreTokens()){
			result = result + st.nextToken() + "\\\"";
		}

		result = result.substring(0, result.length() - 2);
		/* new lines characters */
		String anotherResult = "";
		st = new StringTokenizer(result, EOL, false);
		while(st.hasMoreTokens()){
			anotherResult = anotherResult + st.nextToken() + "\\n";
		}
		anotherResult = anotherResult.substring(0, anotherResult.length() - 2);
		return anotherResult;
	}

	public StringBuffer getNetSource() {
		return netSource;
	}

	public static String getEOL() {
		return EOL;
	}

	public static NetGenerator getINSTANCE() {
		return INSTANCE;
	}

	public static String getPlacePrefix() {
		return placePrefix;
	}

	public static String getTransitionPrefix() {
		return transitionPrefix;
	}

	public String getArcPrefix() {
		return arcPrefix;
	}

	public List getClassDeclarations() {
		return classDeclarations;
	}

	public List getClassImports() {
		return classImports;
	}

	public String getClassName() {
		return className;
	}

	public List getFusions() {
		return fusions;
	}

	public Map getInputArcExpressions() {
		return inputArcExpressions;
	}

	public Map getInputArcGuards() {
		return inputArcGuards;
	}

	public String getInterfaceImpl() {
		return interfaceImpl;
	}

	public String getNetID() {
		return netID;
	}

	public Map getNetInputArcs() {
		return netInputArcs;
	}

	public Set getNetLayouts() {
		return netLayouts;
	}

	public Map getNetOutputArcs() {
		return netOutputArcs;
	}

	public List getNetPlaces() {
		return netPlaces;
	}

	public List getNetSubnets() {
		return netSubnets;
	}

	public Map getNetTransitions() {
		return netTransitions;
	}

	public String getNetType() {
		return netType;
	}

	public Map getOutputArcExpressions() {
		return outputArcExpressions;
	}

	public Map getPlaceNodeName() {
		return placeNodeName;
	}

	public Map getTranActions() {
		return tranActions;
	}

	public Map getTranGuards() {
		return tranGuards;
	}

	public Map getTransitionNodeName() {
		return transitionNodeName;
	}

	public Map getTranSpecs() {
		return tranSpecs;
	}

	public Map getTranTypes() {
		return tranTypes;
	}

	public void setNetID(String netID) {
		this.netID = netID;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

} // NetGenerator
//////////////////// end of file ////////////////////