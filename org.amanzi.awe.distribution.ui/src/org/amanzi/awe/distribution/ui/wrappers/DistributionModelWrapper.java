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

package org.amanzi.awe.distribution.ui.wrappers;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.impl.AbstractTreeModelWrapper;
import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionModelWrapper extends AbstractTreeModelWrapper<IDistributionModel> {

    /**
     * @param model
     */
    public DistributionModelWrapper(final IDistributionModel model) {
        super(model);
    }

    @Override
    protected Class<IDistributionModel> getModelClass() {
        return IDistributionModel.class;
    }

    @Override
    public String getTitle(final ITreeItem item) {
        final IDistributionModel model = item.castParent(IDistributionModel.class);
        final IDataElement element = item.castChild(IDataElement.class);

        String result = null;

        if (model != null && element != null) {
            if (element.getNodeType().equals(model.getDistributionNodeType())) {
                return element.get(model.getPropertyName()).toString();
            }
        }

        if (result == null) {
            result = super.getTitle(item);
        }

        return result;
    }

}
