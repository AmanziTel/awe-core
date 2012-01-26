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

import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;

/**
 * 
 * TODO Purpose of 
 * <p>
 * Antenna patterns validator
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaValidator implements IValidator<NetworkConfiguration>{

    @Override
    public Result appropriate(List<File> filesToLoad) {
        return null;
    }

    @Override
    public IValidateResult validate(NetworkConfiguration configuration) {
        return null;
    }

}
