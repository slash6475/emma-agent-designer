// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.lib;

/**/
import java.util.ArrayList;
import java.util.List;

import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Transition;

/**
 * Simple control pattern representing Sequence Pattern. This pattern
 * net begins with the input place, which makes the first transition
 * enabled, and ends with transition n-th, which puts the processed
 * token into the final output place. All intermediate places and arcs
 * are not visible to the user, they simply propagate single token at
 * the time. This sequence does not wait till the token is passed till
 * the end of the sequence, and can fire another sequence once the
 * input place has at least one token (first transition is enabled).
 *
 *<br><br>
 * Sequence.java<br>
 * Created: Tue Oct  3 09:21:20 2000<br>
 *
 * @author Mariusz Nowostawski
 * @version 4.0.0 $Revision: 1.13 $
 */
public class Sequence extends PatternPT {

	private static final long serialVersionUID = 3618986693403752753L;
	
	protected Place outputPlace;
	protected OutputArc outputArc;
	protected List transitionList;


	/** Creates sequence.
	 */
	public Sequence() {

		final Transition trans2 = new Transition();
		this.transitionList = new ArrayList(10);
		this.transitionList.add(this.transition);
		this.transitionList.add(trans2);

		final Place tmpPlace = new Place();
		createOutputArc(this.transition, tmpPlace);
		createInputArc(tmpPlace, trans2);

		this.outputPlace = new Place();
		createOutputArc(trans2, this.outputPlace);

		add(tmpPlace).add(trans2).add(this.outputPlace);
	}

	/**
	 * xxx
	 * @return xxx
	 */
	public Transition[] getAllTransitions(){
		return (Transition[]) this.transitionList.toArray(new Transition[this.transitionList.size()]);
	}

	/**
	 * xxxx
	 * @return xxx
	 */
	public Place inputPlace(){
		return this.inputPlace;
	}

	/**
	 * xxx
	 * @return xxxx
	 */
	public Place outputPlace(){
		return this.outputPlace;
	}

	/**
	 * xxx
	 * @param p xxx
	 * @param t xxx
	 * @return xxx
	 */
	private InputArc createInputArc(Place p, Transition t) {
		final InputArc arc = new InputArc(p, t);
		arc.setGuard(arc.new Guard() {
				public boolean evaluate() {
					final Multiset multiset = getMultiset();
					return (multiset.size()>0);
				}
			});
		arc.setExpression(arc.new Expression() {
				public void evaluate() {
					var("X");
				}
			});
		return arc;
	}


	/**
	 * xxx
	 * @param t xxx
	 * @param p xxx
	 * @return xxx
	 */
	private OutputArc createOutputArc(Transition t, Place p){
		final OutputArc arc = new OutputArc(t, p);
		arc.setExpression(arc.new Expression() {
				public Multiset evaluate() {
					final Multiset multiset = getMultiset();
					final Multiset result = new Multiset();
					result.add(multiset.getAny());
					return result;
				}
			});
		return arc;
	}


} // Sequence
//////////////// end of file ////////////////////