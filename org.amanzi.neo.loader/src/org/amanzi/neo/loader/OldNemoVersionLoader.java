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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * NeoLoader for old version (1.8x) nemo file format
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class OldNemoVersionLoader extends NemoLoader {
    /** String VELOCITY field */
    private static final String VELOCITY = "velocity";
    /** String SATELITES field */
    private static final String SATELITES = "satelites";
    /** String GPS_STATUS field */
    private static final String GPS_STATUS = "gps_status";
    /** String DISTANCE field */
    private static final String DISTANCE = "distance";
    /** String HEIGHT field */
    private static final String HEIGHT = "height";
    protected String latLong = null;

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param time - file time
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public OldNemoVersionLoader(Calendar time, String filename, Display display, String dataset) {
        super(time, filename, display, dataset);
        driveType = DriveTypes.NEMO1;
        possibleFieldSepRegexes = new String[] {" ", "\t", ",", ";"};
    }
    
    /**
     * Constructor for loading data in AWE, with specified neo4j database service and dataset, but no NeoService
     * Note. Used only in test environments
     * @param time - file time
     * @param filename of file to load
     * @param dataset to add data to
     * @param neo neo4j service
     */
    public OldNemoVersionLoader(final Calendar time,final String filename,final String dataset,final GraphDatabaseService neo) {
        super(time, filename, dataset, neo );
        driveType = DriveTypes.NEMO1;
        possibleFieldSepRegexes = new String[] {" ", "\t", ",", ";"};
    }    

    @Override
    protected void parseLine(String line) {
        try {
            if (_workDate == null && line.startsWith("***")) {
                _workDate = new GregorianCalendar();
                Date date;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(line.split("     ")[2]);
                } catch (Exception e) {
                    NeoLoaderPlugin.error("Wrong time format\n" + e.getLocalizedMessage());
                    date = new Date(new File(filename).lastModified());
                }
                _workDate.setTime(date);
                return;

            } else if (line.startsWith("*") || line.startsWith("#")) {
                NeoLoaderPlugin.error("Not parsed: " + line);
                return;
            }
            
            if (parser == null) {
            	determineFieldSepRegex(line);
            }
        	
            List<String> parsedLine = splitLine(line);
            if (parsedLine.size() < 1) {
                return;
            }
            OldEvent event = new OldEvent(parsedLine);
            try {
                event.analyseKnownParameters(headers);
            } catch (Exception e) {
                e.printStackTrace();
                NeoLoaderPlugin.error(e.getLocalizedMessage());
                return;
            }

            String latLon = event.latitude + "\t" + event.longitude;
            if (Double.parseDouble(event.latitude) == 0 && Double.parseDouble(event.longitude) == 0) {
                NeoLoaderPlugin.error("Not parsed: " + line);
                return;
            }
            createMNode(event);
            if (latLong == null || !latLong.equals(latLon)) {
                latLong = latLon;
                createPointNode(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            NeoLoaderPlugin.error("Not parsed: " + line);
        }

    }

    @Override
    protected void createPointNode(Event events) {
        OldEvent event = (OldEvent)events;
        Transaction transaction = neo.beginTx();
        try {
            Float lon = Float.parseFloat(event.longitude);
            Float lat = Float.parseFloat(event.latitude);
            String time = event.time;
            if (lon == null || lat == null) {
                return;
            }
            Node mp = neo.createNode();
            mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.MP.getId());
            mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, time);
            mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat.doubleValue());
            mp.setProperty(INeoConstants.PROPERTY_LON_NAME, lon.doubleValue());
            GisProperties gisProperties = getGisProperties(dataset);
            gisProperties.updateBBox(lat, lon);
            gisProperties.checkCRS(lat, lon, null);
            // debug("Added measurement point: " + propertiesString(mp));
            index(mp);
            transaction.success();
            pointNode = mp;
        } catch (Exception e) {
            e.printStackTrace();
            NeoLoaderPlugin.error(e.getLocalizedMessage());
            return;
        } finally {
            transaction.finish();
        }
    }

    @Override
    protected void initializeKnownHeaders() {
        super.initializeKnownHeaders();
        headers.put(HEIGHT, new IntegerHeader(new Header(HEIGHT, HEIGHT, 3)));
        headers.put(DISTANCE, new IntegerHeader(new Header(DISTANCE, DISTANCE, 4)));
        headers.put(GPS_STATUS, new IntegerHeader(new Header(GPS_STATUS, GPS_STATUS, 5)));
        headers.put(SATELITES, new IntegerHeader(new Header(SATELITES, SATELITES, 6)));
        headers.put(VELOCITY, new IntegerHeader(new Header(VELOCITY, VELOCITY, 4)));

    }

    /**
     * <p>
     * Event - provide information about command (1 row from log file) for file version "1.86"
     * </p>
     * 
     * @author cinkel_a
     * @since 1.0.0
     */
    public class OldEvent extends Event {

        private String longitude;
        private String latitude;
        
        /**
         * @param parcedLine
         */
        public OldEvent(List<String> parcedLine) {
            super(parcedLine);
        }

        @Override
        protected void parse(List<String> parcedLine) {
            eventId = parcedLine.get(0);
            longitude = parcedLine.get(1);
            latitude = parcedLine.get(2);
            time = parcedLine.get(8);
            event = NemoEvents.getEventById(eventId);
            contextId = null;
            parameters = new ArrayList<String>();
            for (int i = 9; i < parcedLine.size(); i++) {
                parameters.add(parcedLine.get(i));
            }
        }

        @Override
        public void store(Node msNode, Map<String, Header> statisticHeaders) {
            storeProperties(msNode, INeoConstants.PROPERTY_TYPE_EVENT, eventId, statisticHeaders);
            storeProperties(msNode, INeoConstants.PROPERTY_TIME_NAME, time, statisticHeaders);
            // TODO store header if necessary
            for (String key : parsedParameters.keySet()) {
                storeProperties(msNode, key, parsedParameters.get(key), statisticHeaders);
            }
        }

        @Override
        protected String getVersion() {
            return "1.86";
        }
    }
}
