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

package org.amanzi.awe.ui.tree.view;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
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
public final class FactoryResolver {

    private static final Logger LOGGER = Logger.getLogger(FactoryResolver.class);

    private static final String EXTENSION_POINT_NAME = "org.amanzi.tree.factories";

    private static final String CLASS_PROPERTY = "class";

    private static final String VIEW_ID_PROPERTY = "tree_id";

    private static final class FactoryResolverHolder {
        private static volatile FactoryResolver instance = new FactoryResolver();
    }

    private final IExtensionRegistry registry;

    private FactoryResolver() {
        this.registry = Platform.getExtensionRegistry();
    }

    public static synchronized FactoryResolver getResolver() {
        return FactoryResolverHolder.instance;
    }

    public synchronized Set<ITreeWrapperFactory> getWrapperFactories(final String viewId) {
        return initializeFactories(findAllExtensionsForView(viewId));
    }

    private Set<ITreeWrapperFactory> initializeFactories(final Set<IConfigurationElement> elements) {
        final Set<ITreeWrapperFactory> result = new HashSet<ITreeWrapperFactory>();

        for (final IConfigurationElement element : elements) {
            try {
                final ITreeWrapperFactory factory = (ITreeWrapperFactory)element.createExecutableExtension(CLASS_PROPERTY);
                result.add(factory);
            } catch (final CoreException e) {
                LOGGER.error("Unable to create TreeWrapperFactory", e);
            }
        }

        return result;
    }

    private Set<IConfigurationElement> findAllExtensionsForView(final String viewId) {
        final Set<IConfigurationElement> result = new HashSet<IConfigurationElement>();

        for (final IConfigurationElement element : registry.getConfigurationElementsFor(EXTENSION_POINT_NAME)) {
            if (element.getAttribute(VIEW_ID_PROPERTY).equals(viewId)) {
                result.add(element);
            }
        }

        return result;
    }
}
