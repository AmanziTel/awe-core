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
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.ui.validators.ValidatorUtils;

/**
 * <p>
 * Ericson data validator
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class EricssonValidator implements ILoaderInputValidator<CommonConfigData> {

    @Override
    public void filter(CommonConfigData data) {
        if (data.getFileToLoad() != null) {
            List<File> newResult = new ArrayList<File>();
            List<File> fileToLoad = data.getFileToLoad();
            for (File file : fileToLoad) {
                if (file.isFile()) {
                    newResult.add(file);
                }
            }
            if (fileToLoad.size() != newResult.size()) {
                data.setFileToLoad(newResult);
            }
        } else if (data.getRoot() != null) {
            if (!data.getRoot().isFile()) {
                data.setRoot(null);
            }
        }
    }

    @Override
    public IValidateResult validate(final CommonConfigData data) {
        IValidateResult result=ValidatorUtils.checkRootExist(data);
        if (result.getResult()==Result.FAIL){
            return new ValidateResultImpl(result.getResult(), "Network"+result.getMessages());
        }
        return accept(data);
    }

    @Override
    public IValidateResult accept(CommonConfigData data) {
        if (data.getRoot()==null){
             return new ValidateResultImpl(Result.UNKNOWN, "Select a file for import");
        }
        return ValidatorUtils.checkFileAndHeaders(data.getRoot(), 3, new String[]{"dchno_0"}, new String[]{" "},false);
    }

}