package org.neo4j.apps.imdb.domain;

import java.util.LinkedList;
import java.util.List;
import org.neo4j.api.core.Node;
import org.neo4j.apps.imdb.util.PathFinder;
import org.neo4j.util.index.IndexService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;

class ImdbServiceImpl implements ImdbService
{
    //@Autowired
    private IndexService indexService;
    //@Autowired
    private PathFinder pathFinder;

    //@Transactional
    public Actor getActor( String name )
    {
        Node actorNode = indexService.getSingleNode( "name", name );
        Actor actor = null;
        if ( actorNode != null )
        {
            actor = new ActorImpl( actorNode ); 
        }
        return actor;
    }

    //@Transactional
    public Movie getMovie( String title )
    {
        Node movieNode = indexService.getSingleNode( "movie", title );
        Movie movie = null;
        if ( movieNode != null )
        {
            movie = new MovieImpl( movieNode );
        }
        return movie;
    }

    //@Transactional
    public List<?> getBaconPath( Actor actor )
    {
        if ( actor == null )
        {
            throw new IllegalArgumentException( "Null actor" );
        }
        Node baconNode = indexService.getSingleNode( "name", "Bacon, Kevin" );
        if ( baconNode == null )
        {
            throw new RuntimeException( "Unable to find Kevin Bacon actor" );
        }
        Node actorNode = ((ActorImpl) actor).getUnderlyingNode();
        
        List<Node> list = pathFinder.shortestPath( baconNode, actorNode,
            RelTypes.ACTS_IN );
        return convertNodesToActorsAndMovies( list );
    }
    
    private List<?> convertNodesToActorsAndMovies( List<Node> list )
    {
        List<Object> actorAndMovieList = new LinkedList<Object>();
        int mod = 0;
        for ( Node node : list )
        {
            if ( mod++ % 2 == 0 )
            {
                actorAndMovieList.add( new ActorImpl( node ) );
            }
            else
            {
                actorAndMovieList.add( new MovieImpl( node ) );
            }
        }
        return actorAndMovieList;
    }

}