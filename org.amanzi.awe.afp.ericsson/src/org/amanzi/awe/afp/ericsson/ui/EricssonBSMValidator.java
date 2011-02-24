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

package org.amanzi.awe.afp.ericsson.ui;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.ui.validators.ValidatorUtils;

/**
 * <p>
 *Validator of Ericsson BSM Files
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class EricssonBSMValidator implements ILoaderInputValidator<CommonConfigData> {
    private Pattern bscPattern = Pattern.compile("(^.*)(_BSM.log*$)", Pattern.CASE_INSENSITIVE);
    @Override
    public void filter(CommonConfigData data) {
        Collection<File> bsmList = (Collection<File>)data.getAdditionalProperties().get("BSM_FILES");
        if (bsmList != null) {
            LinkedHashSet<File> newResult = new LinkedHashSet<File>();
            for (File file : bsmList) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    Matcher matcher = bscPattern.matcher(fileName);
                    if (matcher.find(0)) {
                        newResult.add(file);
                    }
                }
            }
            if (bsmList.size() != newResult.size()) {
                data.getAdditionalProperties().put("BSM_FILES", newResult);
            }
        }
    }

    @Override
    public IValidateResult validate(final CommonConfigData data) {
        return ValidatorUtils.checkRootExist(data);
    }

    @Override
    public IValidateResult accept(CommonConfigData data) {
        return new ValidateResultImpl(Result.SUCCESS, "");
    }

}