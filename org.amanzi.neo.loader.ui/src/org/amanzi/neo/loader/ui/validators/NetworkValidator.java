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
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;

/**
 * check common Network validation methods
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkValidator implements IValidator {
    private static final Logger LOGGER = Logger.getLogger(NetworkValidator.class);
    private String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    private Result result = Result.FAIL;
    private String message = "";

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return message;
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        for (File f : fileToLoad) {
            result = ValidatorUtils
                    .checkFileAndHeaders(f, 2, new String[] {DataLoadPreferences.NH_SECTOR}, possibleFieldSepRegexes).getResult();
            return result;
        }

        return Result.FAIL;
    }

    @Override
    public Result isValid(IConfiguration config) {
        if (config.getDatasetNames().get("Project") == null) {
            return Result.FAIL;
        }
        if (result == Result.SUCCESS) {

            try {
                IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
                INetworkModel network = projectModel.findNetwork(config.getDatasetNames().get("Network"));
                if (network == null) {
                    result = Result.SUCCESS;
                    return result;
                }
            } catch (AWEException e) {
                LOGGER.error("Error while network data validate", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        message = String.format("Network %s is already exists in db ", config.getDatasetNames().get("Network"));
        return Result.FAIL;
    }

}
