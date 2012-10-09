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

import org.amanzi.neo.models.drive.DriveType;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.measurement.AbstractMeasurementModel;
import org.amanzi.neo.models.measurement.MeasurementNodeType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
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
public class DriveModel extends AbstractMeasurementModel implements IDriveModel {

    private static final Logger LOGGER = Logger.getLogger(DriveModel.class);

    private IDriveType driveType;

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

    public void setDriveType(final IDriveType driveType) {
        this.driveType = driveType;
    }

    @Override
    public IDriveType getDriveType() {
        return driveType;
    }

    @Override
    public void initialize(final Node node) throws ModelException {
        super.initialize(node);

        try {
            final String driveTypeName = getNodeService().getNodeProperty(node,
                    getMeasurementNodeProperties().getDriveTypeProperty(), StringUtils.EMPTY, false);
            this.driveType = getDriveType(driveTypeName);
        } catch (final ServiceException e) {
            processException("Error on initializing Drive Model", e);
        }
    }

    @Override
    public void finishUp() throws ModelException {
        LOGGER.info("Finishing up model <" + getName() + ">");
        try {
            getNodeService()
                    .updateProperty(getRootNode(), getMeasurementNodeProperties().getDriveTypeProperty(), driveType.getId());
        } catch (final ServiceException e) {
            processException("Error on finishing up Drive Model", e);
        }

        super.finishUp();
    }

    protected IDriveType getDriveType(final String driveType) {
        return DriveType.findById(driveType);
    }

}
