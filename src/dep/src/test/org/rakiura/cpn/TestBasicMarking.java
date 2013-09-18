
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for BasicMarking class test suite.
 *
 *<br>
 * TestBasicMarking.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.8 $
 */
public class TestBasicMarking extends TestCase {

	/**/
	private Marking mark;

	/**/
	public TestBasicMarking(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp(){
		this.mark = new Marking();
	}

	/**
	 */
	public void testMarkingOperations(){
		final Integer t1 = new Integer(1);
		final Integer t2 = new Integer(2);
		final Integer t3 = new Integer(3);
		final Multiset multiset = new Multiset();
		multiset.add(t1);
		multiset.add(t2);
		multiset.add(t3);
		assertEquals("first check", multiset.size(), 3);

		final Multiset m = new Multiset();
		m.add(t2);
		m.add(t3);
		assertEquals(m.size(), 2);

		Place p1 = new Place();
		Place p2 = new Place();

		this.mark.put(p1, multiset);//3
		this.mark.put(p2, m);//2

		assertEquals(2, this.mark.places().size());

		Multiset mres = this.mark.forName(p1.getName());
		assertTrue("Boolean check1", mres != null);
		assertEquals("forName1", 3, mres.size());
		mres = this.mark.forID(p1.getID());
		assertEquals("forID1", 3, mres.size());

		mres = this.mark.forName(p2.getName());
		assertTrue("Boolean check2", mres != null);
		assertEquals("forName2", 2, mres.size());
		mres = this.mark.forID(p2.getID());
		assertEquals("forID2", 2, mres.size());
	}

	/**
	 * Test suite. */
	public static Test suite() {
		return new TestSuite(TestBasicMarking.class);
	}

} // TestBasicMarking
//////////////////// end of file ////////////////////
