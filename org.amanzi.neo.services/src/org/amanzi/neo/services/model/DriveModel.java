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

package org.amanzi.neo.services.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * This class manages drive data.
 * </p>
 * 
 * @author Ana Gr.
 * @since 1.0.0
 */
public class DriveModel {

    private static Logger LOGGER = Logger.getLogger(DriveModel.class);

    // constants
    protected final static String DRIVE_TYPE = "drive_type";
    protected final static String TIMESTAMP = "timestamp";
    protected final static String LATITUDE = "lat";
    protected final static String LONGITUDE = "lon";
    protected final static String PATH = "path";
    protected final static String COUNT = "count";
    protected final static String PRIMARY_TYPE = "primary_type";
    protected final static String MIN_TIMESTAMP = "min_timestamp";
    protected final static String MAX_TIMESTAMP = "max_timestamp";

    // private members
    private GraphDatabaseService graphDb;
    private Transaction tx;
    private Index<Node> files;
    private Node root;
    private String name;
    private long min_tst = Long.MAX_VALUE;
    private long max_tst = 0;
    private int count = 0;

    private NewDatasetService dsServ;

    /**
     * <p>
     * This enum describes node types that are present in drive model.
     * </p>
     * 
     * @author Ana Gr.
     * @since 1.0.0
     */
    public enum DriveNodeTypes implements INodeType {
        FILE, M, MP;

        @Override
        public String getId() {
            return name();
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
        VIRTUAL_DATASET, LOCATION;
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
        // if root node is null, get one by name
        if (rootNode != null) {
            graphDb = rootNode.getGraphDatabase();
            dsServ = new NewDatasetService(graphDb);

            this.root = rootNode;
            this.name = (String)rootNode.getProperty(NewAbstractService.NAME, null);
        } else {
            // validate params
            if (parent == null) {
                throw new IllegalArgumentException("Parent is null.");
            }

            graphDb = parent.getGraphDatabase();
            dsServ = new NewDatasetService(graphDb);
            root = dsServ.getDataset(parent, name, DatasetTypes.DRIVE, type);
            this.name = name;
        }
    }

    /**
     * @return the name of the root node of current drive model
     */
    public String getName() {
        return name;
    }

    /**
     * @return the root node
     */
    public Node getRootNode() {
        return root;
    }

    /**
     * Adds a new node of type DRIVE, creates VIRTUAL_DATASET relationship from root node of current
     * DM, and creaates and returns a new DM on base of newly created node.
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
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Name is null or empty.");
        }
        if (driveType == null) {
            throw new IllegalArgumentException("Drive type is null");
        }
        if (findVirtualDataset(name) != null) {
            throw new DuplicateNodeNameException(name, DatasetTypes.DRIVE);
        }

        tx = graphDb.beginTx();
        Node virtual = dsServ.createNode(DatasetTypes.DRIVE);
        try {
            virtual.setProperty(NewAbstractService.NAME, name);
            virtual.setProperty(DRIVE_TYPE, driveType.getId());
            root.createRelationshipTo(virtual, DriveRelationshipTypes.VIRTUAL_DATASET);
            tx.success();
        } finally {
            tx.finish();
        }
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
    public DriveModel findVirtualDataset(String name) {
        LOGGER.debug("start findVirtualDataset(String name)");

        DriveModel result = null;
        for (DriveModel dm : getVirtualDatasets()) {
            if (dm.getName().equals(name)) {
                result = dm;
                break;
            }
        }
        return result;
    }

    /**
     * Looks for a virtual dataset node or created a new one if nothing found. returns a new
     * DriveModel based on resulting node.
     * 
     * @param name
     * @param driveType used to create a new virtual dataset
     * @return a DriveModel based on found or created virtual dataset node
     * @throws AWEException if errors occurred during creation of new node
     */
    public DriveModel getVirtualDataset(String name, IDriveType driveType) throws AWEException {
        LOGGER.debug("start getVirtualDataset(String name, IDriveType driveType)");

        DriveModel result = findVirtualDataset(name);
        if (result == null) {
            result = addVirtualDataset(name, driveType);
        }
        return result;
    }

    /**
     * @return a List<Node> containing DriveModels created on base of virtual dataset nodes in
     *         current DriveModel
     */
    public Iterable<DriveModel> getVirtualDatasets() {
        LOGGER.debug("start getVirtualDatasets()");

        List<DriveModel> result = new ArrayList<DriveModel>();
        for (Node node : getVirtualDatasetsTraversalDescription().traverse(root).nodes()) {
            try {
                result.add(new DriveModel(null, node, null, null));
            } catch (AWEException e) {
                LOGGER.error("Could not create drive model on node " + node.getProperty(NewAbstractService.NAME, null), e);
            }
        }
        return result;
    }

