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

import java.util.HashMap;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class DatasetService extends AbstractService {
    
    public enum DatasetType {
        NETWORK("network"), 
        DRIVE("dataset");
        
        private String propertyValue;
        
        private DatasetType(String propertyValue) {
            this.propertyValue = propertyValue;
        }
        
        public String getPropertyValue() {
            return propertyValue;
        }
    }
    
    public Node getProjectNode(String projectName, boolean canExists) {
        Node projectNode = null;
        if (canExists) {
            projectNode = findProjectNode(projectName);
        }
        if (projectNode == null) {
            projectNode = databaseService.createNode();
            projectNode.setProperty("name", projectName);
            projectNode.setProperty("type", "awe_project");
            
            databaseService.getReferenceNode().createRelationshipTo(projectNode, DatasetRelationshipTypes.AWE_PROJECT);
        }
        
        return projectNode;
    }
    
    public Node findProjectNode(String projectName) {
        Node referenceNode = databaseService.getReferenceNode();
        
        Iterable<Relationship> relationships = referenceNode.getRelationships(DatasetRelationshipTypes.AWE_PROJECT, Direction.OUTGOING);
        for (Relationship singleRel : relationships) {
            Node endNode = singleRel.getEndNode();
            if (endNode.getProperty("type").equals("awe_project") &&
                endNode.getProperty("name").equals(projectName)) {
                return endNode;
            }
        }
        
        return null;
    }
    
    public Node getDatasetNode(Node projectNode, String datasetName, DatasetType datasetType, boolean canExists) {
        Node datasetNode = null;
        if (canExists) {
            datasetNode = findDatasetNode(projectNode, datasetName, datasetType);
        }
        if (datasetNode == null) {
            datasetNode = databaseService.createNode();
            datasetNode.setProperty("type", datasetType.getPropertyValue());
            datasetNode.setProperty("name", datasetName);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationshipTypes.CHILD);
        }
        return datasetNode;
    }
    
    public Node findDatasetNode(Node projectNode, String datasetName, DatasetType datasetType) {
        Iterable<Relationship> relationships = projectNode.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING);
        for (Relationship singleRel : relationships) {
            Node endNode = singleRel.getEndNode();
            if (endNode.getProperty("name").equals(datasetName)) {
                if (!endNode.getProperty("type").equals(datasetType.getPropertyValue())){
                    //TODO add description
                    throw new IllegalArgumentException();
                }
                return endNode;
            }
        }
        
        return null;
    }
    
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
    
    public Node findFileNode(Node datasetNode, String fileName) {
        Iterable<Relationship> relationships = datasetNode.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING);
        for (Relationship singleRel : relationships) {
            Node endNode = singleRel.getEndNode();
            if (endNode.getProperty("type").equals("file") &&
                endNode.getProperty("name").equals(fileName)) {
                return endNode;
            }
        }
        
        return null;
    }
    
    public Node createMNode(Node parentNode, Node lastChild) {
        //TODO: throw exception if parent and last child are null
        
        Node mNode = databaseService.createNode();
        mNode.setProperty("type", "m");
        
        if (lastChild == null) {
            parentNode.createRelationshipTo(mNode, DatasetRelationshipTypes.CHILD);
        }
        else {
            lastChild.createRelationshipTo(mNode, DatasetRelationshipTypes.NEXT);
        }
        
        return mNode;
    }
    
    public Node createMNode(Node parentNode, Node lastChild, HashMap<String, Object> indexInfo) {
        Node mNode = this.createMNode(parentNode, lastChild);
        
        if (indexInfo != null) {
            for (String indexName : indexInfo.keySet()) {
                getIndexService().index(mNode, indexName, indexInfo.get(indexName));
            }
        }
        
        return mNode;
    }
    
    public Node getLastNodeInFile(Node fileNode) {
        Long id = (Long)fileNode.getProperty("lastNodeId", null);
        if ((id != null) && (id != -1)) {
            return databaseService.getNodeById(id);
        }
        return null;
    }
    
    public Node createMPNode(Node mNode) {
        Node mpNode = databaseService.createNode();
        mpNode.setProperty("type", "mp");
        
        mNode.createRelationshipTo(mpNode, DatasetRelationshipTypes.LOCATION);
        
        return mpNode;
    }
    
    public Iterator<Node> getAllDatasets(Node projectNode, final String type) {
        if (projectNode == null) {
            projectNode = getProjectNode("project", true);
        }
        Iterator<Relationship> relationships = projectNode.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        
        return new NodeIterator(relationships) {
            
            @Override
            protected boolean checkNode(Node node) {
                return node.getProperty("drive_type").equals(type);
            }
        };
    }
    
    private abstract class NodeIterator implements Iterator<Node> {
        
        private Iterator<Relationship> relationships;
        
        public NodeIterator(Iterator<Relationship> relationships) {
            this.relationships = relationships;
        }

        @Override
        public boolean hasNext() {
            return relationships.hasNext();
        }

        @Override
        public Node next() {
            Node result = null;
            do {
                result = relationships.next().getEndNode();
            }
            while (checkNode(result));
            return result;
        }
        
        protected abstract boolean checkNode(Node node);

        @Override
        public void remove() {
        }
        
    }


    /**
     * Gets the gpeh statistics.
     *
     * @param datasetNode the dataset node
     * @return the gpeh statistics
     */
    public GpehStatisticModel getGpehStatistics(Node datasetNode) {
        //TODO add synchroniza and multi instance
        if (datasetNode.hasRelationship(DatasetRelationshipTypes.GPEH_STATISTICS, Direction.OUTGOING)){
            Node statNode = datasetNode.getSingleRelationship(DatasetRelationshipTypes.GPEH_STATISTICS, Direction.OUTGOING).getEndNode();
            return new GpehStatisticModel(datasetNode,statNode,databaseService);
        }else{
            Node statNode = databaseService.createNode();
            datasetNode.createRelationshipTo(statNode, DatasetRelationshipTypes.GPEH_STATISTICS);
            return new GpehStatisticModel(datasetNode,statNode,databaseService);
        }
    }

    /**
     *
     */
    public void _test() {
        Node node = databaseService.createNode();
        
        node.setProperty("ss",2);
        Node node2 = databaseService.createNode();
        node2.setProperty("ss",2);
        org.neo4j.graphdb.RelationshipType rel =new org.neo4j.graphdb.RelationshipType() {
            
            @Override
            public String name() {
                return "sss";
            }
        };
        node.createRelationshipTo(node2, rel );
         node = databaseService.createNode();
        
         node.setProperty("ss",2);
         node2 = databaseService.createNode();
         node2.setProperty("ss",2);
         rel =new org.neo4j.graphdb.RelationshipType() {
            
            @Override
            public String name() {
                return "sss3";
            }
        };
        node.createRelationshipTo(node2, rel );
    }


    /**
     * Find root node by name.
     * 
     * @param projectName the project name
     * @param rootName the root name
     * @param service the service
     * @return the node
     */
    public Node findRoot(String projectName, String rootname) {
        return NeoUtils.findRootNodeByName(projectName, rootname, databaseService);
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
            NeoCorePlugin.getDefault().getProjectService().addDataNodeToProject(projectName, result);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }


    public Node findGisNode(Node rootNode, boolean createNew) {
        Relationship rel = rootNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT,Direction.INCOMING);
        //TODO implement
        return null;
    }

}
