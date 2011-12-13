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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.impl.DistributionModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * This class manages drive data.
 * </p>
 * 
 * @author Ana Gr.
 * @since 1.0.0
 */
public class DriveModel extends RenderableModel implements IDriveModel {

    private static Logger LOGGER = Logger.getLogger(DriveModel.class);

    // private members
    private Index<Node> files;
    private int count = 0;
    private INodeType primaryType = DriveNodeTypes.M;
    private IDriveType driveType;

    private DatasetService dsServ = NeoServiceFactory.getInstance().getDatasetService();
    private CorrelationService crServ = NeoServiceFactory.getInstance().getCorrelationService();

    /**
     * <p>
     * This enum describes node types that are present in drive model.
     * </p>
     * 
     * @author Ana Gr.
     * @since 1.0.0
     */
    public enum DriveNodeTypes implements INodeType {
        FILE, M, MP, M_AGGR, MM, MS, SELECTED_PROPERTIES;

        static {
            NodeTypeManager.registerNodeType(DriveNodeTypes.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
        }

        public static DriveNodeTypes findById(String id) {
            for (DriveNodeTypes driveNodeType : values()) {
                if (driveNodeType.getId().equals(id)) {
                    return driveNodeType;
                }
            }

            return null;
        }

    }

    /**
     * <p>
     * This enum describes relationship types that are present in drive model.
     * </p>
     * 
     * @author Ana Gr.
     * @since 1.0.0
     */
    public enum DriveRelationshipTypes implements RelationshipType {
        VIRTUAL_DATASET, LOCATION, CALL_M;
    }

    /**
     * Use this constructor to create a drive model, based on a node, that already exists in the
     * database.
     * 
     * @param driveRoot
     */
    public DriveModel(Node driveRoot) throws AWEException {
        super(driveRoot, DatasetTypes.DRIVE);
        // validate
        if (driveRoot == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if (!DatasetTypes.DRIVE.getId().equals(driveRoot.getProperty(AbstractService.TYPE, null))) {
            throw new IllegalArgumentException("Root node must be of type NETWORK.");
        }

        this.rootNode = driveRoot;
        this.name = rootNode.getProperty(AbstractService.NAME, StringUtils.EMPTY).toString();
        this.primaryType = DriveNodeTypes.findById(rootNode.getProperty(DatasetService.PRIMARY_TYPE).toString());
        initializeStatistics();
        initializeMultiPropertyIndexing();
    }

    /**
     * Constructor. Pass only rootNode, if you have one, <i>OR</i> all the other parameters.
     * 
     * @param parent a project node
     * @param rootNode a drive node
     * @param name the name of root node of the new drive model
     * @param type the type of root node of the new drive model
     * @param primaryType the primary type of root node of the new drive model
     * @throws AWEException if parameters are null or empty or some errors occur in database during
     *         creation of nodes
     */
    public DriveModel(Node parent, Node rootNode, String name, IDriveType type, INodeType primaryType) throws AWEException {
        super(rootNode, DatasetTypes.DRIVE);
        // if root node is null, get one by name
        if (rootNode != null) {
            dsServ = NeoServiceFactory.getInstance().getDatasetService();

            this.rootNode = rootNode;
            this.name = (String)rootNode.getProperty(AbstractService.NAME, null);
            this.driveType = DriveTypes.valueOf(rootNode.getProperty(DatasetService.DRIVE_TYPE, StringUtils.EMPTY).toString());
        } else {
            // validate params
            if (parent == null) {
                throw new IllegalArgumentException("Parent is null.");
            }

            this.rootNode = dsServ.getDataset(parent, name, DatasetTypes.DRIVE, type, primaryType);
            this.name = name;
            this.driveType = type;
        }
        this.primaryType = primaryType;
        initializeStatistics();
        initializeMultiPropertyIndexing();
    }

    /**
     * This constructor additionally sets the primary type of current DriveModel measurements. By
     * default it is <code>{@link DriveNodeTypes#M}</code>. See also
     * {@link DriveModel#DriveModel(Node, Node, String, IDriveType)}.
     * 
     * @param parent
     * @param rootNode
     * @param name
     * @param type
     * @param primaryType
     * @throws AWEException
     */
    public DriveModel(Node parent, Node rootNode, String name, IDriveType type) throws AWEException {
        this(parent, rootNode, name, type, DriveNodeTypes.M);
    }

    /**
     * Initializes location index for sector nodes.
     */
    private void initializeMultiPropertyIndexing() throws AWEException {
        LOGGER.info("Initializing multi proerty index...");
        addLocationIndex(DriveNodeTypes.MP);
        addTimestampIndex(primaryType);
    }

    @Override
    public DriveModel addVirtualDataset(String name, IDriveType driveType) throws AWEException {
        LOGGER.debug("start addVirtualDataset(String name, IDriveType driveType)");

        // validate params
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalNodeDataException("Name is null or empty.");
        }
        if (driveType == null) {
            throw new IllegalArgumentException("Drive type is null");
        }
        if (findVirtualDataset(name) != null) {
            throw new DuplicateNodeNameException(name, DatasetTypes.DRIVE);
        }

        Node virtual = dsServ.createNode(rootNode, DriveRelationshipTypes.VIRTUAL_DATASET, DatasetTypes.DRIVE);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.NAME, name);
        params.put(DRIVE_TYPE, driveType.name());
        dsServ.setProperties(virtual, params);

