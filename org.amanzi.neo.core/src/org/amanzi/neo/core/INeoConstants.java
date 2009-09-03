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
     * Name of 'GIS Type' property of Node
     */
    public static final String PROPERTY_GIS_TYPE_NAME = "gis_type";

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
     * Name of 'project' property of Node
     */    
    public static final String PROPERTY_PROJECT_NAME = "project";
    
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
     * Prefix of GIS catalog
     */
    public static final String GIS_PREFIX = "";
    
    /*
     * Type GIS
     */
    public static final String GIS_TYPE_NAME = "gis";

    /*
     * Type File
     */
    public static final String FILE_TYPE_NAME = "file";
    /*
     * Type Dataset
     */
    public static final String DATASET_TYPE_NAME = "dataset";
    
    /*
     * Type Map
     */
    public static final String MP_TYPE_NAME = "mp";
    
    /*
     * Type Splash
     */
    public static final String SPLASH_TYPE_NAME = "splash";

    /*
     * Type Aggregation
     */
    public static final String AGGREGATION_TYPE_NAME = "aggregation";
    /*
     * Type Aggregation
     */
    public static final String COUNT_TYPE_NAME = "count";
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

    /*
     * Name of Background Color (Blue) property
     */
    public static final String RPOPERTY_BG_COLOR_B_NAME = "bgColorB";

    /*
     * Name of Background Color (Green) property
     */
    public static final String PROPERTY_BG_COLOR_G_NAME = "bgColorG";

    /*
     * Name of Background Color (Red) property
     */
    public static final String PROPERTY_BG_COLOR_R_NAME = "bgColorR";

    /*
     * Name of Font Color (Blue) property
     */
    public static final String PROPERTY_FONT_COLOR_B_NAME = "fontColorB";

    /*
     * Name of Font Color (Green) property
     */
    public static final String PROPERTY_FONT_COLOR_G_NAME = "fontColorG";

    /*
     * Name of Font Color (Red) property
     */
    public static final String PROPERTY_FONT_COLOR_R_NAME = "fontColorR";

    /*
     * Name of Horizontal Allignment property
     */
    public static final String PROPERTY_HORIZONTAL_ALIGNMENT_NAME = "horizontalAlignment";

    /*
     * Name of Vertical Allignment property
     */
    public static final String PROPERT_VERTICAL_ALIGNMENT_NAME = "verticalAlignment";

    /*
     * Name of Font Size property
     */
    public static final String RPOPERTY_FONT_SIZE_NAME = "fontSize";

    /*
     * Name of Font Style property
     */
    public static final String PROPERTY_FONT_STYLE_NAME = "fontStyle";

    /*
     * Name of Font Name property
     */
    public static final String PROPERTY_FONT_NAME_NAME = "fontName";

    /*
     * Name of Definition property
     */
    public static final String PROPERTY_DEFINITION_NAME = "definition";

    /*
     * Name of Value property
     */
    public static final String PROPERTY_VALUE_NAME = "value";

    /*
     * Name of Id property
     */
    public static final String PROPERTY_ID_NAME = "id";
    /*
     * Name of drive Gis node
     */
	public static final String GIS_TEMS_NAME = "drive";
    /**
     * Name of distribute property
     */
    public static final String PROPERTY_DISTRIBUTE_NAME = "distribute";
    /**
     * Name of select property
     */
    public static final String PROPERTY_SELECT_NAME = "select";
    public static final String AWE_PROJECT_NODE_TYPE = "awe_project";
   
}
