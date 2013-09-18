
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test utility for BasicTransition class test suite.
 *
 *<br>
 * TestBasicTransition.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.12 $
 */
public class TestBasicTransition extends TestCase {

	Transition transition;

	/**/
	public TestBasicTransition(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp(){
		this.transition = new Transition();
	}

	/**
	 */
	public void testTransitionOperations(){
		Marking mark = new Marking();
		final Multiset m1 = new Multiset();
		m1.add(new Integer(1));
		final Place p1 = new Place(new Multiset(m1));
		final Place p2 = new Place();
		final Transition t1 = new Transition();
		final InputArc a1 = new InputArc(p1, t1);
		a1.setExpression(a1.new Expression() {
			public void evaluate() {
				var(1);
			}
		});
		final OutputArc a2 = new OutputArc(t1, p2);
		a2.setExpression(a2.new Expression() {
				public Multiset evaluate() {
					return new Multiset(getMultiset().getAny());
				}
			});

		t1.setGuard(t1.new Guard() {
				public boolean evaluate() {
					return t1.inputArcs().size() == 1;
				}
			});

		mark.put(p1, new Multiset(m1));
		mark.put(p2, new Multiset(m1));
		assertTrue(t1.isEnabled());
		assertEquals("Test of the a1 place initial size", 1, a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place initial size", 0, a2.getPlace().getTokens().size());

		t1.fire();

		assertEquals("Test of the a1 place size after fire()", 0, a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, a2.getPlace().getTokens().size());
	}

	/**
	 * Test suite. */
	public static Test suite() {
		return new TestSuite(TestBasicTransition.class);
	}

} // TestBasicTransition
//////////////////// end of file ////////////////////
