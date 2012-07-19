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

package org.amanzi.awe.statistics.ui.view.table;

import java.text.DecimalFormat;

import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * label provider
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsLabelProvider implements ITableLabelProvider, ITableColorProvider {
    private Color color;
    private Color defaultColor;
    private Color backgroundColor;
    private Color selectedColor;
    private boolean showAdditionalColumn;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    /**
     * @param device
     */
    public StatisticsLabelProvider(Device device, boolean showAdditionalColumn) {
        this.color = new Color(device, java.awt.Color.red.getRed(), java.awt.Color.red.getGreen(), java.awt.Color.red.getBlue());
        this.defaultColor = new Color(device, java.awt.Color.black.getRed(), java.awt.Color.black.getGreen(),
                java.awt.Color.black.getBlue());
        this.backgroundColor = new Color(device, java.awt.Color.white.getRed(), java.awt.Color.white.getGreen(),
                java.awt.Color.white.getBlue());
        this.selectedColor = new Color(device, 128, 255, 255);
        this.showAdditionalColumn = showAdditionalColumn;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof StatisticsCell[]) {
            StatisticsCell[] cells = (StatisticsCell[])element;
            StatisticsRow row = cells[0].getParent();
            StatisticsGroup group = row.getParent();
            switch (columnIndex) {
            case 0:
                if (showAdditionalColumn) {
                    // TODO KV:imlement this case
                } else {
                    return group.getName();
                }
                break;
            case 1:
                if (showAdditionalColumn) {
                    return group.getName();
                } else {
                    return row.getName();
                }
            case 2:
                if (showAdditionalColumn) {
                    return row.getName();
                } else {
                    return getFormattedValue(cells[columnIndex - 2]);
                }
            default:
                return getFormattedValue(cells[columnIndex - (showAdditionalColumn ? 3 : 2)]);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @return
     */
    private String getFormattedValue(StatisticsCell cell) {
        Number value = cell.getValue();
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return DECIMAL_FORMAT.format(value.doubleValue());
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
        color.dispose();
        defaultColor.dispose();
        backgroundColor.dispose();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public Color getBackground(Object element, int columnIndex) {
        if (element instanceof StatisticsCell[]) {
            StatisticsCell[] cells = (StatisticsCell[])element;
            if (columnIndex > (showAdditionalColumn ? 2 : 1) && cells[columnIndex - (showAdditionalColumn ? 3 : 2)].isSelected()) {
                return selectedColor;
            }
        }
        return backgroundColor;
    }

    @Override
    public Color getForeground(Object element, int columnIndex) {
        if (element instanceof StatisticsCell[]) {
            StatisticsCell[] cells = (StatisticsCell[])element;
            if (columnIndex > (showAdditionalColumn ? 2 : 1) && cells[columnIndex - (showAdditionalColumn ? 3 : 2)].isFlagged()) {
                return color;
            }
        }
        return defaultColor;
    }

    /**
     * @return Returns the showAdditionalColumn.
     */
    public boolean isShowAdditionalColumn() {
        return showAdditionalColumn;
    }

    /**
     * @param showAdditionalColumn The showAdditionalColumn to set.
     */
    public void setShowAdditionalColumn(boolean showAdditionalColumn) {
        this.showAdditionalColumn = showAdditionalColumn;
    }
}
