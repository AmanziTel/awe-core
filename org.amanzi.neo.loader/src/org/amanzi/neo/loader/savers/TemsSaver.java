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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.LoaderUtils;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * saver of Tems data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class TemsSaver extends DriveSaver<BaseTransferData> {

    protected Double currentLatitude;
    protected Double currentLongitude;
    private Node lastMLocation;

    private Node virtualParent;

    private Node lastMsNode;
    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private String virtualDatasetName;
    private Integer hours;

    @Override
    public void save(BaseTransferData element) {
        super.save(element);
        String time = getStringValue("time", element);
        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getStringValue("message_type", element);
        Double latitude = getLatitude(getStringValue("latitude", element));
        Double longitude = getLongitude(getStringValue("longitude", element));
        if (time == null || latitude == null || longitude == null || timestamp == null) {
            info(String.format("Line %s not saved.", element.getLine()));
            return;
        }
        lastMNode = service.createMNode(parent, lastMNode);
        updateTx(1, 1);
        statistic.increaseTypeCount(rootname, NodeTypes.M.getId(), 1);
        String mtypeId = NodeTypes.M.getId();
        setProperty(rootname, mtypeId, lastMNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
        setProperty(rootname, mtypeId, lastMNode, "message_type", message_type);
        setProperty(rootname, mtypeId, lastMNode, "time", time);
        String event = getStringValue("event", element);
        if (StringUtils.isNotEmpty(event)) {
            setProperty(rootname, mtypeId, lastMNode, "event", event);
        }
        setProperty(rootname, mtypeId, lastMNode, INeoConstants.SECTOR_ID_PROPERTIES, getStringValue(INeoConstants.SECTOR_ID_PROPERTIES, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_BCCH_NAME, getStringValue(INeoConstants.PROPERTY_BCCH_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_TCH_NAME, getStringValue(INeoConstants.PROPERTY_TCH_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_SC_NAME, getStringValue(INeoConstants.PROPERTY_SC_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_PN_NAME, getStringValue(INeoConstants.PROPERTY_PN_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_EcIo_NAME, getStringValue(INeoConstants.PROPERTY_EcIo_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_RSSI_NAME, getStringValue(INeoConstants.PROPERTY_RSSI_NAME, element));
        setUnparsedProperty(lastMNode, rootname, mtypeId, INeoConstants.PROPERTY_CI_NAME, getStringValue(INeoConstants.PROPERTY_CI_NAME, element));
        String ms = getStringValue("ms", element);
        setUnparsedProperty(lastMNode, rootname, mtypeId, "ms", ms);
        Map<String, Object> sectorData = getNotHandledData(element, rootname, NodeTypes.M.getId());
        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            setProperty(rootname, mtypeId, lastMNode, key, entry.getValue());
        }

        index(lastMNode);
      //TODO refactor creating MP node - union with RomesSaver
        if (currentLatitude == null || currentLongitude == null || Math.abs(currentLatitude - latitude) > 10E-10 || Math.abs(currentLongitude - longitude) > 10E-10) {
            currentLatitude = latitude;
            currentLongitude = longitude;
            lastMLocation=createMpLocation(lastMLocation,element, time, timestamp, latitude, longitude);
        }
        lastMNode.createRelationshipTo(lastMLocation, GeoNeoRelationshipTypes.LOCATION);
        updateTx(0, 1);
        if (!"EV-DO Pilot Sets Ver2".equals(message_type))
            return;
        if (virtualParent == null) {
            virtualParent = defineVirtualParent(element);
            lastMsNode = null;
        }
        int channel = 0;
        int pn_code = 0;
        int ec_io = 0;
        int measurement_count = 0;
        try {
            channel = getNumberValue(Integer.class, "all_active_set_channel_1", element);
            pn_code = getNumberValue(Integer.class, "all_active_set_pn_1", element);
            ec_io = getNumberValue(Integer.class, "all_active_set_ec_io_1", element);
            measurement_count = getNumberValue(Integer.class, "all_pilot_set_count", element);
        } catch (Exception e) {
            error("Failed to parse a field on line " + element.getLine() + ": " + e.getMessage());
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
        if (!time.equals(this.previous_time)) {
            changed = true;
            this.previous_time = time;
        }
        if (pn_code != this.previous_pn_code) {
            if (this.previous_pn_code >= 0) {
                error("SERVER CHANGED");
            }
            changed = true;
            this.previous_pn_code = pn_code;
        }
        HashMap<String, float[]> signals = new HashMap<String, float[]>();
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = getNumberValue(Integer.class, "all_pilot_set_ec_io_" + i, element);
                    channel = getNumberValue(Integer.class, "all_pilot_set_channel_" + i, element);
                    pn_code = getNumberValue(Integer.class, "all_pilot_set_pn_" + i, element);
                    String chan_code = "" + channel + "\t" + pn_code;
                    if (!signals.containsKey(chan_code))
                        signals.put(chan_code, new float[2]);
                    signals.get(chan_code)[0] += Math.pow(10.0, ((ec_io) / 10.0));
                    signals.get(chan_code)[1] += 1;
                } catch (Exception e) {
                    error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage());
                }
            }
        }
        if (!signals.isEmpty()) {
            TreeMap<Float, String> sorted_signals = new TreeMap<Float, String>();
            for (String chanCode : signals.keySet()) {
                float[] signal = signals.get(chanCode);
                sorted_signals.put(signal[1] / signal[0], chanCode);
            }
            for (Map.Entry<Float, String> entry : sorted_signals.entrySet()) {
                String chanCode = entry.getValue();
                float[] signal = signals.get(chanCode);
                double mw = signal[0] / signal[1];
                lastMsNode = service.createMsNode(virtualParent, lastMsNode);
                updateTx(1, 1);
                statistic.increaseTypeCount(virtualDatasetName, NodeTypes.HEADER_MS.getId(), 1);
                String[] cc = chanCode.split("\\t");

                lastMsNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PRPOPERTY_CHANNEL_NAME, getNumberValue(Integer.class, cc[0]));
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_CODE_NAME, getNumberValue(Integer.class, cc[1]));
                lastMsNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, cc[1]);
                float dbm = LoaderUtils.mw2dbm(mw);
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_DBM_NAME, dbm);
                lastMsNode.setProperty(INeoConstants.PROPERTY_MW_NAME, mw);
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_MW_NAME, Double.valueOf(mw).floatValue());
                setProperty(virtualDatasetName, NodeTypes.HEADER_MS.getId(), lastMsNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                index(lastMsNode);
                lastMsNode.createRelationshipTo(lastMLocation, GeoNeoRelationshipTypes.LOCATION);
                updateTx(0, 1);
            }
        }
    }



    /**
     * Define virtual parent.
     * 
     * @param element the element
     * @return the node
     */
    private Node defineVirtualParent(BaseTransferData element) {
        Node virtualDataset = service.getVirtualDataset(rootNode, DriveTypes.MS);
        virtualDatasetName = DriveTypes.MS.getFullDatasetName(rootname);
        return service.getFileNode(virtualDataset, element.getFileName());
    }

    /**
     * Define timestamp.
     * 
     * @param workDate the work date
     * @param time the time
     * @return the long
     */
    @SuppressWarnings("deprecation")
    private Long defineTimestamp(Calendar workDate, String time) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dfn = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        try {
            Date datetime = dfn.parse(time);
            return datetime.getTime();
        } catch (ParseException e1) {
            dfn = new SimpleDateFormat("HH:mm:ss.S");
            try {
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

    @Override
    protected void addDriveIndexes() {
        try {
            String virtualDatasetName = DriveTypes.MS.getFullDatasetName(rootname);
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(INeoConstants.HEADER_MS, service.getTimeIndexProperty(virtualDatasetName));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, INeoConstants.PROPERTY_BCCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
        defineHeader(headers, INeoConstants.PROPERTY_TCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_TCH));
        defineHeader(headers, INeoConstants.PROPERTY_SC_NAME, getPossibleHeaders(DataLoadPreferences.DR_SC));
        defineHeader(headers, INeoConstants.PROPERTY_PN_NAME, getPossibleHeaders(DataLoadPreferences.DR_PN));
        defineHeader(headers, INeoConstants.PROPERTY_EcIo_NAME, getPossibleHeaders(DataLoadPreferences.DR_EcIo));
        defineHeader(headers, INeoConstants.PROPERTY_RSSI_NAME, getPossibleHeaders(DataLoadPreferences.DR_RSSI));
        defineHeader(headers, INeoConstants.PROPERTY_CI_NAME, getPossibleHeaders(DataLoadPreferences.DR_CI));
        defineHeader(headers, "longitude", getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));
        defineHeader(headers, "latitude", getPossibleHeaders(DataLoadPreferences.DR_LATITUDE));
        defineHeader(headers, "ms", new String[] {"MS", "ms"});
        defineHeader(headers, "message_type", new String[] {"message_type", "Message Type"});
        defineHeader(headers, "event", new String[] {"Event Type", "event_type"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES, new String[] {".*Cell Id.*"});
        defineHeader(headers, "time", new String[] {"time", "Timestamp", "timestamp"});
        defineHeader(headers, "all_rxlev_full", new String[] {"All-RxLev Full", "all_rxlev_full"});
        defineHeader(headers, "all_rxlev_sub", new String[] {"All-RxLev Sub", "all_rxlev_sub"});
        defineHeader(headers, "all_rxqual_full", new String[] {"All-RxQual Full", "all_rxqual_full"});
        defineHeader(headers, "all_rxqual_sub", new String[] {"All-RxQual Sub", "all_rxqual_sub"});
        defineHeader(headers, "all_sqi", new String[] {"All-SQI", "all_sqi"});
        defineHeader(headers, "all_sqi_mos", new String[] {"All-SQI MOS", "all_sqi_mos"});
        for (int i = 0; i <= 12; i++) {
            defineHeader(headers, "all_pilot_set_ec_io_" + i, new String[] {"all_pilot_set_ec_io_" + i});
            defineHeader(headers, "all_pilot_set_channel_" + i, new String[] {"all_pilot_set_channel_" + i});
            defineHeader(headers, "all_pilot_set_pn_" + i, new String[] {"all_pilot_set_pn_" + i});
        }
        addAnalysedNodeTypes(element.getRootName(), ALL_NODE_TYPES);

    }



    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
        DriveTypes.TEMS.setTypeToNode(rootNode, getService());
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

    @Override
    public boolean beforeSaveNewElement(BaseTransferData element) {
        hours = null;
        currentLatitude = null;
        currentLatitude = null;
        virtualParent = null;
        return  super.beforeSaveNewElement(element);
    }

    /**
     * @param element
     * @return
     */
    @Override
    protected Calendar getWorkDate(final BaseTransferData element) {

        CharSequence filename = element.getFileName();
        Pattern p = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2}).*");
        Matcher m = p.matcher(filename);
        Calendar calendar = Calendar.getInstance();
        boolean correctTime;
        if (m.matches()) {
            String dateText = m.group(1);
            try {
                calendar.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd").parse(dateText).getTime());
                correctTime = true;
            } catch (ParseException e) {
                error("Wrong filename format: " + filename);
                correctTime = false;
            }
        } else {
            error("Wrong filename format: " + filename);
            correctTime = false;
        }
        if (correctTime) {
            return calendar;
        }
        Calendar result = askTime(element);
        if (result == null) {
            applyToAll = true;
        }
        return result;
    }

    @Override
    public void finishSaveNewElement(BaseTransferData element) {
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.MP.getId();
    }

}
