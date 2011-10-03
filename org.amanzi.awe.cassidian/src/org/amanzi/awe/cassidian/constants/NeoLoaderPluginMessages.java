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
package org.amanzi.awe.cassidian.constants;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class NeoLoaderPluginMessages extends NLS {
    
    private static final String BUNDLE_NAME = NeoLoaderPluginMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String DriveDialog_RemoveButtonText;
    public static String DriveDialog_AddButtonText;
    public static String DriveDialog_BrowseButtonText;
    public static String DriveDialog_LoadButtonText;
    public static String DriveDialog_CancelButtonText;
    public static String DriveDialog_FilesToLoadListLabel;
    public static String DriveDialog_FilesToChooseListLabel;
    public static String DriveDialog_DialogTitle;
    public static String DriveDialog_FileDialogTitle;
    public static String DriveDialog_MonitorName;
    public static String DriveDialog_FILE_NAMES_Drive;
    public static String DriveDialog_FILE_NAMES_TEMS;
    public static String DriveDialog_FILE_NAMES_Romes;
    public static String DriveDialog_FILE_NAMES_GPS;
    public static String DriveDialog_FILE_NAMES_NEMO1;
    public static String DriveDialog_FILE_NAMES_NEMO2;
    public static String DriveDialog_FILE_NAMES_All;
    public static String DriveDialog_MEMORY_USAGE;
    public static String DriveDialog_MEMORY_CHANGE;
    public static String DriveDialog_TRANSIENT_MEMORY_CHANGE;
    public static String DriveDialog_PERSISTENT_MEMORY_CHANGE;
    public static String DriveDialog_FILE_HAS_NO_DATE_TEXT3; 
    public static String DriveDialog_FILE_HAS_NO_DATE_TEXT2;
    public static String DriveDialog_FILE_HAS_NO_DATE_TITLE;
    public static String DriveDialog_FILE_HAS_NO_DATE_TEXT1;
    public static String DriveDialog_WRONG_FILENAME_FORMAT_ERROR;
    public static String DriveDialog_UNSUPPORTED_FILE_EXTENSION_ERROR;
    public static String DriveDialog_UNKNOWN_FILE_TYPE_WARN;
    public static String DriveDialog_UNKNOWN_FILE_TYPE_WARN_TITLE;
    public static String DriveDialog_IMPORTING_JOB_NAME;
    
    public static String TemsImportWizard_PAGE_TITLE;
    public static String TemsImportWizard_PAGE_DESCR;
    
    public static String Console_ErrorOnClose;
    public static String NetworkDialog_DialogTitle;
	public static String DriveDialog_AddAllButtonText;
	public static String DriveDialog_RemoveAllButtonText;
	public static String DriveDialog_DatasetLabel;
    public static String ADD_LAYER_MESSAGE;
    public static String ADD_NEW_MAP_MESSAGE;
    public static String ADD_LAYER_TITLE;
    
    public static String TOGLE_MESSAGE;
    public static String TOGLE_MESSAGE_ADD_PROBE;
    public static String TOGLE_MESSAGE_ADD_EVENTS;
    public static String TOGLE_MESSAGE_ADD_PESQ;
    public static String TOGLE_MESSAGE_ADD_CALLS;
    
    public static String CorrelateDialog_DialogTitle;
    public static String CorrelateDialog_CorrelateButtonText;
    public static String CorrelateDialog_select_probe_data;
    public static String CorrelateDialog_select_drive_data;
    public static String CorrelateDialog_correlate_job_name;
    
    public static String AMSImport_page_title;
    public static String AMSImport_page_descr;
    public static String AMSImport_dataset;
    public static String AMSImport_network;
    public static String AMSImport_directory;
    public static String AMSImport_dir_editor_title;
    public static String AMSImport_LOAD_AMS_JOB_NAME;
    
    public static String PrefNetwork_title_network;
    public static String PrefNetwork_title_sector;
    public static String PrefNetwork_title_site;
    public static String PrefNetwork_title_oprional;
    public static String PrefNetwork_field_city;
    public static String PrefNetwork_field_msc;
    public static String PrefNetwork_field_bsc;
    public static String PrefNetwork_field_site;
    public static String PrefNetwork_field_sector;
    public static String PrefNetwork_field_sector_ci;
    public static String PrefNetwork_field_sector_lac;
    public static String PrefNetwork_field_latitude;
    public static String PrefNetwork_field_longitude;
    public static String PrefNetwork_field_beamwidth;
    public static String PrefNetwork_field_azimuth;

    public static String PrefProbe_title;
    public static String PrefProbe_field_name;
    public static String PrefProbe_field_probe_type;
    public static String PrefProbe_field_latitude;
    public static String PrefProbe_field_longitude;

    public static String PrefNeighbour_title;
    public static String PrefNeighbour_title_server;
    public static String PrefNeighbour_title_neighbour;
    public static String PrefNeighbour_field_ci;
    public static String PrefNeighbour_field_bts;
    public static String PrefNeighbour_field_lac;
    public static String PrefNeighbour_field_adj_ci;
    public static String PrefNeighbour_field_adj_bts;
    public static String PrefNeighbour_field_adj_lac;
    
    public static String PrefTransmission_title;
    public static String PrefTransmission_title_server;
    public static String PrefTransmission_title_neighbour;
    public static String PrefTransmission_field_Site_ID;
    public static String PrefTransmission_field_Site_No;
    public static String PrefTransmission_field_ITEM_Name;    
    
    public static String GpehWindowTitle;
    public static String GpehTitle;
    public static String GpehDescr;
    public static String GpehLbOSS;
    public static String GpehImportDirEditorTitle;
    public static String GpehOptionsTitle;
    public static String GpehOptionsDescr;
    public static String Gpeh_MEASUREMENT_REPORTS_TREE_ELEM_NAME;
    public static String Gpeh_LOCATIONS_TREE_ELEM_NAME;
    public static String Gpeh_LOAD_OSS_JOB_NAME;

    public static String NetworkSiteImportWizard_PAGE_TITLE;
    public static String NetworkSiteImportWizard_PAGE_DESCR;
    public static String NetworkSiteImportWizard_NETWORK;
    public static String NetworkSiteImportWizard_FILE; 
    public static String NetworkSiteImportWizard_DATA_TYPE;    
    public static String NetworkSiteImportWizardPage_NETWORK_MUST_EXIST;
    public static String NetworkSiteImportWizardPage_NO_FILE;
    public static String NetworkSiteImportWizardPage_NO_NETWORK;
    public static String NetworkSiteImportWizardPage_NO_TYPE;
    public static String NetworkSiteImportWizardPage_RESTRICTED_NETWORK_NAME;
    public static String NetworkSiteImportWizardPage_WRONG_TYPE_FOR_NETWORK;    
    public static String NetworkSiteImportWizardPage_NETWORK_TYPE_LBL_TEXT;
    public static String NetworkSiteImportWizardPage_LOAD_JOB_NAME;
    
    public static String NetworkImportWizardPage_SELECT_FILE_NAME;
    public static String NetworkImportWizard_LOAD_NETWORK_JOB_NAME;
    public static String NetworkImportWizard_PAGE_TITLE;
    public static String NetworkImportWizard_PAGE_DESCR;

    public static String OSS;
    
    public static String CommonCRSPreference_button_ADD;
    public static String CommonCRSPreference_button_REMOVE;
    public static String CRSdialog_TITLE;
    public static String CRSdialog_button_SAVE;
    public static String CRSdialog_button_CANSEL;
    public static String CRSdialog_label_Select;
    
    public static String CBC_BCS_0;
    public static String CBC_BCS_2;
    
    public static String CME_Error_2;
    public static String CME_Error_3;
    public static String CME_Error_4;
    public static String CME_Error_20;
    public static String CME_Error_24;
    public static String CME_Error_25;
    public static String CME_Error_26;
    public static String CME_Error_27;
    public static String CME_Error_30;
    public static String CME_Error_32;
    public static String CME_Error_33;
    public static String CME_Error_34;
    public static String CME_Error_35;
    public static String CME_Error_36;
    public static String CME_Error_37;
    public static String CME_Error_100;
    public static String CME_Error_512;
    public static String CME_Error_513;
    public static String CME_Error_514;
    public static String CME_Error_515;
    public static String CME_Error_516;
    public static String CME_Error_517;
    public static String CME_Error_518;    
    
    public static String CNUM_Number_Type_0;
    public static String CNUM_Number_Type_1;
    public static String CNUM_Number_Type_2;
    public static String CNUM_Number_Type_3;
    
    public static String CTCC_Hook_0;
    public static String CTCC_Hook_1;
    public static String CTCC_Simplex_0;
    public static String CTCC_Simplex_1;
    public static String CTCC_AI_service_0;
    public static String CTCC_AI_service_1; 
    public static String CTCC_AI_service_2;
    public static String CTCC_AI_service_5;
    public static String CTCC_End_to_end_encryption_0;
    public static String CTCC_End_to_end_encryption_1;
    public static String CTCC_Comms_Type_0;
    public static String CTCC_Comms_Type_1;
    public static String CTCC_Comms_Type_3;
    public static String CTCC_Slots_Codec_0;
    public static String CTCC_Slots_Codec_1;
    
    public static String CTCR_Disconnect_Cause_0;
    public static String CTCR_Disconnect_Cause_1;
    public static String CTCR_Disconnect_Cause_2;
    public static String CTCR_Disconnect_Cause_3;
    public static String CTCR_Disconnect_Cause_4;
    public static String CTCR_Disconnect_Cause_5;
    public static String CTCR_Disconnect_Cause_6;
    public static String CTCR_Disconnect_Cause_7;
    public static String CTCR_Disconnect_Cause_8;
    public static String CTCR_Disconnect_Cause_9;
    public static String CTCR_Disconnect_Cause_10;
    public static String CTCR_Disconnect_Cause_11;
    public static String CTCR_Disconnect_Cause_12;
    public static String CTCR_Disconnect_Cause_13;
    public static String CTCR_Disconnect_Cause_14;
    public static String CTCR_Disconnect_Cause_15;
    public static String CTCR_Disconnect_Cause_16;
    public static String CTCR_Disconnect_Cause_17;
    public static String CTCR_Disconnect_Cause_18;
    public static String CTCR_Disconnect_Cause_19;
    public static String CTCR_Disconnect_Cause_20;
    
    public static String CTICN_Call_Status_0;
    
    public static String CTICN_Calling_Party_Ident_Type_0;
    public static String CTICN_Calling_Party_Ident_Type_1;
    
    public static String CTSDC_Area_0;
    
    public static String CTSDC_RxTx_0;
    public static String CTSDC_RxTx_1;
    
    public static String CTSDC_Priority_0;
    public static String CTSDC_Priority_n;
    
    public static String CTSDS_access_priority_low;
    public static String CTSDS_access_priority_high;
    public static String CTSDS_access_priority_emer;
    public static String CTSDS_ai_service_12;
    public static String CTSDS_ai_service_13;
    public static String CTSDS_ai_service_28;
    public static String CTSDS_ai_service_29;
    
    public static String CMGS_STATUS_stored_unsent;
    public static String CMGS_STATUS_stored_sent;
    public static String CMGS_STATUS_deleted_unsent;
    public static String CMGS_STATUS_deleted_sent;
    
    public static String CTSG_group_selected;
    public static String CTSG_group_not_scanned;
    public static String CTSG_group_psg_low;
    public static String CTSG_group_psg_normal;
    public static String CTSG_group_psg_high;
    public static String CTSG_group_locked;
    public static String CTSG_group_always_scanned;
    
    public static String CDTXC_tx_rq_allowed;
    public static String CDTXC_tx_rq_not_allowed;
    
    public static String CTXG_tx_granted;
    public static String CTXG_tx_not_granted;
    public static String CTXG_tx_queued;
    public static String CTXG_tx_granted_another;
    
    public static String ATE_echo_off;
    public static String ATE_echo_on;
    
    public static String ATQ_terminal_off;
    public static String ATQ_terminal_on;
    
    public static String ATS0_answer_0;
    public static String ATS0_answer_n;
    
    public static String CREG_registered_status_0;
    public static String CREG_registered_status_1;
    public static String CREG_registered_status_2;
    public static String CREG_registered_status_3;
    
    public static String CSPDCS_not_detected;
    public static String CSPDCS_detected;
    public static String CSPDCS_no_restriction;
    public static String CSPDCS_restriction;
    
    public static String CSPTSS_stack_0;
    public static String CSPTSS_stack_1;
    public static String CSPTSS_stack_2;
    public static String CSPTSS_stack_3;
    public static String CSPTSSA_stack;
    
    public static String CTSP_service_profile_0;
    public static String CTSP_service_profile_1;
    public static String CTSP_service_profile_3;
    public static String CTSP_service_profile_17;
    public static String CTSP_service_profile_19;
    public static String CTSP_service_layer1_0;
    public static String CTSP_service_layer1_2;
    public static String CTSP_service_layer1_3;
    public static String CTSP_service_layer2_0;
    public static String CTSP_service_layer2_1;
    public static String CTSP_service_layer2_20;
    
    public static String CTXD_TxDemandPriority_0;
    public static String CTXD_TxDemandPriority_1;
    public static String CTXD_TxDemandPriority_2;
    public static String CTXD_TxDemandPriority_3;
    
    public static String CSPTD_state_enabled;
    public static String CSPTD_tei_state_disabled;
    public static String CSPTD_itsi_state_disabled;
    
    public static String CTXN_tx_cont_0;
    public static String CTXN_tx_cont_1;

    public static String RemoteServerUrlPage_0;

    public static String RemoteServerUrlPage_1;
    
    public static String TransmissionImportWizard_PAGE_TITLE;
    public static String TransmissionImportWizard_PAGE_DESCR;
    public static String TransmissionImportWizard_LOAD_JOB_NAME;
    
    public static String OSSImportWizardPage_LOAD_SINGLE_FILE;
    
    public static String NeighbourImportWizardPage_FILE_EDITOR_NAME;
    public static String NeighbourImportWizardPage_NETWORK_LBL;
    
    public static String NeighbourImportWizard_LOAD_NEIGHBOUR_JPB_NAME;
    public static String NeighbourImportWizard_PAGE_TITLE;
    public static String NeighbourImportWizard_PAGE_DESCR;
    
    public static String DataLoadPreferencePage_LABEL_REMOVE_SITE_NAME;
    public static String DataLoadPreferencePage_USE_COMBINED_CALCULATION;
    public static String DataLoadPreferencePage_ZOOM_TO_DATA;
    public static String DataLoadPreferencePage_PREFERENCE_CHARSET;
    
    public static String AMSCorrellator_CALL_BBOX_CHANGED_INFO;
    public static String AMSCorrellator_DATASET_BBOX_CHANGED_INFO;
    public static String AMSCorrellator_CORRELATING_AMS_DATASET_INFO;
    public static String AMSCorrellator_SUCCESS_CORRELATED_TASK;
    public static String AMSCorrellator_CORRELATION_WAS_INTERRUPTED_INFO;
    public static String AMSCorrellator_EXECUTE_CORRELATING_TASK;
    public static String AMSCorrellator_START_CORRELATED_INFO;
    public static String AMSCorrellator_STAT_CORRELATED_START;
    
    public static String UTRANLoader_WRONG_TAG_ERROR;
    public static String UTRANLoader_WRONG_PARSE_FILE_ERROR;
    public static String UTRANLoader_LOAD_UTRAN_FILES_TASK;
    public static String UTRANLoader_PROCESS_INFO;
    public static String UTRANLoader_FORMAT_STR;
    
    public static String TEMSLoader_ERROR_LOADING_TEMS_DATA;
    public static String TEMSLoader_PARSING_COLUMN_ERROR;
    public static String TEMSLoader_DEBUG_INFO2;
    public static String TEMSLoader_DEBUG_INFO1;
    public static String TEMSLoader_SERVER_CHANGED_ERROR;
    public static String TEMSLoader_MEASUREMENT_COUNT_ERROR;
    public static String TEMSLoader_FAILED_PARSE_LINE_ERROR;
    
    public static String RomesLoader_ERROR_LOADING_ROMES_DATA;
    public static String RomesLoader_WRONG_FILENAME_FORMAT_ERROR;
	public static String RomesLoader_DEL_PREFIX;
    public static String RomesLoader_MISSING_FIELD_MSG;
    
    public static String TifLoader_ERROR_LOADING_TIF_DATA;
    public static String TifLoader_WRONG_FILENAME_FORMAT_ERROR;
    
    public static String ProbeLoader_PROBE_NOT_STORED_ERROR;
    
    public static String OldNemoVersionLoader_WRONG_TIME_FORMAT_ERROR;
    public static String OldNemoVersionLoader_NOT_PARSED_ERROR;
    
    public static String NemoLoader_WRONG_CONTEXT_ID_ERROR;
    public static String NemoLoader_WRONG_TIME_FORMAT_ERROR;
    
    public static String NetworkSiteLoader_ADDED_DEBUG_INFO;
    public static String NetworkSiteLoader_ERROR_PARSING_LINE;
    public static String NetworkSiteLoader_MISSING_SECTOR_NAME_ON_LINE_ERROR;
    
    public static String NetworkLoader_NO_NETWORK_NODE_FOUND_ERROR;
    public static String NetworkLoader_FINISHED_LOADING_INFO;
    public static String NetworkLoader_ERROR_PARSING_LINE;
    public static String NetworkLoader_NEW_SECTOR_DEBUG;
    public static String NetworkLoader_NEW_SITE_DEBUG;
    public static String NetworkLoader_NEW_BSC_DEBUG;
    public static String NetworkLoader_NEW_CITY_DEBUG;
    public static String NetworkLoader_MISSING_SECTOR_NAME_ERROR;
    public static String NetworkLoader_UNCAUGHT_ERRORS_WARN;
    public static String NetworkLoader_MISSING_FIELDS_WARN;
    public static String NetworkLoader_FIELD_PARSING_WARN;
    public static String NetworkLoader_EMPTY_FIELDS_WARN;
    public static String NetworkLoader_WARNING_STRING_END;
    public static String NetworkLoader_WARNING_STRING_START;
    
    public static String NeighbourLoader_NOT_FOUND_SECTORS_ERROR;
    public static String NeighbourLoader_IMPORTING_TASK;
    
    public static String LoadNetwork_ERROR_LOADING_FILE;
    public static String LoadNetwork_NETWORK_FILE_NAMES_ALL;
    public static String LoadNetwork_NETWORK_FILE_NAMES_CSV;
    public static String LoadNetwork_NETWORK_FILE_NAMES_TXT;
    public static String LoadNetwork_NETWORK_FILE_NAMES_SCX;
    public static String LoadNetwork_NETWORK_FILE_NAMES_XLS;
    public static String LoadNetwork_NETWORK_FILE_NAMES_XML;
    public static String LoadNetwork_LOAD_JOB_NAME;
    
    public static String GPEH_FINISHED_LOADING_INFO;
    public static String GPEH_TOTAL_TIME_INFO;
    public static String GPEH_SAVED_EVENTS_INFO;
    public static String GPEH_LOAD_GPEH_DATA_TASK;
    
    public static String DriveLoader_EXITING_STATISTICS_AFTER_100_INFO;
    public static String DriveLoader_FOUND_MEASUREMENT_INFO;
    public static String DriveLoader_NO_FILE_NODE_FOUND_ERROR;
    public static String DriveLoader_STAT_TEXT6;
    public static String DriveLoader_STAT_TEXT5;
    public static String DriveLoader_STAT_TEXT4;
    public static String DriveLoader_STAT_TEXT3;
    public static String DriveLoader_STAT_TEXT2;
    public static String DriveLoader_STAT_TEXT1;
    public static String DriveLoader_ADDED_FIRST_MEASUREMENT_DEBUG;
    
    public static String AMSXML_PARSE_GPS_ERROR;
    public static String AMSXML_LOADING_FILE_TASK;
    public static String AMSXML_SEARCHING_FILES_TASK;
    public static String AMSXML_LOADING_AMS_DATA_TASK;
    public static String AMSXML_UNKNOWN_CALL_TYPE;
    
    public static String NokiaTopologyLoader_WRONG_TAG_ERROR;
    public static String NokiaTopologyLoader_PARSED_CREATED_INFO;
    public static String NokiaTopologyLoader_ADDED_CHILD_DEBUG;
    public static String NokiaTopologyLoader_WRONG_PARSE_FILE_ERROR;
    public static String NokiaTopologyLoader_LOAD_NOKIA_TOPOLOGY_TASK;
    public static String NokiaTopologyLoader_FORMAT_STR;
    
    public static String Common_SELECT_CRS_PAGE_SUB_TITLE;
    public static String Common_SELECT_CRS_PAGE_TITLE;
    public static String Common_FINISHED_LOADING_INFO;
    public static String Common_DELETING_NODE_DEBUG; 
    public static String Common_NO_HEADER_FOUND_FOR_KEY_INFO; 
    public static String Common_CANNOT_ADD_MAPPED_HEADER_INFO;
    public static String Common_ADDED_KNOWN_HEADER_DEBUG;
    public static String Common_ADDED_HEADER_DEBUG;
    public static String Common_MULTIPLE_HEADER_INFO;
    
    public static String OSSCounterLoader_WRONG_COUNT_ERROR;
    public static String OSSCounterLoader_WRONG_PARCE_ERROR;
    
    public static String TransmissionLoader_NOT_FOUND_SITE_ERROR;
    
    public static String Gpeh_LOAD_PREF;
    
    /**
     * Constructor.
     */
    private NeoLoaderPluginMessages() {
    }

    /**
     * @return Returns the resourceBundle.
     */
    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    /**
     * Get formatted string.
     *
     * @param key String
     * @param args Object...
     * @return String
     */
    public static String getFormattedString(String key, Object ... args) {
        return MessageFormat.format(key, args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, NeoLoaderPluginMessages.class);
    }

}
