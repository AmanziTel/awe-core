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

package org.amanzi.neo.models.impl.drive;

import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.impl.measurement.AbstractMeasurementModel;
import org.amanzi.neo.models.measurement.MeasurementNodeType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveModel extends AbstractMeasurementModel implements IDriveModel {

    /**
     * @param nodeService
     * @param generalNodeProperties
     * @param geoNodeProperties
     */
    public DriveModel(final ITimePeriodNodeProperties timePeriodNodeProperties,
            final IMeasurementNodeProperties measurementNodeProperties, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final IGeoNodeProperties geoNodeProperties) {
        super(timePeriodNodeProperties, measurementNodeProperties, nodeService, generalNodeProperties, geoNodeProperties);
    }

    @Override
    protected INodeType getModelType() {
        return MeasurementNodeType.DRIVE;
    }

    @Override
    public INodeType getMainMeasurementNodeType() {
        return MeasurementNodeType.M;
    }

}
