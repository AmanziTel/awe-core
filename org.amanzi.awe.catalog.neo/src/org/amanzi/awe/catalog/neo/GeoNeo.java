package org.amanzi.awe.catalog.neo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;
import org.neo4j.neoclipse.GeoNeoRelationshipTypes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This class is a utility class for reading GIS information from a specific
 * source within a Neo4J database. The GIS information is assummed to start with
 * a node known as the GIS node. This node contains the CRS information and a relationship
 * to the first of a graph of nodes representing GIS data. Each of these is expected to
 * have properties x/y or lat/long containing the location.
 * @author craig
 */
public class GeoNeo {
    private Node gisNode;   // the root of some specific GIS information in the Neo4j database
    private CoordinateReferenceSystem crs;
    private ReferencedEnvelope bounds;
    private String name;
    private org.neo4j.api.core.NeoService neo;
    
    /**
     * A class representing a located Node in the database. By convention all GeoNodes
     * are expected to contain properties for "type" and "name". In addition they should contain
     * a location or set of coordinates in one of the following formats:
     * <ul>
     * <li>"coords" => double[] of coordinates</li>
     * <li>"x" & "y" => two coords of type double</li>
     * <li>"lat" & "long" => two coords of type double</li>
     * <li>"lat" & "lon" => two coords of type double</li>
     * <li>"geom" => Geometry object in WKT or WKB format (currently unsupported)</li>
     * </ul>
     * 
     * @author craig
     * @since 1.0.0
     */
    public static class GeoNode {
        private double[] coords;
        private Node node;
        private Coordinate coordinate;
        private GeoNode(Node node){
            this.coords = getCoords(node);
            this.node = node;
        }
        public Coordinate getCoordinate() {
            if(coordinate==null) coordinate = new Coordinate(coords[0],coords[1]);
            return coordinate;
        }
        public double[] getCoords(){
            return coords;
        }
        public Node getNode(){
            return node;
        }
        public String getType(){
            return node.getProperty("type").toString();
        }
        public String getName(){
            return node.getProperty("name").toString();
        }
        public String toString(){
            return getName();
        }
        private static double[] getCoords(Node next) {
            if(next.hasProperty("coords")){
                return (double[])next.getProperty("coords");
            }
            if(next.hasProperty("x") && next.hasProperty("y")){
                return new double[]{(Float)next.getProperty("x"),(Float)next.getProperty("y")};
            }
            if(next.hasProperty("lat")){
                if(next.hasProperty("lon")){
                    return new double[]{(Float)next.getProperty("lon"),(Float)next.getProperty("lat")};
                }
                if(next.hasProperty("long")){
                    return new double[]{(Double)next.getProperty("long"),(Double)next.getProperty("lat")};
                }
            }
            return null;
        }
    }

    /**
     * Create a GeoNeo reader for loading GIS data from the specified GIS root node.
     * 
     * @param gisNode
     */
    public GeoNeo(org.neo4j.api.core.NeoService neo, Node gisNode){
        this.neo = neo;
        this.gisNode = gisNode;
        this.name = this.gisNode.getProperty("name").toString();
    }
    
    /**
     * Find the Coordinate Reference System in the GIS node, or default
     * to WGS84 if none found.
     * @return CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCRS(){
        return getCRS(DefaultGeographicCRS.WGS84);
    }

    /**
     * Find the Coordinate Reference System in the GIS node, or default
     * to the specified default if no CRS is found.
     * @return CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCRS(CoordinateReferenceSystem defaultCRS){
        if(crs==null){
            crs = defaultCRS; // default if crs cannot be found below
            try{
                if(gisNode.hasProperty("crs")){
                    // The simple approach is to name the CRS, eg. EPSG:4326 (GeoNeo spec prefers a new naming standard, but I'm not sure geotools knows it)
                    crs = CRS.decode(gisNode.getProperty("crs").toString());
                }else if(gisNode.hasProperty("crs_href")){
                    // TODO: This type is specified in GeoNeo spec, but what the HREF means is not, so we assume it is a live URL that will feed a CRS specification directly
                    URL crsURL = new URL(gisNode.getProperty("href").toString());
                    crs = CRS.decode(crsURL.getContent().toString());
                }
            }catch(Exception crs_e){
                System.err.println("Failed to interpret CRS: "+crs_e.getMessage());
                crs_e.printStackTrace(System.err);
            }
        }
        return crs;
    }

    public Traverser makeGeoNeoTraverser(){
        return gisNode.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE,
                GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
    }
    
    /**
     * Find the bounding box for the data set as a ReferenceEnvelope. It uses the getCRS method to
     * find the reference system then looks for explicit "bbox" elements, and finally, if no bbox
     * was found, scans all feature geometries for coordinates and builds the bounds on those. The
     * result is cached for future calls.
     * 
     * @return ReferencedEnvelope for bounding box
     */
    public ReferencedEnvelope getBounds(){
        if(bounds==null){
            // Create Null envelope
            this.bounds = new ReferencedEnvelope(getCRS());
            // First try to find the BBOX definition in the gisNode directly
            try{
                if(gisNode.hasProperty("bbox")){
                    double[] bbox = (double[])gisNode.getProperty("bbox");
                    this.bounds = new ReferencedEnvelope(bbox[0],bbox[1],bbox[2],bbox[3],crs);
                }else{
                    System.err.println("No BBox defined in the GeoNeo object");
                }
            }catch(Exception bbox_e){
                System.err.println("Failed to interpret BBOX: "+bbox_e.getMessage());
                bbox_e.printStackTrace(System.err);
            }
            // Secondly, if bounds is still empty, try find all feature geometries and calculate bounds
            try{
                if(this.bounds.isNull()){
                    // Try to create envelope from any data referenced by the gisNode
                    for(GeoNode node:getGeoNodes()){
                        //TODO: support high dimensions
                        this.bounds.expandToInclude(node.getCoords()[0], node.getCoords()[1]);
                    }
                }
            }catch(Exception bbox_e){
                System.err.println("Failed to interpret BBOX: "+bbox_e.getMessage());
                bbox_e.printStackTrace(System.err);
            }
        }
        return bounds;
    }

    /**
     * Return the name of the dataset as specified in the Neo, or default
     * to the URL.getFile().
     * @return dataset name
     */
    public String getName(){
        return name;
    }

    /**
     * Return a descriptive string of this dataset. This is based on
     * the name, crs and bounding box.
     * @return descriptive string
     */
    public String toString(){
        return "Neo["+getName()+"]: CRS:"+getCRS()+" Bounds:"+getBounds();
    }

    private class GeoIterator implements Iterator<GeoNode>{
        private Iterator<Node> iterator;
        private GeoNode next;
        private Transaction tx;
        private GeoIterator(Node gisNode){
            this.iterator = makeGeoNeoTraverser().iterator();
            this.tx = neo.beginTx();
        }
        @Override
        public boolean hasNext() {
            while(next==null){
                if(!iterator.hasNext()) break;
                next = new GeoNode(iterator.next());
                if(next.getCoords()==null) next = null;
            }
            if(next==null){
                tx.success();
                tx.finish();
            }
            return next!=null;
        }

        @Override
        public GeoNode next() {
            GeoNode toReturn = next;
            next = null;
            return toReturn;
        }
        @Override
        public void remove() {
            throw new RuntimeException("Unimplemented");
        }
    }

    public Iterable<GeoNode> getGeoNodes() {
        return new Iterable<GeoNode>(){
            @Override
            public Iterator<GeoNode> iterator() {
                return new GeoIterator(gisNode);
            }};
    }
}
