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

package org.amanzi.awe.drive.ui.wrapper;

import java.util.Iterator;

import org.amanzi.awe.drive.ui.DriveTreePlugin;
import org.amanzi.awe.drive.ui.item.PeriodItem;
import org.amanzi.awe.drive.ui.preferences.DriveLabelsInitializer;
import org.amanzi.awe.ui.dto.IPeriodItem;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.impl.AbstractModelWrapper;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveModelWrapper extends AbstractModelWrapper<IMeasurementModel> {

    private class PeriodItemIterator implements Iterator<ITreeItem> {

        private final Period period;

        private long startTime;

        private final long endTime;

        private PeriodItem next;

        public PeriodItemIterator(final Period period, final long startTime, final long endTime) {
            this.period = period;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public boolean hasNext() {
            if (startTime < endTime && next == null) {
                gotoNext();
            }

            return next != null;
        }

        private void gotoNext() {
            PeriodItem result = null;
            do {
                final long tempEndTime = period.getUnderlyingPeriod().getEndTime(startTime);

                if (haveLocations(startTime, tempEndTime)) {
                    result = new PeriodItem(getModel(), DriveModelWrapper.this, period.getUnderlyingPeriod(), startTime);
                }

                startTime = tempEndTime;
            } while (result == null && startTime < endTime);

            next = result;
        }

        @Override
        public ITreeItem next() {
            if (next == null) {
                gotoNext();
            }

            final ITreeItem result = next;

            next = null;

            return result;
        }

        @Override
        public void remove() {
            // TODO: LN: 15.10.2012, throw exception
        }

    }

    /**
     * @param wrapper
     * @param model
     */
    protected DriveModelWrapper(final IMeasurementModel model) {
        super(model);
    }

    @Override
    protected ITreeItem getParentInternal(final ITreeItem item) throws ModelException {

        return null;
    }

    @Override
    protected Iterator<ITreeItem> getChildrenInternal(final ITreeItem item) throws ModelException {
        if (item instanceof PeriodItem) {
            final IPeriodItem period = (IPeriodItem)item;

            final Iterator<IDataElement> elementsIterator = getModel().getElements(period.getStartTime(), period.getEndTime())
                    .iterator();

            if (!period.getPeriod().equals(Period.HOURLY)) {
                if (elementsIterator.hasNext()) {
                    return new PeriodItemIterator(period.getPeriod(), period.getStartTime(), period.getEndTime());
                }
            } else {
                return new TreeItemIterator(elementsIterator);
            }
        } else {
            final IMeasurementModel model = item.castChild(IMeasurementModel.class);

            if (model != null) {
                final Period period = Period.getHighestPeriod(model.getMinTimestamp(), model.getMaxTimestamp());
                return new PeriodItemIterator(period, model.getMinTimestamp(), model.getMaxTimestamp());
            }
        }
        return null;
    }

    private boolean haveLocations(final long startTime, final long endTime) {
        try {
            final Iterator<IDataElement> elementsIterator = getModel().getElements(startTime, endTime).iterator();

            return elementsIterator.hasNext();
        } catch (final ModelException e) {
            // TODO: LN: 15.10.2012, log error
            return false;
        }
    }

    @Override
    public String getTitle(final ITreeItem item) {
        if (item instanceof PeriodItem) {
            final IPeriodItem period = (IPeriodItem)item;
            return PeriodManager.getPeriodName(period.getPeriod(), period.getStartTime(), period.getEndTime());
        } else {
            return super.getTitle(item);
        }
    }

    @Override
    protected String getPreferenceKey() {
        return DriveLabelsInitializer.DRIVE_LABEL_TEMPLATE;
    }

    @Override
    protected IPreferenceStore getPreferenceStore() {
        return DriveTreePlugin.getDefault().getPreferenceStore();
    }
}
