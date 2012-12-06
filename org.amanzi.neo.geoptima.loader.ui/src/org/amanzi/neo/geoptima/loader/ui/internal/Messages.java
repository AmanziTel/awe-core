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

package org.amanzi.neo.geoptima.loader.ui.internal;

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
    private static final String BUNDLE_NAME = "org.amanzi.neo.geoptima.loader.ui.internal.messages"; //$NON-NLS-1$

    public static String LoadGeoptimaPage_PageName;
    public static String selectLocalCatalSource_PageName;
    public static String selectWebSource_PageName;
    public static String selectFtpSource_PageName;
    public static String selectDataUploadingFilters_PageName;

    public static String host_Label;

    public static String userName_Label;

    public static String password_Label;

    public static String connectButton_Label;

    public static String selectFilesToUploadMessage;

    public static String enterDatasetName_message;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String formatString(final String pattern, final Object... parameters) {
        return MessageFormat.format(pattern, parameters);
    }
}
