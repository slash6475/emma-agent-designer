
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for BasicNet class test suite.
 *
 *<br>
 * TestBasicNet.java<br><br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.15 $
 */
public class TestBasicNet extends TestCase {

	Net subnet;
	Net net;
	Place p1, p2; // subnet places
	Transition t1;
	InputArc a1;
	OutputArc a2;


	/**/
	public TestBasicNet(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp(){
		this.net = new BasicNet ();
		this.subnet = new BasicNet();
		this.subnet.setName ("subnetA");
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

		this.subnet.add(this.p1).add(this.p2).add(this.t1);
		this.net.add (this.subnet);

		assertEquals ("test of subnet transition size", 1, this.subnet.getAllTransitions().length);
		assertEquals ("test of subnet places size", 2, this.subnet.getAllPlaces().length);
		assertEquals ("test of subnet all net elements size", 3, this.subnet.getNetElements().length);

		assertEquals ("test of net transition size", 1, this.net.getAllTransitions().length);
		assertEquals ("test of net places size", 2, this.net.getAllPlaces().length);
		assertEquals ("test of net all net elements size", 1, this.net.getNetElements().length);

		assertSame ("test forName on subnet", this.subnet, this.net.forName(this.subnet.getName ()));
		assertSame ("test forID on subnet", this.subnet, this.net.forID(this.subnet.getID ()));
	}

	/**
	 */
	public void testNodesOperations(){
		assertEquals("Test of allTransitions()", 1, this.subnet.getAllTransitions().length);
		assertEquals("Test of allPlaces()", 2, this.subnet.getAllPlaces().length);

		assertSame("Test1 of forID", this.p1, this.net.forID(this.p1.getID()));
		assertSame("Test1 of forName", this.p1, this.net.forName(this.p1.getName()));

		assertSame("Test2 of forID", this.p2, this.net.forID(this.p2.getID()));
		assertSame("Test2 of forName", this.p2, this.net.forName(this.p2.getName()));

		assertSame("Test3 of forID", this.t1, this.net.forID(this.t1.getID()));
		assertSame("Test3 of forName", this.t1, this.net.forName(this.t1.getName()));
	}



	/**
	 * Test suite. */
	public static Test suite() {
		return new TestSuite(TestBasicNet.class);
	}

} // TestBasicNet
//////////////////// end of file ////////////////////
