
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for BasicSimulator.
 *
 *<br>
 * TestBasicSimulator.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.12 $
 */
public class TestBasicSimulator extends TestCase {

	Simulator sim;
	Net net;
	Place p1, p2;
	Transition t1;
	InputArc a1;
	OutputArc a2;


	/**/
	public TestBasicSimulator(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp() {
		this.net = new BasicNet();
		final Multiset m1 = new Multiset();
		m1.add(new Integer(1));
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
		this.sim = new BasicSimulator(this.net);
	}

	/**
	 */
	 public void testNetRun(){
		this.sim.run();
		assertEquals("Test of the a1 place size after fire()", 0, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, this.a2.getPlace().getTokens().size());
		}

	/**
	 */
	public void testNetStep(){
		boolean more = this.sim.step();
		assertEquals("Test of the a1 place size after fire()", 0, this.a1.getPlace().getTokens().size());
		assertEquals("Test of the a2 place size after fire()", 1, this.a2.getPlace().getTokens().size());
		assertTrue(!more);
	}

	/**
	 * Test suite for ObservableStore class. */
	public static Test suite() {
		return new TestSuite(TestBasicSimulator.class);
	}

} // TestBasicSimulator
//////////////////// end of file ////////////////////
