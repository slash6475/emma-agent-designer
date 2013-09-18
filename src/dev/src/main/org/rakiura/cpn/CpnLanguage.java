// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Represents abstraction over the CPN Inscription Language.
 * This is a NetGenerator helper, that translates the user inscriptions 
 * into proper Java code that gets natively executed. By default, all inscriptions
 * must follow strict Java rules, and conform to the native JFern inscription 
 * conventions, Users can overwrite this class and plug in custom proprietary 
 * inscription languages. 
 *
 * <br><br>
 * CpnLanguage.java<br>
 * Created: May 20, 2007 9:46:31 PM<br>
 *
 * @author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 * @version $version$ $Revision: 1.2 $
 */
public class CpnLanguage {
	
	private static CpnLanguage INSTANCE = new CpnLanguage();
	protected CpnLanguage() {}
	
	
	public static CpnLanguage getInstance () { return INSTANCE; }
	
	public String bodyTransitionGuard(String body) {
		return body;
	}
	
	public String bodyTransitionAction(String body) {
		return body;
	}
	
	public String bodyInputArcGuard(String body) {
		return body;
	}
	
	public String bodyInputArcExpression(String body) {
		return body;
	}
	
	public String bodyOutputArcExpression(String body) {
		return body;
	}
	
}
