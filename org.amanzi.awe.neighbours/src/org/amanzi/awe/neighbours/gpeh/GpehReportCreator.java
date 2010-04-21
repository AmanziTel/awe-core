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

import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
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
public class GpehReportCreator {
    
    /** The service. */
    private final GraphDatabaseService service;
    
    /** The model. */
    private final GpehReportModel model;
    
    /** The lucene service. */
    private final LuceneIndexService luceneService;
    
    /** The network. */
    private final Node network;
    
    /** The gpeh. */
    private final Node gpeh;

    /**
     * Instantiates a new gpeh report creator.
     *
     * @param network the network
     * @param gpeh the gpeh
     * @param service the service
     * @param luceneService the lucene service
     */
    public GpehReportCreator(Node network, Node gpeh, GraphDatabaseService service, LuceneIndexService luceneService) {
        this.network = network;
        this.gpeh = gpeh;
        this.service = service;
        this.luceneService = luceneService;
        model = new GpehReportModel(network, gpeh, service);
    }

    /**
     * Gets the report model.
     *
     * @return the report model
     */
    public GpehReportModel getReportModel() {
        if (model.getRoot() == null) {
            Transaction tx = service.beginTx();
            try {
                createReportModel();
                tx.success();
            } finally {
                tx.finish();
            }
        }
        return model;
    }

    /**
     * Creates the report model.
     */
    private void createReportModel() {
        if (model.getRoot() != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        Node reports = service.createNode();
        model.getGpeh().createRelationshipTo(reports, ReportsRelations.REPORTS);
        model.getNetwork().createRelationshipTo(reports, ReportsRelations.REPORTS);
        model.findRootNode();
    }

    /**
     * Creates the matrix.
     */
    public void createMatrix() {
        if (model.getIntraFrequencyICDM() != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        Transaction tx = service.beginTx();
        try {
            createReportModel();
            Node intraFMatrix = service.createNode();
            Node interFMatrix = service.createNode();
            Node iRATMatrix = service.createNode();
            model.getRoot().createRelationshipTo(intraFMatrix, ReportsRelations.ICDM_INTRA_FR);
            model.getRoot().createRelationshipTo(interFMatrix, ReportsRelations.ICDM_INTER_FR);
            model.getRoot().createRelationshipTo(iRATMatrix, ReportsRelations.ICDM_IRAT);
            String eventIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getGpehEventsName(), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.GPEH_EVENT);
            for (Node eventNode : luceneService.getNodes(eventIndName, Events.RRC_MEASUREMENT_REPORT.name())) {
                Set<Node> activeSet = getActiveSet(eventNode);
                Set<RrcMeasurement> measSet = getRncMeasurementSet(eventNode);
                Node bestCell=getBestCell(activeSet,measSet);
            }
            tx.success();
        } finally {
            tx.finish();
        }

    }


    /**
     * Gets the best cell.
     *
     * @param activeSet the active set
     * @param measSet the meas set
     * @return the best cell
     */
    private Node getBestCell(Set<Node> activeSet, Set<RrcMeasurement> measSet) {
        if (activeSet.size()<2){
            return activeSet.isEmpty()?null:activeSet.iterator().next();
        }
        Pair<Node,RrcMeasurement>bestCell=new Pair<Node, RrcMeasurement>(null, null);
        for (RrcMeasurement meas:measSet ){
            if (meas.getScrambling()==null||meas.getEcNo()==null){
                continue;
            }
            
        }
        return bestCell.getLeft();
    }

    /**
     * Gets the rnc measurement set.
     *
     * @param eventNode the event node
     * @return the rnc measurement set
     */
    private Set<RrcMeasurement> getRncMeasurementSet(Node eventNode) {
        Set<RrcMeasurement> result = new HashSet<RrcMeasurement>();
        int id = 0;
        Integer psc;
        Integer rscp;
        Integer ecNo;
        Integer bsic;
        while(true) {
            id++;
            psc = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_SCRAMBLING_PREFIX + id, null);
            rscp = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_RSCP_PREFIX + id, null);
            ecNo = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_ECNO_PREFIX + id, null);
            bsic = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_BSIC_PREFIX + id, null);
            if (psc != null || rscp != null || ecNo != null || bsic != null){
                result.add(new RrcMeasurement(psc, rscp, ecNo, bsic));
            }else {
                break;
            }
        } 
        return result;
    }

    /**
     * Gets the active set.
     *
     * @param eventNode the event node
     * @return the active set
     */
    private Set<Node> getActiveSet(Node eventNode) {
        Set<Node> result = new HashSet<Node>();
        for (int id = 1; id <= 4; id++) {
            Integer ci = (Integer)eventNode.getProperty("EVENT_PARAM_C_ID_" + id, null);
            Integer rnc = (Integer)eventNode.getProperty("EVENT_PARAM_RNC_ID_" + id, null);
            if (ci == null || rnc == null) {
                continue;
            }
            Node asNode = NeoUtils.findSector(model.getNetworkName(), ci, String.valueOf(rnc), luceneService, service);
            if (asNode != null) {
                result.add(asNode);
            }
        }
        return result;
    }
}
