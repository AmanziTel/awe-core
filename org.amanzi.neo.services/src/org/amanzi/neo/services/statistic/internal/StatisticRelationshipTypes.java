package org.amanzi.neo.services.statistic.internal;

import org.neo4j.graphdb.RelationshipType;

/**
 * 
 * TODO Purpose of StatisticHandler
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
    public enum StatisticRelationshipTypes implements RelationshipType{
        STATISTIC_PROP,PROPERTIES,NODE_TYPES,PROPERTY;
    }