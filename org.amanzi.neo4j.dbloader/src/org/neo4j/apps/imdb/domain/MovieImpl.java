package org.neo4j.apps.imdb.domain;

import java.util.LinkedList;
import java.util.List;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
//import org.springframework.transaction.annotation.Transactional;

class MovieImpl implements Movie
{
    private final Node underlyingNode;

    MovieImpl( Node node )
    {
        this.underlyingNode = node;
    }

    protected Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }

    //@Transactional
    public String getTitle()
    {
        return ( String ) underlyingNode.getProperty( "title" );
    }

    //@Transactional
    public Iterable<Actor> getActors()
    {
        List<Actor> actors = new LinkedList<Actor>();
        for ( Relationship rel : underlyingNode.getRelationships(
            RelTypes.ACTS_IN, Direction.INCOMING ) )
        {
            actors.add( new ActorImpl( rel.getStartNode() ) );
        }
        return actors;
    }

    //@Transactional
    public int getYear()
    {
        return ( Integer ) underlyingNode.getProperty( "year" );
    }
       
    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof MovieImpl )
        {
            return this.underlyingNode.equals(
                ((MovieImpl) o).getUnderlyingNode() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.underlyingNode.hashCode();
    }

    @Override
    //@Transactional
    public String toString()
    {
        return ( String ) underlyingNode.getProperty( "title" );
    }
}
