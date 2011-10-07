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

package org.amanzi.neo.services.model;

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
     * @return property value or <code>null</code>.
     */
    public Object get(String header);
    

    /**
     * Put any object to DataElement
     *
     * @param key Key by object
     * @param value Value by object
     * @return
     */
    public Object put(String key, Object value);
}
