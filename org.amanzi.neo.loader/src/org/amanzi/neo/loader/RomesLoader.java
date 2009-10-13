package org.amanzi.neo.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

public class RomesLoader extends DriveLoader {
    private Node point = null;
    private int first_line = 0;
    private int last_line = 0;
    private String latlong = null;
    private String time = null;
    private ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public RomesLoader(String filename, Display display, String dataset) {
        initialize("Romes", null, filename, display, dataset);
        initializeKnownHeaders();
    }

    /**
     * Constructor for loading data in test mode, with no display and NeoService passed
     * 
     * @param neo database to load data into
     * @param filename of file to load
     * @param display
     */
    public RomesLoader(NeoService neo, String filename) {
        initialize("Romes", neo, filename, null, null);
        initializeKnownHeaders();
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        addKnownHeader("time", "time.*");
        addKnownHeader("latitude", ".*latitude.*");
        addKnownHeader("longitude", ".*longitude.*");
        addMappedHeader("events", "Event Type", "event_type", new PropertyMapper(){

            @Override
            public Object mapValue(String originalValue) {
                return originalValue.replaceAll("HO Command.*", "HO Command");
            }});
        addMappedHeader("time", "Timestamp", "timestamp", new PropertyMapper(){

            @Override
            public Object mapValue(String time) {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                Date datetime;
                try {
                    datetime = df.parse(time);
                } catch (ParseException e) {
                    error(e.getLocalizedMessage());
                    return 0L;
                }
                return datetime.getTime();
            }});
    }
    protected void parseLine(String line) {
        // debug(line);
        String fields[] = splitLine(line);
        if (fields.length < 2)
            return;
        if (this.isOverLimit())
            return;
        this.incValidMessage(); // we have not filtered the message out on non-accepted content
        this.incValidChanged(); // we have not filtered the message out on lack of data change
        if (first_line == 0)
            first_line = line_number;
        last_line = line_number;
        Map<String,Object> lineData = makeDataMap(fields);
        this.time = lineData.get("time").toString();
        Object latitude = lineData.get("latitude");
        Object longitude = lineData.get("longitude");
        if(time==null || latitude==null || longitude==null){
            return;
        }
        String thisLatLong = latitude.toString() + "\t" + longitude.toString();
        if (!thisLatLong.equals(this.latlong)) {
            saveData(); // persist the current data to database
            this.latlong = thisLatLong;
        }
        this.incValidLocation();    // we have not filtered the message out on lack of location
        if(lineData.size()>0) {
            data.add(lineData);
        }
    }

    /**
     * After all lines have been parsed, this method is called. In this loader we save remaining
     * cached data, and call the super method to finalize saving of data to gis node and the
     * properties map.
     */
    protected void finishUp() {
        saveData();
        super.finishUp();
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
                mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.MP_TYPE_NAME);
                mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, this.time);
                mp.setProperty(INeoConstants.PROPERTY_FIRST_LINE_NAME, first_line);
                mp.setProperty(INeoConstants.PROPERTY_LAST_LINE_NAME, last_line);
                String[] ll = latlong.split("\\t");
                double lat = Double.parseDouble(ll[0]);
                mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
                double lon = Double.parseDouble(ll[1]);
                mp.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);
                findOrCreateFileNode(mp);
                updateBBox(lat, lon);
                checkCRS(ll);
                //debug("Added measurement point: " + propertiesString(mp));
                if (point != null) {
                    point.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
                }
                point = mp;
                Node prev_ms = null;
                for (Map<String, Object> dataLine : data) {
                    Node ms = neo.createNode();
                    ms.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
                    for(Map.Entry<String, Object> entry: dataLine.entrySet()) {
                        ms.setProperty(entry.getKey(), entry.getValue());
                    }
                    //debug("\tAdded measurement: " + propertiesString(ms));
                    point.createRelationshipTo(ms, MeasurementRelationshipTypes.CHILD);
                    if (prev_ms != null) {
                        prev_ms.createRelationshipTo(ms, MeasurementRelationshipTypes.NEXT);
                    }
                    prev_ms = ms;
                }
                incSaved();
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        data.clear();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        NeoLoaderPlugin.debug = false;
        if (args.length < 1)
            args = new String[] {"amanzi/test.ASC"};
        EmbeddedNeo neo = new EmbeddedNeo("var/neo");
        try {
            for (String filename : args) {
                RomesLoader driveLoader = new RomesLoader(neo, filename);
                driveLoader.setLimit(5000);
                driveLoader.run(null);
                driveLoader.printStats(true); // stats for this load
            }
            printTimesStats(); // stats for all loads
        } catch (IOException e) {
            System.err.println("Error loading Romes data: " + e);
            e.printStackTrace(System.err);
        } finally {
            neo.shutdown();
        }
    }
}
