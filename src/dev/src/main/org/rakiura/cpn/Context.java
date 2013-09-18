
// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/

/**
 * Represents public access protocol to CpnContexts.
 *
 *<br><br>
 * Context.java<br>
 * Created: Tue Apr 23 22:24:35 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz</a>
 *@version 4.0.0 $Revision: 1.7 $ $Date: 2003/09/17 00:33:53 $
 *@since 2.0
 */
public interface Context {

	/**
	 * Declares new variable in this context.
	 *@param aVariable new declared variable.
	 */
	void var(final String aVariable);

	/**
	 * Declares <code>n</code> new anonymous variables in this context.
	 * Anonymous variables cannot be referenced directly, they are picked
	 * at random from the input multiset. You should use anonymous variables
	 * in all inscriptions where any token can be taken from a place, as it
	 * highly improves the overall performance of the unification mechanisms
	 * of JFern engine.
	 *@param aNumber <code>n</code>, number of anonymously
	 *  declared variables.
	 */
	void var(final int aNumber);


	/**
	 * Declares new variable in this context. Only tokens derived of
	 * the provided type will be matched and bound to this variable.
	 *@param aVariable new declared variable.
	 *@param type the type of the variable.
	 **@since 3.0
	 */
	void var(final String aVariable, Class type);

	/**
	 * Declares new <code>n</code>anonymous
	 * variables in this context.  Anonymous variables cannot
	 * be referenced directly, and are picked at random from the
	 * input multiset. Only tokens derived of the provided type
	 * will be matched and bound to these anonymous variables.
	 *@see #var(int)
	 *@param aNumber <code>n</code>, number of anonymously
	 *  declared variables.
	 *@param type the type for matching and bounding.
	 *@since 3.0
	 */
	void var(final int aNumber, Class type);



	/**
	 * Gets the variable value.
	 *@param aVariable the variable name.
	 *@return the value of the given variable in this context.
	 */
	Object get(final String aVariable);

	/**
	 * Gets the input multiset. Note, the default implementation
	 * returns the safe copy of the actual multiset, therefore there is no need
	 * to create a copy within the expression itself. This is just a simple shortcut
	 * to ease the arc expressions.
	 *@return input multiset. For transitions, this input multiset is
	 * a union of all input arc expressions multiset.
	 */
	Multiset getMultiset();


} // Context
//////////////////// end of file ////////////////////
