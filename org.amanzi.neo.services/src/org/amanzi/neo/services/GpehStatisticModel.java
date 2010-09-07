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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            processUlRtwp(ci, rnc, value, timestamp);
            break;
        case 3:
            processTotalDlTxPower(ci, rnc, value, timestamp);
            break;
        case 4:
            processR99DlTxPower(ci, rnc, value, timestamp);
            break;
        case 5:
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
        Node result = tableRoot.get(type);
        if (result == null) {
            result = findOrCreateTableRoot(type);
            tableRoot.put(type, result);
        }
        return result;
    }

    /**
     * Find or create table root.
     * 
     * @param type the type
     * @return the i node
     */
    private Node findOrCreateTableRoot(GpehRelationType type) {
        if (statisticNode.hasRelationship(type, Direction.OUTGOING)) {
            return statisticNode.getSingleRelationship(type, Direction.OUTGOING).getEndNode();
        }
        Node result = databaseService.createNode();
        statisticNode.createRelationshipTo(result, type);
        return result;
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
            handleInterFrMeasurement(ci, rnc, timestamp, measList, dataElement);
        } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {

            handleIntraFrMeasurement(ci, rnc, timestamp, measList, dataElement);
        } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
            return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_UE_INTERNAL)) {
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
    private void handleIUeTxPower(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList, HashMap<String, Object> dataElement) {
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
    private void handleIntraFrMeasurement(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList, HashMap<String, Object> dataElement) {
        RrcMeasurement bestCellMeas = measList.get(0);
        if (bestCellMeas.getEcNo() == null || bestCellMeas.getEcNo() > 49 || bestCellMeas.getRscp() == null || bestCellMeas.getRscp() > 91) {
            return;
        }
        RRCCache cache = defineRrcCache(timestamp);
        cache.updateIntraFr(ci, rnc, measList,dataElement);
    }

    /**
     * Define rrc cache.
     * 
     * @param timestamp the timestamp
     * @return the rRC cache
     */
    public RRCCache defineRrcCache(Long timestamp) {
        Node root = getStatTableRoot(GpehRelationType.RRC);
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
    private void handleInterFrMeasurement(Integer ci, Integer rnc, Long timestamp, List<RrcMeasurement> measList, HashMap<String, Object> dataElement) {
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

        /** The begin timestamp. */
        protected long beginTimestamp;

        /** The root. */
        protected Node root;

        /**
         * Form best cell.
         * 
         * @param rel the rel
         * @return the best cell
         */
        protected BestCell formBestCell(Relationship rel) {
            String[] id = rel.getType().name().split("_");
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
            this.root = root;
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
            for (Relationship rel : root.getRelationships(Direction.OUTGOING)) {
                BestCell cell = formBestCell(rel);
                if (cell != null) {
                    NbapPool pool = loadPool(startTime, rel.getEndNode());
                    cellCache.put(cell, pool);
                }
            }
        }

        /**
         * @param startTime
         * @param endNode
         * @return
         */
        protected NbapPool loadPool(long startTime, Node statNode) {
            NbapPool result = new NbapPool();
            result.values = (int[])statNode.getProperty(String.valueOf(startTime), null);
            return result;
        }

        /**
         * Save.
         * 
         * @param service the service
         */
        @Override
        public void save(GraphDatabaseService service) {
            for (Map.Entry<BestCell, NbapPool> entry : cellCache.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                RelationshipType reltype = getCellRelation(entry.getKey());
                Node result;
                if (root.hasRelationship(reltype, Direction.OUTGOING)) {
                    result = root.getSingleRelationship(reltype, Direction.OUTGOING).getEndNode();
                } else {
                    result = service.createNode();
                    root.createRelationshipTo(result, reltype);
                }
                savePool(beginTimestamp, result, entry.getValue());

            }
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
            if (nbapPool.values!=null){
                statNode.setProperty(String.valueOf(beginTimestamp), nbapPool.values);
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

        /** The main cache. */
        private final Map<BestCell, RrcBestCellPool> mainCache = new HashMap<GpehStatisticModel.BestCell, RrcBestCellPool>();

        /**
         * Instantiates a new rRC cache.
         * 
         * @param root the root
         * @param timestamp the timestamp
         */
        public RRCCache(Node root, Long timestamp) {
            this.root = root;
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
        public void updateIntraFr(Integer ci, Integer rnc, List<RrcMeasurement> measList, HashMap<String,Object> dataElement) {
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
            for (Relationship rel : root.getRelationships(Direction.OUTGOING)) {
                BestCell cell = formBestCell(rel);
                if (cell != null) {
                    Node cellRoot = rel.getEndNode();
                    RrcBestCellPool bestCell = loadBestCellStat(cellRoot);
                    mainCache.put(cell, bestCell);
                    Map<String, Pool> cache = getCacheMap(cell);
                    for (Relationship pscRel : cellRoot.getRelationships(Direction.OUTGOING)) {
                        Pool pool = loadPoolFromNode(pscRel.getEndNode());
                        cache.put(pscRel.getType().name(), pool);
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
            result.numMrForBestCellInter = (Integer)cellRoot.getProperty("numMrForBestCellInter" + beginTimestamp, 0);
            result.numMrForBestCellIntra = (Integer)cellRoot.getProperty("numMrForBestCellIntra" + beginTimestamp, 0);
            result.numMrForBestCellIrat = (Integer)cellRoot.getProperty("numMrForBestCellIrat" + beginTimestamp, 0);
            result.oneWay = (Integer)cellRoot.getProperty("oneWay", 0);
            result.twoWay = (Integer)cellRoot.getProperty("twoWay", 0);
            result.threeWay = (Integer)cellRoot.getProperty("threeWay", 0);
            if (cellRoot.hasProperty(String.format("%srscp%s", beginTimestamp, 0))) {
                result.rscpEcno = new int[92][50];
                for (int rscp = 0; rscp <= 91; rscp++) {
                    result.rscpEcno[rscp] = (int[])cellRoot.getProperty(String.format("%srscp%s", beginTimestamp, rscp));
                }
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
            cellRoot.setProperty("twoWay", cell.twoWay);
            cellRoot.setProperty("threeWay", cell.threeWay);
            cellRoot.setProperty("numMrForBestCellInter" + beginTimestamp, cell.numMrForBestCellInter);
            cellRoot.setProperty("numMrForBestCellIntra" + beginTimestamp, cell.numMrForBestCellIntra);
            cellRoot.setProperty("numMrForBestCellIrat" + beginTimestamp, cell.numMrForBestCellIrat);
            if (cell.rscpEcno != null) {
                for (int rscp = 0; rscp <= 91; rscp++) {
                    cellRoot.setProperty(String.format("%srscp%s", beginTimestamp, rscp), cell.rscpEcno[rscp]);
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
                node.setProperty("interMr" + beginTimestamp, pool.interPool.numMeasurements);
                node.setProperty("numMr" + beginTimestamp, pool.interPool.numMeasurements);
                node.setProperty("interEcno" + beginTimestamp, pool.interPool.ecno);
                node.setProperty("interRscp" + beginTimestamp, pool.interPool.rscp);
            }
            if (pool.intraPool != null) {
                node.setProperty("intraMr" + beginTimestamp, pool.intraPool.numMeasurements);
                node.setProperty("intraEcnoD" + beginTimestamp, pool.intraPool.ecnoD);
                node.setProperty("intraRscpD" + beginTimestamp, pool.intraPool.rscpD);
                node.setProperty("positions" + beginTimestamp, pool.intraPool.position);
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
            if (node.hasProperty(ekno)) {
                InterPool inter = result.formInterPool();
                inter.ecno = (int[])node.getProperty(ekno);
                inter.rscp = (int[])node.getProperty(rscp);
                inter.numMeasurements = (Integer)node.getProperty(numMr);
            }
            String intraKey = "intraEcnoD" + beginTimestamp;
            numMr = "intraMr" + beginTimestamp;
            if (node.hasProperty(intraKey)) {
                IntraPool intra = result.formIntraPool();
                intra.ecnoD = (int[])node.getProperty(intraKey);
                intra.rscpD = (int[])node.getProperty("intraRscpD" + beginTimestamp);
                intra.position = (int[])node.getProperty("positions" + beginTimestamp);
                intra.numMeasurements = (Integer)node.getProperty(numMr);
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
            for (Map.Entry<BestCell, RrcBestCellPool> entry : mainCache.entrySet()) {
                RelationshipType reltype = getCellRelation(entry.getKey());
                Node cellRoot;
                if (root.hasRelationship(reltype, Direction.OUTGOING)) {
                    cellRoot = root.getSingleRelationship(reltype, Direction.OUTGOING).getEndNode();
                } else {
                    cellRoot = service.createNode();
                    root.createRelationshipTo(cellRoot, reltype);
                }
                storeBestCellStat(cellRoot, entry.getValue());
                for (Map.Entry<String, Pool> entryCache : entry.getValue().cache.entrySet()) {
                    if (entryCache.getValue() == null) {
                        continue;
                    }
                    RelationshipType relt = getCellRelation(entryCache.getKey());
                    if (relt == null || entryCache.getKey() == null) {
                        System.err.println("Null rel");
                    }
                    Node pscRoot;
                    try {
                        if (cellRoot.hasRelationship(relt, Direction.OUTGOING)) {
                            pscRoot = cellRoot.getSingleRelationship(relt, Direction.OUTGOING).getEndNode();
                        } else {
                            pscRoot = service.createNode();
                            cellRoot.createRelationshipTo(pscRoot, relt);
                        }
                    } catch (Exception e) {
                        System.err.println(relt);
                        e.printStackTrace();

                        // TODO remove try catch
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                    storePool(pscRoot, entryCache.getValue());
                }
            }
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
