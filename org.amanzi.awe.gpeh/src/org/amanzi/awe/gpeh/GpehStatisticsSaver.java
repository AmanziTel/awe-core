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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.gpeh.parser.Events;
import org.amanzi.awe.gpeh.parser.Parameters;
import org.amanzi.awe.gpeh.parser.internal.GPEHEvent.Event;
import org.amanzi.awe.l3messages.AsnParserEvent;
import org.amanzi.awe.l3messages.IAsnParserListener;
import org.amanzi.awe.l3messages.MessageDecoder;
import org.amanzi.awe.neighbours.GpehReportUtil;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.DatasetInfo;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.RrcMeasurement;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Saver for gpeh data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class GpehStatisticsSaver implements ISaver<GpehTransferData> {
    protected DatasetService datasetService;
    private Node datasetNode;
    private DatasetInfo datasetInfo;
    private String eventIndexName;
    private final HashMap<String, Object> asnDataMap = new HashMap<String, Object>();
    private final AsnEventListener asnListener = new AsnEventListener();
    private final List<RrcMeasurement> meas = new LinkedList<RrcMeasurement>();
    private RrcMeasurement lastMeasurement;
    private GpehStatisticModel statmodel;
    private Transaction tx;
    private long oldTimestamp;
    private PrintStream outputStream;
    private final MetaData metadata;
    
    /**
     * Instantiates a new gpeh saver.
     */
    public GpehStatisticsSaver() {
        metadata=new MetaData("oss", "gpeh");
    }

    @Override
    public void save(GpehTransferData dataElement) {
        if (dataElement == null) {
            return;
        }
        Event event = (Event)dataElement.get(GpehTransferData.EVENT);
        if (event == null) {
            return;
        }
        Long timestamp = (Long)dataElement.get(GpehTransferData.TIMESTAMP_OF_DAY);
        if (timestamp == null) {
            return;
        }

        Object content = dataElement.get(Parameters.EVENT_PARAM_MESSAGE_CONTENTS.name());
        if (content != null) {
            if (event.getType() == Events.RRC_MEASUREMENT_REPORT) {
                byte[] messageContent = (byte[])content;

                asnDataMap.clear();
                meas.clear();
                lastMeasurement = new RrcMeasurement();
                MessageDecoder.getInstance().parseRRCMeasurementReport(messageContent, asnListener);
                if (!lastMeasurement.isEmpty()) {
                    meas.add(lastMeasurement);
                }
                if (!meas.isEmpty()) {
                    asnDataMap.put(GpehReportUtil.MEAS_LIST, meas);
                }
                dataElement.putAll(asnDataMap);
            }
        }
        long fullTime = event.getFullTime(timestamp);
        datasetInfo.updateTimestamp(fullTime);
        switch (event.getType()) {
        case RRC_MEASUREMENT_REPORT:
            processRrcMeasurementReport(dataElement, fullTime);
            break;
        case INTERNAL_RADIO_QUALITY_MEASUREMENTS_RNH:
            processNbapMeasurementReport(dataElement, fullTime);
            break;
        default:
            break;
        }
        if (Math.abs(fullTime-oldTimestamp)>15*60*1000){
            if (oldTimestamp!=0){
                tx.success();
                tx.finish();
                tx=DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
            }
            oldTimestamp=fullTime;
        }
    }

    /**
     * Process nbap measurement report.
     * 
     * @param dataElement the data element
     * @param indexInfo the index info
     */
    private void processNbapMeasurementReport(GpehTransferData dataElement, long timestamp) {
        GpehStatisticModel model = getStatisticReport();
        model.processNbapEvent(dataElement, timestamp);

    }

    private void processRrcMeasurementReport(GpehTransferData dataElement, long timestamp) {
        GpehStatisticModel model = getStatisticReport();
        model.processRrcEvent(dataElement, timestamp);
    }

    @Override
    public void init(GpehTransferData element) {
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
        String projectName = (String)element.get(GpehTransferData.PROJECT);
        String datasetName = (String)element.get(GpehTransferData.DATASET);
         tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
            Node projectNode = datasetService.findOrCreateAweProject(projectName);
            datasetNode = datasetService.getRootNode(projectName, datasetName, NodeTypes.DATASET);
            datasetNode.setProperty("drive_type", "oss");
            datasetNode.setProperty("min_timestamp", 0L);
            datasetNode.setProperty("max_timestamp", 0L);
        datasetInfo = new DatasetInfo(datasetNode);

        eventIndexName = new StringBuilder("Id").append(datasetNode.getId()).append("@").append("gpeh_event").append("@").append("name").toString();

    }

    /**
     * Gets the statistic report.
     * 
     * @return the statistic report
     */
    public synchronized GpehStatisticModel getStatisticReport() {
        if (statmodel == null) {
            
            
            if (datasetNode.hasRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)) {
                Node statNode = datasetNode.getSingleRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)
                        .getEndNode();
                statmodel=new GpehStatisticModel(datasetNode, statNode, datasetService.getGraphDatabaseService());
                return new GpehStatisticModel(datasetNode, statNode, datasetService.getGraphDatabaseService());
            } else {
                // Node statNode = databaseService.createNode();
                // datasetNode.createRelationshipTo(statNode, DatasetRelationshipTypes.GPEH_STATISTICS);
                // return new GpehStatisticModel(datasetNode, statNode, databaseService);
            	 statmodel=new GpehStatisticModel(datasetNode, datasetNode, datasetService.getGraphDatabaseService());
                return statmodel;
            }
        }
        return statmodel;
    }

    @Override
    public void finishUp(GpehTransferData element) {
        try {
            getStatisticReport().flush();
            // TODO remove it after fixing bug in batch inserter
            datasetNode = DatabaseManager.getInstance().getCurrentDatabaseService().getNodeById(datasetNode.getId());
            try {
                System.out.println(String.format("min_timestamp %s", datasetInfo.getMinTimestamp()));
                datasetNode.setProperty("min_timestamp", datasetInfo.getMinTimestamp());
                System.out.println(String.format("max_timestamp %s", datasetInfo.getMaxTimestamp()));
                datasetNode.setProperty("max_timestamp", datasetInfo.getMaxTimestamp());
                tx.success();
            } finally {
                tx.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AsnEventListener implements IAsnParserListener {

        @Override
        public boolean processEvent(AsnParserEvent event) {
            switch (event.getClassType()) {
            case INTEGER:
                if (event.getClassName().equals("PrimaryScramblingCode")) {
                    if (lastMeasurement.getScrambling() != null) {
                        meas.add(lastMeasurement);
                        lastMeasurement = new RrcMeasurement();
                    }
                    lastMeasurement.setScrambling(event.getValue().toString());
                } else {
                    int value = ((Long)event.getValue()).intValue();
                    if (event.getClassName().equals("CPICH-RSCP")) {
                        if (lastMeasurement.getRscp() != null) {
                            meas.add(lastMeasurement);
                            lastMeasurement = new RrcMeasurement();
                        }
                        lastMeasurement.setRscp(value);
                    } else if (event.getClassName().equals("CPICH-Ec-N0")) {
                        if (lastMeasurement.getEcNo() != null) {
                            meas.add(lastMeasurement);
                            lastMeasurement = new RrcMeasurement();
                        }
                        lastMeasurement.setEcNo(value);
                    }
                    // else if (event.getElementName().equals("verifiedBSIC")) {
                    // lastMeasurement.setBsic(value);
                    // meas.add(lastMeasurement);
                    // lastMeasurement=new RrcMeasurement();
                    // }
                    else if (event.getClassName().equals("UE-TransmittedPower")) {
                        if (lastMeasurement.getUeTxPower() != null) {
                            meas.add(lastMeasurement);
                            lastMeasurement = new RrcMeasurement();
                        }
                        lastMeasurement.setUeTxPower(value);
                    }
                }
                break;
            case SEQUENCE:
                if (event.getClassName().equals("MeasurementReport")) {
                    if (event.getValue().equals("EventResults")) {
                        return false;
                    } else if (event.getValue().equals("MeasuredResultsOnRACH")) {
                        return false;
                    } else if (event.getElementName().contains("Extensions")) {
                        return false;
                    }
                }
                break;
            case CHOICE:
                if (event.getClassName().equals("UL-DCCH-MessageType")) {
                    if (!event.getValue().equals("MeasurementReport")) {
                        return false;
                    } else if (event.getValue().equals("MeasurementReport-")) {
                        return false;
                    }
                } else if (event.getClassName().startsWith("MeasuredResults") || event.getClassName().startsWith("EventResult")) {
                    String value = (String)event.getValue();
                    if (value.contains("InterFreq")) {
                        if (!asnDataMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnDataMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTERF);
                        }
                    } else if (value.contains("InterRAT")) {
                        if (!asnDataMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnDataMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_IRAT);
                        }
                    } else if (value.contains("IntraFreq")) {
                        if (!asnDataMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnDataMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTRAF);
                        }
                    } else if (value.contains("UE-Internal")) {
                        if (!asnDataMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnDataMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_UE_INTERNAL);
                        }
                    } else {
                        return false;
                    }
                }
                break;
            }
            return true;
        }

    }

    @Override
    public PrintStream getPrintStream() {
        if (outputStream==null){
            return System.out;
        }
        return outputStream;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[]{metadata});
    }

}
