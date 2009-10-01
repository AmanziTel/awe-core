package org.amanzi.neo.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

public class TEMSLoader extends DriveLoader {
    private Node point = null;
    private int first_line = 0;
    private int last_line = 0;
    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private String latlong = null;
    private String time = null;
    private HashMap<String, float[]> signals = new HashMap<String, float[]>();

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public TEMSLoader(String filename, Display display, String dataset) {
        initialize("TEMS", null, filename, display, dataset);
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
    }

    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(filename);
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line_number++;
                if (!haveHeaders())
                    parseHeader(line);
                else
                    parseLine(line);
                if (monitor != null && monitor.isCanceled())
                    break;
            }
        } finally {
            reader.close();
            saveData();
            if (monitor != null)
                monitor.worked(WORKED_PER_FILE);
            addToMap();
        }
    }

    private void parseLine(String line) {
        // debug(line);
        String fields[] = splitLine(line);
        if (fields.length < 2)
            return;

        this.time = fields[i_of(INeoConstants.PROPERTY_TIME_NAME)];
        String ms = fields[i_of(INeoConstants.HEADER_MS)];
        String event = fields[i_of(INeoConstants.HEADER_EVENT)]; // currently only getting this
                                                                    // as a change marker
        String message_type = fields[i_of(INeoConstants.HEADER_MESSAGE_TYPE)]; // need this to
                                                                                // filter for only
                                                                                // relevant messages
        // message_id = fields[i_of("message_id")]; // parsing this is not faster
        if (!INeoConstants.MESSAGE_TYPE_EV_DO.equals(message_type))
            return;
        this.incValidMessage();
        // return unless message_id == '27019' // not faster
        // return unless message_id.to_i == 27019 // not faster

        // TODO: Ignore lines with Event=~/Idle/ since these generally contain invalid All-RX-Power
        // TODO: Also be careful of any All-RX-Power of -63 (since it is most often invalid data)
        // TODO: If number of PN codes does not match number of EC-IO make sure to align correct
        // values to PNs

        String latitude = fields[i_of(INeoConstants.HEADER_ALL_LATITUDE)];
        String longitude = fields[i_of(INeoConstants.HEADER_ALL_LONGITUDE)];
        String thisLatLong = latitude + "\t" + longitude;
        if (!thisLatLong.equals(this.latlong)) {
            saveData(); // persist the current data to database
            this.latlong = thisLatLong;
        }
        if (latitude.length() == 0 || longitude.length() == 0)
            return;
        this.incValidLocation();

        int channel = 0;
        int pn_code = 0;
        int ec_io = 0;
        int measurement_count = 0;
        try {
            channel = Integer.parseInt(fields[i_of(INeoConstants.HEADER_ALL_ACTIVE_SET_CHANNEL_1)]);
            pn_code = Integer.parseInt(fields[i_of(INeoConstants.HEADER_ALL_ACTIVE_SET_PN_1)]);
            ec_io = Integer.parseInt(fields[i_of(INeoConstants.HEADER_ALL_ACTIVE_SET_EC_IO_1)]);
            measurement_count = Integer.parseInt(fields[i_of(INeoConstants.HEADER_ALL_PILOT_SET_COUNT)]);
        } catch (NumberFormatException e) {
            error("Failed to parse a field on line " + line_number + ": " + e.getMessage());
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
        if (measurement_count > 0 && (changed || event.length() > 0)) {
            if (this.isOverLimit())
                return;
            if (first_line == 0)
                first_line = line_number;
            last_line = line_number;
            this.incValidChanged();
            debug(time + ": server channel[" + channel + "] pn[" + pn_code + "] Ec/Io[" + ec_io + "]\t" + event + "\t"
                    + this.latlong);
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = Integer.parseInt(fields[i_of(INeoConstants.HEADER_PREFIX_ALL_PILOT_SET_EC_IO + i)]);
                    channel = Integer.parseInt(fields[i_of(INeoConstants.HEADER_PREFIX_ALL_PILOT_SET_CHANNEL + i)]);
                    pn_code = Integer.parseInt(fields[i_of(INeoConstants.HEADER_PREFIX_ALL_PILOT_SET_PN + i)]);
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
                debug("Added measurement point: " + propertiesString(mp));
                if (point != null) {
                    point.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
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
                    ms.setProperty(INeoConstants.PROPERTY_DBM_NAME, LoaderUtils.mw2dbm(mw));
                    ms.setProperty(INeoConstants.PROPERTY_MW_NAME, mw);
                    debug("\tAdded measurement: " + propertiesString(ms));
                    point.createRelationshipTo(ms, MeasurementRelationshipTypes.CHILD);
                    if (prev_ms != null) {
                        prev_ms.createRelationshipTo(ms, MeasurementRelationshipTypes.NEXT);
                    }
                    prev_ms = ms;
                }
                savedData++;
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
        EmbeddedNeo neo = new EmbeddedNeo("var/neo");
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
