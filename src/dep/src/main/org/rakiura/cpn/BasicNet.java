// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.rakiura.cpn.event.PlaceListener;
import org.rakiura.cpn.event.TransitionListener;

/**
 * Implements a basic Petri Net. This implementation is based on two
 * HashMaps.
 *
 *<br><br>
 * BasicNet.java<br>
 * Created: Tue Sep 26 15:46:46 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.45 $
 *@since 3.0
 */
public class BasicNet extends Node implements Net {

	private static final long serialVersionUID = 3546643209112269365L;
	
	private Set layouts = new HashSet ();
	private String typeText = "";
	private String implementsText = "";
	private String importText = "";
	private String declarationText = "";
	private String cpnlang = XMLSerializer.LANG_NATIVE;

	/**
	 * Net directory. The directory this net might be saved as a file.
	 * It defaults to the user directory. */
	private File netDir = new File(System.getProperty("user.dir"));

	/**
	 * Flag to indicate if the internal datastructures
	 * for this net and subnets are up-to-date. */
	private boolean cachedAll = false;

	/**
	 * All the transitions for this net. This includes
	 * all the transitions from all the subnets. */
	private transient Transition[] allTransitions;

	/**
	 * All the places for this net. This includes
	 * all the places from all the subnets. */
	private transient Place[] allPlaces;

	private transient InputArc[] allInputArcs;
	private transient OutputArc[] allOutputArcs;
	private transient Net[] allSubnets;

	/** Hash of all net elements of this net and its all subnets by IDs. */
	transient Map set_forID = new HashMap();

	/** Hash of all net elements of this net and its all subnets by Names. */
	transient Map set_forName = new HashMap();

	/** List of all net elements of this net. */
	private List netElements = new ArrayList();

	/** Instance counter. */
	private static long COUNT = 1;

	/**
	 * Creates new instance of the Net.
	 */
	public BasicNet() {
		super ("Untitled"+(COUNT++));
	}
	
	public Net getInstance() {
		return new BasicNet();
	}

	/**
	 * Creates a new <code>BasicNet</code> instance with a given name.
	 * @param name a <code>String</code> name value for this Net.
	 */
	public BasicNet (String name) {
		super (name);
	}

