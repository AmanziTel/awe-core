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

package org.amanzi.neo.services.impl.statistics;

import java.util.Map;
import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IPropertyStatistics {

    void indexElement(INodeType nodeType, Map<String, Object> properties) throws ServiceException;

    Set<String> getPropertyNames();

    Set<String> getPropertyNames(INodeType nodeType);

    int getCount();

    int getCount(INodeType nodeType);

    Set<Object> getValues(INodeType nodeType, String property);

    int getValueCount(INodeType nodeType, String property, Object value);

}
