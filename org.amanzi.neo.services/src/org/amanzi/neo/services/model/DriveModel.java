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
import java.util.Map;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
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

    private NewDatasetService dsServ;

    public enum DriveNodeTypes implements INodeType {
        FILE, M, MP;

        @Override
        public String getId() {
            return name();
        }

    }

    public enum DriveRelationshipTypes implements RelationshipType {
        VIRTUAL_DATASET, LOCATION;
    }

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

    public String getName() {
        return name;
    }

    public Node getRootNode() {
        return root;
    }

    public DriveModel addVirtualDataset(String name, IDriveType driveType) {
        return null;
    }

    public Iterable<DriveModel> getVirtualDatasets() {
        return null;
    }

    public Node addFile(File file) throws DatabaseException {
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

    public Node addMeasurement(String filename, Map<String, Object> params) throws DatabaseException {
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

            if ((lat != null) && (lon != null)) {
                createLocationNode(m, lat, lon);
                params.remove(LATITUDE);
                params.remove(LONGITUDE);
            }
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    m.setProperty(key, value);
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return m;
    }

    /**
     * @param m
     * @param lat
     * @param lon
     * @throws DatabaseException
     */
    private void createLocationNode(Node measurement, long lat, long lon) throws DatabaseException {
        Node location = dsServ.createNode(DriveNodeTypes.MP);
        location.setProperty(LATITUDE, lat);
        location.setProperty(LONGITUDE, lon);
        measurement.createRelationshipTo(location, DriveRelationshipTypes.LOCATION);
    }

    /**
     * @param m
     * @return
     */
    public Node getLocation(Node measurement) {
        return measurement.getRelationships(DriveRelationshipTypes.LOCATION, Direction.OUTGOING).iterator().next()
                .getOtherNode(measurement);
    }
}
