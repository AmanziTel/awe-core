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

import java.util.Iterator;

import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
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

    private final IDistributionModelProvider distributionModelProvider;

    private final IProjectModelProvider projectModelProvider;

    public DistributionWrapperFactory() {
        this.distributionModelProvider = DistributionEnginePlugin.getDefault().getDistributionModelProvider();
        this.projectModelProvider = DistributionEnginePlugin.getDefault().getProjectModelProvider();
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
                modelIterator = distributionModelProvider.findAll(projectModel);
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

}
