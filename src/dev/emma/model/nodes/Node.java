package emma.model.nodes;

import emma.model.resources.System;
import emma.model.resources.Agent;
import emma.model.resources.Local;


public abstract class Node {
	
	private int totalSpace;
	private int unavailableSpace;
	private System[] systems;
	private Agent[] agents;
	private Local[] locals;
	
	public Node(int totalSpace, int systemQ, int agentsQ, int localsQ){
		this.totalSpace=totalSpace;
		this.systems = new System[systemQ];
		this.agents = new Agent[agentsQ];
		this.locals = new Local[localsQ];
	}
	public Agent[] getAgents(){
		return agents;
	}
	public Local[] getLocals(){
		return locals;
	}	
	public System[] getSystems(){
		return systems;
	}
	public int getTotalSpace(){
		return totalSpace;
	}
	public int getAvailableSpace(){
		return totalSpace-unavailableSpace;
	}
}