package org.amanzi.neo.core.enums;

import org.neo4j.api.core.RelationshipType;

/**
 * Relationship types defined by the GeoNeo specification for traversing
 * GIS data.
 * @author craig
 * @since 1.0.0
 */
public enum GeoNeoRelationshipTypes implements RelationshipType {
    NEXT, PROPERTIES, CHILD;
}