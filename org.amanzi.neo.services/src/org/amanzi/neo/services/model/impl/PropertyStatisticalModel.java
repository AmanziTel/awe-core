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

package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class PropertyStatisticalModel extends DataModel implements IPropertyStatisticalModel {

    protected void indexProperty(INodeType nodeType, String propertyName, Object propertyValue) {
    }

    protected Object parse(INodeType nodeType, String propertyName, String propertyValue) {
        return null;
    }

    @Override
    public INodeToNodeRelationsType getNodeToNodeRelationsType() {
        return null;
    }

    @Override
    public int getNodeCount(INodeType nodeType) {
        return 0;
    }

    @Override
    public int getPropertyCount(INodeType nodeType, String propertyName) {
        return 0;
    }

    @Override
    public String[] getAllProperties() {
        return null;
    }

    @Override
    public String[] getAllProperties(INodeType nodeType) {
        return null;
    }

}
