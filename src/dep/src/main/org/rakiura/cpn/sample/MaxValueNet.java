// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.sample;

/**/
import java.util.ArrayList;
import java.util.List;

import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.lib.TokenIterator;


/**
 * Example of simple maximum value network.
 * 
 *<br><br>
 * MaxValueNet.java<br>
 * Created: Mon Oct  2 22:07:43 2000<br>
 *
 *@author <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.14 $
 */
public class MaxValueNet extends TokenIterator {
  

  private static final long serialVersionUID = 3257562901899392820L;

  public MaxValueNet() {
    // adds the guard on the input arc
    InputArc inarc = inputArc();
    inarc.setGuard(inarc.new Guard() {
        public boolean evaluate() {
          final Multiset m = getMultiset();
          return (m.size() > 1);
        }
      });

    inarc.setExpression(inarc.new Expression() {
        public void evaluate() {
          // pick up two tokens
          var(2);
        }
      });


    // expression to return the maximum of two tokens
    OutputArc outarc = outputArc();
    outarc.setExpression(outarc.new Expression() {
        @SuppressWarnings("unchecked")
		public Multiset evaluate() {
          final Multiset result = new Multiset();
          final List<Comparable> list = (List<Comparable>) (new ArrayList(getMultiset()));
          final Comparable t1 = list.get(0);
          final Comparable t2 = list.get(1);
          if (t1.compareTo(t2) > 0) {
            result.add(t1);
          } else {
            result.add(t2);
          }
          return result;
        }
      }); 

    transition().setAction(transition().new Action() {
        public void execute() {
          //System.out.print(".");
        }
      });
  }
  
  public MaxValueNet(final Multiset initialMarking) {
    this();
    place().addTokens(initialMarking);
  }

} // MaxValueNet
//////////////////// end of file ////////////////////
