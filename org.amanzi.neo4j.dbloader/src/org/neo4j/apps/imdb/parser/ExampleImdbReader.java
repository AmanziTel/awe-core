package org.neo4j.apps.imdb.parser;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;
import org.neo4j.apps.imdb.domain.RelTypes;
import org.neo4j.util.index.IndexService;

public class ExampleImdbReader implements ImdbReader
{
    private final NeoService neo;
    private final IndexService indexService;
    private Transaction tx;

    public ExampleImdbReader( NeoService neoService, IndexService indexService )
    {
        this.neo = neoService;
        this.indexService = indexService;
    }

    public void beginTransaction()
    {
        tx = neo.beginTx();
    }

    public void endTransaction()
    {
        tx.success();
        tx.finish();
    }

    public void newActor( String name, MovieRole[] movieRoles )
    {
        Node actor = neo.createNode();
        actor.setProperty( "name", name );
        indexService.index( actor, "name", name );
        for ( MovieRole movieRole : movieRoles )
        {
            Node movie = indexService.getSingleNode( "title", movieRole
                .getTitle() );
            if ( movie != null )
            {
                Relationship rel = actor.createRelationshipTo( movie,
                    RelTypes.ACTS_IN );
                if ( movieRole.getRole() != null )
                {
                    rel.setProperty( "role", movieRole.getRole() );
                }
            }
        }
    }

    public void newMovie( String title, int year )
    {
        Node movie = neo.createNode();
        movie.setProperty( "title", title );
        movie.setProperty( "year", year );
        indexService.index( movie, "title", title );
    }
}
