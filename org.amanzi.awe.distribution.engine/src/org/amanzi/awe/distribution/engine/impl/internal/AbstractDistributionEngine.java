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

package org.amanzi.awe.distribution.engine.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.distribution.engine.IDistributionEngine;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.neo.core.transactional.AbstractTransactional;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractDistributionEngine<T extends IPropertyStatisticalModel> extends AbstractTransactional
        implements
            IDistributionEngine<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDistributionEngine.class);

    private final T analyzedModel;

    private final IDistributionModelProvider distributionModelProvider;

    private List<Pair<IRange, IDistributionBar>> distributionConditions;

    protected AbstractDistributionEngine(final T analyzedModel, final IDistributionModelProvider distributionModelProvider) {
        this.analyzedModel = analyzedModel;
        this.distributionModelProvider = distributionModelProvider;
    }

    @Override
    public IDistributionModel build(final IDistributionType< ? > distributionType, IProgressMonitor progressMonitor)
            throws ModelException {
        LOGGER.info("Started Distribution Calculation for Model <" + analyzedModel + "> with distribution type <"
                + distributionType + ">");

        if (progressMonitor == null) {
            progressMonitor = new NullProgressMonitor();
        }

        startTransaction();

        boolean isSuccess = false;

        IDistributionModel result = null;

        try {
            result = distributionModelProvider.findDistribution(analyzedModel, distributionType);

            if (result == null) {
                LOGGER.info("No Distribution was found by type <" + distributionType + "> for model " + analyzedModel
                        + ". Create new one.");

                result = distributionModelProvider.createDistribution(analyzedModel, distributionType);

                calculateDistribution(result, analyzedModel, distributionType, progressMonitor);
            }

            result.setCurrent(true);

            result.finishUp();
            isSuccess = true;
        } catch (final Exception e) {
            LOGGER.error("An error occured on Distribution calculation", e);
            throw new FatalException(e);
        } finally {
            saveTx(isSuccess, false);
        }

        LOGGER.info("Finished Distribution Calculation");

        return result;
    }

    private void calculateDistribution(final IDistributionModel distributionModel, final IPropertyStatisticalModel analyzedModel,
            final IDistributionType< ? > distributionType, final IProgressMonitor monitor) throws ModelException {
        final int totalCount = getTotalElementCount(distributionType, analyzedModel.getPropertyStatistics());

        try {
            monitor.beginTask("Calculating Distribution <" + distributionType + ">", totalCount);

            distributionConditions = createDistributionBars(distributionModel, distributionType);

            for (final IDataElement element : analyzedModel.getAllElementsByType(distributionType.getNodeType())) {
                for (final Pair<IRange, IDistributionBar> condition : distributionConditions) {
                    final IRange range = condition.getLeft();

                    if (range.getFilter().matches(element)) {
                        final IDistributionBar bar = condition.getRight();

                        createAggregation(distributionModel, bar, element, distributionType);
                        updateTransaction();

                        break;
                    }
                }
                monitor.worked(1);
            }
        } finally {
            distributionModel.finishUp();
            monitor.done();
        }
    }

    protected void createAggregation(final IDistributionModel distributionModel, final IDistributionBar distributionBar,
            final IDataElement element, final IDistributionType< ? > distributionType) throws ModelException {
        distributionModel.createAggregation(distributionBar, element);
    }

    private int getTotalElementCount(final IDistributionType< ? > distributionType,
            final IPropertyStatisticsModel propertyStatistics) {
        int result = 0;

        for (final Object property : propertyStatistics.getValues(distributionType.getNodeType(),
                distributionType.getPropertyName())) {
            result += propertyStatistics
                    .getValueCount(distributionType.getNodeType(), distributionType.getPropertyName(), property);
        }

        return result;
    }

    private List<Pair<IRange, IDistributionBar>> createDistributionBars(final IDistributionModel distributionModel,
            final IDistributionType< ? > type) throws ModelException {
        final List<Pair<IRange, IDistributionBar>> result = new ArrayList<Pair<IRange, IDistributionBar>>();

        for (final IRange range : type.getRanges()) {
            result.add(new ImmutablePair<IRange, IDistributionBar>(range, distributionModel.createDistributionBar(range)));
        }

        return result;
    }

    protected T getAnalyzedModel() {
        return analyzedModel;
    }

    protected List<Pair<IRange, IDistributionBar>> getDistributionConditions() {
        return distributionConditions;
    }

}
