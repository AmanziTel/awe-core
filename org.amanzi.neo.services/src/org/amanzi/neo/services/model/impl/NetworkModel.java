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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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

    private Map<INodeType, Index<Node>> indexMap = new HashMap<INodeType, Index<Node>>();

    private NewNetworkService nwServ = NeoServiceFactory.getInstance().getNewNetworkService();
    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    private List<INodeType> currentNetworkStructure = new LinkedList<INodeType>();
    
    /**
     * Use this constructor to create a network model, based on a node, that already exists in the
     * database.
     * 
     * @param networkRoot
     */
    public NetworkModel(Node networkRoot) throws AWEException {
        // validate
        if (networkRoot == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if (!DatasetTypes.NETWORK.getId().equals(networkRoot.getProperty(NewAbstractService.TYPE, null))) {
            throw new IllegalArgumentException("Root node must be of type NETWORK.");
        }

        this.rootNode = networkRoot;
        this.name = rootNode.getProperty(NewAbstractService.NAME, StringUtils.EMPTY).toString();
        initializeStatistics();
        initializeMultiPropertyIndexing();
        initializeNetworkStructure();
    }

    /**
     * Use this constructor to create a new network structure. Be careful to set
     * <code>rootElement</code> NAME and PROJECT properties.
     * 
     * @param network MUST contain property ("project",<code>Node</code> project) <i>OR</i> an
     *        underlying network node.
     * @throws AWEException
     * @throws DuplicateNodeNameException
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     */
    public NetworkModel(IDataElement project, IDataElement network, String name) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, AWEException {
        // validate
        if (project == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node projectNode = ((DataElement)project).getNode();
        if (projectNode == null) {
            throw new IllegalArgumentException("Project node is null.");
        }
        if (network == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty.");
        }
        Node networkNode = ((DataElement)network).getNode();
        if (networkNode == null) {
            networkNode = dsServ.createDataset(projectNode, name, DatasetTypes.NETWORK);
        }
        this.rootNode = networkNode;
        this.name = name;
        initializeStatistics();
        initializeMultiPropertyIndexing();
        initializeNetworkStructure();
    }
    
    /**
     * Initializes Network Structure from Node
     */
    private void initializeNetworkStructure() {
        String[] networkStructure = (String[])rootNode.getProperty(NewNetworkService.NETWORK_STRUCTURE, null);
        
        currentNetworkStructure = new LinkedList<INodeType>();
        if (networkStructure != null) {
            for (String nodeType : networkStructure) {
                currentNetworkStructure.add(NodeTypeManager.getType(nodeType));
            }
        }
    }

    /**
     * Initializes location index for sector nodes.
     */
    private void initializeMultiPropertyIndexing() throws AWEException {
        LOGGER.info("Initializing multi proerty index...");
        addLocationIndex(NetworkElementNodeType.SECTOR);
    }

    @Override
    public IDataElement createElement(IDataElement parent, Map<String, Object> params) throws AWEException {
        return createElement(parent, params, NetworkRelationshipTypes.CHILD);
    }

    @Override
    public void deleteElement(IDataElement elementToDelete) throws AWEException {
        if (elementToDelete == null) {
            throw new IllegalArgumentException("DataElement to delete is null.");
        }
        Node node = ((DataElement)elementToDelete).getNode();
        if (node == null) {
            throw new IllegalArgumentException("Node assotiated with DataElement is null.");
        }
        deleteSubElements(elementToDelete);
        INodeType nodeType = NodeTypeManager.getType(elementToDelete.get(INeoConstants.PROPERTY_TYPE_NAME).toString());
        removeProperty(nodeType, (DataElement)elementToDelete);
        nwServ.deleteOneNode(((DataElement)elementToDelete).getNode(), getRootNode(), indexMap);
        elementToDelete = null;

    }

    /**
     * Recursive deleting all sub-nodes of this node
     * 
     * @param child Node to delete
     * @throws DatabaseException
     */
    private void deleteSubElements(IDataElement elementToDelete) throws AWEException {
        for (IDataElement childElement : getChildren(elementToDelete)) {
            Node subNode = ((DataElement)childElement).getNode();
            if (subNode != null) {
                deleteSubElements(childElement);
                INodeType nodeType = NodeTypeManager.getType(childElement.get(INeoConstants.PROPERTY_TYPE_NAME).toString());
                removeProperty(nodeType, (DataElement)childElement);
                nwServ.deleteOneNode(subNode, getRootNode(), indexMap);
            }
        }
    }

    @Override
    public void renameElement(IDataElement elementToRename, String newName) throws AWEException {
        elementToRename.put(INeoConstants.PROPERTY_NAME_NAME, newName);
        Node node = ((DataElement)elementToRename).getNode();
        nwServ.setNameProperty(node, newName);
    }

    // find element

    @Override
    public IDataElement findElement(Map<String, Object> params) throws AWEException {
        // validate

        if (params == null) {
            throw new IllegalArgumentException("Element is null.");
        }

        INodeType type = NodeTypeManager.getType(params.get(NewAbstractService.TYPE).toString());
        Node node = null;

        // TODO:validate network structure and save it in root node

        if (type != null) {

            if (type.equals(NetworkElementNodeType.SECTOR)) {
                Object elName = params.get(NewAbstractService.NAME);
                Object elCI = params.get(NewNetworkService.CELL_INDEX);
                Object elLAC = params.get(NewNetworkService.LOCATION_AREA_CODE);
                node = nwServ.findSector(getIndex(type), elName == null ? null : elName.toString(),
                        elCI == null ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
            } else {
                node = nwServ.findNetworkElement(getIndex(type), params.get(NewAbstractService.NAME).toString());
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
     * @param params
     * @return<code>DataElement</code> object, created on base of the resulting network node, or
     *                                 <code>null</code>.
     * @throws AWEException
     */
    public IDataElement getElement(IDataElement parent, Map<String, Object> params) throws AWEException {

        IDataElement result = findElement(params);
        if (result == null) {

            result = createElement(parent, params);
        }
        return result;
    }

    /**
     * Manage index names for current model.
     * 
     * @param type the type of node to index
     * @return the index name
     * @throws DatabaseException
     */
    protected Index<Node> getIndex(INodeType type) throws DatabaseException {
        Index<Node> result = indexMap.get(type.getId());
        if (result == null) {
            result = dsServ.getIndex(getRootNode(), type);
            if (result != null) {
                indexMap.put(type, result);
            }
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
    public CoordinateReferenceSystem getCRS() {
        return this.crs;
    }

    @Override
    public INetworkType getNetworkType() {
        return null;
    }

    @Override
    public Iterable<ICorrelationModel> getCorrelationModels() throws AWEException {
        LOGGER.info("getCorrelationModels()");

        Node network = getRootNode();
        List<ICorrelationModel> result = new ArrayList<ICorrelationModel>();
        for (Node dataset : NeoServiceFactory.getInstance().getNewCorrelationService().getCorrelatedDatasets(network)) {
            result.add(new CorrelationModel(network, dataset));
        }

        return result;

    }

    @Override
    public Iterable<INodeToNodeRelationsModel> getNodeToNodeModels(N2NRelTypes type) throws AWEException {
        LOGGER.info("getNodeToNodeModels(N2NRelTypes type)");

        Node network = getRootNode();
        List<INodeToNodeRelationsModel> result = new ArrayList<INodeToNodeRelationsModel>();
        for (Node n2nRoot : nwServ.getNodeToNodeRoots(network)) {
            N2NRelTypes relType = N2NRelTypes.valueOf(n2nRoot.getProperty(NodeToNodeRelationshipModel.RELATION_TYPE).toString());
            if (type == null || relType.equals(type)) {
                result.add(new NodeToNodeRelationshipModel(n2nRoot));
            }
        }
        return result;
    }

    @Override
    public Iterable<INodeToNodeRelationsModel> getNodeToNodeModels() throws AWEException {
        LOGGER.info("getNodeToNodeModels()");

        return getNodeToNodeModels(null);
    }
    
    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        // validate
        if (parent == null) {
            parent = new DataElement(getRootNode());
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

    @Override
    public ISelectionModel findSelectionModel(String name) throws AWEException {
        Node rootSelectionNode = nwServ.findSelectionList(rootNode, name);
        if (rootSelectionNode != null) {
            return new SelectionModel(rootSelectionNode);
        }
        return null;
    }

    @Override
    public ISelectionModel createSelectionModel(String name) throws AWEException {
        LOGGER.info("New SelectionModel <" + name + "> created for Network <" + this.name + ">");
        return new SelectionModel(getRootNode(), name);
    }

    @Override
    public ISelectionModel getSelectionModel(String name) throws AWEException {
        LOGGER.debug("Trying to get Selection model with name <" + name + ">");

        ISelectionModel result = findSelectionModel(name);

        if (result == null) {
            result = createSelectionModel(name);
        }

        return result;
    }

    @Override
    public Iterable<ISelectionModel> getAllSelectionModels() throws AWEException {
        return null;
    }

    @Override
    public void replaceRelationship(IDataElement newParentElement, IDataElement currentNode) throws AWEException {
        Node curentNode;
        Node newParentNode;
        curentNode = ((DataElement)currentNode).getNode();
        newParentNode = ((DataElement)newParentElement).getNode();
        nwServ.replaceRelationship(newParentNode, curentNode, NetworkRelationshipTypes.CHILD, Direction.INCOMING);
    }

    @Override
    public IDataElement completeProperties(IDataElement existedElement, Map<String, Object> newPropertySet, boolean isReplaceExisted)
            throws DatabaseException {
        Node existedNode;
        existedNode = ((DataElement)existedElement).getNode();
        nwServ.completeProperties(existedNode, new DataElement(newPropertySet), isReplaceExisted);
        return new DataElement(existedNode);
    }

    @Override
    public void createRelationship(IDataElement parent, IDataElement child, RelationshipType rel) throws AWEException {
        Node parentNode;
        Node childNode;
        parentNode = ((DataElement)parent).getNode();
        childNode = ((DataElement)child).getNode();
        nwServ.createRelationship(parentNode, childNode, rel);
    }
    
    /**
     * Method to dynamically change of network structure
     *
     * @param parentType Parent type in string format
     * @param childType Child type in string format
     */
    private void changeNetworkStructure(INodeType parentType, INodeType childType) {
        /**
         * if current structure not contains parent type and not contains child type,
         * then add parent and child in end of structure
         */
        if (!currentNetworkStructure.contains(parentType) &&
                !currentNetworkStructure.contains(childType)) {
            currentNetworkStructure.add(parentType);
            currentNetworkStructure.add(childType);
        }
        /**
         * if current structure not contains parent type and contains child type,
         * then add parent at index indexOf(child)
         */
        else if (!currentNetworkStructure.contains(parentType) &&
                currentNetworkStructure.contains(childType)) {
            int indexOfChild = currentNetworkStructure.indexOf(childType);
            currentNetworkStructure.add(indexOfChild, parentType);
        }
        /**
         * if current structure contains parent type and not contains child type,
         * then add child at index indexOf(parent)+1
         */
        else if (currentNetworkStructure.contains(parentType) &&
                !currentNetworkStructure.contains(childType)) {
            int indexOfParent = currentNetworkStructure.indexOf(parentType);
            currentNetworkStructure.add(indexOfParent + 1, childType);
        }
    }
    
    @Override
    public List<INodeType> getNetworkStructure() {
        return currentNetworkStructure;
    }

    @Override
    public IDataElement createElement(IDataElement parent, Map<String, Object> element, RelationshipType reltype)
            throws AWEException {
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        if (element == null) {
            throw new IllegalArgumentException("Parameters map is null.");
        }
        
        INodeType parentType = NodeTypeManager.getType(parent.get(NewAbstractService.TYPE).toString());
        INodeType type = NodeTypeManager.getType(element.get(NewAbstractService.TYPE).toString());
        changeNetworkStructure(parentType, type);
        
        Node node = null;

        // TODO:validate network structure and save it in root node

        if (type != null) {

            if (type.equals(NetworkElementNodeType.SECTOR)) {
                Object elName = element.get(NewAbstractService.NAME);
                Object elCI = element.get(NewNetworkService.CELL_INDEX);
                Object elLAC = element.get(NewNetworkService.LOCATION_AREA_CODE);
                node = nwServ.createSector(parentNode, getIndex(type), elName == null ? null : elName.toString(), elCI == null
                        ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
            } else {
                node = nwServ.createNetworkElement(parentNode, getIndex(type), element.get(NewAbstractService.NAME).toString(),
                        type, reltype);
            }
        }
        nwServ.setProperties(node, element);
        indexProperty(type, element);
        indexNode(node);

        return node == null ? null : new DataElement(node);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void finishUp() throws AWEException {
        nwServ.setNetworkStructure(rootNode, currentNetworkStructure);
        super.finishUp();
    }

    @Override
    public ICorrelationModel getCorrelationModel(IDataElement datasetElement) throws DatabaseException {
        return new CorrelationModel(new DataElement(this.rootNode), datasetElement);
    }

    @Override
    public INodeToNodeRelationsModel createNodeToNodeMmodel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException {
        return new NodeToNodeRelationshipModel(new DataElement(this.rootNode), relType, name, nodeType);
    }

    @Override
    public INodeToNodeRelationsModel findNodeToNodeModel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException {
        Node n2nRoot = dsServ.findNode(this.rootNode, relType, name, nodeType);
        return n2nRoot == null ? null : new NodeToNodeRelationshipModel(n2nRoot);
    }

    @Override
    public INodeToNodeRelationsModel getNodeToNodeModel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException {
        INodeToNodeRelationsModel result = findNodeToNodeModel(relType, name, nodeType);
        if (result == null) {
            result = createNodeToNodeMmodel(relType, name, nodeType);

        }
        return result;
    }

    @Override
    public IDistributionModel getDistributionModel(IDistribution<?> distributionType) {
        return null;
    }

    public Iterable<IDataElement> getElements(Envelope bounds_transformed) {
        return null;
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return super.getBounds();
    }

    @Override
    public Coordinate getCoordinate(IDataElement element) {
        NetworkElementNodeType type = (NetworkElementNodeType)NodeTypeManager.getType(element.get(NewAbstractService.TYPE)
                .toString());
        switch (type) {
        case SITE:
            return new Coordinate((Long)element.get(LATITUDE), (Long)element.get(LONGITUDE));

        case SECTOR:
            IDataElement site = getParentElement(element);
            return new Coordinate((Long)site.get(LATITUDE), (Long)site.get(LONGITUDE));
        default:
            return null;
        }
    }
    
}
