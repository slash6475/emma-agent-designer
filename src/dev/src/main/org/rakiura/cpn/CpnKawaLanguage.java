//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;


/**
* Represents a helper for Kawa/Scheme based inscriptions. Experimental support.
*
* <br><br>
* CpnSchemeLanguage.java<br>
* Created: May 30, 2007 11:20:56 PM<br>
*
* @author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
* @version $version$ $Revision: 1.1 $
*/
public class CpnKawaLanguage extends CpnLanguage {

	private static CpnKawaLanguage INSTANCE = new CpnKawaLanguage();
	protected CpnKawaLanguage() {}
	
	
	public static CpnKawaLanguage getInstance () { return INSTANCE; }
	
	
	// Static registration of environment and init.scm
	static {
		kawa.standard.Scheme.registerEnvironment();
		kawa.standard.Scheme.eval("(load \"init.scm\")", gnu.mapping.Environment.getGlobal());
	}
	
	
	public String bodyTransitionGuard(String body) {
		return SCHEME_TRANSITION_INIT + EOL +
		"try { " + EOL +
		"    return ((Boolean)kawa.standard.Scheme.eval(\"" + NetGenerator.slashQuotes(body) + "\", e)).booleanValue(); " + EOL  +
		"} catch (Throwable t) { t.printStackTrace (); } " + EOL +
		"return context.getBinding().keySet().size() > 0; " + EOL;
	}
	
	
	
	public String bodyTransitionAction(String body) {
		return SCHEME_TRANSITION_INIT + EOL +		
		"try { " + EOL +
		"  kawa.standard.Scheme.eval(\"" + NetGenerator.slashQuotes(body) + "\", e); " + EOL + 
		"} catch (Throwable t) { t.printStackTrace (); } " + EOL;
	}
	
	public String bodyInputArcGuard(String body) {
		return SCHEME_ARC_INIT + EOL +
		"try { " + EOL +
		"    return ((Boolean)kawa.standard.Scheme.eval(\"" + NetGenerator.slashQuotes(body) + "\", e)).booleanValue(); " + EOL  +
		"} catch (Throwable t) { t.printStackTrace (); } " + EOL +
		"return true; " + EOL;
	}
	
	public String bodyInputArcExpression(String body) {
		return body;
	}
	
	public String bodyOutputArcExpression(String body) {
		return SCHEME_ARC_INIT + EOL +
		"try { " + EOL +
		"    Object ret = kawa.standard.Scheme.eval(\"" + NetGenerator.slashQuotes(body) + "\", e); " + EOL +
		"    if (ret instanceof Multiset) return (Multiset)ret; " + EOL +
		"    else if (ret instanceof java.util.Collection) return new Multiset((java.util.Collection)ret); " + EOL +
		"    else return new Multiset(ret); " + EOL + 
		"} catch (Throwable t) { t.printStackTrace (); } " + EOL +
		"return new Multiset(); " + EOL;
	}
	
	/**/
	public static final String EOL = System.getProperty("line.separator");
	
	
	private static String SCHEME_ARC_INIT =  
//		"  kawa.standard.Scheme.registerEnvironment();" + EOL +
		"  gnu.mapping.Environment e = gnu.mapping.Environment.make(\"jfern\", gnu.mapping.Environment.getGlobal()); " + EOL +
		"  try { " + EOL +
		"      CpnContext c = getThisArc().getContext(); " + EOL +
		"      java.util.Iterator iter = c.getBinding().keySet().iterator(); " + EOL +
		"      while (iter.hasNext()) { " + EOL +
		"         String key = iter.next().toString(); " + EOL +
		"         Object value = c.get(key); " + EOL +
		"         e.put(key, value); " + EOL + //DEBUG "System.out.println(\"Adding \" + key + \"  --> \" + value );" +  
		"      } " + EOL +
		"  } catch (Throwable te) { te.printStackTrace(); } " + EOL;

	private static String SCHEME_TRANSITION_INIT =
//		"  kawa.standard.Scheme.registerEnvironment();" + EOL +
		"  gnu.mapping.Environment e = gnu.mapping.Environment.make(\"jfern\", gnu.mapping.Environment.getGlobal()); " + EOL +
		"  CpnContext c = getThisTransition().getContext(); " + EOL +
		"  try { " + EOL +
		"      java.util.Iterator iter = c.getVarPool().keySet().iterator(); " + EOL +
		"      while (iter.hasNext()) { " + EOL +
		"         String key = iter.next().toString(); " + EOL +
		"         Object value = c.get(key); " + EOL +
		"         e.put(key, value); " + EOL +
		"      } " + EOL +
		"  } catch (Throwable te) { te.printStackTrace(); } " + EOL;
}
