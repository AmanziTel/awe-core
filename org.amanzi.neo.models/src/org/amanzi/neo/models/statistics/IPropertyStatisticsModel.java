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

package org.amanzi.neo.models.statistics;

import java.util.Map;
import java.util.Set;

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;

/**
 * Interface to PropertyStaticticalModel to work with statistics
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
// TODO: LN: 10.10.2012, add comments
public interface IPropertyStatisticsModel extends IModel {

    void indexElement(INodeType nodeType, Map<String, Object> properties) throws ServiceException;

    Set<String> getPropertyNames();

    Set<String> getPropertyNames(INodeType nodeType);

    int getCount();

    int getCount(INodeType nodeType);

    Set<Object> getValues(INodeType nodeType, String property);

    int getValueCount(INodeType nodeType, String property, Object value);

    Set<INodeType> getNodeTypes();

    Class< ? > getPropertyClass(INodeType nodeType, String property);

}
