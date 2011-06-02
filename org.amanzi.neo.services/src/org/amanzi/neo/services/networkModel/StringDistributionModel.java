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
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
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
    private IDistributionalModel model;
    private final NetworkService networkService;
    private String propertyName;
    private INodeType type;
    private Node statisticNode;

    public StringDistributionModel(String propertyName, INodeType type, IDistributionalModel model) {
        super(model, type.getId(), propertyName);
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        this.model = model;
        this.propertyName = propertyName;
        this.type = type;
        setRootDistributionNode();

    }

    private void setRootDistributionNode() {
        Evaluator ev = new PropertyEvaluator(propertyName, getName());
        Iterable<Node> networkNodes = this.model.getAllElementsByType(ev, NodeTypes.STATISTICS_ROOT);
        if (!networkNodes.iterator().hasNext()
                && !model.getRootNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.STATISTICS_ROOT)) {
            statisticNode = networkService.createNode(NodeTypes.STATISTICS_ROOT, getName());
            model.getRootNode().createRelationshipTo(statisticNode, NetworkRelationshipTypes.CHILD);
        } else {
            statisticNode = networkNodes.iterator().next();
        }

    }

    @Override
    public String getName() {
        return model.getName();
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
                distributionalModel.getRootNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).toString(), nType, pName)
                .getValueMap();
        IRange valueRange;
        for (Object value : map.keySet()) {
            valueRange = new StringRange((String)value,type);
            rangeList.add(valueRange);
           
        }

    }

    @Override
    public List<IRange> getRangeList() {
        return rangeList;
    }
}
