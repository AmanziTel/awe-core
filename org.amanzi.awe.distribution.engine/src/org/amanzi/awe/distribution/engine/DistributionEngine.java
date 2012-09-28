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

package org.amanzi.awe.distribution.engine;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
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
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionEngine extends AbstractTransactional {

    private static final Logger LOGGER = Logger.getLogger(DistributionEngine.class);

    private static class DistributionEngineHolder {
        private static volatile DistributionEngine instance = new DistributionEngine();
    }

    private final IDistributionModelProvider distributionModelProvider;

    /**
     * 
     */
    protected DistributionEngine(final IDistributionModelProvider distributionModelProvider) {
        this.distributionModelProvider = distributionModelProvider;
    }

    private DistributionEngine() {
        this(DistributionEnginePlugin.getDefault().getDistributionModelProvider());
    }

    public static DistributionEngine getEngine() {
        return DistributionEngineHolder.instance;
    }

    public IDistributionModel build(final IPropertyStatisticalModel analyzedModel, final IDistributionType<?> distributionType, IProgressMonitor progressMonitor) throws ModelException {
        LOGGER.info("Started Distribution Calculation for Model <" + analyzedModel + "> with distribution type <" + distributionType + ">");

        if (progressMonitor == null) {
            progressMonitor = new NullProgressMonitor();
        }

        startTransaction();

        boolean isSuccess = false;

        IDistributionModel result = null;

        try {
            result = distributionModelProvider.findDistribution(analyzedModel, distributionType);

            if (result == null) {
                LOGGER.info("No Distribution was found by type <" + distributionType + "> for model " + analyzedModel + ". Create new one.");

                result = distributionModelProvider.createDistribution(analyzedModel, distributionType);

                calculateDistribution(result, analyzedModel, distributionType, progressMonitor);
            }

            result.setCurrent(true);

            result.finishUp();
            isSuccess = true;
        } catch (Exception e) {
            LOGGER.error("An error occured on Distribution calculation", e);
            throw new FatalException(e);
        } finally {
            saveTx(isSuccess, false);
        }

        LOGGER.info("Finished Distribution Calculation");

        return result;
    }

    private void calculateDistribution(final IDistributionModel distributionModel, final IPropertyStatisticalModel analyzedModel, final IDistributionType<?> distributionType, final IProgressMonitor monitor) throws ModelException {
        int totalCount = getTotalElementCount(distributionType, analyzedModel.getPropertyStatistics());

        try {
            monitor.beginTask("Calculating Distribution <" + distributionType + ">", totalCount);

            List<Pair<IRange, IDistributionBar>> distributionConditions = createDistributionBars(distributionModel, distributionType);

            for (IDataElement element : analyzedModel.getAllElementsByType(distributionType.getNodeType())) {
                for (Pair<IRange, IDistributionBar> condition : distributionConditions) {
                    IRange range = condition.getLeft();

                    if (range.getFilter().matches(element)) {
                        IDistributionBar bar = condition.getRight();

                        distributionModel.createAggregation(bar, element);
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

    private int getTotalElementCount(final IDistributionType<?> distributionType, final IPropertyStatisticsModel propertyStatistics) {
        int result = 0;

        for (Object property : propertyStatistics.getValues(distributionType.getNodeType(), distributionType.getPropertyName())) {
            result += propertyStatistics.getValueCount(distributionType.getNodeType(), distributionType.getPropertyName(), property);
        }

        return result;
    }

    private List<Pair<IRange, IDistributionBar>> createDistributionBars(final IDistributionModel distributionModel, final IDistributionType<?> type) throws ModelException {
        List<Pair<IRange, IDistributionBar>> result = new ArrayList<Pair<IRange,IDistributionBar>>();

        for (IRange range : type.getRanges()) {
            result.add(new ImmutablePair<IRange, IDistributionBar>(range, distributionModel.createDistributionBar(range)));
        }

        return result;
    }

}
