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

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;

/**
 * TODO Purpose of
 * <p>
 * validator for trx data loader
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class TrxDataValidator extends AbstractNetworkValidator {

    /**
     * 
     */
    public TrxDataValidator() {
        super();
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        for (File f : fileToLoad) {
            result = ValidatorUtils.checkFileAndHeaders(f, 3,
                    new String[] {DataLoadPreferences.NH_TRX_ID, DataLoadPreferences.NH_SECTOR, DataLoadPreferences.NH_SUBCELL},
                    possibleFieldSepRegexes).getResult();
            if (result == Result.FAIL) {
                message = "File" + f.getName() + " doesn't contain correct header";
                return result;
            }
        }

        return result;
    }

}
