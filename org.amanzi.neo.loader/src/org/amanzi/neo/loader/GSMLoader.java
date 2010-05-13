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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Loader for GSM data
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GSMLoader extends DriveLoader {
    private static boolean needParceHeader = true;
    private final LinkedHashMap<String, Header> headers;
    private String time = null;
    private Float currentLatitude = null;
    private Float currentLongitude = null;
    private Node mNode = null;
    private final ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    /**
     * Constructor
     * 
     * @param directory
     * @param datasetName
     * @param display
     */
    public GSMLoader(String directory, String datasetName, Display display) {
        initialize("GSM", null, directory, display);
        basename = datasetName;
        headers = getHeaderMap(1).headers;
        needParceHeader = true;
    }

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public GSMLoader(Calendar workTime, String filename, Display display, String dataset) {
        driveType = DriveTypes.GSM;
        initialize("GSM", null, filename, display, dataset);
        basename = dataset;
        headers = getHeaderMap(1).headers;
        needParceHeader = true;
        initializeKnownHeaders();
        
        try {
            addIndex(NodeTypes.MP.getId(), NeoUtils.getLocationIndexProperty(dataset));
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
        }
        catch (IOException e) {
            //TODO: 
            e.printStackTrace();
        }
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        addKnownHeader(1, "timestamp", ".*timestamp.*");
        addKnownHeader(1, "latitude", ".*latitude.*");
        addKnownHeader(1, "longitude", ".*longitude.*");
        dropHeaderStats(1, new String[] {"timestamp", "latitude", "longitude"});
    }

    @Override
    protected Node getStoringNode(Integer key) {
        GisProperties gisProperties = gisNodes.get(dataset);
        return gisProperties == null ? null : gisProperties.getGis();
    }

    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }

    @Override
    protected void parseLine(String line) {
        try {
            // debug(line);
            List<String> fields = splitLine(line);
            if (fields.size() < 2)
                return;
            if (this.isOverLimit())
                return;
            this.incValidMessage(); // we have not filtered the message out on non-accepted content
            this.incValidChanged(); // we have not filtered the message out on lack of data change
            Map<String, Object> lineData = makeDataMap(fields);
            this.time = (String)lineData.get("timestamp");
            // Date nodeDate = (Date)lineData.get("timestamp");
            // this.timestamp = getTimeStamp(nodeDate);
            Float latitude = (Float)lineData.get("latitude");
            Float longitude = (Float)lineData.get("longitude");
            if (time == null || latitude == null || longitude == null) {
                return;
            }
            if ((latitude != null)
                    && (longitude != null)
                    && (((currentLatitude == null) && (currentLongitude == null)) || ((Math.abs(currentLatitude - latitude) > 10E-10) || (Math.abs(currentLongitude
                            - longitude) > 10E-10)))) {
                currentLatitude = latitude;
                currentLongitude = longitude;
                saveData(); // persist the current data to database
            }
            this.incValidLocation(); // we have not filtered the message out on lack of location
            if (lineData.size() > 0) {
                data.add(lineData);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * This method is called to dump the current cache of signals as one located point linked to a
     * number of signal strength measurements.
     */
    private void saveData() {
        if (data.size() > 0) {
            Transaction transaction = neo.beginTx();
            try {
                Node mp = neo.createNode();
                mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.MP.getId());
                mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, this.time);
                mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, currentLatitude.doubleValue());
                mp.setProperty(INeoConstants.PROPERTY_LON_NAME, currentLongitude.doubleValue());
                index(mp);

                // debug("Added measurement point: " + propertiesString(mp));

                boolean haveEvents = false;
                for (Map<String, Object> dataLine : data) {
                    Node m = neo.createNode();
                    findOrCreateFileNode(m);
                    m.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
                    for (Map.Entry<String, Object> entry : dataLine.entrySet()) {
                        if (entry.getKey().equals(INeoConstants.SECTOR_ID_PROPERTIES)) {
                            mp.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, entry.getValue());
                            // ms.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, entry.getValue());
                        } else if ("timestamp".equals(entry.getKey())) {

                            try {
                                String pattern = "dd.MM.yyyy hh:mm:ss";
                                SimpleDateFormat sf = new SimpleDateFormat(pattern);
                                Date date = sf.parse(entry.getValue().toString());
                                long timeStamp = date.getTime();
                                updateTimestampMinMax(1, timeStamp);
                                if (timeStamp != 0) {
                                    m.setProperty(entry.getKey(), timeStamp);
                                    mp.setProperty(entry.getKey(), timeStamp);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            m.setProperty(entry.getKey(), entry.getValue());
                            haveEvents = haveEvents || INeoConstants.PROPERTY_TYPE_EVENT.equals(entry.getKey());
                        }
                    }
                    m.createRelationshipTo(mp, GeoNeoRelationshipTypes.LOCATION);
                    if (mNode != null) {
                        mNode.createRelationshipTo(m, GeoNeoRelationshipTypes.NEXT);
                    }
                    m.setProperty(INeoConstants.PROPERTY_NAME_NAME, getMNodeName(dataLine));
                    mNode = m;
                    index(m);
                }
                if (haveEvents) {
                    index.index(mp, INeoConstants.EVENTS_LUCENE_INDEX_NAME, dataset);
                }
                GisProperties gisProperties = getGisProperties(dataset);
                gisProperties.updateBBox(currentLatitude, currentLongitude);
                gisProperties.checkCRS(currentLatitude, currentLongitude, null);
                gisProperties.incSaved();
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        data.clear();
    }

    /**
     * get name of m node
     * 
     * @param dataLine - node data
     * @return node name
     */
    private String getMNodeName(Map<String, Object> dataLine) {
        // TODO check name of node
        Object timeNode = dataLine.get("time");
        return timeNode == null ? "ms node" : timeNode.toString();
    }
}
