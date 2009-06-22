package org.amanzi.awe.neo.views.network.beans;

import java.util.ArrayList;
import java.util.List;


import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.Transaction;

/**
 * Class container for ExtTreeNodes. Contains list of {@link ExtTreeNode}
 * object.
 * 
 * @author Dalibor
 */
public class ExtTreeNodes 
{
	private List<ExtTreeNode> list;

	

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


	public ExtTreeNodes(EmbeddedNeo neo) {
		this();
		Transaction tx = neo.beginTx();
		Node reference = neo.getReferenceNode();
		for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
			list.add(new ExtTreeNode(neo,relationship.getEndNode()));
		}
		tx.success();
		tx.finish();
	}

	
	public ExtTreeNodes(EmbeddedNeo neo,Node node) {
		this();
		Transaction tx = neo.beginTx();
		Node reference = node;
		for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
			list.add(new ExtTreeNode(neo,relationship.getEndNode()));
		}
		tx.success();
		tx.finish();
	}
	
	
	public ExtTreeNodes() {
		super();
		list = new ArrayList<ExtTreeNode>();
	}

	public List<ExtTreeNode> getList() {
		return list;
	}

}
