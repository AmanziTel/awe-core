package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

public enum AfpNodeTypes implements INodeType {
    AFP("afp"), AFP_SF("afp_scaling_factor");

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    private AfpNodeTypes(String id) {
        this.id = id;
    }

    private String id;

    /**
     * Check node by type
     * 
     * @param currentNode - node
     * @return true if node type
     */
    public boolean checkNode(Node currentNode) {
        return getId().equals(Utils.getNodeType(currentNode, ""));
    }

    /**
     * save type in node
     * 
     * @param container PropertyContainer
     * @param service - neoservice. if null then new transaction not created
     */
    public void setNodeType(PropertyContainer container, GraphDatabaseService service) {
        Transaction tx = Utils.beginTx(service);
        try {
            container.setProperty(INeoConstants.PROPERTY_TYPE_NAME, getId());
            Utils.successTx(tx);
        } finally {
            Utils.finishTx(tx);
        }
    }
}
