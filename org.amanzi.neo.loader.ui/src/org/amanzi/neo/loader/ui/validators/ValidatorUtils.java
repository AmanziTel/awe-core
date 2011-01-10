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

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.neo4j.graphdb.Node;

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

    public static IValidateResult checkFileAndHeaders(File file, int minSize, String[] constants, String[] possibleFieldSepRegexes, boolean convertConstants) {
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
                }
                else {
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
            return new ValidateResultImpl(Result.FAIL, String.format(" '%s' is not found. ", data.getDbRootName()) + "For loader '%s' network should exist.");
        }
        return new ValidateResultImpl(Result.SUCCESS, "");
    }
}
