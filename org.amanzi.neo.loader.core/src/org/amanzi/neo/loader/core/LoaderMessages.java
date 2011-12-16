package org.amanzi.neo.loader.core;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class LoaderMessages extends NLS {
    
    private static final String BUNDLE_NAME = LoaderMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    //time
    public static String TimeOfFileLoading;
    public static String AllTimeOfLoading;
    public static String TimeOfDataSaving;
    
    //statistics
    public static String Loading;
    
    
    
    
    private LoaderMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getFormattedString(String key, Object... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, LoaderMessages.class);
    }

}
