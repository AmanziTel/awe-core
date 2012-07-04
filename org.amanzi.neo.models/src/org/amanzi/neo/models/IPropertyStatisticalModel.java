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

package org.amanzi.neo.models;

import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;

/**
 * Interface to PropertyStaticticalModel to work with statistics
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IPropertyStatisticalModel extends IDataModel {

    void indexProperty(INodeType nodeType, String property, Object value);

    Set<String> getPropertyNames();

    Set<String> getPropertyNames(INodeType nodeType);

    int getCount();

    int getCount(INodeType nodeType);

    Set<Object> getValues(INodeType nodeType, String property);

    int getPropertyCount(INodeType nodeType, String property, Object value);
}
