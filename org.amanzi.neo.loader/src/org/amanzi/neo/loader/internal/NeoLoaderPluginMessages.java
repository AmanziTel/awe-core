package org.amanzi.neo.loader.internal;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class NeoLoaderPluginMessages extends NLS {
    
    private static final String BUNDLE_NAME = NeoLoaderPluginMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String TEMSDialog_RemoveButtonText;
    public static String TEMSDialog_AddButtonText;
    public static String TEMSDialog_BrowseButtonText;
    public static String TEMSDialog_LoadButtonText;
    public static String TEMSDialog_CancelButtonText;
    public static String TEMSDialog_FilesToLoadListLabel;
    public static String TEMSDialog_FilesToChooseListLabel;
    public static String TEMSDialog_DialogTitle;
    public static String TEMSDialog_FileDialogTitle;
    public static String TEMSDialog_MonitorName;
    public static String Console_ErrorOnClose;
    public static String NetworkDialog_DialogTitle;
	public static String TEMSDialog_AddAllButtonText;
	public static String TEMSDialog_RemoveAllButtonText;
	public static String TEMSDialog_DatasetLabel;
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
