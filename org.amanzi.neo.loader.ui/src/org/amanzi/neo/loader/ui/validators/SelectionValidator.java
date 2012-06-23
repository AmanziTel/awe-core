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
import org.amanzi.neo.services.model.ISelectionModel;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * Selection validator
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class SelectionValidator extends AbstractValidator<NetworkConfiguration> {

    private static final String DATASET_TYPE = "selection";
    private Map<String, String[]> map = new HashMap<String, String[]>();

    // parameters
    private static final String[] PARAMETERS = new String[] {"name"};

    // messages
    private static final String SELECTION_NOT_EXIST = "Selection model is not exist in database";
    private static final String NO_CONTENT = "The file no contains selection data";
    private static final String ERROR = "Error while Selection data validate";

    private static final int size = 1;

    @Override
    public Result appropriate(List<File> filesToLoad) {
        for (File file : filesToLoad) {

            // checking for file expansion
            if (!checkFileByExtension(file, CSV) && !checkFileByExtension(file, TXT)) {
                return Result.FAIL;
            }

            // checking for file headers
            map.put(SECTOR, PARAMETERS);
            Result result = checkFileAndHeaders(file, size, DATASET_TYPE, null, map, POSSIBLE_SEPARATIONS).getResult();

            if (result == Result.FAIL || result == Result.UNKNOWN) {
                return result;
            }

            result = checkFileByHeaderNumber(file, size);

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
            if (network == null) {
                return new ValidateResultImpl(Result.FAIL, NETWORK_NOT_EXIST);
            }
            String selectionName = filesToLoad.getFile().getName();
            ISelectionModel selection = network.getSelectionModel(selectionName);
            if (selection == null) {
                return new ValidateResultImpl(Result.FAIL, SELECTION_NOT_EXIST);
            }
            Result result = appropriate(filesToLoad.getFilesToLoad());
            if (result == Result.FAIL || result == Result.UNKNOWN) {
                return new ValidateResultImpl(Result.FAIL, NO_CONTENT);
            }
        } catch (AWEException e) {
            return new ValidateResultImpl(Result.FAIL, ERROR);
        }

        return new ValidateResultImpl(Result.SUCCESS, StringUtils.EMPTY);
    }

}
