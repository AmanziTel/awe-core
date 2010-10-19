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
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;

/**
 * <p>
 *Network site validator
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkSiteDataValidator implements ILoaderInputValidator<CommonConfigData>{
    private String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};

    @Override
    public IValidateResult validate(CommonConfigData data) {
        try {
            File file = data.getRoot();
            if (file == null || !file.isFile()) {
                return new ValidateResultImpl(Result.FAIL, "incorrect file");
            }
            String del = LoaderUtils.defineDelimeters(file, 3, possibleFieldSepRegexes);
            String[]header=LoaderUtils.getCSVRow(file,3,1,del.charAt(0));
            if (header==null){
                return new ValidateResultImpl(Result.FAIL, "not found correct header row");
            }
            int sectorHeader=LoaderUtils.findHeaderId(header,LoaderUiUtils.getPossibleHeaders(DataLoadPreferences.NH_SITE),0);
            if (sectorHeader>=0){
                    return new ValidateResultImpl(Result.SUCCESS, "");
            }
            return new ValidateResultImpl(Result.UNKNOWN, "not found all necessary headers");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new ValidateResultImpl(Result.FAIL, e.getLocalizedMessage());
        }
    }

    @Override
    public void filter(CommonConfigData data) {
    }

}
