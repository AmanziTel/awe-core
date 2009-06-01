package org.neo4j.apps.imdb.domain;

public interface Movie
{
    /**
     * Returns the title of this movie.
     * 
     * @return title of this movie.
     */
    String getTitle();

    /**
     * Returns all actors that acted in this movie.
     * 
     * @return actors that acted in this movie
     */
    Iterable<Actor> getActors();

    /**
     * Returns the year this movie was released.
     * 
     * @return the year this movie was released
     */
    int getYear();
}
