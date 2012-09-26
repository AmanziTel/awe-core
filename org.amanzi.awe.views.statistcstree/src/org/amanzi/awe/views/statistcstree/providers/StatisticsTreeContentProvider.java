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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.collections.CollectionUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeContentProvider extends AbstractContentProvider<IStatisticsModel, Object> {

    private static final StatisticsTreeComparer STATISTICS_TREE_COMPARER = new StatisticsTreeComparer();

    private final IStatisticsModelProvider statisticsModelProvider;

    private final IDriveModelProvider driveModelProvider;

    private DimensionType type;

    @SuppressWarnings("rawtypes")
    protected static class StatisticsTreeComparer implements Comparator<ITreeItem> {

        @Override
        public int compare(final ITreeItem item1, final ITreeItem item2) {
            int result = 0;
            if (item1.getChild() == null) {
                result = -1;
            } else if (item2.getChild() == null) {
                result = 1;
            } else if ((item1.getChild() instanceof IDataElement) && (item2.getChild() instanceof IDataElement)) {
                IDataElement element1 = (IDataElement)item1.getChild();
                IDataElement element2 = (IDataElement)item2.getChild();
                if (element1.getNodeType().equals(StatisticsNodeType.S_ROW)
                        && element2.getNodeType().equals(StatisticsNodeType.S_ROW)) {
                    IStatisticsRow row1 = ((IStatisticsRow)element1);
                    IStatisticsRow row2 = ((IStatisticsRow)element2);
                    if (row1.isSummury()) {
                        result = -1;
                    } else if (row2.isSummury()) {
                        result = 1;
                    } else {
                        Long timestamp1 = row1.getStartDate();
                        Long timestamp2 = ((IStatisticsRow)element2).getStartDate();
                        result = timestamp1.compareTo(timestamp2);
                    }
                } else {
                    result = element1.compareTo(element2);
                }
            }
            return result;
        }
    }

    /**
     * @param projectModelProvider
     */
    protected StatisticsTreeContentProvider(final IProjectModelProvider projectModelProvider,
            final IStatisticsModelProvider statisticsModelProvider, final IDriveModelProvider driveModelProvider) {
        super(projectModelProvider);
        this.statisticsModelProvider = statisticsModelProvider;
        this.driveModelProvider = driveModelProvider;
    }

    public StatisticsTreeContentProvider(final DimensionType type) {
        this(AWEUIPlugin.getDefault().getProjectModelProvider(), StatisticsModelPlugin.getDefault().getStatisticsModelProvider(),
                AWEUIPlugin.getDefault().getDriveModelProvider());
        this.type = type;
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof ITreeItem) {
            ITreeItem< ? , ? > treeItem = (ITreeItem< ? , ? >)element;

            if ((treeItem.getParent() instanceof IStatisticsModel) && (treeItem.getChild() instanceof IDataElement)) {
                IStatisticsModel model = (IStatisticsModel)treeItem.getParent();
                IDataElement child = (IDataElement)treeItem.getChild();

                try {
                    IDataElement parent = model.getParent(child, type);

                    if (parent != null) {
                        if (parent.equals(model.asDataElement())) {
                            return createRootItem(model);
                        } else {
                            return new TreeViewItem<IStatisticsModel, Object>(model, parent);
                        }
                    }
                } catch (ModelException e) {
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    protected boolean checkNext(final ITreeItem<IStatisticsModel, Object> item) throws ModelException {
        IStatisticsModel model = getRoot(item);

        if (isRoot(item)) {
            return model.findAllStatisticsLevels(type).iterator().hasNext();
        } else {
            Object inner = item.getChild();
            if (inner instanceof IDataElement) {
                return checkInner((IDataElement)inner, model);
            } else if (inner instanceof AggregatedItem) {
                AggregatedItem aggregated = (AggregatedItem)inner;
                return aggregated.hasNext();
            }
        }
        return false;
    }

    @Override
    protected boolean additionalCheckChild(final Object element) throws ModelException {
        return false;
    }

    @Override
    protected void getChildren(final ITreeItem<IStatisticsModel, Object> parentElement) throws ModelException {
        IStatisticsModel model = getRoot(parentElement);
        if (isRoot(parentElement)) {
            setChildren(getStatisticsLevels(model));
        } else {
            Object inner = parentElement.getChild();
            if (inner instanceof IDataElement) {
                setChildren(handleDataElement((IDataElement)inner, model));
            } else if (inner instanceof AggregatedItem) {
                AggregatedItem aggregated = (AggregatedItem)inner;
                if (aggregated.hasNext()) {
                    setChildren(aggregated.getNextSources());
                }
            }
        }

    }

    protected Iterable<Object> getStatisticsLevels(IStatisticsModel model) throws ModelException {
        return new StatisticsElementIterable(model.findAllStatisticsLevels(type));
    }

    private boolean checkInner(final IDataElement child, final IStatisticsModel model) throws ModelException {
        INodeType nodeType = child.getNodeType();
        if (nodeType.equals(StatisticsNodeType.GROUP)) {
            IStatisticsGroup group = (IStatisticsGroup)child;
            return model.getStatisticsRows(group.getPeriod()).iterator().hasNext();
        }
        Iterable<Object> innerObjects = handleDataElement(child, model);
        if (innerObjects == null) {
            return false;
        } else {
            return innerObjects.iterator().hasNext();
        }
    }

    /**
     * @param inner
     * @return
     * @throws ModelException
     */
    private Iterable<Object> handleDataElement(final IDataElement child, final IStatisticsModel model) throws ModelException {
        INodeType nodeType = child.getNodeType();
        if (nodeType.equals(StatisticsNodeType.LEVEL)) {
            return new StatisticsElementIterable(getStatisticsGroups(model, type, child.getName()));
        } else if (nodeType.equals(StatisticsNodeType.GROUP)) {
            IStatisticsGroup group = (IStatisticsGroup)child;
            Iterable<IStatisticsRow> rows = model.getStatisticsRows(group.getPeriod());
            return getRowsForGroup(rows, child);
        } else if (nodeType.equals(StatisticsNodeType.S_ROW)) {
            return getRowsChildren((IStatisticsRow)child);
        } else if (nodeType.equals(StatisticsNodeType.S_CELL)) {
            IStatisticsCell cell = (IStatisticsCell)child;
            return getCellsChildren(cell, model);

        }
        return null;
    }

    protected Iterable< ? extends IDataElement> getStatisticsGroups(IStatisticsModel model, DimensionType type, String levelName)
            throws ModelException {
        return model.getAllStatisticsGroups(type, levelName);
    }

    /**
     * @param cell
     * @param model
     * @return
     * @throws ModelException
     */
    private Iterable<Object> getCellsChildren(final IStatisticsCell cell, final IStatisticsModel model) throws ModelException {
        Iterable<IStatisticsCell> cells = model.getSourceCells(cell);
        if (cells == null) {
            AggregatedItem item = new AggregatedItem(model.getSources(cell).iterator());
            return item.getNextSources();
        } else {
            return new StatisticsElementIterable(cells);
        }
    }

    /**
     * @param row
     * @param subRows
     * @return
     * @throws ModelException
     */
    protected Iterable<Object> getRowsChildren(final IStatisticsRow row) throws ModelException {
        return new StatisticsElementIterable(row.getStatisticsCells());
    }

    /**
     * @param rows
     * @param groupd
     * @return
     */
    protected Iterable<Object> getRowsForGroup(final Iterable<IStatisticsRow> rows, final IDataElement group) {
        Set<Object> groups = new HashSet<Object>();
        for (IStatisticsRow row : rows) {
            if (row.getStatisticsGroup().equals(group)) {
                groups.add(row);
            }
        }
        return groups;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Comparator<ITreeItem> getDataElementComparer() {
        return STATISTICS_TREE_COMPARER;
    }

    @Override
    protected List<IStatisticsModel> getRootElements() throws ModelException {
        List<IStatisticsModel> statisticsModels = new ArrayList<IStatisticsModel>();
        for (IMeasurementModel model : this.driveModelProvider.findAll(getActiveProjectModel())) {
            CollectionUtils.addAll(statisticsModels, statisticsModelProvider.findAll(model).iterator());
        }
        return statisticsModels;
    }

    protected DimensionType getType() {
        return type;
    }
}
