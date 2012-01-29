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
import org.apache.commons.lang.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * Interference matrix validator
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class InterferenceMatrixValidator extends AbstractValidator<NetworkConfiguration> {

    private final static String DATASET_TYPE = "n2n";
    private final static String SUB_TYPE = "interference";
    private Map<String, String[]> map = new HashMap<String, String[]>();

    // parameters for this type
    private final static String[] PARAMETERS = new String[] {"serving_name", "interfering_name", "source", "co", "adj"};

    // messages
    private final static String NO_N2N_NAME = "There is no n2n name";
    private final static String NO_CONTENT = "The file no contains interferense matrix";
    private final static String ERROR = "Error while interferense matrix validate";

    @Override
    public Result appropriate(List<File> filesToLoad) {
        for (File file : filesToLoad) {

            // checking for file expansion
            if (!checkFileByExtension(file, CSV) && !checkFileByExtension(file, TXT)) {
                return Result.FAIL;
            }

            // checking for file headers
            map.put(SECTOR, PARAMETERS);
            Result result = checkFileAndHeaders(file, 5, DATASET_TYPE, SUB_TYPE, map, POSSIBLE_SEPARATIONS).getResult();
            if (result == Result.FAIL) {
                return result;
            }
        }

        return Result.SUCCESS;
    }

    @Override
    public IValidateResult validate(NetworkConfiguration filesToLoad) {
        if (filesToLoad.getDatasetName() == null) {
            return new ValidateResultImpl(Result.FAIL, NO_PROJECT);
        }
        try {
            INetworkModel network = findNetwork(filesToLoad);
            if (network != null) {
                return new ValidateResultImpl(Result.FAIL, NETWORK_IS_ALREADY_EXIST);
            }
            String n2nName = filesToLoad.getFile().getName();
            if (n2nName == null) {
                return new ValidateResultImpl(Result.FAIL, NO_N2N_NAME);
            }
            if (appropriate(filesToLoad.getFilesToLoad()) == Result.FAIL) {
                return new ValidateResultImpl(Result.FAIL, NO_CONTENT);
            }
        } catch (AWEException e) {
            return new ValidateResultImpl(Result.FAIL, ERROR);
        }

        return new ValidateResultImpl(Result.SUCCESS, StringUtils.EMPTY);
    }

}
