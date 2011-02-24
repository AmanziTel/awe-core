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

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;

/**
 * <p>
 * TrafficDataValidator
 * </p>
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class TrafficDataValidator implements ILoaderInputValidator<CommonConfigData> {

    private String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};

    @Override
    public IValidateResult validate(CommonConfigData data) {
        IValidateResult result = ValidatorUtils.checkRootExist(data);
        if (result.getResult() != Result.SUCCESS) {
            return result;
        }
        return accept(data);

    }

    @Override
    public void filter(CommonConfigData data) {
        // TODO Auto-generated method stub

    }

    @Override
    public IValidateResult accept(CommonConfigData data) {
        return ValidatorUtils.checkFileAndHeaders(data.getRoot(), 2, 
                new String[] {"Sector", "Traffic"}, possibleFieldSepRegexes,
                false);
    }

}
