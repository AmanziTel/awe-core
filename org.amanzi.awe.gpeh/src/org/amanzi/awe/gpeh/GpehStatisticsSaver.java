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
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.RrcMeasurement;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
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
    private DatasetInfo datasetInfoStatistic;
    private DatasetInfo datasetInfoEvent;
    private String eventIndexNameStatistic;
    private String eventIndexNameEvent;
    private final HashMap<String, Object> asnDataMap = new HashMap<String, Object>();
    private final AsnEventListener asnListener = new AsnEventListener();
    private final List<RrcMeasurement> meas = new LinkedList<RrcMeasurement>();
    private RrcMeasurement lastMeasurement;
    private GpehStatisticModel statmodel;
    private GpehStatisticModel statmodelEvent;
    private Transaction tx;
    private long oldTimestamp;
    private PrintStream outputStream;
    private final MetaData metadata;
    private Node datasetStatistic;
    private Node datasetEvent;
    /**
     * Instantiates a new gpeh saver.
     */
    public GpehStatisticsSaver() {
        metadata=new MetaData("oss", "gpeh");
    }

    @Override
    public void save(GpehTransferData dataElement) {
        boolean isIMSI=false;
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
        
        switch (event.getType()) {
        case RRC_MEASUREMENT_REPORT:
            datasetNode=datasetStatistic;
        	datasetInfoStatistic.updateTimestamp(fullTime);
            processRrcMeasurementReport(dataElement, fullTime);
            
            break;
        case INTERNAL_RADIO_QUALITY_MEASUREMENTS_RNH:
            datasetInfoStatistic.updateTimestamp(fullTime);
        	
            processNbapMeasurementReport(dataElement, fullTime);
            
            break;
        case INTERNAL_IMSI:
            datasetInfoEvent.updateTimestamp(fullTime);
            isIMSI=true;
        	processIMSIReport(dataElement,fullTime);
        	
        	break;
        default:
            break;
        }
        if ((Math.abs(fullTime-oldTimestamp)>15*60*1000)&&!isIMSI){
            if (oldTimestamp!=0){
            	statmodel.saveCacheStatistic();
                tx.success();
                tx.finish();
                tx=DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
            }
            oldTimestamp=fullTime;
        }else if(isIMSI){
        	 if (Math.abs(fullTime-oldTimestamp)>3*60*1000){
        		 if(oldTimestamp!=0){
        			 statmodelEvent.saveEventStatistic();
        			 tx.success();
        			 tx.finish();
        			 tx=DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        		 }
        		 oldTimestamp=fullTime;
        	 }
        }
    }

    private void processIMSIReport(GpehTransferData dataElement, long timestamp) {
    	  GpehStatisticModel model = getStatisticReportEvent();
          model.processIMSIEvent(dataElement, timestamp);
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
        String datasetNameStat = (String)element.get(GpehTransferData.DATASET)+" Statistic";
        String datasetNameEvent = (String)element.get(GpehTransferData.DATASET)+" Event";
         tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
            Node projectNode = datasetService.findOrCreateAweProject(projectName);
            datasetStatistic = datasetService.getRootNode(projectName, datasetNameStat, NodeTypes.DATASET);
            datasetStatistic.setProperty("drive_type", "oss");
            datasetStatistic.setProperty("min_timestamp", 0L);
            datasetStatistic.setProperty("max_timestamp", 0L);
            
         
            datasetEvent=datasetService.createNode(NodeTypes.DATASET);
            datasetEvent.setProperty(INeoConstants.PROPERTY_NAME_NAME, datasetNameEvent);
            datasetEvent.setProperty("drive_type", "oss");
            datasetEvent.setProperty("min_timestamp", 0L);
            datasetEvent.setProperty("max_timestamp", 0L);
            datasetStatistic.createRelationshipTo(datasetEvent, GeoNeoRelationshipTypes.VIRTUAL_DATASET);
        datasetInfoStatistic = new DatasetInfo(datasetStatistic);
        eventIndexNameStatistic = new StringBuilder("Id").append(datasetStatistic.getId()).append("@").append("gpeh_event").append("@").append("name").toString();
        datasetInfoEvent = new DatasetInfo(datasetEvent);
        eventIndexNameEvent = new StringBuilder("Id").append(datasetEvent.getId()).append("@").append("gpeh_event").append("@").append("name").toString();

    }

    /**
     * Gets the statistic report.
     * 
     * @return the statistic report
     */
    public synchronized GpehStatisticModel getStatisticReport() {
        if (statmodel == null) {
            if (datasetStatistic.hasRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)) {
                Node statNode = datasetStatistic.getSingleRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)
                        .getEndNode();
                statmodel=new GpehStatisticModel(datasetStatistic, statNode, datasetService.getGraphDatabaseService());
                return new GpehStatisticModel(datasetStatistic, statNode, datasetService.getGraphDatabaseService());
            } else {
                // Node statNode = databaseService.createNode();
                // datasetNode.createRelationshipTo(statNode, DatasetRelationshipTypes.GPEH_STATISTICS);
                // return new GpehStatisticModel(datasetNode, statNode, databaseService);
            	 statmodel=new GpehStatisticModel(datasetStatistic, datasetStatistic, datasetService.getGraphDatabaseService());
                return statmodel;
            }
        }
        return statmodel;
    }
    public synchronized GpehStatisticModel getStatisticReportEvent() {
        if (statmodelEvent == null) {
            if (datasetEvent.hasRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)) {
                Node statNode = datasetEvent.getSingleRelationship(DatasetRelationshipTypes.STATISTICS, Direction.OUTGOING)
                        .getEndNode();
                statmodelEvent=new GpehStatisticModel(datasetEvent, statNode, datasetService.getGraphDatabaseService());
                return new GpehStatisticModel(datasetEvent, statNode, datasetService.getGraphDatabaseService());
            } else {
                // Node statNode = databaseService.createNode();
                // datasetNode.createRelationshipTo(statNode, DatasetRelationshipTypes.GPEH_STATISTICS);
                // return new GpehStatisticModel(datasetNode, statNode, databaseService);
            	statmodelEvent=new GpehStatisticModel(datasetEvent, datasetEvent, datasetService.getGraphDatabaseService());
                return statmodelEvent;
            }
        }
        return statmodelEvent;
    }
    @Override
    public void finishUp(GpehTransferData element) {
        try {
            getStatisticReport().flush();
            // TODO remove it after fixing bug in batch inserter
            datasetStatistic = DatabaseManager.getInstance().getCurrentDatabaseService().getNodeById(datasetStatistic.getId());
            datasetEvent = DatabaseManager.getInstance().getCurrentDatabaseService().getNodeById(datasetEvent.getId());
            try {
                System.out.println(String.format("min_timestamp %s", datasetInfoStatistic.getMinTimestamp()));
                datasetStatistic.setProperty("min_timestamp", datasetInfoStatistic.getMinTimestamp());
                System.out.println(String.format("max_timestamp %s", datasetInfoStatistic.getMaxTimestamp()));
                datasetStatistic.setProperty("max_timestamp", datasetInfoStatistic.getMaxTimestamp());
               
                System.out.println(String.format("min_timestamp %s", datasetInfoEvent.getMinTimestamp()));
                datasetEvent.setProperty("min_timestamp", datasetInfoEvent.getMinTimestamp());
                System.out.println(String.format("max_timestamp %s", datasetInfoEvent.getMaxTimestamp()));
                datasetEvent.setProperty("max_timestamp", datasetInfoEvent.getMaxTimestamp());
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
