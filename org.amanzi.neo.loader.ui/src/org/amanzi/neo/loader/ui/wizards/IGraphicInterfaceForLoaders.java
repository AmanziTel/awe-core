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

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * <p>
 *Interface for GUI which working with loaders
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface IGraphicInterfaceForLoaders<T extends IConfigurationData> extends IWorkbenchWizard ,IExecutableExtension {


    void addLoader(ILoader<? extends IDataElement,T>loader,IConfigurationElement[] pageConfigElements);

}
