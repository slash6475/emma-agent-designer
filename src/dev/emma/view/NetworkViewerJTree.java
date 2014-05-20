package emma.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import emma.model.nodes.Network;

public class NetworkViewerJTree extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7555400852371126749L;
	private DefaultMutableTreeNode network;
	private JTree tree;
	private static Logger logger = Logger.getLogger(Network.class);
	
	

	private DefaultTreeModel treeModel;
	public NetworkViewerJTree(String networkName){
		network 	= new DefaultMutableTreeNode(networkName);
	    treeModel 	= new DefaultTreeModel(network);

	    tree 		= new JTree(treeModel);

	    tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
	    this.add(new JScrollPane(tree));
	    setVisible(true);
	}	
	
	/*
	 * NODE Management
	 */
		
	public void addNode(String name){
		logger.debug("[NODE] ADD " + name);
		network.add(new DefaultMutableTreeNode(name));
		treeModel.reload();
	}
	
	public boolean isNode(String name){
		DefaultMutableTreeNode node = network;
		while(node != null){
			if(node.getUserObject().toString().equals(name))
				return true;
			node = node.getNextNode();
		}
		return false;
	}
	
	public List<String> getNodes(){
		ArrayList<String> nodes = new ArrayList<String>();

		TreeNode node = null;
		for (int i=0; i < network.getChildCount(); i++){
			node = network.getChildAt(i);
			nodes.add(node.toString());
		}
		
		return nodes;
	}
	
	public void removeNode(String name){
		logger.debug("[NODE] DELETE " + name);
		DefaultMutableTreeNode node = network;
		
		while(node != null){
			if(node.getUserObject().toString().equals(name))
				break;
			node = node.getNextNode();
		}
		network.remove(node);
		treeModel.reload();
	}

	public DefaultMutableTreeNode getNode(String nodeName){
		DefaultMutableTreeNode node = network;
		while(node != null){
			if(node.getUserObject().toString().equals(nodeName))
				return node;
			node = node.getNextNode();
		}
		return null;
	}
	/*
	 * RESOURCE Type Management
	 */
	public boolean addResourceType(String nodeName, String resourceTypeName){
		DefaultMutableTreeNode node = getNode(nodeName);
		if(node == null) {
			return false;
		}
		
		if(getResourceType(nodeName, resourceTypeName) == null){
			node.add(new DefaultMutableTreeNode(resourceTypeName));
			}
		treeModel.reload(node);
		
		return true;
	}
	
	public void removeResourceType(String nodeName, String resourceTypeName){
		DefaultMutableTreeNode node = getNode(nodeName);
		DefaultMutableTreeNode resourceType = getResourceType(nodeName, resourceTypeName);
		node.remove(resourceType);
		treeModel.reload(node);
	}
	
	public List<String> getResourceTypes(String nodeName){
		ArrayList<String> types = new ArrayList<String>();
				
		DefaultMutableTreeNode node = getNode(nodeName);
		if(node.isLeaf())	return types;
		
		TreeNode type = null;
		for (int i=0; i < node.getChildCount(); i++){
			type = node.getChildAt(i);
			types.add(type.toString());
		}
		
		return types;
	}
	
	public DefaultMutableTreeNode getResourceType(String nodeName, String resourceTypeName){
		DefaultMutableTreeNode node = getNode(nodeName);
		if(node == null) 
			return null;
		
		
		if(node.isLeaf()) return null;
		
		DefaultMutableTreeNode resourceType = (DefaultMutableTreeNode) node.getChildAt(0);
		while(resourceType != null){
			if(resourceType.getUserObject().toString().equals(resourceTypeName))
				return resourceType;
			resourceType = resourceType.getNextNode();
		}
		
		return null;
	}
	
	public boolean addResource(String nodeName, String resourceTypeName, String resourceName){
		DefaultMutableTreeNode type = getResourceType(nodeName, resourceTypeName);
		
		if (type == null) 
			return false;

		if(getResource(nodeName, resourceTypeName, resourceName) == null){
			type.add(new DefaultMutableTreeNode(resourceName));
			treeModel.reload(type);
			return true;
		}
		return false;
	}
	
	public List<String> getResources(String nodeName, String resourceTypeName){
		ArrayList<String> resources = new ArrayList<String>();
				
		DefaultMutableTreeNode type = getResourceType(nodeName, resourceTypeName);

		if(type.isLeaf())			return resources;
		
		TreeNode resource = null;
		for (int i=0; i < type.getChildCount(); i++){
			resource = type.getChildAt(i);
			resources.add(resource.toString());
		}
		

		
		return resources;
	}	

	public DefaultMutableTreeNode getResource(String nodeName, String resourceTypeName, String resourceName){
		DefaultMutableTreeNode type = getResourceType(nodeName, resourceTypeName);
		
		if (type == null) return null;
		if (type.isLeaf()) return null;

		DefaultMutableTreeNode resource = (DefaultMutableTreeNode) type.getChildAt(0);
		while(resource != null){
			if(resource.getUserObject().toString().equals(resourceName))
				return resource;
			resource = resource.getNextNode();
		}
		
		return null;
	}

	public void removeResource(String nodeName, String resourceTypeName, String resourceName){
		DefaultMutableTreeNode resourceType = getResourceType(nodeName, resourceTypeName);
		if(resourceType == null) return;
		
		DefaultMutableTreeNode resource 	= getResource(nodeName, resourceTypeName, resourceName);
		if(resource == null) return;
		
		resourceType.remove(resource);
		treeModel.reload(resourceType);
	}	
}
