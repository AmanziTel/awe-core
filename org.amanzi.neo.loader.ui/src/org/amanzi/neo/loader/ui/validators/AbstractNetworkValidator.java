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

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;

/**
 * common action for network config validation
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractNetworkValidator implements IValidator {
    protected static final Logger LOGGER = Logger.getLogger(AbstractNetworkValidator.class);
    protected String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    protected Result result = Result.FAIL;
    protected String message = "";
    protected IConfiguration config;

    @Override
    public Result getResult() {
        return result;
    }

    /**
     * 
     */
    public AbstractNetworkValidator() {
        super();
    }

    @Override
    public String getMessages() {
        return String.format(message, config.getDatasetNames().get(IConfiguration.NETWORK_PROPERTY_NAME));
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
                if (network != null) {
                    result = Result.SUCCESS;
                    message = String.format("");
                    return result;
                } else {
                    message = String.format("Should select some network to load");
                }
            } catch (AWEException e) {
                LOGGER.error("Error while validate Data", e);
                message = String.format("Error while validate Data");
                return Result.FAIL;
            }
        }

        return Result.FAIL;
    }
}
