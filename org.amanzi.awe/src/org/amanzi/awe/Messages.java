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

package org.amanzi.awe;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author harchevnikov_m
 * @since 1.0.0
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.amanzi.awe.messages"; //$NON-NLS-1$
    
    public static String Application_Neo4j_D_Location;
    public static String Application_Running_instance;
    public static String Application_Inv_location;
    public static String Application_Browse;
    public static String Application_SWT_DD;
    public static String Application_Select_Directory;
    public static String Application_Ok;
    public static String Application_Cancel;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
