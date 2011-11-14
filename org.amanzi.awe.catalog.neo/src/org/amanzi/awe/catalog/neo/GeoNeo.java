/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe.catalog.neo;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.ui.IGraphModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * This class is a utility class for reading GIS information from a specific source within a Neo4J
 * database. The GIS information is assumed to start with a node known as the GIS node. This node
 * contains the CRS information and a relationship to the first of a graph of nodes representing GIS
 * data. Each of these is expected to have properties x/y or lat/long containing the location.
 * 
 * @author craig
 */
public class GeoNeo { 
//extends NeoServiceProviderListener {
    private static final Logger LOGGER = Logger.getLogger(GeoNeo.class);
    /** String NEIGH_RELATION field */
    public static final String NEIGH_RELATION = "NEIGH_RELATION";
    public static final String NEIGH_TYPE = "NEIGH_TYPE";

    /** String NEIGH_MAIN_NODE field */
    public static final String NEIGH_MAIN_NODE = "NEIGH_MAIN_NODE";

    /** GeoNeo NEIGH_NAME field */
    public static final String NEIGH_NAME = "NEIGH_NAME";

    /** GeoNeo DRIVE_INQUIRER field */
    public static final String DRIVE_INQUIRER = "DRIVE_INQUIRER";
    private final Node gisNode; // the root of some specific GIS information in the Neo4j database
    private CoordinateReferenceSystem crs;
    private ReferencedEnvelope bounds;
    private final String name;

    private final GisTypes types;
    private String propertyName;
    // private Integer propertyAdjacency;
    private Double minPropertyValue;
    private Double maxPropertyValue;
    private Set<Node> selectedNodes = new HashSet<Node>();
    private Double propertyValueMin;
    private Double propertyValueMax;
    private String distrName;
    private String selectName;
    private long count = 0;
    private String[] aggregatedProperties;
    Map<String, Object> properties = Collections.synchronizedMap(new HashMap<String, Object>());

    private Node aggrNode = null;
    private IGraphModel graphModel;

