package org.amanzi.splash.database.services;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Copy_Error_Title;
    
    private Messages() {
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
