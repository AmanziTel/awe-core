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

package org.amanzi.awe.views.distribution.widgets;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.ui.view.widgets.PropertyComboWidget;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionPropertyWidget extends PropertyComboWidget {

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    public DistributionPropertyWidget(final Composite parent, final IPropertySelectionListener listener, final String label, final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Set<String> getPropertyNames(final INodeType nodeType) {
        //TODO: LN: 04.10.2012, move this to DistributionManager
        Set<String> result = new HashSet<String>();
        for (String propertyName : getModel().getPropertyNames(nodeType)) {
            if (!getModel().getValues(nodeType, propertyName).isEmpty()) {
                result.add(propertyName);
            }
        }

        return result;
    }


}
