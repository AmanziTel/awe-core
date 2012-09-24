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

import java.util.Date;

import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.DateTimeWidget;
import org.amanzi.awe.ui.view.widgets.DriveComboWidget;
import org.amanzi.awe.ui.view.widgets.PropertyComboWidget;
import org.amanzi.awe.ui.view.widgets.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widgets.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widgets.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.views.statistics.filter.container.dto.IStatisticsFilterContainer;
import org.amanzi.awe.views.statistics.filter.container.dto.impl.StatisticsFilterContainer;
import org.amanzi.awe.views.statistics.internal.StatisticsPlugin;
import org.amanzi.awe.views.statistics.table.StatisticsTable;
import org.amanzi.awe.views.statistics.table.StatisticsTable.IStatisticsTableListener;
import org.amanzi.awe.views.statistics.widgets.PeriodComboWidget;
import org.amanzi.awe.views.statistics.widgets.TemplateComboWidget;
import org.amanzi.awe.views.statistics.widgets.PeriodComboWidget.IPeriodSelectionListener;
import org.amanzi.awe.views.statistics.widgets.TemplateComboWidget.ITemplateSelectionListener;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.drive.IDriveModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
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
            ITimeChangedListener,
            SelectionListener,
            IStatisticsTableListener {

    private class StatisticsJob extends Job {

        private final StatisticsManager statisticsManager;

        /**
         * @param isNeedToCreateBuild
         * @param name
         */
        public StatisticsJob(final StatisticsManager statisticsManager) {
            super("Calculate statistics");
            this.statisticsManager = statisticsManager;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                final IStatisticsModel model = statisticsManager.build(monitor);

                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        updateTable(model, new StatisticsFilterContainer(periodCombo.getPeriod(), startTime.getDate().getTime(),
                                endTime.getDate().getTime()));
                        setEnabledItems(true);
                    }
                }, true);

            } catch (StatisticsEngineException e) {
                return new Status(Status.ERROR, StatisticsPlugin.PLUGIN_ID, "Error on Statistics Calculation", e);
            }
            return Status.OK_STATUS;
        }
    }

    public interface IStatisticsChartsUpdate extends IAWEEventListenter {

    }

    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);

    private static final int FIRST_ROW_LABEL_WIDTH = 65;

    private static final int SECOND_ROW_LABEL_WIDTH = 75;

    private static final int THIRD_ROW_LABEL_WIDTH = 85;

    private static final float ADDITIONAL_WIDTH_PERCENTAGE = 0.15f;

    private static Integer MIN_WIDTH = null;

    private static final int MIN_HEIGHT = 300;

    // TODO: LN: 21.08.2012, refactor: move all Layouts and LayoutData's to constants or some
    // Factory

    private DriveComboWidget driveCombo;

    private TemplateComboWidget templateCombo;

    private PropertyComboWidget propertyComboWidget;

    private PeriodComboWidget periodCombo;

    private StatisticsManager statisticsManager;

    private Button buildButton;

    private StatisticsTable statisticsTable;

    private DateTimeWidget startTime;

    private DateTimeWidget endTime;

    private boolean isInitialized = false;

    public StatisticsView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scrolledComposite.setLayout(ONE_ROW_GRID_LAYOUT);

        Composite mainComposite = new Composite(scrolledComposite, SWT.FILL);
        mainComposite.setLayout(ONE_ROW_GRID_LAYOUT);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite controlsComposite = new Composite(mainComposite, SWT.NONE);
        controlsComposite.setLayout(new GridLayout(4, false));
        controlsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addTemplateCompositeContent(controlsComposite);
        addPeriodCompositeContent(controlsComposite);

        statisticsTable = addStatisticsTableWidget(mainComposite, this);
        statisticsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        scrolledComposite.setContent(mainComposite);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        // TODO KV: should be better solution than static field
        if (MIN_WIDTH == null) {
            Rectangle clientArea = this.getViewSite().getShell().getClientArea();
            MIN_WIDTH = (int)(clientArea.width + (clientArea.width * ADDITIONAL_WIDTH_PERCENTAGE));
        }
        scrolledComposite.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        scrolledComposite.pack();

        isInitialized = true;
        driveCombo.updateSelection();
    }

    private void addTemplateCompositeContent(final Composite parent) {
        driveCombo = AWEWidgetFactory.getFactory().addDriveComboWidget(this, "Dataset:", parent, FIRST_ROW_LABEL_WIDTH);
        templateCombo = addTemplateComboWidget(parent, this);
        templateCombo.setEnabled(false);

        propertyComboWidget = AWEWidgetFactory.getFactory().addPropertyComboWidget(this, "Aggregation:", parent,
                THIRD_ROW_LABEL_WIDTH);
        propertyComboWidget.setEnabled(false);

        buildButton = new Button(parent, SWT.NONE);
        buildButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        buildButton.setText("Build");
        buildButton.addSelectionListener(this);
    }

    private void addPeriodCompositeContent(final Composite parent) {
        periodCombo = addPeriodComboWidget(parent, this);
        periodCombo.setEnabled(false);

        startTime = AWEWidgetFactory.getFactory().addPeriodWidget(this, "Start time:", parent, SECOND_ROW_LABEL_WIDTH);
        endTime = AWEWidgetFactory.getFactory().addPeriodWidget(this, "End time:", parent, THIRD_ROW_LABEL_WIDTH);
    }

    @Override
    public void dispose() {
        driveCombo.dispose();
        templateCombo.dispose();
        propertyComboWidget.dispose();
        periodCombo.dispose();
        statisticsTable.dispose();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    // TODO: LN: 09.08.2012, duplicate code
    private TemplateComboWidget addTemplateComboWidget(final Composite parent, final ITemplateSelectionListener listener) {
        TemplateComboWidget result = new TemplateComboWidget(parent, listener, "Template:", SECOND_ROW_LABEL_WIDTH);
        result.initializeWidget();

        return result;
    }

    // TODO: LN: 09.08.2012, duplicate code
    private PeriodComboWidget addPeriodComboWidget(final Composite parent, final IPeriodSelectionListener listener) {
        PeriodComboWidget result = new PeriodComboWidget(parent, listener, "Period:", FIRST_ROW_LABEL_WIDTH);
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

            propertyComboWidget.setDefaultProperties(StatisticsManager.getManager(model).getDefaultProperties());
            propertyComboWidget.setModel(model);
            propertyComboWidget.setEnabled(true);

            periodCombo.setModel(model);
            periodCombo.setEnabled(true);

            startTime.setDefaultDate(new Date(model.getMinTimestamp()));
            endTime.setDefaultDate(new Date(model.getMaxTimestamp()));
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
        updateStatistics();
    }

    private void updateStatistics() {
        if (statisticsManager != null) {
            setEnabledItems(false);
            StatisticsJob job = new StatisticsJob(statisticsManager);
            job.schedule();
        }
    }

    private void setEnabledItems(boolean isEnabled) {
        buildButton.setEnabled(isEnabled);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    private void updateTable(final IStatisticsModel statisticsModel, IStatisticsFilterContainer filterContainer) {
        statisticsTable.updateStatistics(statisticsModel, filterContainer);
    }

    @Override
    public void onTimeChanged(final Date newTime, final DateTimeWidget source) {
    }

}
