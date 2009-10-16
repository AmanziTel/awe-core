package org.amanzi.splash.database.services;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Copy_Error_Title;
    public static String Open_Spreadsheet_Error_Title;
    public static String Open_Spreadsheet_Error_Message;
    
    public static String Format_Error_Title;
    public static String Format_Error_Message;
    
    public static String Wizard_Error_Message;
    public static String File_Import_Wizard_Title;
    public static String Excel_Import_Title;
    public static String Excel_Import_Page_Description;
    public static String File_Editor_Text;
    public static String CSV_Import_Title;
    public static String CSV_Imoprt_Page_Description;
    
    
    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
}
