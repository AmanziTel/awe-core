package org.amanzi.neo.loader.internal;

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
    public static String Console_ErrorOnClose;
    public static String NetworkDialog_DialogTitle;
	public static String DriveDialog_AddAllButtonText;
	public static String DriveDialog_RemoveAllButtonText;
	public static String DriveDialog_DatasetLabel;
    public static String ADD_LAYER_MESSAGE;
    public static String ADD_NEW_MAP_MESSAGE;
    public static String ADD_LAYER_TITLE;
    
    private NeoLoaderPluginMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, NeoLoaderPluginMessages.class);
    }

}
