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

package org.amanzi.awe.correlation.ui.view;

import org.amanzi.awe.correlation.engine.CorrelationEngine;
import org.amanzi.awe.correlation.exception.CorrelationEngineException;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.ui.CorrelationUiPlugin;
import org.amanzi.awe.correlation.ui.internal.CorrelationMessages;
import org.amanzi.awe.correlation.ui.view.table.CorrelationTableColumns;
import org.amanzi.awe.correlation.ui.view.table.CorrelationTableContentProvider;
import org.amanzi.awe.correlation.ui.view.table.CorrelationTableLabelProvider;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.DriveComboWidget;
import org.amanzi.awe.ui.view.widgets.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widgets.NetworkComboWidget;
import org.amanzi.awe.ui.view.widgets.NetworkComboWidget.INetworkSelectionListener;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationView extends ViewPart implements IDriveSelectionListener, INetworkSelectionListener, SelectionListener {

    private class CorrelationJob extends Job {

        private final String correlatedProperty;
        private final IMeasurementModel measurementModel;
        private final String correlationProperty;
        private final INetworkModel networkModel;

        /**
         * @param selectedNetwork
         * @param string
         * @param selectedMeasurement
         * @param string2
         */
        public CorrelationJob(final INetworkModel selectedNetwork, final String correlationProperty,
                final IMeasurementModel selectedMeasurement, final String correlatedProperty) {
            super("Building correlation between " + selectedNetwork + " and : " + selectedMeasurement + "by network property"
                    + correlationProperty + "and measurement property" + correlatedProperty);
            this.networkModel = selectedNetwork;
            this.correlationProperty = correlationProperty;
            this.measurementModel = selectedMeasurement;
            this.correlatedProperty = correlatedProperty;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            CorrelationEngine engine = CorrelationEngine.getEngine(networkModel, correlationProperty, measurementModel,
                    correlatedProperty);
            try {
                final ICorrelationModel model = engine.build(monitor);
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        updateTable(model);
                    }
                }, true);
            } catch (CorrelationEngineException e) {
                return new Status(Status.ERROR, CorrelationUiPlugin.getDefault().getPluginId(), e.getMessage());
            }
            return Status.OK_STATUS;
        }
    }

    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);

    private static final GridLayout TWO_ROW_GRID_LAYOUT = new GridLayout(3, false);

    private static final int MINIMAL_LABEL_WIDTH = 0;

    private INetworkModel selectedNetwork;

    private IMeasurementModel selectedMeasurement;

    private Button btnCorrelate;

    private TableViewer correlationTable;

    /**
     * @param tableLayout
     * @param networkColumn
     */
    private void createColumn(final CorrelationTableColumns columnData, final TableLayout tableLayout) {
        TableColumn column = new TableColumn(correlationTable.getTable(), SWT.BORDER);
        column.setText(columnData.getName());
        column.setToolTipText(columnData.getName());
        tableLayout.addColumnData(new ColumnWeightData(1));

    }

    @Override
    public void createPartControl(final Composite parent) {

        final Composite mainComposite = new Composite(parent, SWT.FILL);
        mainComposite.setLayout(ONE_ROW_GRID_LAYOUT);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Composite controlComposite = new Composite(mainComposite, SWT.FILL);
        controlComposite.setLayout(TWO_ROW_GRID_LAYOUT);
        controlComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        DriveComboWidget driveWidget = AWEWidgetFactory.getFactory().addDriveComboWidget(this,
                CorrelationMessages.DRIVE_NAME_LABEL, controlComposite, MINIMAL_LABEL_WIDTH);

        driveWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        NetworkComboWidget networkWidget = AWEWidgetFactory.getFactory().addNetworkComboWidget(this,
                CorrelationMessages.NETWORK_NAME_LABEL, controlComposite, MINIMAL_LABEL_WIDTH);

        networkWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnCorrelate = new Button(controlComposite, SWT.PUSH);
        btnCorrelate.setText(CorrelationMessages.CORRELATION_BUTTON);
        btnCorrelate.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
        btnCorrelate.addSelectionListener(this);

        createTable(mainComposite);

    }

    /**
     * @param mainComposite
     */
    private void createTable(final Composite mainComposite) {
        final TableLayout tableLayout = new TableLayout();
        correlationTable = new TableViewer(mainComposite, SWT.BORDER);
        for (CorrelationTableColumns column : CorrelationTableColumns.values()) {
            createColumn(column, tableLayout);
        }

        correlationTable.getTable().setLayout(tableLayout);
        correlationTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        correlationTable.setContentProvider(new CorrelationTableContentProvider());
        correlationTable.setLabelProvider(new CorrelationTableLabelProvider());
        correlationTable.getTable().setHeaderVisible(true);
        correlationTable.getTable().setLinesVisible(true);
        correlationTable.getTable().setVisible(true);
    }

    @Override
    public void onDriveModelSelected(final IDriveModel model) {
        this.selectedMeasurement = model;

    }

    @Override
    public void onNetworkModelSelected(final INetworkModel model) {
        this.selectedNetwork = model;

    }

    @Override
    public void setFocus() {

    }

    private void updateTable(final ICorrelationModel model) {
        correlationTable.setInput(model);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        if (e.getSource().equals(btnCorrelate)) {
            if (selectedNetwork != null && selectedMeasurement != null) {
                CorrelationJob job = new CorrelationJob(selectedNetwork, "ci", selectedMeasurement, "cell_id");
                job.schedule();
            }
        }

    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(btnCorrelate)) {
            if (selectedNetwork != null && selectedMeasurement != null) {
                CorrelationJob job = new CorrelationJob(selectedNetwork, "ci", selectedMeasurement, "cell_id");
                job.schedule();
            }
        }

    }
}
