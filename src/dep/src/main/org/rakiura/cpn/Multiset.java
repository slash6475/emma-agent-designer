// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Represents a bag of tokens, a multiset. 
 * Actual implementation of a multiset, one of the basic
 * datastructures in JFern Petri nets. Used to store and manipulate
 * tokens. Tokens in JFern are any valid Java object, i.e. any 
 * Java object-type datastructure can be treated as a token, and can 
 * be inserted and removed from a multiset. 
 * 
 *<br><br>
 * Multiset.java<br>
 * Created: Mon Sep 25 10:51:00 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.16 $
 *@since 1.0
 */
public class Multiset extends AbstractCollection {

  /** Storage. */
  private List store = new ArrayList();

  /**
   * Creates empty multiset. */
  public Multiset () {/*default*/}

  /**
   * Creates new multiset initialized with a given single element.
   *@param aToken an initial element for this multiset.
   */
  public Multiset (final Object aToken) {
	  if (aToken instanceof Collection) {
		  this.store.addAll((Collection)aToken);
	  } else {
		  this.store.add(aToken);
	  }
  }

  /**
   * Creates new multiset initialized with elements from 
   * a given collection.
   *@param aCollection initial elements for this multiset.
   */
  public Multiset(final Collection aCollection) {
    this.store.addAll(aCollection);
  }

  /**
   * Adds a token to this multiset.
   * @param aToken token to be added.
   * @return <code>true</code> if the token has been succesfully added. 
   */
  public boolean add(final Object aToken) {
    return this.store.add(aToken);
  }

  /**
   * Removes the first occurance of the specified element 
   * from this multiset.
   *@param aToken token to be removed
   *@return <code>true</code> if the element was removed, 
   * <code>false</code> if the token was not present in this multiset.
   */
  public boolean remove(final Object aToken) {
    return this.store.remove(aToken);
  }

  /**
   * Counts the tokens in this multiset.
   *@return the number of tokens in this bag. 
   */
  public int size() {
    return this.store.size();
  }

  /**
   * Returns an iterator over elements of this bag. 
   *@return iterator over elements of this bag.
   */
  public Iterator iterator() {
    return this.store.iterator();
  }

  /**
   * Returns a random value from this multiset. 
   * This is a convenience method, useful to access 
   * a single token value from a multiset. If the multiset has
   * more than one token an arbitrary token is picked.
   *@return a randomly picked token.
   */
  public Object getAny() {
    if (this.store.size() > 0) {
      return this.store.get((int)(Math.random() * this.store.size()));
    }
	return null;
  }

  /**
   * Returns a random value from this multiset of a given type. 
   * This is a convenience method, useful to access 
   * a single token value from a multiset of a given type. 
   * If the multiset has more than one token of a given type, 
   * token is picked at random.
   *@param type the type of the token to be picked.
   *@return a randomly picked token.
   *@since 3.0
   */
  public Object getAny (final Class type) {
		final Iterator iter = iterator();
		while (iter.hasNext()) {
			final Object token = iter.next();
			if (type.isInstance(token)) return token;
		}
		return null;
  }

  /**
   * Returns <code>n</code> random tokens from this multiset. 
   * This is a convenience method, useful to access 
   * <code>n</code> tokens from a multiset. If there is not enough tokens
   * in this multiset to fulfill the request <code>null</code> will be
   * returned. 
   *@param aNumber a number of randomly selected tokens
   *@return a multiset with <code>n</code> randomly picked tokens, or
   * <code>null</code> if there is not enough tokens to fulfill the request. 
   */
  public Multiset getAny(final int aNumber) {
    if (aNumber > this.size()) return null;
    final List tmpList = new ArrayList(this);
    Collections.shuffle(tmpList);
    final Multiset result = new Multiset();
    for (int i = 0; i < aNumber; i++) {
      result.add(tmpList.get(i));
    }
    return result;
  }
  
  /**
   * Returns <code>n</code> random tokens from this multiset,
   * all of a specified type. This is a convenience method, useful 
   * to access <code>n</code> tokens from a multiset with a specified 
   * type. If the request cannot be fulfilled, <code>null</code> is being
   * returned. 
   *@param aNumber a number of randomly selected tokens
   *@param type the type pattern for the tokens.
   *@return a multiset with <code>n</code> randomly picked tokens all of a
   *  specified type, or <code>null</code> if the request cannot be fulfilled.
   *@see #getAny(Class)
   *@since 3.0
   */
  public Multiset getAny(final int aNumber, final Class type) {
  	if (aNumber > this.size()) return null; //not enough tokens
  	final Multiset result = new Multiset();
		final Iterator iter = iterator();
		int i = 0;
		while (i < aNumber && i < this.size() && iter.hasNext()) {
			final Object token = iter.next();
			if (type.isInstance(token)) {
				i++;
				result.add (token);	
			}
		}
		// if we found enough tokens of a given type
		if (result.size() == aNumber) return result;
		return null;
  }


} // Multiset
//////////////////// end of file ////////////////////
