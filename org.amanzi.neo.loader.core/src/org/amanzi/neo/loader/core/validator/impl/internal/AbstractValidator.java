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

package org.amanzi.neo.loader.core.validator.impl.internal;

import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractValidator<T extends IConfiguration> implements IValidator<T> {

    /**
     * 
     */
    public AbstractValidator() {
    }

    @Override
    public IValidationResult appropriate(final T configuration) {
        return checkFileContents(configuration);
    }

    @Override
    public IValidationResult validate(final T configuration) {
        IValidationResult result = checkModelExists(configuration);

        if (result.getResult() == IValidationResult.Result.SUCCESS) {
            result = checkFileContents(configuration);
        }

        return result;
    }

    protected abstract IValidationResult checkFileContents(T configuration);

    protected abstract IValidationResult checkModelExists(T configuration);

    @Override
    public IOFileFilter getFileFilter() {
        IOFileFilter filter = FileFilterUtils.trueFileFilter();
        for (String extension : getSupportedFileExtensions()) {
            filter = FileFilterUtils.orFileFilter(filter, FileFilterUtils.prefixFileFilter(extension));
        }
        return filter;
    }

}
