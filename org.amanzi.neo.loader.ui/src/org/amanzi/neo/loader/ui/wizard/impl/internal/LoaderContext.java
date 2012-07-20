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

package org.amanzi.neo.loader.ui.wizard.impl.internal;

import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoaderContext {

    protected static final String LOADER_WIZARD_EXTENSION_ID = "org.amanzi.loader.wizards";

    protected static final String LOADER_PAGE_EXTENSION_ID = "org.amanzi.loader.pages";

    private static class LoaderContextHandler {
        private static volatile LoaderContext INSTANCE = new LoaderContext();
    }

    private final IExtensionRegistry registry;

    protected LoaderContext(final IExtensionRegistry registry) {
        this.registry = registry;
    }

    private LoaderContext() {
        this(Platform.getExtensionRegistry());
    }

    public static LoaderContext getInstance() {
        return LoaderContextHandler.INSTANCE;
    }

    public ILoaderWizard getLoaderWizard(final String id) {
        return null;
    }
}
