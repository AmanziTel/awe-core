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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;

/**
 * 
 * TODO Purpose of 
 * <p>
 *  Separation constraint validator
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class SeparationConstraintValidator extends AbstractNetworkValidator{
    
    private final static String DATASET_TYPE = "separation";
    private Map<String, String[]> map = new HashMap<String, String[]>();

    @Override
    public Result appropriate(List<File> filesToLoad) {
        for (File file : filesToLoad) {

            // checking for file expansion
            String name = file.getName();
            String[] part = name.split("\\.");
            int size = part.length;
            if (!part[size - 1].equals("csv") && !part[size - 1].equals("txt")) {
                return Result.FAIL;
            }

            //checking for file headers
            map.put("sector", new String[] {"name","separation"});
            Result result = ValidatorUtils.checkFileAndHeaders(file, 3, DATASET_TYPE, null, map, possibleFieldSepRegexes).getResult();
            if (result == Result.FAIL) {
                return result;
            }
        }

        return Result.SUCCESS;
    }

    @Override
    public IValidateResult validate(NetworkConfiguration filesToLoad) {
        if (filesToLoad.getDatasetName() == null) {
            return new ValidateResultImpl(Result.FAIL, "There is no project name");
        }
        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
            String networkName = filesToLoad.getDatasetName();
            INetworkModel network = projectModel.findNetwork(networkName);
            if (network == null || networkName == null) {
                return new ValidateResultImpl(Result.FAIL, "Network %s is not exist in database");
            }
            if(appropriate(filesToLoad.getFilesToLoad()) == Result.FAIL){
                return new ValidateResultImpl(Result.FAIL, "The file no contains traffic data");
            }
        } catch (AWEException e) {
            return new ValidateResultImpl(Result.FAIL, "Error while Traffic data validate");
        }

        return new ValidateResultImpl(Result.SUCCESS, "");
    }

}
