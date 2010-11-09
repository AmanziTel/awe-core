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

package org.amanzi.neo.loader.core.saver.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Romes saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RomesSaver extends DriveSaver<BaseTransferData> {
    protected Double currentLatitude;
    protected Double currentLongitude;
    private Node lastMLocation;
    private Integer hours;

    @Override
    public void finishSaveNewElement(BaseTransferData element) {
    }

    @Override
    protected Calendar getWorkDate(BaseTransferData element) {
        CharSequence filename = element.getFileName();
        Pattern p = Pattern.compile(".*_(\\d{6})_.*");
        Matcher m = p.matcher(filename);
        Date date = new Date();
        boolean correctTime = false;
        if (m.matches()) {
            String dateText = m.group(1);
            try {
                date = (new SimpleDateFormat("yyMMdd")).parse(dateText);
                correctTime = true;
            } catch (ParseException e) {
                error("Wrong filename format: " + filename);
            }
        }
        if (correctTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        Calendar result = preferredDate;
        return result;

    }

    @Override
    public boolean beforeSaveNewElement(BaseTransferData element) {
        hours = null;
        return super.beforeSaveNewElement(element);
    }

    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "time", new String[] {"time.*"});
        defineHeader(headers, "latitude", new String[] {".*latitude.*"});
        defineHeader(headers, "longitude", new String[] {".*longitude.*"});
        defineHeader(headers, "events", new String[] {"Event Type", "event_type"});
        defineHeader(headers, "time", new String[] {"time", "Timestamp", "timestamp"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES, new String[] {".*Server.*Report.*CI.*"});

    }

    @Override
    protected void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.MP.getId();
    }

    @Override
    public void save(BaseTransferData element) {
        super.save(element);
        String time = getStringValue("time", element);
        Long timestamp = defineTimestamp(workDate, time);
        Double latitude = getLatitude(getStringValue("latitude", element));
        Double longitude = getLongitude(getStringValue("longitude", element));
        if (time == null || latitude == null || longitude == null || timestamp == null) {
            info(String.format("Line %s not saved.", element.getLine()));
            return;
        }
        lastMNode = service.createMNode(parent, lastMNode);
        updateTx(1, 1);
        statistic.updateTypeCount(rootname, NodeTypes.M.getId(), 1);
        String mtypeId = NodeTypes.M.getId();
        setProperty(rootname, mtypeId, lastMNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
        setProperty(rootname, mtypeId, lastMNode, "time", time);
        String event = getStringValue("event", element);
        setProperty(rootname, mtypeId, lastMNode, "event", event);
        setProperty(rootname, mtypeId, lastMNode, INeoConstants.SECTOR_ID_PROPERTIES, getStringValue(INeoConstants.SECTOR_ID_PROPERTIES, element));
        Map<String, Object> sectorData = getNotHandledData(element, rootname, NodeTypes.M.getId());
        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            setProperty(rootname, mtypeId, lastMNode, key, entry.getValue());
        }
        index(lastMNode);
        if (currentLatitude == null || currentLongitude == null || Math.abs(currentLatitude - latitude) > 10E-10 || Math.abs(currentLongitude - longitude) > 10E-10) {
            currentLatitude = latitude;
            currentLongitude = longitude;
            lastMLocation=createMpLocation(lastMLocation, element, time, timestamp, latitude, longitude);
        }
        lastMNode.createRelationshipTo(lastMLocation, GeoNeoRelationshipTypes.LOCATION);
        updateTx(0, 1);
    }

    /**
     * @param workDate
     * @param time
     * @return
     */
    private Long defineTimestamp(Calendar workDate, String time) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dfn = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        try {
            Date datetime = dfn.parse(time);
            return datetime.getTime();
        } catch (ParseException e1) {
            dfn = new SimpleDateFormat("HH:mm:ss");
            try {
                //TODO: Lagutko: refactor to not use DEPRECATED methods
                Date nodeDate = dfn.parse(time);
                final int nodeHours = nodeDate.getHours();
                if (hours != null && hours > nodeHours) {
                    // next day
                    workDate.add(Calendar.DAY_OF_MONTH, 1);
                    this.workDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                hours = nodeHours;
                workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
                workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
                workDate.set(Calendar.SECOND, nodeDate.getSeconds());
                return workDate.getTimeInMillis();

            } catch (Exception e) {
                error(String.format("Can't parse time: %s", time));

            }
        }
        return null;

    }

}
