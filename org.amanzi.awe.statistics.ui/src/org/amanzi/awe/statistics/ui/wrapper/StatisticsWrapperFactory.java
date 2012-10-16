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

package org.amanzi.awe.statistics.ui.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IDriveModelProvider;
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
public class StatisticsWrapperFactory implements ITreeWrapperFactory {

    private static final Logger LOGGER = Logger.getLogger(StatisticsWrapperFactory.class);

    private final class StatisticsModelIterator implements Iterator<IStatisticsModel> {

        private final Iterator<IMeasurementModel> sourceModels;

        private Iterator<IStatisticsModel> statisticsModels;

        public StatisticsModelIterator(final Iterator<IMeasurementModel> sourceModels) {
            this.sourceModels = sourceModels;
        }

        @Override
        public boolean hasNext() {
            if ((statisticsModels == null || !statisticsModels.hasNext()) && sourceModels.hasNext()) {
                statisticsModels = getStatisticsModelsIterator(sourceModels.next());
            }

            return statisticsModels != null && statisticsModels.hasNext();
        }

        @Override
        public IStatisticsModel next() {
            return statisticsModels.next();
        }

        @Override
        public void remove() {
            // TODO: LN: 16.10.2012, throw exception
        }

    }

    private final IStatisticsModelProvider statisticsModelProvider;

    private final IProjectModelProvider projectModelProvider;

    private final IDriveModelProvider driveModelProvider;

    public StatisticsWrapperFactory() {
        this.statisticsModelProvider = StatisticsModelPlugin.getDefault().getStatisticsModelProvider();
        this.projectModelProvider = StatisticsModelPlugin.getDefault().getProjectModelProvider();
        this.driveModelProvider = StatisticsModelPlugin.getDefault().getDriveModelProvider();
    }

    @Override
    public Iterator<ITreeWrapper> getWrappers(final Object parent) {
        Iterator<ITreeWrapper> result = null;

        try {
            IProjectModel projectModel = null;
            IMeasurementModel sourceModel = null;

            if (parent != null) {
                if (parent.equals(ObjectUtils.NULL)) {
                    projectModel = projectModelProvider.getActiveProjectModel();
                } else if (parent instanceof IUIItemNew) {
                    sourceModel = ((IUIItemNew)parent).castChild(IMeasurementModel.class);
                }
            }

            Iterator<IStatisticsModel> modelIterator = null;

            if (projectModel != null) {
                modelIterator = new StatisticsModelIterator(getMeasurementModelIterator(projectModel));
            } else if (sourceModel != null) {
                modelIterator = getStatisticsModelsIterator(sourceModel);
            }

            if (modelIterator != null) {
                result = new StatisticsWrapperIterator(modelIterator);
            }
        } catch (final ModelException e) {
            LOGGER.error("Error on collecting Tree Wrappers", e);
        }
        return result;
    }

    private Iterator<IMeasurementModel> getMeasurementModelIterator(final IProjectModel activeProject) throws ModelException {
        final List<IMeasurementModel> result = new ArrayList<IMeasurementModel>();

        result.addAll(driveModelProvider.findAll(activeProject));

        return result.iterator();
    }

    private Iterator<IStatisticsModel> getStatisticsModelsIterator(final IMeasurementModel sourceModel) {
        try {
            return statisticsModelProvider.findAll(sourceModel).iterator();
        } catch (final ModelException e) {
            LOGGER.error("Error on searching for Statistics Models", e);
        }
        return null;
    }

}
