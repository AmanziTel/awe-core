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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;

/**
 * IValidator implementation contain methods for validation xml data files
 * 
 * @author Kondratenko_Vladislav
 */
public class XMLValidator implements IValidator {
    private static final Logger LOGGER = Logger.getLogger(XMLValidator.class);
    Result result = Result.FAIL;
    String message = "";

    /**
     * check correct file extension
     * 
     * @param fileToLoad
     * @return
     */
    private void checkFileExtension(List<File> fileToLoad, String extension) {

        List<File> tempFileToLoad = new LinkedList<File>(fileToLoad);
        for (File f : tempFileToLoad) {
            if (f.getName().lastIndexOf(extension) == -1) {
                String fileName = f.getName();
                message += fileName + " has incorrect extension\n";
                // System.out.println(fileName + " has incorrect extension");
                fileToLoad.remove(f);
            }
        }
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        if (fileToLoad == null || fileToLoad.size() < 1) {
            message = "there is no fail founded";
            result = Result.FAIL;
            return Result.FAIL;
        }
        checkFileExtension(fileToLoad, ".xml");
        checkFileContent(fileToLoad);
        if (fileToLoad.size() > 0) {
            result = Result.SUCCESS;
            return Result.SUCCESS;
        } else {
            result = Result.FAIL;
            return Result.FAIL;
        }
    }

    @Override
    public Result isValid(IConfiguration config) {
        if (config.getDatasetNames().get("Project") == null) {
            return Result.FAIL;
        }

        if (result == Result.SUCCESS) {
            INetworkModel network;
            IDriveModel dataset;
            try {
                IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
                network = projectModel.findNetwork(config.getDatasetNames().get("Network"));
                dataset = projectModel.findDataset(config.getDatasetNames().get("Dataset"), DriveTypes.AMS);
                if (network == null && dataset != null) {
                    message = String.format("Drive node %s is already exists in db ", config.getDatasetNames().get("Dataset"));
                    return Result.FAIL;
                } else if (dataset == null && network != null) {
                    message = String.format("Network node %s is already exists in db ", config.getDatasetNames().get("Network"));
                    return Result.FAIL;
                } else if (dataset != null && network != null) {
                    message = String.format("Network and Dataset nodes  is already exists in db ");
                    return Result.FAIL;
                } else {
                    message = "";
                    result = Result.SUCCESS;
                    return result;
                }
            } catch (AWEException e) {
                LOGGER.error("ERROR while ams xml data validate ", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }

        }
        return Result.FAIL;
    }

    /**
     * check for file has correct format
     * 
     * @param fileToLoad
     */
    private void checkFileContent(List<File> fileToLoad) {

        try {
            List<File> tempFileToLoad = new LinkedList<File>(fileToLoad);
            for (File f : tempFileToLoad) {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String str;
                for (int i = 0; i < 2; i++) {
                    str = in.readLine();
                    if (str == null || str.equals("")) {
                        message += f.getName() + " has incorrect format\n";
                        fileToLoad.remove(f);
                        break;
                    }
                    if (i == 1) {
                        if (str.indexOf("<tns") == -1) {
                            fileToLoad.remove(f);
                            break;
                        }
                    }
                }
                in.close();
            }
        }

        catch (IOException e) {
            LOGGER.error("Unable to validate files " + fileToLoad.toString(), e);
            return;
        }
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return message;
    }
}
