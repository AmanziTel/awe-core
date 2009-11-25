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
package org.amanzi.neo.core.enums;

import org.neo4j.api.core.RelationshipType;

/**
 * Enum for RelationshipTypes used in Splash
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum SplashRelationshipTypes implements RelationshipType {
	SPREADSHEET, 
	CHART_ITEM, 
	CHART, 
	RUBY_PROJECT, 
	AWE_PROJECT, 
	PIE_CHART_ITEM, 
	PIE_CHART,
	SCRIPT_CELL, 
	SCRIPT, 
	SPLASH_FORMAT, 
	CHART_CATEGORY,
	CHART_VALUE, 
	REPORT, 
	REPORT_TEXT,
	NEXT_CELL_IN_ROW,
	NEXT_CELL_IN_COLUMN;
}
