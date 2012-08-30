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

package org.amanzi.neo.core.internal;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * <p>
 * Messages for plug-in.
 * </p>
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    // For Call Time Periods
    public static String ctrHourly;
    public static String ctrDaily;
    public static String ctrWeekly;
    public static String ctrMonthly;
    public static String ctrYearly;
    public static String ctrTotal;

    /**
     * Constructor.
     */
    private Messages() {
    }

    /**
     * @return the resourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return BUNDLE;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
