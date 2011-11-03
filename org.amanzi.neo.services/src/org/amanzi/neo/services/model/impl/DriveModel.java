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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
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

    private NewDatasetService dsServ;
    private CorrelationService crServ = NeoServiceFactory.getInstance().getNewCorrelationService();

    /**
     * <p>
     * This enum describes node types that are present in drive model.
     * </p>
     * 
     * @author Ana Gr.
     * @since 1.0.0
     */
    public enum DriveNodeTypes implements INodeType {
        FILE, M, MP, M_AGGR, MM;

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
     * Constructor. Pass only rootNode, if you have one, <i>OR</i> all the other parameters.
     * 
     * @param parent a project node
     * @param rootNode a drive node
     * @param name the name of root node of the new drive model
     * @param type the type of root node of the new drive model
     * @throws AWEException if parameters are null or empty or some errors occur in database during
     *         creation of nodes
     */
    public DriveModel(Node parent, Node rootNode, String name, IDriveType type) throws AWEException {
        super(rootNode, DatasetTypes.DRIVE);
        // if root node is null, get one by name
        if (rootNode != null) {
            dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

            this.rootNode = rootNode;
            this.name = (String)rootNode.getProperty(NewAbstractService.NAME, null);
            this.driveType = DriveTypes.valueOf(rootNode.getProperty(NewDatasetService.DRIVE_TYPE, StringUtils.EMPTY).toString());
        } else {
            // validate params
            if (parent == null) {
                throw new IllegalArgumentException("Parent is null.");
            }

            dsServ = NeoServiceFactory.getInstance().getNewDatasetService();
            this.rootNode = dsServ.getDataset(parent, name, DatasetTypes.DRIVE, type);
            this.name = name;
            this.driveType = type;
        }
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
    public DriveModel(Node parent, Node rootNode, String name, IDriveType type, INodeType primaryType) throws AWEException {
        this(parent, rootNode, name, type);
        if (primaryType != null) {
            this.primaryType = primaryType;
        }
    }

    /**
     * Initializes location index for sector nodes.
     */
    private void initializeMultiPropertyIndexing() throws AWEException {
        LOGGER.info("Initializing multi property index...");
        addLocationIndex(DriveNodeTypes.MP);
        addTimestampIndex(primaryType);
    }

    /**
     * Adds a new node of type DRIVE, creates VIRTUAL_DATASET relationship from root node of current
     * DM, and creates and returns a new DM on base of newly created node.
     * 
     * @param name the name of new virtual dataset
     * @param driveType the drive type of new virtual dataset (NB! not TYPE, TYPE is set to DRIVE)
     * @return DriveModel based on new virtual dataset node
     * @throws AWEException if parameters are null or empty or some errors occur in database during
     *         creation of nodes
     */
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
        params.put(NewAbstractService.NAME, name);
        params.put(DRIVE_TYPE, driveType.name());
        dsServ.setProperties(virtual, params);

        DriveModel result = new DriveModel(null, virtual, name, null);
        return result;
    }

    /**
     * Looks for a virtual dataset node with the defined name, creates a DriveModel based on it, if
     * found
     * 
     * @param name the name of virtual dataset node
     * @return DriveModel based on the found node or null if search failed
     */
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

    /**
     * Looks for a virtual dataset node or creates a new one if nothing found. returns a new
     * DriveModel based on resulting node.
     * 
     * @param name
     * @param driveType used to create a new virtual dataset
     * @return a DriveModel based on found or created virtual dataset node
     * @throws AWEException if errors occurred during creation of new node
     */
    public IDriveModel getVirtualDataset(String name, IDriveType driveType) throws AWEException {
        LOGGER.debug("start getVirtualDataset(String name, IDriveType driveType)");

        IDriveModel result = findVirtualDataset(name);
        if (result == null) {
            result = addVirtualDataset(name, driveType);
        }
        return result;
    }

    /**
     * @return a List<Node> containing DriveModels created on base of virtual dataset nodes in
     *         current DriveModel
     */
    public Iterable<IDriveModel> getVirtualDatasets() {
        LOGGER.debug("start getVirtualDatasets()");

        List<IDriveModel> result = new ArrayList<IDriveModel>();
        for (Node node : dsServ.getVirtualDatasets(rootNode)) {
            try {
                result.add(new DriveModel(null, node, null, null));
            } catch (AWEException e) {
                LOGGER.error("Could not create drive model on node " + node.getProperty(NewAbstractService.NAME, null), e);
            }
        }
        return result;
    }

    /**
     * Adds a FILE node to the drive model. FILE nodes are added to root node via
     * CHILD-NEXT-...-NEXT chain. FILE nodes are indexed by NAME.
     * 
     * @param file a File object containing file name and path
     * @return the newly created node
     * @throws DatabaseException if errors occur in database
     * @throws DuplicateNodeNameException when trying to add a file that already exists
     */
    public IDataElement addFile(File file) throws DatabaseException, DuplicateNodeNameException {
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
        params.put(NewAbstractService.NAME, file.getName());
        params.put(PATH, file.getPath());
        dsServ.setProperties(fileNode, params);
        if (files == null) {
            files = dsServ.addNodeToIndex(fileNode, NewAbstractService.getIndexKey(rootNode, DriveNodeTypes.FILE),
                    NewAbstractService.NAME, file.getName());
        } else {
            dsServ.addNodeToIndex(fileNode, files, NewAbstractService.NAME, file.getName());
        }
        return new DataElement(fileNode);
    }

    /**
     * Adds a measurement node to a file node with defined filename. If params map contains lat and
     * lon properties, also creates a location node. Use this method if you want to create a
     * measurement with default type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(String filename, Map<String, Object> params) throws AWEException {
        return addMeasurement(filename, params, primaryType);
    }

    /**
     * Adds a measurement node to a file node in <code>file</code> parameter. If params map contains
     * lat and lon properties, also creates a location node. Use this method if you want to create a
     * measurement with default type.
     * 
     * @param file a <code>IDataElement</code>, containing he file node
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params) throws AWEException {
        return addMeasurement(file, params, primaryType);
    }

    /**
     * Adds a measurement node to a file node in the <code>file</code> parameter. If params map
     * contains lat and lon properties, also creates a location node. Use this method if you want to
     * create a measurement with type, that is different from drive model primary type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @param nodeType the type of node to create
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType) throws AWEException {

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
        Long lat = (Long)params.get(LATITUDE);
        Long lon = (Long)params.get(LONGITUDE);
        Long tst = (Long)params.get(TIMESTAMP);

        if ((lat != null) && (lat != 0) && (lon != null) && (lon != 0)) {
            createLocationNode(m, lat, lon);
            params.remove(LATITUDE);
            params.remove(LONGITUDE);
        }
        if ((tst != null) && (tst != 0)) {
            updateTimestamp(tst);
        }
        params.put(NewAbstractService.DATASET_ID, this.name);
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

    /**
     * Adds a measurement node to a file node with defined filename. If params map contains lat and
     * lon properties, also creates a location node. Use this method if you want to create a
     * measurement with type, that is different from drive model primary type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @param nodeType the type of node to create
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(String filename, Map<String, Object> params, INodeType nodeType) throws AWEException {
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

        return addMeasurement(new DataElement(fileNode), params, nodeType);
    }

    /**
     * The method creates CALL_M relationships between <code>parent</code> node and
     * <code>source</code> nodes.
     * 
     * @param parent a <code>DataElement</code>, that contains parent node.
     * @param source list of <code>DataElement</code>s, containing <code>Node</code> objects.
     * @throws DatabaseException if problems occur in database
     */
    public void linkNode(IDataElement parent, Iterable<IDataElement> source) throws DatabaseException {
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
            dsServ.createRelationship(parentNode, node, DriveRelationshipTypes.CALL_M);
        }

    }

    /**
     * Creates a node, sets its LATITUDE and LONGITUDE properties, and created a LOCATION
     * relationship from parent node.
     * 
     * @param parent
     * @param lat
     * @param lon
     * @throws DatabaseException if errors occur in the database
     */
    protected void createLocationNode(Node parent, long lat, long lon) throws DatabaseException {
        LOGGER.debug("start createLocationNode(Node measurement, long lat, long lon)");
        // validate params
        if (parent == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }

        Node location = dsServ.createNode(parent, DriveRelationshipTypes.LOCATION, DriveNodeTypes.MP);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LATITUDE, lat);
        params.put(LONGITUDE, lon);
        dsServ.setProperties(location, params);
        updateLocationBounds(lat, lon);
    }

    /**
     * Finds a location node.
     * 
     * @param parent
     * @return the found location node or null.
     */
    public IDataElement getLocation(IDataElement parentElement) {
        // validate
        if (parentElement == null) {
            throw new IllegalArgumentException("Parent element is null.");
        }
        Node parent = ((DataElement)parentElement).getNode();
        if (parent == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        LOGGER.debug("start getLocation(IDataElement parentElement)");

        Iterator<Relationship> it = parent.getRelationships(DriveRelationshipTypes.LOCATION, Direction.OUTGOING).iterator();
        if (it.hasNext()) {
            return new DataElement(it.next().getOtherNode(parent));
        }
        return null;
    }

    /**
     * Looks up for a file node through index
     * 
     * @param name
     * @return
     */
    public IDataElement findFile(String name) {
        // validate parameters
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty");
        }
        if (files == null) {
            files = dsServ.getIndexForNodes(rootNode, DriveNodeTypes.FILE);
        }

        Node fileNode = files.get(NewAbstractService.NAME, name).getSingle();
        return fileNode == null ? null : new DataElement(fileNode);
    }

    /**
     * Finds or creates a file with the defined name.
     * 
     * @param name
     * @return FILE node
     * @throws DatabaseException if errors occur in database
     */
    public IDataElement getFile(String name) throws DatabaseException {
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

    /**
     * Gets all measurements under defined file.
     * 
     * @param filename the name of the file
     * @return and iterator over measurement nodes
     */
    public Iterable<IDataElement> getMeasurements(String filename) {
        // validate
        if ((filename == null) || (filename.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }

        return new DataElementIterable(dsServ.getChildrenChainTraverser(files.get(NewAbstractService.NAME,
                new File(filename).getName()).getSingle()));
    }

    /**
     * @return an iterator over FILE nodes
     */
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
            if (network.getProperty(NewAbstractService.NAME, StringUtils.EMPTY).equals(correlationModelName)) {
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
        LOGGER.info("getChildren(" + parent.toString() + ")");

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
    public INodeType getType() {
        return primaryType;
    }

    @Override
    public Iterable<IDataElement> getElements(Envelope bounds_transformed) {
        return null;
    }

    @Override
    public Coordinate getCoordinate(IDataElement element) {
        IDataElement location = getLocation(element);
        if (location != null) {
            return new Coordinate((Long)location.get(LATITUDE), (Long)location.get(LONGITUDE));
        }
        return null;
    }
}