    /**
     * A class representing a located Node in the database. By convention all GeoNodes are expected
     * to contain properties for "type" and "name". In addition they should contain a location or
     * set of coordinates in one of the following formats:
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
        private final double[] coords;
        private final Node node;
        private Coordinate coordinate;

        public GeoNode(Node node) {
            this.coords = getCoords(node);
            this.node = node;
        }

        public Coordinate getCoordinate() {
            if (coordinate == null)
                if (coords == null) {
                    return null;
                }
            coordinate = new Coordinate(coords[0], coords[1]);
            return coordinate;
        }

        public double[] getCoords() {
            return coords;
        }

        public Node getNode() {
            return node;
        }

        public String getType() {
            return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString();
        }

        public String getName() {
            for (String key : new String[] {"name", "value", "time", "code"}) {
                Object nameObj = node.getProperty(key, null);
                if (nameObj != null)
                    return nameObj.toString();
            }
            return node.toString();
        }

        @Override
        public String toString() {
            return getName();
        }

        private static double[] getCoords(Node next) {
            double[] result = getCoordsFromNode(next);
            return result;
        }
        
        private static double[] internalGetCoordsFromNode(Node next) {
        	if (next.hasProperty(INeoConstants.PROPERTY_LAT_NAME)) {
                if (next.hasProperty(INeoConstants.PROPERTY_LON_NAME)) {
                    try {
                        return new double[] {(Float)next.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Float)next.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    } catch (ClassCastException e) {
                        return new double[] {(Double)next.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Double)next.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    }
                }
            }
        	
        	return null;  
        }

        private static double[] getCoordsFromNode(Node next) {
        	double[] result = internalGetCoordsFromNode(next);
        	
        	if (result != null) {
        		return result;
        	}
            
            //Lagutko, 15.05.2010, get coordinates from correlated node
            Relationship correlationLink = next.getSingleRelationship(CorrelationRelationshipTypes.CORRELATED_LOCATION, Direction.INCOMING);
            if (correlationLink == null) {
            	return null;
            }
            Node correlationNode = correlationLink.getStartNode();
            Node sectorNode = correlationNode.getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING).getEndNode();
            Node site = sectorNode.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
            
            return internalGetCoordsFromNode(site);
        }

    }

    /**
     * Create a GeoNeo reader for loading GIS data from the specified GIS root node.
     * 
     * @param gisNode
     */
    public GeoNeo(GraphDatabaseService neo, Node gisNode) {
//        this.graphDatabaseService = neo;
        this.gisNode = gisNode;
        this.name = this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        this.types = GisTypes.findGisTypeByHeader(this.gisNode.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME).toString());
        this.count = (Long)this.gisNode.getProperty("count", 0L);
    }

    /**
     * get child (depth=1) by necessary type
     * 
     * @param type type name
     * @return set of child's
     */
    public Set<Node> getChildByType(String type) {
        HashSet<Node> result = new HashSet<Node>();
        Iterable<Relationship> relations = gisNode.getRelationships(Direction.OUTGOING);
        for (Relationship relationship : relations) {
            Node node = relationship.getEndNode();
            if (type.equals(node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, ""))) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Find the Coordinate Reference System in the GIS node, or default to WGS84 if none found.
     * 
     * @return CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCRS() {
        return getCRS(DefaultGeographicCRS.WGS84);
    }

    /**
     * Find the Coordinate Reference System in the GIS node, or default to the specified default if
     * no CRS is found.
     * 
     * @return CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCRS(CoordinateReferenceSystem defaultCRS) {
        if (crs == null) {
            crs=NeoUtils.getCRS(gisNode, defaultCRS);
//            crs = defaultCRS; // default if crs cannot be found below
//            try {
//                if (gisNode.hasProperty(INeoConstants.PROPERTY_CRS_NAME)) {
//                    // The simple approach is to name the CRS, eg. EPSG:4326 (GeoNeo spec prefers a
//                    // new naming standard, but I'm not sure geotools knows it)
//                    crs = CRS.decode(gisNode.getProperty(INeoConstants.PROPERTY_CRS_NAME).toString());
//                } else if (gisNode.hasProperty(INeoConstants.PROPERTY_CRS_HREF_NAME)) {
//                    // TODO: This type is specified in GeoNeo spec, but what the HREF means is not,
//                    // so we assume it is a live URL that will feed a CRS specification directly
//                    // TODO: Lagutko: gisNode.hasProperty() has 'crs_href' as parameter, but
//                    // gisNode.getProperty() has only 'href'. What is right?
//                    URL crsURL = new URL(gisNode.getProperty(INeoConstants.PROPERTY_CRS_HREF_NAME).toString());
//                    crs = CRS.decode(crsURL.getContent().toString());
//                }
//            } catch (Exception crs_e) {
//                System.err.println("Failed to interpret CRS: " + crs_e.getMessage());
//                crs_e.printStackTrace(System.err);
//            }
        }
        return crs;
    }

    public Traverser makeGeoNeoTraverser(final Envelope searchBounds) {
        try {
//            MultiPropertyIndex<Double> index = new MultiPropertyIndex<Double>(graphDatabaseService, NeoUtils.getLocationIndexName(name),
//                    new String[] {INeoConstants.PROPERTY_LAT_NAME, INeoConstants.PROPERTY_LON_NAME},
//                    new MultiDoubleConverter(0.001),10);
//             return index.searchTraverser(new Double[] {searchBounds.getMinY(),
//             searchBounds.getMinX()}, new Double[] {
//             searchBounds.getMaxY(), searchBounds.getMaxX()});
            return null;

        } catch (Exception e) {
            LOGGER.debug("GeoNeo: Failed to search location index, doing exhaustive search: " + e);
            if (searchBounds == null) {
                return gisNode.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return NeoUtils.isDrivePointNode(currentPos.currentNode());
                    }
                }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.LOCATION,
                        Direction.OUTGOING);
            } else {
                return gisNode.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        if (!NeoUtils.isDrivePointNode(currentPos.currentNode())) {
                            return false;
                        }
                        double[] c = GeoNode.getCoords(currentPos.currentNode());
                        return c != null && searchBounds.contains(c[0], c[1]);
                    }
                }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING,
                        GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING);
            }
        }
    }
    
    /**
     * Sets the CRS.
     *
     * @param newCRS the new CRS
     */
    public void setCRS(final CoordinateReferenceSystem newCRS){
        Job job=new Job("setCRS"){

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoServiceProviderUi.getProvider().getService().beginTx();
                try{
                Node gis = getMainGisNode();
                String stringCRS = newCRS.getIdentifiers().iterator().next().toString();
                gis.setProperty(INeoConstants.PROPERTY_CRS_NAME, stringCRS);
                tx.success();
                crs=newCRS;
                bounds=null;
                }finally{
                    tx.finish();
                }
                return Status.OK_STATUS;
            }
            
        };
        job.schedule();
        try {
            job.join();
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
    /**
     * Find the bounding box for the data set as a ReferenceEnvelope. It uses the getCRS method to
     * find the reference system then looks for explicit "bbox" elements, and finally, if no bbox
     * was found, scans all feature geometries for coordinates and builds the bounds on those. The
     * result is cached for future calls.
     * 
     * @return ReferencedEnvelope for bounding box
     */
    
    public ReferencedEnvelope getBounds() {
        if (bounds == null) {
            // Create Null envelope
            this.bounds = new ReferencedEnvelope(getCRS());
            // First try to find the BBOX definition in the gisNode directly
            // Transaction tx = this.neo.beginTx();
            try {
                if (gisNode.hasProperty(INeoConstants.PROPERTY_BBOX_NAME)) {
                    double[] bbox = (double[])gisNode.getProperty(INeoConstants.PROPERTY_BBOX_NAME);
                    this.bounds = new ReferencedEnvelope(bbox[0], bbox[1], bbox[2], bbox[3], crs);
                } else {
                    System.err.println("No BBox defined in the GeoNeo object: " + this.name);
                }
                // tx.success();
            } catch (Exception bbox_e) {
                System.err.println("Failed to interpret BBOX: " + bbox_e.getMessage());
                bbox_e.printStackTrace(System.err);
            } finally {
                // tx.finish();
            }
            // Secondly, if bounds is still empty, try find all feature geometries and calculate
            // bounds
            if (this.bounds.isNull()) {
//                Transaction tx = this.graphDatabaseService.beginTx();
//                try {
//                    LOGGER.debug("Re-determining bounding box for gis data: " + this.name);
//                    // Try to create envelope from any data referenced by the gisNode
//                    for (GeoNode node : getGeoNodes(null)) {
//                        // TODO: support high dimensions
//                        this.bounds.expandToInclude(node.getCoords()[0], node.getCoords()[1]);
//                    }
//                    double bbox[] = new double[] {this.bounds.getMinX(), this.bounds.getMaxX(), this.bounds.getMinY(),
//                            this.bounds.getMaxY()};
//                    gisNode.setProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
//                    tx.success();
//                } catch (Exception bbox_e) {
//                    System.err.println("Failed to interpret BBOX: " + bbox_e.getMessage());
//                    bbox_e.printStackTrace(System.err);
//                } finally {
//                    tx.finish();
//                    NeoServiceProviderUi.getProvider().commit();
//                }
            }
            // LOGGER.debug("Determined bounding box for " + this.name + ": " + this.bounds);
            // throw new RuntimeException("Escape a deadlock");
        }
        return bounds;
    }

    /**
     * Return the name of the dataset as specified in the Neo, or default to the URL.getFile().
     * 
     * @return dataset name
     */
    public String getName() {
        return name;
    }

    /**
     * Return a descriptive string of this dataset. This is based on the name, crs and bounding box.
     * 
     * @return descriptive string
     */
    @Override
    public String toString() {
        return "Neo[" + getName() + "]: CRS:" + getCRS() + " Bounds:" + getBounds();
    }

    private class GeoIterator implements Iterator<GeoNode> {
        private final Iterator<Node> iterator;
        private GeoNode next;

        // private Transaction transaction;

        private GeoIterator(Node gisNode, Envelope bounds) {
            // this.transaction = neo.beginTx();
            this.iterator = makeGeoNeoTraverser(bounds).iterator();
        }

        public boolean hasNext() {
            while (next == null) {
                if (!iterator.hasNext())
                    break;
                next = new GeoNode(iterator.next());
                if (next.getCoords() == null)
                    next = null;
            }
            if (next == null) {
                // transaction.success();
                // transaction.finish();
            }
            return next != null;
        }

        public GeoNode next() {
            GeoNode toReturn = next;
            next = null;
            return toReturn;
        }

        public void remove() {
            throw new RuntimeException("Unimplemented");
        }
    }

    public Iterable<GeoNode> getGeoNodes(final Envelope searchBounds) {
        return new Iterable<GeoNode>() {
            public Iterator<GeoNode> iterator() {
                return new GeoIterator(gisNode, searchBounds);
            }
        };
    }

    /**
     * @return Returns the gis type of node.
     */
    public GisTypes getGisType() {
        return types;
    }

    /**
     * Sets or remove property for dawning
     * 
     * @param aggrNode aggregation chart node
     * @param propertyNode property node
     * @param adjacency - adjacency
     * @param maxSelNode
     * @param minSelNode
     * @param aggregatedProperties
     */
    public void setPropertyToRefresh(Node aggrNode, Node propertyNode, Node minSelNode, Node maxSelNode,
            Map<String, String[]> aggregatedProperties) {
        // TODO remove unusual fields and method signatures
        this.aggrNode = aggrNode;
        if (aggrNode != null && propertyNode != null) {
            distrName = (String)aggrNode.getProperty(INeoConstants.PROPERTY_DISTRIBUTE_NAME);
            selectName = (String)aggrNode.getProperty(INeoConstants.PROPERTY_SELECT_NAME, null);
            propertyName = (String)aggrNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
            propertyValueMin = (Double)propertyNode.getProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE);
            propertyValueMax = (Double)propertyNode.getProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE);
            minPropertyValue = (Double)minSelNode.getProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE);
            maxPropertyValue = (Double)maxSelNode.getProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE);
            if (propertyName != null) {
                this.aggregatedProperties = aggregatedProperties.get(propertyName);
            }
            // propertyAdjacency = adjacency;
        } else {
            propertyName = null;
            minPropertyValue = null;
            maxPropertyValue = null;
            this.aggregatedProperties = null;
            // propertyAdjacency = 0;
        }
    }

    /**
     * This node represents a recent statistics calculation that might be used for synchronized
     * colors between various views, for example the map might look to this node to determine the
     * colors of the sectors or drive points.
     * 
     * @return Returns the aggrNode.
     */
    public Node getAggrNode() {
        return aggrNode;
    }

    /**
     * @return Returns the minPropertyValue.
     */
    public Double getMinPropertyValue() {
        return minPropertyValue;
    }

    /**
     * @return Returns the maxPropertyValue.
     */
    public Double getMaxPropertyValue() {
        return maxPropertyValue;
    }

    /**
     * @return Returns the gisNode.
     */
    public Node getMainGisNode() {
        return gisNode;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the propertyValueMin.
     */
    public Double getPropertyValueMin() {
        return propertyValueMin;
    }

    /**
     * @return Returns the propertyValueMax.
     */
    public Double getPropertyValueMax() {
        return propertyValueMax;
    }

    /**
     * @param object
     */
    public void setSelectedNodes(Set<Node> selectedNodes) {
        if (selectedNodes == null) {
            this.selectedNodes = new HashSet<Node>();
        } else {
            this.selectedNodes = selectedNodes;
        }
    }

    /**
     * @param node
     */
    public void addNodeToSelect(Node node) {
        selectedNodes.add(node);
    }

    /**
     * @return Returns the selectedNodes.
     */
    public Set<Node> getSelectedNodes() {
        return selectedNodes;
    }

    /**
     * @return Returns the distrName.
     */
    public String getDistrName() {
        return distrName;
    }

    /**
     * @return Returns the selectName.
     */
    public String getSelectName() {
        return selectName;
    }

    public long getCount() {
        return this.count;
    }

    /**
     * @return Returns the aggregatedProperties.
     */
    public String[] getAggregatedProperties() {
        return aggregatedProperties;
    }

    /**
     * Return an arbitrary property from the representative gis node.
     */
    public Object getProperty(String key, Object defaultValue) {
        return gisNode.getProperty(key, defaultValue);
    }

    /**
     * Sets properties
     * 
     * @param properties
     */
    public void setProperties(HashMap<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public Object getProperties(String key) {
        return properties.get(key);
    }

    /**
     * Sets properties
     * 
     * @param key - property key
     * @param value - property value
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Sets the aggregation node
     * @param aggrNode The aggrNode to set.
     */
    public void setAggrNode(Node aggrNode) {
        this.aggrNode = aggrNode;
    }

    /**
     *
     * @return
     */
    public IGraphModel getGraphModel() {
        return graphModel;
    }

    public void setGraphModel(IGraphModel graphModel) {
        this.graphModel = graphModel;
    }


}
