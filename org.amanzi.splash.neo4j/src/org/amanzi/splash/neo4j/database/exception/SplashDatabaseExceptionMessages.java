package org.amanzi.splash.neo4j.database.exception;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class SplashDatabaseExceptionMessages extends NLS {
    
    private static final String BUNDLE_NAME = SplashDatabaseExceptionMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Not_Single_Row_by_ID;
    public static String Not_Single_Column_by_ID;
    public static String Not_Single_Cell_by_ID;
    public static String Service_Method_Exception;
    
    private SplashDatabaseExceptionMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, SplashDatabaseExceptionMessages.class);
    }

}
