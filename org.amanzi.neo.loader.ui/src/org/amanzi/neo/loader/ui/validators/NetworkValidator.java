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
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.neo4j.graphdb.Node;

/**
 * check common Network validation methods
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkValidator implements IValidator {
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
            NewDatasetService newDatasetService = NeoServiceFactory.getInstance().getNewDatasetService();
            DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
            Node root;
            try {
                root = newDatasetService.findDataset(
                        datasetService.findOrCreateAweProject(config.getDatasetNames().get("Project")), config.getDatasetNames()
                                .get("Network"), DatasetTypes.NETWORK);

                if (root == null) {
                    result = Result.SUCCESS;
                    return result;
                }
            } catch (InvalidDatasetParameterException e) {
                // TODO Handle InvalidDatasetParameterException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (DatasetTypeParameterException e) {
                // TODO Handle DatasetTypeParameterException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (DuplicateNodeNameException e) {
                // TODO Handle DuplicateNodeNameException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }

        }
        message = String.format("Network %s is already exists in db ", config.getDatasetNames().get("Network"));
        return Result.FAIL;
    }

}
