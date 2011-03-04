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

package org.amanzi.awe.statistics.database.entity;

import org.amanzi.awe.statistics.exceptions.IncorrectInputException;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Class that represents the dataset statistics
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class DatasetStatistics {
    private Node node;
    private Dimension networkDimension;
    private Dimension timeDimension;
    public static final String NETWORK_DIMENSION_NAME = "network";
    public static final String TIME_DIMENSION_NAME = "time";

    public DatasetStatistics(Node node) {
        this.node = node;
    }

    Node getNode() {
        return node;
    }

    public Node getDataset() {
        Relationship rel = node.getSingleRelationship(GeoNeoRelationshipTypes.ANALYSIS, Direction.INCOMING);
        if (rel != null) {
            return rel.getStartNode();
        }
        return null;
    }

    public void setDataset(Node dataset) {
        dataset.createRelationshipTo(node, GeoNeoRelationshipTypes.ANALYSIS);
    }

    public void addDimension(Dimension dimension) {
        String name = dimension.getName();
        if (NETWORK_DIMENSION_NAME.equals(name)) {
            networkDimension = dimension;
            node.createRelationshipTo(dimension.getNode(), GeoNeoRelationshipTypes.CHILD);
        } else if (TIME_DIMENSION_NAME.equals(name)) {
            timeDimension = dimension;
            node.createRelationshipTo(dimension.getNode(), GeoNeoRelationshipTypes.CHILD);
        } else
            throw new IncorrectInputException("Dimension " + name + " is not supported!");

    }

    public Dimension getNetworkDimension() {
        if (networkDimension == null) {
            loadDimensions();
        }
        return networkDimension;
    }

    private void loadDimensions() {
        for (Relationship rel : node.getRelationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
            Node dimensNode = rel.getEndNode();
            String name = dimensNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            if (NETWORK_DIMENSION_NAME.equals(name)) {
                networkDimension = new Dimension(dimensNode);
            } else if (TIME_DIMENSION_NAME.equals(name)) {
                timeDimension = new Dimension(dimensNode);
            }
        }
    }

    public Dimension getTimeDimension() {
        if (timeDimension == null) {
            loadDimensions();
        }
        return timeDimension;
    }

    public void setTemplateName(String name) {
        node.setProperty(INeoConstants.PROPERTY_TEMPLATE_NAME, name);
    }

    public String getTemplateName() {
        return node.getProperty("template").toString();
    }
}
