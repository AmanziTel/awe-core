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
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataModel.DataElementIterable;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Correlation model describes relationships between network elements and dataset nodes.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class CorrelationModel extends AbstractModel implements ICorrelationModel {

    private CorrelationService crServ = NeoServiceFactory.getInstance().getNewCorrelationService();

    private Node network = null;
    private Node dataset = null;

    /**
     * Creates correlation model using network and dataset nodes.
     * 
     * @param network a network root node
     * @param dataset a dataset root node
     */
    CorrelationModel(Node network, Node dataset) {
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

    /**
     * Creates correlation model using <code>DataElement</code>s, containing network and dataset
     * nodes.
     * 
     * @param networkElement <code>DataElement</code>, containing a network root node
     * @param datasetElement <code>DataElement</code>, containing a dataset root node
     */
    public CorrelationModel(IDataElement networkElement, IDataElement datasetElement) {
        // validate parameters
        if (networkElement == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        Node networkNode = ((DataElement)networkElement).getNode();
        if (networkNode == null) {
            throw new IllegalArgumentException("Network node is null.");
        }
        if (datasetElement == null) {
            throw new IllegalArgumentException("Dataset is null.");
        }
        Node datasetNode = ((DataElement)datasetElement).getNode();
        if (datasetNode == null) {
            throw new IllegalArgumentException("Dataset node is null.");
        }

        this.network = networkNode;
        this.dataset = datasetNode;
        this.rootNode = crServ.createCorrelation(networkNode, datasetNode);
    }

    @Override
    public IDataElement getNetwork() {

        return new DataElement(network);
    }

    @Override
    public IDataElement getDataset() {
        return new DataElement(dataset);
    }

    @Override
    public Iterable<IDataElement> getSectors() {
        return new DataElementIterable(crServ.getAllCorrelatedSectors(network, dataset));
    }

    @Override
    public Iterable<IDataElement> getMeasurements() {
        return new DataElementIterable(crServ.getAllCorrelatedNodes(network, dataset));
    }

    @Override
    public Iterable<IDataElement> getCorrelatedNodes(Node sector) {
        return new DataElementIterable(crServ.getCorrelatedNodes(network, sector, dataset));
    }

    @Override
    public IDataElement getCorrelatedSector(Node measurement) {
        return new DataElement(crServ.getCorrelatedSector(measurement, network));
    }

}