    /**
     * @return TraversalDescription to iterate over virtual dataset nodes.
     */
    protected TraversalDescription getVirtualDatasetsTraversalDescription() {
        LOGGER.debug("start getVirtualDatasetsTraversalDescription()");

        return Traversal.description().breadthFirst().relationships(DriveRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.atDepth(1)).evaluator(Evaluators.excludeStartPosition());
    }

    /**
     * Adds a FILE node to the drive model. FILE nodes are added to root node via
     * CHILD-NEXT-...-NEXT chain. FILE nodes are indexed by NAME.
     * 
     * @param file a File object containing file name and path
     * @return the newly created node
     * @throws DatabaseException if errors occur in database
     */
    public Node addFile(File file) throws DatabaseException {
        LOGGER.debug("start addFile(File file)");

        // file nodes are added as c-n-n
        // validate params
        if (file == null) {
            throw new IllegalArgumentException("File is null.");
        }
        tx = graphDb.beginTx();

        Node fileNode = dsServ.addChild(root, dsServ.createNode(DriveNodeTypes.FILE), null);
        try {
            fileNode.setProperty(NewAbstractService.NAME, file.getName());
            fileNode.setProperty(PATH, file.getPath());
            if (files == null) {
                files = graphDb.index().forNodes(dsServ.getIndexKey(root, DriveNodeTypes.FILE));
            }
            files.add(fileNode, NewAbstractService.NAME, file.getName());
            tx.success();
        } catch (Exception e) {
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return fileNode;
    }

    /**
     * Adds a measurement node to a file node with defined filename. If params map contains lat and
     * lon properties, also creates a location node.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws DatabaseException if errors occur in database
     */
    public Node addMeasurement(String filename, Map<String, Object> params) throws DatabaseException {
        LOGGER.debug("start addMeasurement(String filename, Map<String, Object> params)");

        // measurements are added as c-n-n o file nodes
        // lat, lon properties are stored in a location node

        // validate params
        if ((filename == null) || (filename.equals(""))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters map is null.");
        }

        if (files == null) {
            files = graphDb.index().forNodes(dsServ.getIndexKey(root, DriveNodeTypes.FILE));
        }

        Node fileNode = files.get(NewAbstractService.NAME, filename).getSingle();
        if (fileNode == null) {
            throw new IllegalArgumentException("File node " + filename + " not found.");
        }
        tx = graphDb.beginTx();
        Node m = dsServ.createNode(DriveNodeTypes.M);
        dsServ.addChild(fileNode, m, null);
        try {
            Long lat = (Long)params.get(LATITUDE);
            Long lon = (Long)params.get(LONGITUDE);
            Long tst = (Long)params.get(TIMESTAMP);

            if ((lat != null) && (lat != 0) && (lon != null) && (lon != 0)) {
                createLocationNode(m, lat, lon);
                params.remove(LATITUDE);
                params.remove(LONGITUDE);
            }
            if ((tst != null) && (tst != 0)) {
                if (min_tst > tst) {
                    min_tst = tst;
                    root.setProperty(MIN_TIMESTAMP, min_tst);
                }
                if (max_tst < tst) {
                    max_tst = tst;
                    root.setProperty(MAX_TIMESTAMP, max_tst);
                }
            }
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    m.setProperty(key, value);
                }
            }
            root.setProperty(PRIMARY_TYPE, DriveNodeTypes.M.getId());
            count++;
            root.setProperty(COUNT, count);
            tx.success();
        } finally {
            tx.finish();
        }
        return m;
    }

    /**
     * Created a node, sets its LATITUDE and LONGITUDE properties, and created a LOCATION
     * relationship from parent node.
     * 
     * @param parent
     * @param lat
     * @param lon
     * @throws DatabaseException if errors occur in the database
     */
    public void createLocationNode(Node parent, long lat, long lon) throws DatabaseException {
        LOGGER.debug("start createLocationNode(Node measurement, long lat, long lon)");
        // validate params
        if (parent == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }

        Node location = dsServ.createNode(DriveNodeTypes.MP);
        location.setProperty(LATITUDE, lat);
        location.setProperty(LONGITUDE, lon);
        parent.createRelationshipTo(location, DriveRelationshipTypes.LOCATION);
    }

    /**
     * Finds a location node.
     * 
     * @param parent
     * @return the found location node or null.
     */
    public Node getLocation(Node parent) {
        LOGGER.debug("start getLocation(Node measurement)");

        Iterator<Relationship> it = parent.getRelationships(DriveRelationshipTypes.LOCATION, Direction.OUTGOING).iterator();
        if (it.hasNext()) {
            return it.next().getOtherNode(parent);
        }
        return null;
    }
}
