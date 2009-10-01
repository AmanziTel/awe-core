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

package org.amanzi.neo.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Contains information of property
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class PropertyHeader {


    private final Node node;
    private boolean isGis;
    private GisTypes gisType;

    /**
     * Constructor
     * 
     * @param node - gis Node
     */
    public PropertyHeader(Node node) {
        isGis=NeoUtils.isGisNode(node);
            gisType=isGis?GisTypes.findGisTypeByHeader((String)node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME,null)):null;
        this.node = node;
    }

    /**
     * get Numeric Fields of Neighbour
     * 
     * @param neighbourName name of neighbour
     * @return array or null
     */
    public String[] getNeighbourNumericFields(String neighbourName) {
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = NeoUtils.getNumericFields(neighbour);
        return result;
    }

    /**
     * get Numeric Fields of current node
     * 
     * @return array or null
     */
    public String[] getNumericFields() {
        
        return isGis?(gisType==GisTypes.DRIVE?NeoUtils.getNumericFields(node):getDataVault().getNumericFields()):NeoUtils.getNumericFields(node);
    }

    /**
     * get data vault
     * 
     * @return data vault
     */
    public PropertyHeader getDataVault() {
        return isGis?new PropertyHeader(node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getOtherNode(node)):this;
    }

    /**
     * @return
     */
    public String[] getNetworkNumericFields() {
        // TODO refactored
        List<String> result = new ArrayList<String>();
        for (Relationship relation : node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING)) {
            String name = String.format("# '%s' neighbours", NeoUtils.getSimpleNodeName(relation.getOtherNode(node), ""));
            result.add(name);
        }
        return result.toArray(new String[0]);
    }
    
    public String[] getDefinedNumericFields() {
        List<String> ints = new ArrayList<String>();
        List<String> floats = new ArrayList<String>();
        List<String> result = new ArrayList<String>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if(propRel!=null){
            Node propNode = propRel.getEndNode();
            for(Node node: propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)){
                String propType = (String)node.getProperty("name", null);
                if(propType != null) {
                    if(propType.equals("integer")){
                        ints.addAll(Arrays.asList(node.getProperty("properties","").toString().split("[\\n\\,]")));
                    } else
                    if(propType.equals("float")){
                        floats.addAll(Arrays.asList(node.getProperty("properties","").toString().split("[\\n\\,]")));
                    }
                }
            }
        }
        result.addAll(ints);
        result.addAll(floats);
        return result.toArray(new String[0]);
    }

    /**
     * Get network vault
     * 
     * @param gisNode - gis node
     * @return network vault
     */
    public static PropertyHeader getNetworkVault(Node gisNode) {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Relationship singleRelationship = gisNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            if (singleRelationship == null) {
                return null;
            }
            return new PropertyHeader(singleRelationship.getOtherNode(gisNode));
        } finally {
            tx.finish();
        }
    }

    /**
     * get Azimuth
     * 
     * @param node node, that contains information
     * @return Azimuth
     */
    public Double getAzimuth(Node node){
        String[] azimuthList = getAzimuthList();// use cache?
        for (String property : azimuthList) {
            if (node.hasProperty(property)){
                return ((Number) node.getProperty(property)).doubleValue();
            }
        }
        return null;
    }

    /**
     * get list of properties
     * 
     * @return azimuth properties
     */
    public String[] getAzimuthList() {
        return (String[])node.getProperty(INeoConstants.PROPERTY_AZIMUT_NAME, null);
    }

    /**
     * Get beamwidth
     * 
     * @param child node
     * @param defValue default value
     * @return beamwidth
     */
    public double getBeamwidth(Node child, Double defValue) {
        String[] beamwidt = getBeamwidthList();// TODO use cache?
        if (beamwidt == null) {
            return defValue;
        }
        for (String property : beamwidt) {
            if (child.hasProperty(property)) {
                return ((Number)child.getProperty(property)).doubleValue();
            }
        }
        return defValue;
    }

    /**
     * get list of properties
     * 
     * @return Beamwidth properties
     */
    public String[] getBeamwidthList() {
        return (String[])node.getProperty(INeoConstants.PROPERTY_BEAMWIDTH_NAME, null);
    }

    /**
     * get list of properties
     * 
     * @return AllChannels properties
     */
    public String[] getAllChannels() {
        return isGis?getDataVault().getAllChannels():(String[])node.getProperty(INeoConstants.PROPERTY_ALL_CHANNELS_NAME, null);
    }

    /**
     * gets list of Neighbour properties in network
     * 
     * @return Collection
     */
    public Collection<String> getNeighbourList() {
        List<String> result = new ArrayList<String>();
        if (!isGis || gisType == GisTypes.DRIVE) {
            return result;
        }
        Iterable<Relationship> neighb = node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING);
        for (Relationship relationship : neighb) {
            result.add(NeoUtils.getNeighbourPropertyName(NeoUtils.getSimpleNodeName(relationship.getOtherNode(node), "")));
        }
        return result;
    }
}
