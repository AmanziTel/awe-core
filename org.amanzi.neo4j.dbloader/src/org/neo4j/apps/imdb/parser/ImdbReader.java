package org.neo4j.apps.imdb.parser;

/**
 * Reads events from the {@link ImdbParser}. The {@link ImdbParser} will start
 * a new transaction calling {@link #beginTransaction()} then, depending on the
 * type of file being parsed (actor or movie list), call the
 * {@link #newActor(String, String[])} or {@link #newMovie(String, int)}.
 * Finally the transaction will be closed and commited via a call to the
 * {@link #endTransaction()}.
 */
public interface ImdbReader
{
    /**
     * Creates a new movie with specified <code>title</code> and
     * <code>year</code>.
     * <p>
     * Use the indexing service to index each movie title for fast access in 
     * the {@link #newActor(String, MovieRole[])} method.
     * <p>
     * <code>indexService.index( movieNode, "movie", title );</code>
     * 
     * @param title
     *            title of the move
     * @param year
     *            year the movie was released
     */
    void newMovie( String title, int year );

    /**
     * Creates a new actor specifying what movies the actor acted in.
     * <p>
     * Use the indexing service to index actor name:
     * <p>
     * <code>indexService.index( actorNode, "name", name );</code>
     * 
     * @param name
     *            name of actor
     * @param movieRoles
     *            titles and (optinal) role of all the movies the actor acted in
     */
    void newActor( String name, MovieRole[] movieRoles );

    /**
     * Starts a new transaction:
     * <p>
     * <code>Transaction tx = neoService.beginTx())</code>.
     */
    void beginTransaction();

    /**
     * Commits the current transaction (first call <code>tx.success()</code>
     * followed by <code>tx.finish()</code>.
     */
    void endTransaction();
}
