package emma.petri.control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import emma.petri.model.InputArc;
import emma.petri.model.Net;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;
import expr.Parser;
import expr.SyntaxException;
/**
 * @author  pierrotws
 */

public class Player extends Thread{
	
	private Net net;
	private boolean play;
	public Player(Net net){
		this.net=net;
		this.play=false;
	}
	
	@Override
	public void run(){
		while(true){
			try {
				Thread.sleep(1000);
				if(play){
					Iterator<Subnet> itSub = net.getSubnets().iterator();
					while(itSub.hasNext()){
						Iterator<Scope> itScope = itSub.next().getScopes().iterator();
						while(itScope.hasNext()){
							Iterator<Transition> itTrans = itScope.next().getTransitions().iterator();
							while(itTrans.hasNext()){
								Transition t = itTrans.next();
								Iterator<InputArc> itia = t.getInputArcs().iterator();
								while(itia.hasNext()){
									if(itia.next().getPlace().hasToken()){
										this.executeTransition(t);
										break;
									}
								}
							}
						}
					}
				}	
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	private void executeTransition(Transition t) {
		System.out.println("EXEC transition : "+t.getPlace().getName());
		HashMap<String,String> map = new HashMap<>();
		Iterator<InputArc> itia = t.getInputArcs().iterator();
		while(itia.hasNext()){
			Place p = itia.next().getPlace();
			map.put(p.getType()+':'+p.getName(),p.getData().get());
			p.removeToken();
		}
		Iterator<OutputArc> itoa = t.getOutputArcs().iterator();
		while(itoa.hasNext()){
			OutputArc a = itoa.next();
			String expr = a.getExpression();
			Iterator<Entry<String,String>> ie = map.entrySet().iterator();
			while(ie.hasNext()){
				Entry<String,String> e = ie.next();
				System.out.print("Replace:["+e.getKey()+","+e.getValue()+"]");
				expr=expr.replaceAll(e.getKey(), e.getValue());
			}
			expr=expr.replaceAll("\\?x",a.getPlace().getData().get());
			System.out.println("\t expression:["+expr+"]");
			try {
				a.getPlace().getData().put(String.valueOf(((int)Parser.parse(expr).value())));
				a.getPlace().putToken();
			} catch (SyntaxException e) {
				System.out.println("CANNOT EVALUATE ARC : "+e.getMessage());
			}
		}
	}

	public void play(){
		this.play=true;
		if(!this.isAlive()){
			this.start();
		}
	}
	
	public void pause(){
		this.play=false;
	}
	
	public void playPause(){
		if(this.play){
			this.pause();
		}
		else{
			this.play();
		}
	}
}
