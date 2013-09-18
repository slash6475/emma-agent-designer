// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**
 * Represents a helper for BeanShell based inscriptions. Experimental support.
 *
 * <br><br>
 * CpnBshLanguage.java<br>
 * Created: May 20, 2007 10:20:56 PM<br>
 *
 * @author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 * @version $version$ $Revision: 1.2 $
 */
public class CpnBshLanguage extends CpnLanguage {

	private static CpnBshLanguage INSTANCE = new CpnBshLanguage();
	protected CpnBshLanguage() {}
	
	
	public static CpnBshLanguage getInstance () { return INSTANCE; }
	
	
	
	public String bodyTransitionGuard(String body) {
		return BSH_TRANSITION_INIT + EOL +
		"try { " + EOL +
		"    return ((Boolean)i.eval(\"" + NetGenerator.slashQuotes(body) + "\")).booleanValue(); " + EOL  +
		"} catch (Exception e) { e.printStackTrace (); } " + EOL +
		"return context.getBinding().keySet().size() > 0; " + EOL;

	}
	
	
	
	public String bodyTransitionAction(String body) {
		return BSH_TRANSITION_INIT + EOL +		
		"try { " + EOL +
		"  i.eval(\"" + NetGenerator.slashQuotes(body) + "\"); " + EOL + 
		"} catch (Exception e) { e.printStackTrace (); } " + EOL;
	}
	
	public String bodyInputArcGuard(String body) {
		return BSH_ARC_INIT + EOL +
		"try { " + EOL +
		"    return ((Boolean)i.eval(\"" + NetGenerator.slashQuotes(body) + "\")).booleanValue(); " + EOL  +
		"} catch (Exception e) { e.printStackTrace (); } " + EOL +
		"return true; " + EOL;
	}
	
	public String bodyInputArcExpression(String body) {
		return body;
	}
	
	public String bodyOutputArcExpression(String body) {
		return BSH_ARC_INIT + EOL +
		"try { " + EOL +
		"    Object ret = i.eval(\"" + NetGenerator.slashQuotes(body) + "\"); " + EOL +
		"    if (ret instanceof Multiset) return (Multiset)ret; " + EOL +
		"    else if (ret instanceof java.util.Collection) return new Multiset((java.util.Collection)ret); " + EOL +
		"    else return new Multiset(ret); " + EOL + 
		"} catch (Exception e) { e.printStackTrace (); } " + EOL +
		"return new Multiset(); " + EOL;
	}
	
	/**/
	public static final String EOL = System.getProperty("line.separator");
	
	
	private static String BSH_ARC_INIT =  		
		"  bsh.Interpreter i = null; " + EOL +
		"  try { " + EOL +
		"      i = new bsh.Interpreter(); " + EOL +
		"      i.eval(\"import org.rakiura.cpn.Multiset;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Place;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Transition;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.BasicNet;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.CpnContext;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Marking;\"); " + EOL + 
		"      CpnContext c = getThisArc().getContext(); " + EOL +
		"      i.set(\"context\", c);  " + EOL +
		"      java.util.Iterator iter = c.getBinding().keySet().iterator(); " + EOL +
		"      while (iter.hasNext()) { " + EOL +
		"         String key = iter.next().toString(); " + EOL +
		"         Object value = c.get(key); " + EOL +
		"         i.set(key, value); " + EOL + //DEBUG "System.out.println(\"Adding \" + key + \"  --> \" + value );" +  
		"      } " + EOL +
		"  } catch (Exception e) { e.printStackTrace(); } " + EOL;

	private static String BSH_TRANSITION_INIT =  
		"  bsh.Interpreter i = new bsh.Interpreter(); " + EOL +
		"  CpnContext c = getThisTransition().getContext(); " + EOL +
		"  try { " + EOL +
		"      i.eval(\"import org.rakiura.cpn.Multiset;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Place;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Transition;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.BasicNet;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.CpnContext;\"); " + EOL + 
		"      i.eval(\"import org.rakiura.cpn.Marking;\"); " + EOL + 
		"      i.set(\"context\", c); " + EOL +
		"      java.util.Iterator iter = c.getVarPool().keySet().iterator(); " + EOL +
		"      while (iter.hasNext()) { " + EOL +
		"         String key = iter.next().toString(); " + EOL +
		"         Object value = c.get(key); " + EOL +
		"         i.set(key, value); " + EOL +
		"      } " + EOL +
		"  } catch (Exception e) { e.printStackTrace(); } " + EOL;
}
