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

package org.amanzi.awe.correlation.service.impl;

import java.util.Iterator;

import org.amanzi.awe.correlation.model.CorrelationTypes;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationService implements ICorrelationService {

    public static enum CorrelationRelationshipType implements RelationshipType {
        CORRELATION, CORRELATED, PROXY;
    }

    private final static String MODEL_NAME_FORMAT = "%s correlate to %s";

    private final INodeService nodeSerivce;

    /**
     * @param nodeSerivce
     * @param generalNodeProeprties
     */
    public CorrelationService(final INodeService nodeSerivce, final IGeneralNodeProperties generalNodeProeprties) {
        super();
        this.nodeSerivce = nodeSerivce;
    }

    @Override
    public Node createCorrelationModelNode(final Node networkRoot, final Node measurementRoot) throws ServiceException {
        assert networkRoot != null;
        assert measurementRoot != null;
        String networkName = nodeSerivce.getNodeName(networkRoot);
        String measuremerntName = nodeSerivce.getNodeName(measurementRoot);

        Node modelNode = nodeSerivce.createNode(networkRoot, CorrelationTypes.CORRELATION_MODEL,
                CorrelationRelationshipType.CORRELATION, String.format(MODEL_NAME_FORMAT, networkName, measuremerntName));
        nodeSerivce.linkNodes(measurementRoot, modelNode, CorrelationRelationshipType.CORRELATED);
        return modelNode;
    }

    @Override
    public Node findCorrelationModelNode(final Node networkRoot, final Node measurementRoot) throws ServiceException {
        assert networkRoot != null;
        assert measurementRoot != null;

        Iterator<Node> children = nodeSerivce.getChildren(networkRoot, CorrelationTypes.CORRELATION_MODEL,
                CorrelationRelationshipType.CORRELATION);
        if (children == null || !children.hasNext()) {
            return null;
        }
        while (children.hasNext()) {
            Node modelNode = children.next();
            Relationship rel = modelNode.getSingleRelationship(CorrelationRelationshipType.CORRELATED, Direction.INCOMING);
            if (rel == null) {
                continue;
            }
            if (rel.getOtherNode(modelNode).equals(modelNode)) {
                return modelNode;
            }
        }
        return null;
    }
}
