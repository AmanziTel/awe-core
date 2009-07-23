package org.amanzi.neo.core;

/**
 * Constans for AWE specific Neo-Database properties
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class INeoConstants {
    
    /*
     * Name of 'Name' property of Node 
     */
    public static final String PROPERTY_NAME_NAME = "name";
    
    /*
     * Name of 'Type' property of Node
     */
    public static final String PROPERTY_TYPE_NAME = "type";
    
    /*
     * Name of 'Bbox' property of Node
     */
    public static final String PROPERTY_BBOX_NAME = "bbox";
    
    /*
     * Name of 'crs' property of Node
     */
    public static final String PROPERTY_CRS_NAME = "crs";
    
    /*
     * Name of 'crs_type' property of Node
     */
    public static final String PROPERTY_CRS_TYPE_NAME = "crs_type";
    
    /*
     * Name of 'filename' property of Node
     */
    public static final String PROPERTY_FILENAME_NAME = "filename";
    
    /*
     * Name of 'long' property of Node
     */
    public static final String PROPERTY_LONG_NAME = "long";
    
    /*
     * Name of 'lon' property of Node
     */
    public static final String PROPERTY_LON_NAME = "lon";
    
    /*
     * Name of 'lat' property of Node
     */
    public static final String PROPERTY_LAT_NAME = "lat";
    
    /*
     * Name of 'mw' property of Node
     */
    public static final String PROPERTY_MW_NAME = "mw";
    
    /*
     * Name of 'dbm' property of Node
     */
    public static final String PROPERTY_DBM_NAME = "dbm";
    
    /*
     * Name of 'code' property of Node
     */
    public static final String PROPERTY_CODE_NAME = "code";
    
    /*
     * Name of 'channel' property of Node
     */
    public static final String PRPOPERTY_CHANNEL_NAME = "channel";
    
    /*
     * Name of 'last_line' property of Node
     */
    public static final String PROPERTY_LAST_LINE_NAME = "last_line";
    
    /*
     * Name of 'first_line' property of Node
     */
    public static final String PROPERTY_FIRST_LINE_NAME = "first_line";
    
    /*
     * Name of 'time' property of Node
     */
    public static final String PROPERTY_TIME_NAME = "time";
    
    /*
     * Name of 'crs_href' property of Node
     */
    public static final String PROPERTY_CRS_HREF_NAME = "crs_href";
    
    /*
     * Name of 'y' property of Node
     */
    public static final String PROPERTY_Y_NAME = "y";
    
    /*
     * Name of 'x' property of Node
     */
    public static final String PROPERTY_X_NAME = "x";
    
    /*
     * Name of 'coords' property of Node
     */
    public static final String PROPERTY_COORDS_NAME = "coords";
    
    /*
     * Name of 'description' property of Node
     */
    public static final String PROPERTY_DESCRIPTION_NAME = "description";
    
    /*
     * Status header
     */
    public static final String NETWORK_HEADER_STATUS_NAME = "status";   
    
    /*
     * All pilot set header
     */
    public static final String HEADER_PREFIX_ALL_PILOT_SET_PN = "all_pilot_set_pn_";
    
    /*
     * All pilot set channel header prefix
     */
    public static final String HEADER_PREFIX_ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    
    /*
     * All pilot set ec io header prefix
     */
    public static final String HEADER_PREFIX_ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    
    /*
     * All pilot set count header
     */
    public static final String HEADER_ALL_PILOT_SET_COUNT = "all_pilot_set_count";
    
    /*
     * All active set ec io header
     */
    public static final String HEADER_ALL_ACTIVE_SET_EC_IO_1 = "all_active_set_ec_io_1";
    
    /*
     * All active set pn header
     */
    public static final String HEADER_ALL_ACTIVE_SET_PN_1 = "all_active_set_pn_1";
    
    /*
     * All active set channel header
     */
    public static final String HEADER_ALL_ACTIVE_SET_CHANNEL_1 = "all_active_set_channel_1";
    
    /*
     * All longited header
     */
    public static final String HEADER_ALL_LONGITUDE = "all_longitude";
    
    /*
     * All latitued header
     */
    public static final String HEADER_ALL_LATITUDE = "all_latitude";
    
    /*
     * Message Hedaer 
     */
    public static final String HEADER_MESSAGE_TYPE = "message_type";
    
    /*
     * Event header
     */
    public static final String HEADER_EVENT = "event";
    
    /*
     * MS header
     */
    public static final String HEADER_MS = "ms";
    
    /*
     * EPSG 3021 for CRS
     */
    public static final String CRS_EPSG_3021 = "EPSG:3021";
    
    /*
     * EPSG 4326 for CRS
     */
    public static final String CRS_EPSG_4326 = "EPSG:4326";
    
    /*
     * CRS type Projects
     */
    public static final String CRS_TYPE_PROJECTED = "projected";
    
    /*
     * CRS type Geographic
     */
    public static final String CRS_TYPE_GEOGRAPHIC = "geographic";
    
    /*
     * Prefix of GIS catalog
     */
    public static final String GIS_PREFIX = "gis: ";
    
    /*
     * Type GIS
     */
    public static final String GIS_TYPE_NAME = "gis";

    /*
     * Type File
     */
    public static final String FILE_TYPE_NAME = "file";
    
    /*
     * Type Map
     */
    public static final String MP_TYPE_NAME = "mp";

    
    /*
     * Names of supported files for Network
     */
    public static final String[] NETWORK_FILE_NAMES = {
        "Comma Separated Values Files (*.csv)",
        "OpenOffice.org Spreadsheet Files (*.sxc)",
        "Microsoft Excel Spreadsheet Files (*.xls)",
        "All Files (*.*)" };
    
    /*
     * Extensions of supported files for Network
     */
    public static final String[] NETWORK_FILE_EXTENSIONS = {"*.csv", "*.sxc", "*.xls", "*.*"};
    
    /*
     * Names of supported files for TEMS data
     */
    public static final String[] TEMS_FILE_NAMES = {
         "(*.FMT)"
    };
    
    /*
     * Extensions of supported files for TEMS data
     */
    public static final String[] TEMS_FILE_EXTENSIONS = {"*.FMT"};
    
    /*
     * ED-VO Message Type
     */    
    public static final String MESSAGE_TYPE_EV_DO = "EV-DO Pilot Sets Ver2";    
   
}
