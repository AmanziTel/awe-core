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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.index.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.index.MultiPropertyIndex.MultiTimeIndexConverter;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.util.index.Isolation;
import org.neo4j.util.index.LuceneIndexService;

public class TEMSLoader extends DriveLoader {
    private Node point = null;
    private int first_line = 0;
    private int last_line = 0;
    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private String latlong = null;
    private String time = null;
    private long timestamp = 0L;
    private HashMap<String, float[]> signals = new HashMap<String, float[]>();
    private String event;
    private LuceneIndexService index;

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public TEMSLoader(String filename, Display display, String dataset) {
        initialize("TEMS", null, filename, display, dataset);
        index = new LuceneIndexService(neo);
        index.setIsolation(Isolation.SAME_TX);
        initializeKnownHeaders();
        addDriveIndexes();
    }

    /**
     * Constructor for loading data in test mode, with no display and NeoService passed
     * 
     * @param neo database to load data into
     * @param filename of file to load
     * @param display
     */
    public TEMSLoader(NeoService neo, String filename) {
        initialize("TEMS", neo, filename, null, null);
        index = new LuceneIndexService(neo);
        index.setIsolation(Isolation.SAME_TX);
        initializeKnownHeaders();
        addDriveIndexes();
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        addHeaderFilters(new String[] {"time", "ms", "message_type", "event", ".*latitude", ".*longitude", ".*active_set.*1",
                ".*pilot_set.*"});
        addKnownHeader("latitude", ".*latitude");
        addKnownHeader("longitude", ".*longitude");
        addMappedHeader("event", "Event Type", "event_type", new PropertyMapper() {

            @Override
            public Object mapValue(String originalValue) {
                return originalValue.replaceAll("HO Command.*", "HO Command");
            }
        });
        addMappedHeader("time", "Timestamp", "timestamp", new PropertyMapper() {

            @Override
            public Object mapValue(String time) {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.S");
                Date datetime;
                try {
                    datetime = df.parse(time);
                } catch (ParseException e) {
                    error(e.getLocalizedMessage());
                    return 0L;
                }
                return datetime.getTime();
            }
        });
        dropHeaderStats(new String[] {"time", "timestamp", "latitude", "longitude"});
    }

