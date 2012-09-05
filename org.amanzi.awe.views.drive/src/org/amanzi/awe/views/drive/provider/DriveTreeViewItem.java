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

import java.util.Date;

import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.drive.IDriveModel;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DriveTreeViewItem<T extends IDriveModel, E extends Object> extends TreeViewItem<T> {

    private static final PeriodManager PERIOD_MANAGER = PeriodManager.getInstance();

    private Long startDate;

    private Long endDate;

    private Period period;

    private boolean isPeriodContainer = false;

    private String name = StringUtils.EMPTY;

    /**
     * @param model
     * @param element
     */
    public DriveTreeViewItem(T model, IDataElement element) {
        super(model, element);
    }

    public DriveTreeViewItem(T model, Long startDate, Long endDate, Period period) {
        super(model, null);
        isPeriodContainer = true;
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
        name = generateName();
    }

    @Override
    public int hashCode() {
        if (StringUtils.isEmpty(name)) {
            return super.hashCode();
        } else {
            return name.hashCode();
        }

    }

    /**
     * @return
     */
    private String generateName() {
        Date start = new Date(startDate);
        Date end = new Date(endDate);
        return PERIOD_MANAGER.getPeriodName(period, start, end);
    }

    /**
     * @return Returns the endDate.
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * @return Returns the startDate.
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * @return Returns the period.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * @return Returns the isPeriodContainer.
     */
    public boolean isPeriodContainer() {
        return isPeriodContainer;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