        DriveModel result = new DriveModel(null, virtual, name, null);
        return result;
    }

    @Override
    public IDriveModel findVirtualDataset(String name) {
        LOGGER.debug("start findVirtualDataset(String name)");

        IDriveModel result = null;
        for (IDriveModel dm : getVirtualDatasets()) {
            if (dm.getName().equals(name)) {
                result = dm;
                break;
            }
        }
        return result;
    }

    @Override
    public IDriveModel getVirtualDataset(String name, IDriveType driveType) throws AWEException {
        LOGGER.debug("start getVirtualDataset(String name, IDriveType driveType)");

        IDriveModel result = findVirtualDataset(name);
        if (result == null) {
            result = addVirtualDataset(name, driveType);
        }
        return result;
    }

    @Override
    public Iterable<IDriveModel> getVirtualDatasets() {
        LOGGER.debug("start getVirtualDatasets()");

        List<IDriveModel> result = new ArrayList<IDriveModel>();
        for (Node node : dsServ.getVirtualDatasets(rootNode)) {
            try {
                result.add(new DriveModel(null, node, null, null));
            } catch (AWEException e) {
                LOGGER.error("Could not create drive model on node " + node.getProperty(AbstractService.NAME, null), e);
            }
        }
        return result;
    }

    @Override
    public IDataElement addFile(File file) throws AWEException {
        LOGGER.debug("start addFile(File file)");

        // file nodes are added as c-n-n
        // validate params
        if (file == null) {
            throw new IllegalArgumentException("File is null.");
        }
        if (findFile(file.getName()) != null) {
            throw new DuplicateNodeNameException(file.getName(), DriveNodeTypes.FILE);
        }

        Node fileNode = dsServ.addChild(rootNode, dsServ.createNode(DriveNodeTypes.FILE), null);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.NAME, file.getName());
        params.put(PATH, file.getPath());
        dsServ.setProperties(fileNode, params);
        if (files == null) {
            files = dsServ.addNodeToIndex(fileNode, AbstractService.getIndexKey(rootNode, DriveNodeTypes.FILE),
                    AbstractService.NAME, file.getName());
        } else {
            dsServ.addNodeToIndex(fileNode, files, AbstractService.NAME, file.getName());
        }
        return new DataElement(fileNode);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params) throws AWEException {
        return addMeasurement(filename, params, primaryType, true);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params, boolean isNeedToCreateLocation)
            throws AWEException {
        return addMeasurement(filename, params, primaryType, isNeedToCreateLocation);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params) throws AWEException {
        return addMeasurement(file, params, primaryType);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException {

        // validate parameters
        if (file == null) {
            throw new IllegalArgumentException("File element is null.");
        }
        Node fileNode = ((DataElement)file).getNode();
        if (fileNode == null) {
            throw new IllegalArgumentException("File node is null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameter map is null.");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        Node m = dsServ.createNode(nodeType);
        dsServ.addChild(fileNode, m, null);
        Double lat = (Double)params.get(LATITUDE);
        Double lon = (Double)params.get(LONGITUDE);
        Long tst = (Long)params.get(TIMESTAMP);
        indexNode(m);

        if ((lat != null) && (lat != 0) && (lon != null) && (lon != 0) && isNeedToCreateLocation) {
            createLocationNode(new DataElement(m), lat, lon);
            params.remove(LATITUDE);
            params.remove(LONGITUDE);
        }
        if ((tst != null) && (tst != 0)) {
            updateTimestamp(tst);
        }
        params.put(AbstractService.DATASET_ID, this.name);
        dsServ.setProperties(m, params);
        indexProperty(primaryType, params); // TODO: ??????????

        count++;
        Map<String, Object> prop = new HashMap<String, Object>();
        prop.put(PRIMARY_TYPE, primaryType.getId());// TODO: ?????????????
        prop.put(COUNT, count);
        dsServ.setProperties(rootNode, prop);
        indexProperty(primaryType, prop); // TODO: ???????????

        return new DataElement(m);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType) throws AWEException {
        return addMeasurement(file, params, nodeType, true);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException {
        LOGGER.debug("start addMeasurement(String filename, Map<String, Object> params)");

        // measurements are added as c-n-n o file nodes
        // lat, lon properties are stored in a location node

        // validate params
        if ((filename == null) || (filename.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters map is null.");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        Node fileNode = ((DataElement)findFile(new File(filename).getName())).getNode();
        if (fileNode == null) {
            throw new IllegalArgumentException("File node " + filename + " not found.");
        }

        return addMeasurement(new DataElement(fileNode), params, nodeType, isNeedToCreateLocation);
    }

    @Override
    public void linkNode(IDataElement parent, Iterable<IDataElement> source, RelationshipType rel) throws DatabaseException {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        // TODO: do we really need source dataset here?
        // if (sourceDataset == null) {
        // throw new IllegalArgumentException("Source dataset is null.");
        // }
        if (source == null) {
            throw new IllegalArgumentException("List of nodes is null.");
        }

        for (IDataElement element : source) {
            Node node = ((DataElement)element).getNode();
            if (node == null) {
                throw new IllegalArgumentException("Source data element must contain nodes.");
            }
            dsServ.createRelationship(parentNode, node, rel);
        }

    }

    @Override
    public IDataElement createLocationNode(IDataElement parent, double lat, double lon) throws DatabaseException {
        LOGGER.debug("start createLocationNode(Node measurement, long lat, long lon)");
        // validate params
        if (parent == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }

        Node parentElement = ((DataElement)parent).getNode();
        if (parentElement == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }
        Node location = dsServ.createNode(parentElement, DriveRelationshipTypes.LOCATION, DriveNodeTypes.MP);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LATITUDE, lat);
        params.put(LONGITUDE, lon);
        dsServ.setProperties(location, params);
        updateLocationBounds(lat, lon);
        indexNode(location);
        return new DataElement(location);
    }

    @Override
    public Iterable<IDataElement> getLocations(IDataElement parentElement) {
        // validate
        if (parentElement == null) {
            throw new IllegalArgumentException("Parent element is null.");
        }
        Node parent = ((DataElement)parentElement).getNode();
        if (parent == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        LOGGER.debug("start getLocation(IDataElement parentElement)");

        Set<IDataElement> locations = new HashSet<IDataElement>();
        Iterator<Relationship> it = parent.getRelationships(DriveRelationshipTypes.LOCATION, Direction.OUTGOING).iterator();
        if (it.hasNext()) {
            locations.add(new DataElement(it.next().getOtherNode(parent)));
        }
        return locations;
    }

    @Override
    public IDataElement findFile(String name) throws AWEException {
        // validate parameters
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty");
        }
        if (files == null) {
            files = dsServ.getIndex(rootNode, DriveNodeTypes.FILE);
        }

        Node fileNode = files.get(AbstractService.NAME, name).getSingle();
        return fileNode == null ? null : new DataElement(fileNode);
    }

    @Override
    public IDataElement getFile(String name) throws AWEException {
        IDataElement result = ((DataElement)findFile(name));
        if (result == null) {
            try {
                result = ((DataElement)addFile(new File(name)));
            } catch (DuplicateNodeNameException e) {
                // impossible
            }
        }
        return result;
    }

    @Override
    public Iterable<IDataElement> getMeasurements(String filename) {
        // validate
        if ((filename == null) || (filename.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }

        return new DataElementIterable(dsServ.getChildrenChainTraverser(files.get(AbstractService.NAME,
                new File(filename).getName()).getSingle()));
    }

    @Override
    public Iterable<IDataElement> getFiles() {
        return new DataElementIterable(dsServ.getChildrenChainTraverser(rootNode));
    }

    @Override
    public IDriveType getDriveType() {
        return driveType;
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return crs;
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
    public void updateLocationBounds(double latitude, double longitude) {
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
    public void updateTimestamp(long timestamp) {
        super.updateTimestamp(timestamp);
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
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        LOGGER.debug("getChildren(" + parent.toString() + ")");

        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        return new DataElementIterable(dsServ.getChildrenChainTraverser(parentNode));
    }

    @Override
    public IDataElement getParentElement(IDataElement childElement) {
        return super.getParentElement(childElement);
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        // validate
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(dsServ.findAllDatasetElements(getRootNode(), elementType));
    }

    /**
     * returns current primary node type
     */
    @Override
    public INodeType getPrimaryType() {
        return primaryType;
    }

    @Override
    public Iterable<IDataElement> getElements(Envelope bounds_transformed) {
        return null;
    }
    
    @Override
    public Iterable<IDataElement> findAllElementsByTimestampPeriod(long min_timestamp, long max_timestamp)
    {
        INodeType primaryType = getPrimaryType();
        try {
            return new DataElementIterable(getNodesByTimestampPeriod(primaryType, min_timestamp, max_timestamp));
        } catch (AWEException e) {
            LOGGER.error("Error with findAllElementsByTimestampPeriod");
        }
        return null;
    }

    @Override
    public Coordinate getCoordinate(IDataElement element) {
        if (element != null) {
            return new Coordinate((Double)element.get(LONGITUDE), (Double)element.get(LATITUDE));
        }
        return null;
    }

    @Override
    public CoordinateReferenceSystem updateCRS(String crsCode) {
        return super.updateCRS(crsCode);
    }

    @Override
    public void setCRS(CoordinateReferenceSystem crs) {
        super.setCRS(crs);
    }

    @Override
    public boolean isUniqueProperties(String property) {
        return false;
    }

    @Override
    public IDistributionModel getDistributionModel(IDistribution< ? > distributionType) throws AWEException {
        return new DistributionModel(this, distributionType);
    }

    @Override
    public IModel getParentModel() throws AWEException {
        if (rootNode == null) {
            throw new IllegalArgumentException("currentModel type is null.");
        }
        Iterator<Node> isVirtual = dsServ.getFirstRelationTraverser(rootNode, DriveRelationshipTypes.VIRTUAL_DATASET,
                Direction.INCOMING).iterator();
        if (isVirtual.hasNext()) {
            return new DriveModel(isVirtual.next());
        }
        return getProject();
    }
    
    @Override
    public IDataElement addSelectedProperties(Set<String> selectedProperties)
    {
        LOGGER.debug("start addSelectedProperties(Set<String> selectedProperties)");
        
        if (selectedProperties == null) {
            throw new IllegalArgumentException("Set<String> is null.");
        }

        Node selectedPropertiesNode = null;
        try {
            selectedPropertiesNode = dsServ.addSelectedPropertiesNode(rootNode);
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, getName());
            String[] array = selectedProperties.toArray(new String[0]);
            params.put(SELECTED_PROPERTIES, array);
            dsServ.setProperties(selectedPropertiesNode, params);
        }
        catch (AWEException e) {
            LOGGER.error("Error with saving selected properties");
        }
        
        return new DataElement(selectedPropertiesNode);
    }
    
    @Override
    public Set<String> getSelectedProperties()
    {
        Set<String> result = new TreeSet<String>();
        try {
            Node selectedPropertiesNode = dsServ.getSelectedPropertiesNode(rootNode);
            String[] array = (String[])selectedPropertiesNode.getProperty(SELECTED_PROPERTIES);
            for (int i = 0; i < array.length; i++) {
                result.add(array[i]);
            }
        } catch (AWEException e) {
            LOGGER.error("Error with get selected properties");
        }
        return result;
    }
}