    private void addDriveIndexes() {
        try {
            addIndex(new MultiPropertyIndex<Long>("Index-timestamp-" + dataset, new String[] {"timestamp"},
                    new MultiTimeIndexConverter(), 10));
            addIndex(new MultiPropertyIndex<Double>("Index-location-" + dataset, new String[] {"lat", "lon"},
                    new MultiDoubleConverter(0.001), 10));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * After all lines have been parsed, this method is called. In this loader we save remaining
     * cached data, and call the super method to finalize saving of data to gis node and the
     * properties map.
     */
    protected void finishUp() {
        saveData();
        index.shutdown();
        super.finishUp();
    }

    protected void parseLine(String line) {
        // debug(line);
        String fields[] = splitLine(line);
        if (fields.length < 2)
            return;
        if (this.isOverLimit())
            return;
        Map<String, Object> lineData = makeDataMap(fields);
        // debug(line);

        this.time = lineData.get("time").toString();
        this.timestamp = (Long)lineData.get("timestamp");
        String ms = (String)lineData.get("ms");
        event = (String)lineData.get("event"); // currently only getting this as a change

        // marker
        String message_type = (String)lineData.get("message_type"); // need this to filter for only
                                                                    // relevant messages
        // message_id = lineData.get("message_id"); // parsing this is not faster
        if (!"EV-DO Pilot Sets Ver2".equals(message_type))
            return;
        this.incValidMessage();
        // return unless message_id == '27019' // not faster
        // return unless message_id.to_i == 27019 // not faster

        // TODO: Ignore lines with Event=~/Idle/ since these generally contain invalid All-RX-Power
        // TODO: Also be careful of any All-RX-Power of -63 (since it is most often invalid data)
        // TODO: If number of PN codes does not match number of EC-IO make sure to align correct
        // values to PNs

        Object latitude = lineData.get("latitude");
        Object longitude = lineData.get("longitude");
        if (time == null || latitude == null || longitude == null) {
            return;
        }
        String thisLatLong = latitude.toString() + "\t" + longitude.toString();
        if (!thisLatLong.equals(this.latlong)) {
            saveData(); // persist the current data to database
            this.latlong = thisLatLong;
        }
        this.incValidLocation();

        int channel = 0;
        int pn_code = 0;
        int ec_io = 0;
        int measurement_count = 0;
        try {
            channel = (Integer)(lineData.get("all_active_set_channel_1"));
            pn_code = (Integer)(lineData.get("all_active_set_pn_1"));
            ec_io = (Integer)(lineData.get("all_active_set_ec_io_1"));
            measurement_count = (Integer)(lineData.get("all_pilot_set_count"));
        } catch (Exception e) {
            error("Failed to parse a field on line " + lineNumber + ": " + e.getMessage());
            return;
        }
        if (measurement_count > 12) {
            error("Measurement count " + measurement_count + " > 12");
            measurement_count = 12;
        }
        boolean changed = false;
        if (!ms.equals(this.previous_ms)) {
            changed = true;
            this.previous_ms = ms;
        }
        if (!this.time.equals(this.previous_time)) {
            changed = true;
            this.previous_time = this.time;
        }
        if (pn_code != this.previous_pn_code) {
            if (this.previous_pn_code >= 0) {
                error("SERVER CHANGED");
            }
            changed = true;
            this.previous_pn_code = pn_code;
        }
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            if (this.isOverLimit())
                return;
            if (first_line == 0)
                first_line = lineNumber;
            last_line = lineNumber;
            this.incValidChanged();
            debug(time + ": server channel[" + channel + "] pn[" + pn_code + "] Ec/Io[" + ec_io + "]\t" + event + "\t"
                    + this.latlong);
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = (Integer)(lineData.get("all_pilot_set_ec_io_" + i));
                    channel = (Integer)(lineData.get("all_pilot_set_channel_" + i));
                    pn_code = (Integer)(lineData.get("all_pilot_set_pn_" + i));
                    debug("\tchannel[" + channel + "] pn[" + pn_code + "] Ec/Io[" + ec_io + "]");
                    addStats(pn_code, ec_io);
                    String chan_code = "" + channel + "\t" + pn_code;
                    if (!signals.containsKey(chan_code))
                        signals.put(chan_code, new float[2]);
                    signals.get(chan_code)[0] += LoaderUtils.dbm2mw(ec_io);
                    signals.get(chan_code)[1] += 1;
                } catch (Exception e) {
                    error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage());
                    // e.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * This method is called to dump the current cache of signals as one located point linked to a
     * number of signal strength measurements.
     */
    private void saveData() {
        if (signals.size() > 0) {
            Transaction transaction = neo.beginTx();
            try {
                Node mp = neo.createNode();
                mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.MP_TYPE_NAME);
                mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, this.time);
                mp.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, this.timestamp);
                mp.setProperty(INeoConstants.PROPERTY_FIRST_LINE_NAME, first_line);
                mp.setProperty(INeoConstants.PROPERTY_LAST_LINE_NAME, last_line);
                String[] ll = latlong.split("\\t");
                double lat = Double.parseDouble(ll[0]);
                mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
                double lon = Double.parseDouble(ll[1]);
                mp.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);
                findOrCreateFileNode(mp);
                updateBBox(lat, lon);
                checkCRS((float)lat, (float)lon, null);
                debug("Added measurement point: " + propertiesString(mp));
                if (point != null) {
                    point.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
                }
                index(mp);
                if (event!=null){
                    index.index(mp, "events", "events");
                }
                point = mp;
                Node prev_ms = null;
                TreeMap<Float, String> sorted_signals = new TreeMap<Float, String>();
                for (String chanCode : signals.keySet()) {
                    float[] signal = signals.get(chanCode);
                    sorted_signals.put(signal[1] / signal[0], chanCode);
                }
                for (Map.Entry<Float, String> entry : sorted_signals.entrySet()) {
                    String chanCode = entry.getValue();
                    float[] signal = signals.get(chanCode);
                    double mw = signal[0] / signal[1];
                    Node ms = neo.createNode();
                    String[] cc = chanCode.split("\\t");
                    ms.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
                    ms.setProperty(INeoConstants.PRPOPERTY_CHANNEL_NAME, Integer.parseInt(cc[0]));
                    ms.setProperty(INeoConstants.PROPERTY_CODE_NAME, Integer.parseInt(cc[1]));
                    if (event != null) {
                        ms.setProperty(INeoConstants.PROPERTY_TYPE_EVENT, event);
                        event = null;
                    }
                    float dbm = LoaderUtils.mw2dbm(mw);
                    ms.setProperty(INeoConstants.PROPERTY_DBM_NAME, dbm);
                    ms.setProperty(INeoConstants.PROPERTY_MW_NAME, mw);
                    debug("\tAdded measurement: " + propertiesString(ms));
                    point.createRelationshipTo(ms, MeasurementRelationshipTypes.CHILD);
                    if (prev_ms != null) {
                        prev_ms.createRelationshipTo(ms, MeasurementRelationshipTypes.NEXT);
                    } else {
                        mp.setProperty("name", Integer.toString((int)Math.rint(dbm)));
                    }
                    prev_ms = ms;
                }
                findOrCreateSectorDriveNode(point);
                incSaved();
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        signals.clear();
        first_line = 0;
        last_line = 0;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1)
            args = new String[] {"amanzi/test.FMT", "amanzi/0904_90.FMT", "amanzi/0905_22.FMT", "amanzi/0908_44.FMT"};
        EmbeddedNeo neo = new EmbeddedNeo("../../testing/neo");
        try {
            for (String filename : args) {
                TEMSLoader driveLoader = new TEMSLoader(neo, filename);
                driveLoader.setLimit(100);
                driveLoader.run(null);
                driveLoader.printStats(true); // stats for this load
            }
            printTimesStats(); // stats for all loads
        } catch (IOException e) {
            System.err.println("Error loading TEMS data: " + e);
            e.printStackTrace(System.err);
        } finally {
            neo.shutdown();
        }
    }
}
