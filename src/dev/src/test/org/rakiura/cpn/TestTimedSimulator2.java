
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for TimedSimulator. This test creates a 
 * more complex net and tests it.
 *
 *<br>
 * TestTimedSimulator2.java<br>
 * <br>
 * Created: Wed Oct 29 14:12:11 2008<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.2 $
 */
public class TestTimedSimulator2 extends TestCase {

	TimedSimulator sim;
	Net net;
	Place p1, p2, p3, p4;
	Transition t1, t2, t3;
	InputArc in1, in2, in3;
	OutputArc o1, o2, o3;
	Object tok1, tok2;


	/**/
	public TestTimedSimulator2(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp() {
		this.net = new BasicNet();
		final Multiset m1 = new Multiset(); // p1 multiset
		final Multiset m2 = new Multiset(); // p2 multiset
		this.tok1 = new Integer(1);
		m1.add(tok1);
		this.tok2 = new Integer(2);
		m2.add(tok2);
		this.p1 = new Place(new Multiset(m1));
		this.p2 = new Place(new Multiset(m2));
		this.p3 = new Place();
		this.p4 = new Place();
		this.t1 = new Transition();
		this.t2 = new Transition();
		this.t3 = new Transition();
		
		this.in1 = new InputArc(this.p1, this.t1);
		this.in1.setExpression(this.in1.new Expression() {
				public void evaluate() {
					var(1);
				}
			});
		this.o1 = new OutputArc(this.t1, this.p3);
		this.o1.setExpression(this.o1.new Expression() {
				public Multiset evaluate() {
					return new Multiset(getMultiset().getAny());
				}
			});
		
		this.in2 = new InputArc(this.p2, this.t2);
		this.in2.setExpression(this.in2.new Expression() {
				public void evaluate() {
					var(1);
				}
			});
		this.o2 = new OutputArc(this.t2, this.p3);
		this.o2.setExpression(this.o2.new Expression() {
				public Multiset evaluate() {
					return new Multiset(getMultiset().getAny());
				}
			});
		
		this.in3 = new InputArc(this.p3, this.t3);
		this.in3.setExpression(this.in3.new Expression() {
				public void evaluate() {
					var(1);
				}
			});
		this.o3 = new OutputArc(this.t3, this.p4);
		this.o3.setExpression(this.o3.new Expression() {
				public Multiset evaluate() {
					return new Multiset(getMultiset().getAny());
				}
			});

		assertTrue(this.t1.isEnabled());
		assertTrue(this.t2.isEnabled());
		assertFalse(this.t3.isEnabled());
		assertEquals("Test of the p1 initial size", 1, this.in1.getPlace().getTokens().size());
		assertEquals("Test of the p2 initial size", 1, this.in2.getPlace().getTokens().size());
		assertEquals("Test of the p3 initial size", 0, this.in3.getPlace().getTokens().size());
		

		this.net.add(this.p1).add(this.p2).add(p3).add(p4);
		this.net.add(this.t1).add(this.t2).add(t3);
		this.sim = new TimedSimulator(this.net);
		this.sim.setTimestamp(this.tok1, 10);
		this.sim.setTimestamp(this.tok2, 50);
		assertEquals("Test of current clock", 0, this.sim.getTime());
	}

	/**
	 */
	public void testNetStep() {
		assertEquals("tok1 has timestamp 10 ", 10, this.sim.getTimestamp(this.tok1));
		assertEquals("tok2 has timestamp 50 ", 50, this.sim.getTimestamp(this.tok2));
		
		assertEquals("Start clock is 0", 0, this.sim.getTime());
		// Initially tokens in the network are in p1 and p2
		assertEquals("Test of p1 size before fire()", 1, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size before fire()", 1, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size before fire()", 0, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size before fire()", 0, this.o3.getPlace().getTokens().size());
		
		// let's go
		boolean more = this.sim.step();
		assertEquals("Test of p1 size after fire()", 0, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size after fire()", 1, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size after fire()", 1, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size after fire()", 0, this.o3.getPlace().getTokens().size());
		assertEquals("Clock after 1 step is still 10", 10, this.sim.getTime());
		
		assertTrue("We have 3 more firing to do...", more);
		
		more = this.sim.step();
		assertEquals("Test of the p1 size after fire()", 0, this.in1.getPlace().getTokens().size());
		assertEquals("Test of the p2 size after fire()", 1, this.in2.getPlace().getTokens().size());
		assertEquals("Test of the p3 size after fire()", 0, this.in3.getPlace().getTokens().size());
		assertEquals("Test of the p4 size after fire()", 1, this.o3.getPlace().getTokens().size());
				
		assertTrue("We have 2 more firing to do...", more);
		assertEquals("Clock after 2 steps is 50", 50, this.sim.getTime());
		
		more = this.sim.step();
		assertEquals("Test of p1 size after fire()", 0, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size after fire()", 0, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size after fire()", 1, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size after fire()", 1, this.o3.getPlace().getTokens().size());
		
		assertTrue("We have 1 more firing to do...", more);
		assertEquals("Clock after 3 steps is 50", 50, this.sim.getTime());
		
		more = this.sim.step();
		assertEquals("Test of p1 size after fire()", 0, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size after fire()", 0, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size after fire()", 0, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size after fire()", 2, this.o3.getPlace().getTokens().size());
		assertEquals(50, this.sim.getTime()); //current simulated time is 50
		assertTrue(!more); // we have no more enabled transitions
	}

	/**
	 */
	public void testNetRun() {
		assertEquals("tok1 has timestamp 10 ", 10, this.sim.getTimestamp(this.tok1));
		assertEquals("tok2 has timestamp 50 ", 50, this.sim.getTimestamp(this.tok2));

		assertEquals("Start clock is 0", 0, this.sim.getTime());
		// Initially tokens in the network are in p1 and p2
		assertEquals("Test of p1 size before run()", 1, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size before run()", 1, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size before run()", 0, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size before run()", 0, this.o3.getPlace().getTokens().size());

		this.sim.run();
		assertEquals("Test of p1 size after run()", 0, this.in1.getPlace().getTokens().size());
		assertEquals("Test of p2 size after run()", 0, this.in2.getPlace().getTokens().size());
		assertEquals("Test of p3 size after run()", 0, this.in3.getPlace().getTokens().size());
		assertEquals("Test of p4 size after run()", 2, this.o3.getPlace().getTokens().size());

		assertEquals("Test of p4 size after run()", 2, this.o3.getPlace().getTokens().size());

		assertEquals("The clock after execution is 50", 50, this.sim.getTime());
	}
	
	/**
	 * Test suite for ObservableStore class. */
	public static Test suite() {
		return new TestSuite(TestTimedSimulator2.class);
	}

} // TestTimedSimulator
//////////////////// end of file ////////////////////
