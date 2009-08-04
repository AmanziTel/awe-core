package org.amanzi.splash.neo4j.database.exception;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class SplashDatabaseExceptionMessages extends NLS {
    
    private static final String BUNDLE_NAME = SplashDatabaseExceptionMessages.class.getName();
	public static final String Not_Single_Chart_by_ID = null;
	public static final String Not_Single_Chart_Item_by_ID = null;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Not_Single_Row_by_ID;
    public static String Not_Single_Column_by_ID;
    public static String Not_Single_Cell_by_ID;
    public static String Service_Method_Exception;
    public static String Duplicate_Spreadsheet;
    
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
