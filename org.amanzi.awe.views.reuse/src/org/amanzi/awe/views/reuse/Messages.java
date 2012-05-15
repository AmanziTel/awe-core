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

package org.amanzi.awe.views.reuse;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for views.
 * <p>
 * 
 * </p>
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = Messages.class.getName();

	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String MessageAndEventTable_label_DATA_TYPE;
	public static String MessageAndEventTable_label_DATASET;
	public static String MessageAndEventTable_label_PROPERTY;
	public static String MessageAndEventTable_label_EXPRESSION;

	public static String MessageAndEventTable_menu_COMMIT;
	public static String MessageAndEventTable_menu_ROLLBACK;
	public static String MessageAndEventTable_menu_CONFIGURE;
	public static String MessageAndEventTable_menu_CLEAR;

	public static String TableConfigWizard_title;
	public static String TableConfigWizard_description;
	public static String TableConfigWizard_label_visible;
	public static String TableConfigWizard_label_invisible;

	public static String ReuseAnalayserView_UNKNOWN_ERROR;
	public static String ReuseAnalayserView_TOOL_TIP_LOG;
	public static String ReuseAnalayserView_TOOL_TIP_DATA;
	public static String ReuseAnalayserView_TOOL_TIP_PROPERTY;
	public static String ReuseAnalayserView_TOOL_TIP_DISTRIBUTE;
	public static String ReuseAnalayserView_TOOL_TIP_SELECT;
	public static String ReuseAnalayserView_TOOL_TIP_ADJACENCY;
	public static String ReuseAnalayserView_TOOL_TIP_SELECTED_VALUES;
	public static String ReuseAnalayserView_SELECT_LABEL;
	public static String ReuseAnalayserView_DISTRIBUTE_LABEL;
	public static String ReuseAnalayserView_LABEL_INFO;
	public static String ReuseAnalayserView_LABEL_INFO_BLEND;
	public static String ReuseAnalayserView_ERROR_TITLE;
	public static String ReuseAnalayserView_ERROR_MSG;
	public static String ReuseAnalayserView_LOG_LABEL;
	public static String ReuseAnalayserView_FIELD_ADJACENCY;
	public static String ReuseAnalayserView_FIELD_PROPERTY_LABEL;
	public static String ReuseAnalayserView_FIELD_GIS_LABEL;
	public static String ReuseAnalayserView_FIELD_COUNT_AXIS;
	public static String ReuseAnalayserView_FIELD_VALUES_DOMAIN;
	public static String ReuseAnalayserView_ROW_KEY;
	public static String ReuseAnalayserView_COLOR_LABEL;
	public static String ReuseAnalayserView_REPORT_LABEL;
	public static String ReuseAnalayserView_RXLEV;
	public static String ReuseAnalayserView_RXQUAL;
	public static String DATASET_LABEL;
	public static String PROPERTY_LABEL;
	public static String DISTRIBUTION_LABEL;
	public static String SELECT_LABEL;
	public static String DISTRIBUTION_CHART_NAME;
	public static String VALUES_AXIS_NAME;
	public static String NUMBERS_AXIS_NAME;
	public static String COLOR_PROPERTIES_LABEL;
	public static String SELECTED_VALUES_LABEL;
	public static String SELECTION_ADJACENCY_LABEL;
	public static String BLEND_LABEL;
	public static String CHART_TYPE_LABEL;
	public static String LEFT_COLOR_LABEL;
	public static String RIGHT_COLOR_LABEL;
	public static String MIDDLE_COLOR_LABEL;
	public static String THIRD_COLOR_LABEL;
	public static String PALETTE_LABEL;
	public static String LOAD_XML_LABEL;
	public static String SELECT_XML_DIALOG_LABEL;
	public static String UPDATE_BARS_ERROR;

	private Messages() {
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
