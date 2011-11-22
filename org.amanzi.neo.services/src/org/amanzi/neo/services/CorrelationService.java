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

import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Correlation service handles calls to database referring correlations.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class CorrelationService extends NewAbstractService {

    /** TraversalDescription ALL_CORRELATED_TRAVERSAL_DESCRIPTION field */
    protected static final TraversalDescription ALL_CORRELATED_TRAVERSAL_DESCRIPTION = Traversal.description().depthFirst()
            .relationships(DatasetRelationTypes.CHILD, Direction.OUTGOING)
            .relationships(DatasetRelationTypes.NEXT, Direction.OUTGOING)
            .relationships(Correlations.CORRELATED, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

    /** TraversalDescription CORRELATED_TRAVERSAL_DESCRIPTION field */
    protected static final TraversalDescription CORRELATED_TRAVERSAL_DESCRIPTION = Traversal.description().breadthFirst()
            .relationships(Correlations.CORRELATED, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

    /** TraversalDescription CORRELATED_SECTORS_TRAVERSAL_DESCRIPTION field */
    protected static final TraversalDescription CORRELATED_SECTORS_TRAVERSAL_DESCRIPTION = Traversal.description().depthFirst()
            .relationships(DatasetRelationTypes.CHILD, Direction.OUTGOING)
            .relationships(DatasetRelationTypes.NEXT, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition())
            .relationships(Correlations.CORRELATED, Direction.BOTH).evaluator(new FilterNodesByType(NetworkElementNodeType.SECTOR));

    /** TraversalDescription CORRELATED_DATASETS_TRAVERSAL_DESCRIPTION field */
    public static final TraversalDescription CORRELATED_DATASETS_TRAVERSAL_DESCRIPTION = Traversal.description().breadthFirst()
            .relationships(Correlations.CORRELATED, Direction.OUTGOING).relationships(Correlations.CORRELATION, Direction.INCOMING);

    private static Logger LOGGER = Logger.getLogger(CorrelationService.class);

    private NewDatasetService datasetService = NeoServiceFactory.getInstance().getNewDatasetService();

    public enum Correlations implements RelationshipType {
        CORRELATION, CORRELATED
    }

    public enum CorrelationNodeTypes implements INodeType {
        CORRELATION, PROXY;

        static {
            NodeTypeManager.registerNodeType(CorrelationNodeTypes.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
        }
    }

    public CorrelationService() {
        super();
        LOGGER.info("Create CorrelationService");
    }

    // createCorrealtion (Network, Dataset <Drive/Counters>)
    public Node createCorrelation(Node network, Node dataset) throws DatabaseException {
        // validate params
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset is null.");
        }
        LOGGER.info("createCorrelation(" + network.getId() + ", " + dataset.getId() + ")");

        Node result = findCorrelationRoot(network, dataset);
        if (result == null) {
            Transaction tx = graphDb.beginTx();
            try {
                result = getCorrelationRoot(network);
                Node dsRoot = getCorrelationRoot(dataset);
                Relationship rel = dsRoot.getSingleRelationship(Correlations.CORRELATION, Direction.INCOMING);
                rel.setProperty(NETWORK_ID, network.getId());
                result.createRelationshipTo(dsRoot, Correlations.CORRELATED);
                tx.success();
            } catch (Exception e) {
                LOGGER.error("Could not create correlation.", e);
                tx.failure();
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        }
        return result;
    }

    /**
     * @param network
     * @param dataset
     * @return
     */
    protected Node findCorrelationRoot(Node network, Node dataset) throws DatabaseException {
        LOGGER.info("findCorrelationRoot(" + network.getId() + ", " + dataset.getId() + ")");
        Node result = null;
        Node root = getCorrelationRoot(network);
        Node dsRoot = getCorrelationRoot(dataset);
        for (Relationship rel : root.getRelationships(Correlations.CORRELATED, Direction.OUTGOING)) {
            if (rel.getEndNode().equals(dsRoot)) {
                result = root;
            }
        }
        return result;
    }

    protected Node getSectorProxy(Node network, Node sector) {
        LOGGER.info("getSectorProxy(" + network.getId() + ", " + sector.getId() + ")");

        Node result = null;
        Relationship rel = sector.getSingleRelationship(Correlations.CORRELATED, Direction.OUTGOING);
        if (rel != null) {
            result = rel.getEndNode();
        }
        return result;
    }

    // addCorerlationNodes(Sector, M)
    public Node addCorrelationNodes(Node network, Node sector, Node dataset, Node measurement) throws DatabaseException {

        // validate parameters
        if (sector == null) {
            throw new IllegalArgumentException("Sector is null");
        }
        if (measurement == null) {
            throw new IllegalArgumentException("Measurement is null");
        }
        LOGGER.info("addCorrelationNodes(" + network.getId() + ", " + sector.getId() + ", " + dataset.getId() + ", "
                + measurement.getId() + ")");

        Node result = findCorrelation(sector, measurement);

        if (result == null) {
            Transaction tx = graphDb.beginTx();
            try {
                result = getSectorProxy(network, sector);
                if (result == null) {
                    result = createNode(CorrelationNodeTypes.PROXY);
                    sector.createRelationshipTo(result, Correlations.CORRELATED);
                    datasetService.addChild(getCorrelationRoot(network), result, null);
                }
                Relationship rel = result.createRelationshipTo(measurement, Correlations.CORRELATED);
                rel.setProperty(DATASET_ID, dataset.getId());
                rel.setProperty(NETWORK_ID, network.getId());
                tx.success();
            } catch (Exception e) {
                LOGGER.error("Could not create correlation", e);
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        }
        return result;
    }

    protected Node findCorrelation(Node sector, Node measurement) {
        LOGGER.info("findCorrelation(" + sector.getId() + ", " + measurement.getId() + ")");

        Node result = null;
        for (Relationship rel : sector.getRelationships(Correlations.CORRELATED, Direction.OUTGOING)) {
            Node proxy = rel.getEndNode();
            for (Relationship r : proxy.getRelationships(Correlations.CORRELATED, Direction.OUTGOING)) {
                if (r.getEndNode().equals(measurement)) {
                    result = proxy;
                    break;
                }
            }
        }
        return result;
    }

    // getCorrelatedSector(M)
    public Node getCorrelatedSector(Node measurement, Node network) {
        // validate parameter
        if (measurement == null) {
            throw new IllegalArgumentException("Measurement is null.");
        }
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        LOGGER.info("getCorrelatedSector(" + measurement.getId() + ", " + network.getId() + ")");

        Node proxy = getMeasurementProxy(measurement, network);
        if (proxy == null) {
            return null;
        } else {
            return proxy.getSingleRelationship(Correlations.CORRELATED, Direction.INCOMING).getStartNode();
        }
    }

    protected Node getMeasurementProxy(Node measurement, Node network) {
        LOGGER.info("getMeasurementProxy(" + measurement.getId() + ", " + network.getId() + ")");

        Node result = null;
        for (Relationship rel : measurement.getRelationships(Correlations.CORRELATED, Direction.INCOMING)) {
            if (rel.getProperty(NETWORK_ID, 0L).equals(network.getId())) {
                result = rel.getStartNode();
            }
        }
        return result;
    }

    // getCorrelatedNodes(Sector)
    public Iterable<Node> getCorrelatedNodes(Node network, Node sector, Node dataset) {
        // validate parameters
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        if (sector == null) {
            throw new IllegalArgumentException("Sector is null.");
        }
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset is null.");
        }
        LOGGER.info("getCorrelatedNodes(" + network.getId() + ", " + sector.getId() + ", " + dataset.getId() + ")");

        Node proxy = getSectorProxy(network, sector);
        if (proxy != null) {
            // add relationship property evaluator
            return CORRELATED_TRAVERSAL_DESCRIPTION
                    .evaluator(
                            new HasRelationshipPropertyValueEvaluator(DATASET_ID, dataset.getId(), Correlations.CORRELATED,
                                    Direction.INCOMING))
                    .evaluator(
                            new HasRelationshipPropertyValueEvaluator(NETWORK_ID, network.getId(), Correlations.CORRELATED,
                                    Direction.INCOMING)).traverse(proxy).nodes();
        } else {
            return NewDatasetService.EMPTY_TRAVERSAL_DESCRIPTION.traverse(sector).nodes();
        }
    }

    // getAllCorrelatedNodes(Network, Dataset)
    public Iterable<Node> getAllCorrelatedNodes(Node network, Node dataset) throws DatabaseException {
        LOGGER.info("getAllCorrelatedNodes(" + network.getId() + ", " + dataset.getId() + ")");
        Node corRoot = getCorrelationRoot(network);
        return ALL_CORRELATED_TRAVERSAL_DESCRIPTION
                .evaluator(
                        new HasRelationshipPropertyValueEvaluator(DATASET_ID, dataset.getId(), Correlations.CORRELATED,
                                Direction.INCOMING))
                .evaluator(
                        new HasRelationshipPropertyValueEvaluator(NETWORK_ID, network.getId(), Correlations.CORRELATED,
                                Direction.INCOMING)).traverse(corRoot).nodes();
    }

    public Iterable<Node> getAllCorrelatedSectors(Node network, Node dataset) throws DatabaseException {
        LOGGER.info("getAllCorrelatedSectors(" + network.getId() + ", " + dataset.getId() + ")");

        Node corRoot = getCorrelationRoot(network);
        return CORRELATED_SECTORS_TRAVERSAL_DESCRIPTION
                .evaluator(
                        new HasRelationshipPropertyValueEvaluator(DATASET_ID, dataset.getId(), Correlations.CORRELATED,
                                Direction.INCOMING)).traverse(corRoot).nodes();
    }

    /**
     * @param network
     * @return
     */
    public Node getCorrelationRoot(Node network) throws DatabaseException {
        // validate parameters
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        LOGGER.info("getCorrelationRoot(" + network.getId() + ")");

        Node result = null;
        Relationship rel = network.getSingleRelationship(Correlations.CORRELATION, Direction.OUTGOING);
        if (rel != null) {
            result = rel.getEndNode();
        } else {
            Transaction tx = graphDb.beginTx();
            try {
                result = createNode(CorrelationNodeTypes.CORRELATION);
                network.createRelationshipTo(result, Correlations.CORRELATION);
                tx.success();
            } catch (Exception e) {
                LOGGER.error("Error on creating root correlation node", e);
                tx.failure();
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }

        }
        return result;
    }

    public Iterable<Node> getCorrelatedDatasets(Node network) throws DatabaseException {
        // validate
        if (network == null) {
            throw new IllegalArgumentException("Network is null.");
        }
        LOGGER.info("getCorrelatedDatasets(" + network.getId() + ")");

        return CORRELATED_DATASETS_TRAVERSAL_DESCRIPTION
                .evaluator(
                        new HasRelationshipPropertyValueEvaluator(NETWORK_ID, network.getId(), Correlations.CORRELATION,
                                Direction.OUTGOING)).traverse(getCorrelationRoot(network)).nodes();
    }

    public Iterable<Node> getCorrelatedNetworks(Node dataset) throws DatabaseException {
        // validate
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset is null.");
        }
        LOGGER.info("getCorrelatedNetworks(" + dataset.getId() + ")");

        return CORRELATED_DATASETS_TRAVERSAL_DESCRIPTION.relationships(Correlations.CORRELATED, Direction.INCOMING)
                .evaluator(new FilterNodesByType(DatasetTypes.NETWORK)).evaluator(Evaluators.atDepth(2))
                .traverse(getCorrelationRoot(dataset)).nodes();
    }

    /**
     * <p>
     * This evaluator includes nodes in result if they have the defined relationship with the
     * defined property set. This is used in correlation, when a relationship holds information
     * about the dataset it belongs to.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public class HasRelationshipPropertyValueEvaluator implements Evaluator {

        private String property;
        private Object value;
        private RelationshipType relationship;
        private Direction direction;

        public HasRelationshipPropertyValueEvaluator(String property, Object value, RelationshipType relationship,
                Direction direction) {
            this.property = property;
            this.value = value;
            this.relationship = relationship;
            this.direction = direction;
        }

        @Override
        public Evaluation evaluate(Path path) {
            boolean includes = false;
            Node node = path.endNode();
            for (Relationship rel : node.getRelationships(relationship, direction)) {
                if (value.equals(rel.getProperty(property, null))) {
                    includes = true;
                }
            }
            return Evaluation.ofIncludes(includes);
        }
    }

    /**
     * @param datasetService The datasetService to set.
     */
    public void setDatasetService(NewDatasetService datasetService) {
        this.datasetService = datasetService;
    }

}
