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

package org.amanzi.neo.loader.ui.internal;

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
    private static final String BUNDLE_NAME = "org.amanzi.neo.loader.ui.internal.messages"; //$NON-NLS-1$

    public static String AbstractLoaderPage_LoaderNotSelectedError;

    public static String LoaderWizardHandler_NoWizardIdError;
    public static String LoaderWizardHandler_NoWizardByIdError;

    public static String LoadNetworkPage_PageName;
    public static String LoadDrivePage_PageName;

    public static String ResourceSelectorWidget_SelectDirectoryTitle;

    public static String ResourceSelectorWidget_SelectFileTitle;

    public static String SelectLoaderWidget_Label;

    public static String SelectNetworkNameWidget_Label;

    public static String SelectDriveNameWidget_Label;

    public static String DriveDataFileSelector_DirectoryFilesLabel;
    public static String DriveDataFileSelector_SelectedFilesLabel;
    public static String DriveDataFileSelector_AddButton;
    public static String DriveDataFileSelector_AddAllButton;
    public static String DriveDataFileSelector_RemoveButton;
    public static String DriveDataFileSelector_RemoveAllButton;

    public static String DriveSynonymsPreferencePage_DriveSynonymsPage_Description;

    public static String NetworkSynonymsPreferencePage_NetworkSynonymsPage_Description;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String formatString(final String pattern, final Object... parameters) {
        return MessageFormat.format(pattern, parameters);
    }
}
