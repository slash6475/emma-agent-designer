/* This is JFern generated file.     Do not edit. */
/* project page see http://sf.net/projects/jfern  */

package org.rakiura.cpn.example.booking;

import org.rakiura.cpn.BasicNet;
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Marking;
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Transition;

/** Net source file. */

@SuppressWarnings("all")
public class BookingNet extends BasicNet {


  private static final long serialVersionUID = 4051322340635848756L;
	
  private BrokerTool broker = new BrokerTool("example/services.props");
  public String banner = "Booking example: First version. Transition guards in use.";
    

  /* Places declaration. */
  private Place _place_p1 = new Place("p1");
  private Place _place_p2 = new Place("p2");
  private Place _place_p3 = new Place("p3");
  private Place _place_p4 = new Place("p4");

  /* Transitions declaration. */
  private Transition _transition_request = new Transition("request");
  private Transition _transition_book = new Transition("book");
  private Transition _transition_available = new Transition("available");
  private Transition _transition_notavailable = new Transition("notavailable");
  private Transition _transition_resign = new Transition("resign");

  /* Input Arcs declaration. */
  private InputArc _arc_a008 = new InputArc(_place_p4, _transition_resign);
  private InputArc _arc_a006 = new InputArc(_place_p3, _transition_book);
  private InputArc _arc_a004 = new InputArc(_place_p2, _transition_notavailable);
  private InputArc _arc_a003 = new InputArc(_place_p2, _transition_available);
  private InputArc _arc_a001 = new InputArc(_place_p1, _transition_request);

  /* Output Arcs declaration. */
  private OutputArc _arc_a007 = new OutputArc(_transition_notavailable, _place_p4);
  private OutputArc _arc_a005 = new OutputArc(_transition_available, _place_p3);
  private OutputArc _arc_a002 = new OutputArc(_transition_request, _place_p2);

