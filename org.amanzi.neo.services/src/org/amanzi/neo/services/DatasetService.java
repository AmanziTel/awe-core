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

package org.amanzi.neo.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.internal.DynamicNodeType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.Predicate;

import com.vividsolutions.jts.util.Assert;

/**
 * <p>
 * Service provide common operations with datasets
 * </p>
 * .
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class DatasetService extends AbstractService {

    /**
     * Gets the root node.
     * 
     * @param projectName the project name
     * @param datasetName the dataset name
     * @param rootType the root type
     * @return the root node
     */
    public Node getRootNode(String projectName, String datasetName, INodeType rootType) {
        return getRootNode(projectName, datasetName, rootType.getId());
    }

    /**
     * Gets the root node.
     * 
     * @param projectName the project name
     * @param datasetName the dataset name
     * @param rootTypeId the root type id
     * @return the root node
     */
    protected Node getRootNode(String projectName, String datasetName, String rootTypeId) {
        Node datasetNode = findRoot(projectName, datasetName);
        if (datasetNode != null) {
            if (!rootTypeId.equals(getNodeType(datasetNode))) {
                throw new IllegalArgumentException(String.format("Wrong types of found node. Expected type: %s, real type: %s", rootTypeId, datasetNode));
            }
        }
        if (datasetNode == null) {
            datasetNode = addSimpleChild(findOrCreateAweProject(projectName), rootTypeId, datasetName);
        }
        return datasetNode;
    }

    /**
     * Adds the simple child.
     * 
     * @param parent the parent
     * @param type the type
     * @param name the name
     * @return the node
     */
    public Node addSimpleChild(Node parent, INodeType type, String name) {
        return addSimpleChild(parent, type.getId(), name);
    }

    /**
     * Adds the simple child.
     * 
     * @param parent the parent
     * @param typeId the type id
     * @param name the name
     * @return the node
     */
    protected Node addSimpleChild(Node parent, String typeId, String name) {
        Transaction tx = databaseService.beginTx();
        try {
            Node child = databaseService.createNode();
            child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, typeId);
            child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            tx.success();
            return child;
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets the node type.
     * 
     * @param node the node
     * @return the node type
     */
    public INodeType getNodeType(Node node) {
        String typeId = getType(node);
        return getNodeType(typeId);
    }

    /**
     * Gets the node type.
     * 
     * @param typeId the type id
     * @return the node type
     */
    public INodeType getNodeType(String typeId) {
        if (typeId == null) {
            return null;
        }
        INodeType result = NodeTypes.getEnumById(typeId);
        if (result == null) {
            result = getDynamicNodeType(typeId);
        }
        return result;
    }

    /**
     * Gets the dynamic node type.
     * 
     * @param type the type
     * @return the dynamic node type
     */
    private INodeType getDynamicNodeType(String type) {
        return new DynamicNodeType(type);
    }

    /**
     * Gets the type.
     * 
     * @param node the node
     * @return the type
     */
    protected String getType(Node node) {
        return (String)node.getProperty("type", null);
    }

    /**
     * Sets the type.
     * 
     * @param node the node
     * @param typeId the type
     */
    protected void setType(Node node, String typeId) {
        node.setProperty("type", typeId);
    }

    /**
     * Gets the file node.
     * 
     * @param datasetNode the dataset node
     * @param fileName the file name
     * @return the file node
     */
    public Node getFileNode(Node datasetNode, String fileName) {
        Node fileNode = findFileNode(datasetNode, fileName);

        if (fileNode == null) {
            fileNode = databaseService.createNode();
            fileNode.setProperty("type", "file");
            fileNode.setProperty("name", fileName);

            datasetNode.createRelationshipTo(fileNode, DatasetRelationshipTypes.CHILD);
        }

        return fileNode;
    }

    /**
     * Find file node.
     * 
     * @param datasetNode the dataset node
     * @param fileName the file name
     * @return the node
     */
    public Node findFileNode(Node datasetNode, String fileName) {
        Iterable<Relationship> relationships = datasetNode.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING);
        for (Relationship singleRel : relationships) {
            Node endNode = singleRel.getEndNode();
            if (endNode.getProperty("type").equals("file") && endNode.getProperty("name").equals(fileName)) {
                return endNode;
            }
        }

        return null;
    }

    /**
     * Creates the m node.
     * 
     * @param parentNode the parent node
     * @param lastChild the last child
     * @return the node
     */
    public Node createMNode(Node parentNode, Node lastChild) {
        // TODO: throw exception if parent and last child are null

        Node mNode = databaseService.createNode();
        mNode.setProperty("type", "m");

        if (lastChild == null) {
            parentNode.createRelationshipTo(mNode, DatasetRelationshipTypes.CHILD);
        } else {
            lastChild.createRelationshipTo(mNode, DatasetRelationshipTypes.NEXT);
        }

        return mNode;
    }

    /**
     * Creates the m node.
     * 
     * @param parentNode the parent node
     * @param lastChild the last child
     * @param indexInfo the index info
     * @return the node
     */
    public Node createMNode(Node parentNode, Node lastChild, HashMap<String, Object> indexInfo) {
        Node mNode = this.createMNode(parentNode, lastChild);

        if (indexInfo != null) {
            for (String indexName : indexInfo.keySet()) {
                getIndexService().index(mNode, indexName, indexInfo.get(indexName));
            }
        }

        return mNode;
    }

    /**
     * Gets the last node in file.
     * 
     * @param fileNode the file node
     * @return the last node in file
     */
    public Node getLastNodeInFile(Node fileNode) {
        Long id = (Long)fileNode.getProperty("lastNodeId", null);
        if ((id != null) && (id != -1)) {
            return databaseService.getNodeById(id);
        }
        return null;
    }

    /**
     * Creates the mp node.
     * 
     * @param mNode the m node
     * @return the node
     */
    public Node createMPNode(Node mNode) {
        Node mpNode = databaseService.createNode();
        mpNode.setProperty("type", "mp");

        mNode.createRelationshipTo(mpNode, DatasetRelationshipTypes.LOCATION);

        return mpNode;
    }

    /**
     * Gets the gpeh statistics.
     * 
     * @param datasetNode the dataset node
     * @return the gpeh statistics
     */
    public GpehStatisticModel getGpehStatistics(Node datasetNode) {
        // TODO add synchroniza and multi instance
        if (datasetNode.hasRelationship(DatasetRelationshipTypes.GPEH_STATISTICS, Direction.OUTGOING)) {
            Node statNode = datasetNode.getSingleRelationship(DatasetRelationshipTypes.GPEH_STATISTICS, Direction.OUTGOING).getEndNode();
            return new GpehStatisticModel(datasetNode, statNode, databaseService);
        } else {
            Node statNode = databaseService.createNode();
            datasetNode.createRelationshipTo(statNode, DatasetRelationshipTypes.GPEH_STATISTICS);
            return new GpehStatisticModel(datasetNode, statNode, databaseService);
        }
    }

    /**
     * _test.
     */
    public void _test() {
        Node node = databaseService.createNode();

        node.setProperty("ss", 2);
        Node node2 = databaseService.createNode();
        node2.setProperty("ss", 2);
        org.neo4j.graphdb.RelationshipType rel = new org.neo4j.graphdb.RelationshipType() {

            @Override
            public String name() {
                return "sss";
            }
        };
        node.createRelationshipTo(node2, rel);
        node = databaseService.createNode();

        node.setProperty("ss", 2);
        node2 = databaseService.createNode();
        node2.setProperty("ss", 2);
        rel = new org.neo4j.graphdb.RelationshipType() {

            @Override
            public String name() {
                return "sss3";
            }
        };
        node.createRelationshipTo(node2, rel);
    }

    /**
     * Find root node by name.
     * 
     * @param projectName the project name
     * @param rootname the rootname
     * @return the node
     */
    public Node findRoot(final String projectName, final String rootname) {
        TraversalDescription td = NeoUtils.getTDRootNodes(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                return rootname.equals(paramT.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, ""))
                        && projectName.equals(paramT.lastRelationship().getStartNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, ""));
            }
        });
        Iterator<Node> it = td.traverse(databaseService.getReferenceNode()).nodes().iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Adds the data node to project.
     * 
     * @param aweProjectName the awe project name
     * @param childNode the child node
     */
    public void addDataNodeToProject(String aweProjectName, Node childNode) {

        for (Relationship rel : childNode.getRelationships(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING)) {
            if (NodeTypes.AWE_PROJECT.checkNode(rel.getOtherNode(childNode))) {
                return;
            }
        }
        Transaction transacation = databaseService.beginTx();
        try {
            Node project = findOrCreateAweProject(aweProjectName);
            project.createRelationshipTo(childNode, GeoNeoRelationshipTypes.CHILD);
            transacation.success();
        } finally {
            transacation.finish();
        }
    }

    /**
     * Find or create awe project.
     * 
     * @param aweProjectName the awe project name
     * @return the node
     */
    public Node findOrCreateAweProject(String aweProjectName) {
        Node result = null;
        // Lagutko, 13.08.2009, use findAweProject() method to find an AWEProjectNode
        result = findAweProject(aweProjectName);
        if (result == null) {
            result = createEmptyAweProject(aweProjectName);
        }
        return result;
    }

    /**
     * Creates the empty awe project.
     * 
     * @param projectName the project name
     * @return the node
     */
    private Node createEmptyAweProject(String projectName) {
        Transaction transaction = databaseService.beginTx();
        try {
            Node result = databaseService.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.AWE_PROJECT.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, projectName);
            databaseService.getReferenceNode().createRelationshipTo(result, SplashRelationshipTypes.AWE_PROJECT);
            transaction.success();
            return result;
        } finally {
            transaction.finish();
        }

    }

    /**
     * Find awe project.
     * 
     * @param aweProjectName the awe project name
     * @return the node
     */
    public Node findAweProject(final String aweProjectName) {
        Iterator<Node> it = NeoUtils.getTDProjectNodes(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                return aweProjectName.equals(paramT.endNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, ""));
            }
        }).traverse(databaseService.getReferenceNode()).nodes().iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Creates the root node.
     * 
     * @param projectName the project name
     * @param rootname the rootname
     * @param rootNodeType the root node type
     * @return the node
     */
    public Node createRootNode(String projectName, String rootname, String rootNodeType) {
        Transaction tx = databaseService.beginTx();
        try {
            Node result = databaseService.createNode();
            NeoUtils.setNodeName(result, rootname, null);
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, rootNodeType);
            addDataNodeToProject(projectName, result);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * Find gis node.
     * 
     * @param rootNode the root node
     * @param createNew the create new
     * @return the node
     */
    public Node findGisNode(Node rootNode, boolean createNew) {
        Relationship rel = rootNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
        if (rel != null) {
            return rel.getOtherNode(rootNode);
        } else if (createNew) {
            Transaction tx = databaseService.beginTx();
            try {
                Node result = databaseService.createNode();
                NeoUtils.setNodeName(result, NeoUtils.getSimpleNodeName(rootNode, "", databaseService), databaseService);
                result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.GIS.getId());
                result.setProperty(INeoConstants.PROPERTY_NAME_NAME, rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, ""));
                GisTypes gisType = GisTypes.getGisTypeFromRootType((String)rootNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME));
                result.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, gisType.getHeader());
                result.createRelationshipTo(rootNode, GeoNeoRelationshipTypes.NEXT);
                databaseService.getReferenceNode().createRelationshipTo(result, NetworkRelationshipTypes.CHILD);
                tx.success();
                return result;
            } finally {
                tx.finish();
            }
        }
        return null;
    }

    /**
     * Index by property.
     * 
     * @param rootId the root id
     * @param node the node
     * @param propertyName the property name
     */
    public void indexByProperty(long rootId, Node node, String propertyName) {
        Assert.isTrue(node.hasProperty(propertyName));
        Transaction tx = databaseService.beginTx();
        try {
            String type = getType(node);
            String indexName = new StringBuilder("Id").append(rootId).append("@").append(type).append("@").append(propertyName).toString();
            getIndexService().index(node, indexName, node.getProperty(propertyName));
            tx.success();
        } finally {
            tx.finish();
        }
    }
    /**
     * Gets the location index property.
     *
     * @param rootname the rootname
     * @return the location index property
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public  MultiPropertyIndex< ? > getLocationIndexProperty(String rootname) throws IOException {
        return new MultiPropertyIndex<Double>(NeoUtils.getLocationIndexName(rootname), new String[] {INeoConstants.PROPERTY_LAT_NAME, INeoConstants.PROPERTY_LON_NAME},
                new org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiDoubleConverter(0.001), 10);
    }
    public  MultiPropertyIndex<Long> getTimeIndexProperty(String name) throws IOException {
        return new MultiPropertyIndex<Long>(NeoUtils.getTimeIndexName(name), new String[] {INeoConstants.PROPERTY_TIMESTAMP_NAME}, new org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiTimeIndexConverter(), 10);
    }
}
