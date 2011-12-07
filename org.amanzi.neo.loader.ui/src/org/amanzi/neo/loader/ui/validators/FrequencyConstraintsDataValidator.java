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
 * <p>
 * Validator for FrequencyConstraints loader
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FrequencyConstraintsDataValidator extends AbstractNetworkValidator {

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        for (File f : fileToLoad) {
            result = ValidatorUtils.checkFileAndHeaders(
                    f,
                    5,
                    new String[] {DataLoadPreferences.NH_SECTOR, DataLoadPreferences.FR_CH_TYPE, DataLoadPreferences.FR_FREQUENCY,
                            DataLoadPreferences.FR_PENALTY, DataLoadPreferences.FR_TRX_ID, DataLoadPreferences.FR_SCALLING_FACTOR},
                    possibleFieldSepRegexes).getResult();
            if (result == Result.FAIL) {
                message = "File" + f.getName() + " doesn't contain correct header";
                return result;
            }
        }

        return result;
    }

    /**
     * 
     */
    public FrequencyConstraintsDataValidator() {
        super();
    }

}
