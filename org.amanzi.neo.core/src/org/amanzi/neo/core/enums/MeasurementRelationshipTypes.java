/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.core.enums;

import org.neo4j.api.core.RelationshipType;

/**
 * RelationshipTypes for Measurement
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public enum MeasurementRelationshipTypes implements RelationshipType {
    // FIRST,
    // LAST,
    // NEXT,
	CHILD,
	SOURCE,
	POINT,
	NEXT
}