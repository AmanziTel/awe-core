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

package org.amanzi.awe.distribution.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.distribution.dto.IAggregationRelation;
import org.amanzi.awe.distribution.engine.impl.internal.AbstractDistributionEngine;
import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.distribution.model.type.IDistributionType.Select;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.apache.commons.lang3.tuple.Pair;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MeasurementDistributionEngine extends AbstractDistributionEngine<IMeasurementModel> {

    private interface IRelationUpdater {

        void updateRelation(IDistributionModel model, IDistributionBar bar, IDataElement element, ILocationElement location,
                IDistributionType< ? > distributionType) throws ModelException;

    }

    private class SimpleRelationUpdater implements IRelationUpdater {

        @Override
        public void updateRelation(final IDistributionModel model, final IDistributionBar bar, final IDataElement element,
                final ILocationElement location, final IDistributionType< ? > distributionType) throws ModelException {
            if (model.findAggregationRelation(location) == null) {
                model.createAggregation(bar, location);
            }
        }

    }

    private abstract class ValueRelationUpdater implements IRelationUpdater {

        @Override
        public void updateRelation(final IDistributionModel model, final IDistributionBar bar, final IDataElement element,
                final ILocationElement location, final IDistributionType< ? > distributionType) throws ModelException {
            final Object value = element.get(distributionType.getPropertyName());

            IAggregationRelation relation = model.findAggregationRelation(location);

            boolean shouldUpdate = false;
            Double dValue = null;

            if (value instanceof Number) {
                dValue = ((Number)value).doubleValue();

                shouldUpdate = relation != null && shouldUpdate(relation, dValue);
            }

            if (relation == null) {
                relation = model.createAggregation(bar, location);
            }

            if (shouldUpdate) {
                update(model, bar, relation, dValue, location);
            }
        }

        protected abstract boolean shouldUpdate(IAggregationRelation relation, double value);

        protected void update(final IDistributionModel model, final IDistributionBar bar, final IAggregationRelation relation,
                final double dValue, final ILocationElement location) throws ModelException {
            relation.setValue(dValue);
            model.updateAggregationRelation(location, relation, bar);
        }
    }

    private class MinRelationUpdated extends ValueRelationUpdater {

        @Override
        protected boolean shouldUpdate(final IAggregationRelation relation, final double value) {
            return value < relation.getValue();
        }

    }

    private class MaxRelationUpdated extends ValueRelationUpdater {

        @Override
        protected boolean shouldUpdate(final IAggregationRelation relation, final double value) {
            return value > relation.getValue();
        }

    }

    private class AverageRelationUpdated extends ValueRelationUpdater {

        @Override
        protected boolean shouldUpdate(final IAggregationRelation relation, final double value) {
            return true;
        }

        @Override
        protected void update(final IDistributionModel model, final IDistributionBar bar, final IAggregationRelation relation,
                final double dValue, final ILocationElement location) throws ModelException {
            // update relation data
            final double previousValue = relation.getValue();
            final int previousCount = relation.getCount();
            final double newValue = (previousValue * previousCount + dValue) / (previousCount + 1);
            relation.setValue(newValue);
            relation.setCount(previousCount + 1);

            model.updateAggregationRelation(null, relation, getDistributionBar(model, dValue));
        }

        private IDistributionBar getDistributionBar(final IDistributionModel model, final Double dValue) {
            for (final Pair<IRange, IDistributionBar> condition : getDistributionConditions()) {
                if (condition.getKey().getFilter().matches(dValue)) {
                    return condition.getRight();
                }
            }

            return null;
        }
    }

    private final Map<Select, IRelationUpdater> relationUpdaterCache = new HashMap<Select, IRelationUpdater>();

    /**
     * @param analyzedModel
     * @param distributionModelProvider
     */
    public MeasurementDistributionEngine(final IMeasurementModel analyzedModel) {
        super(analyzedModel, DistributionEnginePlugin.getDefault().getDistributionModelProvider());

        final SimpleRelationUpdater simpleUpdater = new SimpleRelationUpdater();
        relationUpdaterCache.put(Select.EXISTS, simpleUpdater);
        relationUpdaterCache.put(Select.FIRST, simpleUpdater);

        relationUpdaterCache.put(Select.MIN, new MinRelationUpdated());
        relationUpdaterCache.put(Select.MAX, new MaxRelationUpdated());
        relationUpdaterCache.put(Select.AVERAGE, new AverageRelationUpdated());
    }

    @Override
    protected void createAggregation(final IDistributionModel distributionModel, final IDistributionBar distributionBar,
            final IDataElement element, final IDistributionType< ? > distributionType) throws ModelException {
        super.createAggregation(distributionModel, distributionBar, element, distributionType);

        final ILocationElement location = getAnalyzedModel().getElementLocation(element);

        if (location != null) {
            relationUpdaterCache.get(distributionType.getSelect()).updateRelation(distributionModel, distributionBar, element,
                    location, distributionType);
        }
    }
}
