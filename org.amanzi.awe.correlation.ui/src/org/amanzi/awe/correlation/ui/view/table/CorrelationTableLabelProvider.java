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
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        CorrelationTableColumns column = CorrelationTableColumns.findByIndex(columnIndex);
        ICorrelationModel model = (ICorrelationModel)element;
        switch (column) {
        case NETWORK_COLUMN:
            return model.getNetworkName();
        case MEASUREMENT_COLUMN:
            return model.getMeasurementName();
        case PROXIES_COUNT_COLUMN:
            return String.valueOf(model.getProxiesCount());
        case START_TIME_COLUMN:
            return model.getStartTime().toString();
        case END_TIME_COLUMN:
            return model.getEndTime().toString();
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
