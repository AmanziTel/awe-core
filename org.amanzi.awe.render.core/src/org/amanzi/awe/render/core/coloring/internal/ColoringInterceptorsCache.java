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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.render.core.coloring.IColoringInterceptor;
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

    private final Map<IGISModel, List<IColoringInterceptor>> coloringInterceptors = new HashMap<IGISModel, List<IColoringInterceptor>>();

    private final IExtensionRegistry registry;

    private ColoringInterceptorsCache() {
        this.registry = Platform.getExtensionRegistry();
    }

    public static synchronized ColoringInterceptorsCache getCache() {
        return CacheHandler.instance;
    }

    public synchronized IColoringInterceptor getInterceptor(final IGISModel model) {
        List<IColoringInterceptor> interceptors = coloringInterceptors.get(model);

        if (interceptors == null) {
            interceptors = loadFromRegistry(model);

            coloringInterceptors.put(model, interceptors);
        }

        return getPrioritized(interceptors);
    }

    private IColoringInterceptor getPrioritized(final List<IColoringInterceptor> interceptors) {
        Collections.sort(interceptors);

        return interceptors.get(interceptors.size() - 1);
    }

    private List<IColoringInterceptor> loadFromRegistry(final IGISModel model) {
        final List<IColoringInterceptor> result = new ArrayList<IColoringInterceptor>();

        for (final IConfigurationElement element : registry.getConfigurationElementsFor(EXTENSION_POINT_NAME)) {
            try {
                final IColoringInterceptor inteceptor = (IColoringInterceptor)element.createExecutableExtension(CLASS_PROPERTY);

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
