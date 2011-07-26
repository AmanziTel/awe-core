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
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DublicateDatasetException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kruglik_a
 * @since 1.0.0
 */
/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author grigoreva_a
 * @since 1.0.0
 */
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewDatasetService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(NewDatasetService.class);

    public enum DatasetRelationshipTypes implements RelationshipType {
        PROJECT, DATASET, CHILD, NEXT;
    }

    /**
     * TODO Purpose of DataService
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
     * TODO Purpose of DataService
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
     * constants for dataset property name
     */
    public final static String NAME = "name";
    public final static String TYPE = "type";
    public final static String DRIVE_TYPE = "drive_type";

    public final static String PROPERTY_LAST_CHILD_ID_NAME = "last_child_id";
    public final static String PROPERTY_PARENT_ID_NAME = "parent_id";

    /**
     * TODO Purpose of DataService
     * <p>
     * this class choose from dataset nodes node by name, type and driveType
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    private class FilterDataset implements Evaluator {

        private String name;
        private DatasetTypes type;
        private DriveTypes driveType;

        FilterDataset(String name, DatasetTypes type) {
            this.name = name;
            this.type = type;
            this.driveType = null;
        }

        FilterDataset(String name, DatasetTypes type, DriveTypes driveType) {
            this.name = name;
            this.type = type;
            this.driveType = driveType;
        }

        @Override
        public Evaluation evaluate(Path arg0) {
            boolean includes = false;
            boolean continues;
            if (name.equals(arg0.endNode().getProperty(NAME, "")) && type.getId().equals(arg0.endNode().getProperty(TYPE, ""))
                    && (driveType == null || driveType.name().equals(arg0.endNode().getProperty(DRIVE_TYPE, "")))) {
                includes = true;
            }
            continues = !includes;
            return Evaluation.of(includes, continues);
        }

    }

    /**
     * TODO Purpose of DataService
     * <p>
     * this class choose nodes by type from dataset nodes
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    private class FilterDatasetsByType implements Evaluator {

        FilterDatasetsByType(DatasetTypes type) {
            this.type = type;
        }

        private DatasetTypes type;

        @Override
        public Evaluation evaluate(Path arg0) {
            boolean includes = false;
            if (arg0.endNode().getProperty(TYPE, "").equals(type.name()))
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
     * @return
     */
    private TraversalDescription getDatasetsTraversalDescription() {
        return Traversal.description().relationships(DatasetRelationshipTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition()).evaluator(Evaluators.toDepth(1));
    }

    /**
     * find dataset node by name and type
     * 
     * @param name
     * @param type
     * @return datasetNode
     */
    public Node findDataset(Node projectNode, final String name, final DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException {
        LOGGER.info("start findDataset(Node projectNode, String name, DatasetTypes type)");
        if (name == "" || name == null || type == null || projectNode == null) {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();

        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDataset(name, type)).traverse(projectNode);
        Iterator<Node> iter = tr.nodes().iterator();
        LOGGER.info("finish findDataset(Node projectNode, String name, DatasetTypes type)");
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    /**
     * find dataset node by name, type and driveType
     * 
     * @param name
     * @param type
     * @return datasetNode
     */
    public Node findDataset(Node projectNode, final String name, final DatasetTypes type, final DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException {
        LOGGER.info("start findDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (name == "" || name == null || type == null || driveType == null || projectNode == null) {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();
        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDataset(name, type, driveType)).traverse(projectNode);
        Iterator<Node> iter = tr.nodes().iterator();
        LOGGER.info("finish findDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    /**
     * create dataset node
     * 
     * @param name
     * @param type
     * @return dataset node
     * @throws DatasetTypeParameterException
     */
    public Node createDataset(Node projectNode, String name, DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DublicateDatasetException {
        LOGGER.info("start createDataset(Node projectNode, String name, DatasetTypes type)");
        if (name == null || type == null || projectNode == null || name == "") {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();
        }

        for (Node node : getDatasetsTraversalDescription().traverse(projectNode).nodes()) {
            if (node.getProperty(NAME, "").equals(name)) {
                LOGGER.error("Dublicate Dataset exception");
                throw new DublicateDatasetException();
            }
        }

        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationshipTypes.DATASET);
            datasetNode.setProperty(NAME, name);
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
        } finally {
            tx.finish();
        }
        LOGGER.info("finish createDataset(Node projectNode, String name, DatasetTypes type)");
        return datasetNode;
    }

    /**
     * create dataset node
     * 
     * @param projectNode
     * @param name
     * @param type
     * @param driveType
     * @return dataset node
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DublicateDatasetException
     */
    public Node createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DublicateDatasetException {
        LOGGER.info("start createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        if (name == null || type == null || driveType == null || projectNode == null || name == "") {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();
        }
        for (Node node : getDatasetsTraversalDescription().traverse(projectNode).nodes()) {
            if (node.getProperty(NAME, "").equals(name)) {
                LOGGER.error("Dublicate Dataset exception");
                throw new DublicateDatasetException();
            }
        }

        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationshipTypes.DATASET);
            datasetNode.setProperty(NAME, name);
            datasetNode.setProperty(DRIVE_TYPE, driveType.name());
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
        } finally {
            tx.finish();
        }
        LOGGER.info("finish createDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name and type, and if not found then create dataset
     * node
     * 
     * @param projectNode
     * @param name
     * @param type
     * @return
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DublicateDatasetException
     */
    public Node getDataset(Node projectNode, String name, DatasetTypes type) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DublicateDatasetException {
        LOGGER.info("start getDataset(Node projectNode, String name, DatasetTypes type)");
        if (name == null || type == null || projectNode == null || name == "") {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();
        }
        Node datasetNode = findDataset(projectNode, name, type);
        if (datasetNode == null) {
            datasetNode = createDataset(projectNode, name, type);
        }
        LOGGER.info("finish getDataset(Node projectNode, String name, DatasetTypes type)");
        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name, type and driveType, and if not found then
     * create dataset
     * 
     * @param projectNode
     * @param name
     * @param type
     * @param driveType
     * @return
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DublicateDatasetException
     */
    public Node getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)
            throws InvalidDatasetParameterException, DatasetTypeParameterException, DublicateDatasetException {
        LOGGER.info("start getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");

        if (name == null || type == null || driveType == null || projectNode == null || name == "") {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            LOGGER.error("Dataset type parameter exception");
            throw new DatasetTypeParameterException();
        }

        Node datasetNode = findDataset(projectNode, name, type, driveType);
        if (datasetNode == null) {
            datasetNode = createDataset(projectNode, name, type, driveType);
        }
        LOGGER.info("finish getDataset(Node projectNode, String name, DatasetTypes type, DriveTypes driveType)");
        return datasetNode;
    }

    /**
     * this method find all dataset nodes in project
     * 
     * @param projectNode
     * @return List<Node> list of dataset nodes
     * @throws InvalidDatasetParameterException
     */
    public List<Node> findAllDatasets(Node projectNode) throws InvalidDatasetParameterException {
        LOGGER.info("start findAllDatasets(Node projectNode)");
        if (projectNode == null) {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
        LOGGER.info("finish findAllDatasets(Node projectNode)");
        return datasetList;
    }

    /**
     * this method find all dataset nodes by type in project
     * 
     * @param projectNode
     * @param type
     * @return List<Node> list of dataset nodes
     * @throws InvalidDatasetParameterException
     */
    public List<Node> findAllDatasetsByType(Node projectNode, final DatasetTypes type) throws InvalidDatasetParameterException {
        LOGGER.info("start findAllDatasetsByType(Node projectNode, DatasetTypes type)");
        if (type == null || projectNode == null) {
            LOGGER.error("Invalid dataset parameter");
            throw new InvalidDatasetParameterException();
        }
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().evaluator(new FilterDatasetsByType(type)).traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
        LOGGER.info("finish findAllDatasetsByType(Node projectNode, DatasetTypes type)");
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
     * @param child
     * @return - the parent node, or <code>null</code>, if it wasn't found
     */
    public Node getParent(Node child) {
        return null;
    }

    /**
     * Finds last child in the defined arent's children chain
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
