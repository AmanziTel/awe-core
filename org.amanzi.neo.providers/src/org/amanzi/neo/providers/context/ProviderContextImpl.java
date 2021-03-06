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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.nodeproperties.INodeProperties;
import org.amanzi.neo.providers.ContextException;
import org.amanzi.neo.providers.IProviderContext;
import org.amanzi.neo.providers.internal.IModelProvider;
import org.amanzi.neo.services.IService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.neo4j.graphdb.GraphDatabaseService;

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

    private static final String PROVIDERS_EXTENSION_POINT = "org.amanzi.providers";

    private static final String ID_ATTRIBUTE = "id";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String PARAMETERS_TAG = "parameters";

    private static final String SERVICE_REFERENCE = "serviceReference";

    private static final String NODE_PROPERTIES_REFERENCE = "nodePropertiesReference";

    private static final String PROVIDER_REFERENCES = "providerReference";

    private static final String REFERENCE_ATTRIBUTE = "refId";

    private final Map<String, IModelProvider< ? >> providersCache = new HashMap<String, IModelProvider< ? >>();

    private final Map<String, IService> servicesCache = new HashMap<String, IService>();

    private final Map<String, INodeProperties> nodePropertiesCache = new HashMap<String, INodeProperties>();

    private final IExtensionRegistry registry;

    private List<String> serviceStack;

    private List<String> providerStack;

    public ProviderContextImpl() {
        registry = Platform.getExtensionRegistry();
    }

    protected ProviderContextImpl(final IExtensionRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T extends IModelProvider< ? >> T get(final String id) throws ContextException {
        T result = (T)providersCache.get(id);

        if (result == null) {
            result = (T)createModelProvider(id);
            providersCache.put(id, result);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T extends INodeProperties> T getProperties(final String id) throws ContextException {
        try {
            return (T)getNodeProperties(id);
        } catch (CoreException e) {
            return null;
        }
    }

    protected IService getService(final String id) throws ContextException {
        assert !StringUtils.isEmpty(id);

        IService result = servicesCache.get(id);

        if (result == null) {
            result = createService(id);

            servicesCache.put(id, result);
        }

        return result;
    }

    protected INodeProperties getNodeProperties(final String id) throws CoreException, ContextException {
        assert !StringUtils.isEmpty(id);

        INodeProperties result = nodePropertiesCache.get(id);

        if (result == null) {
            result = createNodeProperties(id);
            nodePropertiesCache.put(id, result);
        }

        return result;
    }

    protected IModelProvider< ? > createModelProvider(final String id) throws ContextException {
        assert !StringUtils.isEmpty(id);

        if (providerStack == null) {
            providerStack = new ArrayList<String>();
        }
        if (providerStack.contains(id)) {
            String message = "A cycle was detected <" + providerStack + ">";
            providerStack = null;
            throw new ContextException(message);
        }
        providerStack.add(id);

        IConfigurationElement element = findConfigurationElement(PROVIDERS_EXTENSION_POINT, id);

        if (element == null) {
            throw new ContextException("ModelProvider <" + id + "> was not found in context");
        }

        try {
            return (IModelProvider< ? >)createInstance(element, false);
        } catch (Exception e) {
            throw new ContextException(e);
        } finally {
            if (providerStack != null) {
                providerStack.remove(id);
            }
        }
    }

    protected IService createService(final String id) throws ContextException {
        assert !StringUtils.isEmpty(id);

        if (serviceStack == null) {
            serviceStack = new ArrayList<String>();
        }
        if (serviceStack.contains(id)) {
            String message = "A cycle was detected <" + serviceStack + ">";
            serviceStack = null;
            throw new ContextException(message);
        }
        serviceStack.add(id);

        IConfigurationElement element = findConfigurationElement(SERVICES_EXTENSION_POINT, id);

        if (element == null) {
            throw new ContextException("Service <" + id + "> was not found in context");
        }

        try {
            return (IService)createInstance(element, true);
        } catch (Exception e) {
            throw new ContextException(e);
        } finally {
            if (serviceStack != null) {
                serviceStack.remove(id);
            }
        }
    }

    protected INodeProperties createNodeProperties(final String id) throws CoreException, ContextException {
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

    private IConfigurationElement findConfigurationElement(final String extensionPoint, final String id) {
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

    @SuppressWarnings("unchecked")
    protected <T> T createInstance(final IConfigurationElement configElement, final boolean isService)
            throws ClassNotFoundException, NoSuchMethodException, CoreException, ContextException, InvocationTargetException,
            IllegalAccessException, InstantiationException {
        String className = configElement.getAttribute(CLASS_ATTRIBUTE);
        Class< ? extends T> clazz = (Class< ? extends T>)Class.forName(className);

        IConfigurationElement[] parametersBlock = configElement.getChildren(PARAMETERS_TAG);

        return createInstance(clazz, parametersBlock, isService);
    }

    protected <T> T createInstance(final Class< ? extends T> clazz, final IConfigurationElement[] parameterBlock,
            final boolean isService) throws ContextException, CoreException, InvocationTargetException, IllegalAccessException,
            InstantiationException {
        // should be 0 or 1 element
        assert parameterBlock != null;
        assert parameterBlock.length < 2;

        IConfigurationElement singleParameterBlock = null;

        if (parameterBlock.length == 1) {
            singleParameterBlock = parameterBlock[0];
        }

        return createInstance(clazz, singleParameterBlock, isService);
    }

    protected <T> T createInstance(final Class< ? extends T> clazz, final IConfigurationElement parameterBlock,
            final boolean isService) throws ContextException, CoreException, InvocationTargetException, IllegalAccessException,
            InstantiationException {
        Map<Class< ? extends Object>, Object> parametersMap = new HashMap<Class< ? extends Object>, Object>();

        if (isService) {
            initializeWithDatabaseService(parametersMap);
        }

        if (parameterBlock != null) {
            for (IConfigurationElement parameter : parameterBlock.getChildren()) {
                String name = parameter.getName();
                String reference = parameter.getAttribute(REFERENCE_ATTRIBUTE);

                Object parameterInstance = null;

                if (name.equals(SERVICE_REFERENCE)) {
                    parameterInstance = getService(reference);
                } else if (name.equals(NODE_PROPERTIES_REFERENCE)) {
                    parameterInstance = getNodeProperties(reference);
                } else if (name.equals(PROVIDER_REFERENCES)) {
                    parameterInstance = get(reference);
                } else {
                    throw new ContextException("Unknown parameter <" + name + ">");
                }

                parametersMap.put(parameterInstance.getClass(), parameterInstance);
            }
        }

        return createInstance(clazz, parametersMap);
    }

    @SuppressWarnings("unchecked")
    protected <T> T createInstance(final Class< ? extends T> clazz, final Map<Class< ? extends Object>, Object> parametersMap)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, ContextException {
        Constructor< ? > correctConstructor = null;
        Object[] arguments = null;

        for (Constructor< ? > constructor : clazz.getConstructors()) {
            Class< ? >[] parameterTypes = constructor.getParameterTypes();

            arguments = new Object[parameterTypes.length];

            int i = 0;

            if (parameterTypes.length == parametersMap.size()) {
                boolean isFound = false;
                for (Class< ? > argumentType : parameterTypes) {
                    for (Entry<Class< ? extends Object>, Object> parameterEntry : parametersMap.entrySet()) {
                        Class< ? > parameterClass = parameterEntry.getKey();
                        if (argumentType.isAssignableFrom(parameterClass)) {
                            isFound = true;

                            arguments[i++] = parameterEntry.getValue();

                            break;
                        }
                    }

                    if (!isFound) {
                        break;
                    }
                }

                if (isFound) {
                    correctConstructor = constructor;
                }
            }
        }

        if (correctConstructor == null) {
            throw new ContextException("Unable to create <" + clazz.getSimpleName() + ">. Constructor for Parameters <"
                    + parametersMap.keySet() + "> not found.");
        }

        return (T)correctConstructor.newInstance(arguments);
    }

    private void initializeWithDatabaseService(final Map<Class< ? extends Object>, Object> parametersMap) {
        GraphDatabaseService databaseService = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();

        parametersMap.put(GraphDatabaseService.class, databaseService);
    }
}
