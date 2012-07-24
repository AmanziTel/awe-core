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

package org.amanzi.neo.loader.core.impl.internal;

import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.Messages;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidationResult.Result;
import org.amanzi.neo.loader.core.validator.ValidationResult;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractConfiguration implements IConfiguration {

    private String datasetName;

    @Override
    public void setDatasetName(final String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public String getDatasetName() {
        return datasetName;
    }

    @Override
    public IValidationResult isValid() {
        IValidationResult result = IValidationResult.SUCCESS;

        if (StringUtils.isEmpty(datasetName)) {
            result = new ValidationResult(Result.FAIL, Messages.AbstractConfiguration_EmptyDatasetNameError);
        }

        return result;

    }

}
