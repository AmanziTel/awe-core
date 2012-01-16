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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.impl.DistributionModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
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

    private NetworkService nwServ = NeoServiceFactory.getInstance().getNetworkService();
    private DatasetService dsServ = NeoServiceFactory.getInstance().getDatasetService();

    private List<INodeType> currentNetworkStructure = new LinkedList<INodeType>();

    /**
     * Use this constructor to create a network model, based on a node, that already exists in the
     * database.
     * 
     * @param networkRoot
     */
    public NetworkModel(Node networkRoot) throws AWEException {
        super(networkRoot, DatasetTypes.NETWORK);
        // validate
        if (networkRoot == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if (!DatasetTypes.NETWORK.getId().equals(networkRoot.getProperty(AbstractService.TYPE, null))) {
            throw new IllegalArgumentException("Root node must be of type NETWORK.");
        }

        this.rootNode = networkRoot;
        this.name = rootNode.getProperty(AbstractService.NAME, StringUtils.EMPTY).toString();
        initializeStatistics();
        initializeMultiPropertyIndexing();
        initializeNetworkStructure();
        initializeListOfUniqueProperties();
    }

    /**
     * Use this constructor to create a new network structure. Be careful to set
     * <code>rootElement</code> NAME and PROJECT properties.
     * 
     * @param project
     * @param network a <code>DataElement</code> object containing properties of a network root that
     *        should be created
     * @param name the name of the new network
     * @param crsCode a string that represents the CRS, used in the new network (e.g. "EPSG:31247")
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws AWEException
     */
    public NetworkModel(IDataElement project, IDataElement network, String name, String crsCode)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DuplicateNodeNameException, AWEException {
        super(null, DatasetTypes.NETWORK);
        // validate
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
        initializeListOfUniqueProperties();
        if ((crsCode != null) && (!crsCode.equals(StringUtils.EMPTY))) {
            updateCRS(crsCode);
        }
    }

    /**
     * Method to fill properties which should be unique in any network
     */
    private void initializeListOfUniqueProperties() {
        uniqueListOfProperties.add(NetworkService.NAME);
        // ci and lac not unique by individual but
        // ci+lac in binding is unique
        uniqueListOfProperties.add(NetworkService.CELL_INDEX);
        uniqueListOfProperties.add(NetworkService.LOCATION_AREA_CODE);

        uniqueListOfProperties.add(NetworkService.BSIC);
        uniqueListOfProperties.add(NetworkService.BCCH);
    }

    /**
     * Initializes Network Structure from Node
     */
    private void initializeNetworkStructure() {
        String[] networkStructure = (String[])rootNode.getProperty(NetworkService.NETWORK_STRUCTURE, null);

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
        LOGGER.info("Initializing multi property index...");
        addLocationIndex(NetworkElementNodeType.SITE);
    }

    @Override
    public IDataElement createElement(IDataElement parent, Map<String, Object> params) throws AWEException {
        return createElement(parent, params, DatasetRelationTypes.CHILD);
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
        INodeType nodeType = NodeTypeManager.getType(elementToDelete.get(AbstractService.TYPE).toString());
        removeProperty(nodeType, (DataElement)elementToDelete);
        nwServ.deleteOneNode(((DataElement)elementToDelete).getNode(), getRootNode(), indexMap);
        elementToDelete = null;
        finishUp();
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
                INodeType nodeType = NodeTypeManager.getType(childElement.get(AbstractService.TYPE).toString());
                childElement.get(AbstractService.NAME);
                removeProperty(nodeType, (DataElement)childElement);
                nwServ.deleteOneNode(subNode, getRootNode(), indexMap);
                finishUp();
            }
        }
    }

    @Override
    public void renameElement(IDataElement elementToRename, String newName) throws AWEException {
        String oldName = elementToRename.get(AbstractService.NAME).toString();
        elementToRename.put(AbstractService.NAME, newName);
        Node node = ((DataElement)elementToRename).getNode();
        // TODO: LN: we have a method to get Type in AbstractService
        INodeType nodeType = NodeTypeManager.getType(elementToRename.get(AbstractService.TYPE).toString());

        nwServ.removeNodeFromIndex(node, getIndex(nodeType), AbstractService.NAME, oldName);

        nwServ.setAnyProperty(node, AbstractService.NAME, newName);
        renameProperty(nodeType, AbstractService.NAME, oldName, newName);

        nwServ.addNodeToIndex(node, getIndex(nodeType), AbstractService.NAME, newName);
        finishUp();
    }

    @Override
    public void updateElement(IDataElement elementToUpdate, String propertyName, Object newValue) throws AWEException {
        Object oldValue = elementToUpdate.get(propertyName);
        elementToUpdate.put(propertyName, newValue);
        Node node = ((DataElement)elementToUpdate).getNode();
        INodeType nodeType = NodeTypeManager.getType(elementToUpdate.get(AbstractService.TYPE).toString());

        if (nwServ.isIndexedProperties(propertyName)) {
            nwServ.removeNodeFromIndex(node, getIndex(nodeType), propertyName, oldValue);
        }

        nwServ.setAnyProperty(node, propertyName, newValue);
        renameProperty(nodeType, propertyName, oldValue, newValue);

        if (nwServ.isIndexedProperties(propertyName)) {
            nwServ.addNodeToIndex(node, getIndex(nodeType), propertyName, newValue);
        }
        finishUp();
    }

    // find element

    @Override
    public IDataElement findElement(Map<String, Object> params) throws AWEException {
        // validate

        if (params == null) {
            throw new IllegalArgumentException("Element is null.");
        }

        INodeType type = NodeTypeManager.getType(params.get(AbstractService.TYPE).toString());
        Node node = null;

        // TODO:validate network structure and save it in root node

        if (type != null) {

            if (type.equals(NetworkElementNodeType.SECTOR)) {
                Object elName = params.get(AbstractService.NAME);
                Object elCI = params.get(NetworkService.CELL_INDEX);
                Object elLAC = params.get(NetworkService.LOCATION_AREA_CODE);
                node = nwServ.findSector(getIndex(type), elName == null ? null : elName.toString(),
                        elCI == null ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
            } else {
                node = nwServ.findNetworkElement(getIndex(type), params.get(AbstractService.NAME).toString());
            }
        }

        return node == null ? null : new DataElement(node);
    }

    @Override
    public IDataElement findSector(String propertyName, String propertyValue) throws AWEException {
        Node node = null;
        if (propertyName.equals(NetworkService.NAME)) {
            node = nwServ.findSector(getIndex(NetworkElementNodeType.SECTOR), propertyValue, null, null);
        } else if (propertyName.equals(NetworkService.CELL_INDEX) || propertyName.equals(NetworkService.LOCATION_AREA_CODE)) {
            int underliningIndex = propertyValue.indexOf('_');
            String ci = propertyValue.substring(0, underliningIndex);
            String lac = propertyValue.substring(underliningIndex + 1, propertyValue.length());
            node = nwServ.findSector(getIndex(NetworkElementNodeType.SECTOR), null, ci, lac);
        }
        if (node == null) {
            return null;
        }
        DataElement dataElement = new DataElement(node);
        return dataElement;
    }

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
    @Override
    public IDataElement getNetworkElement(IDataElement parent, Map<String, Object> params) throws AWEException {

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
        LOGGER.debug("updateBounds(" + latitude + ", " + longitude + ")");
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
        return currentGisModel.getCrs();
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
        for (Node dataset : NeoServiceFactory.getInstance().getCorrelationService().getCorrelatedDatasets(network)) {
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
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        // validate
        if (parent == null) {
            parent = new DataElement(getRootNode());
        }
        LOGGER.debug("getChildren(" + parent.toString() + ")");

        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        return new DataElementIterable(dsServ.getChildrenTraverser(parentNode));
    }

    @Override
    public Iterable<IDataElement> getRelatedNodes(IDataElement parent, RelationshipType reltype) {
        // validate
        if (parent == null) {
            parent = new DataElement(getRootNode());
        }
        LOGGER.info("getFirstRelatedNode(" + parent.toString() + "," + reltype.name() + ")");

        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        return new DataElementIterable(dsServ.getFirstRelationTraverser(parentNode, reltype, Direction.OUTGOING));
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
    void setDatasetService(DatasetService dsServ) {
        this.dsServ = dsServ;
    }

    /**
     * @param nwServ The nwServ to set.
     */
    void setNetworkService(NetworkService nwServ) {
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
        Iterable<Node> nodes = nwServ.getAllSelectionModelsOfNetwork(rootNode);
        List<ISelectionModel> models = new ArrayList<ISelectionModel>();
        for (Node node : nodes) {
            models.add(new SelectionModel(node));
        }
        return models;
    }

    @Override
    public Iterable<ISelectionModel> getAllSelectionModelsOfSector(IDataElement element) throws AWEException {
        Iterable<Node> nodes = nwServ.getAllSelectionModelsOfSector(((DataElement)element).getNode());
        List<ISelectionModel> models = new ArrayList<ISelectionModel>();
        for (Node node : nodes) {
            models.add(new SelectionModel(node));
        }
        return models;
    }

    @Override
    public void replaceRelationship(IDataElement newParentElement, IDataElement currentNode) throws AWEException {
        Node curentNode;
        Node newParentNode;
        curentNode = ((DataElement)currentNode).getNode();
        newParentNode = ((DataElement)newParentElement).getNode();
        nwServ.replaceRelationship(newParentNode, curentNode, DatasetRelationTypes.CHILD, Direction.INCOMING);
    }

    @Override
    public IDataElement completeProperties(IDataElement existedElement, Map<String, Object> newPropertySet, boolean isReplaceExisted)
            throws AWEException {
        Node existedNode;
        existedNode = ((DataElement)existedElement).getNode();
        INodeType nodeType = NodeTypeManager.getType(existedElement.get(AbstractService.TYPE).toString());
        nwServ.completeProperties(existedNode, newPropertySet, isReplaceExisted, getIndex(nodeType));
        nwServ.setProperties(existedNode, newPropertySet);
        indexProperty(nodeType, newPropertySet);
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
         * if current structure not contains parent type and not contains child type, then add
         * parent and child in end of structure
         */
        if (!currentNetworkStructure.contains(parentType) && !currentNetworkStructure.contains(childType)) {
            currentNetworkStructure.add(parentType);
            currentNetworkStructure.add(childType);
        }
        /**
         * if current structure not contains parent type and contains child type, then add parent at
         * index indexOf(child)
         */
        else if (!currentNetworkStructure.contains(parentType) && currentNetworkStructure.contains(childType)) {
            int indexOfChild = currentNetworkStructure.indexOf(childType);
            currentNetworkStructure.add(indexOfChild, parentType);
        }
        /**
         * if current structure contains parent type and not contains child type, then add child at
         * index indexOf(parent)+1
         */
        else if (currentNetworkStructure.contains(parentType) && !currentNetworkStructure.contains(childType)) {
            int indexOfParent = currentNetworkStructure.indexOf(parentType);
            currentNetworkStructure.add(indexOfParent + 1, childType);
        }
    }

    @Override
    public List<INodeType> getNetworkStructure() {
        return currentNetworkStructure;
    }

    public void setCurrentNetworkStructure(List<INodeType> currentNetworkStructure) {
        this.currentNetworkStructure = currentNetworkStructure;
    }

    @Override
    public IDataElement createElement(IDataElement parent, Map<String, Object> element, RelationshipType reltype)
            throws AWEException {
        if (parent == null) {
            parent = new DataElement(rootNode);
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        if (element == null) {
            throw new IllegalArgumentException("Parameters map is null.");
        }

        INodeType parentType = NodeTypeManager.getType(parent.get(AbstractService.TYPE).toString());
        INodeType type = NodeTypeManager.getType(element.get(AbstractService.TYPE).toString());
        changeNetworkStructure(parentType, type);

        Node node = null;

        // TODO:validate network structure and save it in root node

        if (type != null) {

            if (type.equals(NetworkElementNodeType.SECTOR)) {
                Integer bsic = nwServ.getBsicProperty(element);
                Integer bcch = (Integer)element.get(NetworkService.BCCH);
                Object elName = element.get(AbstractService.NAME);
                Object elCI = element.get(NetworkService.CELL_INDEX);
                Object elLAC = element.get(NetworkService.LOCATION_AREA_CODE);

                if (bsic == 0) {
                    node = nwServ.createSector(parentNode, getIndex(type), elName == null ? null : elName.toString(), elCI == null
                            ? null : elCI.toString(), elLAC == null ? null : elLAC.toString());
                } else {
                    node = nwServ.createSector(parentNode, getIndex(type), elName == null ? null : elName.toString(), elCI == null
                            ? null : elCI.toString(), elLAC == null ? null : elLAC.toString(), bsic);
                }
                if (bcch != null) {
                    nwServ.addNodeToIndex(node, getIndex(type), NetworkService.BCCH, bcch);
                }
            } else {
                node = nwServ.createNetworkElement(parentNode, getIndex(type), element.get(AbstractService.NAME).toString(), type,
                        reltype);
            }
        }
        nwServ.setProperties(node, element);
        indexProperty(type, element);
        indexNode(node);

        updateLocationBounds(element);

        return node == null ? null : new DataElement(node);
    }

    /**
     * @param element
     */
    private void updateLocationBounds(Map<String, Object> element) {
        Double lat = (Double)element.get(LATITUDE);
        Double lon = (Double)element.get(LONGITUDE);
        if (lat == null || lon == null) {
            return;
        }
        updateLocationBounds(lat, lon);
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
    public IDistributionModel getDistributionModel(IDistribution< ? > distributionType) throws AWEException {
        return new DistributionModel(this, distributionType);
    }

    public Iterable<IDataElement> getElements(Envelope bounds_transformed) throws AWEException {

        return new DataElementIterable(getNodesInBounds(NetworkElementNodeType.SITE, bounds_transformed.getMinY(),
                bounds_transformed.getMinX(), bounds_transformed.getMaxY(), bounds_transformed.getMaxX()));
        // // currently return all elements
        // return new DataElementIterable(nwServ.findAllNetworkElements(rootNode,
        // NetworkElementNodeType.SITE));
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return super.getBounds();
    }

    @Override
    public Coordinate getCoordinate(IDataElement element) {
        NetworkElementNodeType type = (NetworkElementNodeType)NodeTypeManager.getType(element.get(AbstractService.TYPE).toString());
        switch (type) {
        case SITE:
            return new Coordinate((Double)element.get(LONGITUDE), (Double)element.get(LATITUDE));

        case SECTOR:
            IDataElement site = getParentElement(element);
            return new Coordinate((Double)site.get(LONGITUDE), (Double)site.get(LATITUDE));
        default:
            return null;
        }
    }

    /**
     * return closest to servSector element
     */
    @Override
    public IDataElement getClosestSectorByBsicBcch(IDataElement servSector, Integer bsic, Integer bcch) throws DatabaseException {
        Set<IDataElement> nodes = findSectorsByBsicBcch(bsic, bcch);
        return getClosestElement(servSector, nodes, 30000);
    }

    @Override
    public IDataElement getClosestElement(IDataElement servSector, Set<IDataElement> candidates, int maxDistance) {
        Coordinate c = getCoordinate(servSector);
        CoordinateReferenceSystem crs = getCRS();
        if (c == null || crs == null) {
            return null;
        }
        Double dist = null;
        IDataElement candidateNode = null;
        for (IDataElement candidate : candidates) {
            if (servSector.equals(candidate)) {
                continue;
            }
            Coordinate c1 = getCoordinate(candidate);
            if (c1 == null) {
                continue;
            }
            double distance;
            try {
                distance = JTS.orthodromicDistance(c, c1, crs);
            } catch (Exception e) {
                e.printStackTrace();
                distance = Math.sqrt(Math.pow(c.x - c1.x, 2) + Math.pow(c.y - c1.y, 2));

            }
            if (distance > maxDistance) {
                continue;
            }
            if (candidateNode == null || distance < dist) {
                dist = distance;
                candidateNode = candidate;
            }
        }
        return candidateNode;
    }

    /**
     * find sectors by required bsic and bcch value
     * 
     * @param bsic
     * @param bcch
     * @return
     * @throws DatabaseException
     */
    private Set<IDataElement> findSectorsByBsicBcch(Integer bsic, Integer bcch) throws DatabaseException {
        Set<IDataElement> result = new LinkedHashSet<IDataElement>();
        Iterator<Node> findedNodes = nwServ.findByIndex(getIndex(NetworkElementNodeType.SECTOR), NetworkService.BSIC, bsic);
        while (findedNodes.hasNext()) {
            Node node = findedNodes.next();
            Integer bcchno = (Integer)node.getProperty(NetworkService.BCCH, null);
            if (ObjectUtils.equals(bcch, bcchno)) {
                result.add(new DataElement(node));
            }
        }
        return result;
    }

    @Override
    public Set<IDataElement> findElementByPropertyValue(INodeType type, String propertyName, Object propertyValue)
            throws DatabaseException {
        if (type == null) {
            throw new IllegalArgumentException("type cann't be null");
        }
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cann't be null");
        }
        if (propertyValue == null) {
            throw new IllegalArgumentException("propertyValue cann't be null");
        }
        Set<IDataElement> result = new HashSet<IDataElement>();
        Iterator<Node> findedNodes = nwServ.findByIndex(getIndex(type), propertyName, propertyValue);
        while (findedNodes.hasNext()) {
            Node node = findedNodes.next();
            result.add(new DataElement(node));
        }
        return result;
    }

    @Override
    public Iterable<INodeToNodeRelationsModel> getNodeToNodeModels() throws AWEException {
        LOGGER.info("getNodeToNodeModels()");

        return getNodeToNodeModels(null);
    }

    @Override
    public void setCRS(CoordinateReferenceSystem crs) {
        super.setCRS(crs);
    }

    @Override
    public CoordinateReferenceSystem updateCRS(String crsCode) {
        return super.updateCRS(crsCode);
    }

    @Override
    public boolean isUniqueProperties(String property) {
        return super.isUniqueProperties(property);
    }

    @Override
    public IModel getParentModel() throws AWEException {
        return getProject();
    }
}
