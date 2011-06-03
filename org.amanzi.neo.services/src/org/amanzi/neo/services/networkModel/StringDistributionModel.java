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

import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
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
public class StringDistributionModel extends DefaultDistribution {

    private Node statisticNode;

    public StringDistributionModel(String propertyName, INodeType nodeType, IDistributionalModel distributionalModel) {
        super(distributionalModel, nodeType, propertyName);
        setRootDistributionNode();
    }

    private void setRootDistributionNode() {
        Evaluator ev = new PropertyEvaluator(propertyName, distributionalModel.getName());
        Iterable<Node> networkNodes = distributionalModel.getAllElementsByType(ev, NodeTypes.STATISTICS_ROOT);
        if (!networkNodes.iterator().hasNext()
                && !distributionalModel.getRootNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        .equals(NodeTypes.STATISTICS_ROOT)) {
            statisticNode = createDistributionRoot(distributionalModel.getName());
            distributionalModel.getRootNode().createRelationshipTo(statisticNode, NetworkRelationshipTypes.CHILD);
        } else {
            statisticNode = networkNodes.iterator().next();
        }

    }

    private Node createDistributionRoot(String nodeName){
        return networkService.createNode(NodeTypes.STATISTICS_ROOT, nodeName);
    }
    @Override
    public String getName() {
        return networkService.getNodeName(getRootNode());
    }

    @Override
    public INodeType getType() {
        return null;
    }

    @Override
    public Node getRootNode() {
        return statisticNode;
    }

    @Override
    protected void init() {
        Map<Object, Long> map = stat.findPropertyStatistic(
                distributionalModel.getRootNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).toString(), nodeType.getId(),
                propertyName).getValueMap();
        IRange valueRange;
        for (Object value : map.keySet()) {
            valueRange = new StringRange((String)value, nodeType, propertyName);
            rangeList.add(valueRange);

        }

    }

    @Override
    public List<IRange> getRangeList() {
        return rangeList;
    }
}
