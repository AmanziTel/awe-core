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

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Iterator;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.filter.IStatisticsFilter;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.ui.filter.impl.StatisticsFilterHandler;
import org.amanzi.awe.statistics.ui.handlers.DimensionHandler;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.impl.AbstractTreeModelWrapper;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsModelWrapper extends AbstractTreeModelWrapper<IStatisticsModel> {

    private static final String TOTAL_NAME = "total";

    private static final String CELL_NAME_FORMAT = "{0}: {1}";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

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
        IStatisticsModel model = item.castChild(IStatisticsModel.class);

        if (model != null) {
            IStatisticsFilter filter = StatisticsFilterHandler.getInstance().getFilter(model);

            return new TreeItemIterator(model.findAllStatisticsLevels(DimensionHandler.getInstance().getDimension(), filter)
                    .iterator());
        } else {
            model = item.castParent(IStatisticsModel.class);

            IStatisticsFilter filter = StatisticsFilterHandler.getInstance().getFilter(model);

            if (filter != null) {
                return null;
            } else {
                return super.getChildrenInternal(item);
            }
        }
    }

    @Override
    public String getTitle(final ITreeItem item) {
        String result = null;

        final IStatisticsCell cell = item.castChild(IStatisticsCell.class);

        if (cell != null) {
            result = getTitleFromCell(cell);
        } else {
            final IStatisticsRow row = item.castChild(IStatisticsRow.class);

            if (row != null) {
                result = getTitleFromRow(row);
            } else {
                final IDataElement dataElement = item.castChild(IDataElement.class);

                if (dataElement != null) {
                    result = dataElement.getName();
                } else {
                    return super.getTitle(item);
                }
            }
        }

        return result;
    }

    private String getTitleFromRow(final IStatisticsRow row) {
        String result = null;

        if (row.isSummury()) {
            result = TOTAL_NAME;
        } else {
            final Period period = Period.findById(row.getStatisticsGroup().getPeriod());

            return PeriodManager.getPeriodName(period, row.getStartDate(), row.getEndDate());
        }

        return result;
    }

    private String getTitleFromCell(final IStatisticsCell cell) {
        Number value = cell.getValue();
        if (value == null) {
            value = 0.0d;
        }

        return MessageFormat.format(CELL_NAME_FORMAT, cell.getName(), DECIMAL_FORMAT.format(value));
    }

    @Override
    protected String getPreferenceKey() {
        return null;
    }

    @Override
    protected IPreferenceStore getPreferenceStore() {
        return null;
    }
}
