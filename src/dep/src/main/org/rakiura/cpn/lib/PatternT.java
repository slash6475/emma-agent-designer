// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.lib;

/**/
import org.rakiura.cpn.BasicNet;
import org.rakiura.cpn.Transition;

/**
 * Base abstract class for components. This is an abstract pattern
 * with single transition. It is being used by all other, more complex
 * net patterns. 
 * 
 *<br><br>
 * PatterT.java<br>
 * Created: Mon Oct  2 22:03:31 2000<br>
 *
 * @author Mariusz Nowostawski
 * @version 4.0.0 $Revision: 1.3 $
 */
public abstract class PatternT extends BasicNet {

  protected Transition transition;

  public PatternT() {
    this.transition = new Transition();
    add(this.transition);
  }
  
} // PatternT
//////////////////// end of file ////////////////////
