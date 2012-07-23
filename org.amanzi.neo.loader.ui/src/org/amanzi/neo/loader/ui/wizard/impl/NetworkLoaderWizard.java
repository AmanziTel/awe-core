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

package org.amanzi.neo.loader.ui.wizard.impl;

import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.SingleFileConfiguration;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.impl.internal.AbstractLoaderWizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkLoaderWizard extends AbstractLoaderWizard<ISingleFileConfiguration> {

    private ISingleFileConfiguration configuration;

    @SuppressWarnings("unchecked")
    @Override
    public <C extends IConfiguration> C getConfiguration(final ILoaderPage<C> loaderPage) {
        if (configuration == null) {
            configuration = new SingleFileConfiguration();
        }
        return (C)configuration;
    }
}