  /* The default BookingNet constructor. */
  public BookingNet() {
    setName("BookingNet");
    setID("bookingnet");
    setTypeText("hlnet");
    setImplementsText("");
    setImportText("package org.rakiura.cpn.example.booking;\nimport java.util.Hashtable;\nimport java.util.List;");
    setDeclarationText("  private BrokerTool broker = new BrokerTool(\"example/services.props\");\n  public String banner = \"Booking example: First version. Transition guards in use.\";\n    ");

    add(_place_p1);
    add(_place_p2);
    add(_place_p3);
    add(_place_p4);
    add(_transition_request);
    add(_transition_book);
    add(_transition_available);
    add(_transition_notavailable);
    add(_transition_resign);
    add(_arc_a008);
    add(_arc_a006);
    add(_arc_a004);
    add(_arc_a003);
    add(_arc_a001);
    add(_arc_a007);
    add(_arc_a005);
    add(_arc_a002);

    /* Transition actions. */

    _transition_request.setAction(_transition_request.new Action() {
       public void execute() {

          Object t = get("Service");
          System.out.println("[client]   request for availability of  " + t);
       
       }
    });
    _transition_request.setActionText("          Object t = get(\"Service\");\n          System.out.println(\"[client]   request for availability of  \" + t);\n       ");

    _transition_book.setAction(_transition_book.new Action() {
       public void execute() {

          System.out.println("[client]   booking the service");
       
       }
    });
    _transition_book.setActionText("          System.out.println(\"[client]   booking the service\");\n       ");

    _transition_available.setAction(_transition_available.new Action() {
       public void execute() {

          System.out.println("[broker]   service IS available");
       
       }
    });
    _transition_available.setActionText("          System.out.println(\"[broker]   service IS available\");\n       ");

    _transition_notavailable.setAction(_transition_notavailable.new Action() {
       public void execute() {

          System.out.println("[broker]   service IS NOT available ");
       
       }
    });
    _transition_notavailable.setActionText("          System.out.println(\"[broker]   service IS NOT available \");\n       ");

    _transition_resign.setAction(_transition_resign.new Action() {
       public void execute() {

          System.out.println("[client]   resigning from booking, giving up.");
       
       }
    });
    _transition_resign.setActionText("          System.out.println(\"[client]   resigning from booking, giving up.\");\n       ");

    /* Transition guards. */

    _transition_available.setGuard(_transition_available.new Guard() {
       public boolean evaluate() {

          Marking marking = getMarking();
          Multiset set = marking.forName("p002");
          String service = (String) set.getAny();
          return broker.isAvailable(service);
       
       }
    });
    _transition_available.setGuardText("          Marking marking = getMarking();\n          Multiset set = marking.forName(\"p002\");\n          String service = (String) set.getAny();\n          return broker.isAvailable(service);\n       ");

    _transition_notavailable.setGuard(_transition_notavailable.new Guard() {
       public boolean evaluate() {

          Marking marking = getMarking();
          Multiset set = marking.forName("p002");
          String service = (String) set.getAny();
          return !broker.isAvailable(service);
       
       }
    });
    _transition_notavailable.setGuardText("          Marking marking = getMarking();\n          Multiset set = marking.forName(\"p002\");\n          String service = (String) set.getAny();\n          return !broker.isAvailable(service);\n       ");

    /* Transition types. */

    /* Transition type specifications. */

    /* Input Arcs guards. */

    /* Input Arcs expressions. */

    _arc_a008.setExpression(_arc_a008.new Expression() {
       public void evaluate() {

          var(1);
       
       }
    });
    _arc_a008.setExpressionText("          var(1);\n       ");

    _arc_a006.setExpression(_arc_a006.new Expression() {
       public void evaluate() {

          var(1);
       
       }
    });
    _arc_a006.setExpressionText("          var(1);\n       ");

    _arc_a004.setExpression(_arc_a004.new Expression() {
       public void evaluate() {

          var(1);
       
       }
    });
    _arc_a004.setExpressionText("          var(1);\n       ");

    _arc_a003.setExpression(_arc_a003.new Expression() {
       public void evaluate() {

          var(1);
       
       }
    });
    _arc_a003.setExpressionText("          var(1);\n       ");

    _arc_a001.setExpression(_arc_a001.new Expression() {
       public void evaluate() {

          var("Service");
       
       }
    });
    _arc_a001.setExpressionText("          var(\"Service\");\n       ");

    /* Output Arcs expressions. */

    _arc_a007.setExpression(_arc_a007.new Expression() {
       public Multiset evaluate() {

          return new Multiset(getMultiset().getAny());
       
       }
    });
    _arc_a007.setExpressionText("          return new Multiset(getMultiset().getAny());\n       ");

    _arc_a005.setExpression(_arc_a005.new Expression() {
       public Multiset evaluate() {

          return new Multiset(getMultiset().getAny());
       
       }
    });
    _arc_a005.setExpressionText("          return new Multiset(getMultiset().getAny());\n       ");

    _arc_a002.setExpression(_arc_a002.new Expression() {
       public Multiset evaluate() {

          return new Multiset(getMultiset().getAny());
       
       }
    });
    _arc_a002.setExpressionText("          return new Multiset(getMultiset().getAny());\n       ");


    /* Place Node Names */

    _place_p4.setName("Place_D");
    _place_p4.setID("p4");
    _place_p3.setName("Place_C");
    _place_p3.setID("p3");
    _place_p2.setName("Place_B");
    _place_p2.setID("p2");
    _place_p1.setName("Place_A");
    _place_p1.setID("p1");

    /* Transition Node Names */


    /* OutputArcs Names and IDs */

    _arc_a007.setName("_a007");
    _arc_a007.setID("a007");
    _arc_a005.setName("_a005");
    _arc_a005.setID("a005");
    _arc_a002.setName("_a002");
    _arc_a002.setID("a002");

    /* InputArcs Names and IDs */

    _arc_a008.setName("_a008");
    _arc_a008.setID("a008");
    _arc_a006.setName("_a006");
    _arc_a006.setID("a006");
    _arc_a004.setName("_a004");
    _arc_a004.setID("a004");
    _arc_a003.setName("_a003");
    _arc_a003.setID("a003");
    _arc_a001.setName("_a001");
    _arc_a001.setID("a001");

    /* Fusion of Places */
  }

}

