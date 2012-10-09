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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.distribution.engine.impl.MeasurementDistributionEngine;
import org.amanzi.awe.distribution.engine.impl.NetworkDistributionEngine;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionEngineFactory {

    private static class DistributionEngineFactoryHolder {
        private static volatile DistributionEngineFactory instance = new DistributionEngineFactory();
    }

    private final Map<IPropertyStatisticalModel, IDistributionEngine< ? >> engineCache = new HashMap<IPropertyStatisticalModel, IDistributionEngine< ? >>();

    private DistributionEngineFactory() {

    }

    public static synchronized DistributionEngineFactory getFactory() {
        return DistributionEngineFactoryHolder.instance;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends IPropertyStatisticalModel> IDistributionEngine<T> getEngine(final T model) {
        IDistributionEngine<T> engine = (IDistributionEngine<T>)engineCache.get(model);

        if (engine == null) {
            if (model instanceof INetworkModel) {
                engine = (IDistributionEngine<T>)new NetworkDistributionEngine((INetworkModel)model);
            } else if (model instanceof IMeasurementModel) {
                engine = (IDistributionEngine<T>)new MeasurementDistributionEngine((IMeasurementModel)model);
            }

            if (engine != null) {
                engineCache.put(model, engine);
            }
        }

        return engine;
    }
}
