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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.NodeTypeSynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.PropertySynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.Synonym;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;

/**
 * <p>
 * Validator utils
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ValidatorUtils {
    
    //separators
    public final static String[] possibleFieldSepRegexes = new String[] {"\t",",",";"};
    
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
     * 
     * @author Ladornaya_A
     */
    public static IValidateResult checkFileAndHeaders(File file, int minSize, String datasetType, String subType, Map<String,String[]> mandatoryHeaders,
            String[] possibleFieldSepRegexes) {
        try {
            if (file == null || !file.isFile()) {
                return new ValidateResultImpl(Result.FAIL, "incorrect file");
            }
            String del = LoaderUtils.defineDelimeters(file, minSize, possibleFieldSepRegexes);
            String[] header = LoaderUtils.getCSVRow(file, minSize, 1, del.charAt(0));
            if (header == null) {
                return new ValidateResultImpl(Result.FAIL, "not found correct header row");
            }

            List<String> headers = new ArrayList<String>(Arrays.asList(header));
            NodeTypeSynonyms nodeTypeSynonyms = ImportSynonymsManager.getManager().getNodeTypeSynonyms(datasetType, subType);

            for(String mandatoryKey:mandatoryHeaders.keySet()){
                PropertySynonyms propertySynonyms = nodeTypeSynonyms.get(mandatoryKey);
                String[] mandatoryValues = mandatoryHeaders.get(mandatoryKey);
                for(String mandatoryValue:mandatoryValues){
                    Synonym synonym = new Synonym(mandatoryValue);
                    String[] possibleHeadersArray = propertySynonyms.get(synonym);
                    boolean flag = false;
                    for(String possibleHeader:possibleHeadersArray){
                        if(headers.contains(possibleHeader)){
                            flag = true;
                        }
                    }
                    if(flag == false){
                        return new ValidateResultImpl(Result.UNKNOWN, "not found all necessary headers");
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
     * Gets the data map.
     * 
     * @param file the file
     * @param size the size
     * @param constants the constants
     * @param possibleFieldSepRegexes the possible field separations regexes
     * @param convertConstants the convert constants
     * @return the data map
     */
    public static String[][] getDataMap(File file, int size, String[] constants, String[] possibleFieldSepRegexes,
            boolean convertConstants) {
        String[][] result = new String[size][constants.length];
        String del = LoaderUtils.defineDelimeters(file, constants.length, possibleFieldSepRegexes);
        String[][] lines = LoaderUtils.getCSVRows(file, constants.length, 1, size, del.charAt(0));
        if (lines == null || lines.length == 0) {
            return null;
        }
        int i = -1;
        for (String constant : constants) {
            i++;
            String[] possibleHeaders = null;
            if (convertConstants) {
                possibleHeaders = LoaderUiUtils.getPossibleHeaders(constant);
            } else {
                possibleHeaders = new String[] {constant};
            }
            int id = LoaderUtils.findHeaderId(lines[0], possibleHeaders, 0);
            if (id >= 0) {
                for (int j = 0; j < size; j++) {
                    result[j][i] = lines[j][id];
                }
            }
        }
        return result;
    }
}
