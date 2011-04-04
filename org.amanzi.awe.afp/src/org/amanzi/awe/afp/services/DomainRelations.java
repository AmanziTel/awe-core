package org.amanzi.awe.afp.services;

import org.neo4j.graphdb.RelationshipType;

public enum DomainRelations implements RelationshipType{
    DOMAINS,
    NEXT,
    ASSIGNED_NEXT

}
