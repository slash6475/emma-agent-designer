
package org.rakiura.cpn;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test utility for BasicMultiset class test suite.
 *
 *<br>
 * TestBasicMultiset.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.8 $
 */
public class TestBasicMultiset extends TestCase {

	/**/
	private Multiset multiset;

	/**/
	public TestBasicMultiset(String name) {
		super(name);
	}

	/** Setup. */
	protected void setUp(){
		this.multiset = new Multiset();
	}

	/**
	 */
	public void testToArray(){
		this.multiset.add(new Integer(1));
		this.multiset.add(new Integer(2));
		final Object[] array = this.multiset.toArray();
		assertEquals(array.length, 2);
	}

	 /**
	 */
	public void testMultisetOperations(){
		final Integer t1 = new Integer(1);
		final Integer t2 = new Integer(2);
		final Integer t3 = new Integer(3);
		this.multiset.add(t1);
		this.multiset.add(t2);
		this.multiset.add(t3);
		assertEquals("first test", this.multiset.size(), 3);

		final Multiset m = new Multiset();
		m.add(t2);
		m.add(t3);
		assertEquals(m.size(), 2);

		this.multiset.removeAll(m);
		assertEquals(this.multiset.size(), 1);

		m.addAll(this.multiset);
		assertEquals(m.size(), 3);

		this.multiset.add(t3);
		assertEquals(this.multiset.size(), 2);

		m.removeAll(this.multiset);
		assertEquals(m.size(), 1);
	}

	/**
	 *
	 */
	public void testMultisetGetAny() {
		final Integer t1 = new Integer(1);
		final Integer t2 = new Integer(2);
		final Long t3 = new Long(3);
		Multiset m = new Multiset ();
		m.add(t1);
		Integer i = (Integer) m.getAny();
		assertEquals(i.intValue(), 1);
		i = (Integer) m.getAny(Number.class);
		assertEquals(i.intValue(), 1);
		i = (Integer) m.getAny(Integer.class);
		assertEquals(i.intValue(), 1);
		m.add(t2);
		m.add(t3);
		Object o = m.getAny ();
		assertTrue(o.equals(t1) || o.equals(t2) || o.equals(t3));
		Multiset r = m.getAny (2, Long.class);
		assertEquals(r, null);
		r = m.getAny(2, Integer.class);
		assertEquals(r.size(), 2);
		r = m.getAny(3, Number.class);
		assertEquals(r.size(), 3);
	}


	/**
	 * Test suite. */
	public static Test suite() {
		return new TestSuite(TestBasicMultiset.class);
	}

}
//////////////////// end of file ////////////////////
