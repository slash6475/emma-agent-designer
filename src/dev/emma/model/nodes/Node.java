package emma.model.nodes;

import emma.model.resources.System;
import emma.model.resources.Agent;
import emma.model.resources.Local;


public abstract class Node {
	
	private int totalSize; 
	private System[] systems;
	private Agent[] agents;
	private Local[] locals;
	
	public Node(int systemQ, int agentsQ, int localsQ){
		systems = new System[systemQ];
		agents = new Agent[agentsQ];
		locals = new Local[localsQ];
	}
}
