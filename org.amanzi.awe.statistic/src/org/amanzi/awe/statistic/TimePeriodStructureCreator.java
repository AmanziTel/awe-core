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

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class TimePeriodStructureCreator {
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

    public Node createStructure() {
        if (rootNode != null) {
            return rootNode;
        }
        Transaction tx = service.beginTx();
        try {
            rootNode = StatisticNeoService.createRootNode(parent, structureId, service);
            rootNode.setProperty(StatisticNeoService.STATISTIC_PERIOD, period.getId());
            rootNode.setProperty(StatisticNeoService.STATISTIC_TIME_START, startTime);
            rootNode.setProperty(StatisticNeoService.STATISTIC_TIME_END, endTime);
            Node lastNode = null;
            Long periodTime = startTime;
            do {
                Long periodEnd =period==CallTimePeriods.ALL?endTime:period.getLastTime(periodTime);
                IStatisticElement statElem = sourceHandler.getStatisics(periodTime, periodEnd);
                if (statElem != null) {
                    Node node=service.createNode();
                    node.setProperty(StatisticNeoService.STATISTIC_TIME_START, periodTime);
                    node.setProperty(StatisticNeoService.STATISTIC_TIME_END, periodEnd);
                    NeoUtils.addChild(rootNode, node, lastNode, service);
                    statStore.storeStatisticElement(statElem,node);
                    lastNode=node;
                    //TODo add recreate transaction
                }
            } while ((periodTime = period.addPeriod(periodTime)) < endTime);
            tx.success();
            return rootNode;
        } finally {
            tx.finish();
        }

    }
}
