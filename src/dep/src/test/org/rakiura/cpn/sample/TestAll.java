
package org.rakiura.cpn.sample;

/**/
import junit.framework.TestSuite;
import junit.framework.Test;
import junit.textui.TestRunner;

/**
 * Test utility for the whole org.rakiura.cpn.sample package. Run this test for
 * integrated test suite for all classes from this package.
 * 
 *<br>
 * TestAll.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.1 $
 */
public class TestAll {
    
  /**
   */
  public static Test suite() { 
    TestSuite suite = new TestSuite(); 
    suite.addTest(TestMaxValueExample.suite());
    suite.addTest(TestTwoTasksExample.suite());
    return suite;
  }

  /**
   * Execute all tests. */
  public static void main(String[] args){
    TestRunner.run(TestAll.suite());
  }

} // TestAll
//////////////////// end of file ////////////////////
