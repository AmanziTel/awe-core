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

package org.amanzi.awe.statistics.ui.wrapper;

import java.util.Iterator;

import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.impl.AbstractTreeModelWrapper;
import org.amanzi.neo.models.exceptions.ModelException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsModelWrapper extends AbstractTreeModelWrapper<IStatisticsModel> {

    /**
     * @param model
     */
    public StatisticsModelWrapper(final IStatisticsModel model) {
        super(model);
    }

    @Override
    protected Class<IStatisticsModel> getModelClass() {
        return IStatisticsModel.class;
    }

    @Override
    protected Iterator<ITreeItem> getChildrenInternal(final ITreeItem item) throws ModelException {
        final IStatisticsModel model = item.castChild(IStatisticsModel.class);

        if (model != null) {
            return new TreeItemIterator(model.findAllStatisticsLevels(DimensionType.TIME).iterator());
        } else {
            return super.getChildrenInternal(item);
        }
    }
}
