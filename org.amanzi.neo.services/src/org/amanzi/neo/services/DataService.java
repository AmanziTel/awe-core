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
import org.amanzi.neo.services.exceptions.DublicateDatasetException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kruglik_a
 * @since 1.0.0
 */
public class DataService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(DataService.class);

    public enum DatasetRelationTypes implements RelationshipType {
        PROJECT, DATASET;
    }
    /**
     * 
     * TODO Purpose of DataService
     * <p>
     *enum of dataset types
     * </p>
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
     * 
     * TODO Purpose of DataService
     * <p>
     * enum of Drive types
     * </p>
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

    
    /**
     * TODO Purpose of DataService
     * <p>
     * this class choose from dataset nodes node by name, type and driveType
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    private class ChooseDataset implements Evaluator {

        private String name;
        private DatasetTypes type;
        private DriveTypes driveType;

        ChooseDataset(String name, DatasetTypes type) {
            this.name = name;
            this.type = type;
            this.driveType = null;
        }

        ChooseDataset(String name, DatasetTypes type, DriveTypes driveType) {
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
    private class ChooseDatasetsByType implements Evaluator {

        ChooseDatasetsByType(DatasetTypes type) {
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
    public DataService() {
        super();
    }

    /**
     * constructor for testing
     * 
     * @param service
     */
    public DataService(GraphDatabaseService service) {
        super(service);
    }
    
    /**
     * this method return TraversalDescription for Dataset nodes
     * @return
     */
    private TraversalDescription getDatasetsTraversalDescription() {
        return Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
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

        if (name == "" || name == null || type == null || projectNode == null) {
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new ChooseDataset(name, type)).traverse(projectNode);
        Iterator<Node> iter = tr.nodes().iterator();
        if (iter.hasNext()) {
            return tr.nodes().iterator().next();
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
        if (name == "" || name == null || type == null || driveType == null || projectNode == null) {
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }

        Traverser tr = getDatasetsTraversalDescription().evaluator(new ChooseDataset(name, type, driveType)).traverse(projectNode);

        if (tr.nodes().iterator().hasNext()) {
            return tr.nodes().iterator().next();
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
        if (name == null || type == null || projectNode == null || name == "") {
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }
        
        for (Node node : getDatasetsTraversalDescription().traverse(projectNode).nodes()){
            if (node.getProperty(NAME, "").equals(name)){
                throw new DublicateDatasetException();
            }
        }
        
        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {            
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
            datasetNode.setProperty(NAME, name);
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
        } finally {
            tx.finish();
        }
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
        if (name == null || type == null || driveType == null || projectNode == null || name == "") {
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }
        for (Node node : getDatasetsTraversalDescription().traverse(projectNode).nodes()){
            if (node.getProperty(NAME, "").equals(name)){
                throw new DublicateDatasetException();
            }
        }
        
        Node datasetNode = null;
        Transaction tx = graphDb.beginTx();
        try {
            datasetNode = createNode(type);
            projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
            datasetNode.setProperty(NAME, name);
            datasetNode.setProperty(DRIVE_TYPE, driveType.name());
            tx.success();

        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Could not create dataset node.", e);
        } finally {
            tx.finish();
        }
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
        if (name == null || type == null || projectNode == null || name == "") {
            throw new InvalidDatasetParameterException();
        }
        if (type != DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }
        Node datasetNode = findDataset(projectNode, name, type);
        if (datasetNode == null) {
            return createDataset(projectNode, name, type);
        }
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
        if (name == null || type == null || driveType == null || projectNode == null || name == "") {
            throw new InvalidDatasetParameterException();
        }
        if (type == DatasetTypes.NETWORK) {
            throw new DatasetTypeParameterException();
        }

        Node datasetNode = findDataset(projectNode, name, type, driveType);
        if (datasetNode == null) {
            return createDataset(projectNode, name, type, driveType);
        }
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
        if (projectNode == null)
            throw new InvalidDatasetParameterException();
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
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
        if (type == null || projectNode == null)
            throw new InvalidDatasetParameterException();
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = getDatasetsTraversalDescription().evaluator(new ChooseDatasetsByType(type)).traverse(projectNode);
        for (Node dataset : tr.nodes()) {
            datasetList.add(dataset);
        }
        return datasetList;
    }

}
