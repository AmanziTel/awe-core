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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.NodeTypeSynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.PropertySynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.Synonym;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of
 * <p>
 * Abstract class for validators
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 * @param <T> configuration
 */
public abstract class AbstractValidator<T extends IConfiguration> implements IValidator<T> {

    // extensions
    public final static String CSV = ".csv";
    public final static String TXT = ".txt";
    public final static String MSI = ".msi";
    public final static String CHR = ".chr";
    public final static String GZ = ".gz";
    public final static String BIN = ".bin";
    public final static String XML = ".xml";

    // separators
    public final static String[] POSSIBLE_SEPARATIONS = new String[] {"\t", ",", ";"};

    // types
    public final static String SITE = "site";
    public final static String SECTOR = "sector";

    // messages
    public final static String NO_PROJECT = "There is no project name";
    public final static String NETWORK_NOT_EXIST = "Network is not exist in database";
    public final static String NETWORK_IS_ALREADY_EXIST = "Network is already exist in database";

    // messages for file
    private final static String INCORRECT_FILE = "Incorrect file";
    private final static String NO_HEADER_ROW = "Not found correct header row";
    private final static String NO_NECESSARY_HEADERS = "Not found all necessary headers";

    /**
     * checks file by extension
     * 
     * @param file
     * @param extension
     * @return result
     */
    public static boolean checkFileByExtension(File file, String extension) {
        String name = file.getName();
        if (!name.endsWith(extension)) {
            return false;
        }
        return true;
    }

    /**
     * Checks file by header number
     * 
     * @param file
     * @param size
     * @return result
     */
    public static Result checkFileByHeaderNumber(File file, int size) {
        CSVReader reader = null;
        try {
            CountingFileInputStream is = new CountingFileInputStream(file);
            String del = LoaderUtils.defineDelimeters(file, size, POSSIBLE_SEPARATIONS);
            reader = new CSVReader(new InputStreamReader(is), del.charAt(0));
            String[] line;
            if ((line = reader.readNext()) != null) {
                if (line.length != size) {
                    return Result.FAIL;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.SUCCESS;
    }

    /**
     * Check file and headers by dataset synonyms
     * 
     * @param file loading file
     * @param minSize
     * @param datasetType
     * @param subType
     * @param possibleFieldSepRegexes
     * @param convertConstants
     * @return validate result
     */
    public IValidateResult checkFileAndHeaders(File file, int minSize, String datasetType, String subType,
            Map<String, String[]> mandatoryHeaders, String[] possibleFieldSepRegexes) {
        try {
            if (file == null || !file.isFile()) {
                return new ValidateResultImpl(Result.FAIL, INCORRECT_FILE);
            }
            String del = LoaderUtils.defineDelimeters(file, minSize, possibleFieldSepRegexes);
            String[] header = LoaderUtils.getCSVRow(file, minSize, 1, del.charAt(0));
            if (header == null) {
                return new ValidateResultImpl(Result.FAIL, NO_HEADER_ROW);
            }

            List<String> headers = new ArrayList<String>(Arrays.asList(header));
            NodeTypeSynonyms nodeTypeSynonyms = ImportSynonymsManager.getManager().getNodeTypeSynonyms(datasetType, subType);

            for (String mandatoryKey : mandatoryHeaders.keySet()) {
                PropertySynonyms propertySynonyms = nodeTypeSynonyms.get(mandatoryKey);
                String[] mandatoryValues = mandatoryHeaders.get(mandatoryKey);
                for (String mandatoryValue : mandatoryValues) {
                    Synonym synonym = new Synonym(mandatoryValue);
                    String[] possibleHeadersArray = propertySynonyms.get(synonym);
                    boolean flag = false;
                    for (String possibleHeader : possibleHeadersArray) {
                        if (headers.contains(possibleHeader)) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        return new ValidateResultImpl(Result.UNKNOWN, NO_NECESSARY_HEADERS);
                    }
                }

            }

            return new ValidateResultImpl(Result.SUCCESS, "");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new ValidateResultImpl(Result.FAIL, e.getLocalizedMessage());
        }
    }
    
    /**
     * Find networks
     *
     * @param configuration 
     * @return network model
     * @throws AWEException 
     */
    public INetworkModel findNetwork(IConfiguration configuration) throws AWEException{
        IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
        String networkName = configuration.getDatasetName();
        return projectModel.findNetwork(networkName);
    }

    @Override
    public Result appropriate(List<File> filesToLoad) {
        return null;
    }

    @Override
    public IValidateResult validate(IConfiguration configuration) {
        return null;
    }

}
