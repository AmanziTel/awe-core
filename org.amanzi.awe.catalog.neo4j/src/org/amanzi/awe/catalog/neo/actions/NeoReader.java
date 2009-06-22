package org.amanzi.awe.catalog.neo.actions;

import java.util.Vector;



import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.Transaction;

import net.refractions.udig.catalog.CatalogPlugin;


public class NeoReader 
{
	public static enum NetworkElementTypes 
	{
		NETWORK("Network"),
		BSC("BSC"),
		SITE("Name"),
		SECTOR("Cell");
		
		private String header = null;
		
		private NetworkElementTypes(String header)
		{
			this.header = header;
		}
		public String getHeader()
		{
			return header;
		}
		
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
	
	public static enum NetworkRelationshipTypes implements RelationshipType 
	{
		CHILD,
		SIBLING,
		INTERFERS
	}

	EmbeddedNeo neo;
	
	String NeoLocation;
	
	public NeoReader(String NeoDir) 
	{
        super();
        this.NeoLocation=NeoDir;
        neo = new EmbeddedNeo(NeoLocation);
        CatalogPlugin.addListener(new NeoReaderResolveChangeReporter());
    }
	
	
	public static Node getNetwork(EmbeddedNeo neo, String basename) {
		Node network = null;
		Transaction tx = neo.beginTx();
		try {
			Node reference = neo.getReferenceNode();
			for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) 
			{
				Node node = relationship.getEndNode();
				if (node.hasProperty("type") && node.getProperty("type").equals(NetworkElementTypes.NETWORK.toString()) && node.hasProperty("name")
						&& node.getProperty("name").equals(basename))
					return node;
			}
			tx.success();
		} finally {
			tx.finish();
		}
		return network;
	}
	
	
	public static Vector<Node> getAllNetworkNodes(EmbeddedNeo neo)
	{
		Vector<Node> NetNodes=new Vector<Node>();
		Vector<Node> AllNodes=new Vector<Node>();
		AllNodes=(Vector<Node>) neo.getAllNodes();
		Node networkNode=null;
		
		for(int nodes=0;nodes<AllNodes.size();nodes++)
		{
           networkNode=AllNodes.elementAt(nodes);
           
           if (networkNode.hasProperty("type") && networkNode.getProperty("type").equals(NetworkElementTypes.NETWORK.toString()))
           {
				NetNodes.add(networkNode);
		   }
		}
		
		return NetNodes;
	}
	
	
	public static Vector<Node> getChildren(Node node)
	{
		Vector<Node> ChildrenNodes=new Vector<Node>()  ;
		for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.CHILD,Direction.OUTGOING))
		{
			ChildrenNodes.add(relationship.getEndNode());
		}
          return ChildrenNodes;
	}
	
	
}
