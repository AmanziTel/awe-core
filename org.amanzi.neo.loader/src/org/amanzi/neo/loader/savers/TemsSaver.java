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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.dialogs.DateTimeDialogWithToggle;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class TemsSaver extends AbstractHeaderSaver<HeaderTransferData> implements IStructuredSaver<HeaderTransferData>{

    protected boolean isFirstLine;
    protected boolean newElem;
    protected Calendar workDate;
    protected boolean applyToAll;
    protected Double currentLatitude;
    protected Double currentLongitude;
    @Override
    public void save(HeaderTransferData element) {
        if (isFirstLine) {
            startMainTx();
            initializeIndexes();
            isFirstLine = false;
        }
        if (newElem){
            //redefine property header for each element(file)
            propertyMap.clear();
            definePropertyMap(element);
            newElem=false;
        }
        String time=getStringValue("time", element);
        Long timestamp=defineTimestamp(workDate,time);
        String message_type = getStringValue("message_type",element); 
        Double latitude = getLatitude(getStringValue("latitude", element));
        Double longitude = getLongitude(getStringValue("longitude", element));
        if (time == null || latitude == null || longitude == null) {
            info(String.format("Line %s not saved.",element.getLine()));
            return;
        }
        if (currentLatitude == null ||currentLongitude == null || Math.abs(currentLatitude - latitude) > 10E-10 || Math.abs(currentLongitude
                        - longitude) > 10E-10) {
          currentLatitude = latitude;
          currentLongitude = longitude;  
        }
//            currentLatitude = latitude;
//            currentLongitude = longitude;
//            saveData(); // persist the current data to database
//        }
//        if (!lineData.isEmpty()) {
//            data.add(lineData);
//        }
//        this.incValidLocation();
//
//        if (!"EV-DO Pilot Sets Ver2".equals(message_type))
//            return;
//        int channel = 0;
//        int pn_code = 0;
//        int ec_io = 0;
//        int measurement_count = 0;
//        try {
//            channel = (Integer)(lineData.get("all_active_set_channel_1"));
//            pn_code = (Integer)(lineData.get("all_active_set_pn_1"));
//            ec_io = (Integer)(lineData.get("all_active_set_ec_io_1"));
//            measurement_count = (Integer)(lineData.get("all_pilot_set_count"));
//        } catch (Exception e) {
//            error("Failed to parse a field on line " + lineNumber + ": " + e.getMessage());
//            return;
//        }
//        if (measurement_count > 12) {
//            error("Measurement count " + measurement_count + " > 12");
//            measurement_count = 12;
//        }
//        boolean changed = false;
//        if (!ms.equals(this.previous_ms)) {
//            changed = true;
//            this.previous_ms = ms;
//        }
//        if (!this.time.equals(this.previous_time)) {
//            changed = true;
//            this.previous_time = this.time;
//        }
//        if (pn_code != this.previous_pn_code) {
//            if (this.previous_pn_code >= 0) {
//                error("SERVER CHANGED");
//            }
//            changed = true;
//            this.previous_pn_code = pn_code;
//        }
//        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
//            if (this.isOverLimit())
//                return;
//            if (first_line == 0)
//                first_line = lineNumber;
//            last_line = lineNumber;
//            this.incValidChanged();
//            debug(time + ": server channel[" + channel + "] pn[" + pn_code + "] Ec/Io[" + ec_io + "]\t" + event + "\t" + this.currentLatitude + "\t"
//                    + this.currentLongitude);
//            for (int i = 1; i <= measurement_count; i++) {
//                // Delete invalid data, as you can have empty ec_io
//                // zero ec_io is correct, but empty ec_io is not
//                try {
//                    ec_io = (Integer)(lineData.get("all_pilot_set_ec_io_" + i));
//                    channel = (Integer)(lineData.get("all_pilot_set_channel_" + i));
//                    pn_code = (Integer)(lineData.get("all_pilot_set_pn_" + i));
//                    debug("\tchannel[" + channel + "] pn[" + pn_code + "] Ec/Io[" + ec_io + "]");
//                    addStats(pn_code, ec_io);
//                    String chan_code = "" + channel + "\t" + pn_code;
//                    if (!signals.containsKey(chan_code))
//                        signals.put(chan_code, new float[2]);
//                    signals.get(chan_code)[0] += LoaderUtils.dbm2mw(ec_io);
//                    signals.get(chan_code)[1] += 1;
//                } catch (Exception e) {
//                    error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage());
//                }
//            }
//        }
    }

    /**
     *
     * @param stringValue
     * @return
     */
    protected Double getLongitude(String stringValue) {
        return null;
    }

    /**
     *
     * @param stringValue
     * @return
     */
    protected Double getLatitude(String stringValue) {
        return null;
    }

    /**
     *
     * @param workDate2
     * @param time
     * @return
     */
    private Long defineTimestamp(Calendar workDate2, String time) {
        return null;
    }

    private static final String MS_KEY = "ms";
    private void addDriveIndexes() {
        try {
            String virtualDatasetName = DriveTypes.MS.getFullDatasetName(rootname);
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(INeoConstants.HEADER_MS, service.getTimeIndexProperty(virtualDatasetName));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
            addMappedIndex(MS_KEY, NodeTypes.MP.getId(), service.getLocationIndexProperty(virtualDatasetName));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    /**
     *
     * @param element
     */
    private void definePropertyMap(HeaderTransferData element) {
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
        defineHeader(headers, "ms",new String[]{"MS","ms"});
        defineHeader(headers, "message_type",new String[]{"message_type","Message Type"});
        defineHeader(headers, "event",new String[]{"Event Type","event_type"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES,new String[]{".*Cell Id.*"});
        defineHeader(headers, "time",new String[]{"Timestamp","timestamp"});
        defineHeader(headers, "all_rxlev_full",new String[]{"All-RxLev Full","all_rxlev_full"});
        defineHeader(headers, "all_rxlev_sub",new String[]{"All-RxLev Sub", "all_rxlev_sub"});
        defineHeader(headers, "all_rxqual_full",new String[]{"All-RxQual Full", "all_rxqual_full"});
        defineHeader(headers, "all_rxqual_sub",new String[]{"All-RxQual Sub", "all_rxqual_sub"});
        defineHeader(headers, "all_sqi",new String[]{"All-SQI", "all_sqi"});
        defineHeader(headers, "all_sqi_mos",new String[]{"All-SQI MOS", "all_sqi_mos"});
        addAnalysedNodeTypes(element.getRootName(), ALL_NODE_TYPES);

    }
    @Override
    public void init(HeaderTransferData element) {
        super.init(element);
        isFirstLine=true;
        addDriveIndexes();
        newElem=true;
        workDate=null;
        applyToAll=false;
    }
    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    protected String[] getPossibleHeaders(String key) {
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }
    @Override
    protected void fillRootNode(Node rootNode, HeaderTransferData element) {
        DriveTypes.TEMS.setTypeToNode(rootNode, getService());
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }
    @Override
    public boolean beforeSaveNewElement(HeaderTransferData element) {
        newElem=true;
        //TODO define new latitude
        currentLatitude=null;
        currentLatitude=null; 
        workDate=getWorkDate(element);

        return workDate==null;
    }
    /**
     *
     * @param element
     * @return
     */
    private Calendar getWorkDate(final HeaderTransferData element) {
        if (applyToAll){
            return workDate;
        }
        CharSequence filename=element.getFileName();
        Pattern p = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2}).*");
        Matcher m = p.matcher(filename);
        Calendar calendar=Calendar.getInstance();
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
        if (correctTime){
            return calendar;
        }
        Calendar result = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Calendar>() {
            Calendar result;
            private DateTimeDialogWithToggle dialog;

            @Override
            public void run() {
                Calendar prefDate = Calendar.getInstance();
                String time=element.get("timestamp");
                long millis=time==null?System.currentTimeMillis():Long.parseLong(time);
                prefDate.setTimeInMillis(millis);
                dialog = new DateTimeDialogWithToggle(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Date of file", null, String.format(
                        "File '%s' has no date information.", element.getFileName()), "Please specify the date on which this data was collected:",
                        MessageDialogWithToggle.QUESTION, new String[] {IDialogConstants.CANCEL_LABEL, IDialogConstants.OK_LABEL}, 0,
                        "apply this date to all files in this load ", applyToAll, prefDate.get(Calendar.YEAR), prefDate.get(Calendar.MONTH), prefDate
                                .get(Calendar.DAY_OF_MONTH));
                dialog.open();
                if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
                    result = dialog.getCallendar();
                    applyToAll = dialog.getToggleState();
                } else {
                    result = null;
                }
            }

            @Override
            public Calendar getValue() {
                return result;
            }
        });
        if (result == null) {
            applyToAll=true;
        }   
        return result;
    }
    @Override
    public void finishSaveNewElement(HeaderTransferData element) {
    }

}
