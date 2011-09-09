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
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NetworkModel extends RenderableModel implements INetworkModel {

    private static Logger LOGGER = Logger.getLogger(NetworkModel.class);

    private NewNetworkService nwServ = NeoServiceFactory.getInstance().getNewNetworkService();
    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    @Override
    public void updateBounds(double latitude, double longitude) {
        LOGGER.info("updateBounds(" + latitude + ", " + longitude + ")");
        super.updateLocationBounds(latitude, longitude);
    }

    @Override
    public double getMinLatitude() {
        return super.getMinLatitude();
    }

    @Override
    public double getMaxLatitude() {
        return super.getMaxLatitude();
    }

    @Override
    public double getMinLongitude() {
        return super.getMinLongitude();
    }

    @Override
    public double getMaxLongitude() {
        return super.getMaxLongitude();
    }

    @Override
    public CRS getCRS() {
        return null;
    }

    @Override
    public INetworkType getNetworkType() {
        return null;
    }

    @Override
    public Iterable<ICorrelationModel> getCorrelationModels() {
        LOGGER.info("getCorrelationModels()");

        Node network = getRootNode();
        List<ICorrelationModel> result = new ArrayList<ICorrelationModel>();
        for (Node dataset : NeoServiceFactory.getInstance().getNewCorrelationService().getCorrelatedDatasets(network)) {
            result.add(new CorrelationModel(network, dataset));
        }

        return result;

    }

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        LOGGER.info("getChildren(" + parent.toString() + ")");

        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        return new DataElementIterable(dsServ.getChildrenTraverser(parentNode));
    }

    /**
     * Traverses only over CHILD relationships.
     */
    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        // validate
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(nwServ.findAllNetworkElements(getRootNode(), elementType));
    }

}
