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

package org.amanzi.awe.render.core.coloring.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.render.core.coloring.IColoringInterceptorFactory;
import org.amanzi.neo.models.render.IGISModel;
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
public class ColoringInterceptorsCache {

    private static final String EXTENSION_POINT_NAME = "org.amanzi.render.coloring";

    private static final String CLASS_PROPERTY = "class";

    private static final class CacheHandler {
        private static volatile ColoringInterceptorsCache instance = new ColoringInterceptorsCache();
    }

    private final IExtensionRegistry registry;

    private ColoringInterceptorsCache() {
        this.registry = Platform.getExtensionRegistry();
    }

    public static synchronized ColoringInterceptorsCache getCache() {
        return CacheHandler.instance;
    }

    public synchronized IColoringInterceptorFactory getFactory(final IGISModel model) {
        final List<IColoringInterceptorFactory> factories = loadFromRegistry(model);

        return getPrioritized(factories);
    }

    private IColoringInterceptorFactory getPrioritized(final List<IColoringInterceptorFactory> factories) {
        Collections.sort(factories);

        if (factories.isEmpty()) {
            return null;
        }
        return factories.get(factories.size() - 1);
    }

    private List<IColoringInterceptorFactory> loadFromRegistry(final IGISModel model) {
        final List<IColoringInterceptorFactory> result = new ArrayList<IColoringInterceptorFactory>();

        for (final IConfigurationElement element : registry.getConfigurationElementsFor(EXTENSION_POINT_NAME)) {
            try {
                final IColoringInterceptorFactory inteceptor = (IColoringInterceptorFactory)element
                        .createExecutableExtension(CLASS_PROPERTY);

                if (inteceptor.accept(model)) {
                    result.add(inteceptor);
                }
            } catch (final CoreException e) {
                // TODO: LN: 8.10.2012, handle exception
            }
        }

        return result;
    }
}
