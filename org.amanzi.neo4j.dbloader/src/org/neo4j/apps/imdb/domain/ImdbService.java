package org.neo4j.apps.imdb.domain;

import java.util.List;

public interface ImdbService
{
    /**
     * Returns the actor with the given <code>name</code> or <code>null</code> 
     * if not found.
     * 
     * @param name name of actor
     * @return actor or <code>null</code> if not found
     */
    Actor getActor( String name );

    /**
     * Return the movie with given <code>title</code> or <code>null</code> if 
     * not found.
     * 
     * @param title movie title
     * @return movie or <code>null</code> if not found
     */
    Movie getMovie( String title );

    /**
     * Returns a list with first element {@link Actor} followed by {@link Movie} 
     * ending with an {@link Actor}. The list is one of the shortest paths
     * between the <code>actor</code> and actor Kevin Bacon.
     * 
     * @param actor name of actor to find shortest path to Kevin Bacon
     * @return one of the shortest paths to Kevin Bacon
     */
    List<?> getBaconPath( Actor actor );
}
