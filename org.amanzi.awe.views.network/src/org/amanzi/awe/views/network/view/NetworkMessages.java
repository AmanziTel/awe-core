/**
 * 
 */
package org.amanzi.awe.views.network.view;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Bondoronok_P
 */
public class NetworkMessages {

	private static final String BUNDLE_NAME = NetworkMessages.class.getName();
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String LABEL_TITLE;
	public static String BUTTON_OK_TITLE;
	public static String BUTTON_CANCEL_TITLE;
	public static String NAME_ERROR_TITLE;
	public static String NAME_ERROR;
	public static String ELEMENT_EXIST_ERROR_TITLE;
	public static String ELEMENT_EXIST_ERROR;
	public static String LAT_LON_ERROR_TITLE;
	public static String LAT_LON_ERROR;
	public static String CI_LAC_ERROR_TITLE;
	public static String CI_LAC_ERROR;
	public static String PAGE_NAME;
	public static String DIRECTORY_NAME;
	public static String DIRECTORY_LABEL;
	public static String MAIN_DESCRIPTION;
	public static String DESCRIPTION_DIRECTORY;
	public static String DESCRIPTION_SEPARATOR;
	public static String LABEL_EXTENSION;
	public static String LABEL_SEPARATOR;
	public static String OTHER_SEPARATOR;
	public static String ERROR_TITLE;
	public static String EXPORT_WINDOW_TITLE;
	public static String DESTINATION_OF_THIS_VIEW;
	public static String SELECT_ALL;
	public static String PROPERTIES;
	public static String PROPERTIES_FILTER;
	public static String RENAME_MSG;
	public static String SHOW_PROPERTIES;
    public static String EDIT_PROPERTIES;
    public static String NETWORK_PROPERTIES_OPEN_ERROR;
    public static String ERROR_MSG;
	public static String SELECT_DATA_ELEMENTS_TO_DELETE;
	public static String DELETE_DATA_ELEMENT;
	public static String DELETE_DATA_ELEMENT_MSG;
	public static String RENAME;
	public static String CREATE_SELECTION_LIST;
	public static String NEW_SELECTION_LIST;
	public static String ADD_TO_SELECTION_LIST;
	public static String ADD_TO_SELECTION_LIST_ERROR;
	public static String DELETE_FROM_SELECTION_LIST;
	public static String DELETE_FROM_SELECTION_LIST_ERROR;
	public static String SECTOR;
	public static String ALREADY_EXIST;
	public static String COPY_OF_ELEMENT;
	public static String CREATE_NEW_ELEMENT;
	public static String SHOW_ON_MAP;
	public static String NAME_OF_SELECTION_LIST_TITLE;

	private NetworkMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getFormattedString(String key, String... args) {
		return MessageFormat.format(key, (Object[]) args);
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NetworkMessages.class);
	}
}
