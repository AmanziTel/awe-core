package org.amanzi.awe.catalog.neo.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import net.refractions.udig.catalog.CatalogPlugin;



import org.amanzi.awe.catalog.neo.shapes.NeoLine;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiLine;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiPoint;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiPolygon;
import org.amanzi.awe.catalog.neo.shapes.NeoPoint;
import org.amanzi.awe.catalog.neo.shapes.NeoPolygon;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;



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
	Node node;
	private URL url;
    private CoordinateReferenceSystem crs;
    private ReferencedEnvelope bounds;
	
	public NeoReader(EmbeddedNeo neo,Node node) throws MalformedURLException 
	{
        super();
        this.neo=neo;
        this.node=node;
        CatalogPlugin.addListener(new NeoReaderResolveChangeReporter(neo));
    }
	
	public NeoReader() throws MalformedURLException 
	{
       
    }
	
	public NeoReader(NeoService service) throws MalformedURLException 
	{// TODO Auto-generated constructor stub
		 this();
         this.url = service.getIdentifier();
         if(neo==null)
         {
        	 neo =new EmbeddedNeo(url.toString());
         }
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
	
	
	 public final CoordinateReferenceSystem getCRS( final CoordinateReferenceSystem defaultCRS,Node node ) 
	 {
	        if (crs == null) {
	            crs = defaultCRS; // default if crs cannot be found below
	            try 
	            {
	                if (node.getProperty("crs")!=null) 
	                {
	                    // The simple approach is to name the CRS, eg. EPSG:4326 (GeoJSON spec prefers a
	                    // new naming standard, but I'm not sure geotools knows it)
	                    crs = CRS.decode(node.getProperty("crs").toString());
	                } 
	            } 
	            catch (Exception crs_e) 
	            {
	                System.err.println("Failed to interpret CRS: " + crs_e.getMessage());
	                crs_e.printStackTrace(System.err);
	            }
	        }
	        return crs;
	  }
	 
	 
	 public final ReferencedEnvelope getBounds() {
	        if (bounds == null) {
	            // Create Null envelope
	            this.bounds = new ReferencedEnvelope(getCRS());
	            // First try to find the BBOX definition in the JSON directly
	            try {
	              double[] neoBBox =( double[])node.getProperty("bbox");
	                if (neoBBox != null) {
	                    double minX = neoBBox[0];
	                    double minY = neoBBox[1];
	                    double maxX = neoBBox[2];
	                    double maxY = neoBBox[3];
	                    this.bounds = new ReferencedEnvelope(minX, maxX, minY, maxY, crs);
	                } else {
	                    System.err.println("No BBox defined in the Node bbox object");
	                }
	            } catch (Exception e) {
	                System.err.println("Failed to interpret BBOX: " + e.getMessage());
	                e.printStackTrace(System.err);
	            }
	           
	            try {
	                if (this.bounds.isNull()) {

	                   
	                    if (node.hasProperty("features")) 
	                    {
	                    	
	                        for( int i = 0; i < ((Object[])node.getProperty("features")).length; i++ ) 
	                        {
	                            if(((Object[])node.getProperty("features"))[i] instanceof NeoPoint)
	                            {
	                            	this.bounds.expandToInclude(((NeoPoint)((Object[])node.getProperty("features"))[i]).getCoords());
	                            }
	                            else if(((Object[])node.getProperty("features"))[i] instanceof NeoMultiPoint)
	                            {
	                            	for(int j=0;j<((NeoMultiPoint)((Object[])node.getProperty("features"))[i]).getCoords().length;j++)
	                            	{
	                            	this.bounds.expandToInclude(((NeoMultiPoint)((Object[])node.getProperty("features"))[i]).getCoords()[j]);
	                                } 
	                            }
	                            else if(((Object[])node.getProperty("features"))[i] instanceof NeoLine)
	                            {
	                            	for(int j=0;j<((NeoLine)((Object[])node.getProperty("features"))[i]).getCoords().length;j++)
	                            	{
	                            	this.bounds.expandToInclude(((NeoLine)((Object[])node.getProperty("features"))[i]).getCoords()[j]);
	                                } 
	                            }
	                            else if(((Object[])node.getProperty("features"))[i] instanceof NeoMultiLine)
	                            {
	                            	for(int j=0;j<((NeoMultiLine)((Object[])node.getProperty("features"))[i]).getCoords().length;j++)
	                            	{
	                            	this.bounds.expandToInclude(((NeoMultiLine)((Object[])node.getProperty("features"))[i]).getCoords()[j]);
	                                } 
	                            }
	                            else if(((Object[])node.getProperty("features"))[i] instanceof NeoPolygon)
	                            {
	                            	for(int j=0;j<((NeoPolygon)((Object[])node.getProperty("features"))[i]).getCoords().length;j++)
	                            	{
	                            	this.bounds.expandToInclude(((NeoPolygon)((Object[])node.getProperty("features"))[i]).getCoords()[j]);
	                                } 
	                            }
	                            else if(((Object[])node.getProperty("features"))[i] instanceof NeoMultiPolygon)
	                            {
	                            	for(int j=0;j<((NeoMultiPolygon)((Object[])node.getProperty("features"))[i]).getPoints().length;j++)
	                            	{
	                            	this.bounds.expandToInclude(((NeoMultiPolygon)((Object[])node.getProperty("features"))[i]).getPoints()[j]);
	                                } 
	                            }
	                            
	                                  //  this.bounds.expandToInclude(coordinates.getDouble(0),
	                                         //   coordinates.getDouble(1));
	                        }
	                    }
	                }
	            } catch (Exception e) {
	                System.err.println("Failed to interpret BBOX: " + e.getMessage());
	                e.printStackTrace(System.err);
	            }
	        }
	        return bounds;
	    }

	 public final CoordinateReferenceSystem getCRS() {
	        return getCRS(DefaultGeographicCRS.WGS84,node);
	    }
	
	
}
