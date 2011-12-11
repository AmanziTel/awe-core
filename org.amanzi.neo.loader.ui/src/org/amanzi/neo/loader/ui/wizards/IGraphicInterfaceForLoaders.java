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

package org.amanzi.neo.loader.ui.wizards;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.saver.IData;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Vladislav_Kondratenko
 */
public interface IGraphicInterfaceForLoaders<T extends IConfiguration> extends IWorkbenchWizard, IExecutableExtension {
    void addLoaderToPage(ILoader<IData, T> loader, IConfigurationElement pageConfigElements);
}
