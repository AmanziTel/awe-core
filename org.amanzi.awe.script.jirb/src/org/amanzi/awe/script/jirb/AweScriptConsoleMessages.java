package org.amanzi.awe.script.jirb;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class AweScriptConsoleMessages extends NLS {
    
    private static final String BUNDLE_NAME = AweScriptConsoleMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Welcome;
    public static String Restart_IRB;
    public static String Swing_based_Console_Text;
    public static String Swing_based_Console_Tooltip;
    public static String SWT_based_Console_Text;
    public static String SWT_based_Console_Tooltip;
    
    private AweScriptConsoleMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, AweScriptConsoleMessages.class);
    }

}
