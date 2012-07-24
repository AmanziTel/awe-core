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

package org.amanzi.neo.models.impl.network;

import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkModel extends AbstractDatasetModel implements INetworkModel {

    private static final Logger LOGGER = Logger.getLogger(NetworkModel.class);

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public NetworkModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperties) {
        super(nodeService, generalNodeProperties, geoNodeProperties);
    }

    @Override
    protected INodeType getModelType() {
        return NetworkElementType.NETWORK;
    }

    @Override
    public IDataElement findElement(final INetworkElementType elementType, final String elementName) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findElement", elementType, elementName));
        }

        // validate input
        if (elementType == null) {
            throw new ParameterInconsistencyException("elementType");
        }

        if (StringUtils.isEmpty(elementName)) {
            throw new ParameterInconsistencyException("elementName", elementName);
        }

        IDataElement result = null;

        Node elementNode = getIndexModel()
                .getSingleNode(elementType, getGeneralNodeProperties().getNodeNameProperty(), elementName);

        if (elementNode != null) {
            result = new DataElement(elementNode);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findElement"));
        }
        return result;
    }

    @Override
    public IDataElement createElement(final INetworkElementType elementType, final IDataElement parent, final String name,
            final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createElement", elementType, parent, name, properties));
        }

        // validate input
        if (elementType == null) {
            throw new ParameterInconsistencyException("elementType");
        }
        if (parent == null) {
            throw new ParameterInconsistencyException("parent");
        }
        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException("name", name);
        }
        if (properties == null) {
            throw new ParameterInconsistencyException("properties", null);
        }

        IDataElement result = null;

        try {
            Node parentNode = ((DataElement)parent).getNode();
            Node node = getNodeService().createNode(parentNode, elementType, NodeServiceRelationshipType.CHILD, name, properties);

            getIndexModel().index(elementType, node, getGeneralNodeProperties().getNodeNameProperty(), name);

            getPropertyStatisticsModel().indexElement(elementType, properties);

            result = new DataElement(node);
        } catch (ServiceException e) {
            processException("An error occured on creating new Network Element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createElement"));
        }

        return result;
    }

    @Override
    public IDataElement replaceChild(final IDataElement child, final IDataElement newParent) throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }
}
