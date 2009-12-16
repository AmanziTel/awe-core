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

import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * NeoLoader for old version nemo file format
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
     * @param filename
     * @param display
     * @param dataset
     */
    public OldNemoVersionLoader(String filename, Display display, String dataset) {
        super(filename, display, dataset);
        possibleFieldSepRegexes = new char[] {' ', '\t', ',', ';'};
    }

    @Override
    protected void parseLine(String line) {
        try {
            if (line.startsWith("#") || line.startsWith("*")) {
                return;
            }
            if (parser == null) {
                determineFieldSepRegex(line);
            }
            List<String> parsedLine = parser.parse(line);
            if (parsedLine.size() < 8) {
                return;
            }
            String longitude = getLongitude(parsedLine);
            String latitude = getLatitude(parsedLine);
            String latLon = latitude + "\t" + longitude;
            if (latLong != null && latLong.equals(latLon)) {
                createMsNode(parsedLine);
            } else {
                if (Double.parseDouble(latitude) == 0 && Double.parseDouble(longitude) == 0) {
                    return;
                }
                latLong = latLon;
                createPointNode(parsedLine);
            }
        } catch (Exception e) {
            NeoLoaderPlugin.error(e.getLocalizedMessage());
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

    @Override
    protected String getLongitude(List<String> parsedLine) {
        return parsedLine.get(1);
    }

    @Override
    protected String getLatitude(List<String> parsedLine) {
        return parsedLine.get(2);
    }

    @Override
    protected void createMsNode(List<String> parsedLine) {
        if (pointNode == null) {
            NeoLoaderPlugin.error("Not saved: " + parsedLine);
            return;
        }
        Transaction transaction = neo.beginTx();
        try {
            String id = getEventId(parsedLine);
            String time = getEventTime(parsedLine);

            String[] parameters;
            if (parsedLine.size() <= 8) {
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
            String heightStr = parsedLine.get(3);
            if (heightStr != null) {
                Integer height = (Integer)headers.get(HEIGHT).parse(heightStr);
                ms.setProperty(HEIGHT, height);
            }
            String distanceStr = parsedLine.get(4);
            if (distanceStr != null) {
                Integer distance = (Integer)headers.get(DISTANCE).parse(distanceStr);
                ms.setProperty(DISTANCE, distance);
            }
            String gpsStatusStr = parsedLine.get(5);
            if (gpsStatusStr != null) {
                Integer gpsStatus = (Integer)headers.get(GPS_STATUS).parse(gpsStatusStr);
                ms.setProperty(GPS_STATUS, gpsStatus);
            }
            String satelitesStr = parsedLine.get(6);
            if (satelitesStr != null) {
                Integer satelites = (Integer)headers.get(SATELITES).parse(satelitesStr);
                ms.setProperty(SATELITES, satelites);
            }
            String velocityStr = parsedLine.get(7);
            if (velocityStr != null) {
                Integer velocity = (Integer)headers.get(VELOCITY).parse(velocityStr);
                ms.setProperty(VELOCITY, velocity);
            }
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

    @Override
    protected String getEventTime(List<String> parsedLine) {
        return parsedLine.get(8);
    }
}
