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

package org.amanzi.awe.statistics.engine;

import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsEngine {

    private static final Logger LOGGER = Logger.getLogger(StatisticsEngine.class);

    private static class StatisticsEngineHandler {
        private static volatile StatisticsEngine instance = new StatisticsEngine();
    }

    private final IStatisticsModelProvider statisticsModelProvider;

    /**
     * 
     */
    private StatisticsEngine() {
        this(StatisticsModelPlugin.getDefault().getStatisticsModelProvider());
    }

    protected StatisticsEngine(IStatisticsModelProvider statisticsModelProvider) {
        this.statisticsModelProvider = statisticsModelProvider;
    }

    public static synchronized StatisticsEngine getEngine() {
        return StatisticsEngineHandler.instance;
    }

    public IStatisticsModel build(IMeasurementModel measurementModel, Template template, Period period, String propertyName)
            throws StatisticsEngineException {
        LOGGER.info("Started Statistics Calculation for Model <" + measurementModel + "> on property <" + propertyName
                + "> by template <" + template + "> with period <" + period + ">.");

        // TODO: LN: 10.08.2012, check input

        IStatisticsModel result = null;

        try {
            result = statisticsModelProvider.find(measurementModel, template.getTemplateName(), propertyName);
            if (result == null) {
                LOGGER.info("Statistics not exists in Database. Calculate new one.");

                result = buildStatistics(measurementModel, template, period, propertyName);

            } else {
                LOGGER.info("Statistics already exists in Database");
            }
        } catch (ModelException e) {
            LOGGER.error("An error occured on Statistics Calculation");
            throw new UnderlyingModelException(e);
        }

        LOGGER.info("Finished Statistics Calculation");

        return result;
    }

    protected IStatisticsModel buildStatistics(IMeasurementModel measurementModel, Template template, Period period,
            String propertyName) throws StatisticsEngineException {
        return null;
    }
}
