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

package org.amanzi.awe.cassidian.saver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.cassidian.collector.AbstractCall;
import org.amanzi.awe.cassidian.collector.CallCollector;
import org.amanzi.awe.cassidian.collector.CellReselCall;
import org.amanzi.awe.cassidian.collector.HandoverCall;
import org.amanzi.awe.cassidian.collector.ItsiAttachCall;
import org.amanzi.awe.cassidian.collector.MessageCall;
import org.amanzi.awe.cassidian.collector.RealCall;
import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;
import org.amanzi.awe.cassidian.enums.CallProperties;
import org.amanzi.awe.cassidian.enums.CallProperties.CallType;
import org.amanzi.awe.cassidian.enums.ProbeCallRelationshipType;
import org.amanzi.awe.cassidian.structure.AbstractTOCTTC;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;
import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.networkModel.IModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class AMSXMLSaver implements ISaver<IModel, IData, IConfiguration> {
    private static Logger LOGGER = Logger.getLogger(AMSXMLSaver.class);
    private Node mainDatasetNode;
    private Node callDatasetNode;
    private DriveModel tocTTCModel;
    private DriveModel callsModel;
    private String fileName;
    private DriveModel pesqModel;
    private CallCollector collector;
    private Node projectNode;
    private static DatasetService datasetService;
    private Node currentNode;
    private Map<String, Object> propertyMap;
    private GraphDatabaseService graphDb;
    private float delay;
    private float lq;
    private final static String PESQ_NAME = "pesq";
    private final static String NTPQ_NAME = "ntpq";
    private final static String TOC_NAME = "Toc";
    private final static String TTC_NAME = "Ttc";
    private final static String PROBE_TYPE = "probe_type";

    private IConfiguration configuration;

    /**
     * initialize required variables;
     * 
     * @param projectName
     * @param gisName
     */
    public AMSXMLSaver(String projectName, String gisname) {
        super();
        graphDb = NeoServiceProvider.getProvider().getService();

        getDatasetService();
        projectNode = datasetService.findOrCreateAweProject(projectName);
        mainDatasetNode = findOrCreateDatasetNodes(projectNode, gisname);
        try {
            callDatasetNode = findOrCreateVirtualDataset(mainDatasetNode, gisname + " Calls");
            currentNode = callDatasetNode;
        } catch (Exception e) {
            LOGGER.error("Error in saver initialization", e);
        } finally {
        }
    }

    /**
     * 
     */
    public AMSXMLSaver() {
        super();
    }

    /**
     * initialize dataset service
     * 
     * @return
     */
    private void getDatasetService() {
        if (datasetService == null) {
            datasetService = new DatasetService();
        }
    }

    /**
     * find or create gisNode
     */
    private Node findOrCreateDatasetNodes(Node root, String name) {
        Node dataset = null;
        dataset = datasetService.findChildByName(root, name);
        if (dataset == null) {
            dataset = datasetService.createNode(NodeTypes.DATASET, name);
            root.createRelationshipTo(dataset, NetworkRelationshipTypes.CHILD);
        }
        return dataset;
    }

    /**
     * find virtual dataset from current dataset
     * 
     * @param root -current dataset
     * @param name- required virtual dataset name
     * @return dataset
     */
    private Node findOrCreateVirtualDataset(Node root, String name) {
        Node dataset = null;
        dataset = findVirtualDataset(root, name);
        if (dataset == null) {
            dataset = datasetService.createNode(NodeTypes.DATASET, name);
            root.createRelationshipTo(dataset, GeoNeoRelationshipTypes.VIRTUAL_DATASET);
        }
        return dataset;
    }

    /**
     * find required virtual dataset from current dataset
     * 
     * @param root -current dataset;
     * @param name - required name
     * @return dataset node if founded,else null
     */
    private Node findVirtualDataset(Node root, String name) {
        for (Relationship rel : root.getRelationships(GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING)) {
            if (rel.getEndNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(name))
                return rel.getEndNode();
        }
        return null;
    }

    /**
     * extract calls from collector and save them to db;
     * 
     * @param collector
     */
    public void saveCallColection(CallCollector collector) {
        Transaction tx = graphDb.beginTx();
        try {
            Map<String, AbstractCall> collection = collector.getCallsCache();
            for (String key : collection.keySet()) {
                saveCall(collection.get(key));
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error while saving call ", e);
        } finally {
            tx.finish();
        }
    }

    /**
     * save calls
     * 
     * @param abstractCall
     */
    private void saveCall(AbstractCall abstractCall) {
        switch (abstractCall.getCallType()) {
        case INDIVIDUAL:
        case GROUP:
        case EMERGENCY:
        case HELP:
            saveRealCall(abstractCall);
            break;
        case SDS:
        case TSM:
        case ALARM:
            saveMessageCall(abstractCall);
            break;
        case ITSI_HO:
            saveHandoverCall((HandoverCall)abstractCall);
            break;
        case ITSI_CC:
            saveReselectionCall((CellReselCall)abstractCall);
            break;
        case ITSI_ATTACH:
            saveItsiAttach((ItsiAttachCall)abstractCall);
            break;
        }
    }

    /**
     * save itsi attach call;
     * 
     * @param abstractCall
     */
    private void saveItsiAttach(ItsiAttachCall abstractCall) {
        propertyMap = new HashMap<String, Object>();
        ItsiAttachCall itsiAtt = (ItsiAttachCall)abstractCall;
        propertyMap.put(CallProperties.CALL_TYPE.getId(), itsiAtt.getCallType().toString());
        propertyMap.put(CallProperties.CALL_RESULT.getId(), itsiAtt.getCallResult().name());
        propertyMap.put(CallProperties.NAME.getId(), itsiAtt.getId());
        propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
        propertyMap.put(CallProperties.IS_INCONCLUSIVE.getId(), itsiAtt.isInconclusive());
        propertyMap.put(CallProperties.CALL_TYPE.getId(), itsiAtt.getCallType().toString());

        propertyMap.put(CallProperties.CALL_DURATION.getId(), itsiAtt.getCallDuration());
        Node lastNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
    }

    /**
     * save cell reselection call into DB
     * 
     * @param abstractCall
     */
    private void saveReselectionCall(CellReselCall abstractCall) {
        propertyMap = new HashMap<String, Object>();
        CellReselCall reselection = (CellReselCall)abstractCall;
        propertyMap.put(CallProperties.CALL_TYPE.getId(), reselection.getCallType().toString());
        propertyMap.put(CallProperties.CALL_RESULT.getId(), reselection.getCallResult().name());
        propertyMap.put(CallProperties.NAME.getId(), reselection.getId());
        propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
        propertyMap.put(CallProperties.IS_INCONCLUSIVE.getId(), reselection.isInconclusive());
        propertyMap.put(CallProperties.CALL_TYPE.getId(), reselection.getCallType().toString());

        propertyMap.put(CallProperties.CC_RESELECTION_TIME.getId(), reselection.getCellReselection());
        Node lastNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
    }

    /**
     * save handover call into DB
     * 
     * @param abstractCall
     */
    private void saveHandoverCall(HandoverCall abstractCall) {
        propertyMap = new HashMap<String, Object>();
        HandoverCall handover = (HandoverCall)abstractCall;
        propertyMap.put(CallProperties.CALL_TYPE.getId(), handover.getCallType().toString());
        propertyMap.put(CallProperties.CALL_RESULT.getId(), handover.getCallResult().name());
        propertyMap.put(CallProperties.NAME.getId(), handover.getId());
        propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
        propertyMap.put(CallProperties.IS_INCONCLUSIVE.getId(), handover.isInconclusive());
        propertyMap.put(CallProperties.CALL_TYPE.getId(), handover.getCallType().toString());
        propertyMap.put(CallProperties.NAME.getId(), handover.getId());

        propertyMap.put(CallProperties.CC_HANDOVER_TIME.getId(), handover.getHandoverTime());
        Node lastNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
    }

    /**
     * save sds,tsm,alarm call into DB
     * 
     * @param abstractCall
     */
    private void saveMessageCall(AbstractCall abstractCall) {
        propertyMap = new HashMap<String, Object>();
        MessageCall messageCall = (MessageCall)abstractCall;
        propertyMap.put(CallProperties.CALL_TYPE.getId(), messageCall.getCallType().toString());
        propertyMap.put(CallProperties.CALL_RESULT.getId(), messageCall.getCallResult().name());
        propertyMap.put(CallProperties.NAME.getId(), messageCall.getId());
        propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
        propertyMap.put(CallProperties.IS_INCONCLUSIVE.getId(), messageCall.isInconclusive());
        propertyMap.put(CallProperties.CALL_TYPE.getId(), messageCall.getCallType().toString());
        propertyMap.put(CallProperties.NAME.getId(), messageCall.getId());

        propertyMap.put(CallProperties.MESS_ACKNOWLEDGE_TIME.getId(), messageCall.getMessageAcnowledgeTime());
        propertyMap.put(CallProperties.MESS_RECEIVE_TIME.getId(), messageCall.getMessageRecievedTime());

        Node lastNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
    }

    /**
     * save group,individual, emergency ,help call into DB
     * 
     * @param abstractCall
     */
    private void saveRealCall(AbstractCall abstractCall) {
        RealCall realCall;
        propertyMap = new HashMap<String, Object>();
        realCall = (RealCall)abstractCall;
        propertyMap.put(CallProperties.CALL_TYPE.getId(), realCall.getCallType().toString());
        propertyMap.put(CallProperties.DELAY.getId(), realCall.getDelay());
        propertyMap.put(CallProperties.AVERAGE_DELAY.getId(), realCall.getAverageDelay());
        propertyMap.put(CallProperties.AVERAGE_LQ.getId(), realCall.getAverageLQ());
        propertyMap.put(CallProperties.CALL_DURATION.getId(), realCall.getCallDuration());
        propertyMap.put(CallProperties.CALL_RESULT.getId(), realCall.getCallResult().name());
        propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
        propertyMap.put(CallProperties.IS_INCONCLUSIVE.getId(), realCall.isInconclusive());
        if (realCall.isInconclusive()) {
            propertyMap.put(CallProperties.INCONCLUSIVE_CODE.getId(), realCall.getErrCode());
        }
        propertyMap.put(CallProperties.PHONE_NUMBER.getId(), realCall.getPhoneNumber());
        propertyMap.put(CallProperties.SETUP_DURATION.getId(), realCall.getSetupDuration());
        propertyMap.put(CallProperties.NAME.getId(), realCall.getId());

        // propertyMap.put(INeoConstants.PROPERTY_IS_VALID,individual.isValid());
        propertyMap.put(CallProperties.LQ.getId(), realCall.getLq());
        propertyMap.put(CallProperties.MAXIMUM_DELAY.getId(), realCall.getMaxDelay());
        propertyMap.put(CallProperties.MINIMUM_DELAY.getId(), realCall.getMinDelay());
        propertyMap.put(CallProperties.MINIMUM_LQ.getId(), realCall.getMinLq());
        propertyMap.put(CallProperties.MAXIMUM_LQ.getId(), realCall.getMaxLq());
        propertyMap.put(CallProperties.TERMINATION_DURATION.getId(), realCall.getTerminationDuration());
        propertyMap.put(CallProperties.TIMESTAMP.getId(), realCall.getTimestamp());
        Node lastNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
        try {
            Node pesqNode = buildPesqModelStructure(lastNode, realCall.getTocPesqList(), realCall.getTtcPesqList(),
                    realCall.getCallType());
            // Node ntpq = buildNtpqStructure(lastNode, collector.getNtpq(),
            // realCall.getCallType());
            // Node tocTTc = attachTocTtc(lastNode, realCall.getTocTtcList(), ntpq,
            // realCall.getCallType());
        } catch (Exception e) {
            LOGGER.error("Error while saving call", e);
        }
    }

    /**
     * added Node to required model in Child-->Next structure
     * 
     * @param model required model
     * @param fileName filename in model
     * @param propertyMap node properties
     * @return added node
     */
    private Node addMeasurmentToModel(DriveModel model, String fileName, Map<String, Object> propertyMap) {
        try {
            return model.addMeasurement(fileName, propertyMap);
        } catch (DatabaseException e) {
            LOGGER.error("Error while added node to " + model.getName(), e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * build CHILD-->NEXT structure of pesqNode in pesq model and link created node to callNode ;
     * 
     * @param callNode call node for attach pesq
     * @param tocPesqList toc pesq list
     * @param ttcPesqList ttc pesq list
     */
    private Node buildPesqModelStructure(Node callNode, List<PESQResultElement> tocPesqList, List<PESQResultElement> ttcPesqList,
            CallType type) {
        Node lastPesqNode = null;
        for (PESQResultElement pesqToc : tocPesqList) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(LoaderConstants.DELAY, pesqToc.getDelay());
            propertyMap.put(LoaderConstants.PESQ, pesqToc.getPesq());
            propertyMap.put(INeoConstants.PROPERTY_TIMESTAMP_NAME, pesqToc.getSendSampleStart().getTimeInMillis());
            propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, type.toString());
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, PESQ_NAME);
            propertyMap.put(PROBE_TYPE, ChildTypes.TOC.getId());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
            propertyMap.put(CallProperties.CALL_TYPE.getId(), ChildTypes.TOC.toString());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
            lastPesqNode = addMeasurmentToModel(pesqModel, fileName, propertyMap);
            callNode.createRelationshipTo(lastPesqNode, ProbeCallRelationshipType.CALL_M);
           
        }
        for (PESQResultElement pesqToc : ttcPesqList) {
            propertyMap.put(LoaderConstants.DELAY, pesqToc.getDelay());
            propertyMap.put(LoaderConstants.PESQ, pesqToc.getPesq());
            propertyMap.put(INeoConstants.PROPERTY_TIMESTAMP_NAME, pesqToc.getSendSampleStart().getTimeInMillis());
            propertyMap.put(CallProperties.DATASET_ID.getId(), callDatasetNode.getId());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, type.toString());
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, PESQ_NAME);
            propertyMap.put(PROBE_TYPE, ChildTypes.TTC.getId());
            propertyMap.put(CallProperties.CALL_TYPE.getId(), ChildTypes.TTC.toString());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
            lastPesqNode = addMeasurmentToModel(pesqModel, fileName, propertyMap);
            callNode.createRelationshipTo(lastPesqNode, ProbeCallRelationshipType.CALL_M);
            
        }
        return lastPesqNode;
    }

    /**
     * build structure of ntpqs and return last ntpq node id;
     * 
     * @param currentNode datasetNode
     * @param ntpq
     */
    private Node buildNtpqStructure(Node datasetNode, List<Ntpq> ntpq, CallType type) {
        Map<String, List<Ntpq>> ntpqMap = new HashMap<String, List<Ntpq>>();
        for (Ntpq ntpqMember : ntpq) {
            if (ntpqMap.containsKey(ntpqMember.getProbeId())) {
                ntpqMap.get(ntpqMember.getProbeId()).add(ntpqMember);
            } else {
                ntpqMap.put(ntpqMember.getProbeId(), new LinkedList<Ntpq>());
                ntpqMap.get(ntpqMember.getProbeId()).add(ntpqMember);
            }

        }
        Node lastNode = buildMainAndInnerNtpq(tocTTCModel, datasetNode, ntpqMap, type);
        return lastNode;
    }

    /**
     * build structure of NTPQ nodes and link it to callNode;
     * 
     * @param callNode data
     * @param ntpq
     * @return
     */
    private Node buildMainAndInnerNtpq(DriveModel model, Node callNode, Map<String, List<Ntpq>> ntpqMap, CallType type) {
        Node maggrNode = null;
        for (String key : ntpqMap.keySet()) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, key);
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M_AGGR.getId());
            addMeasurmentToModel(model, fileName, propertyMap);
            for (Ntpq ntpq : ntpqMap.get(key)) {
                propertyMap.put(LoaderConstants.JITTER, ntpq.getJitter());
                propertyMap.put(LoaderConstants.NTPQ_TIME, ntpq.getNtpqTime().getTimeInMillis());
                propertyMap.put(LoaderConstants.OFFSET, ntpq.getOffset());
                propertyMap.put(CallProperties.CALL_TYPE.getId(), type.toString());
                propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
                propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, NTPQ_NAME);
                propertyMap.put(LoaderConstants.PROBE_ID, ntpq.getProbeId());
                addMeasurmentToModel(model, fileName, propertyMap);
                callNode.createRelationshipTo(maggrNode, ProbeCallRelationshipType.CALL_M);
            }
        }
        return maggrNode;
    }

    /**
     * attach toc and ttc node to call node
     * 
     * @param callNode
     * @param tocTtcList
     * @param callType
     */
    private Node attachTocTtc(Node callNode, List<AbstractTOCTTC> tocTtcList, Node lastNtpq, CallType callType) {
        if (tocTtcList.isEmpty()) {
            return null;
        }
        Node tocTtc = null;
        for (AbstractTOCTTC tocttc : tocTtcList) {
            if (tocttc instanceof TOCElement) {
                tocTtc = findOrCreateTocNodes(currentNode, (TOCElement)tocttc, callType);
            } else {
                tocTtc = findOrCreateTtcNodes(currentNode, (TTCElement)tocttc, callType);
            }
        }
        return tocTtc;

    }

    /**
     * create and attach ttc nodes
     * 
     * @param currentCallNode
     * @param ttcList
     */
    private Node findOrCreateTtcNodes(Node currentCallNode, TTCElement ttc, CallType callType) {
        propertyMap = new HashMap<String, Object>();
        propertyMap.put(CallProperties.CALL_TYPE.getId(), callType.toString());
        propertyMap.put(LoaderConstants.CALLING_NUMBER, ttc.getCallingNumber());
        propertyMap.put(LoaderConstants.CAUSE_FOR_TERMINATION, ttc.getCauseForTermination());
        propertyMap.put(LoaderConstants.INDICATION_TIME, ttc.getIndicationTime().getTimeInMillis());
        propertyMap.put(LoaderConstants.ANSWER_TIME, ttc.getAnswerTime().getTimeInMillis());
        propertyMap.put(LoaderConstants.HOOK, ttc.getHook());
        propertyMap.put(LoaderConstants.SIMPLEX, ttc.getSimplex());
        propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, TTC_NAME);
        propertyMap.put(LoaderConstants.RELEASE_TIME, ttc.getReleaseTime().getTimeInMillis());
        propertyMap.put(INeoConstants.PROPERTY_TIMESTAMP_NAME, ttc.getIndicationTime().getTimeInMillis());
        calculateDelayAndLq(ttc.getPesqResult());
        propertyMap.put(INeoConstants.PROPERTY_TTC_AUDIO_DELAY, delay);
        propertyMap.put(INeoConstants.PROPERTY_TTC_LISTENING_QUALITY, lq);
        propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
        Node ttcNode = addMeasurmentToModel(tocTTCModel, fileName, propertyMap);
        currentCallNode.createRelationshipTo(ttcNode, ProbeCallRelationshipType.CALL_M);
        return ttcNode;
    }

    /**
     * create and attach toc nodes from currentNode callNode
     * 
     * @param currentCallNode
     * @param tocList
     */
    private Node findOrCreateTocNodes(Node currentCallNode, TOCElement toc, CallType callType) {

        propertyMap = new HashMap<String, Object>();
        propertyMap.put(CallProperties.CALL_TYPE.getId(), callType.toString());
        propertyMap.put(LoaderConstants.CALLED_NUMBER, toc.getCalledNumber());
        propertyMap.put(LoaderConstants.CAUSE_FOR_TERMINATION, toc.getCauseForTermination());
        propertyMap.put(LoaderConstants.CONFIG_TIME, toc.getConfigTime().getTimeInMillis());
        propertyMap.put(LoaderConstants.CONNECT_TIME, toc.getConfigTime().getTimeInMillis());
        if (toc.getDisconnectTime() != null) {
            propertyMap.put(LoaderConstants.DISCONNECT_TIME, toc.getDisconnectTime().getTimeInMillis());
        }
        propertyMap.put(LoaderConstants.HOOK, toc.getHook());
        propertyMap.put(LoaderConstants.SIMPLEX, toc.getSimplex());
        propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, TOC_NAME);
        propertyMap.put(LoaderConstants.PRIORITY, toc.getPriority());
        propertyMap.put(LoaderConstants.RELEASE_TIME, toc.getReleaseTime().getTimeInMillis());
        propertyMap.put(LoaderConstants.SEND_TIME, toc.getSetupTime().getTimeInMillis());
        propertyMap.put(INeoConstants.PROPERTY_TIMESTAMP_NAME, toc.getConfigTime().getTimeInMillis());
        calculateDelayAndLq(toc.getPesqResult());
        propertyMap.put(INeoConstants.PROPERTY_TOC_AUDIO_DELAY, delay);
        propertyMap.put(INeoConstants.PROPERTY_TOC_LISTENING_QUALITY, lq);
        propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
        Node tocNode = addMeasurmentToModel(tocTTCModel, fileName, propertyMap);
        currentCallNode.createRelationshipTo(tocNode, ProbeCallRelationshipType.CALL_M);
        return tocNode;
    }

    /**
     * calculate pesq results for toc && ttc;
     * 
     * @param pesqResult
     */
    private void calculateDelayAndLq(List<PESQResultElement> pesqResult) {
        for (PESQResultElement pesq : pesqResult) {
            delay += pesq.getDelay().floatValue();
            lq += pesq.getPesq().floatValue();
        }
        if (pesqResult.size() != 0) {
            delay = delay / pesqResult.size();
            lq = lq / pesqResult.size();
        }
    }

    /**
     * create node from properties map,and attach it to child-next structure
     * 
     * @param parentNode
     * @param propertyMap
     */
    private void createCallNode(Node parentNode, Map<String, Object> propertyMap) {
        if (propertyMap.isEmpty()) {
            return;
        }
        Node newNode = graphDb.createNode();
        for (String key : propertyMap.keySet()) {
            newNode.setProperty(key, propertyMap.get(key));
        }
        newNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, "call");
        if (parentNode.equals(callDatasetNode)) {
            parentNode.createRelationshipTo(newNode, NetworkRelationshipTypes.CHILD);
        } else {
            parentNode.createRelationshipTo(newNode, NetworkRelationshipTypes.NEXT);
        }
        currentNode = newNode;
    }

    @Override
    public void init(IConfiguration configuration, IData dataElement) {

        this.configuration = configuration;
        Map<Object, String> datasetNames = configuration.getDatasetNames();
        graphDb = NeoServiceProvider.getProvider().getService();

        getDatasetService();
        projectNode = datasetService.findOrCreateAweProject(datasetNames.get("Project"));
        try {
            tocTTCModel = new DriveModel(projectNode, null, datasetNames.get("Dataset"), DriveTypes.AMS);
            mainDatasetNode = tocTTCModel.getRootNode();
            callsModel = tocTTCModel.addVirtualDataset(datasetNames.get("Calls"), DriveTypes.AMS_CALLS);
            callDatasetNode = callsModel.getRootNode();
            currentNode = callDatasetNode;

            pesqModel = tocTTCModel.addVirtualDataset(datasetNames.get("Pesq"), DriveTypes.AMS_PESQ);
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    @Override
    public void saveElement(IData dataElement) {
        Transaction tx = graphDb.beginTx();
        try {

            if (dataElement instanceof CallCollector) {
                this.collector = ((CallCollector)dataElement);
                tocTTCModel.addFile(collector.getFile());
                pesqModel.addFile(collector.getFile());
                callsModel.addFile(collector.getFile());
                fileName = collector.getFile().getName();
                for (String key : collector.getCallsCache().keySet()) {
                    saveCall(collector.getCallsCache().get(key));
                }
            }

            tx.success();
        } catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    @Override
    public void finishUp() {
    }
}
