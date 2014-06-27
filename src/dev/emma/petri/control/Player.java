package emma.petri.control;

import java.util.Iterator;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import emma.petri.model.InputArc;
import emma.petri.model.Net;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;
/**
 * @author  pierrotws
 */
public class Player extends Thread{
    private static Logger logger = Logger.getLogger(Player.class);
	
	private ScriptEngine engine;
	private Bindings bindings;
	private Net net;
	private boolean play;
	
	public Player(Net net){
		this.net=net;
		this.play=false;
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("JavaScript");
		this.bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
	}
	
	@Override
	public void run(){
		while(true){
			if(play){
				play=false;
				logger.debug("Player running...");
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
									new Execution(t);
								}
							}
						}
					}
				}	
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage());
				}
				if(!play){
					JOptionPane.showMessageDialog(null, "Simulation finished");
				}
			}
			else{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage());
				}
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
	
	private class Execution extends Thread{
		private Transition t;
		public Execution(Transition t){
			this.t = t;
			this.start();
		}
		
		@Override
		public void run() {
			Iterator<InputArc> itia = t.getInputArcs().iterator();
			while(itia.hasNext()){
				InputArc ia = itia.next();
				ia.activate();
				Place p = ia.getPlace();
				try {
					p.removeToken();
					this.put(p.getType(),p.getName(),p.getData().get());
				} catch (ScriptException e) {
					logger.warn(e.getMessage());
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			itia = t.getInputArcs().iterator();
			while(itia.hasNext()){
				itia.next().desactivate();
			}
			try {
				Boolean test = (Boolean) this.eval(t.getCondition());
				if(test){
					t.activate();
					//Si une transition s'active, la simulation peut continuer
					//Si aucune exec n'actualise la transition, on considère que 
					//la simulation est dans un état bloqué
					play=true;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						logger.warn(e.getMessage());
					}
					t.desactivate();
					Iterator<OutputArc> itoa = t.getOutputArcs().iterator();
					while(itoa.hasNext()){
						OutputArc a = itoa.next();
						a.activate();
						this.put("","x", a.getPlace().getData().get());
						a.getPlace().getData().put(String.valueOf(this.eval(a.getExpression())));
						a.getPlace().putToken();
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						logger.warn(e.getMessage());
					}
					itoa = t.getOutputArcs().iterator();
					while(itoa.hasNext()){
						itoa.next().desactivate();
					}
				}
			} catch (ScriptException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
			}
		}

		private Object eval(String condition) throws ScriptException {
			return engine.eval(condition.replaceAll("\\?|:", "_"),bindings);
		}
		
		private void put(String type,String name, String value) throws ScriptException{
			bindings.put(type+"_"+name, engine.eval(value));
		}
	}
}
