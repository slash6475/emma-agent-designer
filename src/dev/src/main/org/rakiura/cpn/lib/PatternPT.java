// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.lib;

/**/
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Place;

/**
 * Base abstract class for components. This is an abstract pattern
 * with single input place and single transition with an arc to that
 * place. It is being used by all other, more complex net patterns. 
 * 
 *<br><br>
 * PatterPT.java<br>
 * Created: Mon Oct  2 22:03:31 2000<br>
 *
 * @author Mariusz Nowostawski
 * @version 4.0.0 $Revision: 1.6 $
 */
public abstract class PatternPT extends PatternT {

  protected Place inputPlace;
  protected InputArc inputArc;

  public PatternPT() {
    this.inputPlace = new Place();
    this.inputArc = new InputArc(this.inputPlace, this.transition);
    this.inputArc.setExpression(this.inputArc.new Expression() {
        public void evaluate() {
          var("X");
        }
      });
    add(this.inputPlace);
  }
  
} // PatternT
//////////////////// end of file ////////////////////
