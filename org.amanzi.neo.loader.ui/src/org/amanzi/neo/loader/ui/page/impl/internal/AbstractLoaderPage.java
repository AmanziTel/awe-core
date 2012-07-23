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

package org.amanzi.neo.loader.ui.page.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.eclipse.jface.wizard.WizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLoaderPage<T extends IConfiguration> extends WizardPage implements ILoaderPage<T> {

    private final List<ILoader<T, ? >> loaders = new ArrayList<ILoader<T, ? >>();

    /**
     * @param pageName
     */
    protected AbstractLoaderPage(final String pageName) {
        super(pageName);
    }

    @Override
    public void addLoader(final ILoader<T, ? > loader) {
        loaders.add(loader);
    }

    @Override
    public List<ILoader<T, ? >> getLoaders() {
        return loaders;
    }

}
