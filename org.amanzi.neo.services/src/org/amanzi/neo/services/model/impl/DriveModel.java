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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * This class manages drive data.
 * </p>
 * 
 * @author Ana Gr.
 * @since 1.0.0
 */
public class DriveModel extends MeasurementModel implements IDriveModel {

    private static Logger LOGGER = Logger.getLogger(DriveModel.class);

    // private members
    private IDriveType driveType;

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
        setPrimaryType(DriveNodeTypes.findById(rootNode.getProperty(DatasetService.PRIMARY_TYPE).toString()));
        this.driveType = DriveTypes.valueOf(rootNode.getProperty(DatasetService.DRIVE_TYPE, StringUtils.EMPTY).toString());
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
            datasetService = NeoServiceFactory.getInstance().getDatasetService();

            this.rootNode = rootNode;
            this.name = (String)rootNode.getProperty(AbstractService.NAME, null);
            this.driveType = DriveTypes.valueOf(rootNode.getProperty(DatasetService.DRIVE_TYPE, StringUtils.EMPTY).toString());
        } else {
            // validate params
            if (parent == null) {
                throw new IllegalArgumentException("Parent is null.");
            }

            this.rootNode = datasetService.getDataset(parent, name, DatasetTypes.DRIVE, type, primaryType);
            this.name = name;
            this.driveType = type;
        }
        setPrimaryType(primaryType);
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
        addTimestampIndex(getPrimaryType());
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

        Node virtual = datasetService.createNode(rootNode, DriveRelationshipTypes.VIRTUAL_DATASET, DatasetTypes.DRIVE);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.NAME, name);
        params.put(DRIVE_TYPE, driveType.name());
        datasetService.setProperties(virtual, params);

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
        for (Node node : datasetService.getVirtualDatasets(rootNode)) {
            try {
                result.add(new DriveModel(null, node, null, null));
            } catch (AWEException e) {
                LOGGER.error("Could not create drive model on node " + node.getProperty(AbstractService.NAME, null), e);
            }
        }
        return result;
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
            datasetService.createRelationship(parentNode, node, rel);
        }

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
    public IDriveType getDriveType() {
        return driveType;
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return currentGisModel.getCrs();
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

        return new DataElementIterable(datasetService.getChildrenChainTraverser(parentNode));
    }

    @Override
    public IDataElement getParentElement(IDataElement childElement) {
        return super.getParentElement(childElement);
    }

    @Override
    public Iterable<IDataElement> findAllElementsByTimestampPeriod(long min_timestamp, long max_timestamp) {
        INodeType primaryType = getPrimaryType();
        try {
            return new DataElementIterable(getNodesByTimestampPeriod(primaryType, min_timestamp, max_timestamp));
        } catch (AWEException e) {
            LOGGER.error("Error with findAllElementsByTimestampPeriod");
        }
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
        Iterator<Node> isVirtual = datasetService.getFirstRelationTraverser(rootNode, DriveRelationshipTypes.VIRTUAL_DATASET,
                Direction.INCOMING).iterator();
        if (isVirtual.hasNext()) {
            return new DriveModel(isVirtual.next());
        }
        return getProject();
    }

    @Override
    public IDataElement addSelectedProperties(Set<String> selectedProperties) {
        LOGGER.debug("start addSelectedProperties(Set<String> selectedProperties)");

        if (selectedProperties == null) {
            throw new IllegalArgumentException("Set<String> is null.");
        }

        Node selectedPropertiesNode = null;
        try {
            selectedPropertiesNode = datasetService.addSelectedPropertiesNode(rootNode);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, getName());
            String[] array = selectedProperties.toArray(new String[0]);
            params.put(SELECTED_PROPERTIES, array);
            datasetService.setProperties(selectedPropertiesNode, params);
        } catch (AWEException e) {
            LOGGER.error("Error with saving selected properties");
        }

        return new DataElement(selectedPropertiesNode);
    }

    @Override
    public Set<String> getSelectedProperties() {
        Set<String> result = new TreeSet<String>();
        try {
            Node selectedPropertiesNode = datasetService.getSelectedPropertiesNode(rootNode);
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
