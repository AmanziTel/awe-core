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

package org.amanzi.neo.services;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class NodeTypeManager {

    private static List<Class< ? >> registeredNodeTypes = new ArrayList<Class< ? >>();

    static {
        registerNodeType(DriveNodeTypes.class);
    }

    public static void registerNodeType(Class< ? extends INodeType> nodeType) {
        // validate
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        registeredNodeTypes.add(nodeType);
    }

    public static INodeType getType(String typeID) {
        INodeType result = null;
        for (Class T : registeredNodeTypes) {
            StringToEnumConverter conv = new StringToEnumConverter(T);
            try {
                result = (INodeType)conv.convert(typeID);

                break;
            } finally {
            }
        }
        return result;
    }

    private static final class StringToEnumConverter<T extends Enum> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T)Enum.valueOf(this.enumType, source.trim().toUpperCase());
        }
    }

}
