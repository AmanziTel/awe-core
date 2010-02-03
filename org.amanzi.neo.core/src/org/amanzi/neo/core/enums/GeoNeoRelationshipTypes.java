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
 * Relationship types defined by the GeoNeo specification for traversing
 * GIS data.
 * @author craig
 * @since 1.0.0
 */
public enum GeoNeoRelationshipTypes implements RelationshipType {
    NEXT, PROPERTIES, CHILD, VIRTUAL_DATASET, LOCATION, SOURCE;
}
