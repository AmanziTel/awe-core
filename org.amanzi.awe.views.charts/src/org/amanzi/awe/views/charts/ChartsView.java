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
import java.util.List;

import org.amanzi.awe.chart.manger.ChartsManager;
import org.amanzi.awe.charts.impl.ChartModelPlugin;
import org.amanzi.awe.charts.model.ChartType;
import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.charts.model.provider.IChartModelProvider;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.charts.widget.ItemsSelectorWidget;
import org.amanzi.awe.views.charts.widget.ItemsSelectorWidget.ItemSelectedListener;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChartsView extends ViewPart implements ItemSelectedListener {

    private static final String GROUPS_LABEL = "Group(s)";

    private static final String CELLS_LABEL = "Column(s)";

    private Composite controlsComposite;

    private ItemsSelectorWidget groupSelectorWidget;

    private ItemsSelectorWidget columnsSelectorWidget;

    private ChartComposite chartComposite;

    private ChartType type = ChartType.TIME_CHART;

    @Override
    public void createPartControl(Composite parent) {

        controlsComposite = new Composite(parent, SWT.BORDER);
        controlsComposite.setLayout(new GridLayout(1, false));

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
        columnsSelectorWidget.initializeWidget();

        chartComposite = new ChartComposite(controlsComposite, SWT.FILL);
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * @param allStatisticsGroups
     * @return
     */
    private List<String> getStatisticsGroups(Iterable<IStatisticsGroup> allStatisticsGroups) {
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
    private Button createRadioButton(Composite parent, String text, boolean selected, final ChartType chartType) {
        Button radioButton = new Button(parent, SWT.RADIO);
        radioButton.setText(text);
        radioButton.setSelection(selected);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        radioButton.setLayoutData(layoutData);
        radioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
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
      
    }
}
