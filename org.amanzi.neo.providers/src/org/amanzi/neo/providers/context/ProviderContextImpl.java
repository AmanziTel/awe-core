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

package org.amanzi.neo.providers.context;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.nodeproperties.INodeProperties;
import org.amanzi.neo.providers.IModelProvider;
import org.amanzi.neo.providers.IProviderContext;
import org.amanzi.neo.services.internal.IService;
import org.apache.commons.lang3.StringUtils;
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
public class ProviderContextImpl implements IProviderContext {

    private static final String NODE_PROPERTIES_EXTENSION_POINT = "org.amanzi.nodeproperties";

    private static final String SERVICES_EXTENSION_POINT = "org.amanzi.services";

    private static final String ID_ATTRIBUTE = "id";

    private static final String CLASS_ATTRIBUTE = "class";

    private final Map<String, ? extends IModelProvider< ? , ? >> providersCache = new HashMap<String, IModelProvider< ? , ? >>();

    private final Map<String, IService> servicesCache = new HashMap<String, IService>();

    private final Map<String, INodeProperties> nodePropertiesCache = new HashMap<String, INodeProperties>();

    private final IExtensionRegistry registry;

    public ProviderContextImpl() {
        registry = Platform.getExtensionRegistry();
    }

    protected ProviderContextImpl(IExtensionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public synchronized <T extends IModelProvider< ? , ? >> T get(String id) throws ContextException {
        T result = (T)providersCache.get(id);

        if (result == null) {

        }

        return result;
    }

    protected IService getService(String id) throws CoreException, ContextException {
        assert !StringUtils.isEmpty(id);

        IService result = servicesCache.get(id);

        if (result == null) {
            result = createService(id);

            servicesCache.put(id, result);
        }

        return result;
    }

    protected INodeProperties getNodeProperties(String id) throws CoreException, ContextException {
        assert !StringUtils.isEmpty(id);

        INodeProperties result = nodePropertiesCache.get(id);

        if (result == null) {
            result = createNodeProperties(id);
            nodePropertiesCache.put(id, result);
        }

        return result;
    }

    protected IService createService(String id) throws CoreException, ContextException {
        assert !StringUtils.isEmpty(id);

        IConfigurationElement element = findConfigurationElement(SERVICES_EXTENSION_POINT, id);

        if (element == null) {
            throw new ContextException("Service <" + id + "> was not found in context");
        }

        return null;
    }

    protected INodeProperties createNodeProperties(String id) throws CoreException, ContextException {
        assert !StringUtils.isEmpty(id);

        IConfigurationElement element = findConfigurationElement(NODE_PROPERTIES_EXTENSION_POINT, id);

        if (element != null) {
            try {
                INodeProperties result = (INodeProperties)element.createExecutableExtension(CLASS_ATTRIBUTE);

                return result;
            } catch (ClassCastException e) {
                throw new ContextException(e);
            }
        }

        throw new ContextException("NodeProperties <" + id + "> was not found in context");
    }

    private IConfigurationElement findConfigurationElement(String extensionPoint, String id) {
        IConfigurationElement result = null;

        IConfigurationElement[] nodePropertiesElements = registry.getConfigurationElementsFor(extensionPoint);

        for (IConfigurationElement singleElement : nodePropertiesElements) {
            if (singleElement.getAttribute(ID_ATTRIBUTE).equals(id)) {
                result = singleElement;
                break;
            }
        }

        return result;
    }

}
