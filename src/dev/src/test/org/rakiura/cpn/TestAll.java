
package org.rakiura.cpn;

/**/
import junit.framework.TestSuite;
import junit.framework.Test;
import junit.textui.TestRunner;

/**
 * Test utility for the whole org.rakiura.cpn.basic package. Run this test for
 * integrated test suite for all classes from this package.
 * 
 *<br>
 * TestAll.java<br>
 * <br>
 * Created: Fri Oct 29 17:42:14 1999<br>
 *
 * @author Mariusz Nowostawski
 * @version $Revision: 1.7 $
 */
public class TestAll {
    
  /**
   */
  public static Test suite() { 
    TestSuite suite = new TestSuite(); 
    suite.addTest(TestBasicMultiset.suite());    
    suite.addTest(TestBasicMarking.suite());
    suite.addTest(TestBasicTransition.suite());
    suite.addTest(TestBasicNet.suite());
    suite.addTest(TestBasicSimulator.suite());
    suite.addTest(TestTimedSimulator.suite());
    suite.addTest(TestTimedSimulator2.suite());
    suite.addTest(TestMultitokenNet.suite());    
	suite.addTest(TestXMLSerializer.suite());
    return suite;
  }

  /**
   * Execute all tests. */
  public static void main(String[] args){
    TestRunner.run(TestAll.suite());
  }

} // TestAll
//////////////////// end of file ////////////////////
