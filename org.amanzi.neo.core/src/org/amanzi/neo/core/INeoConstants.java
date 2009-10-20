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
    public static final String PROPERTY_TIMESTAMP_NAME = "timestamp";
    
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
     * All longited header
     */
    public static final String HEADER_ALL_LONGITUDE = "all_longitude";
    
    /*
     * All latitued header
     */
    public static final String HEADER_ALL_LATITUDE = "all_latitude";
    
    /*
     * MS header
     */
    public static final String HEADER_MS = "ms";
    
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
     * Which is the current or most recent selected aggregation for a gis node.
     * Used by the reuse analyser for saving state, and by the star analysis for
     * determining property to use.
     */
    public static final String PROPERTY_SELECTED_AGGREGATION = "selected_aggregation";
    
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
    /*
     * Name of star Gis node
     */
    public static final String GIS_STAR_NAME = "star";
    /**
     * Name of distribute property
     */
    public static final String PROPERTY_DISTRIBUTE_NAME = "distribute";
    /**
     * Name of select property
     */
    public static final String PROPERTY_SELECT_NAME = "select";
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
    /**
     * name of "beamwidth" property
     */
    /** INeoConstants PROPERTY_ALL_CHANNEL_NAME field */
    public static final String PROPERTY_ALL_CHANNELS_NAME = "All Channels";
    /** INeoConstants PROPERTY_OLD_NAME field */
    public static final String PROPERTY_OLD_NAME = "old_name";
    /** INeoConstants NEIGHBOUR_TYPE_NAME field */
    public static final String NEIGHBOUR_TYPE_NAME = "neighbours";
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
}
