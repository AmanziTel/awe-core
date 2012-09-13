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
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeContentProvider extends AbstractContentProvider<IStatisticsModel, IDataElement> {

    private IStatisticsModelProvider statisticsModelProvider;
    private IDriveModelProvider driveModelProvider;
    private DimensionType type;

    /**
     * @param projectModelProvider
     */
    protected StatisticsTreeContentProvider(IProjectModelProvider projectModelProvider,
            IStatisticsModelProvider statisticsModelProvider, IDriveModelProvider driveModelProvider) {
        super(projectModelProvider);
        this.statisticsModelProvider = statisticsModelProvider;
        this.driveModelProvider = driveModelProvider;
    }

    public StatisticsTreeContentProvider(DimensionType type) {
        this(AWEUIPlugin.getDefault().getProjectModelProvider(), StatisticsModelPlugin.getDefault().getStatisticsModelProvider(),
                AWEUIPlugin.getDefault().getDriveModelProvider());
        this.type = type;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    protected boolean checkNext(ITreeItem<IStatisticsModel, IDataElement> item) throws ModelException {
        IStatisticsModel model = getRoot(item);

        if (isRoot(item)) {
            return model.findAllStatisticsLevels(type).iterator().hasNext();
        } else {
            IDataElement child = item.getChild();
            if (item.getChild().getNodeType().equals(StatisticsNodeType.LEVEL)) {
                return model.getAllStatisticsGroups(type, item.getChild().getName()).iterator().hasNext();
            } else if (item.getChild().getNodeType().equals(StatisticsNodeType.GROUP)) {
                IStatisticsGroup group = (IStatisticsGroup)item.getChild();
                Iterable<IStatisticsRow> rows = model.getStatisticsRows(group.getPeriod());
                return getRowsForGroup(rows, item.getChild()).iterator().hasNext();
            } else if (item.getChild().getNodeType().equals(StatisticsNodeType.S_ROW)) {
                Iterable<IStatisticsCell> cells = ((IStatisticsRow)item.getChild()).getStatisticsCells();
                return cells.iterator().hasNext();
            } else if (child.getNodeType().equals(StatisticsNodeType.S_CELL)) {
                IStatisticsCell cell = (IStatisticsCell)child;
                Iterable<IDataElement> cells = model.getSources(cell);
                return cells.iterator().hasNext();
            }
        }
        return false;
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void getChildren(ITreeItem<IStatisticsModel, IDataElement> parentElement) throws ModelException {
        IStatisticsModel model = getRoot(parentElement);
        if (isRoot(parentElement)) {
            setChildren(model.findAllStatisticsLevels(type));
        } else {
            IDataElement children = parentElement.getChild();
            if (children.getNodeType().equals(StatisticsNodeType.LEVEL)) {
                setChildren(IteratorUtils.toList(model.getAllStatisticsGroups(type, children.getName()).iterator()));
            } else if (children.getNodeType().equals(StatisticsNodeType.GROUP)) {
                IStatisticsGroup group = (IStatisticsGroup)children;
                Iterable<IStatisticsRow> rows = model.getStatisticsRows(group.getPeriod());
                setChildren(getRowsForGroup(rows, children));
            } else if (children.getNodeType().equals(StatisticsNodeType.S_ROW)) {
                IStatisticsRow row = (IStatisticsRow)children;
                Iterable<IStatisticsRow> subRows = model.getSourceRows(row);
                if (subRows == null) {
                    Iterable<IStatisticsCell> cells = ((IStatisticsRow)children).getStatisticsCells();
                    setChildren(IteratorUtils.toList(cells.iterator()));
                } else {
                    setChildren(IteratorUtils.toList(subRows.iterator()));
                }
            } else if (children.getNodeType().equals(StatisticsNodeType.S_CELL)) {
                IStatisticsCell cell = (IStatisticsCell)children;
                Iterable<IStatisticsCell> cells = model.getSourceCells(cell);
                if (cells == null) {
                    setChildren(model.getSources(cell));
                } else {
                    setChildren(IteratorUtils.toList(cells.iterator()));
                }
            }
        }

    }

    /**
     * @param rows
     * @param groupd
     * @return
     */
    private Iterable<IDataElement> getRowsForGroup(Iterable<IStatisticsRow> rows, IDataElement group) {
        Set<IDataElement> groups = new HashSet<IDataElement>();
        for (IStatisticsRow row : rows) {
            if (row.getStatisticsGroup().equals(group)) {
                groups.add((IDataElement)row);
            }
        }
        return groups;
    }

    @Override
    protected List<IStatisticsModel> getRootElements() throws ModelException {
        List<IStatisticsModel> statisticsModels = new ArrayList<IStatisticsModel>();
        for (IMeasurementModel model : this.driveModelProvider.findAll(getActiveProjectModel())) {
            CollectionUtils.addAll(statisticsModels, statisticsModelProvider.findAll(model).iterator());
        }
        return statisticsModels;
    }

}
