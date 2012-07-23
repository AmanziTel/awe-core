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

package org.amanzi.neo.loader.core.impl;

import java.io.File;

import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.internal.AbstractConfiguration;
import org.amanzi.neo.loader.core.internal.Messages;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidationResult.Result;
import org.amanzi.neo.loader.core.validator.ValidationResult;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SingleFileConfiguration extends AbstractConfiguration implements ISingleFileConfiguration {

    private File file;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public IValidationResult isValid() {
        IValidationResult result = super.isValid();

        if (result.getResult() == IValidationResult.Result.SUCCESS) {
            if (file == null) {
                result = new ValidationResult(Result.FAIL, Messages.SingleFileConfiguration_NullFile);
            } else if (!file.exists()) {
                result = new ValidationResult(Result.FAIL, Messages.format(Messages.SingleFileConfiguration_FileNotExists, file));
            } else if (!file.isFile()) {
                result = new ValidationResult(Result.FAIL,
                        Messages.format(Messages.SingleFileConfiguration_LocationIsNotFile, file));
            }
        }

        return result;
    }
}
