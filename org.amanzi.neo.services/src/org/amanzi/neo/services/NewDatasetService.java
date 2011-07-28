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
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
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
    public List<Node> findAllDatasets(){
        LOGGER.debug("start findAllDatasets()");
        List<Node> datasetList = new ArrayList<Node>();
        TraversalDescription allProjects = new ProjectService().getProjectTraversalDescription();
        
        for(Node projectNode : allProjects.traverse(graphDb.getReferenceNode()).nodes()){
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
    public List<Node> findAllDatasetsByType(DatasetTypes type) throws InvalidDatasetParameterException{
        LOGGER.debug("start findAllDatasetsByType()");
        
        if (type == null) {
            LOGGER.error("InvalidDatasetParameterException: parameter type = null");
            throw new InvalidDatasetParameterException(INeoConstants.PROPERTY_TYPE_NAME, type);
        }
        
        List<Node> datasetList = new ArrayList<Node>();
        TraversalDescription allProjects = new ProjectService().getProjectTraversalDescription();
        
        for(Node projectNode : allProjects.traverse(graphDb.getReferenceNode()).nodes()){
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
     * Adds <code>child</code> to the end of <code>parent</code>'s children chain. If
     * 
     * @param parent - parent node of a chain
     * @param child - the node to be added to the end of the chain
     * @param lastChild - current last child of the chain
     * @return - the added node or <code>null</code>, if the node could not be added
     */
    public Node addChild(Node parent, Node child, Node lastChild) {
        return null;
    }

    /**
     * Finds parent node for the defined child
     * 
     * @param child
     * @return - the parent node, or <code>null</code>, if it wasn't found
     */
    public Node getParent(Node child) {
        return null;
    }

    /**
     * Finds last child in the defined arent's children chain
     * 
     * @param parent
     * @return - last child node, or <code>null</code>, if it wasn't found
     */
    public Node getLastChild(Node parent) {
        return null;
    }

    /**
     * @param parent
     * @return an <code>Iterable</code> over children in the chain
     */
    public Iterable<Node> getChildrenChainTraverser(Node parent) {
        return null;
    }

    /**
     * @return <code>TraversalDescription</code> to iterate over children in a chain
     */
    protected TraversalDescription getChildrenChainTraversalDescription() {
        return null;
    }

}
