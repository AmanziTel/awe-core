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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Create time based structure
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class TimePeriodStructureCreator {
    private static final Logger LOGGER=Logger.getLogger(TimePeriodStructureCreator.class);
    private Node rootNode;
    private final Long startTime;
    private final Long endTime;
    private final CallTimePeriods period;
    private final IStatisticHandler sourceHandler;
    private final GraphDatabaseService service;
    private final String structureId;
    private final Node parent;
    private final IStatisticStore statStore;

    public TimePeriodStructureCreator(Node parent, String structureId, Long startTime, Long endTime, CallTimePeriods period,
            IStatisticHandler sourceHandler, IStatisticStore statStore, GraphDatabaseService service) {
        this.parent = parent;
        this.structureId = structureId;
        this.statStore = statStore;
        this.service = service;
        this.startTime = period.getFirstTime(startTime);
        this.endTime = period.getLastTime(endTime);
        this.period = period;
        this.sourceHandler = sourceHandler;
        rootNode = StatisticNeoService.findRootNode(parent, structureId);

    }

    public StatisticByPeriodStructure createStructure() {
        if (rootNode != null) {
            return new StatisticByPeriodStructure(rootNode, service);
        }
        Transaction tx = service.beginTx();
        try {
            rootNode = StatisticNeoService.createRootNode(parent, structureId, service);
            rootNode.setProperty(StatisticNeoService.STATISTIC_PERIOD, period.getId());
            rootNode.setProperty(INeoConstants.MIN_TIMESTAMP, startTime);
            rootNode.setProperty(INeoConstants.MAX_TIMESTAMP, endTime);
            int createdNodes=0;
            Node lastNode = null;
            Long periodTime = startTime;
            do {
                Long periodEnd =period==CallTimePeriods.ALL?endTime:period.getLastTime(periodTime);
                IStatisticElement statElem = sourceHandler.getStatisics(periodTime, periodEnd);
                if (statElem != null) {
                    Node node=service.createNode();
                    node.setProperty(INeoConstants.MIN_TIMESTAMP, periodTime);
                    node.setProperty(INeoConstants.MAX_TIMESTAMP, periodEnd);
                    NeoUtils.addChild(rootNode, node, lastNode, service);
                    statStore.storeStatisticElement(statElem, node);
                    lastNode=node;
                    createdNodes+=1+statStore.getStoredNodesCount();
                    //TODo add recreate transaction
                }
            } while ((periodTime = period.addPeriod(periodTime)) < endTime);
            tx.success();
            StatisticByPeriodStructure statisticByPeriodStructure = new StatisticByPeriodStructure(rootNode, service);
            statisticByPeriodStructure.setCreatedNodes(createdNodes);
            return statisticByPeriodStructure;
        } finally {
            tx.finish();
        }

    }
}
