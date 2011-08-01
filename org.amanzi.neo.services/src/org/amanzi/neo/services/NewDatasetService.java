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

package org.amanzi.neo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicy;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Service provide common operations with base structure in database
 * </p>
 * 
 * @author kruglik_a
 * @since 1.0.0
 */
public class NewDatasetService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(NewDatasetService.class);
    /**
     * constants for dataset property name
     */

    public final static String DRIVE_TYPE = "drive_type";
    public final static String PROJECT_NODE = "project_node";
    public static final String LAST_CHILD_ID = "last_child_id";
    public static final String PARENT_ID = "parent_id";

    private Transaction tx;

    /**
     * <p>
     * enum of dataset relationships types
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    public enum DatasetRelationTypes implements RelationshipType {
        PROJECT, DATASET, CHILD, NEXT;
    }

    /**
     * <p>
     * enum of dataset types
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    public enum DatasetTypes implements INodeType {
        NETWORK, DRIVE, COUNTERS;

        @Override
        public String getId() {
            return name();
        }
    }

    /**
     * <p>
     * enum of Drive types
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    public enum DriveTypes {
        NEMO_V1, NEMO_V2, TEMS, ROMES;
    }

    /**
     * <p>
     * this class choose from dataset nodes node by name, type and driveType
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    private class FilterDataset extends NameTypeEvaluator {

        private DriveTypes driveType;

        /**
         * constructor with name and type parameter for filter
         * 
         * @param name - dataset name
         * @param type - dataset type
         */
        public FilterDataset(String name, DatasetTypes type) {
            super(name, type);
            this.driveType = null;
        }

        /**
         * constructor with name, type and driveType parameter for filter
         * 
         * @param name - dataset name
         * @param type - dataset type
         * @param driveType - dataset drive type
         */
        public FilterDataset(String name, DatasetTypes type, DriveTypes driveType) {
            super(name, type);
            this.driveType = driveType;
        }

        @Override
        public Evaluation evaluate(Path arg0) {
            if (super.evaluate(arg0).includes()) {
                if (driveType == null || driveType.name().equals(arg0.endNode().getProperty(DRIVE_TYPE, ""))) {
                    return Evaluation.INCLUDE_AND_CONTINUE;
                }
                return Evaluation.EXCLUDE_AND_CONTINUE;
            }

            return Evaluation.EXCLUDE_AND_CONTINUE;
        }

    }

    /**
     * <p>
     * this class choose nodes by type from dataset nodes
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    private class FilterDatasetsByType implements Evaluator {
        /**
         * constructor for filter datasets by type
         * 
         * @param type - dataset type
         */
        public FilterDatasetsByType(DatasetTypes type) {
            this.type = type;
        }

        private DatasetTypes type;

        @Override
        public Evaluation evaluate(Path arg0) {
            boolean includes = false;
            if (getNodeType(arg0.endNode()).equals(type.name()))
                includes = true;
            return Evaluation.ofIncludes(includes);
        }

    }

    /**
     * constructor
     */
    public NewDatasetService() {
        super();
    }

    /**
     * constructor for testing
     * 
     * @param service
     */
    public NewDatasetService(GraphDatabaseService service) {
        super(service);
    }

    /**
     * this method return TraversalDescription for Dataset nodes
     * 
     * @return TraversalDescription
     */
    private TraversalDescription getDatasetsTraversalDescription() {
        return Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition());
    }

    /**
     * find dataset node by name and type
     * 
     * @param projectNode - node, which defines the project, within which will be implemented search
     * @param name - dataset name
     * @param type - dataset type
     * @return dataset node or null if dataset not found
     * @throws InvalidDatasetParameterException this method may call exception if name == "" or name
     *         == null or type == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter
     *         differs from NETWORK
     * @throws DuplicateNodeNameException this method may call exception if it find more than one
     *         dataset
     */
    public Node findDataset(Node projectNode, final String name, final DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        LOGGER.debug("start findDataset(Node projectNode, String name, DatasetTypes type)");

        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }

        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: type parameter differs from NETWORK");
            throw new DatasetTypeParameterException(type);

        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDataset(name, type)).traverse(projectNode);
        Iterator<Node> iter = tr.nodes().iterator();
        LOGGER.debug("finish findDataset(Node projectNode, String name, DatasetTypes type)");
        if (iter.hasNext()) {
            Node result = iter.next();
            if (iter.hasNext()) {
                throw new DuplicateNodeNameException(name, type);
            }
            return result;
        }
        return null;
    }

    /**
     * find dataset node by name, type and driveType
     * 
     * @param projectNode - node, which defines the project, within which will be implemented search
     * @param name - dataset name
     * @param type - dataset type
     * @param driveType - dataset drive type
     * @return dataset node or null if dataset not found
     * @throws InvalidDatasetParameterException this method may call exception if name == "" or name
     *         == null or type == null or driveType == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter is
     *         NETWORK
     * @throws DuplicateNodeNameException this method may call exception if it find more than one
     *         dataset
     */
    public Node findDataset(Node projectNode, final String name, final DatasetTypes type, final DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DuplicateNodeNameException {
        LOGGER.debug("start findDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (driveType == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter driveType = null");
            throw new InvalidDatasetParameterException(DRIVE_TYPE, driveType);
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: parameter type can not be NETWORK in this method");
            throw new DatasetTypeParameterException(type);
        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDataset(name, type, driveType)).traverse(projectNode);
        Iterator<Node> iter = tr.nodes().iterator();
        LOGGER.debug("finish findDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (iter.hasNext()) {
            Node result = iter.next();
            if (iter.hasNext()) {
                throw new DuplicateNodeNameException(name, type);
            }
            return result;
        }
        return null;
    }

    /**
     * create dataset node
     * 
     * @param projectNode - node, which defines the project within which the dataset will be created
     * @param name - dataset name
     * @param type - dataset type
     * @return created dataset node
     * @throws InvalidDatasetParameterException this method may call exception if name == "" or name
     *         == null or type == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter
     *         differs from NETWORK
     * @throws DuplicateNodeNameException this method may call exception if dataset with that name
     *         already exists
     * @throws DatabaseException - exception in database
     */
    public Node createDataset(Node projectNode, String name, DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("start createDataset(Node projectNode, String name, DatasetTypes type)");
        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: type parameter differs from NETWORK");
            throw new DatasetTypeParameterException(type);
        }
        if (findDataset(projectNode, name, type) != null) {
            LOGGER.error("DublicateDatasetException: dataset with that name already exists ");
            throw new DuplicateNodeNameException(name, type);
        }

        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
            datasetNode.setProperty(PROPERTY_NAME_NAME, name);
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        LOGGER.debug("finish createDataset(Node projectNode, String name, DatasetTypes type)");
        return datasetNode;
    }

    /**
     * create dataset node
     * 
     * @param projectNode - node, which defines the project within which the dataset will be created
     * @param name - dataset name
     * @param type - dataset type
     * @param driveType - dataset drive type
     * @return created dataset node
     * @throws InvalidDatasetParameterExceptionthis method may call exception if name == "" or name
     *         == null or type == null or driveType == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter is
     *         NETWORK
     * @throws DuplicateNodeNameException this method may call exception if dataset with that name
     *         already exists
     */
    public Node createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DuplicateNodeNameException {
        LOGGER.debug("start createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (driveType == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter driveType = null");
            throw new InvalidDatasetParameterException(DRIVE_TYPE, driveType);
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: parameter driveType can not be NETWORC in this method");
            throw new DatasetTypeParameterException(type);
        }
        if (findDataset(projectNode, name, type, driveType) != null) {
            LOGGER.error("DublicateDatasetException: dataset with that name already exists ");
            throw new DuplicateNodeNameException(name, type);
        }

        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
            datasetNode.setProperty(PROPERTY_NAME_NAME, name);
            datasetNode.setProperty(DRIVE_TYPE, driveType.name());
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
        } finally {
            tx.finish();
        }
        LOGGER.debug("finish createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name and type, and if not found then create dataset
     * node
     * 
     * @param projectNode node, which defines the project within which the dataset will be got
     * @param name - dataset name
     * @param type - dataset type
     * @return dataset node
     * @throws InvalidDatasetParameterException this method may call exception if name == "" or name
     *         == null or type == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter
     *         differs from NETWORK
     * @throws DuplicateNodeNameException this method may call exception if dataset with that name
     *         already exists
     * @throws DatabaseException - exception in database
     */
    public Node getDataset(Node projectNode, String name, DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("start getDataset(Node projectNode, String name, DatasetTypes type)");
        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: type parameter differs from NETWORK");
            throw new DatasetTypeParameterException(type);
        }
        Node datasetNode = findDataset(projectNode, name, type);
        if (datasetNode == null) {
            datasetNode = createDataset(projectNode, name, type);
        }
        LOGGER.debug("finish getDataset(Node projectNode, String name, DatasetTypes type)");
        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name, type and driveType, and if not found then
     * create dataset
     * 
     * @param projectNode - node, which defines the project within which the dataset will be got
     * @param name - dataset name
     * @param type - dataset type
     * @param driveType - dataset drive type
     * @return dataset node
     * @throws InvalidDatasetParameterException this method may call exception if name == "" or name
     *         == null or type == null or driveType == null or projectNode == null
     * @throws DatasetTypeParameterException this method may call exception if type parameter is
     *         NETWORK
     * @throws DuplicateNodeNameException this method may call exception if dataset with that name
     *         already exists
     */
    public Node getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DuplicateNodeNameException {
        LOGGER.debug("start getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");

        if (name == "") {
            LOGGER.error("InvalidDatasetParameterException: parameter name is empty string");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (name == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter name = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_NAME_NAME, name);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (driveType == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter driveType = null");
            throw new InvalidDatasetParameterException(DRIVE_TYPE, driveType);
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("DatasetTypeParameterException: parameter driveType can not be NETWORC in this method");
            throw new DatasetTypeParameterException(type);
        }

        Node datasetNode = findDataset(projectNode, name, type, driveType);
        if (datasetNode == null) {
            datasetNode = createDataset(projectNode, name, type, driveType);
        }
        LOGGER.debug("finish getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        return datasetNode;
    }

    /**
     * this method find all dataset nodes in all projects
     * 
     * @return List<Node> list of dataset nodes
     */
    public List<Node> findAllDatasets() {
        LOGGER.debug("start findAllDatasets()");
        List<Node> datasetList = new ArrayList<Node>();
        TraversalDescription allProjects = new ProjectService().getProjectTraversalDescription();

        for (Node projectNode : allProjects.traverse(graphDb.getReferenceNode()).nodes()) {
            Traverser tr = getDatasetsTraversalDescription().traverse(projectNode);
            for (Node dataset : tr.nodes()) {
                datasetList.add(dataset);
            }
        }
        LOGGER.debug("finish findAllDatasets()");
        return datasetList;
    }

    /**
     * this method find all dataset nodes by type in all projects
     * 
     * @param type - dataset type
     * @return List<Node> list of dataset nodes
     * @throws InvalidDatasetParameterException this method may call exception if type == null
     */
    public List<Node> findAllDatasetsByType(DatasetTypes type) throws InvalidDatasetParameterException {
        LOGGER.debug("start findAllDatasetsByType()");

        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }

        List<Node> datasetList = new ArrayList<Node>();
        TraversalDescription allProjects = new ProjectService().getProjectTraversalDescription();

        for (Node projectNode : allProjects.traverse(graphDb.getReferenceNode()).nodes()) {
            Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDatasetsByType(type)).traverse(projectNode);
            for (Node dataset : tr.nodes()) {
                datasetList.add(dataset);
            }
        }
        LOGGER.debug("finish findAllDatasetsByType()");
        return datasetList;
    }

    /**
     * this method find all dataset nodes in project
     * 
     * @param projectNode - node, which defines the project, within which will be implemented search
     * @return List<Node> list of dataset nodes
     * @throws InvalidDatasetParameterException this method may call exception if projectNode ==
     *         null
     */
    public List<Node> findAllDatasets(Node projectNode) throws InvalidDatasetParameterException {
        LOGGER.debug("start findAllDatasets(Node projectNode)");
        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
        LOGGER.debug("finish findAllDatasets(Node projectNode)");
        return datasetList;
    }

    /**
     * this method find all dataset nodes by type in project
     * 
     * @param projectNode - node, which defines the project, within which will be implemented search
     * @param type - datasets type
     * @return List<Node> list of dataset nodes
     * @throws InvalidDatasetParameterException this method may call exception if projectNode ==
     *         null or type == null
     */
    public List<Node> findAllDatasetsByType(Node projectNode, final DatasetTypes type) throws InvalidDatasetParameterException {
        LOGGER.debug("start findAllDatasetsByType(Node projectNode, DatasetTypes type)");

        if (projectNode == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter projectNode = null");
            throw new InvalidDatasetParameterException(PROJECT_NODE, projectNode);
        }
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }

        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDatasetsByType(type)).traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
        LOGGER.debug("finish findAllDatasetsByType(Node projectNode, DatasetTypes type)");
        return datasetList;
    }

    /**
     * Adds <code>child</code> to the end of <code>parent</code>'s children chain. If in parent
     * <code>last_child_id</code> is not set, method tries to add child to the
     * <code>lastChild</code>. If <code>lastChild</code> is not set, too, the method will look up
     * for the last child in a chain. If <code>last_child_id</code> property in parent and
     * <code>lastChild.getId()</code> are not equal, exception is thrown
     * 
     * @param parent - parent node of a chain
     * @param child - the node to be added to the end of the chain
     * @param lastChild - current last child of the chain
     * @return - the added node or <code>null</code>, if the node could not be added
     * @throws IllegalArgumentException if <code>parent</code> or <code>child</code> id
     *         <code>null</code>
     * @throws IllegalArgumentException if last_child_id and lastChild.getId() are not equal
     * @throws DatabaseException if some neo error occur
     */
    public Node addChild(Node parent, Node child, Node lastChild) throws DatabaseException {
        LOGGER.debug("start addChild(Node parent, Node child, Node lastChild)");
        // validate arguments
        if (parent == null) {
            throw new IllegalArgumentException("Parent cannot be null");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }

        // try to chain child to lastChild
        if (lastChild != null) {
            long lastChildId = (Long)parent.getProperty(LAST_CHILD_ID, 0L);
            if (lastChildId > 0) {
                if (lastChildId == lastChild.getId()) {
                    insertChild(parent, child, lastChild, DatasetRelationTypes.NEXT);
                } else {
                    throw new IllegalArgumentException("Last child ID does not match info in parent properties.");
                }
            } else {
                // try to look up last child
                Node node = getLastChild(parent);
                if (node == null) {
                    // parent has no children
                    insertChild(parent, child, parent, DatasetRelationTypes.CHILD);
                } else {
                    // parent has children
                    insertChild(parent, child, node, DatasetRelationTypes.NEXT);
                }
            }
        } else {
            // try to get last child from parent properties
            long lastChildId = (Long)parent.getProperty(LAST_CHILD_ID, 0L);
            if (lastChildId != 0) {
                Node node = graphDb.getNodeById(lastChildId);
                if (node != null) {
                    insertChild(parent, child, node, DatasetRelationTypes.NEXT);
                }
            } else {
                // try to look up last child
                Node node = getLastChild(parent);
                if (node == null) {
                    // parent has no children
                    insertChild(parent, child, parent, DatasetRelationTypes.CHILD);
                } else {
                    // parent has children
                    insertChild(parent, child, node, DatasetRelationTypes.NEXT);
                }
            }
        }
        return child;
    }

    /**
     * @param parent
     * @param child
     */
    private void updateProperties(Node parent, Node child) {
        LOGGER.debug("start updateProperties(Node parent, Node child)");
        Transaction tx = graphDb.beginTx();
        try {
            parent.setProperty(LAST_CHILD_ID, child.getId());
            child.setProperty(PARENT_ID, parent.getId());
            tx.success();
        } finally {
            tx.finish();
        }
        LOGGER.debug("finish updateProperties(Node parent, Node child)");
    }

    /**
     * @param parent
     * @param child
     * @param linkTo
     * @param relationship
     */
    private void insertChild(Node parent, Node child, Node linkTo, RelationshipType relationship) throws DatabaseException {
        LOGGER.debug("start insertChild(Node parent, Node child, Node linkTo, RelationshipType relationship)");
        tx = graphDb.beginTx();
        try {
            linkTo.createRelationshipTo(child, relationship);
            updateProperties(parent, child);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not add child", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        LOGGER.debug("finish insertChild(Node parent, Node child, Node linkTo, RelationshipType relationship)");
    }

    /**
     * Finds parent node for the defined child
     * 
     * @param child
     * @return - the parent node, or <code>null</code>, if it wasn't found
     */
    public Node getParent(Node child) throws DatabaseException {
        LOGGER.debug("start getParent(Node child)");
        // validate parameters
        if (child == null) {
            throw new IllegalArgumentException("child is null");
        }

        // check if parent_id is set
        long parent_id = (Long)child.getProperty(NewDatasetService.PARENT_ID, 0L);
        if (parent_id > 0) {
            return graphDb.getNodeById(parent_id);
        }
        // else traverse database to find parent node
        TraversalDescription tr = getChildrenChainTraversalDescription().relationships(DatasetRelationTypes.NEXT,
                Direction.INCOMING).order(Traversal.postorderDepthFirst());
        Iterable<Node> nodes = tr.traverse(child).nodes();
        for (Node node : nodes) {
            Node parent = getNextNode(node, DatasetRelationTypes.CHILD, Direction.INCOMING);
            if (parent == null) {
                return null;
            }
            tx = graphDb.beginTx();
            try {
                child.setProperty(PARENT_ID, parent.getId());
                tx.success();
            } catch (Exception e) {
                LOGGER.error("Could not update child", e);
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
            return parent;
        }
        return null;
    }

    /**
     * Finds last child in the defined arent's children chain
     * 
     * @param parent
     * @return - last child node, or <code>null</code>, if it wasn't found
     */
    public Node getLastChild(Node parent) throws DatabaseException {
        LOGGER.debug("start getLastChild(Node parent)");
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        // check if last_child_id is set
        long last_child_id = (Long)parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L);
        if (last_child_id > 0) {
            return graphDb.getNodeById(last_child_id);
        }
        // else traverse database to find last child node
        Node child = getNextNode(parent, DatasetRelationTypes.CHILD, Direction.OUTGOING);
        if (child == null) {
            return null;
        }
        TraversalDescription tr = getChildrenChainTraversalDescription().order(Traversal.postorderDepthFirst());
        Iterable<Node> nodes = tr.traverse(child).nodes();
        for (Node node : nodes) {
            tx = graphDb.beginTx();
            try {
                parent.setProperty(LAST_CHILD_ID, node.getId());
                tx.success();
            } catch (Exception e) {
                LOGGER.error("Could not update child", e);
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
            return node;
        }
        return null;
    }

    /**
     * @param parent
     * @return an <code>Iterable</code> over children in the chain
     */
    public Iterable<Node> getChildrenChainTraverser(Node parent) {
        LOGGER.debug("start getChildrenChainTraverser(Node parent)");
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        try {
            Node firstChild = getNextNode(parent, DatasetRelationTypes.CHILD, Direction.OUTGOING);
            if (firstChild != null) {
                return getChildrenChainTraversalDescription().traverse(firstChild).nodes();
            } else {
                // a work-around to return an empty traverser
                return getChildrenChainTraversalDescription().relationships(DatasetRelationTypes.NEXT, Direction.INCOMING)
                        .evaluator(Evaluators.excludeStartPosition()).traverse(parent).nodes();
            }
        } catch (DatabaseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return <code>TraversalDescription</code> to iterate over children in a chain
     */
    protected TraversalDescription getChildrenChainTraversalDescription() {
        LOGGER.debug("start getChildrenChainTraversalDescription()");
        return Traversal.description().depthFirst().relationships(DatasetRelationTypes.NEXT, Direction.OUTGOING)
                .evaluator(Evaluators.all());
    }

    private Node getNextNode(Node startNode, RelationshipType relationship, Direction direction) throws DatabaseException {
        Node result = null;

        Iterator<Relationship> rels = startNode.getRelationships(relationship, direction).iterator();
        if (rels.hasNext()) {
            result = rels.next().getOtherNode(startNode);
        }
        if (rels.hasNext()) {
            // result is ambiguous
            throw new DatabaseException("Errors exist in database structure");
        }

        return result;
    }

}
