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

package org.amanzi.neo.geoptima.core.ui.messages;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class CoreMessages extends NLS {
    private static final String BUNDLE_NAME = "org.amanzi.neo.geoptima.core.ui.messages.messages"; //$NON-NLS-1$

    public static String host;

    public static String userName;

    public static String password;

    static {
        NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
    }

    private CoreMessages() {
    }

    public static String formatString(final String pattern, final Object... parameters) {
        return MessageFormat.format(pattern, parameters);
    }
}
