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

package org.amanzi.neo.loader.ui.page;

import java.util.List;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface ILoaderPage<T extends IConfiguration> extends IWizardPage {

    public void addLoader(ILoader<T, ? > loader);

    public List<ILoader<T, ? >> getLoaders();

    ILoader<T, ? > getCurrentLoader();
}
