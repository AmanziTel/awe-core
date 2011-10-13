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

package org.amanzi.neo.services.synonyms;

import java.util.HashMap;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * Service to work with Export Synonyms
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class ExportSynonymsService extends NewAbstractService {

    private static final Logger LOGGER = Logger.getLogger(ExportSynonymsService.class);

    /*
     * Constant for node property to show type of Export Synonyms
     */
    static final String EXPORT_SYNONYMS_TYPE = "export_synonyms_type";

    /*
     * Separator between node type and property name
     */
    private static final String SEPARATOR = ".";

    /**
     * Type of Export Synonyms
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public enum ExportSynonymType {
        /**
         * Global Synonyms
         */
        GLOBAL,

        /**
         * Dataset Synonym
         */
        DATASET;
    }

    /**
     * Default constructore. Get GraphDBService from DatabaseManager
     */
    public ExportSynonymsService() {
        super();
    }

    /**
     * Constructor for Testing. Initializes with provided GraphDBService
     */
    public ExportSynonymsService(GraphDatabaseService dbService) {
        super(dbService);
    }

    /**
     * Wrapper for all Export Synonyms of Dataset
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static class ExportSynonyms {

        /*
         * Map with raw synonyms
         */
        HashMap<String, String> rawSynonyms = new HashMap<String, String>();

        /*
         * Type of ExportSynonyms
         */
        private ExportSynonymType synonymsType;

        /**
         * Internal constructor for ExportSynonyms Will load data from Node to map with RawSynonyms
         * 
         * @param synonymsNode
         */
        private ExportSynonyms(Node synonymsNode) {
            for (String propertyKey : synonymsNode.getPropertyKeys()) {
                if (!propertyKey.equals(TYPE) && !propertyKey.equals(EXPORT_SYNONYMS_TYPE)) {
                    rawSynonyms.put(propertyKey, synonymsNode.getProperty(propertyKey).toString());
                }
            }
        }

        /**
         * Public constructor to create new Synonyms
         * 
         * @param synonymsType type of new Synonyms
         */
        public ExportSynonyms(ExportSynonymType synonymsType) {
            this.synonymsType = synonymsType;
        }

        /**
         * Returns Synonym of property
         * 
         * @param nodeType node type of Property
         * @param propertyName name of Property
         * @return synonym
         */
        public String getSynonym(INodeType nodeType, String propertyName) {
            String key = nodeType.getId() + SEPARATOR + propertyName;
            return rawSynonyms.get(key);
        }

        /**
         * Add new Synonym to ExportSynonyms
         * 
         * @param nodeType nodeType of Property
         * @param propertyName name of Property
         * @param synonym synonym of Property
         */
        public void addSynonym(INodeType nodeType, String propertyName, String synonym) {
            rawSynonyms.put(nodeType.getId() + SEPARATOR + propertyName, synonym);
        }

    }

    /**
     * Relationship Types for Export Synonyms Database Structure
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum ExportSynonymsRelationshipTypes implements RelationshipType {
        GLOBAL_SYNONYMS, DATASET_SYNONYMS;
    }

    public static enum ExportSynonymsNodeType implements INodeType {
        EXPORT_SYNONYMS;

        public String getId() {
            return name().toLowerCase();
        }
    }

    /**
     * Tries to find existing Global Export Synonyms If it's not found creates new one
     * 
     * @return Global Export Synonyms
     */
    public ExportSynonyms getGlobalExportSynonyms() throws DatabaseException {
        LOGGER.debug("start getGlobalExportSynonyms()");

        Node exportSynonymsNode = getExportSynonymsNode(graphDb.getReferenceNode(),
                ExportSynonymsRelationshipTypes.GLOBAL_SYNONYMS, ExportSynonymType.GLOBAL);

        LOGGER.debug("finish getGlobalExportSynonyms()");
        return new ExportSynonyms(exportSynonymsNode);
    }

    /**
     * Searches for Export Synonyms Node in Database Create new one if nothing found
     * 
     * @param rootNode root Node of Export Synonyms
     * @param relationshipType Relationship Type to search
     * @param synonymType type of Synonyms
     * @return node for Export Synonyms
     * @throws DatabaseException
     */
    private Node getExportSynonymsNode(Node rootNode, ExportSynonymsRelationshipTypes relationshipType,
            ExportSynonymType synonymType) throws DatabaseException {
        LOGGER.debug("start getExportSynonymsNode(<" + rootNode + ">, <" + relationshipType + ">, <" + synonymType + ">)");

        Node exportSynonymsNode = null;

        Relationship exportSynonymsRelationship = rootNode.getSingleRelationship(relationshipType, Direction.OUTGOING);
        if (exportSynonymsRelationship == null) {
            LOGGER.info("No ExportSynonyms node. Create new one");

            Transaction tx = graphDb.beginTx();
            try {
                exportSynonymsNode = createNode(ExportSynonymsNodeType.EXPORT_SYNONYMS);
                exportSynonymsNode.setProperty(EXPORT_SYNONYMS_TYPE, synonymType.name());
                rootNode.createRelationshipTo(exportSynonymsNode, relationshipType);
                tx.success();
            } catch (Exception e) {
                tx.failure();
                LOGGER.error("Error on creating new Export Synonyms node", e);
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        } else {
            // check founded node
            exportSynonymsNode = exportSynonymsRelationship.getEndNode();

            if (!getNodeType(exportSynonymsNode).equals(ExportSynonymsNodeType.EXPORT_SYNONYMS.getId())) {
                LOGGER.error("Database Error - node by Export Synonyms link have incorrect type");
                throw new DatabaseException("Node <" + exportSynonymsNode + "> by incoming " + exportSynonymsRelationship.getType()
                        + " link have incorrect type <" + getNodeType(exportSynonymsNode) + ">");
            }

            // check type of Synonyms
            if (!exportSynonymsNode.getProperty(EXPORT_SYNONYMS_TYPE, StringUtils.EMPTY).equals(synonymType.name())) {
                LOGGER.error("Database Error - Export Synonyms have incorrect Synonyms Type");
                throw new DatabaseException("Node <" + exportSynonymsNode + "> by incoming " + exportSynonymsRelationship.getType()
                        + " link have incorrect Synonyms Type <"
                        + exportSynonymsNode.getProperty(EXPORT_SYNONYMS_TYPE, StringUtils.EMPTY) + ">");
            }
        }

        LOGGER.debug("finish getExportSynonymsNode()");

        return exportSynonymsNode;
    }

    /**
     * Tries to find Global Export Synonyms for current Dataset If it's not found creates new one
     * 
     * @return Dataset Export Synonyms
     */
    public ExportSynonyms getDatasetExportSynonyms(Node datasetNode) throws DatabaseException {
        LOGGER.debug("start getDatasetExportSynonyms(<" + datasetNode + ">)");

        if (datasetNode == null) {
            LOGGER.error("datasetNode of Export Synonyms cannot be null");
            throw new IllegalArgumentException("DatasetNode of ExportSynonyms cannot be null");
        }

        Node exportSynonymsNode = getExportSynonymsNode(datasetNode, ExportSynonymsRelationshipTypes.DATASET_SYNONYMS,
                ExportSynonymType.DATASET);

        LOGGER.debug("finish getDatasetExportSynonyms()");
        return new ExportSynonyms(exportSynonymsNode);
    }

    /**
     * save export synonyms into existing synonyms node.
     * 
     * @param rootNode
     * @throws DatabaseException
     */
    public void saveExportSynonyms(Node rootNode, ExportSynonyms synonyms) throws DatabaseException {
        Node exportDatasetSynonymsNode = getExportSynonymsNode(rootNode, ExportSynonymsRelationshipTypes.DATASET_SYNONYMS,
                ExportSynonymType.DATASET);
        for (String key : synonyms.rawSynonyms.keySet()) {
            exportDatasetSynonymsNode.setProperty(key, synonyms.rawSynonyms.get(key));
        }
    }
}
