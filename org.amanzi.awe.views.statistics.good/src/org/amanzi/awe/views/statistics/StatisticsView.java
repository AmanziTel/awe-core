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

package org.amanzi.awe.views.statistics;

import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.awe.ui.view.widget.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widget.DriveComboWidget;
import org.amanzi.awe.ui.view.widget.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widget.PeriodWidget;
import org.amanzi.awe.ui.view.widget.PeriodWidget.ITimePeriodSelectionListener;
import org.amanzi.awe.ui.view.widget.PropertyComboWidget;
import org.amanzi.awe.ui.view.widget.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.views.statistics.internal.StatisticsPlugin;
import org.amanzi.awe.views.statistics.table.StatisticsTable;
import org.amanzi.awe.views.statistics.table.StatisticsTable.IStatisticsTableListener;
import org.amanzi.awe.views.statistics.widget.PeriodComboWidget;
import org.amanzi.awe.views.statistics.widget.PeriodComboWidget.IPeriodSelectionListener;
import org.amanzi.awe.views.statistics.widget.TemplateComboWidget;
import org.amanzi.awe.views.statistics.widget.TemplateComboWidget.ITemplateSelectionListener;
import org.amanzi.neo.models.drive.IDriveModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsView extends ViewPart
        implements
            IDriveSelectionListener,
            IPropertySelectionListener,
            ITemplateSelectionListener,
            IPeriodSelectionListener,
            ITimePeriodSelectionListener,
            SelectionListener,
            IStatisticsTableListener {

    private class StatisticsJob extends Job {

        private final StatisticsManager statisticsManager;

        /**
         * @param name
         */
        public StatisticsJob(StatisticsManager statisticsManager) {
            super("Calculate statistics");

            this.statisticsManager = statisticsManager;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                final IStatisticsModel model = statisticsManager.build(monitor);

                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        updateTable(model);
                    }
                }, true);
            } catch (StatisticsEngineException e) {
                return new Status(Status.ERROR, StatisticsPlugin.PLUGIN_ID, "Error on Statistics Calculation", e);
            }
            return Status.OK_STATUS;
        }
    }

    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);

    private DriveComboWidget driveCombo;

    private TemplateComboWidget templateCombo;

    private PropertyComboWidget propertyComboWidget;

    private PeriodComboWidget periodCombo;

    private StatisticsManager statisticsManager;

    private PeriodWidget timePeriod;

    private Button buildButton;

    private StatisticsTable statisticsTable;

    private boolean isInitialized = false;

    public StatisticsView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.BORDER);
        mainComposite.setLayout(ONE_ROW_GRID_LAYOUT);

        addTemplateComposite(mainComposite);
        addPeriodComposite(mainComposite);

        statisticsTable = addStatisticsTableWidget(parent, this);
        statisticsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        isInitialized = true;
        driveCombo.updateSelection();
    }

    private void addTemplateComposite(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(getCompositeGridData());
        composite.setLayout(new GridLayout(4, false));

        addTemplateCompositeContent(composite);
    }

    private void addTemplateCompositeContent(final Composite parent) {
        driveCombo = AWEWidgetFactory.getFactory().addDriveComboWidget(this, "Dataset:", parent);

        templateCombo = addTemplateComboWidget(parent, this);
        templateCombo.setEnabled(false);

        propertyComboWidget = AWEWidgetFactory.getFactory().addPropertyComboWidget(this, "Aggregation:", parent);
        propertyComboWidget.setEnabled(false);

        buildButton = new Button(parent, SWT.NONE);
        buildButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        buildButton.setText("Build");
        buildButton.addSelectionListener(this);
    }

    private void addPeriodComposite(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(getCompositeGridData());
        composite.setLayout(new GridLayout(2, false));

        addPeriodCompositeContent(composite);
    }

    private GridData getCompositeGridData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false);
    }

    private void addPeriodCompositeContent(final Composite parent) {
        periodCombo = addPeriodComboWidget(parent, this);
        periodCombo.setEnabled(false);

        timePeriod = AWEWidgetFactory.getFactory().addPeriodWidget(this, "Start time:", "End time", parent);
        timePeriod.setLayoutData(getCompositeGridData());
        // timePeriod.setEnabled(false);
    }

    @Override
    public void dispose() {
        driveCombo.dispose();
        templateCombo.dispose();
        propertyComboWidget.dispose();
        periodCombo.dispose();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    // TODO: LN: 09.08.2012, duplicate code
    private TemplateComboWidget addTemplateComboWidget(final Composite parent, final ITemplateSelectionListener listener) {
        TemplateComboWidget result = new TemplateComboWidget(parent, listener, "Template:");
        result.initializeWidget();

        return result;
    }

    // TODO: LN: 09.08.2012, duplicate code
    private PeriodComboWidget addPeriodComboWidget(final Composite parent, final IPeriodSelectionListener listener) {
        PeriodComboWidget result = new PeriodComboWidget(parent, listener, "Period:");
        result.initializeWidget();

        return result;
    }

    private StatisticsTable addStatisticsTableWidget(final Composite parent, final IStatisticsTableListener listener) {
        StatisticsTable result = new StatisticsTable(parent, listener);
        result.initializeWidget();

        return result;
    }

    @Override
    public void onTemplateSelected(final ITemplate template) {
        if (statisticsManager != null) {
            statisticsManager.setTemplate(template);
        }
    }

    @Override
    public void onPropertySelected(final String property) {
        if (statisticsManager != null) {
            statisticsManager.setProperty(property);
        }
    }

    @Override
    public void onDriveModelSelected(final IDriveModel model) {
        if (isInitialized && (model != null)) {
            statisticsManager = StatisticsManager.getManager(model);
            templateCombo.setStatisticsManager(statisticsManager);
            templateCombo.setEnabled(true);

            propertyComboWidget.setModel(model);
            propertyComboWidget.setEnabled(true);

            periodCombo.setModel(model);
            periodCombo.setEnabled(true);

            timePeriod.setPeriod(model.getMinTimestamp(), model.getMaxTimestamp());
            timePeriod.setEnabled(true);
        }
    }

    @Override
    public void onPeriodSelected(final Period period) {
        if (statisticsManager != null) {
            statisticsManager.setPeriod(period);
        }
    }

    @Override
    public void widgetSelected(final SelectionEvent event) {
        if (statisticsManager != null) {
            StatisticsJob job = new StatisticsJob(statisticsManager);

            job.schedule();
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    private void updateTable(IStatisticsModel statisticsModel) {

    }

}
