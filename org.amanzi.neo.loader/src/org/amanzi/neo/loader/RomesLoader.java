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
import java.util.ArrayList;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.index.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.index.MultiPropertyIndex.MultiTimeIndexConverter;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.util.index.Isolation;
import org.neo4j.util.index.LuceneIndexService;

public class RomesLoader extends DriveLoader {
    private Node point = null;
    private int first_line = 0;
    private int last_line = 0;
    private String latlong = null;
    private String time = null;
    private long timestamp = 0L;
    private ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public RomesLoader(String filename, Display display, String dataset) {
        initialize("Romes", null, filename, display, dataset);
        initializeLuceneIndex();
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
    public RomesLoader(NeoService neo, String filename) {
        initialize("Romes", neo, filename, null, null);
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
        addHeaderFilters(new String[] {"time.*", "events", ".*latitude.*", ".*longitude.*", ".*server_report.*",
                ".*state_machine.*", ".*layer_3_message.*", ".*handover_analyzer.*"});
        addKnownHeader("time", "time.*");
        addKnownHeader("latitude", ".*latitude.*");
        addKnownHeader("longitude", ".*longitude.*");
        addMappedHeader("events", "Event Type", "event_type", new PropertyMapper() {

            @Override
            public Object mapValue(String originalValue) {
                return originalValue.replaceAll("HO Command.*", "HO Command");
            }
        });
        addMappedHeader("time", "Timestamp", "timestamp", new DateTimeMapper("HH:mm:ss"));
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
            first_line = lineNumber;
        last_line = lineNumber;
        Map<String, Object> lineData = makeDataMap(fields);
        this.time = (String)lineData.get("time");
        this.timestamp = (Long)lineData.get("timestamp");
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
        this.incValidLocation(); // we have not filtered the message out on lack of location
        if (lineData.size() > 0) {
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
        index.shutdown();
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
                // debug("Added measurement point: " + propertiesString(mp));
                if (point != null) {
                    point.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
                }
                index(mp);
                point = mp;
                Node prev_ms = null;
                boolean haveEvents = false;
                for (Map<String, Object> dataLine : data) {
                    Node ms = neo.createNode();
                    ms.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
                    for (Map.Entry<String, Object> entry : dataLine.entrySet()) {
                        ms.setProperty(entry.getKey(), entry.getValue());
                        haveEvents = haveEvents || INeoConstants.PROPERTY_TYPE_EVENT.equals(entry.getKey());
                    }
                    // debug("\tAdded measurement: " + propertiesString(ms));
                    point.createRelationshipTo(ms, MeasurementRelationshipTypes.CHILD);
                    if (prev_ms != null) {
                        prev_ms.createRelationshipTo(ms, MeasurementRelationshipTypes.NEXT);
                    }
                    prev_ms = ms;
                }
                if (haveEvents) {
                    index.index(mp, "events", nameGis);
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
        EmbeddedNeo neo = new EmbeddedNeo("../../testing/neo");
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
