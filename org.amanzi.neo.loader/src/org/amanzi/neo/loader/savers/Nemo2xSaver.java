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

package org.amanzi.neo.loader.savers;

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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.DriveEvents;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.NemoEvents;
import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Nemo 2x Saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Nemo2xSaver extends AbstractHeaderSaver<LineTransferData> implements IStructuredSaver<LineTransferData> {
    protected static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    protected static final String TIME_FORMAT = "HH:mm:ss.S";
    protected Calendar workDate;
    protected Node parent;
    protected Node lastMNode;
    protected Node lastMPNode;
    private SimpleDateFormat timeFormat;

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
                        NeoLoaderPlugin.error("Wrong context id:" + field);
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
        if (parameters.isEmpty()) {
            return;
        }

        if (event == null) {
            return;
        }
        Map<String, Object> parParam;
        try {
            parParam = event.fill(getVersion(), parameters);
        } catch (Exception e1) {
            // TODO Handle Exception
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
        if (parParam.isEmpty()) {
            return;
        }
        DriveEvents driveEvents = (DriveEvents)parParam.remove(NemoEvents.DRIVE_EVENTS);
        List<Map<String, Object>> subNodes = (List<Map<String, Object>>)parParam.remove(NemoEvents.SUB_NODES);
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
            for (int i = 0; i < contextId.size() && i < contextName.size(); i++) {
                if (contextId.get(i) != 0) {
                    parParam.put(contextName.get(i), contextId.get(i));
                }
            }
        }
        if (workDate == null && event == NemoEvents.START) {
            workDate = new GregorianCalendar();
            Date date;
            try {
                date = EVENT_DATE_FORMAT.parse((String)parParam.get("Date"));

            } catch (Exception e) {
                NeoLoaderPlugin.error("Wrong time format" + e.getLocalizedMessage());
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
        // create M node
        lastMNode=service.createMNode(parent, lastMNode);
        statistic.increaseTypeCount(rootname, NodeTypes.M.getId(), 1);
        updateTx(1, 1);
        long timestamp;
        try {
            timestamp = getTimeStamp(1, timeFormat.parse(time));
        } catch (ParseException e) {
            // some parameters do not have time
            // NeoLoaderPlugin.error(e.getLocalizedMessage());
            timestamp = 0;
        }

        if (timestamp != 0) {
            setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
        }
        setProperty(rootname, NodeTypes.M.getId(), lastMNode, INeoConstants.PROPERTY_NAME_NAME, eventId);
        if (lastMPNode != null) {
            lastMNode.createRelationshipTo(lastMPNode, GeoNeoRelationshipTypes.LOCATION);
            if (timestamp != 0&&!lastMPNode.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)) {
                setProperty(rootname, NodeTypes.MP.getId(), lastMPNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                lastMPNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            }
            if (driveEvents != null) {
                getIndexService().index(lastMPNode, INeoConstants.EVENTS_LUCENE_INDEX_NAME, rootname);
            }
        }
        index(lastMNode);
        //create subnodes
        if (subNodes == null) {
            return;
        }
        Node lastMMNode=null;
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
                    lastMMNode =service.createMMNode(parent, lastMMNode);
                    if (timestamp != 0) {
                        setProperty(rootname, NodeTypes.MM.getId(), lastMMNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                    }
/*                    findOrCreateVirtualFileNode(mm);
                    NodeTypes.HEADER_MS.setNodeType(mm, neo);
                    mm.setProperty(INeoConstants.PROPERTY_NAME_NAME, event.eventId);
                    findOrCreateVirtualFileNode(mm);
                    if (virtualMnode != null) {
                        virtualMnode.createRelationshipTo(mm, GeoNeoRelationshipTypes.NEXT);
                    }
                    virtualMnode = mm;
                    for (String key : propertyMap.keySet()) {

                        Object parsedValue = propertyMap.get(key);
                        if (parsedValue != null && parsedValue.getClass().isArray()) {
                            setProperty(mm, key, parsedValue);
                        } else {
                            setIndexProperty(headersVirt, mm, key, parsedValue);
                        }
                    }

                    index(mm);
                    if (pointNode != null) {
                        mm.createRelationshipTo(pointNode, GeoNeoRelationshipTypes.LOCATION);
                    }*/
                } catch (Exception e) {
                    NeoLoaderPlugin.exception(e);
                    e.printStackTrace();
                }
            }

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
     * @return
     */
    private String getVersion() {
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
        workDate=null;
        parent = service.getFileNode(rootNode, element.getFileName());
        lastMNode = null;
        lastMPNode=null;
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
    }


    protected void addIndex(LineTransferData element) {
        try {
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Converts to lower case and replaces all illegal characters with '_' and removes trailing '_'.
     * This is useful for creating a version of a header or property name that can be used as a
     * variable or method name in programming code, notably in Ruby DSL code.
     * 
     * @param original header String
     * @return edited String
     */
    protected final static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_").replaceAll("\\_$", "").toLowerCase();
    }
}
