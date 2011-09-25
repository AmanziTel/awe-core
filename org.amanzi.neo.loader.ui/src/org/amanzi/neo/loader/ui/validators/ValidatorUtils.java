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

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Validator utils
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ValidatorUtils {
    private ValidatorUtils() {
        // hide constructor
    }

    public static IValidateResult checkFileAndHeaders(File file, int minSize, String[] constants, String[] possibleFieldSepRegexes) {
        return checkFileAndHeaders(file, minSize, constants, possibleFieldSepRegexes, true);
    }

    public static IValidateResult checkFileAndHeaders(File file, int minSize, String[] constants, String[] possibleFieldSepRegexes,
            boolean convertConstants) {
        try {
            if (file == null || !file.isFile()) {
                return new ValidateResultImpl(Result.FAIL, "incorrect file");
            }
            String del = LoaderUtils.defineDelimeters(file, minSize, possibleFieldSepRegexes);
            String[] header = LoaderUtils.getCSVRow(file, minSize, 1, del.charAt(0));
            if (header == null) {
                return new ValidateResultImpl(Result.FAIL, "not found correct header row");
            }
            for (String constant : constants) {
                String[] possibleHeaders = null;
                if (convertConstants) {
                    possibleHeaders = LoaderUiUtils.getPossibleHeaders(constant);
                } else {
                    possibleHeaders = new String[] {constant};
                }
                int id = LoaderUtils.findHeaderId(header, possibleHeaders, 0);
                if (id < 0) {
                    return new ValidateResultImpl(Result.UNKNOWN, "not found all necessary headers");
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
     * Check root exist.
     * 
     * @param data the data
     * @return validation result
     */
    public static IValidateResult checkRootExist(CommonConfigData data) {
        if (data.getProjectName() == null || data.getDbRootName() == null)
            return new ValidateResultImpl(Result.FAIL, " is not found.");
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        Node root = datasetService.findRoot(data.getProjectName(), data.getDbRootName());
        if (root == null) {
            return new ValidateResultImpl(Result.FAIL, String.format(" '%s' is not found. ", data.getDbRootName())
                    + "For loader '%s' network should exist.");
        }
        return new ValidateResultImpl(Result.SUCCESS, "");
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

    public static CoordinateReferenceSystem defineCRS(File file, int size, String[] constants, String[] possibleFieldSepRegexes,
            boolean convertConstants) {
        String[][] dataMap = ValidatorUtils.getDataMap(file, size, constants, possibleFieldSepRegexes, convertConstants);
        if (dataMap == null) {
            return null;
        }
        Double lat = null;
        Double lon = null;
        String hint = null;
        for (int i = 0; i < dataMap.length; i++) {
            try {
                lat = Double.parseDouble(dataMap[i][0]);
                lon = Double.parseDouble(dataMap[i][1]);
                if (lat != null && lon != null) {
                    break;
                }
            } catch (Exception e) {
                if (dataMap[i][0].contains("wert")) {
                    hint = "germany";
                }
                lat = null;
                lon = null;
            }
        }
        return AweUiPlugin.getDefault().getUiService().defineCRS(lat, lon, hint);
    }
}
