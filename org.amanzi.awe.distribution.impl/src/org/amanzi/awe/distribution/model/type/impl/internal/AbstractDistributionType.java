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

package org.amanzi.awe.distribution.model.type.impl.internal;

import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDistributionType<T extends IRange> implements IDistributionType<T> {

    private final IPropertyStatisticalModel model;

    private final INodeType nodeType;

    private final String propertyName;

    private final Select select;

    protected AbstractDistributionType(final IPropertyStatisticalModel model, final INodeType nodeType, final String propertyName,
            final Select select) {
        this.model = model;
        this.nodeType = nodeType;
        this.propertyName = propertyName;
        this.select = select;
    }

    @Override
    public INodeType getNodeType() {
        return nodeType;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    protected IPropertyStatisticalModel getModel() {
        return model;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Select getSelect() {
        return select;
    }

}
