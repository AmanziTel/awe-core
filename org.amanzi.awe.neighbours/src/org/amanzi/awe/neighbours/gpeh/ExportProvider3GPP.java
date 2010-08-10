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

package org.amanzi.awe.neighbours.gpeh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.neighbours.gpeh.Calculator3GPPdBm.ValueType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

// TODO: Auto-generated Javadoc
/**
 * TODO Purpose of
 * <p>
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportProvider3GPP implements IExportProvider {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    final SimpleDateFormat dateFormat2 = new SimpleDateFormat("HHmm");
  public static final Logger LOGGER=Logger.getLogger(ExportProvider3GPP.class);  
    /** The dataset. */
    private final Node dataset;
    
    /** The network. */
    private final Node network;
    
    /** The stat root. */
    private final Node statRoot;
    
    /** The service. */
    private final GraphDatabaseService service;
    
    /** The value3gpp. */
    private final ValueType value3gpp;
    
    /** The stat relation. */
    private final RelationshipType statRelation;
    
    /** The period. */
    private final CallTimePeriods period;
    
    /** The dataname. */
    private final String dataname;
    
    /** The headers. */
    protected List<String> headers = null;
    
    /** The model. */
    Model3GPPValue model;
    
    /** The min max. */
    private Pair<Long, Long> minMax;
    
    /** The start time. */
    private Long startTime;
    
    /** The compute time. */
    private Long computeTime;

    private final LuceneIndexService luceneService;

    /**
     * Instantiates a new export provider3 gpp.
     *
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param value3gpp the value3gpp
     * @param statRelation the stat relation
     * @param period the period
     * @param dataname the dataname
     */
    public ExportProvider3GPP(Node dataset, Node network, GraphDatabaseService service, ValueType value3gpp, RelationshipType statRelation, CallTimePeriods period, String dataname,LuceneIndexService luceneService) {
        this.dataset = dataset;
        this.network = network;
        this.service = service;
        this.value3gpp = value3gpp;
        this.statRelation = statRelation;
        this.period = period;
        this.dataname = dataname;
        this.luceneService = luceneService;
        minMax=NeoUtils.getMinMaxTimeOfDataset(dataset, service);
        startTime=period.getFirstTime(minMax.getLeft());
        computeTime=startTime;
        statRoot=defineStatRoot();
    }


    /**
     * Define statistic node.
     *
     * @return the node
     */
    private Node defineStatRoot() {//TODO check on existing statistics
        Transaction tx = NeoUtils.beginTx(service);
        try{
           Node statMain= dataset.getSingleRelationship(GpehRelationshipType.GPEH_STATISTICS, Direction.OUTGOING).getOtherNode(dataset);
           return statMain.getSingleRelationship(statRelation, Direction.OUTGOING).getOtherNode(statMain);
        }finally{
            tx.finish();
        }
    }


    /**
     * Creates the header.
     */
    protected void createHeader() {
        headers.clear();
        headers.add("Cell Name");
        headers.add("Date");
        headers.add("Time");
        headers.add("Resolution");
        createArrayHeader();
    }

    /**
     * Creates the array header.
     */
    protected void createArrayHeader() {
        for (int i = value3gpp.getMin3GPP(); i <= value3gpp.getMax3GPP(); i++) {
            headers.add(String.valueOf(value3gpp.getLeftBound(i)));
        }
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Checks for next line.
     *
     * @return true, if successful
     */
    @Override
    public boolean hasNextLine() {
        while (computeTime<minMax.getRight()||computeTime==startTime){
            if (model==null){
                computeModel();
            }   
            if (model.hasNext()){
                return true;
            }
            computeTime=period.addPeriod(computeTime);
            model.clear();
            model=null;
        }
        return false;
    }

    /**
     * Compute model.
     */
    private void computeModel() {
        if (model == null) {
            model = new Model3GPPValue();
        }
        model.clear();
        Transaction tx = NeoUtils.beginTx(service);
        try {
            
            updateModel(statRoot,computeTime, model);
        } finally {
            tx.finish();
        }

    }



    /**
     * Update model.
     *
     * @param statNode the time stat node
     * @param computeTime 
     * @param model the model
     */
    private void updateModel(Node statNode, Long computeTime, Model3GPPValue model) {
        for (Relationship relation : statNode.getRelationships(Direction.OUTGOING)) {
            Pair<Integer,Integer>ciRnc=getCiRncPair(relation);
            Node bestCell=NeoUtils.findSector(network, ciRnc.getLeft(), String.valueOf(ciRnc.getRight()), luceneService, service);
            if (bestCell==null){
                LOGGER.warn(String.format("Data not included in statistics! Not found sector with ci=%s, rnc=%s",ciRnc.getLeft(),ciRnc.getRight()));
                continue;
            }
            Node bestCellNode=relation.getEndNode();
            for (String propertyName:bestCellNode.getPropertyKeys()){
                if (StringUtils.isNumeric(propertyName)){
                   long time= Long.valueOf(propertyName);
                   if (period.compareByPeriods(computeTime, time)==0){
                       int[] values = (int[])bestCellNode.getProperty(propertyName,null);
                       model.update(bestCell, values);
                   }
                }
            }
        }       
    }


    /**
     *
     * @param relation
     * @return
     */
    private Pair<Integer, Integer> getCiRncPair(Relationship relation) {
        String[] ciRnc = relation.getType().name().split("_");
        Integer ci=Integer.valueOf(ciRnc[0]);
        Integer rnc=Integer.valueOf(ciRnc[1]);
        return new Pair<Integer, Integer>(ci,rnc);
    }


    /**
     * Gets the data name.
     *
     * @return the data name
     */
    @Override
    public String getDataName() {
        return dataname;
    }

    /**
     * Gets the next line.
     *
     * @return the next line
     */
    @Override
    public List<Object> getNextLine() {
        Pair<Node, int[]> values = model.next();
        List<Object> result=new LinkedList<Object>();
        String name = (String)values.getLeft().getProperty("userLabel", "");
        if (StringUtil.isEmpty(name)) {
            name = NeoUtils.getNodeName(values.getLeft(), service);
        }
        result.add(name);
        
        return result;
    }

    /**
     * Gets the headers.
     *
     * @return the headers
     */
    @Override
    public List<String> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<String>();
            createHeader();
        }
        return headers;
    }

    /**
     * The Class Model3GPPValue.
     */
    public static class Model3GPPValue {
        
        /** The maps. */
        private final Map<Node, int[]> maps = new LinkedHashMap<Node, int[]>();
        
        /** The iter. */
        private Iterator<Entry<Node, int[]>> iter=null;
        
        /**
         * Update.
         *
         * @param sector the sector
         * @param values the values
         */
        public void update(Node sector, int[] values) {
            iter=null;
            int[] val = maps.get(sector);
            if (val == null) {
                val = values;
                maps.put(sector, values);
                return;
            }
            for (int i = 0; i < val.length; i++) {
                val[i] += values[i];
            }
        }


        /**
         * Checks for next.
         *
         * @return true, if successful
         */
        public boolean hasNext() {
            if (iter==null){
                initIterator();
            }
            return iter.hasNext();
        }


        /**
         *
         */
        private void initIterator() {
            iter=maps.entrySet().iterator();
        }

        /**
         * Next.
         *
         * @return the list
         */
        public Pair<Node,int[]> next() {
            if (iter==null){
                initIterator(); 
            }
            Entry<Node, int[]> entry = iter.next();
            return new Pair<Node, int[]>(entry.getKey(), entry.getValue());
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty() {
            return maps.values().isEmpty();
        }

        /**
         * Clear.
         */
        public void clear() {
            maps.values().clear();
        }

    }
}
