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

package org.amanzi.awe.correlation.engine;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.correlation.CorrelationPlugin;
import org.amanzi.awe.correlation.exception.CorrelationEngineException;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.provider.ICorrelationModelProvider;
import org.amanzi.neo.core.transactional.AbstractTransactional;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationEngine extends AbstractTransactional {

    @SuppressWarnings("unused")
    private static class ID {

        private final String correlatedProperies;
        private final IMeasurementModel measurementModel;
        private final String correlatedProeprty;
        private final INetworkModel networkModel;

        /**
         * @param model
         * @param period
         * @param template
         * @param propertyName
         */
        public ID(final INetworkModel networkModel, final IMeasurementModel measurementModel, final String correlationProperties,
                final String correlatedroperties) {
            super();
            this.networkModel = networkModel;
            this.correlatedProeprty = correlationProperties;
            this.measurementModel = measurementModel;
            this.correlatedProperies = correlatedroperties;
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, false);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

    }

    private static final Logger LOGGER = Logger.getLogger(CorrelationEngine.class);

    private final ICorrelationModelProvider modelProvider;

    private final INetworkModel networkModel;

    private final String correlationPropertyName;

    private final IMeasurementModel measurementModel;

    private final String correlatedPropertyName;

    private static Map<ID, CorrelationEngine> engineCache = new HashMap<ID, CorrelationEngine>();

    public static synchronized CorrelationEngine getEngine(final INetworkModel networkModel, final String correlationProperties,
            final IMeasurementModel measurementModel, final String correlatedroperties) {
        final ID id = new ID(networkModel, measurementModel, correlationProperties, correlatedroperties);
        CorrelationEngine result = engineCache.get(id);

        if (result == null) {
            result = new CorrelationEngine(networkModel, correlationProperties, measurementModel, correlatedroperties);
            engineCache.put(id, result);
        }

        return result;
    }

    /**
     * @param correlationModelProvider
     * @param networkModel
     * @param correlationProperty
     * @param measurementModel
     * @param correlatedroperties
     */
    protected CorrelationEngine(final ICorrelationModelProvider correlationModelProvider, final INetworkModel networkModel,
            final String correlationProperty, final IMeasurementModel measurementModel, final String correlatedroperties) {
        this.modelProvider = correlationModelProvider;
        this.networkModel = networkModel;
        this.correlationPropertyName = correlationProperty;
        this.measurementModel = measurementModel;
        this.correlatedPropertyName = correlatedroperties;
    }

    private CorrelationEngine(final INetworkModel networkModel, final String correlationProperties,
            final IMeasurementModel measurementModel, final String correlatedroperties) {
        this(CorrelationPlugin.getDefault().getCorrelationModelProvider(), networkModel, correlationProperties, measurementModel,
                correlatedroperties);
    }

    public ICorrelationModel build(IProgressMonitor monitor) throws CorrelationEngineException {
        LOGGER.info("Start correlation colculating for network <" + networkModel.getName() + "> and measurement model <"
                + measurementModel.getName() + "> by properties " + correlationPropertyName + " and " + correlatedPropertyName);

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        startTransaction();

        ICorrelationModel correlationModel = null;

        monitor.beginTask("Start correlation calculation", 1);

        boolean isSuccess = false;
        try {
            correlationModel = modelProvider.findCorrelationModel(networkModel, measurementModel, correlationPropertyName,
                    correlatedPropertyName);
            if (correlationModel == null) {
                LOGGER.info("CorrelationModel not exists in Database. Create new one.");
                correlationModel = modelProvider.createCorrelationModel(networkModel, measurementModel, correlationPropertyName,
                        correlatedPropertyName);

            }
            buildCorrelation(correlationModel, monitor);
            isSuccess = true;
        } catch (final Exception e) {
            LOGGER.error("An error occured on Statistics Calculation", e);
            throw new CorrelationEngineException(networkModel, measurementModel, correlationPropertyName, correlatedPropertyName, e);
        } finally {
            saveTx(isSuccess, false);
            monitor.done();
        }

        LOGGER.info("Finished Correlation Calculation");

        return correlationModel;
    }

    /**
     * @param correlationModel
     * @param monitor
     * @throws ModelException
     */
    private void buildCorrelation(final ICorrelationModel correlationModel, final IProgressMonitor monitor) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building statistics");
        }

        try {
            computeCorrelation(correlationModel, monitor);
        } catch (final ModelException e) {
            LOGGER.error("Error on calculating statistics", e);
            throw e;
        } finally {
            correlationModel.finishUp();
        }

    }

    /**
     * @param correlationModel
     * @param monitor
     * @throws ModelException
     */
    private void computeCorrelation(final ICorrelationModel correlationModel, final IProgressMonitor monitor) throws ModelException {
        String subProgressName = "Compute correlation for network <" + networkModel.getName() + "> and measurement model <"
                + measurementModel.getName() + "> by properties " + correlationPropertyName + " and " + correlatedPropertyName;
        final IProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 1);
        monitor.subTask(subProgressName);
        subProgressMonitor.beginTask(subProgressName, networkModel.getPropertyStatistics().getCount(NetworkElementType.SECTOR));

        for (IDataElement sector : networkModel.getAllElementsByType(NetworkElementType.SECTOR)) {
            Object correlationProperty = sector.get(correlationPropertyName);
            Object sectorLac = sector.get("lac");
            if (correlationProperty == null) {
                subProgressMonitor.worked(1);
                continue;
            }
            Iterable<IDataElement> correlatedElements = measurementModel.findElementByProperty(this.correlatedPropertyName,
                    correlationProperty);

            for (IDataElement element : correlatedElements) {
                Object measurementLac = element.get("lac");
                if (measurementLac != null && sectorLac != null && !measurementLac.equals(sectorLac)) {
                    continue;
                }
                correlationModel.getProxy(sector, element);
            }
            updateTransaction();
            subProgressMonitor.worked(1);
        }
    }

}
