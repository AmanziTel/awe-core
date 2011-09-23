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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * This class manages network data.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NetworkModel extends RenderableModel implements INetworkModel {

    private static Logger LOGGER = Logger.getLogger(NetworkModel.class);

    private Map<INodeType, String> indexNames = new HashMap<INodeType, String>();

    private NewNetworkService nwServ = NeoServiceFactory.getInstance().getNewNetworkService();
    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    /**
     * Use this constructor to create a network model, based on a node, that already exists in the
     * database.
     * 
     * @param networkRoot
     */
    public NetworkModel(Node networkRoot) {
        // validate
        if (networkRoot == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if (!DatasetTypes.NETWORK.getId().equals(networkRoot.getProperty(NewAbstractService.TYPE, null))) {
            throw new IllegalArgumentException("Root node must be of type NETWORK.");
        }

        this.rootNode = networkRoot;
        this.name = rootNode.getProperty(NewAbstractService.NAME, "").toString();
    }

    /**
     * Use this constructor to create a new network structure. Be careful to set
     * <code>rootElement</code> NAME and PROJECT properties.
     * 
     * @param rootElement MUST contain property ("project",<code>Node</code> project) <i>OR</i> an
     *        underlying network node.
     */
    public NetworkModel(IDataElement rootElement) {
        // validate
        if (rootElement == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        // TODO: approve

        Node network = ((DataElement)rootElement).getNode();
        if (network == null) {
            // TODO: i think it sucks
            try {
                network = dsServ.createDataset((Node)rootElement.get("project"), rootElement.get(NewAbstractService.NAME)
                        .toString(), DatasetTypes.NETWORK);
            } catch (AWEException e) {
                LOGGER.error("Could not create network root.", e);
            }
        }
        this.rootNode = network;
        this.name = network.getProperty(NewAbstractService.NAME, "").toString();
    }

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param element
     * @return <code>DataElement</code> object, created on base of the new network node.
     */
    public IDataElement createElement(IDataElement parent, IDataElement element) {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        if (element == null) {
            throw new IllegalArgumentException("Element is null.");
        }

        INodeType type = NodeTypeManager.getType(element.get(NewAbstractService.TYPE).toString());
        Node node = null;
        try {

            // TODO:validate network structure and save it in root node

            if (type != null) {

                if (type.equals(NetworkElementNodeType.SECTOR)) {
                    Object elName = element.get(NewAbstractService.NAME);
                    Object elCI = element.get(NewNetworkService.CELL_INDEX);
                    Object elLAC = element.get(NewNetworkService.LOCATION_AREA_CODE);
                    node = nwServ.createSector(parentNode, getIndexName(type), elName == null ? null : elName.toString(),
                            elCI == null ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
                } else {
                    node = nwServ.createNetworkElement(parentNode, getIndexName(type), element.get(NewAbstractService.NAME)
                            .toString(), type);
                }
            }
            nwServ.setProperties(node, (DataElement)element);
        } catch (AWEException e) {
            LOGGER.error("Could not create network element.", e);
        }

        return node == null ? null : new DataElement(node);
    }

    // find element

    /**
     * Find a network element, based on properties set in the <code>IDataElement</code> object.
     * Don't forget to set TYPE property.
     * 
     * @param element
     * @return <code>DataElement</code> object, created on base of the found network node, or
     *         <code>null</code>.
     */
    public IDataElement findElement(IDataElement element) {
        // validate

        if (element == null) {
            throw new IllegalArgumentException("Element is null.");
        }

        INodeType type = NodeTypeManager.getType(element.get(NewAbstractService.TYPE).toString());
        Node node = null;

        // TODO:validate network structure and save it in root node

        if (type != null) {

            if (type.equals(NetworkElementNodeType.SECTOR)) {
                Object elName = element.get(NewAbstractService.NAME);
                Object elCI = element.get(NewNetworkService.CELL_INDEX);
                Object elLAC = element.get(NewNetworkService.LOCATION_AREA_CODE);
                node = nwServ.findSector(getIndexName(type), elName == null ? null : elName.toString(),
                        elCI == null ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
            } else {
                node = nwServ.findNetworkElement(getIndexName(type), element.get(NewAbstractService.NAME).toString());
            }
        }

        return node == null ? null : new DataElement(node);
    }

    // get element
    /**
     * Find or create a network element, based on properties set in the <code>IDataElement</code>
     * object. Don't forget to set TYPE property.
     * 
     * @param parent specify this parameter if you suppose that a new element will be created
     * @param element
     * @return<code>DataElement</code> object, created on base of the resulting network node, or
     *                                 <code>null</code>.
     */
    public IDataElement getElement(IDataElement parent, IDataElement element) {

        IDataElement result = findElement(element);
        if (result == null) {

            result = createElement(parent, element);
        }
        return result;
    }

    /**
     * Manage index names for current model.
     * 
     * @param type the type of node to index
     * @return the index name
     */
    protected String getIndexName(INodeType type) {
        String result = indexNames.get(type.getId());
        if (result == null) {
            result = NewAbstractService.getIndexKey(getRootNode(), type);
            indexNames.put(type, result);
        }
        return result;
    }

    @Override
    public void updateLocationBounds(double latitude, double longitude) {
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
            // TODO: maybe we should traverse over the whole network?
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(nwServ.findAllNetworkElements(getRootNode(), elementType));
    }

    @Override
    public void finishUp() {
        super.finishUp();
    }

    /**
     * @param dsServ The dsServ to set.
     */
    void setDatasetService(NewDatasetService dsServ) {
        this.dsServ = dsServ;
    }

    /**
     * @param nwServ The nwServ to set.
     */
    void setNetworkService(NewNetworkService nwServ) {
        this.nwServ = nwServ;
    }

}
