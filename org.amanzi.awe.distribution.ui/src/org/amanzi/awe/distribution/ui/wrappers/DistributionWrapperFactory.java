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

package org.amanzi.awe.distribution.ui.wrappers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionWrapperFactory implements ITreeWrapperFactory {

    private static final Logger LOGGER = Logger.getLogger(DistributionWrapperFactory.class);

    private final class DistributionModelIterator implements Iterator<IDistributionModel> {

        private final Iterator<IPropertyStatisticalModel> sourceModels;

        private Iterator<IDistributionModel> distributionModels;

        public DistributionModelIterator(final Iterator<IPropertyStatisticalModel> sourceModels) {
            this.sourceModels = sourceModels;
        }

        @Override
        public boolean hasNext() {
            if ((distributionModels == null || !distributionModels.hasNext()) && sourceModels.hasNext()) {
                distributionModels = getDistributionModelsIterator(sourceModels.next());
            }

            return distributionModels != null && distributionModels.hasNext();
        }

        @Override
        public IDistributionModel next() {
            return distributionModels.next();
        }

        @Override
        public void remove() {
            // TODO: LN: 16.10.2012, throw exception
        }

    }

    private final IDistributionModelProvider distributionModelProvider;

    private final IProjectModelProvider projectModelProvider;

    private final INetworkModelProvider networkModelProvider;

    private final IDriveModelProvider driveModelProvider;

    public DistributionWrapperFactory() {
        this.distributionModelProvider = DistributionEnginePlugin.getDefault().getDistributionModelProvider();
        this.projectModelProvider = DistributionEnginePlugin.getDefault().getProjectModelProvider();
        this.driveModelProvider = DistributionEnginePlugin.getDefault().getDriveModelProvider();
        this.networkModelProvider = DistributionEnginePlugin.getDefault().getNetworkModelProvider();
    }

    @Override
    public Iterator<ITreeWrapper> getWrappers(final Object parent) {
        Iterator<ITreeWrapper> result = null;

        try {
            IProjectModel projectModel = null;
            IPropertyStatisticalModel sourceModel = null;

            if (parent != null) {
                if (parent.equals(ObjectUtils.NULL)) {
                    projectModel = projectModelProvider.getActiveProjectModel();
                } else if (parent instanceof IUIItemNew) {
                    sourceModel = ((IUIItemNew)parent).castChild(IPropertyStatisticalModel.class);
                }
            }

            Iterator<IDistributionModel> modelIterator = null;

            if (projectModel != null) {
                modelIterator = new DistributionModelIterator(getSourceModelIterator(projectModel));
            } else if (sourceModel != null) {
                modelIterator = distributionModelProvider.findAll(sourceModel);
            }

            if (modelIterator != null) {
                result = new DistributionWrapperIterator(modelIterator);
            }
        } catch (final ModelException e) {
            LOGGER.error("Error on collecting Tree Wrappers", e);
        }
        return result;
    }

    private Iterator<IPropertyStatisticalModel> getSourceModelIterator(final IProjectModel activeProject) throws ModelException {
        final List<IPropertyStatisticalModel> result = new ArrayList<IPropertyStatisticalModel>();

        result.addAll(driveModelProvider.findAll(activeProject));
        result.addAll(networkModelProvider.findAll(activeProject));

        return result.iterator();
    }

    private Iterator<IDistributionModel> getDistributionModelsIterator(final IPropertyStatisticalModel sourceModel) {
        try {
            return distributionModelProvider.findAll(sourceModel);
        } catch (final ModelException e) {
            LOGGER.error("Error on searching for Distribution Models", e);
        }
        return null;
    }

}
