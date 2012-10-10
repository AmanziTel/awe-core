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

package org.amanzi.neo.nodetypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.internal.NeoCorePlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>
 * Node type manager holds types, that implement {@link INodeType}.
 * </p>
 * <p>
 * The class is used to register implementations of <code>INodeType </code> interface and get valid
 * objects the registered <code>INodeType</code> implementations by id, that is stored in database
 * nodes. Note that if enum members in different implementations repeat each other, than there's no
 * guarantee that the result of method {@link NodeTypeManager#getType(String)} will be valid.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */

public final class NodeTypeManager {

    private static final Logger LOGGER = Logger.getLogger(NodeTypeManager.class);

    private static final String NODE_TYPE_EXTENSION_ID = "org.amanzi.nodetypes";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String DYNAMIC_NODE_TYPES_KEY = "dynnamic_types";

    private static final IPreferenceStore PREFERENCE_STORE = NeoCorePlugin.getDefault().getPreferenceStore();

    private static final String COMMA_SEPARATOR = ",";

    @SuppressWarnings("rawtypes")
    private static final class StringToEnumConverter<T extends Enum> {

        private final Class<T> enumType;

        public StringToEnumConverter(final Class<T> enumType) {
            this.enumType = enumType;
        }

        @SuppressWarnings("unchecked")
        public T convert(final String source) {
            return (T)Enum.valueOf(this.enumType, source.trim().toUpperCase(Locale.getDefault()));
        }
    }

    private static final class NodeTypeManagerHandler {
        private static volatile NodeTypeManager instance = new NodeTypeManager();
    }

    private static Set<Class< ? >> registeredNodeTypes = new HashSet<Class< ? >>();

    private static Map<String, INodeType> nodeTypeCache = new HashMap<String, INodeType>();

    private final IExtensionRegistry registry;

    protected NodeTypeManager() {
        registry = Platform.getExtensionRegistry();
        initializeNodeTypesFromExtensions();
        initializeNodeTypesFromPreferenceStore();
    }

    public static NodeTypeManager getInstance() {
        return NodeTypeManagerHandler.instance;
    }

    /**
     * Adds a class, implementing <code>INodeType</code> to the collection of registered classes.
     * 
     * @param nodeType a class that implements INodeType interface
     */
    public void registerNodeType(final Class< ? > nodeType) {
        assert nodeType != null;

        registeredNodeTypes.add(nodeType);
    }

    /**
     * This method registered classes, and tries to get an instance of this class by calling method
     * <i>valueOf(String)</i> on class objects, with the specified <code>typeID</code> in parameter.
     * 
     * @param typeID
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public INodeType getType(final String typeID) throws NodeTypeNotExistsException {
        assert !StringUtils.isEmpty(typeID);

        INodeType result = null;

        result = nodeTypeCache.get(typeID);

        if (result == null) {
            for (Class clazz : registeredNodeTypes) {
                StringToEnumConverter conv = new StringToEnumConverter<Enum>(clazz);
                try {
                    result = (INodeType)conv.convert(typeID);

                    break;
                } catch (IllegalArgumentException e) {
                    // this enum didn't have corresponding node type
                }
            }
            nodeTypeCache.put(typeID, result);
        }

        if (result == null) {
            throw new NodeTypeNotExistsException(typeID);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void initializeNodeTypesFromExtensions() {
        for (IConfigurationElement nodeTypesElement : registry.getConfigurationElementsFor(NODE_TYPE_EXTENSION_ID)) {
            String className = nodeTypesElement.getAttribute(CLASS_ATTRIBUTE);

            try {
                Class< ? extends INodeType> clazz = (Class< ? extends INodeType>)Class.forName(className);

                registerNodeType(clazz);
            } catch (ClassNotFoundException e) {
                LOGGER.info("Cannot initialize NodeTypes <" + className + ">", e);
            }

        }
    }

    /**
    *
    */
    private void initializeNodeTypesFromPreferenceStore() {
        String dynamicTypesString = PREFERENCE_STORE.getDefaultString(DYNAMIC_NODE_TYPES_KEY);
        if (StringUtils.isEmpty(dynamicTypesString)) {
            return;
        }
        String[] dynamicTypesArray = dynamicTypesString.split(COMMA_SEPARATOR);
        for (String type : dynamicTypesArray) {
            if (!type.isEmpty()) {
                nodeTypeCache.put(type, new DynamicNodeType(type));
            }
        }
    }

    public void addDynamicNodeTypes(String... types) {
        String existedTypes = PREFERENCE_STORE.getDefaultString(DYNAMIC_NODE_TYPES_KEY);
        StringBuilder builder = new StringBuilder(existedTypes);
        for (String type : types) {
            if (nodeTypeCache.containsKey(type)) {
                continue;
            }
            if (!builder.toString().isEmpty()) {
                builder.append(COMMA_SEPARATOR);
            }
            builder.append(type);
            nodeTypeCache.put(type, new DynamicNodeType(type));
        }
        PREFERENCE_STORE.setDefault(DYNAMIC_NODE_TYPES_KEY, builder.toString());
    }

    protected Set<Class< ? >> getRegisteredNodeTypes() {
        return registeredNodeTypes;
    }
}