	/**
	 * {@inheritDoc}
	 * @param aListener {@inheritDoc}
	 */
	public void addPlaceListener (final PlaceListener aListener) {
		final Place[] p = getAllPlaces();
		for (int i = 0; i < p.length; i++) {
			p[i].addPlaceListener(aListener);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param aListener {@inheritDoc}
	 */
	public void addTransitionListener(final TransitionListener aListener) {
		final Transition[] t = getAllTransitions();
		for (int i = 0; i < t.length; i++) {
			t[i].addTransitionListener(aListener);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param aListener {@inheritDoc}
	 */
	public void removePlaceListener(PlaceListener aListener) {
		final Place[] p = getAllPlaces();
		for (int i = 0; i < p.length; i++) {
			p[i].removePlaceListener(aListener);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param aListener {@inheritDoc}
	 */
	public void removeTransitionListener(TransitionListener aListener) {
		final Transition[] t = getAllTransitions();
		for (int i = 0; i < t.length; i++) {
			t[i].removeTransitionListener(aListener);
		}
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public NetElement[] getNetElements() {
		return (NetElement[]) this.netElements.toArray (new NetElement[this.netElements.size()]);
	}

	/**
	 * {@inheritDoc}
	 * @param node {@inheritDoc}
	 * @return {@inheritDoc} */
	public Net add(NetElement node) {
// DEBUG if (node == null) throw new RuntimeException("Adding NULL node!");
		this.netElements.add(node);
		this.cachedAll = false;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * @param node {@inheritDoc}
	 * @return {@inheritDoc} */
	public NetElement remove(NetElement node) {
		this.cachedAll = false;
		if (this.netElements.remove(node))
			return node;
		return null;
	}
	
	/**
	 * Removes all listeners, places, transitions and arcs.
	 */
	public void clear() {
		this.cachedAll = false;
		this.netElements.clear();
		rehash();
	}

	/**
	 * {@inheritDoc}
	 *@param id {@inheritDoc}
	 *@return {@inheritDoc} */
	public NetElement forID(String id) {
		if (!this.cachedAll) rehash();
		return (NetElement) this.set_forID.get(id);
	}

	/**
	 * {@inheritDoc}
	 *@param name {@inheritDoc}
	 *@return {@inheritDoc} */
	public NetElement forName(String name) {
		if (!this.cachedAll) rehash();
		return (NetElement) this.set_forName.get(name);
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public InputArc[] getAllInputArcs () {
		if (!this.cachedAll) rehash();
			return this.allInputArcs;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public OutputArc[] getAllOutputArcs () {
		if (!this.cachedAll) rehash();
			return this.allOutputArcs;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Place[] getAllPlaces() {
		if (!this.cachedAll) rehash();
		return this.allPlaces;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Transition[] getAllTransitions() {
		if (!this.cachedAll)	rehash();
		return this.allTransitions;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Net[] getAllSubnets() {
			if (!this.cachedAll)	rehash();
			return this.allSubnets;
	}

	/**
	 * Visitor pattern.
	 * @param aVisitor a <code>NetVisitor</code> value
	 * @return a <code>NetElement</code> value
	 */
	public NetElement apply(final NetVisitor aVisitor) {
		aVisitor.net(this);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Marking getMarking() {
		final Marking m = new Marking();
		final Place[] p = getAllPlaces();
		for (int i = 0; i < p.length; i++) {
			m.put(p[i], p[i].getTokens());
		}
		return m;
	}

	/**
	 * {@inheritDoc}
	 *@param aMarking {@inheritDoc}
	 *@see #resetMarking
	 *@return {@inheritDoc}
	 */
	public Marking setMarking(final Marking aMarking) {
		final Marking old = getMarking();
		final Iterator iter = aMarking.places().iterator();
		while (iter.hasNext()) {
			final Place p = (Place) iter.next();
			Place np = (Place) forID (p.getID());
			if (np == null) {
				np = (Place) forName (p.getName ());
			}
			if (np != null) {
				np.clearTokens ();
				np.addTokens (aMarking.forID (p.getID()));
			}
		}
		return old;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc} */
	public Marking resetMarking() {
		final Marking old = getMarking();
		final Place[] p = getAllPlaces();
		for (int i = 0; i < p.length; i++) {
			p[i].clearTokens();
		}
		return old;
	}

	/**
	 * {@inheritDoc}. This method should be
	 * used to update the internal net datastructures after changing
	 * the name of any, already inserted into Net, net elements.
	 */
	@SuppressWarnings("serial")
	public void rehash () {
		final List transitions = new ArrayList ();
		final List places = new ArrayList ();
		final List inArcs = new ArrayList ();
		final List outArcs = new ArrayList ();
		final List subnets = new ArrayList ();
		this.set_forName.clear ();
		this.set_forID.clear ();
		final NetVisitor walker = new NetVisitorAdapter () {
			public void transition (Transition t) {
				transitions.add (t);
				BasicNet.this.set_forName.put (t.getName(), t);
				BasicNet.this.set_forID.put (t.getID(), t);
			}
			public void place (Place t) {
				places.add (t);
				BasicNet.this.set_forName.put (t.getName(), t);
				BasicNet.this.set_forID.put (t.getID(), t);
			}
			public void inputArc (InputArc t) {
				inArcs.add (t);
				BasicNet.this.set_forName.put (t.getName(), t);
				BasicNet.this.set_forID.put (t.getID(), t);
			}
			public void outputArc (OutputArc t) {
				outArcs.add (t);
				BasicNet.this.set_forName.put (t.getName(), t);
				BasicNet.this.set_forID.put (t.getID(), t);
			}
			public void net (Net net) {
				subnets.add (net);
				BasicNet.this.set_forName.put (net.getName(), net);
				BasicNet.this.set_forID.put (net.getID(), net);
				super.net (net);
			}
		};

		final Iterator iter = this.netElements.iterator();
		while (iter.hasNext()) {
			((NetElement) iter.next()).apply(walker);
		}

		this.allPlaces = (Place[]) places.toArray (new Place[places.size()]);
		this.allTransitions = (Transition[]) transitions.toArray (new Transition[transitions.size()]);
		this.allInputArcs = (InputArc[]) inArcs.toArray(new InputArc[inArcs.size()]);
		this.allOutputArcs = (OutputArc[]) outArcs.toArray(new OutputArc[outArcs.size()]);
		this.allSubnets = (Net[]) subnets.toArray(new Net[subnets.size()]);
		this.cachedAll = true;
	}

	/**
	 * Re-generates and re-hashes all the IDs in this net. This method will try to generate
	 * new, nice and unique IDs for all the net elements. Use this method with care,
	 * as it invalidates all the datastructures which rely on the old IDs of this net
	 * (layout, markings, etc).names of all the net elements.
	 */
	@SuppressWarnings("serial")
	public void regenerateIDs () {
		final String prefix = getName() + "_" +
				 (new Random (System.currentTimeMillis()).nextInt(99)) + "_";
		this.set_forName.clear();

		final NetVisitor walker = new NetVisitorAdapter() {
			int Tcounter =  1; // transition counter
			int Pcounter = 1; // place counter
			int Acounter = 1; // arc counter
			int Ncounter = 1; // net counter
			public void transition(Transition t) {
				String tmpID = prefix + "t" + (this.Tcounter++);
				t.setID (tmpID);
			}
			public void place(Place t) {
				String tmpID = prefix + "p" + (this.Pcounter++);
				t.setID (tmpID);
			}
			public void inputArc(InputArc t) {
				String tmpID = prefix + "a" + (this.Acounter++);
				t.setID (tmpID);
			}
			public void outputArc(OutputArc t) {
				String tmpID = prefix + "a" + (this.Acounter++);
				t.setID (tmpID);
			}

			public void net(Net n) {
				String tmpID = prefix + "n" + (this.Ncounter++);
				n.setID (tmpID);
				super.net(n);
			}
		};

		final Iterator iter = this.netElements.iterator();
		while (iter.hasNext()) {
			((NetElement) iter.next()).apply(walker);
		}
		rehash ();
	}


	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public String getDeclarationText () {
		return this.declarationText;
	}

	/**
	 * {@inheritDoc}
	 * @param aText {@inheritDoc}
	 */
	public void setDeclarationText (final String aText) {
		this.declarationText = aText;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public String getImportText () {
		return this.importText;
	}

	/**
	 * {@inheritDoc}
	 * @param aText {@inheritDoc}
	 */
	public void setImportText (final String aText) {
		this.importText = aText;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public String getImplementsText () {
		return this.implementsText;
	}

	/**
	 * {@inheritDoc}
	 * @param aText {@inheritDoc}
	 */
	public void setImplementsText (final String aText) {
		this.implementsText = aText;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public String getTypeText() {
		return this.typeText;
	}

	/**
	 * {@inheritDoc}
	 * @param aText {@inheritDoc}
	 */
	public void setTypeText(final String aText) {
		this.typeText = aText;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public String[] getLayouts () {
		return (String[]) this.layouts.toArray (new String[this.layouts.size()]);
	}

	/**
	 * {@inheritDoc}
	 * @param aFilename {@inheritDoc}
	 */
	public void addLayout (String aFilename) {
		this.layouts.add (aFilename);
	}

	/**
	 * {@inheritDoc}
	 * @param aFilename {@inheritDoc}
	 */
	public void removeLayout (String aFilename) {
		this.layouts.remove (aFilename);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAllLayouts () {
		this.layouts.clear();
	}

	/**
	 * {@inheritDoc}
	 * @param aFile {@inheritDoc}
	 */
	public void setNetDir (final File aFile) {
		this.netDir = aFile;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public File getNetDir () {
		return this.netDir;
	}
	
	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */	
	public String getCpnLang() {
		return cpnlang;
	}
	
	/** Sets the language. */
	public void setCpnLang(String lang) {
		this.cpnlang = lang;
	}

	/**
	 * Returns human readable description of this net.
	 * @return a <code>String</code> value.
	 */
	public String toString() {
		if (!this.cachedAll) rehash ();
		String res = "(Network: ";
		res += getName();
		res += "  ["+getAllPlaces().length+" P]";
		res += "["+getAllTransitions().length+" T]";
		res += "["+getAllInputArcs().length+" I]";
		res += "["+getAllOutputArcs().length+" O] ";
		return res + ")";
	}


} // BasicNet
//////////////////// end of file ////////////////////
