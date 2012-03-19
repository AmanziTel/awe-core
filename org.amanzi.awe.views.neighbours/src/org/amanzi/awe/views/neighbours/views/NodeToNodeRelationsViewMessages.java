/**
 * 
 */
package org.amanzi.awe.views.neighbours.views;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * @author Bondoronok_P
 */
public class NodeToNodeRelationsViewMessages {

	private static final String BUNDLE_NAME = NodeToNodeRelationsViewMessages.class
			.getName();

	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);
	
	
	public static String CANNOT_SET_N2N_MODEL;
	public static String SHOW_ON_MAP_8_X;
	public static String SHOW_ON_MAP_4_X;
	public static String SHOW_ON_MAP_2_X;
	public static String SERVING_COLUMN;
	public static String NEIGHBOR_COLUMN;
	public static String SERVING_FILTER;
	public static String NEIGHBOR_FILTER;
	public static String FILTER_TITLE;
	public static String ERROR_TITLE;
	public static String DATA_TITLE;
	public static String N2N_TYPE_TITLE;
	public static String N2N_NAME_TITLE;
	

	private NodeToNodeRelationsViewMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getFormattedString(String key, String... args) {
		return MessageFormat.format(key, (Object[]) args);
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME,
				NodeToNodeRelationsViewMessages.class);
	}

}
