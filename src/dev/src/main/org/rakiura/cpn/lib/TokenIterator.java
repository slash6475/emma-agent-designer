// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.lib;

/**/
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Transition;

/**
 * Simple control pattern representing Iterator Pattern.
 * 
 *<br><br>
 * TokenIterator.java<br>
 * Created: Mon Oct  2 21:43:18 2000<br>
 *
 * @author Mariusz Nowostawski
 * @version 4.0.0 $Revision: 1.5 $
 */
public class TokenIterator extends PatternPT {

  private static final long serialVersionUID = 3257565092316067124L;
	
  protected OutputArc outputArc;

  public TokenIterator() {
    this.outputArc = new OutputArc(this.transition, this.inputPlace);
  }

  public Transition transition(){ return this.transition; }
  public Place place(){ return this.inputPlace; }
  public InputArc inputArc(){ return this.inputArc; }
  public OutputArc outputArc(){ return this.outputArc; }

} // TokenIterator
//////////////////// end of file ////////////////////
