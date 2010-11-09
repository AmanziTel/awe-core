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

package org.amanzi.awe.statistic;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.neo.services.Pair;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Utility class for working with period based statistic structure
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class StatisticByPeriodStructure {
    
    /** The root. */
    private final Node root;
    
    /** The service. */
    private final GraphDatabaseService service;
    
    /** The period. */
    private final CallTimePeriods period;
    
    /** The min max. */
    private final Pair<Long, Long> minMax;
    
    /** The use cache. */
    private boolean useCache;
    private int createdNodes=0;
    
    /** The cache. */
    private final HashMap<Long,StatisticElementNodeImpl> cache=new HashMap<Long,StatisticElementNodeImpl>();
    

    /**
     * Instantiates a new statistic by period structure.
     *
     * @param root the root
     * @param service the service
     */
    public StatisticByPeriodStructure(Node root,GraphDatabaseService service){
        createdNodes=0;
        this.root = root;
        this.service = service;
        minMax=NeoUtils.getMinMaxTimeOfDataset(root, service);
        Transaction tx = service.beginTx();
        useCache=false;
        try{
            period=CallTimePeriods.findById((String)root.getProperty(StatisticNeoService.STATISTIC_PERIOD));
        }finally{
            tx.finish();
        }
    }

    
    /**
     * Gets the created nodes.
     *
     * @return the created nodes
     */
    public int getCreatedNodes() {
        return createdNodes;
    }


    /**
     * Sets the created nodes.
     *
     * @param createdNodes the new created nodes
     */
    public void setCreatedNodes(int createdNodes) {
        this.createdNodes = createdNodes;
    }


    /**
     * Gets the statistic node.
     *
     * @param time the time
     * @return the statistic node
     */
    public IStatisticElementNode getStatisticNode(Long time){
        if (time<minMax.getLeft()||time>minMax.getRight()){
            return null;
        }
        if (useCache){
            Long beginPeriod=period.getFirstTime(time);
            StatisticElementNodeImpl result=cache.get(beginPeriod);
            if (result!=null){
                return result;
            }
        }
       Transaction tx = service.beginTx();
       try{
           for (Node statNode:NeoUtils.getChildTraverser(root)){
               Pair<Long, Long> timePeriod = NeoUtils.getMinMaxTimeOfDataset(statNode, service);
               if (useCache){
                   //timePeriod.getLeft() contains start time of period
                  cache.put(timePeriod.getLeft(),new StatisticElementNodeImpl(statNode,period,timePeriod.getLeft(),timePeriod.getRight())); 
               }
               if (time>=timePeriod.getLeft()&&time<=timePeriod.getRight()){
                   return new StatisticElementNodeImpl(statNode,period,timePeriod.getLeft(),timePeriod.getRight());
               }
           }
           return null;
       }finally{
           tx.finish();
       }
       
    }

    public Set<IStatisticElementNode> getStatNedes(Long from, Long to) {
        LinkedHashSet<IStatisticElementNode> result = new LinkedHashSet<IStatisticElementNode>();
        Transaction tx = service.beginTx();
        try {
            for (Node statNode : NeoUtils.getChildTraverser(root)) {
                Pair<Long, Long> timePeriod = NeoUtils.getMinMaxTimeOfDataset(statNode, service);
                if (Math.max(from, timePeriod.getLeft()) <= Math.min(to, timePeriod.getRight())) {
                    result.add(new StatisticElementNodeImpl(statNode, period, timePeriod.getLeft(), timePeriod.getRight()));
                }
            }
            return result;
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Checks if is use cache.
     *
     * @return true, if is use cache
     */
    public boolean isUseCache() {
        return useCache;
    }
    
    /**
     * Sets the use cache. Not support period CallTimePeriods.ALL
     *
     * @param useCache the new use cache
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache&&period!=CallTimePeriods.ALL;
        if (!useCache){
            cache.clear();
        }
    }

    public Node getRoot() {
        return root;
    }
    
}
