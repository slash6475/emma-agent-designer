// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Represents an input Arc in JFern Petri Net model.
 *
 *<br><br>
 * InputArc.java<br>
 * Created: Mon Sep 25 11:49:12 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.27 $
 *@since 1.0
 */
public class InputArc extends AbstractArc {

	private static final long serialVersionUID = 3256443594901368887L;

/** Default guard. */
  private Guard guard = new Guard() {
      public boolean evaluate() {
        Multiset multiset = getMultiset();
        return (multiset.size() > 0);
      }
    };

  /** Default expression. */
  private Expression expr = new Expression() {
      public void evaluate() { var(1); }
  };

  /**/
  private String expressionText = "var(1);";
  /**/
  private String guardText = "return (getMultiset().size() > 0);";


  /**/
  protected InputArc() {/*default*/}

  /**/
  public InputArc(final Place aFrom, final Transition aTo) {
    super(aFrom, aTo);
    aTo.addInput(this);
    aFrom.addInput(this);
  }


  public void release () {
	getPlace().removeInput (this);
	getTransition().removeInput (this);
  }


  /**
   * Checks if this arc is enabled.
   *@return this call evaluates the guard, and returns
   *  <code>true</code> if the arc is enabled,
   *  <code>false</code> otherwise.
   */
  public boolean guard() {
    return this.guard.evaluate();
  }

  /**
   * Evaluates this arc expression. The actual result of this
   * expression evaluation is accessible from the context. The
   * arc expression is evaluated many times during net simulation,
   * thus should not have any side effects, apart from selecting
   * tokens via {@link org.rakiura.cpn.Context} methods.
   */
  public void expression() {
    this.expr.evaluate();
  }

  /**
   * Sets a guard for this arc.
   *@param aGuard a guard.
   */
  public void setGuard(final Guard aGuard) {
    this.guard = aGuard;
  }

  /**
   * Sets the multiset expression.
   *@param anExpr an expression
   */
  public void setExpression(final Expression anExpr){
    this.expr = anExpr;

  }
  /**
   * Sets the guard text.
   *@param aText guard text.
   */
  public void setGuardText(final String aText) {
    this.guardText = aText;
  }

  /**
   * Returns the guard text.
   *@return this transition guard text.
   */
  public String getGuardText() {
    return this.guardText;
  }

  /**
   * Sets the expression text.
   *@param aText expression text.
   */
  public void setExpressionText(final String aText) {
    this.expressionText = aText;
  }

  /**
   * Returns the expression text.
   *@return this arc expression text.
   */
  public String getExpressionText() {
    return this.expressionText;
  }

  /**
   * Visitor pattern.
   * @param aVisitor net visitor handle. 
   * @return this input arc.
   */
  public NetElement apply(NetVisitor aVisitor) {
    aVisitor.inputArc(this);
    return this;
  }

  	
  	public Arc getThis() {
  		return this;
  	}
  	
	/**
	 * Cloning.
	 * @return the cloned copy of this input arc.
	 */
	public Object clone() {
		return (InputArc) super.clone();
	}

	public String toString () {
		return "InputArc: " + this.getPlace().getName()+ " � " + this.getTransition().getName();
	}

  /**
   * Represents an input arc multiset expression.
   *
   *
   *@author  <a href="mariusz@rakiura.org">Mariusz</a>
   *@version 4.0.0 $Revision: 1.27 $ $Date: 2007/05/20 21:47:42 $
   *@since 2.0
   */
  public abstract class Expression implements Context {

    /**
     * Evaluates this expression. This method implements the
     * actual expression on the input arc, which given
     * a  multiset from an input place evaluates to a set
     * of tokens (a single multiset). Picking up and binding
     * tokens is achieved via the var() methods.
     */
    public abstract void evaluate();


    public void var(final String aVariable) {
      getContext().var(aVariable);
    }
    public void var(final int aNumber) {
      getContext().var(aNumber);
    }
		public void var(final String aVariable, Class type) {
		  getContext().var(aVariable, type);
		}
		public void var(final int aNumber, Class type) {
		  getContext().var(aNumber, type);
		}
    public Object get(final String aVariable) {
      return getContext().get(aVariable);
    }
    public Multiset getMultiset() {
      return getContext().getMultiset();
    }
  } // Expression



  /**
   * Represents a generic input arc guard.
   * Note, in JFern, due to pure Java based inscription
   * language, all the partial matches must be done
   * explicitely via input arc guards. In such a guard
   * no references other to variables from this arc
   * should be made.
   *
   *
   *@author  <a href="mariusz@rakiura.org">Mariusz</a>
   *@version 4.0.0 $Revision: 1.27 $
   *@since 2.0
   */
  public abstract class Guard implements Context {

    /**
     * Guard function.
     *@return <code>true</code> if this guard evaluates
     * to enabled transition/arc;
     * <code>false</code> otherwise.
     */
    public abstract boolean evaluate();


    public void var(final String aVariable) {
      getContext().var(aVariable);
    }
    public void var(final int aNumber) {
      getContext().var(aNumber);
    }
		public void var(final String aVariable, final Class type) {
		  getContext().var(aVariable, type);
		}
		public void var(final int aNumber, final Class type) {
	  	getContext().var(aNumber, type);
		}

    public Object get(final String aVariable) {
      return getContext().get(aVariable);
    }
    public Multiset getMultiset() {
      return getContext().getMultiset();
    }
    public Arc getThisArc() {
    	return getThis();
    }

  } // Guard


} // InputArc
//////////////////// end of file ////////////////////
