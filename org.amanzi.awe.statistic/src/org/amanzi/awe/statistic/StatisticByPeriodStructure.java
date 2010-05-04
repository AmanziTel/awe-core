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

import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
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
    
    /** The cache. */
    private final HashMap<Long,StatisticElementNodeImpl> cache=new HashMap<Long,StatisticElementNodeImpl>();
    

    /**
     * Instantiates a new statistic by period structure.
     *
     * @param root the root
     * @param service the service
     */
    public StatisticByPeriodStructure(Node root,GraphDatabaseService service){
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
    
}
