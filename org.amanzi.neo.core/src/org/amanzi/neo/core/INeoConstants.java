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
package org.amanzi.neo.core;

/**
 * Constans for AWE specific Neo-Database properties
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class INeoConstants {
    
    /***********************************************************
     * Properties used by the org.amanzi.neo.loader clases
     * and the data structures in neo4j that are built by them
     * and used by various elements in other plugins.
     */

    public static final String PROPERTY_NAME_NAME = "name";
    public static final String PROPERTY_GIS_TYPE_NAME = "gis_type";
    public static final String PROPERTY_TYPE_NAME = "type";
    public static final String PROPERTY_LON_NAME = "lon";
    public static final String PROPERTY_LAT_NAME = "lat";
    public static final String FILE_TYPE_NAME = "file";
    public static final String DIRECTORY_TYPE_NAME = "directory";
    public static final String DATASET_TYPE_NAME = "dataset";
    public static final String PROPERTY_FILENAME_NAME = "filename";
    public static final String MP_TYPE_NAME = "mp";
    public static final String PROPERTY_MW_NAME = "mw";
    public static final String PROPERTY_DBM_NAME = "dbm";
    public static final String PROPERTY_CODE_NAME = "code";
    public static final String PRPOPERTY_CHANNEL_NAME = "channel";
    public static final String PROPERTY_LAST_LINE_NAME = "last_line";
    public static final String PROPERTY_FIRST_LINE_NAME = "first_line";
    public static final String PROPERTY_TIME_NAME = "time";
    public static final String PROPERTY_TIMESTAMP_NAME = "timestamp";
    public static final String PROPERTY_PARAMS_NAME = "event_parameters";
    public static final String HEADER_M = "m";
    public static final String HEADER_MS = "ms";
    public static final String GIS_TYPE_NAME = "gis";
    public static final String PROPERTY_BBOX_NAME = "bbox";
    public static final String PROPERTY_CRS_NAME = "crs";
    public static final String PROPERTY_CRS_HREF_NAME = "crs_href";
    public static final String PROPERTY_CRS_TYPE_NAME = "crs_type";
    public static final String PROPERTY_PROJECT_NAME = "project";
    public static final String PROPERTY_DESCRIPTION_NAME = "description";
    public static final String PROPERTY_DATA = "data_properties";
    public static final String PROPERTY_STATS = "stats_properties";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_CONTEXT_ID = "context_id";
    public static final String CALL_TYPE_NAME = "call";
    public static final String PROBE_TYPE_NAME = "probe";
    public static final String CALLS_TYPE_NAME = "calls";
    public static final String LAST_CALL_NODE_ID_PROPERTY_NAME = "last_call_node_id";    
    // dataset property
    public static final String DRIVE_TYPE = "drive_type";
    /*
     * Type Splash
     */
    public static final String SPLASH_TYPE_NAME = "splash";

    
    /******************************************************************
     * Properties used in the Reuse analysis
     */

    public static final String PROPERTY_VALUE_NAME = "value";
    public static final String PROPERTY_DISTRIBUTE_NAME = "distribute";
    public static final String PROPERTY_SELECT_NAME = "select";
    public static final String AGGREGATION_TYPE_NAME = "aggregation";
    public static final String COUNT_TYPE_NAME = "count";
    /*
     * Which is the current or most recent selected aggregation for a gis node.
     * Used by the reuse analyser for saving state, and by the star analysis for
     * determining property to use.
     */
    public static final String PROPERTY_SELECTED_AGGREGATION = "selected_aggregation";

    /**
     * Name of awe_ project property
     */
    public static final String AWE_PROJECT_NODE_TYPE = "awe_project";
    /**
     * Name of min property
     */
    public static final String PROPERTY_NAME_MIN_VALUE = "min";
    /**
     * name of max property
     */
    public static final String PROPERTY_NAME_MAX_VALUE = "max";
    /**
     * name of chart error property
     */
    public static final String PROPERTY_CHART_ERROR_NAME = "error_node";
    /** INeoConstants PROPERTY_ALL_CHANNEL_NAME field */
    public static final String PROPERTY_ALL_CHANNELS_NAME = "All Channels";
    /** INeoConstants PROPERTY_OLD_NAME field */
    public static final String PROPERTY_OLD_NAME = "old_name";
    /** INeoConstants NEIGHBOUR_TYPE_NAME field */
    public static final String NEIGHBOUR_TYPE_NAME = "neighbours";
    public static final String TRANSMISSION_TYPE_NAME = "transmission";
    public static final String NEIGHBOUR_NAME = "neighbours_list";
    /** INeoConstants LIST_NUMERIC_PROPERTIES field */
    public static final String LIST_NUMERIC_PROPERTIES = "list_numeric_field";
    /** INeoConstants LIST_DATA_PROPERTIES field */
    public static final String LIST_DATA_PROPERTIES = "list_data_field";
    /** INeoConstants LIST_ALL_PROPERTIES field */
    public static final String LIST_ALL_PROPERTIES = "list_all_field";
    public static final String NEIGHBOUR_NUMBER = "# neighbours listname";
    
    /*
     * Constants for DriveLoader data structures
     */
    public static final String NODE_TYPE_PROPERTIES = "properties";
    /** INeoConstants LIST_DOUBLE_PROPERTIES field */
    public static final String LIST_DOUBLE_PROPERTIES = "list_double_field";
    /** INeoConstants LIST_INTEGER_PROPERTIES field */
    public static final String LIST_INTEGER_PROPERTIES = "list_integer_field";
    /** INeoConstants PALETTE_NAME field */
    public static final String PALETTE_NAME = "palette";
    /** INeoConstants AGGREGATION_COLOR field */
    public static final String AGGREGATION_COLOR = "column color";
    /** INeoConstants PROPERTY_AGGR_PARENT_ID field */
    public static final String PROPERTY_AGGR_PARENT_ID = "agr node id";
    /** INeoConstants COLOR_LEFT field */
    public static final String COLOR_LEFT = "color left";
    /** INeoConstants COLOR_RIGHT field */
    public static final String COLOR_RIGHT = "color right";
    public static final String COLOR_MIDDLE = "color middle";
    public static final String MIDDLE_RANGE = "MIDDLE_RANGE";
    public static final String ROOT_SECTOR_DRIVE = "root_sector_site";
    public static final String PROPERTY_TYPE_EVENT = "event_type";
    
    public static final String EVENTS_LUCENE_INDEX_NAME = "events";
    
	public static final String SECTOR_ID_PROPERTIES = "sector_id";
    public static final String DRIVE_GIS_NAME = "drive gis name";
    public static final String NETWORK_GIS_NAME = "network gis name";
    public static final String MIN_TIMESTAMP = "min timestamp";
    public static final String MAX_TIMESTAMP = "max timestamp";
    
    public static final String COMMAND_PROPERTY_NAME = "command";
    
    //call properites
    public static final String CALL_SETUP_DURATION = "setup_duration";
    public static final String CALL_TYPE = "call_type";
    public static final String CALL_DIRECTION = "call_direction";
    public static final String CALL_TERMINATION = "call_termination";
}
