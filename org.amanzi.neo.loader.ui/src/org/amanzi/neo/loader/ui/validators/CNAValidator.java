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

public class CNAValidator implements IValidator {

    private String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    private Result result = Result.FAIL;
    private String message = "";
    private static Logger LOGGER = Logger.getLogger(CNAValidator.class);

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
            result = ValidatorUtils.checkFileAndHeaders(
                    f,
                    3,
                    new String[] {DataLoadPreferences.NH_SECTOR, DataLoadPreferences.NH_AZIMUTH, DataLoadPreferences.NH_SECTOR_CI,
                            DataLoadPreferences.NH_SECTOR_LAC}, possibleFieldSepRegexes).getResult();
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
                if (network != null) {
                    result = Result.SUCCESS;
                    return result;
                }
            } catch (AWEException e) {
                LOGGER.error("Error while Sector selection data validate", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        message = String.format("Should select some network to build cna model");
        return Result.FAIL;
    }

}
