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
package org.amanzi.neo.services.node2node;

import java.util.Iterator;
import java.util.Set;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * <p>
 * Service Node to node relation
 * </p>
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NodeToNodeRelationService extends AbstractService {
    private static final String N2N_TYPE = "node2node";
    private static final String COUNT_PROXY = "count_proxy";
    private static final String COUNT_RELATION = "count_rel";

    private enum NodeToNodeRelationshipTypes implements RelationshipType {
        SET_TO_ROOT, PROXYS;
    }

    private DatasetService datasetService;

    /**
     * constructor
     */
    public NodeToNodeRelationService() {
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
    }

    /**
     * find root
     * 
     * @param rootNode root node
     * @param type type of relation
     * @param nameRelation name of relation
     * @return finded node
     */
    public Node findNodeToNodeRelationsRoot(Node rootNode, INodeToNodeType type, final String nameRelation) {
        final String typename = type.name();
        Traverser traverser = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst()
                .relationships(NodeToNodeRelationshipTypes.SET_TO_ROOT, Direction.OUTGOING).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean continues = arg0.length() == 0;
                        boolean includes = !continues && typename.equals(getINodeToNodeType(arg0.endNode()));
                        return Evaluation.of(includes, continues);
                    }
                }).traverse(rootNode);

        Iterator<Node> nodes = traverser.nodes().iterator();
        return nodes.hasNext() ? nodes.next() : null;
    }

    public String getINodeToNodeType(Node node) {
        return (String)node.getProperty(N2N_TYPE, null);
    }

    /**
     * method to create root
     * 
     * @param rootNode root node
     * @param type type of relation
     * @param name name of created node
     * @return
     */
    private Node createNodeToNodeRelationsRoot(Node rootNode, INodeToNodeType type, String name) {
        Transaction tx = databaseService.beginTx();

        try {
            Node createdNode = datasetService.createNode(NodeTypes.ROOT_PROXY, name);
            rootNode.createRelationshipTo(createdNode, NodeToNodeRelationshipTypes.SET_TO_ROOT);
            tx.success();

            return createdNode;
        } finally {
            tx.finish();
        }
    }

    /**
     * find or create node
     * 
     * @param rootNode root node
     * @param type type of relation
     * @param name name of node
     * @return finded or created node
     */
    public Node getNodeToNodeRelationsRoot(Node rootNode, INodeToNodeType type, String name) {
        Node returnedNode = findNodeToNodeRelationsRoot(rootNode, type, name);

        if (returnedNode == null) {
            returnedNode = createNodeToNodeRelationsRoot(rootNode, type, name);
        }

        return returnedNode;
    }

    /**
     * find proxy node
     * 
     * @param indexKey key of index
     * @param indexValue value of index
     * @return finded node
     */
    public Node findProxy(String indexKey, String indexValue) {
        return getIndexService().getSingleNode(indexKey, indexValue);
    }

    public Node getProxy(Node rootNode, String proxyIndexKey, Node node) {
        String name = String.valueOf(node.getId());// datasetService.getNodeName(node);
        Node result = findProxy(proxyIndexKey, name);
        if (result == null) {
            result = createProxy(node, proxyIndexKey, name, rootNode);
        }
        return result;
    }

    /**
     * create proxy node
     * 
     * @param index index of node
     * @param name name of node
     * @param rootNode root node
     * @param lastChild last child node
     * @return
     */
    public Node createProxy(Node originalNode, String index, String name, Node rootNode) {
        Transaction tx = databaseService.beginTx();
        try {
            Node node = datasetService.createNode(NodeTypes.PROXY, datasetService.getNodeName(originalNode));
            datasetService.addChild(rootNode, node, null);
            rootNode.setProperty(COUNT_PROXY, getCountProxy(rootNode) + 1);
            getIndexService().index(node, index, name);
            originalNode.createRelationshipTo(node, DatasetRelationshipTypes.PROXY);
            tx.success();
            return node;
        } finally {
            tx.finish();
        }
    }

    /**
     * Clears Node2Node relations model
     * 
     * @param rootNode root node of structure
     * @param indexKeys used names of indexes
     * @param deleteRootNode is it need to delete root node of model
     */
    public void clearNodeToNodeStructure(Node rootNode, String[] indexKeys, boolean deleteRootNode) {
        Iterator<Node> proxyNodes = datasetService.getChildTraversal(null).traverse(rootNode).nodes().iterator();
        while (proxyNodes.hasNext()) {
            Node proxy = proxyNodes.next();

            Iterator<Relationship> relationIterator = proxy.getRelationships().iterator();
            while (relationIterator.hasNext()) {
                relationIterator.next().delete();
            }

            proxy.delete();
        }

        for (String singleIndex : indexKeys) {
            getIndexService().removeIndex(singleIndex);
        }

        if (deleteRootNode) {
            rootNode.delete();
        }
    }

    public String getIndexKeyForRelation(Node rootNode, RelationshipType relationshipType) {
        return new StringBuilder("Id").append(rootNode.getId()).append("@").append(relationshipType.name()).toString();
    }

    public long getCountProxy(Node rootNode) {
        return (Long)rootNode.getProperty(COUNT_PROXY, 0l);
    }

    public long getCountRelation(Node rootNode) {
        return (Long)rootNode.getProperty(COUNT_RELATION, 0l);
    }

    public Relationship getRelation(Node rootNode, String proxyIndexKey, Node servingNode, Node dependentNode) {
        Node proxyServ=getProxy(rootNode, proxyIndexKey, servingNode);
        Node neigh=getProxy(rootNode, proxyIndexKey, dependentNode);
        Set<Relationship> relations = Utils.getRelations(proxyServ, neigh, NodeToNodeRelationshipTypes.PROXYS);
        if (relations.isEmpty()){
            Transaction tx = databaseService.beginTx();
            try{
                Relationship rel = proxyServ.createRelationshipTo(neigh, NodeToNodeRelationshipTypes.PROXYS);
                rootNode.setProperty(COUNT_RELATION, getCountRelation(rootNode)+1);
                tx.success();
                return rel;
            }finally{
                tx.finish();
            }
        }else{
            return relations.iterator().next();
        }
    }
}
