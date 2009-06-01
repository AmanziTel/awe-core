package org.neo4j.apps.imdb.domain;

import org.neo4j.api.core.Relationship;
//import org.springframework.transaction.annotation.Transactional;

class RoleImpl implements Role
{
    private final Relationship underlyingRel;

    RoleImpl( Relationship rel )
    {
        this.underlyingRel = rel;
    }

    protected Relationship getUnderlyingRelationship()
    {
        return this.underlyingRel;
    }

    //@Transactional
    public Actor getActor()
    {
        return new ActorImpl( underlyingRel.getStartNode() );
    }

    //@Transactional
    public Movie getMovie()
    {
        return new MovieImpl( underlyingRel.getEndNode() );
    }

    //@Transactional
    public String getName()
    {
        if ( underlyingRel.hasProperty( "role" ) )
        {
            return (String) underlyingRel.getProperty( "role" );
        }
        return null;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof RoleImpl )
        {
            return this.underlyingRel.equals(
                ((RoleImpl) o).getUnderlyingRelationship() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.underlyingRel.hashCode();
    }

    @Override
    //@Transactional
    public String toString()
    {
        String role = this.getName();
        if ( role == null )
        {
            role = "";
        }
        return this.getActor() + "-[" + role + "]->" + this.getMovie();
    }
}
