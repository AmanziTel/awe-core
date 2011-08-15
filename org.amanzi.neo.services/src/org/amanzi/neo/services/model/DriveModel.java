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

import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

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

    public DriveModel(Node parent, Node rootNode, String name) {
        // if root node is null, get one by name
    }

    public String getName() {
        return null;
    }

    public Node getRootNode() {
        return null;
    }

    public DriveModel addVirtualDataset(String name, IDriveType driveType) {
        return null;
    }

    public Iterable<DriveModel> getVirtualDatasets() {
        return null;
    }

    public Node addFile(File file) {
        // file nodes are added as c-n-n
        return null;
    }

    public Node addMeasurement(String filename, Map<String, Object> params) {
        // measurements are added as c-n-n o file nodes
        // lat, lon properties are stored in a location node
        return null;
    }

    /**
     *
     * @param m
     * @return
     */
    public Node getLocation(Node measurement) {
        return null;
    }

    // public void setDatabase(GraphDatabaseService graphDb) {
    //
    // }
}
