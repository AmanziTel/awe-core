package org.amanzi.awe.networktree;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for Network Tree
 * 
 */

public class NetworkTreePluginMessages extends NLS {
    
    private static final String BUNDLE_NAME = NetworkTreePluginMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String NetworkTree_CouldNotBeShown;
    public static String NetworkPropertySheet_Description;
    
    private NetworkTreePluginMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, NetworkTreePluginMessages.class);
    }

}
