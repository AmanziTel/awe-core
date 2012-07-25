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

package org.amanzi.neo.loader.core.internal;

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
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.amanzi.neo.loader.core.internal.messages"; //$NON-NLS-1$

    public static String AbstractConfiguration_EmptyDatasetNameError;

    public static String AbstractHeadersValidator_IOError;

    public static String AbstractHeadersValidator_SynonymsFailed;

    public static String NetworkValidator_DuplicatedNetworkName;
    public static String SingleFileConfiguration_FileNotExists;

    public static String SingleFileConfiguration_LocationIsNotFile;

    public static String SingleFileConfiguration_NullFile;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String format(String message, Object... parameters) {
        return MessageFormat.format(message, parameters);
    }
}
