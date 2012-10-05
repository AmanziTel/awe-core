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

package org.amanzi.awe.views.statistcstree.providers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.views.treeview.provider.impl.CommonTreeViewLabelProvider;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeLabelProvider extends CommonTreeViewLabelProvider {

    private static final String TOTAL_NAME = "total";
    private static final String CELL_NAME_FORMAT = "%s: %s";

    private static final int DECIMAL_SIZE = 2;

    @Override
    protected String getStrignFromOtherElement(Object element) {
        if (element instanceof AggregatedItem) {
            return ((AggregatedItem)element).getName();
        }
        return super.getStrignFromOtherElement(element);
    }

    /**
     * @param element
     * @return
     */
    @Override
    protected String getStringFromDataElement(IDataElement element) {
        if (element.getNodeType().equals(StatisticsNodeType.S_ROW)) {
            IStatisticsRow row = (IStatisticsRow)element;
            if (row.isSummury()) {
                return TOTAL_NAME;
            }
            Period period = Period.findById(row.getStatisticsGroup().getPeriod());
            return PeriodManager.getInstance().getPeriodName(period, new Date(row.getStartDate()), new Date(row.getEndDate()));
        } else if (element.getNodeType().equals(StatisticsNodeType.S_CELL)) {
            IStatisticsCell cell = (IStatisticsCell)element;
            Number value = cell.getValue();
            if (value == null) {
                value = 0.0d;
            }
            BigDecimal bd = new BigDecimal(value.floatValue()).setScale(DECIMAL_SIZE, RoundingMode.HALF_EVEN);
            return String.format(CELL_NAME_FORMAT, cell.getName(), bd);
        }
        return super.getStringFromDataElement(element);
    }
}
