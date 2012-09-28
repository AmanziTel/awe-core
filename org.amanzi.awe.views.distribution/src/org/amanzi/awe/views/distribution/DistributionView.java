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

package org.amanzi.awe.views.distribution;

import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
import org.amanzi.awe.distribution.engine.manager.DistributionManager;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.PropertyComboWidget;
import org.amanzi.awe.ui.view.widgets.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.views.distribution.internal.DistributionPlugin;
import org.amanzi.awe.views.distribution.widgets.DistributionChartWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionChartWidget.IDistributionChartListener;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.DistributionDataset;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.IDistributionDatasetSelectionListener;
import org.amanzi.awe.views.distribution.widgets.DistributionTypeWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionTypeWidget.IDistributionTypeListener;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionView extends ViewPart implements IDistributionDatasetSelectionListener, IPropertySelectionListener, IDistributionTypeListener, IDistributionChartListener {

    private static final int FIRST_ROW_LABEL_WIDTH = 55;

    private static final int SECOND_ROW_LABEL_WIDTH = 60;

    private static final int THIRD_ROW_LABEL_WIDTH = 85;

    private class DistributionJob extends Job {

        private IDistributionModel model;

        public DistributionJob() {
            super("Create Distribuiton model <" + currentManager.getCurrentDistributionType() + ">");
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                model = currentManager.build(monitor);
            } catch (ModelException e) {
                return new Status(Status.ERROR, DistributionPlugin.PLUGIN_ID, "Error on calculating Distribution", e);
            } finally {
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        parentComposite.setEnabled(true);

                        if (model != null) {
                            updateCharts(model);
                        }
                    }

                }, true);
            }

            return Status.OK_STATUS;
        }

    }

    private PropertyComboWidget propertyCombo;

    private boolean isInitialized = false;

    private DistributionManager currentManager;

    private DistributionTypeWidget distributionTypeCombo;

    private DistributionChartWidget distributionChart;

    private Composite parentComposite;

    /**
     * 
     */
    public DistributionView() {

    }

    @Override
    public void createPartControl(final Composite parent) {
        parentComposite = parent;

        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(3, false));
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addDistributionTypeComposite(mainComposite);
        addDistributionChartComposite(mainComposite);

        isInitialized = true;
    }

    private void addDistributionChartComposite(final Composite parent) {
        distributionChart = addDistributionChartWidget(parent, this);
        distributionChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

        distributionChart.setVisible(false);
    }

    private void addDistributionTypeComposite(final Composite parent) {
        addDistributionDatasetWidget(parent, this, FIRST_ROW_LABEL_WIDTH);
        propertyCombo = AWEWidgetFactory.getFactory().addPropertyComboWidget(this, "Property:", parent, SECOND_ROW_LABEL_WIDTH);
        distributionTypeCombo = addDistributionTypeWidget(parent, this, THIRD_ROW_LABEL_WIDTH);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    private DistributionDatasetWidget addDistributionDatasetWidget(final Composite parent, final IDistributionDatasetSelectionListener listener, final int minWidth) {
        DistributionDatasetWidget result = new DistributionDatasetWidget(parent, listener, "Dataset:", minWidth,
                DistributionEnginePlugin.getDefault().getProjectModelProvider(),
                DistributionEnginePlugin.getDefault().getNetworkModelProvider(),
                DistributionEnginePlugin.getDefault().getDriveModelProvider());
        result.initializeWidget();

        return result;
    }

    private DistributionChartWidget addDistributionChartWidget(final Composite parent, final IDistributionChartListener listener) {
        DistributionChartWidget result = new DistributionChartWidget(parent, listener);
        result.initializeWidget();

        return result;
    }

    private DistributionTypeWidget addDistributionTypeWidget(final Composite parent, final IDistributionTypeListener listener, final int minWidth) {
        DistributionTypeWidget result = new DistributionTypeWidget(parent, listener, "Distribution:", minWidth);
        result.initializeWidget();

        return result;
    }

    @Override
    public void onDistributionDatasetSelected(final DistributionDataset distributionDataset) {
        if (isInitialized) {
            currentManager = DistributionManager.getManager(distributionDataset.getModel());
            currentManager.setNodeType(distributionDataset.getNodeType());

            propertyCombo.setModel(distributionDataset.getModel(), distributionDataset.getNodeType());
        }
    }

    @Override
    public void onPropertySelected(final String property) {
        if (isInitialized && (currentManager != null)) {
            currentManager.setProperty(property);

            distributionTypeCombo.setDistributionManager(currentManager);
        }
    }

    @Override
    public void onDistributionTypeSelected(final IDistributionType< ? > distributionType) {
        if (isInitialized && (currentManager != null)) {
            currentManager.setDistributionType(distributionType);

            runDistribution();
        }
    }

    private void runDistribution() {
        if (currentManager.canBuild()) {
            parentComposite.setEnabled(false);

            new DistributionJob().schedule();
        }
    }

    private void updateCharts(final IDistributionModel model) {
        distributionChart.updateDistribution(model);
    }

}
