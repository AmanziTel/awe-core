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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * Antenna patterns validator
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaValidator extends AbstractValidator<NetworkConfiguration> {

    // file reader
    private CountingFileInputStream is;
    private BufferedReader reader;

    private String charSetName = Charset.defaultCharset().name();

    // separator
    private static final String SEPARATOR = StringUtils.EMPTY;

    // constants
    private static final String NAME = "NAME";
    private static final String FREQUENCY = "FREQUENCY";
    private static final String GAIN = "GAIN";

    // messages
    private final static String NO_CONTENT = "The file no contents antenna patterns data";
    private final static String ERROR = "Error while antenna patterns data validate";

    @Override
    public Result appropriate(List<File> filesToLoad) {
        for (File file : filesToLoad) {

            // checking for file expansion
            if (!checkFileByExtension(file, MSI)) {
                return Result.FAIL;
            }

            // checking for file headers
            try {
                is = new CountingFileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));

                String line1 = reader.readLine();
                if (!line1.split(SEPARATOR)[1].equals(NAME)) {
                    return Result.FAIL;
                }

                String line2 = reader.readLine();
                if (!line2.split(SEPARATOR)[1].equals(FREQUENCY)) {
                    return Result.FAIL;
                }

                String line3 = reader.readLine();
                if (!line3.split(SEPARATOR)[1].equals(GAIN)) {
                    return Result.FAIL;
                }
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (UnsupportedEncodingException e) {
                // TODO Handle UnsupportedEncodingException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
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
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
            String networkName = filesToLoad.getDatasetName();
            INetworkModel network = projectModel.findNetwork(networkName);
            if (network == null || networkName == null) {
                return new ValidateResultImpl(Result.FAIL, NETWORK_NOT_EXIST);
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
