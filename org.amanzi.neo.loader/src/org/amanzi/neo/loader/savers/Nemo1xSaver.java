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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.NemoEvents;
import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;

/**
 * <p>
 * Saver for nemo data v.1.86
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Nemo1xSaver extends Nemo2xSaver {

    private Double currentLatitude;
    private Double currentLongitude;

    @Override
    public void save(LineTransferData element) {
        try {
            String line = element.getStringLine();
            if (StringUtil.isEmpty(line)) {
                return;
            }
            if (workDate == null && line.startsWith("***")) {
                workDate = new GregorianCalendar();
                Date date;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(line.split("     ")[2]);
                } catch (Exception e) {
                    error("Wrong time format\n" + e.getLocalizedMessage());
                    date = new Date();
                }
                workDate.setTime(date);
                return;

            } else if (line.startsWith("*") || line.startsWith("#")) {
                error("Not parsed: " + line);
                return;
            }

            String[] parsedLineArr = line.split(" ");
            List<String> parcedLine = Arrays.asList(parsedLineArr);
            if (parcedLine.size() < 1) {
                error("Not parsed: " + line);
                return;
            }
            if (parcedLine.size() < 8) {
                error("Not parsed: " + line);
                return;
            }
            // parse
            String eventId = parcedLine.get(0);
            String longitude = parcedLine.get(1);
            String latitude = parcedLine.get(2);
            String time = parcedLine.get(8);
            NemoEvents event = NemoEvents.getEventById(eventId);
            List<Integer> contextId = new ArrayList<Integer>();
            ArrayList<String> parameters = new ArrayList<String>();
            for (int i = 9; i < parcedLine.size(); i++) {
                parameters.add(parcedLine.get(i));
            }
            // analyse
            Map<String, Object> parsedParameters = analyseKnownParameters(element, event, contextId, parameters);
            if (parsedParameters == null) {
                return;
            }
            long timestamp;
            try {
                timestamp = getTimeStamp(1, timeFormat.parse(time));
            } catch (ParseException e) {
                // some parameters do not have time
                // NeoLoaderPlugin.error(e.getLocalizedMessage());
                timestamp = 0;
            }
            if (StringUtils.isNotEmpty(latitude)) {
                Double lon = Double.parseDouble(longitude);
                Double lat = Double.parseDouble(latitude);
                if (lon != 0f && lat != 0f) {
                    if ((lat != null)
                            && (lon != null)
                            && (((currentLatitude == null) && (currentLongitude == null)) || ((Math.abs(currentLatitude - lat) > 10E-10) || (Math.abs(currentLongitude - lon) > 10E-10)))) {
                        currentLatitude = lat;
                        currentLongitude = lon;
                        lastMPNode = createMpLocation(lastMPNode, element, time, timestamp, lat, lon);
                    }
                }
            }
            // create M node
            createMNode(eventId, driveEvents, timestamp, parsedParameters);
            // create subnodes
            createSubNodes(eventId, subNodes, timestamp);

        } catch (Exception e) {
            e.printStackTrace();
            error("Not parsed: " + element.getLine());
        }
    }

    @Override
    protected String getVersion() {
        return "1.86";
    }

    @Override
    public boolean beforeSaveNewElement(LineTransferData element) {
        currentLatitude = null;
        currentLongitude = null;
        return super.beforeSaveNewElement(element);
    }

}
