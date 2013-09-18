
package org.rakiura.cpn.example.booking;

/**/
import java.io.File;

import org.rakiura.cpn.BasicSimulator;
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.Net;
import org.rakiura.cpn.NetGenerator;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Simulator;
import org.rakiura.cpn.gui.NetViewer;

/**
 * A wee test for the booking net example.
 * To run the test, generate the java source for the nest first
 * <pre> java -classpath lib/rakiura-jfern.jar org.rakiura.cpn.NetGenerator example/BookingNet.xml</pre>
 * and then run this class to execute the generated net. 
 * 
 *<br><br>
 * BookingTest.java<br>
 * Created: Thu Nov  1 00:44:57 2001<br>
 *
 * @author Mariusz Nowostawski  (mariusz@rakiura.org)
 * @version $Revision: 1.19 $ $Date: 2006/11/02 03:14:23 $
 */
public class BookingTest  {
  
  public static void main(String[] args) throws Exception {
    if(args.length < 1){
      System.out.println("Call it with the service as an argument:");
      System.out.println("\te.g.  java BookingTest biking");
      return;
    }

    // First net test, static
    long begin = System.currentTimeMillis();
    final BookingNet net = new BookingNet();
    System.out.println("\n"+net.banner+"\n");

    Multiset m = new Multiset();
    for (int i = 0; i < args.length; i++) {
      m.add(new String(args[i]));
    }

    final Place startPlace = (Place) net.forName("p001");
    startPlace.addTokens(m);

    final Simulator sim = new BasicSimulator(net);
    sim.run();
    long end = System.currentTimeMillis();
    long time1 = end - begin;
    System.out.println("### Running the net in the Native Mode: "+time1+"millis");
    


    // Second net test, dynamic
    begin = System.currentTimeMillis();
    final Net net2 = NetGenerator.createNet(new File("example/BookingNet2.xml"));

    System.out.println("\n\n\n"+((BookingNet2) net2).banner+"\n");

    final Multiset m2 = new Multiset();
    for (int i=0; i < args.length; i++) {
      m2.add(new String(args[i]));
    }

    final Place startPlace2 = (Place) net2.forName("p001");
    startPlace2.addTokens(m2);

    final Simulator sim2 = new BasicSimulator(net2);
    sim2.run();
    end = System.currentTimeMillis();
    long time2 = end - begin;
    System.out.println("### Running the net in the Dynamic Mode: "+time2+"millis\n");

    System.out.println("Conclusion:\n   static vs dynamic loading ratio - dynamic loading is "+ Math.round((float)time2 / time1)+" times slower");

    System.out.println("\nDone.  Lets try some GUI now ;o)");



    /////////////  GUI test

    Multiset m3 = new Multiset();
    for(int i=0; i < args.length; i++){
      m3.add(new String(args[i]));
    }

    ((Place) net2.forName("p001")).addTokens(m3);

    new NetViewer(net2).getFrame().setVisible(true);
    // simple hack to give the user some time and 
    // to allow the user orgnize the net view by mouse
    System.out.println("Hit <enter> to run the booking demo....");
    try {
      System.in.read();
    } catch (java.io.IOException e) {/* ignore */}

    final Simulator sim3 = new BasicSimulator(net2);
    sim3.run();
    
    System.out.println("\nDONE.");
  }
  
} // BookingTest
//////////////////// end of file ////////////////////
