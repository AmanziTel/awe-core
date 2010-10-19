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

import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;

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
                int id = LoaderUtils.findHeaderId(header, LoaderUiUtils.getPossibleHeaders(constant), 0);
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

}
