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

import java.util.ArrayList;
import java.util.List;
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
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.collections.CollectionUtils;
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

    private final INetworkNodeProperties networkNodeProperties;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public NetworkModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperties, final INetworkNodeProperties networkNodeProperties) {
        super(nodeService, generalNodeProperties, geoNodeProperties);
        this.networkNodeProperties = networkNodeProperties;
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

    protected IDataElement createSite(final IDataElement parent, final String name, final Double lat, final Double lon,
            final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createSite", parent, name, lat, lon, properties));
        }

        // validate input
        if (lat == null) {
            throw new ParameterInconsistencyException(getGeoNodeProperties().getLatitideProperty());
        }
        if (lon == null) {
            throw new ParameterInconsistencyException(getGeoNodeProperties().getLongitudeProperty());
        }

        DataElement result = createDefaultElement(NetworkElementType.SITE, parent, name, properties);

        if (result != null) {
            getIndexModel().indexInMultiProperty(NetworkElementType.SITE, result.getNode(), Double.class,
                    getGeoNodeProperties().getLatitideProperty(), getGeoNodeProperties().getLongitudeProperty());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createSite"));
        }

        return result;
    }

    protected IDataElement createSector(final IDataElement parent, final String name, final Integer lac, final Integer ci,
            final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createSector", parent, name, lac, ci, properties));
        }

        // validate input
        if (ci == null) {
            throw new ParameterInconsistencyException(networkNodeProperties.getCIProperty(), ci);
        }

        DataElement result = createDefaultElement(NetworkElementType.SECTOR, parent, name, properties);

        if (result != null) {
            getIndexModel().index(NetworkElementType.SECTOR, result.getNode(), networkNodeProperties.getCIProperty(), ci);
            if (lac != null) {
                getIndexModel().index(NetworkElementType.SECTOR, result.getNode(), networkNodeProperties.getLACProperty(), lac);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createSector"));
        }

        return result;
    }

    protected DataElement createDefaultElement(final INetworkElementType elementType, final IDataElement parent, final String name,
            final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createDefaultElement", elementType, parent, name, properties));
        }

        DataElement result = null;

        try {
            Node parentNode = ((DataElement)parent).getNode();
            Node node = getNodeService().createNode(parentNode, elementType, NodeServiceRelationshipType.CHILD, name, properties);

            getIndexModel().index(elementType, node, getGeneralNodeProperties().getNodeNameProperty(), name);

            getPropertyStatisticsModel().indexElement(elementType, removeIgnoredProperties(properties));

            result = new DataElement(node);
        } catch (ServiceException e) {
            processException("An error occured on creating new Network Element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createDefaultElement"));
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

        if (elementType == NetworkElementType.SECTOR) {
            Integer ci = (Integer)properties.get(networkNodeProperties.getCIProperty());
            Integer lac = (Integer)properties.get(networkNodeProperties.getLACProperty());
            result = createSector(parent, name, lac, ci, properties);
        } else if (elementType == NetworkElementType.SITE) {
            Double lat = (Double)properties.get(getGeoNodeProperties().getLatitideProperty());
            Double lon = (Double)properties.get(getGeoNodeProperties().getLongitudeProperty());

            result = createSite(parent, name, lat, lon, properties);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createElement"));
        }

        return result;
    }

    protected Map<String, Object> removeIgnoredProperties(final Map<String, Object> properties) {
        properties.remove(getGeoNodeProperties().getLatitideProperty());
        properties.remove(getGeoNodeProperties().getLongitudeProperty());

        return properties;
    }

    @Override
    public IDataElement replaceChild(final IDataElement child, final IDataElement newParent) throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDataElement findSector(final String sectorName, final Integer ci, final Integer lac) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findSector", sectorName, ci, lac));
        }

        // validate input
        if (StringUtils.isEmpty(sectorName) && (ci == null) && (lac == null)) {
            throw new ParameterInconsistencyException(getGeneralNodeProperties().getNodeNameProperty(), sectorName);
        }
        if (lac == null) {
            throw new ParameterInconsistencyException(networkNodeProperties.getCIProperty(), ci);
        }

        IDataElement result = null;

        if ((ci == null) || (lac == null)) {
            result = findElement(NetworkElementType.SECTOR, sectorName);
        } else {
            List<Node> ciList = getNodeListFromIndex(NetworkElementType.SECTOR, networkNodeProperties.getCIProperty(), ci);
            List<Node> resultList = null;

            if (lac != null) {
                List<Node> lacNodes = getNodeListFromIndex(NetworkElementType.SECTOR, networkNodeProperties.getLACProperty(), lac);

                resultList = new ArrayList<Node>(CollectionUtils.intersection(ciList, lacNodes));
            }

            if (!resultList.isEmpty()) {
                result = new DataElement(resultList.get(0));
            } else if (!ciList.isEmpty()) {
                result = new DataElement(ciList.get(0));
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findElement"));
        }
        return result;
    }

    private List<Node> getNodeListFromIndex(final INodeType nodeType, final String propertyName, final Object value)
            throws ModelException {
        List<Node> result = new ArrayList<Node>();

        CollectionUtils.addAll(result, getIndexModel().getNodes(nodeType, propertyName, value));

        return result;
    }
}
