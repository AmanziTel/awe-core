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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.dto.SectorElement;
import org.amanzi.neo.impl.dto.SiteElement;
import org.amanzi.neo.impl.util.IDataElementIterator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.vividsolutions.jts.geom.Envelope;

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

    // TODO: LN: 12.09.2012, duplicates AbstractMeasurementModel.ElementLocationIterator
    private final class ElementLocationIterator implements IDataElementIterator<ILocationElement> {

        private final Iterator<IDataElement> dataElements;

        private ILocationElement nextElement;

        private boolean moveToNext;

        private final Set<ILocationElement> locationElements = new HashSet<ILocationElement>();

        public ElementLocationIterator(final Iterable<IDataElement> dataElements) {
            this.dataElements = dataElements.iterator();

            moveToNext = true;
        }

        @Override
        public boolean hasNext() {
            if (moveToNext) {
                nextElement = moveToNext();
            }
            return nextElement != null;
        }

        @Override
        public ILocationElement next() {
            if (moveToNext) {
                nextElement = moveToNext();
            }

            moveToNext = true;
            return nextElement;
        }

        private ILocationElement moveToNext() {
            try {
                ILocationElement element = null;
                while (dataElements.hasNext() && (element == null)) {
                    final IDataElement dataElement = dataElements.next();

                    if (dataElement.getNodeType().equals(NetworkElementType.SITE)) {
                        element = getLocationElement(((DataElement)dataElement).getNode());
                    } else if (dataElement.getNodeType() instanceof INetworkElementType) {
                        if (NetworkElementType.compare(NetworkElementType.SITE, (NetworkElementType)dataElement.getNodeType()) < 0) {
                            IDataElement tempElement = dataElement;
                            while (!tempElement.getNodeType().equals(NetworkElementType.SITE)) {
                                tempElement = NetworkModel.this.getParentElement(tempElement);
                            }

                            element = getLocationElement(((DataElement)tempElement).getNode());
                        }

                    }
                }

                if (element != null) {
                    locationElements.add(element);
                }

                return element;
            } catch (final ModelException e) {
                LOGGER.error(e);
                return null;
            } finally {
                moveToNext = false;
            }
        }

        @Override
        public void remove() {
            dataElements.remove();
        }

        @Override
        public Iterable<ILocationElement> toIterable() {
            return new Iterable<ILocationElement>() {

                @Override
                public Iterator<ILocationElement> iterator() {
                    return ElementLocationIterator.this;
                }
            };
        }

    }

    private final INetworkNodeProperties networkNodeProperties;

    private final List<String> structure = new ArrayList<String>() {
        /** long serialVersionUID field */
        private static final long serialVersionUID = 7149098047373556881L;

        {
            add(NetworkElementType.NETWORK.getId());
        }
    };

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
    public void initialize(Node rootNode) throws ModelException {
        structure.clear();
        structure.addAll(Arrays.asList(((String[])rootNode.getProperty(networkNodeProperties.getStuctureProperty()))));
        super.initialize(rootNode);
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

        final Node elementNode = getIndexModel().getSingleNode(elementType, getGeneralNodeProperties().getNodeNameProperty(),
                elementName);

        if (elementNode != null) {
            result = new DataElement(elementNode);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findElement"));
        }
        return result;
    }

    protected IDataElement createSite(final IDataElement parent, final String name, final Double latitude, final Double longitude,
            final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createSite", parent, name, latitude, longitude, properties));
        }

        // validate input
        if (latitude == null) {
            throw new ParameterInconsistencyException(getGeoNodeProperties().getLatitudeProperty());
        }
        if (longitude == null) {
            throw new ParameterInconsistencyException(getGeoNodeProperties().getLongitudeProperty());
        }

        final DataElement result = createDefaultElement(NetworkElementType.SITE, parent, name, properties);

        if (result != null) {
            getIndexModel().indexInMultiProperty(NetworkElementType.SITE, result.getNode(), Double.class,
                    getGeoNodeProperties().getLatitudeProperty(), getGeoNodeProperties().getLongitudeProperty());
            updateLocation(latitude, longitude);
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

        final DataElement result = createDefaultElement(NetworkElementType.SECTOR, parent, name, properties);

        if (result != null) {
            if (ci != null) {
                getIndexModel().index(NetworkElementType.SECTOR, result.getNode(), networkNodeProperties.getCIProperty(), ci);
            }
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

            updateNetworkStructure(parent, elementType);

            getIndexModel().index(elementType, node, getGeneralNodeProperties().getNodeNameProperty(), name);

            getPropertyStatisticsModel().indexElement(elementType, removeIgnoredProperties(properties));

            result = new DataElement(node);
        } catch (final ServiceException e) {
            processException("An error occured on creating new Network Element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createDefaultElement"));
        }

        return result;
    }

    /**
     * @param parent
     * @param elementType
     * @throws ServiceException
     */
    private void updateNetworkStructure(IDataElement parent, INetworkElementType elementType) throws ServiceException {
        String parentType;
        try {
            parentType = getNodeService().getNodeType(((DataElement)parent).getNode()).getId();
        } catch (ServiceException e) {
            throw e;
        } catch (NodeTypeNotExistsException e) {
            LOGGER.error("can't get parent node type", e);
            return;

        }
        String currentType = elementType.getId();
        if (!structure.contains(currentType)) {
            structure.add(currentType);
            return;
        } else if (structure.contains(currentType) && structure.contains(parentType)) {
            return;
        }

        int parentIndex = structure.indexOf(parentType);
        int lastIndex = structure.indexOf(NetworkElementType.SITE);
        if (parentIndex < lastIndex) {
            structure.add(parentIndex, currentType);
        }
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
            final Integer ci = (Integer)properties.get(networkNodeProperties.getCIProperty());
            final Integer lac = (Integer)properties.get(networkNodeProperties.getLACProperty());
            result = createSector(parent, name, lac, ci, properties);
        } else if (elementType == NetworkElementType.SITE) {
            final Double lat = (Double)properties.get(getGeoNodeProperties().getLatitudeProperty());
            final Double lon = (Double)properties.get(getGeoNodeProperties().getLongitudeProperty());

            result = createSite(parent, name, lat, lon, properties);
        } else {
            result = createDefaultElement(elementType, parent, name, properties);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createElement"));
        }

        return result;
    }

    protected Map<String, Object> removeIgnoredProperties(final Map<String, Object> properties) {
        properties.remove(getGeoNodeProperties().getLatitudeProperty());
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
        if ((ci == null) && StringUtils.isEmpty(sectorName)) {
            throw new ParameterInconsistencyException(networkNodeProperties.getCIProperty(), ci);
        }

        IDataElement result = null;

        if (!StringUtils.isEmpty(sectorName)) {
            result = findElement(NetworkElementType.SECTOR, sectorName);
        } else {
            final List<Node> ciList = getNodeListFromIndex(NetworkElementType.SECTOR, networkNodeProperties.getCIProperty(), ci);
            List<Node> resultList = null;

            if (lac != null) {
                final List<Node> lacNodes = getNodeListFromIndex(NetworkElementType.SECTOR, networkNodeProperties.getLACProperty(),
                        lac);

                resultList = new ArrayList<Node>(CollectionUtils.intersection(ciList, lacNodes));
            }

            if ((resultList != null) && !resultList.isEmpty()) {
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
        final List<Node> result = new ArrayList<Node>();

        CollectionUtils.addAll(result, getIndexModel().getNodes(nodeType, propertyName, value));

        return result;
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound) throws ModelException {
        final Double[] min = new Double[] {bound.getMinY(), bound.getMinX()};
        final Double[] max = new Double[] {bound.getMaxY(), bound.getMaxX()};

        final Iterator<Node> nodeIterator = getIndexModel().getNodes(NetworkElementType.SITE, Double.class, min, max,
                getGeoNodeProperties().getLatitudeProperty(), getGeoNodeProperties().getLongitudeProperty());

        return new LocationIterator(nodeIterator).toIterable();
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound, final IFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ILocationElement getLocationElement(final Node node) {
        final SiteElement site = new SiteElement(node);
        site.setNodeType(NetworkElementType.SITE);

        try {
            site.setLatitude((Double)getNodeService().getNodeProperty(node, getGeoNodeProperties().getLatitudeProperty(), null,
                    true));
            site.setLongitude((Double)getNodeService().getNodeProperty(node, getGeoNodeProperties().getLongitudeProperty(), null,
                    true));

            final Iterator<Node> sectorNodes = getNodeService().getChildren(node, NetworkElementType.SECTOR,
                    NodeServiceRelationshipType.CHILD);
            while (sectorNodes.hasNext()) {
                site.addSector(getSectorElement(sectorNodes.next()));
            }

        } catch (final ServiceException e) {
            LOGGER.error("Unable to create a SiteElement from node", e);

            return null;
        }

        return site;
    }

    private ISectorElement getSectorElement(final Node node) throws ServiceException {
        final SectorElement element = new SectorElement(node);
        element.setNodeType(NetworkElementType.SECTOR);
        element.setName(getName());
        element.setAzimuth((Double)getNodeService().getNodeProperty(node, networkNodeProperties.getAzimuthProperty(), null, false));
        element.setBeamwidth((Double)getNodeService().getNodeProperty(node, networkNodeProperties.getBeamwidthProperty(), null,
                false));

        return element;
    }

    @Override
    public int getRenderableElementCount() {
        return getPropertyStatistics().getCount(NetworkElementType.SITE);
    }

    @Override
    public void finishUp() throws ModelException {
        LOGGER.info("Finishing up model <" + getName() + ">");
        try {

            getNodeService().updateProperty(getRootNode(), networkNodeProperties.getStuctureProperty(),
                    structure.toArray(new String[structure.size()]));
        } catch (ServiceException e) {
            processException("can't set structure properties", e);
        }
        super.finishUp();
    }

    @Override
    public Iterable<ILocationElement> getElementsLocations(final Iterable<IDataElement> dataElements) {
        return new ElementLocationIterator(dataElements).toIterable();
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getAllElementsByType", nodeType));
        }

        final Iterable<IDataElement> result = new DataElementIterator(getIndexModel().getAllNodes(nodeType)).toIterable();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getAllElementsByType"));
        }

        return result;
    }

    @Override
    public ILocationElement getElementLocation(final IDataElement dataElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getElelemntLocation", dataElement));
        }

        ILocationElement location = null;

        final Node elementNode = ((DataElement)dataElement).getNode();

        try {
            if (dataElement instanceof ISiteElement) {
                location = (ISiteElement)dataElement;
            } else if (dataElement.getNodeType().equals(NetworkElementType.SITE)) {
                location = getLocationElement(elementNode);
            } else if (dataElement.getNodeType().equals(NetworkElementType.SECTOR)) {
                location = getLocationElement(getParent(elementNode));
            }
        } catch (final ServiceException e) {
            processException("Error on computing Location element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getElementLocation"));
        }

        return location;
    }

    @Override
    public String[] getNetworkStructure() {
        return structure.toArray(new String[structure.size()]);
    }
}
