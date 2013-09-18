//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

import gnu.lists.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import clojure.lang.Associative;
import clojure.lang.Binding;
import clojure.lang.Compiler;
import clojure.lang.Namespace;
import clojure.lang.PersistentHashMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

/**
* Represents a helper for Clojure-based inscriptions. Experimental support.
*
* <br><br>
* CpnClojureLanguage.java<br>
* Created: August 29, 2009 11:26:56 PM<br>
*
* @author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
* @version $version$ $Revision: 1.3 $
*/
public class CpnClojureLanguage extends CpnLanguage {

	private static CpnClojureLanguage INSTANCE = new CpnClojureLanguage();
	protected CpnClojureLanguage() {}
	
	public static CpnClojureLanguage getInstance () { return INSTANCE; }
		
	
	public String bodyTransitionGuard(String body) {	
		return
		"try { " + EOL +
		"      CpnContext c = getThisTransition().getContext(); " + EOL +
		"      java.util.Map m = c.getVarPool();  " + EOL +
		"	   return ((Boolean) CpnClojureLanguage.runClosureScript(m, \"" + NetGenerator.slashQuotes(body) + "\")).getBoolean(); " + EOL +
		"} catch(Throwable t) { t.printStackTrace(); } " + EOL +
		"return context.getBinding().keySet().size() > 0; " + EOL;
	}	
	
	public String bodyTransitionAction(String body) {	
		return
		"try { " + EOL +
		"      CpnContext c = getThisTransition().getContext(); " + EOL +
		"      java.util.Map m = c.getVarPool();  " + EOL +
		"	   CpnClojureLanguage.runClosureScript(m, \"" + NetGenerator.slashQuotes(body) + "\"); " + EOL +
		"} catch (Throwable t){ t.printStackTrace(); } " + EOL;
	}
	
	public String bodyInputArcGuard(String body) {
		return
		"try { " + EOL +
		"      CpnContext c = getThisArc().getContext(); " + EOL +
		"      java.util.Map m = c.getBinding();  " + EOL +
		"	   return ((Boolean)CpnClojureLanguage.runClosureScript(m, \"" + NetGenerator.slashQuotes(body) + "\")).booleanValue(); " + EOL +
		"} catch (Throwable t){ t.printStackTrace(); } " + EOL +
		"return true; ";
	}
	
	public String bodyInputArcExpression(String body) {
		return body;
	}
	
	public String bodyOutputArcExpression(String body) {
		// The result of running the script is converted to Multiset by default
		return 
		"try { " + EOL +
		"      CpnContext c = getThisArc().getContext(); " + EOL +
		"      java.util.Map m = c.getBinding();  " + EOL +
		"	   Object ret = CpnClojureLanguage.runClosureScript(m, \"" + NetGenerator.slashQuotes(body) + "\"); " + EOL +
		"      if (ret instanceof java.util.Collection) return new Multiset((java.util.Collection)ret); " + EOL +
		"	   return new Multiset(ret);" + EOL + 
		"} catch (Throwable t){ t.printStackTrace(); } " + EOL +
		"return new Multiset(); ";
	}
	
	final static public Object runClosureScript( 
			final Map<String, ?> b, 
			final String script) {
		try {
			new Binding<String>(script);
			final Namespace ns = (Namespace) RT.CURRENT_NS.get();
			Associative mappings = PersistentHashMap.EMPTY;
			mappings = mappings.assoc(RT.CURRENT_NS, RT.CURRENT_NS.get());
			if (b != null) {
				Iterator<String> iter = b.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next().toString();
					Symbol sym = Symbol.intern(key); 
					Var var = Var.intern(ns, sym); 
					Object value = b.get(key);
					mappings = mappings.assoc(var, value);
				}
			}
			Var.pushThreadBindings(mappings);		
			Object ret = Compiler.load(new StringReader(script)); 
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			Var.popThreadBindings();
		}
	}

	/**/
	public static final String EOL = System.getProperty("line.separator");

	
	
	
	/* Testing routine. */
	public static void main(String[] args) throws IOException {
		Map b = new HashMap();
		b.put("x", new Integer(20));
		b.put("y", new Integer(30));
		
		System.out.println(runClosureScript(b, "(+ x y)"));
		System.out.println(runClosureScript(b, "(< x 30)"));
		
		// Go into a loop so we can test some Clojure types
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = reader.readLine();
		while (line != null) {
			Object ret = runClosureScript(b, line);
			System.out.println("Result: " + ret);
			System.out.println("Type: " + ret.getClass());
			line = reader.readLine();
		}
	}
	
}
