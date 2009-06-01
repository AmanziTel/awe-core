package org.neo4j.apps.imdb.domain;

public interface Actor
{
    /**
     * Returns this actors imdb-encoded name.
     * 
     * @return actor name
     */
    String getName();

    /**
     * Returns all movies this actor acted in.
     * 
     * @return all movies
     */
    Iterable<Movie> getMovies();

    /**
     * Returns the specifc role an actor had in a movie or null if actor 
     * didn't have a role in the movie.
     * 
     * @param inMovie the movie to get role for
     * @return the role or null
     */
    Role getRole( Movie inMovie );
}
