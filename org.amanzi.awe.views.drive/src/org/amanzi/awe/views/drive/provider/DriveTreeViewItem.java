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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amanzi.awe.views.treeview.provider.impl.AbstractTreeViewItem;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DriveTreeViewItem extends AbstractTreeViewItem<IDriveModel, Object> {

    private static final PeriodManager PERIOD_MANAGER = PeriodManager.getInstance();

    private Long startDate;

    private Long endDate;

    private Period period;

    private String name = StringUtils.EMPTY;

    /**
     * @param model
     * @param element
     */
    public DriveTreeViewItem(IDriveModel model, Object element) {
        super(model, element);
    }

    public DriveTreeViewItem(IDriveModel model, Long startDate, Long endDate, Period period) {
        super(model, null);
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    @Override
    public Iterable<Object> getChildren() throws ModelException {
        if (getModel().asDataElement().equals(getChild())) {
            Period period = Period.getHighestPeriod(getModel().getMinTimestamp(), getModel().getMaxTimestamp());
            if (period == Period.ALL) {
                period = Period.YEARLY;
            }
            return buildLevelTree(getModel().getMinTimestamp(), getModel().getMaxTimestamp(), period);
        }
        if (getChild() instanceof PeriodItem) {
            PeriodItem item = (PeriodItem)getChild();
            return buildLevelTree(item.getStartDate(), item.getEndDate(), item.getPeriod().getUnderlyingPeriod());
        } else {
            return new ObjectIterable(getModel().getChildren((IDataElement)getChild()));
        }
    }

    @Override
    public boolean hasChildren() throws ModelException {
        if (getChild() instanceof PeriodItem) {
            PeriodItem item = (PeriodItem)getChild();
            return item.hasNext();
        } else {
            return getModel().getChildren((IDataElement)getChild()).iterator().hasNext();
        }
    }

    /**
     * @param period
     * @param underlyingPeriod
     * @throws ModelException
     */
    private Iterable<Object> buildLevelTree(Long start, Long end, Period period) throws ModelException {
        IDriveModel model = getModel();
        List<Object> items = new ArrayList<Object>();
        if (period != null) {

            long currentStartTime = period.getStartTime(start);
            long nextStartTime = PERIOD_MANAGER.getNextStartDate(period, model.getMaxTimestamp(), currentStartTime);

            do {
                PeriodItem item = new PeriodItem(currentStartTime, nextStartTime, period, model);
                if (item.hasNext()) {
                    items.add(item);
                }
                currentStartTime = nextStartTime;
                nextStartTime = PERIOD_MANAGER.getNextStartDate(period, end, currentStartTime);
            } while (currentStartTime < end);
            return items;
        } else {
            return new ObjectIterable(getModel().getElements(start, end));
        }

    }
}
