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

package org.amanzi.neo.services.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.ICountersModel;
import org.amanzi.neo.services.model.ICountersType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class CountersModel extends AbstractIndexedModel implements ICountersModel {

    @Override
    public Iterable<ICorrelationModel> getCorrelatedModels() {
        List<ICorrelationModel> result = new ArrayList<ICorrelationModel>();
        for (Node dataset : NeoServiceFactory.getInstance().getNewCorrelationService().getCorrelatedDatasets(getRootNode())) {
            // TODO: create correlated models
        }
        return result;
    }

    @Override
    public ICorrelationModel getCorrelatedModel(String correlationModelName) {
        return null;
    }

    @Override
    public void updateTimestamp(long timestamp) throws DatabaseException {
        super.updateTimestamp(timestamp);
    }

    @Override
    public ICountersType getCountersType() {
        return null;
    }

    @Override
    public long getMaxTimestamp() {
        return super.getMaxTimestamp();
    }

    @Override
    public long getMinTimestamp() {
        return super.getMinTimestamp();
    }

}
