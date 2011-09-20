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

import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class CorrelationModel extends AbstractModel implements ICorrelationModel {

    private CorrelationService crServ = NeoServiceFactory.getInstance().getCorrelationService();

    private Node network = null;
    private Node dataset = null;

    public CorrelationModel(Node network, Node dataset) {
        // validate parameters
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset is null.");
        }

        this.network = network;
        this.dataset = dataset;
        this.rootNode = crServ.createCorrelation(network, dataset);
    }

    @Override
    public Node getNetwork() {

        return network;
    }

    @Override
    public Node getDataset() {
        return dataset;
    }

    @Override
    public Iterable<Node> getSectors() {
        return crServ.getAllCorrelatedSectors(network, dataset);
    }

    @Override
    public Iterable<Node> getMeasurements() {
        return crServ.getAllCorrelatedNodes(network, dataset);
    }

    @Override
    public Iterable<Node> getCorrelatedNodes(Node sector) {
        return crServ.getCorrelatedNodes(network, sector, dataset);
    }

    @Override
    public Node getCorrelatedSector(Node measurement) {
        return crServ.getCorrelatedSector(measurement, network);
    }

}
