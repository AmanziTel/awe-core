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
@SuppressWarnings({"unchecked", "rawtypes"})
public final class NodeTypeManager {

    private static Set<Class< ? >> registeredNodeTypes = new HashSet<Class< ? >>();

    private static Map<String, INodeType> nodeTypeCache = new HashMap<String, INodeType>();

    private NodeTypeManager() {

    }

    /**
     * Adds a class, implementing <code>INodeType</code> to the collection of registered classes.
     * 
     * @param nodeType a class that implements INodeType interface
     */
    public static void registerNodeType(Class< ? extends INodeType> nodeType) {
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
    public static INodeType getType(String typeID) {
        INodeType result = null;

        result = nodeTypeCache.get(typeID);

        if (result == null) {
            for (Class clazz : registeredNodeTypes) {
                StringToEnumConverter conv = new StringToEnumConverter(clazz);
                try {
                    result = (INodeType)conv.convert(typeID);

                    break;
                } catch (IllegalArgumentException e) {
                    // this enum didn't have corresponding node type
                }
            }
            nodeTypeCache.put(typeID, result);
        }
        return result;
    }

    private static final class StringToEnumConverter<T extends Enum> {

        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T)Enum.valueOf(this.enumType, source.trim().toUpperCase(Locale.getDefault()));
        }
    }

}
