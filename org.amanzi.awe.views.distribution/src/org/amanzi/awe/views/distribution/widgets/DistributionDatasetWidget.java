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

package org.amanzi.awe.views.distribution.widgets;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.DistributionDataset;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.IDistributionDatasetSelectionListener;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionDatasetWidget extends AbstractComboWidget<DistributionDataset, IDistributionDatasetSelectionListener> {

    private static final Logger LOGGER = Logger.getLogger(DistributionDatasetWidget.class);

    private static final String DISTRIBUTION_DATASET_NAME_PATTERN = "{0} - {1}";

    public interface IDistributionDatasetSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onDistributionDatasetSelected(DistributionDataset distributionDataset);

    }

    public class DistributionDataset {

        private final IPropertyStatisticalModel model;

        private final INodeType nodeType;

        public DistributionDataset(final IPropertyStatisticalModel model, final INodeType nodeType) {
            this.model = model;
            this.nodeType = nodeType;
        }

        public IPropertyStatisticalModel getModel() {
            return model;
        }

        public INodeType getNodeType() {
            return nodeType;
        }

    }

    private final IProjectModelProvider projectModelProvider;

    private final INetworkModelProvider networkModelProvider;

    private final IDriveModelProvider driveModelProvider;

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    public DistributionDatasetWidget(final Composite parent, final IDistributionDatasetSelectionListener listener,
            final String label, final int minimalLabelWidth, final IProjectModelProvider projectModelProvider,
            final INetworkModelProvider networkModelProvider, final IDriveModelProvider driveModelProvider) {
        super(parent, listener, label, minimalLabelWidth);

        this.driveModelProvider = driveModelProvider;
        this.networkModelProvider = networkModelProvider;
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    protected Collection<DistributionDataset> getItems() {
        Collection<DistributionDataset> result = null;
        try {
            IProjectModel activeProject = projectModelProvider.getActiveProjectModel();

            if (activeProject != null) {
                result = new ArrayList<DistributionDatasetWidget.DistributionDataset>();

                for (IDriveModel driveModel : driveModelProvider.findAll(activeProject)) {
                    addDistributionDatasetsForModel(driveModel, result);
                }

                for (INetworkModel networkModel : networkModelProvider.findAll(activeProject)) {
                    addDistributionDatasetsForModel(networkModel, result);
                }
            }
        } catch (ModelException e) {
            LOGGER.error("An error occured on getting all Distribution Datasets", e);
            result = null;
        }

        return result == null ? null : result.isEmpty() ? null : result;
    }

    private void addDistributionDatasetsForModel(final IPropertyStatisticalModel propertyStatisticalModel,
            final Collection<DistributionDataset> distributionDatasets) {
        for (INodeType nodeType : propertyStatisticalModel.getPropertyStatistics().getNodeTypes()) {
            distributionDatasets.add(new DistributionDataset(propertyStatisticalModel, nodeType));
        }
    }

    @Override
    protected String getItemName(final DistributionDataset item) {
        return MessageFormat.format(DISTRIBUTION_DATASET_NAME_PATTERN, item.getModel().getName(), item.getNodeType().getId());
    }

    @Override
    protected void fireListener(final IDistributionDatasetSelectionListener listener, final DistributionDataset selectedItem) {
        listener.onDistributionDatasetSelected(selectedItem);

    }

    @Override
    protected int getDefaultSelectedItemIndex() {
        return -1;
    }

}
