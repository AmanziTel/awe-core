package org.neo4j.apps.imdb.domain;

import java.util.LinkedList;
import java.util.List;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
//import org.springframework.transaction.annotation.Transactional;

class ActorImpl implements Actor
{
    private final Node underlyingNode;

    ActorImpl( Node node )
    {
        this.underlyingNode = node;
    }

    protected Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }
    
    //@Transactional
    public Role getRole( Movie inMovie )
    {
        Node movieNode = ((MovieImpl) inMovie).getUnderlyingNode();
        for ( Relationship rel : underlyingNode.getRelationships( 
            RelTypes.ACTS_IN, Direction.OUTGOING ) )
        {
            if ( rel.getEndNode().equals( movieNode ) )
            {
                return new RoleImpl( rel );
            }
        }
        return null;
    }
    
    //@Transactional
    public String getName()
    {
        return ( String ) underlyingNode.getProperty( "name" );
    }

    //@Transactional
    public Iterable<Movie> getMovies()
    {
        List<Movie> movies = new LinkedList<Movie>();
        for ( Relationship rel : underlyingNode.getRelationships(
            RelTypes.ACTS_IN, Direction.OUTGOING ) )
        {
            movies.add( new MovieImpl( rel.getEndNode() ) );
        }
        return movies;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof ActorImpl )
        {
            return this.underlyingNode.equals(
                ((ActorImpl) o).getUnderlyingNode() );
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
        return "Actor '" + this.getName() + "'";
    }
}
