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

package org.amanzi.awe.statistics.provider.impl;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.impl.StatisticsModel;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsModelProvider extends AbstractModelProvider<StatisticsModel, IStatisticsModel>
        implements
            IStatisticsModelProvider {

    @Override
    protected StatisticsModel createInstance() {
        return null;
    }

    @Override
    protected Class< ? extends IStatisticsModel> getModelClass() {
        return StatisticsModel.class;
    }

    @Override
    public IStatisticsModel find(IMeasurementModel analyzedModel, String template, String propertyName) throws ModelException {
        return null;
    }

    @Override
    public IStatisticsModel create(IMeasurementModel analyzedModel, String template, String propertyName) throws ModelException {
        return null;
    }

}
