package org.amanzi.awe.neo.views.network.beans;


import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;

public class ExtTreeNode 
{
	
	public static enum NetworkElementTypes 
	{
		NETWORK("Network"),
		BSC("BSC"),
		SITE("Name"),
		SECTOR("Cell");
		private String header = null;
		private NetworkElementTypes(String header){
			this.header = header;
		}
		public String getHeader(){return header;}
		public String toString(){return super.toString().toLowerCase();}
	}
	
	public static enum NetworkRelationshipTypes implements RelationshipType 
	{
		CHILD,
		SIBLING,
		INTERFERS
	}

	Node treeNode;

	public ExtTreeNode(EmbeddedNeo neo,Node node)
	{
		treeNode=neo.getNodeById(node.getId());
	}
	
	public String getText()
	{
		return (String)treeNode.getProperty("name");
	}
	
	public String getId()
	{
	   return	String.valueOf(treeNode.getId());
	}
	
	
	public boolean isLeaf()
	{
		int childNodesNumber=0;
		for(@SuppressWarnings("unused") Relationship relationship : treeNode.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING))
		{
			childNodesNumber++;
		}
		
     if(childNodesNumber==0)
     {
    	 return true;
     }
     else
     {
    	 return false;
     }
	}
	
	public long getNodeId()
	{
	   return	treeNode.getId();
	}
	
	
}
