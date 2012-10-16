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

package org.amanzi.awe.views.charts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.charts.builder.dataset.dto.ICategoryRow;
import org.amanzi.awe.charts.builder.dataset.dto.IColumn;
import org.amanzi.awe.charts.builder.dataset.dto.ITimeRow;
import org.amanzi.awe.charts.impl.ChartModelPlugin;
import org.amanzi.awe.charts.manger.ChartsManager;
import org.amanzi.awe.charts.model.ChartType;
import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.charts.model.provider.IChartModelProvider;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.ui.filter.IStatisticsFilter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.charts.filters.ShowInStatisticsTreeFilter;
import org.amanzi.awe.views.charts.widget.ItemsSelectorWidget;
import org.amanzi.awe.views.charts.widget.ItemsSelectorWidget.ItemSelectedListener;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChartsView extends ViewPart implements ItemSelectedListener, ChartMouseListener {

    private static final Logger LOGGER = Logger.getLogger(ChartsView.class);

    public static final String VIEW_ID = "org.amanzi.awe.views.ChartsView";

    private static final String GROUPS_LABEL = "Group(s)";

    private static final String CELLS_LABEL = "Column(s)";

    private static final String CHART_NAME_FORMAT = "%s, %s";

    private Composite controlsComposite;

    private ItemsSelectorWidget groupSelectorWidget;

    private ItemsSelectorWidget columnsSelectorWidget;

    private ChartComposite chartComposite;

    private ChartType type = ChartType.TIME_CHART;

    private final Map<ChartsCahceId, JFreeChart> chartsCache = new HashMap<ChartsCahceId, JFreeChart>();

    private IStatisticsModel model;

    private IStatisticsFilter container;

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        controlsComposite = new Composite(parent, SWT.NONE);
        controlsComposite.setLayout(new GridLayout(1, false));
        controlsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite buttonsContainer = new Composite(controlsComposite, SWT.NONE);
        buttonsContainer.setLayout(new GridLayout(4, true));
        buttonsContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createRadioButton(buttonsContainer, "Line", true, ChartType.TIME_CHART);
        createRadioButton(buttonsContainer, "Bar", false, ChartType.BAR_CHART);
        createRadioButton(buttonsContainer, "Stacked", false, ChartType.STACKED_CHART);
        createRadioButton(buttonsContainer, "Pie", false, ChartType.PIE_CHART);

        Composite filteringContainer = new Composite(controlsComposite, SWT.BORDER);
        filteringContainer.setLayout(new GridLayout(2, true));
        filteringContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        groupSelectorWidget = new ItemsSelectorWidget(filteringContainer, this, GROUPS_LABEL);
        columnsSelectorWidget = new ItemsSelectorWidget(filteringContainer, this, CELLS_LABEL);

        groupSelectorWidget.initializeWidget();
        int width = parent.getShell().getSize().x;

        chartComposite = new ChartComposite(parent, SWT.FILL, null, ChartComposite.DEFAULT_WIDTH, ChartComposite.DEFAULT_HEIGHT,
                ChartComposite.DEFAULT_MINIMUM_DRAW_WIDTH, ChartComposite.DEFAULT_MINIMUM_DRAW_HEIGHT, width,
                ChartComposite.DEFAULT_MAXIMUM_DRAW_HEIGHT, true, true, true, true, true, true);

        columnsSelectorWidget.initializeWidget();
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        chartComposite.setVisible(false);
        chartComposite.addChartMouseListener(this);
    }

    /**
     * @param allStatisticsGroups
     * @return
     */
    private List<String> getStatisticsGroups(final Iterable<IStatisticsGroup> allStatisticsGroups) {
        List<String> groups = new ArrayList<String>();
        for (IStatisticsGroup group : allStatisticsGroups) {
            groups.add(group.getPropertyValue());
        }
        return groups;
    }

    /**
     * Creates radio button with the text specified and assigns the layout data
     * 
     * @param parent parent composite
     * @param chartType TODO
     */
    private Button createRadioButton(final Composite parent, final String text, final boolean selected, final ChartType chartType) {
        Button radioButton = new Button(parent, SWT.RADIO);
        radioButton.setText(text);
        radioButton.setSelection(selected);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        radioButton.setLayoutData(layoutData);
        radioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                type = chartType;
                onItemSelected();
            }
        });
        return radioButton;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemSelected() {
        if (container == null) {
            return;
        }
        IChartModel chartModel = createChartModel(model, container);
        updateChart(chartModel);
    }

    /**
     * @param model
     * @param container
     */
    public void fireStatisticsChanged(final IStatisticsModel model, final IStatisticsFilter container) {
        this.model = model;
        this.container = container;
        List<String> groups;
        try {
            groups = getStatisticsGroups(model.getAllStatisticsGroups(DimensionType.TIME, container.getPeriod().getId()));
            groupSelectorWidget.setItems(groups);
            columnsSelectorWidget.setItems(model.getColumns());
            IChartModel chartModel = createChartModel(model, container);
            updateChart(chartModel);
        } catch (ModelException e) {
            LOGGER.error("can't init necessary field with statistics model " + model + " and period" + container);
        }

    }

    /**
     * firstly trying to find chart in cache. if not exists - create new one
     * 
     * @param chartModel
     */
    private void updateChart(final IChartModel chartModel) {
        LOGGER.info("Chart updating begin, for model " + chartModel);

        Long startTime = System.currentTimeMillis();
        ChartsCahceId ID = new ChartsCahceId(chartModel, container);

        JFreeChart chart = chartsCache.get(ID);
        if (chart == null) {
            chart = ChartsManager.getInstance().buildChart(chartModel);
            chartsCache.put(ID, chart);
        }
        LOGGER.info("Chart updated in : " + (System.currentTimeMillis() - startTime) + " ms");

        chartComposite.setChart(chart);
        chartComposite.forceRedraw();
        chartComposite.setVisible(true);
    }

    /**
     * @param model
     * @param container
     */
    private IChartModel createChartModel(final IStatisticsModel model, final IStatisticsFilter container) {
        IChartModelProvider chartProvider = ChartModelPlugin.getDefault().getChartModelProvider();
        IChartDataFilter filter = chartProvider.getChartDataFilter(container.getStartTime(), container.getEndTime(),
                groupSelectorWidget.getSelected());
        IRangeAxis axis = chartProvider.getRangeAxisContainer("value", columnsSelectorWidget.getSelected());
        String chartName = String.format(CHART_NAME_FORMAT, model.getName(), container.getPeriod().getId());
        return chartProvider.getChartModel(chartName, "cells", type, model, container.getPeriod(), filter, axis);

    }

    /**
     * <p>
     * ID of charts in cache
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static class ChartsCahceId {
        private final IChartModel model;
        private final IStatisticsFilter container;

        /**
         * @param model
         * @param container
         */
        public ChartsCahceId(final IChartModel model, final IStatisticsFilter container) {
            super();
            this.model = model;
            this.container = container;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((container == null) ? 0 : container.hashCode());
            result = (prime * result) + ((model == null) ? 0 : model.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ChartsCahceId other = (ChartsCahceId)obj;
            if (container == null) {
                if (other.container != null) {
                    return false;
                }
            } else if (!container.equals(other.container)) {
                return false;
            }
            if (model == null) {
                if (other.model != null) {
                    return false;
                }
            } else if (!model.equals(other.model)) {
                return false;
            }
            return true;
        }

    }

    @Override
    public void dispose() {
        chartsCache.clear();
        super.dispose();
    }

    @Override
    public void chartMouseClicked(final ChartMouseEvent event) {
        if (event == null) {
            return;
        }
        Collection<String> groups = null;
        long startDate = Long.MIN_VALUE;
        long endDate = Long.MAX_VALUE;
        String cellName = StringUtils.EMPTY;

        if (event.getEntity() instanceof CategoryItemEntity) {
            CategoryItemEntity entity = (CategoryItemEntity)event.getEntity();
            IColumn period = (IColumn)entity.getColumnKey();
            ICategoryRow column = period.getItemByName((String)entity.getRowKey());

            groups = column.getGroupsNames();
            startDate = period.getStartDate();
            endDate = period.getEndDate();
            cellName = column.getName();
        } else if (event.getEntity() instanceof XYItemEntity) {
            XYItemEntity entity = (XYItemEntity)event.getEntity();
            TimeSeriesCollection dataset = (TimeSeriesCollection)entity.getDataset();
            TimeSeries ts = dataset.getSeries(entity.getSeriesIndex());
            ITimeRow row = (ITimeRow)ts.getKey();
            RegularTimePeriod period = ts.getTimePeriod(entity.getItem());

            startDate = period.getStart().getTime();
            endDate = container.getPeriod().addPeriod(startDate);
            groups = row.getGroupsForTime(startDate);
            cellName = row.getName();
        } else {
            return;
        }

        AWEEventManager.getManager().fireShowInViewEvent(model,
                new ShowInStatisticsTreeFilter(groups, startDate, endDate, cellName, container.getPeriod()), this);
    }

    @Override
    public void chartMouseMoved(final ChartMouseEvent arg0) {
        // TODO Auto-generated method stub

    }
}
