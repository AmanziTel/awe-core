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

package org.amanzi.awe.gpeh;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.neighbours.GpehReportUtil;
import org.amanzi.awe.neighbours.gpeh.RrcMeasurement;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.internal.StatisticHandler;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * Model for working with GPEH statistics
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class GpehStatisticModel {

    /** The dataset. */
    @SuppressWarnings("unused")
    private final Node dataset;
    protected static StatisticHandler statistic;
    /** The statistic node. */
    private final Node statisticNode;

    /** The database service. */
    private final GraphDatabaseService databaseService;

    /** The time stat nodes. */
    private final Map<Node, NBapCache> timeStatNodes = new HashMap<Node, NBapCache>();

    /** The intra fr. */
    private final Map<Node, RRCCache> rrcCache = new HashMap<Node, RRCCache>();

    /** The table root. */
    private final Map<GpehRelationType, Node> tableRoot = new HashMap<GpehStatisticModel.GpehRelationType, Node>();

    /**
     * Instantiates a new gpeh statistic model.
     * 
     * @param dataset the dataset
     * @param statisticNode the statistic node
     * @param databaseService the database service
     */
    public GpehStatisticModel(Node dataset, Node statisticNode, GraphDatabaseService databaseService) {
        this.dataset = dataset;
        this.statisticNode = statisticNode;
        this.databaseService = databaseService;
    }

    /**
     * Process nbap event.
     * 
     * @param dataElement the data element
     * @param timestamp the actuual timestamp
     */
    public void processNbapEvent(HashMap<String, Object> dataElement, Long timestamp) {
        // Set<Node> activeSet = getActiveSet(eventNode);
        Integer type = (Integer)dataElement.get("EVENT_PARAM_MEASURED_ENTITY");
        if (type == null || type < 2 || type > 5) {
            return;
        }
        Integer value = (Integer)dataElement.get("EVENT_PARAM_MEASURED_VALUE");
        if (value == null) {
            return;
        }
        Integer ci = (Integer)dataElement.get("EVENT_PARAM_C_ID_1");
        Integer rnc = (Integer)dataElement.get("EVENT_PARAM_RNC_ID_1");
        if (ci == null || rnc == null) {
            return;
        }
        switch (type) {
        case 2:
            statType = GpehRelationType.Ul_RTWP.name();
            processUlRtwp(ci, rnc, value, timestamp);
            break;
        case 3:
            statType = GpehRelationType.TOTAL_DL_TX_POWER.name();
            processTotalDlTxPower(ci, rnc, value, timestamp);
            break;
        case 4:
            statType = GpehRelationType.R99_DL_TX_POWER.name();
            processR99DlTxPower(ci, rnc, value, timestamp);
            break;
        case 5:
            statType = GpehRelationType.HS_DL_TX_RequiredPower.name();
            processHsDlTxRequiredPower(ci, rnc, value, timestamp);
            break;
        default:
            break;
        }

    }

    /**
     * Process HsDlTxRequiredPower.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param value the value
     * @param timestamp the timestamp
     */
    private void processHsDlTxRequiredPower(Integer ci, Integer rnc, Integer value, Long timestamp) {
        updateNbapStat(ci, rnc, value, timestamp, GpehRelationType.HS_DL_TX_RequiredPower);
    }

    /**
     * Process r99 dl tx power.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param value the value
     * @param timestamp the timestamp
     */
    private void processR99DlTxPower(Integer ci, Integer rnc, Integer value, Long timestamp) {

        updateNbapStat(ci, rnc, value, timestamp, GpehRelationType.R99_DL_TX_POWER);
    }

    /**
     * Process total dl tx power.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param value the value
     * @param timestamp the timestamp
     */
    private void processTotalDlTxPower(Integer ci, Integer rnc, Integer value, Long timestamp) {
        updateNbapStat(ci, rnc, value, timestamp, GpehRelationType.TOTAL_DL_TX_POWER);
    }

    /**
     * Process ul rtwp.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param value the value
     * @param timestamp the timestamp
     */
    private void processUlRtwp(Integer ci, Integer rnc, Integer value, Long timestamp) {
        updateNbapStat(ci, rnc, value, timestamp, GpehRelationType.Ul_RTWP);
    }

    /**
     * Update nbap stat.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param value the value
     * @param timestamp the timestamp
     * @param rel the rel
     * @param dataElement
     */
    private void updateNbapStat(Integer ci, Integer rnc, Integer value, Long timestamp, GpehRelationType rel) {
        Node root = getStatTableRoot(rel);
        int minvalue = rel.getMin3GGPValue();
        int maxvalue = rel.getMax3GGPValue();
        timestamp = getStartPeriod(timestamp);
        NBapCache cache = getNBapCache(root, minvalue, maxvalue, timestamp);
        cache.update(ci, rnc, value);
    }

    /**
     * Flush.
     */
    public void flush() {
        for (NBapCache cashe : timeStatNodes.values()) {
            cashe.save(databaseService);
        }
        timeStatNodes.clear();
        for (Cache cashe : rrcCache.values()) {
            cashe.save(databaseService);
        }
        rrcCache.clear();
    }

    /**
     * Gets the n bap cache.
     * 
     * @param root the root
     * @param minValue the min value
     * @param maxvalue the maxvalue
     * @param timestamp the timestamp
     * @return the n bap cache
     */
    private NBapCache getNBapCache(Node root, int minValue, int maxvalue, Long timestamp) {
        if (statistic == null || NodeTypes.DATASET.checkNode(root)) {

            statistic = new StatisticHandler(root);
            Cache.root_key = root.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();

        }
        NBapCache result = timeStatNodes.get(root);
        timestamp = getStartPeriod(timestamp);
        if (result != null) {
            if (result.beginTimestamp != timestamp) {
                result.save(databaseService);
                result.load(timestamp, databaseService);
            }
        } else {
            result = new NBapCache(root, timestamp, minValue, maxvalue);
            timeStatNodes.put(root, result);
            result.load(timestamp, databaseService);
        }
        return result;
    }

    /**
     * Gets the time relation.
     * 
     * @param timestamp the timestamp
     * @return the time relation
     */
    protected static RelationshipType getTimeRelation(final long timestamp) {
        return Cache.getCellRelation(String.valueOf(timestamp));

    }

    /**
     * Gets the start period.
     * 
     * @param timestamp the timestamp
     * @return the start period - timestamp rounded by 15 min period
     */
    private Long getStartPeriod(Long timestamp) {
        return timestamp -= timestamp % (15 * 60 * 1000);
    }

    /**
     * Gets the stat table root.
     * 
     * @param type the type
     * @return the stat table root
     */
    private Node getStatTableRoot(GpehRelationType type) {
        // statType = type.name();
        Node result = tableRoot.get(type);
        if (result == null) {
            result = findOrCreateTableRoot(type);
            tableRoot.put(type, result);
        }
        return result;
    }

    private static String statType;

    /**
     * Find or create table root.
     * 
     * @param type the type
     * @return the i node
     */
    private Node findOrCreateTableRoot(GpehRelationType type) {
        // if (statisticNode.hasRelationship(type, Direction.OUTGOING)) {
        //
        // return statisticNode.getSingleRelationship(type, Direction.OUTGOING).getEndNode();
        //
        // }

        return Cache.currentNode;
    }

    /**
     * Process rrc event.
     * 
     * @param dataElement the data element
     * @param timestamp the timestamp
     */
    public void processRrcEvent(HashMap<String, Object> dataElement, Long timestamp) {
        @SuppressWarnings("unchecked")
        List<RrcMeasurement> measList = (List<RrcMeasurement>)dataElement.remove("MEAS_LIST");
        if (measList == null || measList.isEmpty()) {
            return;
        }
        String type = (String)dataElement.get(GpehReportUtil.MR_TYPE);
        if (type == null || type.isEmpty()) {
            return;
        }
        Integer ci = (Integer)dataElement.get("EVENT_PARAM_C_ID_1");
        Integer rnc = (Integer)dataElement.get("EVENT_PARAM_RNC_ID_1");
        if (ci == null || rnc == null) {
            return;
        }

        if (type.equals(GpehReportUtil.MR_TYPE_INTERF)) {
            statType = GpehRelationType.RRC.name();
            handleInterFrMeasurement(ci, rnc, timestamp, measList, dataElement);

        } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {
            statType = GpehRelationType.RRC.name();
            handleIntraFrMeasurement(ci, rnc, timestamp, measList, dataElement);

        } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {

            return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_UE_INTERNAL)) {
            statType = GpehRelationType.UE_TX_POWER.name();
            handleIUeTxPower(ci, rnc, timestamp, measList, dataElement);
        } else {
            return;
        }

    }

    /**
     * Handle i ue tx power.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param timestamp the timestamp
     * @param measList the meas list
     * @param dataElement the data element
     */
    private void handleIUeTxPower(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList,
            HashMap<String, Object> dataElement) {
        RrcMeasurement bestCellMeasurement = measList.get(0);
        Integer value = bestCellMeasurement.getUeTxPower();
        if (value == null) {
            return;
        }
        updateNbapStat(ci, rnc, value, timestamp, GpehRelationType.UE_TX_POWER);
    }

    /**
     * Handle intra fr measurement.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param timestamp the timestamp
     * @param measList the meas list
     * @param dataElement the data element
     */
    private void handleIntraFrMeasurement(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList,
            HashMap<String, Object> dataElement) {
        RrcMeasurement bestCellMeas = measList.get(0);
        if (bestCellMeas.getEcNo() == null || bestCellMeas.getEcNo() > 49 || bestCellMeas.getRscp() == null
                || bestCellMeas.getRscp() > 91) {
            return;
        }
        RRCCache cache = defineRrcCache(timestamp);
        cache.updateIntraFr(ci, rnc, measList, dataElement);
    }

    /**
     * Define rrc cache.
     * 
     * @param timestamp the timestamp
     * @return the rRC cache
     */
    public RRCCache defineRrcCache(Long timestamp) {
        // Node root = getStatTableRoot(GpehRelationType.RRC);
        // statType = GpehRelationType.RRC.name();
        Node root = dataset;
        timestamp = getStartPeriod(timestamp);
        RRCCache cache = getRrcCache(root, timestamp);
        return cache;
    }

    /**
     * Gets the intra fr cache.
     * 
     * @param root the root
     * @param timestamp the timestamp
     * @return the intra fr cache
     */
    private RRCCache getRrcCache(Node root, Long timestamp) {
        RRCCache result = rrcCache.get(root);
        timestamp = getStartPeriod(timestamp);
        if (result != null) {
            if (result.beginTimestamp != timestamp) {
                result.save(databaseService);
                result.load(timestamp, databaseService);
            }
        } else {
            result = new RRCCache(root, timestamp);
            rrcCache.put(root, result);
            result.load(timestamp, databaseService);
        }
        return result;
    }

    /**
     * Handle inter fr measurement.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param timestamp the timestamp
     * @param measList the meas list
     * @param dataElement the data element
     */
    private void handleInterFrMeasurement(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList,
            HashMap<String, Object> dataElement) {
        RRCCache cache = defineRrcCache(timestamp);
        cache.updateInterFr(ci, rnc, measList, dataElement);
    }

    /**
     * The Enum GpehRelationType.
     */
    public static enum GpehRelationType implements RelationshipType {

        /** The Ul_ rtwp. */
        Ul_RTWP(0, 621),
        /** The TOTA l_ d l_ t x_ power. */
        TOTAL_DL_TX_POWER(0, 1001),
        /** The R99_ d l_ t x_ power. */
        R99_DL_TX_POWER(0, 1001),
        /** The H s_ d l_ t x_ required power. */
        HS_DL_TX_RequiredPower(0, 1001),
        /** The U e_ t x_ power. */
        UE_TX_POWER(21, 104),
        /** The INTE r_ fr. */
        RRC(0, 100);

        /**
         * Instantiates a new gpeh relation type.
         * 
         * @param min3GPP the min3 gpp
         * @param max3GPP the max3 gpp
         */
        private GpehRelationType(int min3GPP, int max3GPP) {
            min3gpp = min3GPP;
            max3gpp = max3GPP;

        }

        /** The min3gpp. */
        private final int min3gpp;

        /** The max3gpp. */
        private final int max3gpp;

        /**
         * Gets the min3 ggp value.
         * 
         * @return the min3 ggp value
         */
        public int getMin3GGPValue() {
            return min3gpp;
        }

        /**
         * Gets the max3 ggp value.
         * 
         * @return the max3 ggp value
         */
        public int getMax3GGPValue() {
            return max3gpp;
        }
    }

    /**
     * The Class Cache.
     */
    public static abstract class Cache {
        public static final String STATISTIC_PROPERTY_TYPE = "statistic property type";

        /** The begin timestamp. */
        protected long beginTimestamp;
        protected static String root_key = null;

        protected static Node currentNode;
        /** The root. */
        protected Node root = null;

        /**
         * Form best cell.
         * 
         * @param rel the rel
         * @return the best cell
         */
        protected BestCell formBestCell(Node rel) {

            String[] id = rel.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().split("_");
            try {
                return new BestCell(Integer.valueOf(id[0]), Integer.valueOf(id[1]));
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Gets the cell relation.
         * 
         * @param cell the cell
         * @return the cell relation
         */
        protected RelationshipType getCellRelation(final BestCell cell) {
            final int ci = cell.getCi();
            final int rnc = cell.getRnc();
            return getCellRelation(String.format("%s_%s", ci, rnc));
        }

        /**
         * Gets the cell relation.
         * 
         * @param name the name
         * @return the cell relation
         */
        protected static RelationshipType getCellRelation(final String name) {
            return DynamicRelationshipType.withName(name);
        }

        /**
         * Clear.
         */
        public abstract void clearValues();

        /**
         * Load.
         * 
         * @param startTime TODO
         * @param service the service
         */
        public abstract void load(long startTime, GraphDatabaseService service);

        /**
         * Save.
         * 
         * @param service the service
         */
        public abstract void save(GraphDatabaseService service);
    }

    public static class NbapPool {
        int[] values;

        /**
         * @param range
         * @return
         */
        public int[] formValues(int range) {
            if (values == null) {
                values = new int[range];
            }
            return values;
        }
    }

    /**
     * The Class NBapCache.
     */
    public static class NBapCache extends Cache {

        /** The min value. */
        private final int minValue;

        /** The range. */
        private int range;

        /** The cell cache. */
        private final Map<BestCell, NbapPool> cellCache = new HashMap<GpehStatisticModel.BestCell, NbapPool>();

        /**
         * Instantiates a new n bap cache.
         * 
         * @param root the root
         * @param beginTimestamp the begin timestamp
         * @param minValue the min value
         * @param maxvalue the maxvalue
         */
        public NBapCache(Node root, long beginTimestamp, int minValue, int maxvalue) {
            if (this.root == null && this.currentNode != null) {
                this.root = this.currentNode;
            } else if (this.root == null && this.currentNode == null) {
                this.root = root;
            } else if (currentNode != null) {
                this.root = this.currentNode;
            }

            if (this.root.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && NodeTypes.DATASET.checkNode(this.root)) {
                statistic = new StatisticHandler(this.root);
                root_key = this.root.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            }
            this.beginTimestamp = beginTimestamp;
            this.minValue = minValue;
            range = maxvalue - minValue + 1;
        }

        /**
         * Update.
         * 
         * @param ci the ci
         * @param rnc the rnc
         * @param value the value
         */
        public void update(int ci, int rnc, Integer value) {
            BestCell bestCell = new BestCell(ci, rnc);
            NbapPool pool = getNbapPool(bestCell);
            int[] values = pool.formValues(range);
            value = value - minValue;
            values[value]++;

        }

        /**
         * @param bestCell
         * @return
         */
        private NbapPool getNbapPool(BestCell bestCell) {
            NbapPool result = cellCache.get(bestCell);
            if (result == null) {
                result = new NbapPool();
                cellCache.put(bestCell, result);
            }
            return result;
        }

        /**
         * Load.
         * 
         * @param service the service
         */
        @Override
        public void load(long startTime, GraphDatabaseService service) {

            clearValues();
            beginTimestamp = startTime;
            if (currentNode != null) {
                // for (Relationship rel : root.getRelationships(Direction.OUTGOING)) {
                BestCell cell = formBestCell(currentNode);
                if (cell != null) {
                    NbapPool pool = loadPool(startTime, currentNode);
                     cellCache.put(cell, pool);
                }
                // }
            }

        }

        /**
         * @param startTime
         * @param endNode
         * @return
         */
        protected NbapPool loadPool(long startTime, Node statNode) {
            NbapPool result = new NbapPool();
            
            if (statNode.hasProperty("Range size")
                    && startTime == (Long)statNode.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, null)) {
                // result.values = (int[])statNode.getProperty("value", null);
                result.formValues((Integer)statNode.getProperty("Range size"));
                for (int i = 0; i < result.values.length; i++) {
                    if (statNode.hasProperty(String.format("%s %d", statType, i))) {
                        result.values[i] = (Integer)statNode.getProperty(String.format("%s %d", statType, i));
                    }
                }
            }
            return result;
        }

        /**
         * Save.
         * 
         * @param service the service
         */
        @Override
        public void save(GraphDatabaseService service) {
            // if (root.hasRelationship(reltype, Direction.OUTGOING)) {
            // result = root.getSingleRelationship(reltype, Direction.OUTGOING).getEndNode();
            // } else {
            // result = service.createNode();
            // root.createRelationshipTo(result, reltype);
            // }

            for (Map.Entry<BestCell, NbapPool> entry : cellCache.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                RelationshipType reltype = getCellRelation(entry.getKey());
                Node result;
                if (!root.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING) && NodeTypes.DATASET.checkNode(root)) {
                    currentNode = root;
                    result = service.createNode();
                    result.setProperty("type", NodeTypes.M.getId());

                    result.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());
                    currentNode.createRelationshipTo(result, NetworkRelationshipTypes.CHILD);
                    currentNode = result;

                } else {
                    result = service.createNode();
                    result.setProperty("type", NodeTypes.M.getId());
                    result.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());
                    currentNode.createRelationshipTo(result, NetworkRelationshipTypes.NEXT);
                    currentNode = result;
                }
                statistic.indexValue(root_key, NodeTypes.M.getId(), STATISTIC_PROPERTY_TYPE, statType);
                savePool(beginTimestamp, currentNode, entry.getValue());
                currentNode.setProperty(STATISTIC_PROPERTY_TYPE, statType);

            }
            statistic.increaseTypeCount(root_key, NodeTypes.M.getId(), 1);
            statistic.saveStatistic();
            clearValues();
        }

        /**
         * Save NBAP pool
         * 
         * @param beginTimestamp the begin timestamp
         * @param statNode the stat node
         * @param nbapPool the nbap pool
         */
        private void savePool(long beginTimestamp, Node statNode, NbapPool nbapPool) {
            if (nbapPool.values != null) {
                statNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
                for (int i = 0; i < nbapPool.values.length; i++) {
                    if (nbapPool.values[i] != 0) {
                        statNode.setProperty(String.format("%s %d", statType, i), nbapPool.values[i]);
                        statistic.indexValue(root_key, NodeTypes.M.getId(), String.format("%s %d", statType, i), nbapPool.values[i]);
                    }
                }
                statNode.setProperty("Range size", nbapPool.values.length);
            }
        }

        /**
         * Clear.
         */
        @Override
        public void clearValues() {

            cellCache.values().clear();
        }
    }

    /**
     * The Class BestCell.
     */
    public static class BestCell {

        /** The ci. */
        final int ci;

        /** The rnc. */
        final int rnc;

        /**
         * Instantiates a new best cell.
         * 
         * @param ci the ci
         * @param rnc the rnc
         */
        public BestCell(int ci, int rnc) {
            super();
            this.ci = ci;
            this.rnc = rnc;
        }

        /**
         * Gets the ci.
         * 
         * @return the ci
         */
        public int getCi() {
            return ci;
        }

        /**
         * Gets the rnc.
         * 
         * @return the rnc
         */
        public int getRnc() {
            return rnc;
        }

        /**
         * Hash code.
         * 
         * @return the int
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ci;
            result = prime * result + rnc;
            return result;
        }

        /**
         * Equals.
         * 
         * @param obj the obj
         * @return true, if successful
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            BestCell other = (BestCell)obj;
            if (ci != other.ci)
                return false;
            if (rnc != other.rnc)
                return false;
            return true;
        }

    }

    /**
     * The Class IntraFrCache.
     */
    public static class RRCCache extends Cache {

        private static final String COUNT_INTRA_ECNO_DELTA_3 = "count of intra EC_NO delta  <=3 Dbm";
        private static final String COUNT_INTRA_ECNO_DELTA_6 = "count of intra EC_NO delta  <=6 Dbm";
        private static final String COUNT_INTRA_ECNO_DELTA_9 = "count of intra EC_NO delta  <=9 Dbm";
        private static final String COUNT_INTRA_ECNO_DELTA_12 = "count of intra EC_NO delta  <=12 Dbm";
        private static final String COUNT_INTRA_ECNO_DELTA_15 = "count of intra EC_NO delta  <=15 Dbm";

        private static final String COUNT_INTRA_RSCP_DELTA_3 = "count of intra RSCP delta  <=3 Dbm";
        private static final String COUNT_INTRA_RSCP_DELTA_6 = "count of intra RSCP delta  <=6 Dbm";
        private static final String COUNT_INTRA_RSCP_DELTA_9 = "count of intra RSCP delta  <=9 Dbm";
        private static final String COUNT_INTRA_RSCP_DELTA_12 = "count of intra RSCP delta  <=12 Dbm";
        private static final String COUNT_INTRA_RSCP_DELTA_15 = "count of intra RSCP delta  <=15 Dbm";

        private static final String NUM_INTRA_MES = "number of intra Measurements ";
        private static final String NUM_INTER_MES = "number of inter Measurements ";
        private static final String NUM_INTER_MES_BEST_CELL = "number of mesurement for  inter best cell";
        private static final String NUM_INTRA_MES_BEST_CELL = "number of mesurement for  intra best cell";
        private static final String NUM_IRAT_MES_BEST_CELL = "number of mesurement for  irat best cell";

        private static final String COUNT_INTER_ECNO_6DB = "count inter EC_NO >=-6dB";
        private static final String COUNT_INTER_ECNO_9DB = "count inter EC_NO >=-9dB";
        private static final String COUNT_INTER_ECNO_12DB = "count inter EC_NO >=-12dB";
        private static final String COUNT_INTER_ECNO_15DB = "count inter EC_NO >=-15dB";
        private static final String COUNT_INTER_ECNO_18DB = "count inter EC_NO >=-18dB";

        private static final String COUNT_INTER_RSCP_105DB_ECNO_14DB = "count inter RSCP <-105dB EC_NO>-14dB";
        private static final String COUNT_INTER_RSCP_95DB_ECNO_14DB = "count inter RSCP <-95dB EC_NO>-14dB";
        private static final String COUNT_INTER_RSCP_85DB_ECNO_14DB = "count inter RSCP <-85dB EC_NO>-14dB";
        private static final String COUNT_INTER_RSCP_L75DB_ECNO_14DB = "count inter RSCP <-75dB EC_NO>-14dB";
        private static final String COUNT_INTER_RSCP_M75DB_ECNO_14DB = "count inter RSCP >=-75dB EC_NO>-14dB";

        private static final String COUNT_INTER_RSCP_105DB_ECNO_10DB = "count inter RSCP <-105dB EC_NO>-10dB";
        private static final String COUNT_INTER_RSCP_95DB_ECNO_10DB = "count inter RSCP <-95dB EC_NO>-10dB";
        private static final String COUNT_INTER_RSCP_85DB_ECNO_10DB = "count inter RSCP <-85dB EC_NO>-10dB";
        private static final String COUNT_INTER_RSCP_L75DB_ECNO_10DB = "count inter RSCP <-75dB EC_NO>-10dB";
        private static final String COUNT_INTER_RSCP_M75DB_ECNO_10DB = "count inter RSCP >=-75dB EC_NO>-10dB";

        private static final String COUNT_POSITION_1 = "Position 1";
        private static final String COUNT_POSITION_2 = "Position 2";
        private static final String COUNT_POSITION_3 = "Position 3";
        private static final String COUNT_POSITION_4 = "Position 4";
        private static final String COUNT_POSITION_5 = "Position 5";

        private List<String> paramPositionList;
        private List<String> paramIntraEcnoListDelta;
        private List<String> paramIntraRSCPListDelta;
        private List<String> paramInterEcnoList;
        private List<String> paramInterRSCPECNO14;
        private List<String> paramInterRSCPECNO10;

        private void init() {
            if (paramPositionList == null) {
                paramPositionList = new LinkedList<String>();
                setPositionList();
            }
            if (paramIntraEcnoListDelta == null) {
                paramIntraEcnoListDelta = new LinkedList<String>();
                setIntraEcnoList();
            }
            if (paramIntraRSCPListDelta == null) {
                paramIntraRSCPListDelta = new LinkedList<String>();
                setIntraRSCPList();
            }
            if (paramInterEcnoList == null) {
                paramInterEcnoList = new LinkedList<String>();
                setInterEcnoList();
            }
            if (paramInterRSCPECNO14 == null) {
                paramInterRSCPECNO14 = new LinkedList<String>();
                setInterRSCPECNO14List();
            }
            if (paramInterRSCPECNO10 == null) {
                paramInterRSCPECNO10 = new LinkedList<String>();
                setInterRSCPECNO10List();
            }

        }

        private void setPositionList() {
            if (paramPositionList.isEmpty()) {
                paramPositionList.add(COUNT_POSITION_1);
                paramPositionList.add(COUNT_POSITION_2);
                paramPositionList.add(COUNT_POSITION_3);
                paramPositionList.add(COUNT_POSITION_4);
                paramPositionList.add(COUNT_POSITION_5);
            }
        }

        private void setIntraEcnoList() {
            if (paramIntraEcnoListDelta.isEmpty()) {
                paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_3);
                paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_6);
                paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_9);
                paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_12);
                paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_15);
            }

        }

        private void setIntraRSCPList() {
            if (paramIntraRSCPListDelta.isEmpty()) {
                paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_3);
                paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_6);
                paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_9);
                paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_12);
                paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_15);
            }
        }

        private void setInterEcnoList() {
            if (paramInterEcnoList.isEmpty()) {
                paramInterEcnoList.add(COUNT_INTER_ECNO_6DB);
                paramInterEcnoList.add(COUNT_INTER_ECNO_9DB);
                paramInterEcnoList.add(COUNT_INTER_ECNO_12DB);
                paramInterEcnoList.add(COUNT_INTER_ECNO_15DB);
                paramInterEcnoList.add(COUNT_INTER_ECNO_18DB);
            }
        }

        private void setInterRSCPECNO14List() {
            if (paramInterRSCPECNO14.isEmpty()) {
                paramInterRSCPECNO14.add(COUNT_INTER_RSCP_105DB_ECNO_14DB);
                paramInterRSCPECNO14.add(COUNT_INTER_RSCP_95DB_ECNO_14DB);
                paramInterRSCPECNO14.add(COUNT_INTER_RSCP_85DB_ECNO_14DB);
                paramInterRSCPECNO14.add(COUNT_INTER_RSCP_L75DB_ECNO_14DB);
                paramInterRSCPECNO14.add(COUNT_INTER_RSCP_M75DB_ECNO_14DB);
            }
        }

        private void setInterRSCPECNO10List() {
            if (paramInterRSCPECNO10.isEmpty()) {
                paramInterRSCPECNO10.add(COUNT_INTER_RSCP_105DB_ECNO_10DB);
                paramInterRSCPECNO10.add(COUNT_INTER_RSCP_95DB_ECNO_10DB);
                paramInterRSCPECNO10.add(COUNT_INTER_RSCP_85DB_ECNO_10DB);
                paramInterRSCPECNO10.add(COUNT_INTER_RSCP_L75DB_ECNO_10DB);
                paramInterRSCPECNO10.add(COUNT_INTER_RSCP_M75DB_ECNO_10DB);
            }
        }

        /** The main cache. */
        private final Map<BestCell, RrcBestCellPool> mainCache = new HashMap<GpehStatisticModel.BestCell, RrcBestCellPool>();

        /**
         * Instantiates a new rRC cache.
         * 
         * @param root the root
         * @param timestamp the timestamp
         */
        public RRCCache(Node root, Long timestamp) {
            init();
            // statType = GpehRelationType.RRC.name();

            if (statistic == null || NodeTypes.DATASET.checkNode(root)) {

                statistic = new StatisticHandler(root);
                Cache.root_key = root.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();

            }
            if (this.root == null && this.currentNode != null) {
                this.root = this.currentNode;
            } else if (this.root == null && this.currentNode == null) {
                this.root = root;
            } else if (currentNode != null) {
                this.root = this.currentNode;
            }

            this.beginTimestamp = timestamp;
        }

        /**
         * Update intra fr.
         * 
         * @param ci the ci
         * @param rnc the rnc
         * @param measList the meas list
         * @param dataElement
         */
        public void updateIntraFr(Integer ci, Integer rnc, List<RrcMeasurement> measList, HashMap<String, Object> dataElement) {
            BestCell cell = new BestCell(ci, rnc);

            Iterator<RrcMeasurement> measIter = measList.iterator();

            RrcMeasurement bestCellMeas = measIter.next();
            Integer ecNo = bestCellMeas.getEcNo();
            Integer rscp = bestCellMeas.getRscp();
            RrcBestCellPool pool = getRrcBestCellPool(cell);
            updateShoStat(pool, dataElement);
            if (pool.rscpEcno == null) {
                pool.rscpEcno = new int[92][50];
            }
            pool.rscpEcno[rscp][ecNo]++;
            pool.numMrForBestCellIntra++;
            Map<String, Pool> cache = getCacheMap(cell);
            measList.remove(0);
            Collections.sort(measList, new Comparator<RrcMeasurement>() {

                @Override
                public int compare(RrcMeasurement o1, RrcMeasurement o2) {
                    if (o1.getEcNo() == null) {
                        return 1;
                    }
                    if (o2.getEcNo() == null) {
                        return -1;
                    };
                    return o2.getEcNo().compareTo(o1.getEcNo());
                }

            });
            measIter = measList.iterator();
            int pos = 0;
            while (measIter.hasNext()) {
                pos++;
                RrcMeasurement sectorMeas = measIter.next();
                String psc = sectorMeas.getScrambling();
                if (psc == null || sectorMeas.getEcNo() == null && sectorMeas.getRscp() == null) {
                    continue;
                }
                IntraPool pul = getIntraPool(cache, psc);
                pul.numMeasurements++;
                if (sectorMeas.getEcNo() != null) {
                    double deltaDbm = (double)Math.abs(bestCellMeas.getEcNo() - sectorMeas.getEcNo()) / 2;
                    if (deltaDbm <= 3) {
                        pul.ecnoD[0]++;
                    }
                    if (deltaDbm <= 6) {
                        pul.ecnoD[1]++;
                    }
                    if (deltaDbm <= 9) {
                        pul.ecnoD[2]++;
                    }
                    if (deltaDbm <= 12) {
                        pul.ecnoD[3]++;
                    }
                    if (deltaDbm <= 15) {
                        pul.ecnoD[4]++;
                    }
                }
                if (sectorMeas.getRscp() != null) {
                    double deltaRscp = (double)Math.abs(bestCellMeas.getRscp() - sectorMeas.getRscp()) / 1;
                    if (deltaRscp <= 3) {
                        pul.rscpD[0]++;
                    }
                    if (deltaRscp <= 6) {
                        pul.rscpD[1]++;
                    }
                    if (deltaRscp <= 9) {
                        pul.rscpD[2]++;
                    }
                    if (deltaRscp <= 12) {
                        pul.rscpD[3]++;
                    }
                    if (deltaRscp <= 15) {
                        pul.rscpD[4]++;
                    }
                }
                if (pos < 5) {
                    pul.position[pos]++;
                }
            }

        }

        /**
         * Gets the intra pool.
         * 
         * @param cache the cache
         * @param psc the psc
         * @return the intra pool
         */
        private IntraPool getIntraPool(Map<String, Pool> cache, String psc) {
            Pool pool = getPool(cache, psc);
            IntraPool result = pool.formIntraPool();
            return result;
        }

        /**
         * Update.
         * 
         * @param ci the ci
         * @param rnc the rnc
         * @param measList the meas list
         * @param dataElement
         */
        public void updateInterFr(Integer ci, Integer rnc, List<RrcMeasurement> measList, HashMap<String, Object> dataElement) {
            BestCell cell = new BestCell(ci, rnc);
            Map<String, Pool> cache = getCacheMap(cell);

            Iterator<RrcMeasurement> measIter = measList.iterator();
            // fist meas is meas of best cell!
            @SuppressWarnings("unused")
            RrcMeasurement bestCellMeas = measIter.next();
            RrcBestCellPool pool = getRrcBestCellPool(cell);
            updateShoStat(pool, dataElement);
            pool.numMrForBestCellInter++;
            while (measIter.hasNext()) {
                RrcMeasurement sectorMeas = measIter.next();
                String psc = sectorMeas.getScrambling();
                if (psc == null || psc.isEmpty()) {
                    continue;
                }
                InterPool pul = getInterPool(cache, psc);
                pul.numMeasurements++;
                Integer ecNo = sectorMeas.getEcNo();
                if (ecNo != null) {
                    if (ecNo >= 37) {// >=-6dB
                        pul.ecno[0]++;
                    }
                    if (ecNo >= 31) {// >=-9
                        pul.ecno[1]++;
                    }
                    if (ecNo >= 25) {// >=-12
                        pul.ecno[2]++;
                    }
                    if (ecNo >= 19) {// >=-15
                        pul.ecno[3]++;
                    }
                    if (ecNo >= 13) {// >=-18
                        pul.ecno[4]++;
                    }
                    Integer rscp = sectorMeas.getRscp();
                    if (rscp != null) {
                        if (ecNo > 21) {// >-14
                            if (rscp < 11) {// <-105
                                pul.rscp[0]++;
                            }
                            if (rscp < 21) {// <-95
                                pul.rscp[1]++;
                            }
                            if (rscp < 31) {// <-85
                                pul.rscp[2]++;
                            }
                            if (rscp < 41) {// <-75
                                pul.rscp[3]++;
                            }
                            if (rscp >= 41) {// >=-75
                                pul.rscp[4]++;
                            }
                        }
                        if (ecNo > 29) {// >-10
                            if (rscp < 11) {
                                pul.rscp[5]++;
                            }
                            if (rscp < 21) {// <-95
                                pul.rscp[6]++;
                            }
                            if (rscp < 31) {// <-85
                                pul.rscp[7]++;
                            }
                            if (rscp < 41) {// <-75
                                pul.rscp[8]++;
                            }
                            if (rscp >= 41) {// >=-75
                                pul.rscp[9]++;
                            }
                        }

                    }
                }
            }

        }

        /**
         * Update count of AS cell
         * 
         * @param pool the pool
         * @param dataElement the property container
         */
        private void updateShoStat(RrcBestCellPool pool, HashMap<String, Object> dataElement) {
            if (dataElement.isEmpty()) {
                return;
            }
            int count = 1;
            for (int i = 2; i <= 4; i++) {
                if (dataElement.get("EVENT_PARAM_C_ID_" + i) != null) {
                    count++;
                }
            }
            if (count == 1) {
                pool.oneWay++;
            } else if (count == 2) {
                pool.twoWay++;
            } else {
                pool.threeWay++;
            }
        }

        /**
         * Gets the inter pool.
         * 
         * @param cache the cache
         * @param psc the psc
         * @return the inter pool
         */
        private InterPool getInterPool(Map<String, Pool> cache, String psc) {
            Pool pool = getPool(cache, psc);
            InterPool result = pool.formInterPool();
            return result;
        }

        /**
         * Gets the pool.
         * 
         * @param cache the cache
         * @param psc the psc
         * @return the pool
         */
        private Pool getPool(Map<String, Pool> cache, String psc) {
            Pool pool = cache.get(psc);
            if (pool == null) {
                pool = new Pool();
                cache.put(psc, pool);
            }
            return pool;
        }

        /**
         * Gets the cache map.
         * 
         * @param cell the cell
         * @return the cache map
         */
        private Map<String, Pool> getCacheMap(BestCell cell) {
            RrcBestCellPool pool = getRrcBestCellPool(cell);
            return pool.cache;
        }

        /**
         * Gets the rrc best cell pool.
         * 
         * @param cell the cell
         * @return the rrc best cell pool
         */
        private RrcBestCellPool getRrcBestCellPool(BestCell cell) {
            RrcBestCellPool result = mainCache.get(cell);
            if (result == null) {
                result = new RrcBestCellPool();
                mainCache.put(cell, result);
            }
            return result;
        }

        /**
         * Clear.
         */
        @Override
        public void clearValues() {
            mainCache.clear();
        }

        /**
         * Load.
         * 
         * @param service the service
         */
        @Override
        public void load(long startTime, GraphDatabaseService service) {
            clearValues();
            beginTimestamp = startTime;

            for (Relationship rel : root.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                BestCell cell = formBestCell(rel.getEndNode());
                if (cell != null) {
                    Node cellRoot = rel.getEndNode();
                    RrcBestCellPool bestCell = loadBestCellStat(cellRoot);

                    mainCache.put(cell, bestCell);

                    Map<String, Pool> cache = getCacheMap(cell);

                    while (cellRoot.hasRelationship(NetworkRelationshipTypes.NEXT, Direction.OUTGOING)) {

                        Pool pool = loadPoolFromNode(cellRoot.getSingleRelationship(NetworkRelationshipTypes.NEXT,
                                Direction.OUTGOING).getEndNode());
                        cache.put(statType, pool);

                        cellRoot = cellRoot.getSingleRelationship(NetworkRelationshipTypes.NEXT, Direction.OUTGOING).getEndNode();

                    }
                }
            }
        }

        /**
         * Load best cell stat.
         * 
         * @param cellRoot the cell root
         * @return the rrc best cell pool
         */
        private RrcBestCellPool loadBestCellStat(Node cellRoot) {
            RrcBestCellPool result = new RrcBestCellPool();
            result.numMrForBestCellInter = (Integer)cellRoot.getProperty(RRCCache.NUM_INTER_MES_BEST_CELL, 0);
            result.numMrForBestCellIntra = (Integer)cellRoot.getProperty(RRCCache.NUM_INTRA_MES_BEST_CELL, 0);
            result.numMrForBestCellIrat = (Integer)cellRoot.getProperty(RRCCache.NUM_IRAT_MES_BEST_CELL, 0);
            result.oneWay = (Integer)cellRoot.getProperty("oneWay", 0);
            result.twoWay = (Integer)cellRoot.getProperty("twoWay", 0);
            result.threeWay = (Integer)cellRoot.getProperty("threeWay", 0);
            int[] rspArr = new int[50];
            // if (cellRoot.hasProperty(String.format("%srscp%s", beginTimestamp, 0))) {
            // result.rscpEcno = new int[92][50];
            // for (int rscp = 0; rscp <= 91; rscp++) {
            // result.rscpEcno[rscp] = (int[])cellRoot.getProperty(String.format("%srscp%s",
            // beginTimestamp, rscp));
            // }
            // }

            result.rscpEcno = new int[92][50];
            for (int rscp = 0; rscp <= 91; rscp++) {
                rspArr = new int[50];
                for (int ecno = 0; ecno <= 50; ecno++) {
                    if (cellRoot.hasProperty(String.format("RSCP %d ECNO %d", rscp, ecno).toString())) {
                        rspArr[ecno] = Integer.parseInt(cellRoot.getProperty(String.format("RSCP %s ECNO %d", rscp, ecno))
                                .toString());
                    }

                }
                result.rscpEcno[rscp] = rspArr;
            }

            return result;
        }

        /**
         * Store best cell stat.
         * 
         * @param cellRoot the cell root
         * @param cell the cell
         */
        private void storeBestCellStat(Node cellRoot, RrcBestCellPool cell) {
            cellRoot.setProperty("oneWay", cell.oneWay);
            statistic.indexValue(root_key, NodeTypes.M.getId(), "oneWay", cell.oneWay);
            cellRoot.setProperty("twoWay", cell.twoWay);
            statistic.indexValue(root_key, NodeTypes.M.getId(), "twoWay", cell.twoWay);
            cellRoot.setProperty("threeWay", cell.threeWay);
            statistic.indexValue(root_key, NodeTypes.M.getId(), "threeWay", cell.threeWay);
            cellRoot.setProperty(NUM_INTER_MES_BEST_CELL, cell.numMrForBestCellInter);
            statistic.indexValue(root_key, NodeTypes.M.getId(), NUM_INTER_MES_BEST_CELL, cell.numMrForBestCellInter);
            cellRoot.setProperty(NUM_INTRA_MES_BEST_CELL, cell.numMrForBestCellIntra);
            statistic.indexValue(root_key, NodeTypes.M.getId(), NUM_INTRA_MES_BEST_CELL, cell.numMrForBestCellIntra);
            cellRoot.setProperty(NUM_IRAT_MES_BEST_CELL, cell.numMrForBestCellIrat);
            statistic.indexValue(root_key, NodeTypes.M.getId(), NUM_IRAT_MES_BEST_CELL, cell.numMrForBestCellIrat);
            cellRoot.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
            statistic.indexValue(root_key, NodeTypes.M.getId(), INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
            String ecnoParamName;
            String rspcParamName;
            if (cell.rscpEcno != null) {
                Integer rscpCount = 0;
                Integer ecnoCount = 0;
                // rscp count
                for (int rscp = 0; rscp <= 91; rscp++) {
                    int[] ecnoArr = cell.rscpEcno[rscp];
                    for (int index = 0; index < ecnoArr.length; index++) {
                        if (ecnoArr[index] > 0) {
                            rscpCount += ecnoArr[index];
                        }
                    }
                    if (rscpCount > 0) {
                        rspcParamName = "RSCP " + rscp;
                        cellRoot.setProperty(rspcParamName, rscpCount);
                        statistic.indexValue(root_key, NodeTypes.M.getId(), rspcParamName, rscpCount);
                        rscpCount = 0;

                    }
                    // cellRoot.setProperty(String.format("%srscp%s", beginTimestamp, rscp),
                    // cell.rscpEcno[rscp]);
                }
                // ecno sum
                for (int ecno = 0; ecno < 50; ecno++) {
                    for (int rscp = 0; rscp <= 91; rscp++) {
                        int[] ecnoArr = cell.rscpEcno[rscp];
                        if (ecnoArr[ecno] > 0) {
                            ecnoCount += ecnoArr[ecno];
                        }
                    }
                    if (ecnoCount > 0) {
                        ecnoParamName = "ECNO " + ecno;
                        cellRoot.setProperty(ecnoParamName, ecnoCount);
                        statistic.indexValue(root_key, NodeTypes.M.getId(), ecnoParamName, ecnoCount);
                        ecnoCount = 0;

                    }
                }
                // rscp ecno
                for (int rscp = 0; rscp <= 91; rscp++) {
                    int[] ecnoArr = cell.rscpEcno[rscp];
                    rspcParamName = "RSCP " + rscp;
                    for (int index = 0; index < ecnoArr.length; index++) {
                        ecnoParamName = " ECNO " + index;
                        if (ecnoArr[index] > 0) {
                            cellRoot.setProperty(String.format("RSCP %d ECNO %d", rscp, index), ecnoArr[index]);
                            // statistic.indexValue(root_key, NodeTypes.M.getId(),
                            // String.format("RSCP %d ECNO %d", rscp, index),
                            // ecnoArr[index]);
                        }
                    }
                }

            }

        }

        /**
         * Store pool.
         * 
         * @param node the node
         * @param pool the pool
         */
        private void storePool(Node node, Pool pool) {
            if (pool.interPool != null) {
                node.setProperty(NUM_INTER_MES, pool.interPool.numMeasurements);
                statistic.indexValue(root_key, NodeTypes.M.getId(), NUM_INTER_MES, pool.interPool.numMeasurements);
                // node.setProperty(NUM_INTER_MES + beginTimestamp, pool.interPool.numMeasurements);

                for (int i = 0; i < paramInterEcnoList.size(); i++) {
                    node.setProperty(paramInterEcnoList.get(i), pool.interPool.ecno[i]);
                    statistic.indexValue(root_key, NodeTypes.M.getId(), paramInterEcnoList.get(i), pool.interPool.ecno[i]);

                }
                // node.setProperty("interRscp" + beginTimestamp, pool.interPool.rscp);
                for (int i = 0; i < (paramInterRSCPECNO14.size() + paramInterRSCPECNO10.size()); i++) {
                    if (i < paramInterRSCPECNO14.size()) {
                        node.setProperty(paramInterRSCPECNO14.get(i), pool.interPool.rscp[i]);
                        statistic.indexValue(root_key, NodeTypes.M.getId(), paramInterRSCPECNO14.get(i), pool.interPool.rscp[i]);
                    } else {
                        node.setProperty(paramInterRSCPECNO10.get(i - paramInterRSCPECNO10.size()), pool.interPool.rscp[i]);
                        statistic.indexValue(root_key, NodeTypes.M.getId(),
                                paramInterRSCPECNO10.get(i - paramInterRSCPECNO10.size()), pool.interPool.rscp[i]);
                    }
                }

                node.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
                statistic.indexValue(root_key, NodeTypes.M.getId(), INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
            }
            if (pool.intraPool != null) {
                node.setProperty(NUM_INTRA_MES, pool.intraPool.numMeasurements);
                statistic.indexValue(root_key, NodeTypes.M.getId(), NUM_INTER_MES, pool.intraPool.numMeasurements);
                // node.setProperty("intraEcnoD" + beginTimestamp, pool.intraPool.ecnoD);
                for (int i = 0; i < paramIntraEcnoListDelta.size(); i++) {
                    node.setProperty(paramIntraEcnoListDelta.get(i), pool.intraPool.ecnoD[i]);
                    statistic.indexValue(root_key, NodeTypes.M.getId(), paramIntraEcnoListDelta.get(i), pool.intraPool.ecnoD[i]);
                }

                // node.setProperty("intraRscpD" + beginTimestamp, pool.intraPool.rscpD);
                for (int i = 0; i < paramIntraRSCPListDelta.size(); i++) {
                    node.setProperty(paramIntraRSCPListDelta.get(i), pool.intraPool.rscpD[i]);
                    statistic.indexValue(root_key, NodeTypes.M.getId(), paramIntraRSCPListDelta.get(i), pool.intraPool.rscpD[i]);
                }

                // node.setProperty("positions", pool.intraPool.position);
                for (int i = 0; i < paramPositionList.size(); i++) {
                    node.setProperty(paramPositionList.get(i), pool.intraPool.position[i]);
                    statistic.indexValue(root_key, NodeTypes.M.getId(), paramPositionList.get(i), pool.intraPool.position[i]);
                }

                node.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
                statistic.indexValue(root_key, NodeTypes.M.getId(), INeoConstants.PROPERTY_TIMESTAMP_NAME, beginTimestamp);
            }

        }

        /**
         * Load pool from node.
         * 
         * @param node the node
         * @return the pool
         */
        private Pool loadPoolFromNode(Node node) {
            Pool result = new Pool();
            String ekno = "interEcno" + beginTimestamp;
            String rscp = "interRscp" + beginTimestamp;
            String numMr = "interMr" + beginTimestamp;
            // if (node.hasProperty(ekno)) {
            // InterPool inter = result.formInterPool();
            // inter.ecno = (int[])node.getProperty(ekno);
            // inter.rscp = (int[])node.getProperty(rscp);
            // inter.numMeasurements = (Integer)node.getProperty(numMr);
            // }

            if (node.hasProperty(NUM_INTER_MES) && node.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)) {
                InterPool inter = result.formInterPool();
                inter.numMeasurements = (Integer)node.getProperty(NUM_INTER_MES);
                for (int i = 0; i < paramInterEcnoList.size(); i++) {
                    if (node.hasProperty(paramInterEcnoList.get(i))) {
                        inter.ecno[i] = (Integer)node.getProperty(paramInterEcnoList.get(i));
                    }

                }

                for (int i = 0; i < (paramInterRSCPECNO14.size() + paramInterRSCPECNO10.size()); i++) {

                    if (i < paramInterRSCPECNO14.size()) {
                        inter.rscp[i] = (Integer)node.getProperty(paramInterRSCPECNO14.get(i));
                    } else {
                        inter.rscp[i] = (Integer)node.getProperty(paramInterRSCPECNO10.get(i - paramInterRSCPECNO10.size()));
                    }
                }
                result.interPool=inter;
               
            }

            if (node.hasProperty(NUM_INTRA_MES) && node.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)) {
                IntraPool intra = result.formIntraPool();
                intra.numMeasurements = (Integer)node.getProperty(NUM_INTRA_MES);
                for (int i = 0; i < paramIntraEcnoListDelta.size(); i++) {
                    if (node.hasProperty(paramIntraEcnoListDelta.get(i))) {
                        intra.ecnoD[i] = (Integer)node.getProperty(paramIntraEcnoListDelta.get(i));
                    }
                }

                // node.setProperty("intraRscpD" + beginTimestamp, pool.intraPool.rscpD);
                for (int i = 0; i < paramIntraRSCPListDelta.size(); i++) {
                    if (node.hasProperty(paramIntraRSCPListDelta.get(i))) {
                        intra.rscpD[i] = (Integer)node.getProperty(paramIntraRSCPListDelta.get(i));
                    }
                }

                // node.setProperty("positions", pool.intraPool.position);
                for (int i = 0; i < paramPositionList.size(); i++) {
                    if (node.hasProperty(paramPositionList.get(i))) {
                        intra.position[i] = (Integer)node.getProperty(paramPositionList.get(i));
                    }
                }
                result.intraPool=intra;
            }
            
            // String intraKey = "intraEcnoD" + beginTimestamp;
            // numMr = "intraMr" + beginTimestamp;
            // if (node.hasProperty(intraKey)) {
            // IntraPool intra = result.formIntraPool();
            // intra.ecnoD = (int[])node.getProperty(intraKey);
            // intra.rscpD = (int[])node.getProperty("intraRscpD" + beginTimestamp);
            // intra.position = (int[])node.getProperty("positions" + beginTimestamp);
            // intra.numMeasurements = (Integer)node.getProperty(numMr);
            // }
            return result;
        }

        /**
         * Save.
         * 
         * @param service the service
         */

        @Override
        public void save(GraphDatabaseService service) {

            for (Map.Entry<BestCell, RrcBestCellPool> entry : mainCache.entrySet()) {
                // RelationshipType reltype = getCellRelation(entry.getKey());
                Node cellRoot;
                if (!root.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING) && NodeTypes.DATASET.checkNode(root)) {
                    currentNode = root;
                    cellRoot = service.createNode();
                    cellRoot.setProperty("type", NodeTypes.M.getId());
                    cellRoot.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());

                    currentNode.createRelationshipTo(cellRoot, NetworkRelationshipTypes.CHILD);
                    currentNode = cellRoot;
                } else {
                    cellRoot = service.createNode();
                    cellRoot.setProperty("type", NodeTypes.M.getId());
                    cellRoot.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());
                    currentNode.createRelationshipTo(cellRoot, NetworkRelationshipTypes.NEXT);
                    currentNode = cellRoot;
                }
                storeBestCellStat(currentNode, entry.getValue());
                cellRoot.setProperty(Cache.STATISTIC_PROPERTY_TYPE, statType);
                statistic.indexValue(root_key, NodeTypes.M.getId(), STATISTIC_PROPERTY_TYPE, statType);
                statistic.increaseTypeCount(root_key, NodeTypes.M.getId(), 1);

                for (Map.Entry<String, Pool> entryCache : entry.getValue().cache.entrySet()) {
                    if (entryCache.getValue() == null) {
                        continue;
                    }
                    // RelationshipType relt = getCellRelation(entryCache.getKey());
                    // if (relt == null || entryCache.getKey() == null) {
                    // System.err.println("Null rel");
                    // }
                    Node pscRoot;
                    try {
                        if (!currentNode.hasRelationship(Direction.OUTGOING)) {
                            pscRoot = service.createNode();
                            pscRoot.setProperty("type", NodeTypes.M.getId());
                            pscRoot.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());
                            currentNode.createRelationshipTo(pscRoot, NetworkRelationshipTypes.NEXT);
                            currentNode = pscRoot;
                        } else {
                            pscRoot = service.createNode();
                            pscRoot.setProperty("type", NodeTypes.M.getId());
                            pscRoot.setProperty("name", entry.getKey().getCi() + "_" + entry.getKey().getRnc());
                            currentNode.createRelationshipTo(pscRoot, NetworkRelationshipTypes.NEXT);
                            currentNode = pscRoot;
                        }
                        pscRoot.setProperty(STATISTIC_PROPERTY_TYPE, statType);
                        statistic.indexValue(root_key, NodeTypes.M.getId(), "statistic property type", statType);
                    } catch (Exception e) {
                        // System.err.println(relt);
                        e.printStackTrace();

                        // TODO remove try catch
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                    storePool(currentNode, entryCache.getValue());
                    statistic.increaseTypeCount(root_key, NodeTypes.M.getId(), 1);

                }

            }
            statistic.saveStatistic();
            clearValues();

        }

        /**
         * The Class InterPool.
         */
        public static class InterPool {
            int numMeasurements;
            /** The ecno. */
            int[] ecno = new int[5];

            /** The rscp. */
            int[] rscp = new int[10];
        }

        /**
         * The Class IntraPool.
         */
        public static class IntraPool {
            int numMeasurements;
            /** The ecno d. */
            int[] ecnoD = new int[5];

            /** The rscp d. */
            int[] rscpD = new int[5];

            /** The position. */
            int[] position = new int[5];
        }

        /**
         * The Class RrcBestCellPool.
         */
        public static class RrcBestCellPool {

            /** The num mr for best cell. */
            int numMrForBestCellInter = 0;
            int numMrForBestCellIntra = 0;
            int numMrForBestCellIrat = 0;
            int oneWay = 0;
            int twoWay = 0;
            int threeWay = 0;

            /** The rscp ecno. */
            int[][] rscpEcno = null;

            /** The cache. */
            Map<String, Pool> cache = new HashMap<String, Pool>();

        }

        /**
         * The Class Pool.
         */
        public static class Pool {

            /** The intra pool. */
            IntraPool intraPool = null;

            /** The inter pool. */
            InterPool interPool = null;

            /**
             * Form intra pool.
             * 
             * @return the intra pool
             */
            public IntraPool formIntraPool() {
                if (intraPool == null) {
                    intraPool = new IntraPool();
                }
                return intraPool;
            }

            /**
             * Form inter pool.
             * 
             * @return the inter pool
             */
            public InterPool formInterPool() {
                if (interPool == null) {
                    interPool = new InterPool();
                }
                return interPool;
            }

        }
    }
}
