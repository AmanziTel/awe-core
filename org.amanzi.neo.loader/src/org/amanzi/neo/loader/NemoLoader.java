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
import java.text.SimpleDateFormat;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.core.utils.CSVParser;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.index.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Nemo loader (new version nemo files)
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NemoLoader extends DriveLoader {
    /** String TIME field */
    protected static final String TIME = "time";
    /** String EVENT_ID field */
    protected static final String EVENT_ID = "event_id";
    /** String TIME_FORMAT field */
    protected static final String TIME_FORMAT = "HH:mm:ss.S";
    protected CSVParser parser = null;
    protected char fieldSepRegex;
    protected char[] possibleFieldSepRegexes = new char[] {'\t', ',', ';'};
    protected Node pointNode;
    protected SimpleDateFormat timeFormat;
    protected Node msNode;
    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public NemoLoader(String filename, Display display, String dataset) {
        initialize("Romes", null, filename, display, dataset);
        timeFormat = new SimpleDateFormat(TIME_FORMAT);
        pointNode = null;
        initializeKnownHeaders();
        addDriveIndexes();
    }

    /**
     *
     */
    protected void initializeKnownHeaders() {
        headers.put(EVENT_ID, new StringHeader(new Header(EVENT_ID, EVENT_ID, 0)));
        // headers.put(TIME, new Header(TIME, TIME, 1));
        // MappedHeaderRule mapRule = new MappedHeaderRule("timestamp", TIME, new
        // DateTimeMapper(TIME_FORMAT));
        // headers.put(mapRule.key, new MappedHeader(headers.get(TIME), mapRule));

    }
    @Override
    protected void parseLine(String line) {
        if (line.startsWith("#")) {
            return;
        }
        if (parser == null) {
            determineFieldSepRegex(line);
        }
        List<String> parsedLine = parser.parse(line);
        if (parsedLine.size() < 1) {
            return;
        }
        String eventId = getEventId(parsedLine);
        if ("GPS".equalsIgnoreCase(eventId)) {
            createPointNode(parsedLine);
            return;
        }
        if (pointNode != null) {
            createMsNode(parsedLine);
        }

    }


    /**
     * @param parsedLine
     */
    protected void createPointNode(List<String> parsedLine) {
        Transaction transaction = neo.beginTx();
        try {
            double lon = Double.parseDouble(getLongitude(parsedLine));
            double lat = Double.parseDouble(getLatitude(parsedLine));
            String time = getEventTime(parsedLine);
            long timestamp = timeFormat.parse(time).getTime();
            Node mp = neo.createNode();
            mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.MP_TYPE_NAME);
            mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, time);
            mp.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
            mp.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);
            findOrCreateFileNode(mp);
            updateBBox(lat, lon);
            checkCRS((float)lat, (float)lon, null);
            // debug("Added measurement point: " + propertiesString(mp));
            if (pointNode != null) {
                pointNode.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
            }
            index(mp);
            transaction.success();
            pointNode = mp;
            msNode = null;
            createMsNode(parsedLine);
        } catch (Exception e) {
            NeoLoaderPlugin.error(e.getLocalizedMessage());
            return;
        } finally {
            transaction.finish();
        }
    }

    /**
     *
     * @param parsedLine
     * @return
     */
    protected String getLatitude(List<String> parsedLine) {
        return parsedLine.get(4);
    }

    /**
     *
     * @param parsedLine
     * @return
     */
    protected String getLongitude(List<String> parsedLine) {
        return parsedLine.get(3);
    }

    /**
     * @param parsedLine
     */
    protected void createMsNode(List<String> parsedLine) {
        if (pointNode == null) {
            NeoLoaderPlugin.error("Not saved: " + parsedLine);
            return;
        }
        Transaction transaction = neo.beginTx();
        try {
            String id = getEventId(parsedLine);
            String time = getEventTime(parsedLine);
            // TODO add parsing event parameters depends on eventId if necessary
            String[] parameters;
            if (parsedLine.size() <= 2) {
                parameters = new String[0];
            } else {
                parameters = new String[parsedLine.size() - 2];
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = parsedLine.get(i + 2);
                }
            }
            Node ms = neo.createNode();
            ms.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
            ms.setProperty(EVENT_ID, id);
            ms.setProperty(INeoConstants.PROPERTY_TIME_NAME, time);
            ms.setProperty(INeoConstants.PROPERTY_PARAMS_NAME, parameters);
            pointNode.createRelationshipTo(ms, MeasurementRelationshipTypes.CHILD);
            if (msNode != null) {
                msNode.createRelationshipTo(ms, MeasurementRelationshipTypes.NEXT);
            }
            msNode = ms;
            // add to statistic
            headers.get(EVENT_ID).parse(id);
            transaction.success();
        } finally {
            transaction.finish();
        }
    }

    /**
     * Get event time
     * 
     * @param parsedLine - list of fields
     * @return String
     */
    protected String getEventTime(List<String> parsedLine) {
        return parsedLine.get(1);
    }

    /**
     * Get event id
     * 
     * @param parsedLine - list of fields
     * @return String
     */
    protected String getEventId(List<String> parsedLine) {
        return parsedLine.get(0);
    }

    /**
     * add index
     */
    private void addDriveIndexes() {
        try {
            addIndex(new MultiPropertyIndex<Double>("Index-location-" + dataset, new String[] {"lat", "lon"},
                    new MultiDoubleConverter(0.001), 10));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * determine parser
     * 
     * @param line - first event line
     */
    protected void determineFieldSepRegex(String line) {
        int maxMatch = 0;
        for (char regex : possibleFieldSepRegexes) {
            String[] fields = line.split(String.valueOf(regex));
            if (fields.length > maxMatch) {
                maxMatch = fields.length;
                fieldSepRegex = regex;
            }
        }
        parser = new CSVParser(fieldSepRegex);
    }
}
