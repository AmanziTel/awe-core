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

package org.amanzi.neo.dto;

import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;

/**
 * <p>
 * This interface represents a 'proxy' object between view and database layers.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDataElement {

    /**
     * Gets a property value.
     * 
     * @param header the property name
     * @return Property value or <code>null</code>.
     */
    Object get(String header);

    /**
     * Put any object to DataElement
     * 
     * @param key Key by object
     * @param value Value by object
     * @return Put object
     */
    Object put(String key, Object value);

    /**
     * Get all properties from DataElement
     * 
     * @return Set of properties
     */
    Set<String> keySet();

    long getId();

    INodeType getNodeType();
}
