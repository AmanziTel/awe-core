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

package org.amanzi.awe.statistics.ui.table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.filter.IStatisticsFilter;
import org.amanzi.neo.core.period.PeriodManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsLabelProvider implements ITableLabelProvider, ITableColorProvider {

    public enum CellType {
        KPI, SUMMARY, PERIOD, PROPERTY;
    }

    private static final int DECIMAL_SIZE = 2;

    private static final String SUMMURY_ROW_LABEL = "total";

    private IStatisticsRow previousRow = null;

    private final List<IStatisticsCell> cellList = new ArrayList<IStatisticsCell>();

    private IStatisticsRow selectedRow;

    private int selectedColumn;

    private IStatisticsFilter filter;

    @Override
    public void addListener(final ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(final Object element, int columnIndex) {
        if (columnIndex == 0) {
            return StringUtils.EMPTY;
        } else {
            columnIndex--;
        }

        if (element instanceof IStatisticsRow) {
            final IStatisticsRow statisticsRow = (IStatisticsRow)element;

            if (!statisticsRow.equals(previousRow)) {
                initializeCellList(statisticsRow);

                previousRow = statisticsRow;
            }

            switch (columnIndex) {
            case 0:
                return statisticsRow.getStatisticsGroup().getPropertyValue();
            case 1:
                return getStatisticsRowName(statisticsRow);
            default:
                final Number value = cellList.get(columnIndex - 2).getValue();
                if (value != null) {
                    final float floatValue = value.floatValue();
                    final BigDecimal bd = new BigDecimal(floatValue).setScale(DECIMAL_SIZE, RoundingMode.HALF_EVEN);
                    return bd.toString();
                }
            }
        }

        return StringUtils.EMPTY;
    }

    public CellType getCellType(final IStatisticsRow statisticsRow, final int columnIndex) {
        if (statisticsRow.isSummury()) {
            return CellType.SUMMARY;
        } else {
            switch (columnIndex) {
            case 0:
            case 1:
                return CellType.PERIOD;
            default:
                return CellType.KPI;
            }
        }
    }

    private void initializeCellList(final IStatisticsRow statisticsRow) {
        cellList.clear();
        Iterables.addAll(cellList, statisticsRow.getStatisticsCells());
    }

    private String getStatisticsRowName(final IStatisticsRow row) {
        if (row.isSummury()) {
            return SUMMURY_ROW_LABEL;
        }
        final Date startDate = new Date(row.getStartDate());
        final Date endDate = new Date(row.getEndDate());
        return PeriodManager.getPeriodName(filter.getPeriod(), startDate, endDate);
    }

    /**
     * @param filterContainer
     */
    public void setFilter(final IStatisticsFilter filterContainer) {
        this.filter = filterContainer;

    }

    public void setSelectedRow(final IStatisticsRow selectedRow) {
        this.selectedRow = selectedRow;
    }

    public void setSelectedColumn(final int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }

    @Override
    public Color getForeground(final Object element, final int columnIndex) {
        if (element.equals(selectedRow) && (selectedColumn < 2)) {
            return Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
        }

        return null;
    }

    @Override
    public Color getBackground(final Object element, final int columnIndex) {
        if (element.equals(selectedRow) && (selectedColumn < 2)) {
            return Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
        }

        return null;
    }
}
