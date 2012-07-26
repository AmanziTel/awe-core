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

import java.util.Map;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.LocationElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.measurement.MeasurementNodeType;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.services.INodeService;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ILocationElement createLocation(final IDataElement parent, final double latitude, final double longitude,
            final long timestamp) throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToLocation(final IDataElement measurement, final ILocationElement location) throws ModelException {
        // TODO Auto-generated method stub

    }

    @Override
    public IDataElement addMeasurement(final IDataElement parent, final Map<String, Object> properties) throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound) throws ModelException {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    protected ILocationElement getLocationElement(final Node node) {
        LocationElement location = new LocationElement(node);

        location.setNodeType(MeasurementNodeType.MP);

        return location;
    }

}
