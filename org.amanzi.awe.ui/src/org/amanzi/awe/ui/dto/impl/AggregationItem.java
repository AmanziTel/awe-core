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

package org.amanzi.awe.ui.dto.impl;

import org.amanzi.awe.ui.dto.IAggregationItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AggregationItem extends UIItem implements IAggregationItem {

    public interface ICollectFunction {

        Iterable<IDataElement> collectSourceElements(IDataElement parent);

    }

    private final ICollectFunction function;

    /**
     * @param parent
     * @param child
     */
    public AggregationItem(final IModel parent, final Object child, final ICollectFunction function) {
        super(parent, child);

        this.function = function;
    }

    @Override
    public Iterable<IDataElement> getSources() {
        return function.collectSourceElements(castChild(IDataElement.class));
    }

}
