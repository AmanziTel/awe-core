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

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;

/**
 * <p>
 * common validator interface;
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public interface IValidator<T extends IConfiguration> {
    /**
     * check files for appropriate to loader
     * 
     * @param fileToLoad
     * @return
     */
    public Result appropriate(List<File> filesToLoad);

    /**
     * check files for validation
     * 
     * @param fileToLoad
     * @return
     */
    public IValidateResult validate(T configuration);
}
