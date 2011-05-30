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

package org.amanzi.neo.services.networkModel;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class StringDistributionModel implements IDistributionModel {
    private IDistributionalModel model;

    private final NetworkService networkService;
    private String propertyName;
    private INodeType type;
    private Node returnableNode;

    public StringDistributionModel(String propertyName, INodeType type, IDistributionalModel model) {
        // System.out.println(this.getClass().getName());
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        this.model = model;
        this.propertyName = propertyName;
        this.type = type;
        setRootDistributionNode();

    }

    private void setRootDistributionNode() {
        if (searchModel()) {
            returnableNode = networkService.createNode(getType(), getModelName());
            model.getRootNode().createRelationshipTo(returnableNode, NetworkRelationshipTypes.CHILD);
        }

    }

    private boolean searchModel() {
        Evaluator ev = new PropertyEvaluator(propertyName, getModelName());
        Iterable<Node> networkNodes = networkService.getNetworkElementTraversal(ev, getType()).traverse(model.getRootNode())
                .nodes();

        if (!networkNodes.iterator().hasNext()
                && !model.getRootNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(getType())) {
            return true;
        }
        returnableNode = networkNodes.iterator().next();
        return false;
    }

    @Override
    public String getModelName() {
        return model.getModelName();
    }

    @Override
    public INodeType getType() {
        return type;
    }

    @Override
    public Node getRootNode() {
        return returnableNode;
    }
}
