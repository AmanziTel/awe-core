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

package org.amanzi.neo.loader.core.validator.impl.network;

import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.impl.internal.AbstractValidator;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkValidator extends AbstractValidator<ISingleFileConfiguration> {

    protected NetworkValidator(IProjectModelProvider projectModelProvider, INetworkModelProvider networkModelProvider) {

    }

    @Override
    protected IValidationResult checkFileContents(ISingleFileConfiguration configuration) {
        return null;
    }

    @Override
    protected IValidationResult checkModelExists(ISingleFileConfiguration configuration) {
        return null;
    }

}
