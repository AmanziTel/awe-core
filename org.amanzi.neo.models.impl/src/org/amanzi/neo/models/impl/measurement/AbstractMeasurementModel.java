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

package org.amanzi.neo.models.impl.measurement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.dto.FileElement;
import org.amanzi.neo.impl.dto.LocationElement;
import org.amanzi.neo.impl.util.AbstractDataElementIterator;
import org.amanzi.neo.impl.util.IDataElementIterator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.measurement.MeasurementNodeType;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.MeasurementRelationshipType;
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
public abstract class AbstractMeasurementModel extends AbstractDatasetModel implements IMeasurementModel {

    private static final Logger LOGGER = Logger.getLogger(AbstractMeasurementModel.class);

    private static final INodeType DEFAULT_PRIMARY_TYPE = MeasurementNodeType.M;

    private final class ElementLocationIterable implements IDataElementIterator<ILocationElement> {

        private final Iterator<IDataElement> dataElements;

        private final Set<ILocationElement> locationElements = new HashSet<ILocationElement>();

        private boolean moveToNext;

        private ILocationElement nextElement;

        public ElementLocationIterable(final Iterable<IDataElement> dataElements) {
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
                if (dataElements.hasNext()) {
                    ILocationElement element = getElementLocation((DataElement)dataElements.next());

                    if (locationElements.contains(nextElement)) {
                        element = moveToNext();
                    }

                    if (element != null) {
                        locationElements.add(element);
                    }

                    return element;
                } else {
                    return null;
                }
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
                    return ElementLocationIterable.this;
                }
            };
        }

    }

    protected final class MeasurementIterator extends AbstractDataElementIterator<IDataElement> {

        /**
         * @param nodeIterator
         */
        public MeasurementIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected IDataElement createDataElement(final Node node) {
            return convertToDataElement(node);
        }

    }

    private int locationCount;

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private final IMeasurementNodeProperties measurementNodeProperties;

    private long minTimestamp = Long.MAX_VALUE;

    private long maxTimestamp = Long.MIN_VALUE;

    private INodeType primaryType = DEFAULT_PRIMARY_TYPE;

    /**
     * @param nodeService
     * @param generalNodeProperties
     * @param geoNodeProperties
     */
    protected AbstractMeasurementModel(final ITimePeriodNodeProperties timePeriodNodeProperties,
            final IMeasurementNodeProperties measurementNodeProperties, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final IGeoNodeProperties geoNodeProperties) {
        super(nodeService, generalNodeProperties, geoNodeProperties);
        this.timePeriodNodeProperties = timePeriodNodeProperties;
        this.measurementNodeProperties = measurementNodeProperties;
    }

    @Override
    public IFileElement getFile(final IDataElement parent, final String name, final String path) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getFile", parent, name, path));
        }

        // validate input
        if (parent == null) {
            throw new ParameterInconsistencyException("parent");
        }
        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException(getGeneralNodeProperties().getNodeNameProperty(), name);
        }
        if (StringUtils.isEmpty(path)) {
            throw new ParameterInconsistencyException(measurementNodeProperties.getFilePathProperty(), path);
        }

        IFileElement result = null;

        try {
            DataElement parentElement = (DataElement)parent;
            Node parentNode = parentElement.getNode();

            Node fileNode = getNodeService().getChildInChainByName(parentNode, name, MeasurementNodeType.FILE);

            if (fileNode == null) {
                Map<String, Object> properties = new HashMap<String, Object>();

                properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);
                properties.put(measurementNodeProperties.getFilePathProperty(), path);

                fileNode = getNodeService().createNodeInChain(parentNode, MeasurementNodeType.FILE, properties);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Created new File Node by name <" + name + "> in Model <" + getName() + ">");
                }
            }

            result = getFileElement(fileNode, name, path);
        } catch (ServiceException e) {
            processException("Error on adding new File", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getFile"));
        }

        return result;
    }

    @Override
    public ILocationElement createLocation(final IDataElement parent, final double latitude, final double longitude,
            final long timestamp) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createLocation", parent, latitude, longitude, timestamp));
        }

        // validate input
        if (parent == null) {
            throw new ParameterInconsistencyException("parent");
        }

        ILocationElement location = null;

        try {
            DataElement measurementElement = (DataElement)parent;
            Node measurementNode = measurementElement.getNode();

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(getGeoNodeProperties().getLatitudeProperty(), latitude);
            properties.put(getGeoNodeProperties().getLongitudeProperty(), longitude);
            properties.put(timePeriodNodeProperties.getTimestampProperty(), timestamp);

            Node locationNode = getNodeService().createNode(measurementNode, MeasurementNodeType.MP,
                    MeasurementRelationshipType.LOCATION, properties);

            getIndexModel().indexInMultiProperty(MeasurementNodeType.MP, locationNode, Double.class,
                    getGeoNodeProperties().getLatitudeProperty(), getGeoNodeProperties().getLongitudeProperty());
            updateLocation(latitude, longitude);

            location = getLocationElement(locationNode);

            locationCount++;
        } catch (ServiceException e) {
            processException("Exception on creating Location", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createLocation"));
        }
        return location;
    }

    @Override
    public void addToLocation(final IDataElement measurement, final ILocationElement location) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("addToLocation", measurement, location));
        }

        try {
            DataElement measurementElement = (DataElement)measurement;
            DataElement locationElement = (DataElement)location;

            getNodeService().linkNodes(measurementElement.getNode(), locationElement.getNode(),
                    MeasurementRelationshipType.LOCATION);
        } catch (ServiceException e) {
            processException("Error on adding Location to Measurement", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("addToLocation"));
        }
    }

    @Override
    public IDataElement addMeasurement(final IDataElement parent, final Map<String, Object> properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("addMeasurement", parent, properties));
        }

        // validate input
        if (parent == null) {
            throw new ParameterInconsistencyException("parent");
        }
        if ((properties == null) || properties.isEmpty()) {
            throw new ParameterInconsistencyException("properties", properties);
        }

        IDataElement result = null;

        try {
            DataElement parentElement = (DataElement)parent;
            Node parentNode = parentElement.getNode();

            Node measurementNode = getNodeService().createNodeInChain(parentNode, getMainMeasurementNodeType(), properties);

            result = getDataElement(measurementNode, null, null);

            getIndexModel().indexInMultiProperty(getMainMeasurementNodeType(), measurementNode, Long.class,
                    timePeriodNodeProperties.getTimestampProperty());

            getPropertyStatisticsModel().indexElement(getMainMeasurementNodeType(), properties);

            Long timestamp = (Long)properties.get(timePeriodNodeProperties.getTimestampProperty());
            updateTimestamp(timestamp);
        } catch (ServiceException e) {
            processException("Error on adding Measurement", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("addMeasurement"));
        }
        return result;
    }

    private void updateTimestamp(final long timestamp) {
        minTimestamp = Math.min(minTimestamp, timestamp);
        maxTimestamp = Math.max(maxTimestamp, timestamp);
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound) throws ModelException {
        Double[] min = new Double[] {bound.getMinY(), bound.getMinX()};
        Double[] max = new Double[] {bound.getMaxY(), bound.getMaxX()};

        Iterator<Node> nodeIterator = getIndexModel().getNodes(MeasurementNodeType.MP, Double.class, min, max,
                getGeoNodeProperties().getLatitudeProperty(), getGeoNodeProperties().getLongitudeProperty());

        return new LocationIterator(nodeIterator).toIterable();
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound, final IFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRenderableElementCount() {
        return locationCount;
    }

    protected IFileElement getFileElement(final Node node, final String name, final String path) {
        FileElement file = new FileElement(node);

        file.setName(name);
        file.setPath(path);
        file.setNodeType(MeasurementNodeType.FILE);

        return file;
    }

    @Override
    protected ILocationElement getLocationElement(final Node node) {
        LocationElement location = new LocationElement(node);

        location.setNodeType(MeasurementNodeType.MP);

        try {
            location.setLatitude((Double)getNodeService().getNodeProperty(node, getGeoNodeProperties().getLatitudeProperty(), null,
                    true));
            location.setLongitude((Double)getNodeService().getNodeProperty(node, getGeoNodeProperties().getLongitudeProperty(),
                    null, true));

        } catch (ServiceException e) {
            LOGGER.error("Unable to create a Location Element from node", e);

            return null;
        }

        return location;
    }

    @Override
    public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
        assert parentElement != null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getChildren", parentElement));
        }

        Iterable<IDataElement> result = null;

        try {
            DataElement parent = (DataElement)parentElement;
            Node parentNode = parent.getNode();

            result = new MeasurementIterator(getNodeService().getChildrenChain(parentNode)).toIterable();
        } catch (ServiceException e) {
            processException("An error occured on search child for parent Element", e);
        }
        return result;
    }

    private IDataElement convertToDataElement(final Node node) {
        IDataElement element = null;

        try {
            INodeType type = getNodeService().getNodeType(node);

            if (type.equals(getMainMeasurementNodeType())) {
                String name = getNodeService().getNodeProperty(node, measurementNodeProperties.getEventProperty(), null, false);

                element = getDataElement(node, type, name);
            } else if (type.equals(MeasurementNodeType.FILE)) {
                String name = getNodeService().getNodeName(node);
                String path = getNodeService().getNodeProperty(node, measurementNodeProperties.getFilePathProperty(), null, false);

                element = getFileElement(node, name, path);
            }
        } catch (Exception e) {
            LOGGER.error("Error on converting Node to Measurement Element", e);
        }

        return element;
    }

    @Override
    public INodeType getMainMeasurementNodeType() {
        return primaryType;
    }

    @Override
    public void initialize(final Node node) throws ModelException {
        try {
            super.initialize(node);

            minTimestamp = getNodeService().getNodeProperty(node, timePeriodNodeProperties.getMinTimestampProperty(),
                    Long.MAX_VALUE, false);
            maxTimestamp = getNodeService().getNodeProperty(node, timePeriodNodeProperties.getMaxTimestampProperty(),
                    Long.MIN_VALUE, false);
            String primaryTypeName = getNodeService().getNodeProperty(node, measurementNodeProperties.getPrimaryTypeProperty(),
                    DEFAULT_PRIMARY_TYPE.getId(), false);
            primaryType = NodeTypeManager.getInstance().getType(primaryTypeName);

            locationCount = getNodeService().getNodeProperty(node, getGeoNodeProperties().getLocationCountProperty(), 0, false);
        } catch (Exception e) {
            processException("Error on initialization of Measurement Model", e);
        }
    }

    @Override
    public void finishUp() throws ModelException {
        try {
            getNodeService().updateProperty(getRootNode(), timePeriodNodeProperties.getMinTimestampProperty(), minTimestamp);
            getNodeService().updateProperty(getRootNode(), timePeriodNodeProperties.getMaxTimestampProperty(), maxTimestamp);
            getNodeService().updateProperty(getRootNode(), measurementNodeProperties.getPrimaryTypeProperty(), primaryType.getId());
            getNodeService().updateProperty(getRootNode(), getGeoNodeProperties().getLocationCountProperty(), locationCount);

            super.finishUp();
        } catch (ServiceException e) {
            processException("Exception on finishin up Measurement Model", e);
        }
    }

    protected IMeasurementNodeProperties getMeasurementNodeProperties() {
        return measurementNodeProperties;
    }

    @Override
    public long getMinTimestamp() {
        return minTimestamp;
    }

    @Override
    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    @Override
    public Iterable<IDataElement> getElements(final long minTimestamp, final long maxTimestamp) throws ModelException {
        Long[] min = new Long[] {minTimestamp};
        Long[] max = new Long[] {maxTimestamp};

        Iterator<Node> nodeIterator = getIndexModel().getNodes(getMainMeasurementNodeType(), Long.class, min, max,
                timePeriodNodeProperties.getTimestampProperty());

        return new DataElementIterator(nodeIterator).toIterable();
    }

    protected ILocationElement getElementLocation(final DataElement dataElement) {
        ILocationElement result = null;

        try {
            Node locationNode = getNodeService().getSingleChild(dataElement.getNode(), MeasurementNodeType.MP, MeasurementRelationshipType.LOCATION);

            result = getLocationElement(locationNode);
        } catch (ServiceException e) {
            LOGGER.error("Error on calculating location Node", e);
        }

        return result;
    }

    @Override
    public Iterable<ILocationElement> getElementsLocations(final Iterable<IDataElement> dataElements) throws ModelException {
        return new ElementLocationIterable(dataElements).toIterable();
    }
}
