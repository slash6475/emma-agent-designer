/* This is JFern generated file.     Do not edit. */
/* project page see http://sf.net/projects/jfern  */

package org.rakiura.cpn.example.booking;
import org.rakiura.cpn.BasicNet;
import org.rakiura.cpn.InputArc;
import org.rakiura.cpn.Multiset;
import org.rakiura.cpn.OutputArc;
import org.rakiura.cpn.Place;
import org.rakiura.cpn.Transition;

/** Net source file. */
@SuppressWarnings("all")
public class BookingNet2 extends BasicNet {

  private static final long serialVersionUID = 3761972674481370424L;
  
  private BrokerTool broker = new BrokerTool("example/services.props");
  public String banner = "Second version of the booking net. Input Arc guards.";
    

  /* Places declaration. */
  private Place _place_p001 = new Place("p001");
  private Place _place_p002 = new Place("p002");
  private Place _place_p003 = new Place("p003");
  private Place _place_p004 = new Place("p004");

  /* Transitions declaration. */
  private Transition _transition_request = new Transition("request");
  private Transition _transition_book = new Transition("book");
  private Transition _transition_available = new Transition("available");
  private Transition _transition_notavailable = new Transition("notavailable");
  private Transition _transition_resign = new Transition("resign");

  /* Input Arcs declaration. */
  private InputArc _arc_a008 = new InputArc(_place_p004, _transition_resign);
  private InputArc _arc_a006 = new InputArc(_place_p003, _transition_book);
  private InputArc _arc_a004 = new InputArc(_place_p002, _transition_notavailable);
  private InputArc _arc_a003 = new InputArc(_place_p002, _transition_available);
  private InputArc _arc_a001 = new InputArc(_place_p001, _transition_request);

  /* Output Arcs declaration. */
  private OutputArc _arc_a007 = new OutputArc(_transition_notavailable, _place_p004);
  private OutputArc _arc_a005 = new OutputArc(_transition_available, _place_p003);
  private OutputArc _arc_a002 = new OutputArc(_transition_request, _place_p002);

  /* The default BookingNet2 constructor. */
  public BookingNet2() {
    setName("BookingNet2");
    setID("bookingnet");
    setTypeText("hlnet");
    setImplementsText("");
    setImportText("package org.rakiura.cpn.example.booking;");
    setDeclarationText("  private BrokerTool broker = new BrokerTool(\"example/services.props\");\n  public String banner = \"Second version of the booking net. Input Arc guards.\";\n    ");

    add(_place_p001);
    add(_place_p002);
    add(_place_p003);
    add(_place_p004);
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

          Multiset multiset = getMultiset();
          Object t = multiset.getAny();
          System.out.println("[client]   request for availability of " + t.toString());
       
       }
    });
    _transition_request.setActionText("          Multiset multiset = getMultiset();\n          Object t = multiset.getAny();\n          System.out.println(\"[client]   request for availability of \" + t.toString());\n       ");

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

    /* Transition types. */

    /* Transition type specifications. */

    /* Input Arcs guards. */

    _arc_a004.setGuard(_arc_a004.new Guard() {
       public boolean evaluate() {

          final Multiset multiset = getMultiset();
          if(multiset.size() == 0) return false;
          String service = (String) get("y");
System.out.println("[NOT available] checking if we have " + service + "   ...." +  broker.isAvailable(service));

          return !broker.isAvailable(service);
       
       }
    });
    _arc_a004.setGuardText("          final Multiset multiset = getMultiset();\n          if(multiset.size() == 0) return false;\n          String service = (String) get(\"y\");\nSystem.out.println(\"[NOT available] checking if we have \" + service + \"   ....\" +  broker.isAvailable(service));\n          return !broker.isAvailable(service);\n       ");

    _arc_a003.setGuard(_arc_a003.new Guard() {
       public boolean evaluate() {

          final Multiset multiset = getMultiset();
          if (multiset.size() == 0) return false;
          String service = (String) get("x");
System.out.println("[available] checking if we have " + service + "   ...." +  broker.isAvailable(service));
          return broker.isAvailable(service);
       
       }
    });
    _arc_a003.setGuardText("          final Multiset multiset = getMultiset();\n          if (multiset.size() == 0) return false;\n          String service = (String) get(\"x\");\nSystem.out.println(\"[available] checking if we have \" + service + \"   ....\" +  broker.isAvailable(service));\n          return broker.isAvailable(service);\n       ");

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

          var("y");
       
       }
    });
    _arc_a004.setExpressionText("          var(\"y\");\n       ");

    _arc_a003.setExpression(_arc_a003.new Expression() {
       public void evaluate() {

          var("x");
       
       }
    });
    _arc_a003.setExpressionText("          var(\"x\");\n       ");

    _arc_a001.setExpression(_arc_a001.new Expression() {
       public void evaluate() {

          var(1);
       
       }
    });
    _arc_a001.setExpressionText("          var(1);\n       ");

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

