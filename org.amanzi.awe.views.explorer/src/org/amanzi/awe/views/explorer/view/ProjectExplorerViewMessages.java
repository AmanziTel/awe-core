package org.amanzi.awe.views.explorer.view;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class ProjectExplorerViewMessages {

	private static final String BUNDLE_NAME = ProjectExplorerViewMessages.class
			.getName();
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String SHOW_IN_DISTRIBUTION_ANALYSE_ITEM;
	public static String SHOW_IN_PROPERTY_TABLE_ITEM;
	public static String SHOW_IN_DRIVE_INUQIER_ITEM;
	public static String SHOW_IN_N2N_VIEW_ITEM;
	public static String STAR_TOOL_ANALYSE;
	public static String SHOW_ON_MAP;

	private ProjectExplorerViewMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getFormattedString(String key, String... args) {
		return MessageFormat.format(key, (Object[]) args);
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, ProjectExplorerViewMessages.class);
	}
}
