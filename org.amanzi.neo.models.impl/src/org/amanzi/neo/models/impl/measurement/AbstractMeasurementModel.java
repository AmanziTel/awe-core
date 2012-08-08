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
import java.util.Iterator;
import java.util.Map;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.dto.FileElement;
import org.amanzi.neo.impl.dto.LocationElement;
import org.amanzi.neo.impl.util.AbstractDataElementIterator;
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
            throw new ParameterInconsistencyException(measurementNodeProperties.getFilePath(), path);
        }

        IFileElement result = null;

        try {
            DataElement parentElement = (DataElement)parent;
            Node parentNode = parentElement.getNode();

            Node fileNode = getNodeService().getChildInChainByName(parentNode, name, MeasurementNodeType.FILE);

            if (fileNode == null) {
                Map<String, Object> properties = new HashMap<String, Object>();

                properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);
                properties.put(measurementNodeProperties.getFilePath(), path);

                fileNode = getNodeService().createNodeInChain(parentNode, MeasurementNodeType.FILE, properties);
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

            Node measurementNode = getNodeService().createNodeInChain(parentNode, MeasurementNodeType.M, properties);

            result = getDataElement(measurementNode, null, null);

            getIndexModel().indexInMultiProperty(MeasurementNodeType.M, measurementNode, Long.class,
                    timePeriodNodeProperties.getTimestampProperty());

            getPropertyStatisticsModel().indexElement(MeasurementNodeType.M, properties);
        } catch (ServiceException e) {
            processException("Error on adding Measurement", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("addMeasurement"));
        }
        return result;
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

    private IDataElement convertToDataElement(Node node) {
        IDataElement element = null;

        try {
            INodeType type = getNodeService().getNodeType(node);

            if (type.equals(MeasurementNodeType.M)) {
                element = getDataElement(node, type, null);
            } else if (type.equals(MeasurementNodeType.FILE)) {
                String name = getNodeService().getNodeName(node);
                String path = getNodeService().getNodeProperty(node, measurementNodeProperties.getFilePath(), null, false);

                element = getFileElement(node, name, path);
            }
        } catch (Exception e) {
            LOGGER.error("Error on converting Node to Measurement Element", e);
        }

        return element;
    }

}
