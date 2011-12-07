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

package org.amanzi.neo.loader.ui.validators;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;

/**
 * check common Network validation methods
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkValidator extends AbstractNetworkValidator {

    /**
     * 
     */
    public NetworkValidator() {
        super();
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        for (File f : fileToLoad) {
            result = ValidatorUtils
                    .checkFileAndHeaders(
                            f,
                            3,
                            new String[] {DataLoadPreferences.NH_LATITUDE, DataLoadPreferences.NH_LONGITUDE,
                                    DataLoadPreferences.NH_SECTOR}, possibleFieldSepRegexes).getResult();
            if (result == Result.FAIL) {
                message = "File" + f.getName() + " doesn't contain correct header";
                return result;
            }
        }

        return result;
    }

    @Override
    public Result isValid(IConfiguration config) {
        this.config = config;
        if (config.getDatasetNames().get(IConfiguration.PROJECT_PROPERTY_NAME) == null) {
            message = String.format("there is no project name");
            return Result.FAIL;
        }
        if (result == Result.SUCCESS) {
            try {
                IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
                String networkName = config.getDatasetNames().get(IConfiguration.NETWORK_PROPERTY_NAME);
                INetworkModel network = projectModel.findNetwork(networkName);
                if (network == null && networkName != null) {
                    result = Result.SUCCESS;
                    return result;
                } else {
                    message = "Network %s is already exist in database";

                }
            } catch (AWEException e) {
                LOGGER.error("Error while Sector selection data validate", e);
                return Result.FAIL;
            }
        }

        return Result.FAIL;
    }
}
