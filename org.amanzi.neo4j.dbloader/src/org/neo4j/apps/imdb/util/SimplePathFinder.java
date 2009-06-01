package org.neo4j.apps.imdb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;
//import org.springframework.transaction.annotation.Transactional;

public class SimplePathFinder implements PathFinder
{
    public List<Node> shortestPath( Node node1, Node node2,
        RelationshipType relType )
    {
        return findPath( node1, node2, relType );
    }

    //@Transactional
    private List<Node> findPath( Node actor1, Node actor2, RelationshipType
        relType )
    {
        // actor list containing nodes starting with an actor/actress
        // followed by movie,actor/actress pairs until the other
        // actor/actress is found
        List<Node> nodeList = new ArrayList<Node>();
        // using index service to find actor/actress
        final Map<Node, List<Node>> traversedNodes1 =
            new HashMap<Node, List<Node>>();
        final Map<Node, List<Node>> traversedNodes2 =
            new HashMap<Node, List<Node>>();
        StopEvaluator stopEval = new PathStopEval();
        PathReturnEval returnEval1 = new PathReturnEval( traversedNodes1,
            traversedNodes2 );
        PathReturnEval returnEval2 = new PathReturnEval( traversedNodes2,
            traversedNodes1 );
        Traverser trav1 = actor1.traverse( Order.BREADTH_FIRST, stopEval,
            returnEval1, relType, Direction.BOTH );
        Traverser trav2 = actor2.traverse( Order.BREADTH_FIRST, stopEval,
            returnEval2, relType, Direction.BOTH );
        Iterator<Node> itr1 = trav1.iterator();
        Iterator<Node> itr2 = trav2.iterator();
        while ( itr1.hasNext() || itr2.hasNext() )
        {
            if ( itr1.hasNext() )
            {
                itr1.next();
            }
            if ( returnEval1.getMatch() != null )
            {
                return returnEval1.getMatch();
            }
            if ( itr2.hasNext() )
            {
                itr2.next();
            }
            if ( returnEval2.getMatch() != null )
            {
                return returnEval2.getMatch();
            }
        }
        return Collections.emptyList();
    }

    private static class PathStopEval implements StopEvaluator
    {
        public boolean isStopNode( TraversalPosition currentPos )
        {
            return currentPos.depth() >= 5;
        }
    }
    private static class PathReturnEval implements ReturnableEvaluator
    {
        final Map<Node, List<Node>> myNodes;
        final Map<Node, List<Node>> otherNodes;
        private List<Node> match = null;

        public PathReturnEval( final Map<Node, List<Node>> myNodes,
            final Map<Node, List<Node>> otherNodes )
        {
            this.myNodes = myNodes;
            this.otherNodes = otherNodes;
        }

        public boolean isReturnableNode( TraversalPosition currentPos )
        {
            Node prevNode = currentPos.previousNode();
            List<Node> prevList = Collections.emptyList();
            List<Node> newList = new LinkedList<Node>();
            if ( prevNode != null )
            {
                prevList = myNodes.get( prevNode );
                newList.addAll( prevList );
                newList.add( prevNode );
            }
            Node currentNode = currentPos.currentNode();
            if ( !otherNodes.containsKey( currentNode ) )
            {
                myNodes.put( currentNode, newList );
            }
            else
            {
                // match
                LinkedList<Node> otherList = ( LinkedList<Node> ) otherNodes
                    .get( currentNode );
                match = new LinkedList<Node>();
                for ( Node node : newList )
                {
                    match.add( node );
                }
                match.add( currentNode );
                // reverse order and since java 1.5 no decending iterator
                Collections.reverse( otherList );
                Iterator<Node> itr = otherList.iterator();
                while ( itr.hasNext() )
                {
                    match.add( itr.next() );
                }
            }
            return true;
        }

        public List<Node> getMatch()
        {
            return match;
        }
    }
}