package emma.view;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import emma.model.nodes.Network;
import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.tools.BoundedSet;
import emma.tools.Listener;



public class NetworkViewer extends JPanel implements Listener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Network network = null;
	private NetworkViewerJTree jtree;
	private static Logger logger = Logger.getLogger(Network.class);
	
	public NetworkViewer(Network net){
		super();
		this.network = net;
	    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.jtree 	 = new NetworkViewerJTree(net.getName());
	    this.add(jtree);        
	    setVisible(true);
	    
	    net.getNotifier().addListener(this);
	}

	@Override
	public void Changed(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof Network)		networkChanged((Network) obj);
		else if(obj instanceof Node) 	nodeChanged((Node) obj);	
		}
	
	/*
	 * Update node list of the JTree view
	 */
	private void networkChanged(Network net){
		Node[] nodes = net.getNodes();
		boolean found = false;

		for(int i=0; i < nodes.length; i++){
			if(!jtree.isNode(nodes[i].getIp()))
				jtree.addNode(nodes[i].getIp());
		}
		Iterator<String> it = jtree.getNodes().iterator();
		while(it.hasNext()){
			found = false;
			String node = it.next();
			
			for(int i=0; i < nodes.length; i++)
				if(nodes[i].getIp().equals(node))
					found = true;
			
			if(!found)
				jtree.removeNode(node);
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
			if(jtree.getResourceType(node.getIp(), root) == null)
				jtree.addResourceType(node.getIp(), root);
			
			// Add resource 
			BoundedSet<Resource> resources = node.getResourceRoot(root); 
			Iterator<Resource> it2 = resources.iterator();
			while(it2.hasNext()){
				Resource r = it2.next();
				if(jtree.getResource(node.getIp(), root, r.getName()) == null)
					jtree.addResource(node.getIp(), root, r.getName());
			}
		}
		
		// Remove resources if it is not existed anymore
		List<String> types = jtree.getResourceTypes(node.getIp());
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
			if(!found)
				jtree.removeResourceType(node.getIp(), type);
			
			else {
			// Check for resource in type
			Iterator<String> it3 = jtree.getResources(node.getIp(), type).iterator();
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
					jtree.removeResource(node.getIp(), type, resource);
					found2 = false;
				}
				}
			}
		}
		

	}	
}
