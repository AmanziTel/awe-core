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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.AbstractLoader.PropertyMapper;
import org.amanzi.neo.loader.AbstractLoader.StringMapper;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.SectorIdentificationType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * Loader for GSM data
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPSLoader extends DriveLoader {
    private static boolean needParceHeader = true;
    private String time = null;
    private Float currentLatitude = null;
    private Float currentLongitude = null;
    private Node mNode = null;
    private final ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    
    private LuceneIndexService luceneService;
    
    private String luceneIndexName;
    
    private static final String TIMESTAMP_DATE_FORMAT = "HH:mm:ss.S";

    /**
     * Constructor
     * 
     * @param directory
     * @param datasetName
     * @param display
     */
    public GPSLoader(String directory, String datasetName, Display display) {
    	driveType = DriveTypes.TEMS;
    	initialize("GPS", null, directory, display);
    	
        if (datasetName == null || datasetName.trim().isEmpty()) {
            this.dataset = null;
        } else {
            this.dataset = datasetName.trim();
        }

        if (gisType == null) {
            gisType = GisTypes.DRIVE;
        }
    	
        basename = datasetName;
        getHeaderMap(1);
        needParceHeader = true;
        
        initializeLucene();
    }

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public GPSLoader(Calendar workTime, String filename, Display display, String dataset) {
        driveType = DriveTypes.TEMS;
        initialize("GSM", null, filename, display, dataset);
        basename = dataset;
        getHeaderMap(1);
        needParceHeader = true;
        initializeKnownHeaders();
        luceneIndexName=null;
        
        try {
            addIndex(NodeTypes.MP.getId(), NeoUtils.getLocationIndexProperty(dataset));
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
        }
        catch (IOException e) {
            //TODO: 
            e.printStackTrace();
        }
        
        initializeLucene();
    }
    
    private void initializeLucene() {
        luceneService = NeoServiceProviderUi.getProvider().getIndexService();
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
//    private void initializeKnownHeaders() {
//        addKnownHeader(1, "timestamp", ".*timestamp.*", false);
//        
//        addKnownHeader(1, "latitude", ".*latitude.*", false);
//        addKnownHeader(1, "longitude", ".*longitude.*", false);
//        addKnownHeader(1, INeoConstants.SECTOR_ID_PROPERTIES, "cellid", false);
//        dropHeaderStats(1, new String[] {"timestamp", "latitude", "longitude"});
//    }
    
    
    /**
     * Add a known header entry as well as mark it as a main header. All other fields will be
     * assumed to be sector properties.
     * 
     * @param key
     * @param regexes
     */
    private void addMainHeader(String key, String[] regexes) {
        addKnownHeader(1, key, regexes, false);
        // mainHeaders.add(key);
    }
    
    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        // addMainHeader(INeoConstants.PROPERTY_LATITUDE_NAME,
        // getPossibleHeaders(DataLoadPreferences.DR_LATITUDE));
        // addMainHeader(INeoConstants.PROPERTY_LONGITUDE_NAME,
        // getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));
        addMainHeader(INeoConstants.PROPERTY_BCCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
        addMainHeader(INeoConstants.PROPERTY_TCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_TCH));
        addMainHeader(INeoConstants.PROPERTY_SC_NAME, getPossibleHeaders(DataLoadPreferences.DR_SC));
        addMainHeader(INeoConstants.PROPERTY_PN_NAME, getPossibleHeaders(DataLoadPreferences.DR_PN));
        addMainHeader(INeoConstants.PROPERTY_EcIo_NAME, getPossibleHeaders(DataLoadPreferences.DR_EcIo));
        addMainHeader(INeoConstants.PROPERTY_RSSI_NAME, getPossibleHeaders(DataLoadPreferences.DR_RSSI));
        addMainHeader(INeoConstants.PROPERTY_CI_NAME, getPossibleHeaders(DataLoadPreferences.DR_CI));

        // String[] latH = getPossibleHeaders(DataLoadPreferences.DR_LATITUDE);
        // addKnownHeader(1, INeoConstants.PROPERTY_LATITUDE_NAME, latH, false);

        // addMainHeader(INeoConstants.PROPERTY_LONGITUDE_NAME,
        // getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));

        PropertyMapper cpm = new PropertyMapper() {

            @Override
            public Object mapValue(String originalValue) {
                try {
                    return Float.valueOf(originalValue);
                } catch (NumberFormatException e) {
                    // TODO: handle exception
                }
                Pattern p = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
                Matcher m = p.matcher(originalValue);
                if (m.matches()) {
                    try {
                        // System.out.println(m.group(1));
                        return Float.valueOf(m.group(1));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                } else {
                    // System.out.println("originalValue: " + originalValue);
                    return null;
                }
                return null;
            }
        };
        String[] headers = getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE);
        for (String singleHeader : headers) {
            addMappedHeader(1, singleHeader, singleHeader, "parsedLongitude", cpm);
        }

        headers = getPossibleHeaders(DataLoadPreferences.DR_LATITUDE);
        for (String singleHeader : headers) {
            addMappedHeader(1, singleHeader, singleHeader, "parsedLatitude", cpm);
        }
        // addKnownHeader(1, "longitude", ".*longitude", false);
        addKnownHeader(1, "ms", "MS", false);
        addMappedHeader(1, "ms", "MS", "ms", new StringMapper());
        addMappedHeader(1, "message_type", "Message Type", "message_type", new StringMapper());
        addMappedHeader(1, "event", "Event Type", "event_type", new PropertyMapper() {

            @Override
            public Object mapValue(String originalValue) {
                return originalValue.replaceAll("HO Command.*", "HO Command");
            }
        });
        
        //lagutko, add additional header for cell id
        addKnownHeader(1, INeoConstants.SECTOR_ID_PROPERTIES, ".*Cell Id.*", true);

        final SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_DATE_FORMAT);
        addMappedHeader(1, "time", "Timestamp", "timestamp", new PropertyMapper() {

            @Override
            public Object mapValue(String time) {
                Date datetime;
                try {
                    datetime = df.parse(time);
                } catch (ParseException e) {
                    SimpleDateFormat dfn = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    try {
                        datetime = dfn.parse(time);
                    } catch (ParseException e1) {
                        error(e.getLocalizedMessage());
                        return 0L;
                    }

                }
                return datetime;
            }
        });
        PropertyMapper intMapper = new PropertyMapper() {

            @Override
            public Object mapValue(String value) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    error(e.getLocalizedMessage());
                    return 0L;
                }
            }
        };
        addMappedHeader(1, "all_rxlev_full", "All-RxLev Full", "all_rxlev_full", intMapper);
        addMappedHeader(1, "all_rxlev_sub", "All-RxLev Sub", "all_rxlev_sub", intMapper);
        addMappedHeader(1, "all_rxqual_full", "All-RxQual Full", "all_rxqual_full", intMapper);
        addMappedHeader(1, "all_rxqual_sub", "All-RxQual Sub", "all_rxqual_sub", intMapper);
        addMappedHeader(1, "all_sqi", "All-SQI", "all_sqi", intMapper);
        List<String> keys = new ArrayList<String>();

        for (int i = 1; i <= 12; i++) {
            keys.add("all_active_set_channel_" + i);
            keys.add("all_active_set_pn_" + i);
            keys.add("all_active_set_ec_io_" + i);
        }
        keys.add("all_pilot_set_count");
        addNonDataHeaders(1, keys);
        PropertyMapper floatMapper = new PropertyMapper() {

            @Override
            public Object mapValue(String value) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    error(e.getLocalizedMessage());
                    return 0L;
                }
            }
        };
        addMappedHeader(1, "all_sqi_mos", "All-SQI MOS", "all_sqi_mos", floatMapper);
    }
    
    
    public Object testMapValue(String time) {
        Date datetime;
        try {
            datetime = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse(time);
        } catch (ParseException e) {
            error(e.getLocalizedMessage());
            return 0L;
        }
        return datetime;
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return datasetNode;
    }

    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.M.getId();
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
            this.time = (String)lineData.get("time");
            // Date nodeDate = (Date)lineData.get("timestamp");
            // this.timestamp = getTimeStamp(nodeDate);
            Float latitude = (Float)lineData.get("gps_latitude");
            Float longitude = (Float)lineData.get("gps_longitude");
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
                    if (luceneIndexName==null){
                        luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(datasetNode, INeoConstants.SECTOR_ID_PROPERTIES, NodeTypes.M);
                    }
                    m.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
                    for (Map.Entry<String, Object> entry : dataLine.entrySet()) {
                        if (entry.getKey().equals(INeoConstants.SECTOR_ID_PROPERTIES)) {
                            luceneService.index(m, luceneIndexName, entry.getValue());
                            m.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, entry.getValue());
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
                                //TODO:
                                e.printStackTrace();
                            }
                        } else {
                            m.setProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    m.createRelationshipTo(mp, GeoNeoRelationshipTypes.LOCATION);
                    if (mNode != null) {
                        mNode.createRelationshipTo(m, GeoNeoRelationshipTypes.NEXT);
                    }
                    m.setProperty(INeoConstants.PROPERTY_NAME_NAME, getMNodeName(dataLine));
                    mNode = m;
                    index(m);
                    
                    storingProperties.values().iterator().next().incSaved();
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
        Object timeNode = dataLine.get("timestamp");
        return timeNode == null ? "ms node" : timeNode.toString();
    }
    
    @Override
    protected void finishUp() {
        getStoringNode(1).setProperty(INeoConstants.SECTOR_ID_TYPE, SectorIdentificationType.CI.toString());
        super.finishUp();
    }
}
