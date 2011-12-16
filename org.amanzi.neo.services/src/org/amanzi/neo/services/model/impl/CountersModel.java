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
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.ICountersModel;
import org.amanzi.neo.services.model.ICountersType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
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

    private CorrelationService crServ = NeoServiceFactory.getInstance().getCorrelationService();
    private DatasetService dsServ = NeoServiceFactory.getInstance().getDatasetService();

    protected CountersModel(Node rootNode) throws AWEException {
        super(rootNode, DatasetTypes.COUNTERS);
    }

    @Override
    public Iterable<ICorrelationModel> getCorrelatedModels() throws AWEException {
        List<ICorrelationModel> result = new ArrayList<ICorrelationModel>();
        for (Node network : crServ.getCorrelatedNetworks(getRootNode())) {
            result.add(new CorrelationModel(network, getRootNode()));
        }
        return result;
    }

    @Override
    public ICorrelationModel getCorrelatedModel(String correlationModelName) throws AWEException {
        ICorrelationModel result = null;
        for (Node network : crServ.getCorrelatedNetworks(getRootNode())) {
            if (network.getProperty(AbstractService.NAME, StringUtils.EMPTY).equals(correlationModelName)) {
                result = new CorrelationModel(network, getRootNode());
                break;
            }
        }
        return result;
    }

    @Override
    public void updateTimestamp(long timestamp) {
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

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        return null;
    }

    @Override
    public boolean isUniqueProperties(String property) {
        return false;
    }

    @Override
    public IModel getParentModel() throws AWEException {
        if (rootNode == null) {
            throw new IllegalArgumentException("currentModel type is null.");
        }
        Iterator<Node> isVirtual = dsServ.getFirstRelationTraverser(rootNode, DatasetRelationTypes.DATASET, Direction.INCOMING)
                .iterator();
        if (isVirtual.hasNext()) {
            return new ProjectModel(isVirtual.next());
        }
        return null;
    }

    @Override
    public Iterable<IDataElement> findAllElementsByTimestampPeriod(long min_timestamp, long max_timestamp) {
        return null;
    }
}
