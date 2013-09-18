// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents a CPN context.
 * Implements a basic context for inscription evaluations.
 * Currently, the context can be accessed from any CPN net element,
 * and provides basic mechanisms for obtaining token references,
 * manipulating tokens in expressions and guards, unification
 * mechanism, and variable handling.
 *
 *<br><br>
 * CpnContext.java<br>
 * Created: Fri Apr 19 11:23:01 2002<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.22 $
 *@since 2.0
 */
public class CpnContext implements Context {

	/** Parent context. */
	private CpnContext parent = null;

	/**/
	private Multiset multiset;

	/** Set of all declared variables. */
	private Set variables = new HashSet ();
	/** Colors map for variables. */
	private Map colorsMap = new HashMap ();

	/** Mapping between variable name and its values. */
	private Map varPool = null;

	/** Anonymous variables. */
	private int anonymousVariables = 0;
	private Map anonColors = new HashMap ();

	/**
	 * Stores the mapping for this context. Used by transtion firing
	 * machinery.
	 */
	private Map binding;



	/**
	 * Creates an empty top level context.
	 */
	public CpnContext() {
		this.varPool = new HashMap();
	}

	/**
	 * Creates a child context with a given parent context.
	 * @param aParent parent context.
	 */
	public CpnContext(final CpnContext aParent) {
		this.parent = aParent;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Multiset getMultiset() {
		return new Multiset (this.multiset);
	}

	/**
	 * Sets the input multiset to the given value. This method also resets
	 * the local variables pool for this context, so it can be reused as
	 * refresh method.
	 *@param aMultiset a multiset.
	 */
	public void setMultiset(final Multiset aMultiset) {
		this.multiset = aMultiset;
		this.anonymousVariables = 0;
	}


	/**
	 * {@inheritDoc}
	 * @param aVariable {@inheritDoc}
	 */
	public void var(final String aVariable) {
		if (this.parent != null) {
			this.parent.var(aVariable);
		}
		this.variables.add(aVariable);
	}

	/**
	 * {@inheritDoc}
	 * @param aNumber {@inheritDoc}
	 */
	public void var (final int aNumber) {
		this.anonymousVariables += aNumber;
	}

	/**
	 * Method not implemented yet.
	 * TODO: Do implement it now!
	 * @param aVariable a variable
	 * @param aType not used yet
	 */
	public void var (final String aVariable, final Class aType) {
		if (this.parent != null) {
			this.parent.var(aVariable, aType);
		}
		this.variables.add(aVariable);
		this.colorsMap.put (aVariable, aType);
		throw new RuntimeException("var(String, class) not implemented yet");
	}

	/**
	 * Method is not yet implemented. 
	 * TODO: Do implement it now!
	 * @param aNumber not implemented yet
	 * @param aType not implemented yet
	 */
	public void var(int aNumber, Class aType) {
		this.anonColors.put (aType, new Integer(aNumber));
		throw new RuntimeException("var(int, class) not implemented yet");
	}


	/**
	 * Returns all declared variables in this context.
	 *@return variables declared in this context
	 */
	public Set variables() {
		return this.variables;
	}

	/**
	 * {@inheritDoc}
	 * @param aVariable {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public Object get(final String aVariable) {
		if (this.parent != null) {
			return this.parent.get(aVariable);
		}
		return this.varPool.get(aVariable);
	}

	/**
	 * Returns the varPool of this context.
	 *@return the variables pool map.
	 */
	public Map getVarPool() {
		return this.varPool;
	}

	public List getPossibleBindings() {
		final Object[] values = this.getMultiset().toArray();
		final String[] vars = (String[]) this.variables
			.toArray(new String[this.variables.size()]);
		if (values.length == 0
				|| (vars.length == 0 && this.anonymousVariables == 0)
				|| (values.length < vars.length + this.anonymousVariables)) {
			return new ArrayList();
		}
		if (vars.length == 0) {
			final Map m = new HashMap();
			final List pr = new ArrayList(this.multiset.getAny(this.anonymousVariables));
			for (int i = 0; i < this.anonymousVariables; i++) {
				m.put("__anonVar_" + getUniqueString(), pr.get(i));
			}
			final List result = new ArrayList();
			result.add(m);
			return result;
		}
		final List list = new ArrayList();
		kpermutations(vars, values, this.anonymousVariables, list);
		return list;
	}


	public void setBinding(Map aBinding) {
		this.binding = aBinding;
	}

	public Map getBinding() {
		return this.binding;
	}



	private static long COUNTER = 0;

	/**
	 * Generates a new unique identifier. 
	 * @return unique string. */
	private final static String getUniqueString() {
		return "" + System.currentTimeMillis() + COUNTER++;
	}


	/**
	 * The implementation to calculate kpermutations is based on
	 * final static private methods for better performance.
	 * @param variables 
	 * @param values 
	 * @param anonymousVariables 
	 * @param result 
	 */
	private static final void kpermutations(final String[] variables,
		final Object[] values,
		final int anonymousVariables,
		final List result) {
		permutation(combination(values.length, variables.length),
								variables,
								values,
								anonymousVariables,
								result);
	}



	/**
	 * Calculates a factorial of given integer.
	 * @param from lower limit
	 * @param to upper limit
	 * @return return partial factorial value
	 */
	private static final long factorial(long from, long to) {
		if (from == to) return to;
		return (from * factorial(from - 1, to));
	}

	/**
	 * Private  function used for generating combination states.
	 * @param state 
	 * @param max 
	 */
	private static final void check_state(int[] state, int max) {
		int imax = max, isize = state.length-1;
		while (isize > 0) {
			if(state[isize] == imax) {
				state[isize-1]++;
				int i = isize;
				while (i < state.length) {
					state[i] = state[i-1]+1;
					i++;
				}
			}
			imax--;
			isize--;
		}
	}


	/**
	 * Function generating all possible combinations with permutations
	 * from a sequence of numbers 0&hellip;(size-1).
	 * @param size max value, number of elements
	 * @param howmany how many elements we are selecting
	 * @return an array with all possible combinations without repeating
	 *  result_array[result_size][howmany]
	 */
	private static final int[][] combination(int size, int howmany) {
		final long long_result_size = factorial(size, size - howmany + 1) / factorial(howmany, 1);
		final int result_size = (int) long_result_size;
		if (long_result_size != result_size)
			throw new RuntimeException("Limit of combinations too big to fit into memory");

		final int[][] result = new int[result_size][howmany];
		int[] current_state = new int[howmany];

		for(int i = 0; i < howmany; i++) {
			current_state[i] = i;
		}

		for(int i1 = 0; i1 < result_size; i1++) {
			for(int i2 = 0; i2 < howmany; i2++) {
				result[i1][i2] = current_state[i2];
			}
			current_state[howmany-1]++;
			check_state(current_state, size);
		}

		return result;
	}

	private static final void permutation(final int[][] s,
																				final String[] variables,
																				final Object[] values,
																				final int anonymousVariables,
																				final List result) {
		for (int i = 0; i < s.length; i++) {
			rpermute(s[i], 0, variables, values, anonymousVariables, result);
		}
	}

	private static final void rpermute(final int[] c, final int k,
																		 final String[] variables,
																		 final Object[] values,
																		 final int anonymousVariables,
																		 final List result) {
		if (k == c.length - 1) {
			final Map m = new HashMap();
			for (int i = 0; i < c.length; i++) {
				m.put(variables[i], values[c[i]]);
			}
			if (anonymousVariables > 0) {
				List pr = java.util.Arrays.asList(values);
				pr.retainAll(m.values());
				for (int i = 0; i < anonymousVariables; i++) {
					m.put("__anonVar_" + i, pr.get(i));
				}
			}
			result.add(m);
		} else {
			int temp;
			for (int i = k; i < c.length; i++) {
				temp = c [i];
				c [i] = c [k];
				c [k] = temp;
				rpermute(c.clone(), k + 1, variables, values, anonymousVariables, result);
			} // for
		} // else
	}





	/** 
	 * Test. 
	 * @param args not used. 
	 * */
	public static void main(String[] args) {
		System.out.println("Test for variables: X,Y and values 1,2,3");
		String[] variables1 = new String[] { "X", "Y" };
		Object[] values1 = new String[] { "1", "2", "3" };
		List list1 = new ArrayList();
		kpermutations(variables1, values1, 0, list1);
		System.out.println(list1 + "\n\n");

		list1.clear();
		System.out.println("Test for variables: X,Y,Z and values 1,2,3,4,5");
		String[] variables = new String[] { "X", "Y", "Z" };
		Object[] values = new String[] { "1", "2", "3", "4", "5" };
		kpermutations(variables, values, 0, list1);
		System.out.println(list1 + "\n\n");

		for (int i = 0; i < list1.size(); i++) {
			Map m = (Map) list1.get(i);
			System.out.println("" + m.get("X") + m.get("Y") + m.get("Z"));
		}
	}


} // CpnContext
//////////////////// end of file ////////////////////