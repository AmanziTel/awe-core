package org.amanzi.awe.views.explorer.view;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class ProjectExplorerViewMessages {

    private static final String BUNDLE_NAME = ProjectExplorerViewMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String showInDistributionAnalyseItem;
    public static String showInPropertyViewItem;
    public static String showIndDriveInqureItem;
    public static String showInN2nViewItem;
    public static String starToolAnaluse;
    public static String showOnMap;

    private ProjectExplorerViewMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getFormattedString(String key, String... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, ProjectExplorerViewMessages.class);
    }
}
