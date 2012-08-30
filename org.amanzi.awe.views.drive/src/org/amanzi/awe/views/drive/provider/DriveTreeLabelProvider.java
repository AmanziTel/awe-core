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

package org.amanzi.awe.views.drive.provider;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.drive.provider.namesmanager.MeasurementNamesManager;
import org.amanzi.awe.views.treeview.provider.impl.CommonTreeViewLabelProvider;
import org.amanzi.neo.dateformat.DateFormatManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DriveTreeLabelProvider extends CommonTreeViewLabelProvider {
    private final MeasurementNamesManager manager = MeasurementNamesManager.getInstance();
    private final ITimePeriodNodeProperties nodeProperties;

    public DriveTreeLabelProvider() {
        this(AWEUIPlugin.getDefault().getTimePeriodNodeProperties());
    }

    /**
     * @param timePeriodNodeProperties
     */
    public DriveTreeLabelProvider(ITimePeriodNodeProperties timePeriodNodeProperties) {
        super();
        nodeProperties = timePeriodNodeProperties;
    }

    protected String getStringFromDataElement(IDataElement element) {
        for (String name : manager.getGeneralNames()) {
            if (element.contains(name)) {
                if (name.equals(nodeProperties.getTimestampProperty())) {
                    return DateFormatManager.getInstance().parseLongToStringDate((Long)element.get(name));
                }
                return (String)element.get(name);
            }
        }
        return super.getStringFromDataElement(element);
    }
}
