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
import org.amanzi.awe.cassidian.structure.Attachment;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
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
    /**
     * toc ttc dataset node
     */
    private Node mainDatasetNode;
    /**
     * calls dataset node
     */
    private Node callDatasetNode;
    /**
     * toc ttc drive model
     */
    private DriveModel tocTTCModel;
    /**
     * calls drive model
     */
    private DriveModel callsModel;
    /**
     * pesq drive model
     */
    private DriveModel pesqModel;
    /**
     * current processing file name
     */
    private String fileName;
    /**
     * current procesing collector
     */
    private CallCollector collector;
    /**
     * project node
     */
    private Node projectNode;
    /**
     * dataset service instance
     */
    private static DatasetService datasetService;
    /**
     * Node property map collector
     */
    private Map<String, Object> propertyMap;
    /**
     * graph database instance
     */
    private GraphDatabaseService graphDb;
    /**
     * average delay
     */
    private float delay;
    /**
     * average listening quality (pesq)
     */
    private float lq;
    /**
     * last created measured node in toc Ttc Model
     */
    private Node lastMeasuredTocTtcModelNode;
    /**
     * last created call node
     */
    private Node lastCallNode;
    /**
     * configuration instance
     */
    private IConfiguration configuration;
    /**
     * processing call type;
     */
    private CallType type;
    private final static String PESQ_NAME = "pesq";
    private final static String NTPQ_NAME = "ntpq";
    private final static String TOC_NAME = "Toc";
    private final static String TTC_NAME = "Ttc";
    private final static String PROBE_TYPE = "probe_type";
    /**
     * ntpq cache
     */
    Map<String, Node> ntpqNodeMap;

    /**
     * 
     */
    public AMSXMLSaver() {
        super();
        getDatasetService();
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
     * save calls
     * 
     * @param abstractCall
     */
    private void saveCall(AbstractCall abstractCall) {
        type = abstractCall.getCallType();
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
        lastCallNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
        attachCalltoNTP(itsiAtt.getId().substring(0, itsiAtt.getId().lastIndexOf('_')), itsiAtt.getCallType(), lastCallNode);
        lastMeasuredTocTtcModelNode = buildItsiAttachInTocTtcModel(lastCallNode, itsiAtt.getItsiAttachList());
    }

    /**
     * save itsi attach node in toc ttc model
     * 
     * @param itsiAtt list of itsi Attach Element
     * @return lastAddedItsiAttach
     */
    private Node buildItsiAttachInTocTtcModel(Node lastCallNode, List<ItsiAttach> itsiAtt) {
        Node lastNode = null;
        for (ItsiAttach itsiAttach : itsiAtt) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(LoaderConstants.ITSI_ATT_ACCEPT, itsiAttach.getItsiAccept().getTimeInMillis());
            propertyMap.put(LoaderConstants.ITSI_ATT_REQ, itsiAttach.getItsiAttReq().getTimeInMillis());
            propertyMap.put(LoaderConstants.LOCATION_AREA_AFTER, itsiAttach.getLocationAreaAfter());
            propertyMap.put(LoaderConstants.LOCATION_AREA_BEFORE, itsiAttach.getLocationAreaBefore());
            propertyMap.put(LoaderConstants.PROBE_ID, itsiAttach.getProbeId());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
            propertyMap.put(CallProperties.CALL_TYPE.getId(), ChildTypes.ITSI_ATTACH.toString());
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, ChildTypes.ITSI_ATTACH.getId());
            lastNode = addMeasurmentToModel(tocTTCModel, fileName, propertyMap);
            lastCallNode.createRelationshipTo(lastNode, ProbeCallRelationshipType.CALL_M);

        }
        return lastNode;
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
        propertyMap.put(CallProperties.CALL_TYPE.getId(), type.toString());
        propertyMap.put(CallProperties.CC_RESELECTION_TIME.getId(), reselection.getCellReselection());
        lastCallNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
        attachCalltoNTP(reselection.getId().substring(0, reselection.getId().lastIndexOf('_')), reselection.getCallType(),
                lastCallNode);
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
        propertyMap.put(CallProperties.CALL_TYPE.getId(), type.toString());
        propertyMap.put(CallProperties.NAME.getId(), handover.getId());

        propertyMap.put(CallProperties.CC_HANDOVER_TIME.getId(), handover.getHandoverTime());
        lastCallNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
        attachCalltoNTP(handover.getId().substring(0, handover.getId().lastIndexOf('_')), handover.getCallType(), lastCallNode);
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
        propertyMap.put(CallProperties.CALL_TYPE.getId(), type.toString());
        propertyMap.put(CallProperties.NAME.getId(), messageCall.getId());

        propertyMap.put(CallProperties.MESS_ACKNOWLEDGE_TIME.getId(), messageCall.getMessageAcnowledgeTime());
        propertyMap.put(CallProperties.MESS_RECEIVE_TIME.getId(), messageCall.getMessageRecievedTime());

        lastCallNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
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

        propertyMap.put(CallProperties.CALL_TYPE.getId(), type.toString());
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
        lastCallNode = addMeasurmentToModel(callsModel, fileName, propertyMap);
        attachCalltoNTP(realCall.getId().substring(0, realCall.getId().lastIndexOf('_')), realCall.getCallType(), lastCallNode);
        try {
            buildPesqModelStructure(lastCallNode, realCall.getTocPesqList(), realCall.getTtcPesqList(), realCall.getCallType());
            // lastMeasuredTocTtcModelNode = buildNtpqStructure(lastCallNode, collector.getNtpq(),
            // realCall.getCallType());
            lastMeasuredTocTtcModelNode = attachTocTtc(lastCallNode, realCall.getTocTtcList(), lastMeasuredTocTtcModelNode, type);
        } catch (Exception e) {
            LOGGER.error("Error while saving real call ", e);
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
     * build structure of NTPQ nodes and link it to callNode;
     * 
     * @param callNode data
     * @param ntpq
     */
    private void buildNTPNode(DriveModel model, Map<String, List<Ntpq>> ntpqMap) {
        for (String key : ntpqMap.keySet()) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, "NTP " + key);
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M_AGGR.getId());
            lastMeasuredTocTtcModelNode = addMeasurmentToModel(model, fileName, propertyMap);
            ntpqNodeMap.put(key, lastMeasuredTocTtcModelNode);
            buildNTPQNode(lastMeasuredTocTtcModelNode, ntpqMap.get(key), type);
        }
    }

    /**
     * create ntpq node and link created node with call node and ntpNode
     * 
     * @param callNode call node
     * @param ntp ntp node
     * @param list list of ntpqs
     * @param type call type;
     */
    private void buildNTPQNode(Node ntp, List<Ntpq> list, CallType type) {
        Node ntpqNode = null;
        Node tempNode;
        for (Ntpq ntpq : list) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(LoaderConstants.JITTER, ntpq.getJitter());
            propertyMap.put(LoaderConstants.NTPQ_TIME, ntpq.getNtpqTime().getTimeInMillis());
            propertyMap.put(LoaderConstants.OFFSET, ntpq.getOffset());
            propertyMap.put(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
            // propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, NTPQ_NAME);
            propertyMap.put(LoaderConstants.PROBE_ID, ntpq.getProbeId());
            tempNode = datasetService.createNode(NodeTypes.M, NTPQ_NAME);
            for (String property : propertyMap.keySet()) {
                tempNode.setProperty(property, propertyMap.get(property));
            }
            if (!ntp.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                ntpqNode = tempNode;
                ntp.createRelationshipTo(ntpqNode, NetworkRelationshipTypes.CHILD);
            } else {
                ntpqNode.createRelationshipTo(tempNode, NetworkRelationshipTypes.NEXT);
                ntpqNode = tempNode;
            }

        }
    }

    /**
     * attach toc and ttc node to call node
     * 
     * @param callNode
     * @param tocTtcList
     * @param callType
     */
    private Node attachTocTtc(Node callNode, List<AbstractTOCTTC> tocTtcList, Node lastMeasuredNode, CallType callType) {
        if (tocTtcList.isEmpty()) {
            return null;
        }
        Node tocTtc = null;
        for (AbstractTOCTTC tocttc : tocTtcList) {
            if (tocttc instanceof TOCElement) {
                tocTtc = buildTocNode(callNode, (TOCElement)tocttc, callType);
            } else {
                tocTtc = buildTTCNodes(callNode, (TTCElement)tocttc, callType);
            }
        }
        return tocTtc;

    }

    /**
     * create ttc node in tocTTCmodel and link it with call in callModel
     * 
     * @param callNode
     * @param ttcList
     */
    private Node buildTTCNodes(Node callNode, TTCElement ttc, CallType callType) {
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
        callNode.createRelationshipTo(ttcNode, ProbeCallRelationshipType.CALL_M);
        return ttcNode;
    }

    /**
     * create toc node in tocTTCmodel and link it with call in callModel
     * 
     * @param callNode
     * @param tocList
     */
    private Node buildTocNode(Node callNode, TOCElement toc, CallType callType) {

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
        callNode.createRelationshipTo(tocNode, ProbeCallRelationshipType.CALL_M);
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
                buildNTPNode(tocTTCModel, collector.getNtpqCache());
                for (String key : collector.getCallsCache().keySet()) {
                    saveCall(collector.getCallsCache().get(key));
                }

                buildGroupAttach(collector.getGroupAttach());
            }
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    /**
     * save groupAttach in Toc Ttc model
     * 
     * @param groupAttach
     */
    private void buildGroupAttach(List<GroupAttach> groupAttach) {
        for (GroupAttach grAttach : groupAttach) {
            propertyMap = new HashMap<String, Object>();
            propertyMap.put(LoaderConstants.GROUP_ATTACH_TIME, grAttach.getGroupAttachTime().getTimeInMillis());
            propertyMap.put(INeoConstants.PROPERTY_TIMESTAMP_NAME, grAttach.getGroupAttachTime().getTimeInMillis());
            propertyMap.put(LoaderConstants.PROBE_ID, grAttach.getProbeId());
            propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, ChildTypes.GROUP_ATTACH.getId());
            lastMeasuredTocTtcModelNode = addMeasurmentToModel(tocTTCModel, fileName, propertyMap);
            if (!grAttach.getAttachment().isEmpty()) {
                Node lastAttachNode = lastMeasuredTocTtcModelNode;
                for (Attachment att : grAttach.getAttachment()) {
                    lastAttachNode = buildAttachment(lastMeasuredTocTtcModelNode, lastAttachNode, att);
                    lastMeasuredTocTtcModelNode.setProperty(INeoConstants.LAST_CHILD_ID, lastAttachNode.getId());
                }
            }
        }
    }

    /**
     * <b>Attach callNode to NTPnode</b>
     * <p>
     * search NTP Node in cache by probe id and link it to callNode if founded.
     * </p>
     * 
     * @param probeId
     * @param callNode
     */
    private void attachCalltoNTP(String probeId, CallType type, Node callNode) {
        if (ntpqNodeMap.containsKey(probeId)) {
            callNode.createRelationshipTo(ntpqNodeMap.get(probeId), ProbeCallRelationshipType.CALL_M);
            Node ntpqNode;
            if (ntpqNodeMap.get(probeId).hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                ntpqNode = ntpqNodeMap.get(probeId).getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
                        .getEndNode();
                while (ntpqNode.hasRelationship(NetworkRelationshipTypes.NEXT, Direction.OUTGOING)) {
                    ntpqNode.setProperty(CallProperties.CALL_TYPE.toString(), type.toString());
                    ntpqNode = ntpqNodeMap.get(probeId).getSingleRelationship(NetworkRelationshipTypes.NEXT, Direction.OUTGOING)
                            .getEndNode();
                }
            }
        }
    }

    /**
     * save attachment node;
     * 
     * @param groupNode
     * @param lastAttachNode
     * @param att
     * @return
     */
    private Node buildAttachment(Node groupNode, Node lastAttachNode, Attachment att) {
        Node attachmentNode = datasetService.createNode(NodeTypes.MM, ChildTypes.ATTACHMENT.getId());
        propertyMap = new HashMap<String, Object>();
        propertyMap.put(LoaderConstants.GSSI, att.getGssi());
        propertyMap.put(LoaderConstants.GROUP_TYPE, att.getGroupType());
        for (String key : propertyMap.keySet()) {
            attachmentNode.setProperty(key, propertyMap.get(key));
        }
        if (lastAttachNode != groupNode) {
            lastAttachNode.createRelationshipTo(attachmentNode, NetworkRelationshipTypes.NEXT);
        } else {
            lastAttachNode.createRelationshipTo(attachmentNode, NetworkRelationshipTypes.CHILD);
        }
        return attachmentNode;
    }
    
    @Override
    public void finishUp() {
    }
}
