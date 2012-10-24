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

import org.amanzi.awe.correlation.exceptions.DuplicatedProxyException;
import org.amanzi.awe.correlation.model.CorrelationTypes;
import org.amanzi.awe.correlation.nodeproperties.ICorrelationProperties;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

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

    private static final Logger LOGGER = Logger.getLogger(CorrelationService.class);

    private static final TraversalDescription INCOMING_PROXIES_TRAVERSAL = Traversal.description().depthFirst()
            .evaluator(Evaluators.excludeStartPosition()).relationships(CorrelationRelationshipType.PROXY, Direction.INCOMING);

    private final INodeService nodeSerivce;

    private final ICorrelationProperties correlationProperties;

    private final String CORRELATION_MODEL_NAME_FORMAT = "%s:%s@%s%s";

    /**
     * @param nodeSerivce
     * @param generalNodeProeprties
     */
    public CorrelationService(final INodeService nodeSerivce, final IGeneralNodeProperties generalNodeProeprties,
            final ICorrelationProperties correlationProperties) {
        super();
        this.nodeSerivce = nodeSerivce;
        this.correlationProperties = correlationProperties;
    }

    @Override
    public Node createCorrelationModelNode(final Node networkRoot, final Node measurementRoot, final String correlationProperty,
            final String correlatedProperty) throws ServiceException {
        assert networkRoot != null;
        assert measurementRoot != null;
        assert !StringUtils.isEmpty(correlatedProperty);
        assert !StringUtils.isEmpty(correlationProperty);

        String networkName = nodeSerivce.getNodeName(networkRoot);
        String measurementName = nodeSerivce.getNodeName(measurementRoot);

        String modelName = String.format(CORRELATION_MODEL_NAME_FORMAT, networkName, correlationProperty, measurementName,
                correlatedProperty);

        Node modelNode = nodeSerivce.createNode(networkRoot, CorrelationTypes.CORRELATION_MODEL,
                CorrelationRelationshipType.CORRELATION, String.format(modelName, networkName, measurementName));

        nodeSerivce.updateProperty(modelNode, correlationProperties.getCorrelatedNodeProperty(), correlatedProperty);
        nodeSerivce.updateProperty(modelNode, correlationProperties.getCorrelationNodeProperty(), correlationProperty);

        nodeSerivce.linkNodes(measurementRoot, modelNode, CorrelationRelationshipType.CORRELATED);
        return modelNode;
    }

    @Override
    public Node createProxy(final Node rootNode, final Node sectorNode, final Node measurementNode, final String measuremntName)
            throws ServiceException {
        assert rootNode != null;
        assert sectorNode != null;
        assert !StringUtils.isEmpty(measuremntName);

        Node proxyNode = findProxy(sectorNode, measurementNode, measuremntName);

        if (proxyNode != null) {
            throw new DuplicatedProxyException(rootNode, sectorNode, measurementNode);
        }

        proxyNode = nodeSerivce.createNodeInChain(rootNode, CorrelationTypes.PROXY);
        nodeSerivce.linkNodes(proxyNode, sectorNode, CorrelationRelationshipType.PROXY);
        Relationship relationship = nodeSerivce.linkNodes(proxyNode, measurementNode, CorrelationRelationshipType.PROXY);
        nodeSerivce.updateProperty(relationship, correlationProperties.getCorrelatedModelNameProperty(), measuremntName);
        return proxyNode;
    }

    @Override
    public Node findCorrelationModelNode(final Node networkRoot, final Node measurementRoot, final String correlationProperty,
            final String correlatedProperty) throws ServiceException {
        assert networkRoot != null;
        assert measurementRoot != null;
        assert !StringUtils.isEmpty(correlatedProperty);
        assert !StringUtils.isEmpty(correlationProperty);

        Iterator<Node> children = nodeSerivce.getChildren(networkRoot, CorrelationTypes.CORRELATION_MODEL,
                CorrelationRelationshipType.CORRELATION);
        if (children == null || !children.hasNext()) {
            return null;
        }
        String networkName = nodeSerivce.getNodeName(networkRoot);
        String measuremerntName = nodeSerivce.getNodeName(measurementRoot);

        String modelName = String.format(CORRELATION_MODEL_NAME_FORMAT, networkName, correlationProperty, measuremerntName,
                correlatedProperty);

        while (children.hasNext()) {
            Node modelNode = children.next();
            if (nodeSerivce.getNodeName(modelNode).equals(modelName)) {
                return modelNode;
            }
        }
        return null;
    }

    private Node findMeasurementInProxy(final Node proxy, final Node measurementNode, final String measuremntName)
            throws ServiceException {
        Iterable<Relationship> rel = proxy.getRelationships(CorrelationRelationshipType.PROXY, Direction.OUTGOING);
        if (rel == null || !rel.iterator().hasNext()) {
            return null;
        }
        Iterator<Relationship> relationships = rel.iterator();

        while (relationships.hasNext()) {
            Relationship relation = relationships.next();
            if (relation.hasProperty(correlationProperties.getCorrelatedModelNameProperty())
                    && relation.getProperty(correlationProperties.getCorrelatedModelNameProperty()).equals(measuremntName)) {
                Node proxyMeasurement = relation.getOtherNode(proxy);
                if (proxyMeasurement.equals(measurementNode)) {
                    return proxyMeasurement;
                }
            }
        }
        return null;
    }

    @Override
    public Node findProxy(final Node sectorNode, final Node measurementNode, final String measuremntName) throws ServiceException {
        Iterator<Node> proxies = findSectorProxies(sectorNode);
        Node proxyNode = null;
        while (proxies.hasNext()) {
            Node proxy = proxies.next();
            proxyNode = findMeasurementInProxy(proxy, measurementNode, measuremntName);
            if (proxyNode != null) {
                break;
            }
        }
        return proxyNode;
    }

    public Iterator<Node> findSectorProxies(final Node sectorNode) {
        assert sectorNode != null;
        return INCOMING_PROXIES_TRAVERSAL.traverse(sectorNode).nodes().iterator();
    }

    @Override
    public Node getMeasurementForProxy(final Node proxy) throws ServiceException {
        return searchNode(proxy, false);
    }

    @Override
    public Node getSectorForProxy(final Node proxy) throws ServiceException {
        return searchNode(proxy, true);
    }

    private Node searchNode(final Node proxy, final boolean isSectorSearch) throws ServiceException {
        Iterable<Relationship> relations = proxy.getRelationships(Direction.OUTGOING, CorrelationRelationshipType.PROXY);
        if (proxy == null || !relations.iterator().hasNext()) {
            return null;
        }
        Node searchableNode;
        for (Relationship relation : relations) {
            searchableNode = relation.getOtherNode(proxy);
            try {
                if (nodeSerivce.getNodeType(searchableNode).equals(NetworkElementType.SECTOR)) {
                    if (isSectorSearch) {
                        return searchableNode;
                    }
                } else {
                    if (!isSectorSearch) {
                        return searchableNode;
                    }
                }
            } catch (NodeTypeNotExistsException e) {
                LOGGER.error("can't get type for node" + searchableNode);
                searchableNode = null;
            }
        }
        return null;
    }
}
