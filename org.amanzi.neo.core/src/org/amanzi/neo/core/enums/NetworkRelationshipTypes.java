package org.amanzi.neo.core.enums;

import org.neo4j.api.core.RelationshipType;

/**
 * RelationshipTypes for Network
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public enum NetworkRelationshipTypes implements RelationshipType {
    AGGREGATION,
    CHILD,
    SIBLING,
    INTERFERS;
}
