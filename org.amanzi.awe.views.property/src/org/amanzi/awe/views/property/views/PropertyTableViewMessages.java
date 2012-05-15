package org.amanzi.awe.views.property.views;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class PropertyTableViewMessages {

	private static final String BUNDLE_NAME = PropertyTableViewMessages.class.getName();
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String DATA_TITLE;
	public static String PROPERTY_TITLE;
	public static String FILTER_TITLE;
	
	
	private PropertyTableViewMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getFormattedString(String key, String... args) {
		return MessageFormat.format(key, (Object[]) args);
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, PropertyTableViewMessages.class);
	}

}
