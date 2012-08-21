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

package org.amanzi.neo.nodeproperties.impl;

import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MeasurementNodeProperties implements IMeasurementNodeProperties {

    private static final String FILE_PATH_PROPERTY = "path";

    private static final String PRIMARY_TYPE_PROPERTY = "primary_type";

    private static final String DRIVE_TYPE_PROEPRTY = "drive_type";

    private static final String EVENT_PROPERTY = "event";

    private static final String IMEI_PROPERTY = "imei";

    /**
     * @return Returns the filePathProperty.
     */
    @Override
    public String getFilePathProperty() {
        return FILE_PATH_PROPERTY;
    }

    @Override
    public String getPrimaryTypeProperty() {
        return PRIMARY_TYPE_PROPERTY;
    }

    @Override
    public String getDriveTypeProperty() {
        return DRIVE_TYPE_PROEPRTY;
    }

    @Override
    public String getEventProperty() {
        return EVENT_PROPERTY;
    }

    @Override
    public String getIMEIProperty() {
        return IMEI_PROPERTY;
    }

}
