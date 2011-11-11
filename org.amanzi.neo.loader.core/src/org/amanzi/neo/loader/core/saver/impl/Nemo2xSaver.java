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

package org.amanzi.neo.loader.core.saver.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *  Saver for nemo data v.2.01
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Nemo2xSaver extends DatasetSaver<LineTransferData> implements IStructuredSaver<LineTransferData> {
    protected static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    protected static final String TIME_FORMAT = "HH:mm:ss.S";
    protected Calendar workDate;
    protected Node parent;
    protected Node lastMNode;
    protected Node lastMPNode;
    protected SimpleDateFormat timeFormat;
    private Node virtualParent;
    private Node virtualDataset;
    private Node lastMsNode;
    private String virtualDatasetName;
//    protected DriveEvents driveEvents;
    protected List<Map<String, Object>> subNodes;
    protected  MetaData metadata=new MetaData("dataset", MetaData.SUB_TYPE,"nemo","version","2.01");

    @Override
    public void save(LineTransferData element) {
        String line = element.getStringLine();
        if (StringUtil.isEmpty(line)) {
            return;
        }
        String[] parsedLineArr = splitLine(line);
        List<String> parcedLine = Arrays.asList(parsedLineArr);
        if (parcedLine.size() < 1) {
            return;
        }
        // parse
        String eventId = parcedLine.get(0);
        NemoEvents event = NemoEvents.getEventById(eventId);
        String time = parcedLine.get(1);
        String numberContextId = parcedLine.get(2);
        List<Integer> contextId = new ArrayList<Integer>();
        Integer firstParamsId = 3;
        if (!numberContextId.isEmpty()) {
            int numContext = Integer.parseInt(numberContextId);
            for (int i = 1; i <= numContext; i++) {
                int value = 0;
                String field = parcedLine.get(firstParamsId++);
                if (!field.isEmpty()) {
                    try {
                        value = Integer.parseInt(field);
                    } catch (NumberFormatException e) {
                        // TODO Handle NumberFormatException
                        error("Wrong context id:" + field);
                        value = 0;
                    }
                }
                contextId.add(value);
            }
        }
        ArrayList<String> parameters = new ArrayList<String>();
        for (int i = firstParamsId; i < parcedLine.size(); i++) {
            parameters.add(parcedLine.get(i));
        }
        // analyse
        Map<String, Object> parsedParameters = analyseKnownParameters(element, event, contextId, parameters);
        if (parsedParameters == null) {
            return;
        }
        long timestamp;
        try {
            timestamp = getTimeStamp(1, timeFormat.parse(time));
        } catch (ParseException e) {
            // some parameters do not have time
            // NeoLoaderPlugin.error(e.getLocalizedMessage());
            timestamp = 0;
        }
        if ("GPS".equalsIgnoreCase(eventId)) {
            Float lon = (Float)parsedParameters.get("lon");
            Float lat = (Float)parsedParameters.get("lat");
            if ((lon == null || lat == null) || (lon == 0) && (lat == 0)) {
                return;
            }
            lastMPNode = createMpLocation(lastMPNode, element, time, timestamp, lat, lon);
        }
        // create M node
//        createMNode(eventId, driveEvents, timestamp, parsedParameters);
        // create subnodes
        createSubNodes(eventId, subNodes, timestamp);

    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> analyseKnownParameters(LineTransferData element, NemoEvents event, List<Integer> contextId, ArrayList<String> parameters) {
        if (parameters.isEmpty()) {
            return null;
        }

        if (event == null) {
            return null;
        }
        Map<String, Object> parParam;
        try {
            parParam = event.fill(getVersion(), parameters);
        } catch (Exception e1) {
            error(String.format("Line %s not parsed", element.getLine()));
            e1.printStackTrace();
            // exception(e1);
            return null;
        }
        if (parParam.isEmpty()) {
            return null;
        }
//        driveEvents = (DriveEvents)parParam.remove(NemoEvents.DRIVE_EVENTS);
        subNodes = (List<Map<String, Object>>)parParam.remove(NemoEvents.SUB_NODES);
        // TODO check documentation
        if (subNodes != null) {
            // store in parameters like prop1,prop2...
            int i = 0;
            for (Map<String, Object> oneSet : subNodes) {
                i++;
                for (Map.Entry<String, Object> entry : oneSet.entrySet()) {
                    parParam.put(new StringBuilder(entry.getKey()).append(i).toString(), entry.getValue());
                }
            }
            subNodes.clear();
        }
        // add context field
        if (parParam.containsKey(NemoEvents.FIRST_CONTEXT_NAME)) {
            List<String> contextName = (List<String>)parParam.get(NemoEvents.FIRST_CONTEXT_NAME);
            parParam.remove(NemoEvents.FIRST_CONTEXT_NAME);
            if (contextId != null) {
                for (int i = 0; i < contextId.size() && i < contextName.size(); i++) {
                    if (contextId.get(i) != 0) {
                        parParam.put(contextName.get(i), contextId.get(i));
                    }
                }
            }
        }
        if (workDate == null && event == NemoEvents.START) {
            workDate = new GregorianCalendar();
            Date date;
            try {
                date = EVENT_DATE_FORMAT.parse((String)parParam.get("Date"));

            } catch (Exception e) {
                error("Wrong time format" + e.getLocalizedMessage());
                date = new Date();
            }
            workDate.setTime(date);
        }
        // Pechko_E make property names Ruby-compatible
        Set<Entry<String, Object>> entrySet = parParam.entrySet();
        // TODO Check may be a new map is unnecessary and we can use parsedParameters
        Map<String, Object> parsedParameters = new HashMap<String, Object>(parParam.size());
        for (Entry<String, Object> entry : entrySet) {
            parsedParameters.put(cleanHeader(entry.getKey()), entry.getValue());
        }
        return parsedParameters;
    }

    /**
     * Creates the m node.
     * 
     * @param eventId the event id
     * @param driveEvents the drive events
     * @param timestamp the timestamp
     * @param parsedParameters
     */
//    protected void createMNode(String eventId, DriveEvents driveEvents, long timestamp, Map<String, Object> parsedParameters) {
//        lastMNode = service.createMNode(parent, lastMNode);
//        statistic.updateTypeCount(rootname, NodeTypes.M.getId(), 1);
//        updateTx(1, 1);
//
//        if (timestamp != 0) {
//            setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
//        }
//        setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_NAME_NAME, eventId);
//        setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_TYPE_EVENT, eventId);
//        if (driveEvents != null) {
//            setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_DRIVE_TYPE_EVENT, driveEvents.name());
//        }
//        for (Map.Entry<String, Object> entry : parsedParameters.entrySet()) {
//            if (lastMNode.hasProperty(entry.getKey())) {
//                continue;
//            }
//            setProperty(rootname, NodeTypes.M.getId(), lastMNode, entry.getKey(), entry.getValue());
//        }
//
//        if (lastMPNode != null) {
//            lastMNode.createRelationshipTo(lastMPNode, GeoNeoRelationshipTypes.LOCATION);
//            updateTx(0, 1);
//            if (timestamp != 0 && !lastMPNode.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)) {
//                setProperty(rootname, NodeTypes.MP.getId(), lastMPNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
//                lastMPNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
//            }
//            if (driveEvents != null) {
//                getIndexService().index(lastMPNode, INeoConstants.EVENTS_LUCENE_INDEX_NAME, rootname);
//            }
//        }
//
//        index(lastMNode);
//    }

    /**
     * Creates the sub nodes.
     * 
     * @param eventId the event id
     * @param subNodes the sub nodes
     * @param timestamp the timestamp
     */
    protected void createSubNodes(String eventId, List<Map<String, Object>> subNodes, long timestamp) {
        if (subNodes == null) {
            return;
        }
        for (Map<String, Object> propertyMap : subNodes) {
            Iterator<Entry<String, Object>> iter = propertyMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Object> entry = iter.next();
                if (entry.getValue() == null) {
                    iter.remove();
                }
            }
            if (propertyMap.isEmpty()) {
                continue;
            }
            try {
                lastMsNode = service.createMsNode(getVirtualParent(), lastMsNode);
                statistic.updateTypeCount(virtualDatasetName, NodeTypes.M.getId(), 1);
                updateTx(1, 1);
                if (timestamp != 0) {
                    setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                }
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_NAME_NAME, eventId);

                for (String key : propertyMap.keySet()) {
                    Object parsedValue = propertyMap.get(key);
                    setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, key, parsedValue);
                }

                index(lastMsNode);
                if (lastMPNode != null) {
                    lastMsNode.createRelationshipTo(lastMPNode, GeoNeoRelationshipTypes.LOCATION);
                    updateTx(0, 1);
                }
            } catch (Exception e) {
                exception(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the virtual parent.
     * 
     * @return the virtual parent
     */
    protected Node getVirtualParent() {
        if (virtualParent == null) {
            virtualParent = service.getFileNode(virtualDataset, element.getFileName());
        }
        return virtualParent;
    }

    /**
     * Gets the virtual dataset.
     * 
     * @return the virtual dataset
     */
    protected Node getVirtualDataset() {
        if (virtualDataset == null) {
            virtualDataset = service.getVirtualDataset(rootNode, DriveTypes.MS);
            virtualDatasetName = DriveTypes.MS.getFullDatasetName(rootname);
        }
        return virtualDataset;
    }

    /**
     * get Timestamp of nodeDate
     * 
     * @param nodeDate date of node
     * @return long (0 if nodeDate==null)
     */
    @SuppressWarnings("deprecation")
    protected long getTimeStamp(Integer key, Date nodeDate) {
        if (nodeDate == null || workDate == null) {
            return 0L;
        }
        final int nodeHours = nodeDate.getHours();
        workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
        workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
        workDate.set(Calendar.SECOND, nodeDate.getSeconds());
        final long timestamp = workDate.getTimeInMillis();
        return timestamp;
    }


    /**
     * Gets the version.
     *
     * @return the version
     */
    protected String getVersion() {
        return "2.01";
    }

    /**
     * @param line
     * @return
     */
    protected String[] splitLine(String line) {
        return line.split(",");
    }

    @Override
    protected void fillRootNode(Node rootNode, LineTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.MP.getId();
    }

    @Override
    public boolean beforeSaveNewElement(LineTransferData element) {
        workDate = null;
        parent = service.getFileNode(rootNode, element.getFileName());
        virtualParent = null;
        lastMNode = null;
        lastMPNode = null;
        lastMsNode = null;
        return false;
    }

    @Override
    public void finishSaveNewElement(LineTransferData element) {
    }

    @Override
    public void init(LineTransferData element) {
        super.init(element);
        timeFormat = new SimpleDateFormat(TIME_FORMAT);
        addIndex(element);
        startMainTx(4000);
        initializeIndexes();
        virtualDataset = null;
        virtualParent = null;
    }

    protected void addIndex(LineTransferData element) {
        try {
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[]{metadata});
    }
}
