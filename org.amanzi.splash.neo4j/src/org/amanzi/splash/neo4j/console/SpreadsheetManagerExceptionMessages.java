package org.amanzi.splash.neo4j.console;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class SpreadsheetManagerExceptionMessages extends NLS {
    
    private static final String BUNDLE_NAME = SpreadsheetManagerExceptionMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String No_Ruby_Projects;
    public static String No_AWE_Project;
    public static String No_Spreadsheet;
    public static String No_Ruby_Project_in_AWE;
    
    private SpreadsheetManagerExceptionMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, SpreadsheetManagerExceptionMessages.class);
    }

}
