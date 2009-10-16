/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
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
