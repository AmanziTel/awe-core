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

package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class CompleteGpsData implements IXmlTag {
    public static final Pattern COORD_PAT_LAT = Pattern.compile("^(\\d{2})(\\d{2}\\.\\d{4,5})$");
    public static final Pattern COORD_PAT_LON = Pattern.compile("^(\\d{3})(\\d{2}\\.\\d{4,5})$");
    private String probeId;
    private Calendar deliveryTime;
    private String location;

    /**
     * @return Returns the location.
     */
    public String getLocation() {
        return location;
    }

    /** The command id. */
    private String commandId;

    /** The lat. */
    private double lat;

    /** The lon. */
    private double lon;
    /** The valid. */
    private boolean valid;

    /**
     * @return Returns the probeId.
     */
    public String getProbeId() {
        return probeId;
    }

    private String checkLatLon(Double value, String parametereName) {
        String result = "";
        if (parametereName.equals("lat")&&value.toString().lastIndexOf('.') < 4) {
            for (int i = value.toString().lastIndexOf('.'); i < 4; i++) {
                result += "0";
            }
        } else if (parametereName.equals("lon")&&value.toString().lastIndexOf('.') < 5) {
            for (int i = value.toString().lastIndexOf('.'); i <= 5; i++) {
                result += "0";
            }
        }
        result+=value;
        return result;
    }

    public void setLocation(String type, Double lat, Double lon, Calendar calendar, Double minNorth, Double minWest, Double speed,
            double courseMade, Calendar dateFix, Double magnaticVariation, int checkSum) {
        String str = "";
        String latStr = checkLatLon(lat,"lat");
        String lonStr = checkLatLon(lon,"lon");
       
        if (type.equals("GPGLL")) {
            str += "$" + type + "," + latStr + ",N," + lonStr + ",W," + calendar.HOUR + calendar.MINUTE + calendar.SECOND + ",A";
        } else {
            str += "$" + type + calendar.HOUR + calendar.MINUTE + calendar.SECOND + "A," + lat + minNorth + ",N," + lon + minWest
                    + ",W," + speed + "," + courseMade + "," + dateFix.HOUR + dateFix.MINUTE + dateFix.SECOND + ","
                    + magnaticVariation + ",E*" + checkSum;
        }
        location = str;

    }

    /**
     * @param probeId The probeId to set.
     */
    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    /**
     * @return Returns the deliveryTime.
     */
    public Calendar getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * @param deliveryTime The deliveryTime to set.
     */
    public void setDeliveryTime(Calendar deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public String getType() {
        return ChildTypes.COMPLEATE_GPS_DATA.getId();
    }

    private Long getTime(String stringData) {
        try {
            if (stringData == null) {
                return null;
            }
            int i = stringData.lastIndexOf(':');
            StringBuilder time = new StringBuilder(stringData.substring(0, i)).append(stringData.substring(i + 1,
                    stringData.length()));
            long time2;

            time2 = SDF.parse(time.toString()).getTime();
            return time2;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public String getTimeiInXMLformat(Calendar calendar) {
        try {
            Date calendarDate = calendar.getTime();
            String calendarString = AbstractTOCTTC.SDF.format(calendarDate);
            int i = calendarString.lastIndexOf('+');
            StringBuilder time = new StringBuilder(calendarString.substring(0, i += 3)).append(":").append(
                    calendarString.substring(i, calendarString.length()));
            // long time2 = SDF.parse(time.toString()).getTime();
            return time.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equalsIgnoreCase(LoaderConstants.PROBE_ID)) {
            setProbeId(value.toString());
        } else if (tagName.equalsIgnoreCase(LoaderConstants.DELIVERY_TIME)) {
            calendar.setTimeInMillis(getTime(value.toString()));
            setDeliveryTime(calendar);
        } else {
            parse(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        return null;
    }

    public void GPSData(String gpsSentence) {
        valid = false;
        parse(gpsSentence);
    }

    private void parse(String gpsSentence) {
        valid = false;
        // StringTokenizer st = new StringTokenizer(gpsSentence, ",");
        StringTokenizer st2 = new StringTokenizer(gpsSentence, ",");
        // System.out.println("tocent2 "+st2.nextToken());
        // System.out.println("tocent2 "+st2.nextToken());
        // System.out.println("tocent2 "+st2.nextToken());
        if (st2.hasMoreTokens()) {
            commandId = st2.nextToken();
            // NOW parse only GPGLL and GPRMC commands
            // <gpsSentence>$GPGLL,5230.2470,N,01319.8800,E,113952,A</gpsSentence>
            // <gpsSentence>$GPRMC,113952,A,5230.2470,N,01319.8800,E,13.138,5.1,100610,2.2,E*4E</gpsSentence>
            boolean gpgll = commandId.equalsIgnoreCase("$GPGLL");
            boolean gprmc = commandId.equalsIgnoreCase("$GPRMC");
            if (!gpgll && !gprmc) {
                return;
            }
            try {
                String validate = null;
                if (gprmc) {
                    st2.nextToken();
                    validate = st2.nextToken();
                }
                String latStr = st2.nextToken();
                String latNS = st2.nextToken();
                String lonStr = st2.nextToken();
                String lonNS = st2.nextToken();
                if (gpgll) {
                    st2.nextToken();// not parsed value
                    validate = st2.nextToken();
                }
                if (validate.equalsIgnoreCase("A")) {

                    valid = true;
                    lat = parseLat(latStr);
                    if (latNS.equalsIgnoreCase("S")) {
                        lat = -lat;
                    }
                    lon = parseLon(lonStr);
                    if (lonNS.equalsIgnoreCase("W")) {
                        lon = -lon;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                valid = false;

            }
        }
    }

    /**
     * Parse latitude.
     * 
     * @param latStr String
     * @return Double
     */
    private Double parseLat(String latStr) {
        final Matcher matcher = COORD_PAT_LAT.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {
            valid = false;
            return null;
        }
    }

    /**
     * Parse longitude.
     * 
     * @param latStr String
     * @return Double
     */
    private Double parseLon(String latStr) {
        final Matcher matcher = COORD_PAT_LON.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {
            valid = false;
            return null;
        }
    }

    /**
     * @return Returns the lat.
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat The lat to set.
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return Returns the lon.
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon The lon to set.
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
