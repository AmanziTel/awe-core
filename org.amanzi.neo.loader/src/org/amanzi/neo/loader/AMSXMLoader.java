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

package org.amanzi.neo.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.CallProperties.CallResult;
import org.amanzi.neo.core.enums.CallProperties.CallType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.sax_parsers.AbstractTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTagFactory;
import org.amanzi.neo.loader.sax_parsers.PropertyCollector;
import org.amanzi.neo.loader.sax_parsers.ReadContentHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * AMS XML Loader
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AMSXMLoader extends AbstractCallLoader {
    /** TOC-TTC call */
    private AMSCall tocttc;
    private AMSCall tocttcGroup;
    private AMSCall msgCall;

    /** The Constant subNodes. */
    protected final static Map<String, Class< ? extends AbstractEvent>> subNodes = new HashMap<String, Class< ? extends AbstractEvent>>();
    /** Initialize events map */
    static {
        subNodes.put("itsiAttach", ItsiAttach.class);
        subNodes.put("cellResel", CellResel.class);
        subNodes.put("handover", Handover.class);
        subNodes.put("groupAttach", GroupAttach.class);
        subNodes.put("toc", Toc.class);
        subNodes.put("ttc", Ttc.class);
        subNodes.put("tpc", Tpc.class);
        subNodes.put("sendMsg", SendMsg.class);
        subNodes.put("receiveMsg", ReceiveMsg.class);
    }
    /** The LOGGER. */
    public static Logger LOGGER = Logger.getLogger(AMSXMLoader.class);

    /** The formatter. */
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSSz");

    /** The directory name. */
    private final String directoryName;

    /** The network name. */
    private String networkName;

    /** The network gis. */
    private Node networkGis;

    /** The last dataset node. */
    private Node lastDatasetNode;

    /** The in. */
    private CountingFileInputStream in;

    /** The handler. */
    private final ContentHandler handler;

    /** The probe cache. */
    private final Map<String, Node> probeCache = new HashMap<String, Node>();
    private final Map<String, Node> probeCallCache = new HashMap<String, Node>();
    private final Map<String, String> phoneNumberCache = new HashMap<String, String>();
    /** active file node for event dataset */
    private Node datasetFileNode;

    // TODO change after implement feature 1131

    @Override
    protected Node getStoringNode(Integer key) {
        switch (key) {
        case REAL_DATASET_HEADER_INDEX:
            return datasetNode;
        case CALL_DATASET_HEADER_INDEX:
            return callDataset;
        case PROBE_NETWORK_HEADER_INDEX:
            return networkNode;
        default:
            return null;
        }
    }

    @Override
    protected String getPrymaryType(Integer key) {
        switch (key) {
        case REAL_DATASET_HEADER_INDEX:
            return NodeTypes.M.getId();
        case CALL_DATASET_HEADER_INDEX:
            return NodeTypes.CALL.getId();
        case PROBE_NETWORK_HEADER_INDEX:
            return NodeTypes.PROBE.getId();
        default:
            return null;
        }
    }
    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
        // do nothing
    }

    /**
     * Parse string to timestamp.
     * 
     * @param stringData the string data format
     * @return the time
     * @throws ParseException the parse exception
     */
    private static Long getTime(String stringData) throws ParseException {
        int i = stringData.lastIndexOf(':');
        StringBuilder time = new StringBuilder(stringData.substring(0, i)).append(stringData.substring(i + 1, stringData.length()));
        return formatter.parse(time.toString()).getTime();
    }

    /**
     * Instantiates a new aMS loader from xml.
     * 
     * @param directoryName the directory name
     * @param display the display
     * @param datasetName the dataset name
     * @param networkName the network name
     */
    public AMSXMLoader(String directoryName, Display display, String datasetName, String networkName) {
        driveType = DriveTypes.AMS;
        handler = new ReadContentHandler(new Factory());
        if (datasetName == null) {
            int startIndex = directoryName.lastIndexOf(File.separator);
            if (startIndex < 0) {
                startIndex = 0;
            } else {
                startIndex++;
            }
            datasetName = directoryName.substring(startIndex);
        }

        this.directoryName = directoryName;
        this.filename = directoryName;
        this.networkName = networkName;

        initialize("AMS", null, directoryName, display, datasetName);

        // timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    }

    /**
     * Adds the drive indexes.
     */
    private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.PROBE.getId(), NeoUtils.getLocationIndexProperty(networkName));
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
            addIndex(NodeTypes.CALL.getId(), NeoUtils.getTimeIndexProperty(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        monitor.beginTask("Loading AMS data", 2);
        monitor.subTask("Searching for files to load");
        List<File> allFiles = LoaderUtils.getAllFiles(directoryName, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || LoaderUtils.getFileExtension(pathname.getName()).equalsIgnoreCase(".xml");
            }
        });

        monitor = SubMonitor.convert(monitor, allFiles.size());
        monitor.beginTask("Loading AMS data", allFiles.size());
        lastDatasetNode = null;
        Transaction tx = neo.beginTx();
        try {
            initializeNetwork(networkName);
            initializeDatasets(dataset);
            addDriveIndexes();
            initializeIndexes();
            long count = 0;
            long time = System.currentTimeMillis();
            for (File logFile : allFiles) {
                monitor.subTask("Loading file " + logFile.getAbsolutePath());
                try {
                    handleFile(logFile);
                } catch (Exception e) {
                    // TODO Handle SAXException
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
                if (count++ > 10) {
                    commit(tx);
                }
                monitor.worked(1);
            }
            time = System.currentTimeMillis() - time;
            LOGGER.info(new StringBuilder("total handle time ").append(time).toString());
            saveProperties();
            finishUpIndexes();
            finishUp();

            cleanupGisNode();
            finishUpGis();
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Handle file.
     * 
     * @param singleFile the file
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void handleFile(File singleFile) throws SAXException, IOException {
        tocttc = null;
        tocttcGroup = null;
        lastDatasetNode = null;
        datasetFileNode = NeoUtils.findOrCreateFileNode(neo, datasetNode, singleFile.getName(), singleFile.getName()).getRight();
        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(handler);
        in = new CountingFileInputStream(singleFile);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
        if (tocttcGroup != null) {
            saveCall(tocttcGroup);
            tocttcGroup = null;
        }
        if (tocttc != null) {
            saveCall(tocttc);
            tocttc = null;
        }
        if (msgCall != null) {
            saveCall(msgCall);
            msgCall = null;
        }
    }

    /**
     * Initializes Network for probes.
     * 
     * @param networkName name of network
     */
    private void initializeNetwork(String networkName) {
        String oldBasename = basename;
        if ((networkName == null) || (networkName.length() == 0)) {
            networkName = basename + " Probes";
        } else {
            networkName = networkName.trim();
        }

        basename = networkName;
        networkGis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.PROBE);
        networkNode = findOrCreateNetworkNode(networkGis);
        this.networkName = basename;
        basename = oldBasename;
    }

    /**
     * Initializes a Call dataset.
     * 
     * @param datasetName name of Real dataset
     */
    private void initializeDatasets(String datasetName) {
        Transaction tx = neo.beginTx();
        try {
            datasetNode = findOrCreateDatasetNode(neo.getReferenceNode(), dataset);
            findOrCreateGISNode(datasetNode, GisTypes.DRIVE.getHeader());

            callDataset = getVirtualDataset(DriveTypes.AMS_CALLS);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            NeoCorePlugin.error(null, e);
            throw new RuntimeException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * <p>
     * Abstract handler of one event
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public abstract class AbstractEvent extends PropertyCollector {

        /** The node. */
        protected Node node;

        /** The defined values. */
        protected final Map<String, Class< ? extends Object>> definedValues;
        protected final Map<String, AMSCommandParameters> parameterMap;

        /** The header. */
        protected LinkedHashMap<String, Header> header;

        /**
         * Instantiates a new abstract event.
         * 
         * @param tagName the tag name
         * @param parent the parent
         * @param computeSubChild the compute sub child
         */
        public AbstractEvent(String tagName, IXmlTag parent, Boolean computeSubChild) {
            super(tagName, parent, computeSubChild);
            definedValues = new HashMap<String, Class< ? extends Object>>();
            parameterMap = new HashMap<String, AMSCommandParameters>();
            header = getHeaderMap(REAL_DATASET_HEADER_INDEX).headers;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the i xml tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {

            if (openTag == null) {
                Transaction tx = neo.beginTx();
                try {
                    createEventChild();
                    handleCollector();
                    index(node);

                    tx.success();
                } catch (Exception e) {
                    NeoLoaderPlugin.exception(e);
                    LOGGER.error("event parsed with exception:", e);
                } finally {
                    tx.finish();
                }
                return parent;
            }
            propertyMap.put(localName, chars.toString());
            openTag = null;
            return this;
        }

        /**
         * Handle property store (stored property in neo Node).
         * 
         * @throws ParseException
         */
        protected void handleCollector() throws ParseException {
            Long timestamp = null;
            Map<String, String> map = getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Pair<Object, Class< ? extends Object>> pair = getParcedValue(entry.getKey(), entry.getValue(), definedValues);
                Object parsedValue = pair.getLeft();
                Class< ? extends Object> klass = pair.getRight();
                if (klass == Timestamp.class) {
                    timestamp = timestamp == null ? (Long)parsedValue : Math.min(timestamp, (Long)parsedValue);
                }
                AMSCommandParameters amsCommandParameters = parameterMap.get(entry.getKey());
                if (amsCommandParameters != null) {
                    handleAMSCommand(amsCommandParameters, entry.getKey(), parsedValue);
                } else {
                    setIndexProperty(header, node, entry.getKey(), parsedValue);
                }
            }
            PropertyCollector isInclus = getSubCollectorByName("isInconclusive");
            if (isInclus != null) {
                map = isInclus.getPropertyMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    Object parsedValue;
                    if (entry.getKey().equals("errCode")) {
                        parsedValue = Integer.parseInt(entry.getValue());
                    } else {
                        parsedValue = entry.getValue();
                    }
                    setIndexProperty(header, node, entry.getKey(), parsedValue);
                }
            }
            if (timestamp != null) {
                node.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            }
        }

        /**
         * Handle ams command.
         * 
         * @param amsCommandParameters the ams command parameters
         * @param key the key
         * @param parsedValue the parsed value
         */
        protected void handleAMSCommand(AMSCommandParameters amsCommandParameters, String key, Object parsedValue) {
            setIndexProperty(header, node, amsCommandParameters.getName(), amsCommandParameters.convert(parsedValue));
        }

        protected Pair<Object, Class< ? extends Object>> getParcedValue(String key, String value, Map<String, Class< ? extends Object>> castMap) throws ParseException {

            Class< ? extends Object> klass = castMap.get(key);
            if (klass == null) {
                klass = String.class;
            }
            Object parsedValue = null;
            if (klass == String.class) {
                parsedValue = value;
            } else if (klass == Double.class) {
                parsedValue = Double.parseDouble(value);
            } else if (klass == Integer.class) {
                parsedValue = Integer.parseInt(value);
            } else if (klass == Timestamp.class) {
                parsedValue = getTime(value);
            }
            return new Pair<Object, Class< ? extends Object>>(parsedValue, klass);
        }

        /**
         * create new event node.
         */
        protected void createEventChild() {
            node = neo.createNode();
            NodeTypes.M.setNodeType(node, neo);
            NeoUtils.addChild(datasetFileNode, node, lastDatasetNode, neo);
            lastDatasetNode = node;
            setNewIndexProperty(header, node, INeoConstants.PROPERTY_NAME_NAME, getClass().getSimpleName());
        }

    }

    /**
     * <p>
     * Handler ItsiAttach tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class ItsiAttach extends AbstractEvent {

        /**
         * Instantiates a new itsi attach.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public ItsiAttach(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("itsiAtt_Req", Timestamp.class);
            definedValues.put("itsiAtt_Accept", Timestamp.class);
            definedValues.put("locationAreaBefore", Integer.class);
            definedValues.put("locationAreaAfter", Integer.class);
            definedValues.put("errorCode", Integer.class);
        }
        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            handleCall();
        }
        /**
         *
         */
        private void handleCall() {
            Call call=new Call();
            call.addRelatedNode(node);
            call.setCallType(CallType.ITSI_ATTACH);
            Long beginTime = (Long)node.getProperty("itsiAtt_Req", null);
            if (beginTime!=null){
                call.setCallSetupBeginTime(beginTime); 
                call.setCallSetupEndTime(beginTime); 
            }
            Long endTime = (Long)node.getProperty("itsiAtt_Accept", null);
            if (endTime!=null){
                call.setCallTerminationBegin(endTime); 
                call.setCallTerminationEnd(endTime); 
            }
            if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
                call.setCallResult(CallResult.FAILURE);
            } else {
                call.setCallResult(CallResult.SUCCESS);
            }
            Node callerProbe = probeCallCache.get(getPropertyMap().get("probeID"));
            call.setCallerProbe(callerProbe);
            call.addRelatedNode(node);
            saveCall(call);
        }
    }

    /**
     * <p>
     * Handler CellResel tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class CellResel extends AbstractEvent {

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public CellResel(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("cellReselReq", Timestamp.class);
            definedValues.put("cellReselAccept", Timestamp.class);
            definedValues.put("locationAreaBefore", Integer.class);
            definedValues.put("locationAreaAfter", Integer.class);
            definedValues.put("errorCode", Integer.class);
        }

    }

    /**
     * <p>
     * Handler handover tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class Handover extends AbstractEvent {

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public Handover(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("ho_Req", Timestamp.class);
            definedValues.put("ho_Accept", Timestamp.class);
            definedValues.put("locationAreaBefore", Integer.class);
            definedValues.put("locationAreaAfter", Integer.class);
            definedValues.put("errorCode", Integer.class);
        }
        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            handleCall();
        }
        /**
        *
        */
       private void handleCall() {
           Call call=new Call();
           call.addRelatedNode(node);
           call.setCallType(CallType.ITSI_CC);
           Long beginTime = (Long)node.getProperty("ho_Req", null);
           if (beginTime!=null){
               call.setCallSetupBeginTime(beginTime); 
               call.setCallSetupEndTime(beginTime); 
           }
           Long endTime = (Long)node.getProperty("ho_Accept", null);
           if (endTime!=null){
               call.setCallTerminationBegin(endTime); 
               call.setCallTerminationEnd(endTime); 
           }
           if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
               call.setCallResult(CallResult.FAILURE);
           } else {
               call.setCallResult(CallResult.SUCCESS);
           }
           Node callerProbe = probeCallCache.get(getPropertyMap().get("probeID"));
           call.setCallerProbe(callerProbe);
           call.addRelatedNode(node);
           saveCall(call);
       }

    }

    /**
     * <p>
     * Handler Ttc tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class Ttc extends AbstractEvent {
        protected final Map<String, Class< ? extends Object>> pesqCastMap;
        Node lastMM = null;
        private Integer hook = null;
        private Integer simplex = null;

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public Ttc(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("hook", Integer.class);
            definedValues.put("simplex", Integer.class);
            definedValues.put("indicationTime", Timestamp.class);
            definedValues.put("answerTime", Timestamp.class);
            definedValues.put("connectTime", Timestamp.class);
            definedValues.put("disconnectTime", Timestamp.class);
            definedValues.put("releaseTime", Timestamp.class);
            definedValues.put("causeForTermination", Integer.class);
            definedValues.put("errorCode", Integer.class);
            pesqCastMap = new HashMap<String, Class< ? extends Object>>();
            pesqCastMap.put("sendSampleStart", Timestamp.class);
            pesqCastMap.put("pesq", Double.class);
            pesqCastMap.put("delay", Integer.class);
            parameterMap.put("hook", AMSCommandParameters.HOOK);
            parameterMap.put("simplex", AMSCommandParameters.SIMPLEX);

        }

        @Override
        protected void handleAMSCommand(AMSCommandParameters amsCommandParameters, String key, Object parsedValue) {
            super.handleAMSCommand(amsCommandParameters, key, parsedValue);
            // Field field = this.getClass().getField(key);
            // field.set(this, parsedValue);
            if (key.equals("hook")) {
                hook = (Integer)parsedValue;
            } else if (key.equals("simplex")) {
                simplex = (Integer)parsedValue;
            }
        }

        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            List<PropertyCollector> collectorList = getSubCollectors();
            handleCall();
            for (PropertyCollector collector : collectorList) {
                if (collector.getName().equals("pesqResult")) {
                    createAttachmentNode(collector);
                }
            }
            if (tocttc != null && hook != null && simplex != null && hook == 0 && simplex == 0) {
                saveCall(tocttc);
                tocttc = null;
            }
        }

        protected void handleCall() {
            if (hook == null || simplex == null) {
                return;
            }
            assert hook.equals(simplex) : hook + "\t" + simplex;
            if (tocttc != null) {
                if (hook == 0 && simplex == 0) {
                    tocttc.setCallSetupEndTime((Long)node.getProperty("connectTime", 0l));
                    tocttc.setCallTerminationEnd((Long)node.getProperty("releaseTime", 0l));
                    if (getPropertyMap().get("errorCode") != null) {
                        tocttc.setCallResult(CallResult.FAILURE);
                    }
                    tocttc.addRelatedNode(node);
                    tocttc.addCalleeProbe(probeCallCache.get(phoneNumberCache.get(tocttc.getCalledPhoneNumber())));

                }
            } else if (tocttcGroup != null) {
                if (hook == 1 && simplex == 1) {
                    tocttcGroup.setCallSetupEndTime((Long)node.getProperty("connectTime", 0l));
                    tocttcGroup.setCallTerminationEnd((Long)node.getProperty("releaseTime", 0l));
                    if (getPropertyMap().get("errorCode") != null) {
                        tocttcGroup.setCallResult(CallResult.FAILURE);
                    }
                    tocttcGroup.addCalleeProbe(probeCallCache.get(getPropertyMap().get("probeID")));
                    tocttcGroup.addRelatedNode(node);

                }
            }

        }

        /**
         * Creates the attachment node.
         * 
         * @param collector the collector
         * @throws ParseException
         */
        private void createAttachmentNode(PropertyCollector collector) throws ParseException {
            Node mm = neo.createNode();
            NeoUtils.addChild(node, mm, lastMM, neo);
            NodeTypes.MM.setNodeType(mm, neo);
            lastMM = mm;
            Map<String, String> map = collector.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object parseValue = getParcedValue(entry.getKey(), entry.getValue(), pesqCastMap).getLeft();
                if (entry.getKey().equals("delay")) {
                    if (tocttc != null) {
                        tocttc.addDelay(((Number)parseValue).floatValue());
                    }
                    if (tocttcGroup != null) {
                        tocttcGroup.addDelay(((Number)parseValue).floatValue());
                    }
                }
                if (entry.getKey().equals("pesq")) {
                    if (tocttc != null) {
                        tocttc.addLq(((Number)parseValue).floatValue());
                    }
                    if (tocttcGroup != null) {
                        tocttcGroup.addLq(((Number)parseValue).floatValue());
                    }
                }
                setProperty(mm, entry.getKey(), parseValue);
            }
        }
    }

    /**
     * <p>
     * Handler Toc tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class Toc extends AbstractEvent {
        protected final Map<String, Class< ? extends Object>> pesqCastMap;
        Node lastMM = null;
        AMSCall call = null;;
        Integer hook = null;
        Integer simplex = null;

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public Toc(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("configTime", Timestamp.class);
            definedValues.put("setupTime", Timestamp.class);
            definedValues.put("connectTime", Timestamp.class);
            definedValues.put("disconnectTime", Timestamp.class);
            definedValues.put("releaseTime", Timestamp.class);
            definedValues.put("hook", Integer.class);
            definedValues.put("simplex", Integer.class);
            definedValues.put("priority", Integer.class);
            definedValues.put("causeForTermination", Integer.class);
            definedValues.put("errorCode", Integer.class);
            pesqCastMap = new HashMap<String, Class< ? extends Object>>();
            pesqCastMap.put("sendSampleStart", Timestamp.class);
            pesqCastMap.put("pesq", Double.class);
            pesqCastMap.put("delay", Integer.class);
            parameterMap.put("hook", AMSCommandParameters.HOOK);
            parameterMap.put("simplex", AMSCommandParameters.SIMPLEX);
            parameterMap.put("priority", AMSCommandParameters.PRIORITY);
        }

        @Override
        protected void handleAMSCommand(AMSCommandParameters amsCommandParameters, String key, Object parsedValue) {
            super.handleAMSCommand(amsCommandParameters, key, parsedValue);
            // Field field = this.getClass().getField(key);
            // field.set(this, parsedValue);
            if (key.equals("hook")) {
                hook = (Integer)parsedValue;
            } else if (key.equals("simplex")) {
                simplex = (Integer)parsedValue;
            }
        }

        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            List<PropertyCollector> collectorList = getSubCollectors();
            handleCall();
            for (PropertyCollector collector : collectorList) {
                if (collector.getName().equals("pesqResult")) {
                    createAttachmentNode(collector);
                }
            }

        }

        protected void handleCall() {
            if (hook != null && simplex != null) {

                if (hook == 0 && simplex == 0) {
                    tocttc = new AMSCall();
                    call = tocttc;
                    tocttc.setCallType(CallType.INDIVIDUAL);
                    tocttc.addRelatedNode(node);
                    Long disconnectTime = (Long)node.getProperty("disconnectTime", null);
                    if (disconnectTime != null) {
                        tocttc.setCallTerminationBegin(disconnectTime);
                    }
                    if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
                        tocttc.setCallResult(CallResult.FAILURE);
                    } else {
                        tocttc.setCallResult(CallResult.SUCCESS);
                    }
                    Node callerProbe = probeCallCache.get(getPropertyMap().get("probeID"));
                    tocttc.setCallerProbe(callerProbe);
                    tocttc.setCalledPhoneNumber(getPropertyMap().get("calledNumber"));
                    tocttc.setCallSetupBeginTime((Long)node.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, 0l));
                    tocttc.setCallTerminationBegin((Long)node.getProperty("releaseTime", 0l));
                } else if (hook == 1 && simplex == 1) {
                    tocttcGroup = new AMSCall();
                    call = tocttc;
                    tocttcGroup.setCallType(CallType.GROUP);
                    tocttcGroup.addRelatedNode(node);
                    Long disconnectTime = (Long)node.getProperty("disconnectTime", null);
                    if (disconnectTime != null) {
                        tocttcGroup.setCallTerminationBegin(disconnectTime);
                    }
                    if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
                        tocttcGroup.setCallResult(CallResult.FAILURE);
                    } else {
                        tocttcGroup.setCallResult(CallResult.SUCCESS);
                    }
                    Node callerProbe = probeCallCache.get(getPropertyMap().get("probeID"));
                    tocttcGroup.setCallerProbe(callerProbe);
                    tocttcGroup.setCalledPhoneNumber((String)callerProbe.getProperty("phoneNumber", null));
                    tocttcGroup.setCallSetupBeginTime((Long)node.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, 0l));
                    tocttcGroup.setCallTerminationBegin((Long)node.getProperty("releaseTime", 0l));
                }
            }
        }

        /**
         * Creates the attachment node.
         * 
         * @param collector the collector
         * @throws ParseException
         */
        private void createAttachmentNode(PropertyCollector collector) throws ParseException {
            Node mm = neo.createNode();
            NeoUtils.addChild(node, mm, lastMM, neo);
            NodeTypes.MM.setNodeType(mm, neo);
            lastMM = mm;
            Map<String, String> map = collector.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object parseValue = getParcedValue(entry.getKey(), entry.getValue(), pesqCastMap).getLeft();
                if (entry.getKey().equals("delay")) {
                    if (tocttc != null) {
                        tocttc.addDelay(((Number)parseValue).floatValue());
                    }
                    if (tocttcGroup != null) {
                        tocttcGroup.addDelay(((Number)parseValue).floatValue());
                    }
                }
                if (entry.getKey().equals("pesq")) {
                    if (tocttc != null) {
                        tocttc.addLq(((Number)parseValue).floatValue());
                    }
                    if (tocttcGroup != null) {
                        tocttcGroup.addLq(((Number)parseValue).floatValue());
                    }
                }
                setProperty(mm, entry.getKey(), parseValue);
            }
        }
    }

    /**
     * <p>
     * Handler Tpc tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class Tpc extends AbstractEvent {
        protected final Map<String, Class< ? extends Object>> pdResult;
        Node lastMM = null;

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public Tpc(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("connectTime", Timestamp.class);
            definedValues.put("pdpRequest", Timestamp.class);
            definedValues.put("pdpAccept", Timestamp.class);
            definedValues.put("ftpConnReq", Timestamp.class);
            definedValues.put("ftpConnAccept", Timestamp.class);
            definedValues.put("releaseTime", Integer.class);
            definedValues.put("causeForTermination", Integer.class);
            definedValues.put("errorCode", Integer.class);
            pdResult = new HashMap<String, Class< ? extends Object>>();
            pdResult.put("size", Integer.class);
            pdResult.put("transmitStart", Timestamp.class);
            pdResult.put("transmitEnd", Timestamp.class);

        }

        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            List<PropertyCollector> collectorList = getSubCollectors();
            for (PropertyCollector collector : collectorList) {
                if (collector.getName().equals("pdResult")) {
                    createAttachmentNode(collector);
                }
            }
        }

        /**
         * Creates the attachment node.
         * 
         * @param collector the collector
         * @throws ParseException
         */
        private void createAttachmentNode(PropertyCollector collector) throws ParseException {
            Node mm = neo.createNode();
            NeoUtils.addChild(node, mm, lastMM, neo);
            NodeTypes.MM.setNodeType(mm, neo);
            lastMM = mm;
            Map<String, String> map = collector.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object parseValue = getParcedValue(entry.getKey(), entry.getValue(), pdResult).getLeft();
                setProperty(mm, entry.getKey(), parseValue);
            }
        }
    }

    /**
     * <p>
     * Handler sendMsg tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class SendMsg extends AbstractEvent {
        protected final Map<String, Class< ? extends Object>> sendReport;
        Node lastMM = null;
        Long timeEnd;

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public SendMsg(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("msgType", Integer.class);
            definedValues.put("dataLength", Integer.class);
            definedValues.put("sendTime", Timestamp.class);
            definedValues.put("msgRef", Integer.class);
            definedValues.put("releaseTime", Integer.class);
            definedValues.put("errorCode", Integer.class);
            sendReport = new HashMap<String, Class< ? extends Object>>();
            sendReport.put("reportTime", Timestamp.class);
            sendReport.put("status", Integer.class);
        }

        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            handleCall();
            List<PropertyCollector> collectorList = getSubCollectors();
            for (PropertyCollector collector : collectorList) {
                if (collector.getName().equals("sendReport")) {
                    createAttachmentNode(collector);
                }
            }
            msgCall.setCallSetupEndTime(timeEnd);
        }

        /**
         *
         */
        private void handleCall() {
            assert msgCall == null;
            msgCall = new AMSCall();

            msgCall.addRelatedNode(node);
            Long sendTime = (Long)node.getProperty("sendTime", null);
            msgCall.setCallSetupBeginTime(sendTime);
            timeEnd = sendTime;
            if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
                msgCall.setCallResult(CallResult.FAILURE);
            } else {
                msgCall.setCallResult(CallResult.SUCCESS);
            }
            Node callerProbe = probeCallCache.get(getPropertyMap().get("probeID"));
            msgCall.setCallerProbe(callerProbe);
            msgCall.addRelatedNode(node);
            msgCall.setCalledPhoneNumber(getPropertyMap().get("calledNumber"));
            String type = getPropertyMap().get("msgType");
            if (type.equals("12")){
                msgCall.setCallType(CallType.SDS);
            }else{
                assert type.equals("13");
                msgCall.setCallType(CallType.TSM);
            }
            
        }

        /**
         * Creates the attachment node.
         * 
         * @param collector the collector
         * @throws ParseException
         */
        private void createAttachmentNode(PropertyCollector collector) throws ParseException {
            Node mm = neo.createNode();
            NeoUtils.addChild(node, mm, lastMM, neo);
            NodeTypes.MM.setNodeType(mm, neo);
            lastMM = mm;
            Map<String, String> map = collector.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object parseValue = getParcedValue(entry.getKey(), entry.getValue(), sendReport).getLeft();
                if (sendReport.get(entry.getKey())==Timestamp.class){
                    timeEnd=Math.max(timeEnd, (Long)parseValue);
                }
                setProperty(mm, entry.getKey(), parseValue);
            }
        }
    }

    /**
     * <p>
     * Handler ReceiveMsg tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class ReceiveMsg extends AbstractEvent {
        Node lastMM = null;

        /**
         * Instantiates a new cell resel.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public ReceiveMsg(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("msgType", Integer.class);
            definedValues.put("dataLength", Integer.class);
            definedValues.put("receiveTime", Timestamp.class);
            definedValues.put("msgRef", Integer.class);
            definedValues.put("releaseTime", Integer.class);
            definedValues.put("errorCode", Integer.class);
        }
        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            handleCall();
        }
        private void handleCall() {
            if (msgCall==null){
                LOGGER.debug("Found resive message without send event "+basename);
                return;
            }
            Long reciveTime = (Long)node.getProperty("receiveTime", null);
            if (reciveTime!=null){
                msgCall.setCallTerminationBegin(reciveTime);
                msgCall.setCallTerminationEnd(reciveTime);
            }
            if (node.hasProperty("errorCode") || node.hasProperty("errCode")) {
                msgCall.setCallResult(CallResult.FAILURE);
            } 
            msgCall.addCalleeProbe(probeCallCache.get(getPropertyMap().get("probeID")));
            msgCall.addRelatedNode(node);
        }
    }

    /**
     * <p>
     * Handler groupAttach tag
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class GroupAttach extends AbstractEvent {

        /** The last mm. */
        Node lastMM = null;

        /**
         * Instantiates a new group attach.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        public GroupAttach(String tagName, IXmlTag parent) {
            super(tagName, parent, true);
            definedValues.put("groupAttachTime", Timestamp.class);
            definedValues.put("errorCode", Integer.class);
        }

        @Override
        protected void handleCollector() throws ParseException {
            super.handleCollector();
            List<PropertyCollector> collectorList = getSubCollectors();
            for (PropertyCollector collector : collectorList) {
                if (collector.getName().equals("attachment")) {
                    createAttachmentNode(collector);
                }
            }

        }

        /**
         * Creates the attachment node.
         * 
         * @param collector the collector
         */
        private void createAttachmentNode(PropertyCollector collector) {
            Node mm = neo.createNode();
            NeoUtils.addChild(node, mm, lastMM, neo);
            NodeTypes.MM.setNodeType(mm, neo);
            lastMM = mm;
            Map<String, String> map = collector.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                setProperty(mm, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * <p>
     * Handler of tag "events"
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class EventTag extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "events";

        /**
         * Instantiates a new event tag.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        protected EventTag(String tagName, IXmlTag parent) {
            super(tagName, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            Class< ? extends AbstractEvent> klass = subNodes.get(localName);
            assert klass != null : localName;
            try {
                Constructor< ? extends AbstractEvent> konstr = klass.getConstructor(AMSXMLoader.class, String.class, IXmlTag.class);
                return konstr.newInstance(AMSXMLoader.this, localName, this);
            } catch (SecurityException e) {
                // TODO Handle SecurityException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (NoSuchMethodException e) {
                // TODO Handle NoSuchMethodException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (IllegalArgumentException e) {
                // TODO Handle IllegalArgumentException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (InstantiationException e) {
                // TODO Handle InstantiationException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (IllegalAccessException e) {
                // TODO Handle IllegalAccessException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (InvocationTargetException e) {
                // TODO Handle InvocationTargetException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }

        }

    }

    /**
     * The Class ProbeIDNumberMap.
     */
    public class ProbeIDNumberMap extends PropertyCollector {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "probeIDNumberMap";

        /**
         * Instantiates a new probe id number map.
         * 
         * @param tagName the tag name
         * @param parent the parent
         */
        protected ProbeIDNumberMap(String tagName, IXmlTag parent) {
            super(tagName, parent, false);
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the i xml tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (openTag == null) {
                handleCollector();
                return parent;
            }
            propertyMap.put(localName, chars.toString());
            openTag = null;
            return this;
        }

        /**
         * Handle collector.
         */
        private void handleCollector() {
            Map<String, String> map = getPropertyMap();
            String id = map.get("probeID");
            if (probeCache.get(id) != null) {
                return;
            }
            Node probeNew = NeoUtils.findOrCreateProbeNode(networkNode, id, neo);
            Node currentProbeCalls = NeoUtils.getCallsNode(callDataset, id, probeNew, neo);
            probeCache.put(id, probeNew);
            probeCallCache.put(id, currentProbeCalls);
            String phone = map.get("phoneNumber");
            if (!StringUtils.isEmpty(phone)) {
                phoneNumberCache.put(phone, id);
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object valueToSave;
                String key = entry.getKey();
                if (key.equals("locationArea")) {
                    key = INeoConstants.PROBE_LA;
                    valueToSave = Integer.parseInt(entry.getValue());
                } else if (key.equals("frequency")) {
                    key = INeoConstants.PROBE_F;
                    valueToSave = Double.parseDouble(entry.getValue());
                } else {
                    valueToSave = entry.getValue();
                }
                setIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers, probeNew, key, valueToSave);
            }

            index(probeNew);
        }

    }

    /**
     * <p>
     * Factory class
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class Factory implements IXmlTagFactory {

        /**
         * Creates the instance.
         * 
         * @param tagName the tag name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (ProbeIDNumberMap.TAG_NAME.equals(tagName)) {
                return new ProbeIDNumberMap(tagName, null);
            } else if (EventTag.TAG_NAME.equals(tagName)) {
                return new EventTag(tagName, null);
            }
            return null;
        }

    }

    public static class AMSCall extends Call {
        protected String calledPhoneNumber;

        public String getCalledPhoneNumber() {
            return calledPhoneNumber;
        }

        public void setCalledPhoneNumber(String callerPhoneNumber) {
            this.calledPhoneNumber = callerPhoneNumber;
        }

    }

}
