// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.sample;

/**/
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Transition;
import org.rakiura.cpn.lib.Sequence;

/**
 * Simple example of two sequencial tasks.
 * 
 *<br><br>
 * TwoTasksNet.java<br>
 * Created: Tue Oct  3 16:53:15 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.13 $
 */
public class TwoTasksNet extends Sequence {
  
  private static final long serialVersionUID = 3256444694396678193L;

  public TwoTasksNet() {
    final Transition[] l = getAllTransitions();
    final Transition t1 =  l[0];
    final OutputArc out1 = (OutputArc) t1.outputArcs().get(0);
    out1.setExpression(out1.new Expression() {
        public Multiset evaluate() {
          final Multiset out = new Multiset();
          Object t = getMultiset().getAny();
          int n = ((Number) t).intValue();
          out.add(new Integer(n + 1));
          return out;
        }
      });

    final Transition t2 =  l[1];
    final OutputArc out2 = (OutputArc) t2.outputArcs().get(0);
    out2.setExpression(out2.new Expression() {
        public Multiset evaluate() {
          int n = ((Number) getMultiset().getAny()).intValue();
          return new Multiset(new Integer(n + 1));
        }
      });
  }
  
  public TwoTasksNet(Multiset m) {
    this();
    inputPlace().addTokens(m);
  }
    
} // TwoTasksNet
//////////////////// end of file ////////////////////
