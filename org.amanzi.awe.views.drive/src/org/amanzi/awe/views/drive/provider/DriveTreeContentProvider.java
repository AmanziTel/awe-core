package org.amanzi.awe.views.drive.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * @author Kondratenko_Vladislav
 */
public class DriveTreeContentProvider extends AbstractContentProvider<IDriveModel, Object> {

    private static final PeriodManager PERIOD_MANAGER = PeriodManager.getInstance();

    private final IDriveModelProvider driveModelProvider;

    private List<DriveTreeViewItem<IDriveModel, Object>> items;

    public DriveTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getDriveModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());
    }

    @SuppressWarnings("rawtypes")
    protected static class DataElementComparator implements Comparator<DriveTreeViewItem> {
        @Override
        public int compare(final DriveTreeViewItem dataElement1, final DriveTreeViewItem dataElement2) {
            return dataElement1.getStartDate().compareTo(dataElement2.getStartDate());
        }
    }

    /**
     * @param projectModelProvider
     */
    protected DriveTreeContentProvider(IDriveModelProvider driveModelProvider, IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.driveModelProvider = driveModelProvider;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return true;
    }

    @Override
    protected void handleInnerElements(ITreeItem<IDriveModel, Object> parentElement) throws ModelException {
        items = new ArrayList<DriveTreeViewItem<IDriveModel, Object>>();
        DriveTreeViewItem<IDriveModel, Object> item = (DriveTreeViewItem<IDriveModel, Object>)parentElement;
        IDriveModel model = item.getParent();

        if (item.isPeriodContainer()) {
            buildLevelTree(item, item.getStartDate(), item.getEndDate(), item.getPeriod().getUnderlyingPeriod());
        } else if (item.getParent().asDataElement().equals(item.getChild())) {
            Period period = Period.getHighestPeriod(model.getMinTimestamp(), model.getMaxTimestamp());
            if (period == Period.ALL) {
                period = Period.YEARLY;
            }
            buildLevelTree(item, model.getMinTimestamp(), model.getMaxTimestamp(), period);
        } else {
            for (IDataElement element : item.getParent().getChildren((IDataElement)item.getChild())) {
                items.add(new DriveTreeViewItem<IDriveModel, Object>(item.getParent(), element));
            }
        }
    }

    @Override
    protected ITreeItem<IDriveModel, Object> createrootItem(IDriveModel model) {
        return createItem(model, model.asDataElement());
    }

    /**
     * @param period
     * @param underlyingPeriod
     * @throws ModelException
     */
    private void buildLevelTree(DriveTreeViewItem<IDriveModel, Object> item, Long start, Long end, Period period)
            throws ModelException {
        IDriveModel model = item.getParent();
        if (period != null) {
            long currentStartTime = period.getStartTime(start);
            long nextStartTime = PERIOD_MANAGER.getNextStartDate(period, model.getMaxTimestamp(), currentStartTime);

            do {
                DriveTreeViewItem<IDriveModel, Object> checkForExistanceItem = new DriveTreeViewItem<IDriveModel, Object>(
                        item.getParent(), currentStartTime, nextStartTime, period);
                if (checkNext(checkForExistanceItem)) {
                    items.add(checkForExistanceItem);
                }
                currentStartTime = nextStartTime;
                nextStartTime = PERIOD_MANAGER.getNextStartDate(period, end, currentStartTime);
            } while (currentStartTime < end);

        } else {
            for (IDataElement element : model.getElements(item.getStartDate(), item.getEndDate())) {
                items.add(new DriveTreeViewItem<IDriveModel, Object>(item.getParent(), element));
            }
        }

    }

    @Override
    protected ITreeItem<IDriveModel, Object> createItem(IDriveModel root, Object element) {
        return new DriveTreeViewItem<IDriveModel, Object>(root, element);
    }

    @Override
    protected Object[] processReturment(IDriveModel model) {
        Collections.sort(items, getDataElementComparer());
        return items.toArray();
    }

    @Override
    protected List<IDriveModel> getRootElements() throws ModelException {
        return driveModelProvider.findAll(getActiveProjectModel());
    }

    @Override
    protected void handleRoot(ITreeItem<IDriveModel, Object> item) throws ModelException {
        handleInnerElements(item);
    }

    @Override
    protected boolean checkNext(ITreeItem<IDriveModel, Object> item) throws ModelException {
        DriveTreeViewItem<IDriveModel, Object> driveItem = (DriveTreeViewItem<IDriveModel, Object>)item;
        IDriveModel model = driveItem.getParent();
        if (driveItem.isPeriodContainer()) {
            return model.getElements(driveItem.getStartDate(), driveItem.getEndDate()).iterator().hasNext();
        } else {
            return driveItem.getParent().getChildren((IDataElement)item.getChild()).iterator().hasNext();
        }

    }
}
