package org.amanzi.rdt.internal.launching;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class AweLaunchingPluginMessages extends NLS{

    private static final String BUNDLE_NAME = AweLaunchingPluginMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Could_not_open_editor;
    public static String Console_Terminated;
    
    private AweLaunchingPluginMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, AweLaunchingPluginMessages.class);
    }
    
}
