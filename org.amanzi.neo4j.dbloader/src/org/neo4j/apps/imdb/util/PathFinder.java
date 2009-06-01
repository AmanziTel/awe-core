package org.neo4j.apps.imdb.util;

import java.util.List;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;

public interface PathFinder
{
    List<Node> shortestPath( Node node1, Node node2, RelationshipType relType );
}