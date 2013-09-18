
package org.rakiura.cpn.example.booking;

/**/
import java.util.Properties;

/**
 * Represents a simple utility for a broker.
 * This class reads an input file, which must be
 * in standard Java properties format. The key is 
 * the name of the service, the value on/off defines
 * if the service is available or not.
 * 
 *<br><br>
 * BrokerTool.java<br>
 * Created: Thu Nov  1 00:36:16 2001<br>
 *
 * @author Mariusz Nowostawski (mariusz@rakiura.org)
 * @version $Revision: 1.2 $ $Date: 2006/01/13 04:56:52 $
 */

public class BrokerTool  {
  
  Properties services = new Properties();

  public BrokerTool(String filename) {
    try {
      this.services.load(new java.io.FileInputStream(filename));
    } catch(java.io.IOException e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  public boolean isAvailable(String service){
    if(this.services.get(service) != null &&
       this.services.get(service).equals("on"))
      return true;
	return false;
  }
  
} // BrokerTool
//////////////////// end of file ////////////////////
