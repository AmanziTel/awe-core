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

package org.amanzi.neo.loader.core.saver.drive.impl;

import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.saver.impl.AbstractDriveSaver;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.drive.DriveType;
import org.amanzi.neo.models.drive.IDriveModel.IDriveType;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TEMSSaver extends AbstractDriveSaver {

    public TEMSSaver() {
        this(LoaderCorePlugin.getInstance().getTimePeriodNodeProperties(), LoaderCorePlugin.getInstance().getGeoNodeProperties(),
                LoaderCorePlugin.getInstance().getDriveModelProvider(), LoaderCorePlugin.getInstance().getProjectModelProvider(),
                SynonymsManager.getInstance());
    }

    /**
     * @param projectModelProvider
     * @param synonymsManager
     */
    protected TEMSSaver(final ITimePeriodNodeProperties timePeriodNodeProperties, final IGeoNodeProperties geoNodeProperties,
            final IDriveModelProvider driveModelProvider, final IProjectModelProvider projectModelProvider,
            final SynonymsManager synonymsManager) {
        super(timePeriodNodeProperties, geoNodeProperties, driveModelProvider, projectModelProvider, synonymsManager);
    }

    @Override
    protected IDriveType getDriveType() {
        return DriveType.TEMS;
    }

}
