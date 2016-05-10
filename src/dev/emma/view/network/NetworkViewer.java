package emma.view.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import emma.model.nodes.Network;
import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.tools.BoundedSet;
import emma.tools.Listener;



public class NetworkViewer extends NetworkJTree implements Listener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7947025900047711801L;
		
	public NetworkViewer(Network net){
		super(net.getName());
		net.getNotifier().addListener(this);
	}

	@Override
	public void changed(Object obj) {
		if(obj instanceof Network)	networkChanged((Network) obj);
		else if(obj instanceof Node) 	nodeChanged((Node) obj);	
	}
	
	/*
	 * Update node list of the JTree view
	 */
	private void networkChanged(Network net){
		Node[] nodes = (Node[])net.getNodesArray();System.out.println("ooo " + nodes.toString());
		boolean found = false;

		for(int i=0; i < nodes.length; i++){
			if(!this.isNode(nodes[i].getIp()))
				this.addNode(nodes[i].getIp());
		}
		Iterator<String> it = this.getNodes().iterator();
		while(it.hasNext()){
			found = false;
			String node = it.next();
			
			for(int i=0; i < nodes.length; i++)
				if(nodes[i].getIp().equals(node))
					found = true;
			
			if(!found){
				this.removeNode(node);
			}
		}
	}
	/*
	 * Update resource list of the JTree view
	 */
	private void nodeChanged(Node node){
		ArrayList<String> roots = node.getResourceRoots();
		// Add resource if it not exist
		Iterator<String> it = roots.iterator();
		while(it.hasNext()){
			String root = it.next();
			// Add resource type
			if(this.getResourceType(node.getIp(), root) == null){
				this.addResourceType(node.getIp(), root);
			}
			// Add resource 
			BoundedSet<Resource> resources = node.getResourceRoot(root); 
			Iterator<Resource> it2 = resources.iterator();
			while(it2.hasNext()){
				Resource r = it2.next();
				if(this.getResource(node.getIp(), root, r.getName()) == null){
					this.addResource(node.getIp(), root, r.getName());
				}
			}
		}
		
		List<String> types = this.getResourceTypes(node.getIp());
		it = types.iterator();
		while(it.hasNext()){
			String type = it.next();
			boolean found = false;
			
			Iterator<String> it2 = node.getResourceRoots().iterator();
			while(it2.hasNext()){
				String root = it2.next();
				if(root.equals(type))
					found = true;
			}
			if(!found){
				this.removeResourceType(node.getIp(), type);
			}
			else {
				// Check for resource in type
				Iterator<String> it3 = this.getResources(node.getIp(), type).iterator();
				while(it3.hasNext()){
					String resource = it3.next();
					boolean found2 = false;
					Iterator<Resource> it4 = node.getResourceRoot(type).iterator();
					while(it4.hasNext()){
						Resource resource2 = it4.next();
						if(resource.equals(resource2.getName())){
							found2 = true;
						}
					}
					if(!found2){
						this.removeResource(node.getIp(), type, resource);
						found2 = false;
					}
				}
			}
		}
	}	
}
