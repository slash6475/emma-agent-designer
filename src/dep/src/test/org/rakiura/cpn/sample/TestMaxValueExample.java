
package org.rakiura.cpn.sample;

/**/
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.rakiura.cpn.*;

/**
 * Test utility for MaxValueExample.
 * 
 *<br>
 * TestMaxValueExample.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.6 $
 */
public class TestMaxValueExample extends TestCase {
    
  /**/
  private MaxValueNet net;
  private Simulator sim;

  /**/
  public TestMaxValueExample(String name) {
    super(name);
  }
    
  /** Setup. */
  protected void setUp(){
	  /* do nothing */
  }
  
  /**
   */
  public void testMaxValue2only(){
    Multiset m = new Multiset();
    for(int i=0; i<2; i++){
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    this.sim = new BasicSimulator(this.net);
    this.sim.run();
  }

  /**
   */
public void testMaxValue10() {
    Multiset m = new Multiset();
    for(int i=0; i<10; i++){
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    sim = new BasicSimulator(this.net);
    sim.run();
    }

  /**
   */
  public void testMaxValue100(){
    Multiset m = new Multiset();
    for(int i=0; i<100; i++){
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    this.sim = new BasicSimulator(this.net);
    long begin = System.currentTimeMillis();
    this.sim.run();
    long end = System.currentTimeMillis();
    System.out.println("\nTest with 100 took exactly: " + (end - begin) + " mills");
  } 
  
  /**
   */
  public void testMaxValue500() {
    Multiset m = new Multiset();
    for(int i=0; i < 500; i++) {
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    this.sim = new BasicSimulator(this.net);
    long begin = System.currentTimeMillis();
    this.sim.run();
    long end = System.currentTimeMillis();
    System.out.println("\nTest with 500 took exactly: " + (end-begin) + " mills");
  } 

  /**
   */
  public void testMaxValue1000(){
    Multiset m = new Multiset();
    for(int i = 0; i < 1000; i++) {
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    this.sim = new BasicSimulator(this.net);
    long begin = System.currentTimeMillis();
    this.sim.run();
    long end = System.currentTimeMillis();
    System.out.println("\nTest with 1000 took exactly: " + (end - begin) + " mills");
  } 

  /**
   * If you want to execute that test, write 'test'before the method name.
   */
  public void MaxValue10000() {
    Multiset m = new Multiset();
    for(int i = 0; i < 10000; i++) {
      m.add(new Integer(i));
    }
    this.net = new MaxValueNet(m);
    this.sim = new BasicSimulator(this.net);
    long begin = System.currentTimeMillis();
    this.sim.run();
    long end = System.currentTimeMillis();
    System.out.println("\nTest with 10000 took exactly: " + (end - begin) + " mills");
  } 


  /**
   * Test suite. */
  public static Test suite() { 
    return new TestSuite(TestMaxValueExample.class); 
  }

} // TestMaxValueExample
//////////////////// end of file ////////////////////
