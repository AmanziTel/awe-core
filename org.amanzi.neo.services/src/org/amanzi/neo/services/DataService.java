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

import org.amanzi.neo.services.exceptions.InvalidParameterException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
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
public class DataService extends AbstractService {

    public enum DatasetRelationTypes implements RelationshipType {
        PROJECT, DATASET;
    }

    public enum DatasetTypes {
        NETWORK, OTHER;
    }

    public enum DriveTypes {
        DRIVE_TYPE_1, DRIVE_TYPE_2;
    }

    public final static String NAME = "name";
    public final static String TYPE = "type";
    public final static String DRIVE_TYPE = "drive_type";

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
     * find dataset node by name and type
     * 
     * @param name
     * @param type
     * @return datasetNode
     */
    public Node findDataset(final String name, final DatasetTypes type, Node projectNode) throws InvalidParameterException,
            DatasetTypeParameterException {

        if (name == "" || name == null || type == null || projectNode == null)
            throw new InvalidParameterException();
        if (type != DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();

        Traverser tr = Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition()).evaluator(Evaluators.toDepth(1)).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean includes = false;
                        boolean continues;
                        if (name.equals(arg0.endNode().getProperty(NAME, ""))
                                && type.name().equals(arg0.endNode().getProperty(TYPE, ""))) {
                            includes = true;
                        }
                        continues = !includes;
                        return Evaluation.of(includes, continues);
                    }
                }).traverse(projectNode);
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
    public Node findDataset(final String name, final DatasetTypes type, final DriveTypes driveType, Node projectNode)
            throws InvalidParameterException, DatasetTypeParameterException {
        if (name == "" || name == null || type == null || driveType == null || projectNode == null)
            throw new InvalidParameterException();
        if (type == DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();

        Traverser tr = Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition()).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {

                        boolean includes = false;
                        boolean continues;

                        if (arg0.endNode().getProperty(NAME, "").equals(name)
                                && arg0.endNode().getProperty(TYPE, "").equals(type.name())
                                && arg0.endNode().getProperty(DRIVE_TYPE, "").equals(driveType.name())) {
                            includes = true;
                        }
                        continues = !includes;
                        return Evaluation.of(includes, continues);
                    }
                }).traverse(projectNode);

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
    public Node createDataset(String name, DatasetTypes type, Node projectNode) throws InvalidParameterException,
            DatasetTypeParameterException {
        if (name == null || type == null || projectNode == null || name == "")
            throw new InvalidParameterException();
        if (type != DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();
        Node datasetNode = databaseService.createNode();
        projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
        datasetNode.setProperty(NAME, name);
        datasetNode.setProperty(TYPE, type.name());
        return datasetNode;
    }

    /**
     * create dataset node
     * 
     * @param name
     * @param type
     * @param driveType
     * @param projectNode
     * @return dataset node
     * @throws InvalidParameterException
     * @throws DatasetTypeParameterException
     */
    public Node createDataset(String name, DatasetTypes type, DriveTypes driveType, Node projectNode)
            throws InvalidParameterException, DatasetTypeParameterException {
        if (name == null || type == null || driveType == null || projectNode == null || name == "")
            throw new InvalidParameterException();
        if (type == DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();
        Node datasetNode = databaseService.createNode();
        projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
        datasetNode.setProperty(NAME, name);
        datasetNode.setProperty(TYPE, type.name());
        datasetNode.setProperty(DRIVE_TYPE, driveType.name());
        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name and type, and if not found then create dataset
     * node
     * 
     * @param name
     * @param type
     * @param projectNode
     * @return
     * @throws InvalidParameterException
     * @throws DatasetTypeParameterException
     */
    public Node getDataset(String name, DatasetTypes type, Node projectNode) throws InvalidParameterException,
            DatasetTypeParameterException {
        if (name == null || type == null || projectNode == null || name == "")
            throw new InvalidParameterException();
        if (type != DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();
        Node datasetNode = findDataset(name, type, projectNode);
        if (datasetNode == null)
            return createDataset(name, type, projectNode);

        return datasetNode;
    }

    /**
     * get dataset node - find dataset node by name, type and driveType, and if not found then
     * create dataset
     * 
     * @param name
     * @param type
     * @param driveType
     * @param projectNode
     * @return
     * @throws InvalidParameterException
     * @throws DatasetTypeParameterException
     */
    public Node getDataset(String name, DatasetTypes type, DriveTypes driveType, Node projectNode)
            throws InvalidParameterException, DatasetTypeParameterException {
        if (name == null || type == null || driveType == null || projectNode == null || name == "")
            throw new InvalidParameterException();
        if (type == DatasetTypes.NETWORK)
            throw new DatasetTypeParameterException();

        Node datasetNode = findDataset(name, type, driveType, projectNode);
        if (datasetNode == null)
            return createDataset(name, type, driveType, projectNode);

        return datasetNode;
    }

    /**
     * this method find all dataset nodes in project
     * 
     * @param projectNode
     * @return List<Node> list of dataset nodes
     * @throws InvalidParameterException
     */
    public List<Node> findAllDatasets(Node projectNode) throws InvalidParameterException {
        if (projectNode == null)
            throw new InvalidParameterException();
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition()).traverse(projectNode);
        for (Node dataset : tr.nodes())
            datasetList.add(dataset);
        return datasetList;
    }

    /**
     * this method find all dataset nodes by type in project
     * 
     * @param type
     * @param projectNode
     * @return List<Node> list of dataset nodes
     * @throws InvalidParameterException
     */
    public List<Node> findAllDatasetsByType(final DatasetTypes type, Node projectNode) throws InvalidParameterException {
        if (type == null || projectNode == null)
            throw new InvalidParameterException();
        List<Node> datasetList = new ArrayList<Node>();
        Traverser tr = Traversal.description().relationships(DatasetRelationTypes.DATASET, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition()).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean includes = false;
                        if (arg0.endNode().getProperty(TYPE, "").equals(type.name()))
                            includes = true;
                        return Evaluation.ofIncludes(includes);
                    }
                }).traverse(projectNode);
        for (Node dataset : tr.nodes())
            datasetList.add(dataset);
        return datasetList;
    }

}
