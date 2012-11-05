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

package org.amanzi.awe.correlation.ui.view.table;

import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.neo.dateformat.DateFormatManager;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationTableLabelProvider implements ITableLabelProvider {

    private static final String UNDEFINED = "undefined";

    private static final Image DELETE_IMG = IconManager.getInstance().getImage("delete");

    @Override
    public void addListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        CorrelationTableColumns column = CorrelationTableColumns.findByIndex(columnIndex);
        switch (column) {
        case DELETE:
            return DELETE_IMG;
        default:
            return null;
        }
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        CorrelationTableColumns column = CorrelationTableColumns.findByIndex(columnIndex);
        ICorrelationModel model = (ICorrelationModel)element;
        switch (column) {
        case NETWORK_COLUMN:
            return model.getNetworModel().getName();
        case MEASUREMENT_COLUMN:
            return model.getMeasurementModel().getName();
        case TOTAL_SECTORS_COUNT:
            return model.getTotalSectorsCount().toString();
        case CORRELATED_M_COUNT:
            return model.getCorrelatedMCount().toString();
        case TOTAL_M_COUNT:
            return model.getTotalMCount().toString();
        case PROXIES_COUNT_COLUMN:
            return String.valueOf(model.getProxiesCount());
        case START_TIME_COLUMN:
            if (model.getProxiesCount() <= 0) {
                return UNDEFINED;
            }
            return DateFormatManager.getInstance().parseLongToStringDate(model.getStartTime());
        case END_TIME_COLUMN:
            if (model.getProxiesCount() <= 0) {
                return UNDEFINED;
            }
            return DateFormatManager.getInstance().parseLongToStringDate(model.getEndTime());
        case DELETE:
            return StringUtils.EMPTY;
        default:
            return model.getName();
        }
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return true;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

}
