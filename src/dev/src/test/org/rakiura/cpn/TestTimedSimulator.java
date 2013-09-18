
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for TimedSimulator.
 *
 *<br>
 * TestBasicSimulator.java<br>
 * <br>
 * Created: Thu Oct 03 17:12:11 2002<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.8 $
 */
public class TestTimedSimulator extends TestCase {

	TimedSimulator sim;
	Net net;
	Place p1, p2;
	Transition t1;
	InputArc a1;
	OutputArc a2;


	/**/
	public TestTimedSimulator(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp() {
		this.net = new BasicNet();
		final Multiset m1 = new Multiset();
		Integer token = new Integer(1);
		m1.add(token);
		this.p1 = new Place(new Multiset(m1));
		this.p2 = new Place();
		this.t1 = new Transition();
		this.a1 = new InputArc(this.p1, this.t1);
		this.a1.setExpression(this.a1.new Expression() {
				public void evaluate() {
					var(1);
				}
			});
		this.a2 = new OutputArc(this.t1, this.p2);
		this.a2.setExpression(this.a2.new Expression() {
				public Multiset evaluate() {
					return new Multiset(getMultiset().getAny());
				}
			});


		Marking mark = new Marking();
		mark.put(this.p1, new Multiset(m1));
		mark.put(this.p2, new Multiset(m1));
		assertTrue(this.t1.isEnabled());
		assertEquals("Test of the a1 place initial size", 1, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place initial size", 0, this.a2.getPlace().getTokens().size());

		this.net.add(this.p1).add(this.p2).add(this.t1);
		this.sim = new TimedSimulator(this.net);
		this.sim.setTimestamp(token, 10);
		assertEquals("Test of current clock", 0, this.sim.getTime());
	}

	/**
	 */
	 public void testNetRun(){
		this.sim.run();
		assertEquals("Test of the a1 place size after fire()", 0, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, this.a2.getPlace().getTokens().size());

		assertEquals("Test of the clock after execution", 10, this.sim.getTime());
	 }

	/**
	 */
	public void testNetStep() {

		assertEquals("Test of the clock after execution", 0, this.sim.getTime());
		boolean more = this.sim.step();
		assertEquals("Test of the a1 place size after fire()", 0, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, this.a2.getPlace().getTokens().size());
		assertTrue(!more);
		assertEquals("Test of the clock after execution", 10, this.sim.getTime());

		more = this.sim.step();
		assertEquals("Test of the a1 place size after fire()", 0, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, this.a2.getPlace().getTokens().size());
		assertTrue(!more);
		assertEquals("Test of the clock after execution", 10, this.sim.getTime());
	}

	/**
	 * Test suite for ObservableStore class. */
	public static Test suite() {
		return new TestSuite(TestTimedSimulator.class);
	}

} // TestTimedSimulator
//////////////////// end of file ////////////////////
