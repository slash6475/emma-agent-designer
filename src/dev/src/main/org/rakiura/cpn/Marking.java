// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implments a basic Marking concept. Marking is a simple hash-like
 * data structure which maps places to their respective multisets. Marking 
 * can be obtained from any existing net, and can be applied to any net, given,
 * that the places in both nets match. 
 * 
 *<br><br>
 * Marking.java<br>
 * Created: Fri Sep 29 12:20:59 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.6 $
 */
public class Marking {

	private PlaceHolder places;
	private Map marking;

	/**
	 * Default constructor. Creates an empty marking.
	 */
	public Marking() {
		this.places = new PlaceHolder();
		this.marking = new HashMap();
	}

	public Multiset forID(String placeID) {
		return (Multiset) this.marking.get(this.places.forID(placeID));
	}

	public Multiset forName(String placeName) {
		return (Multiset) this.marking.get(this.places.forName(placeName));
	}

	public Marking put(Place place, Multiset multiset) {
		this.places.add(place);
		this.marking.put(place, multiset);
		return this;
	}

	public Set places() {
		return this.places.places();
	}

	public String toString() {
		String res = "Marking: ";
		final Iterator iter = this.marking.keySet().iterator();
		while (iter.hasNext()) {
			Place p = (Place) iter.next();
			res += "(" + p.toString() + " -> " + this.marking.get(p) + ")";
		}
		return res;
	}

} // Marking
//////////////////// end of file ////////////////////
