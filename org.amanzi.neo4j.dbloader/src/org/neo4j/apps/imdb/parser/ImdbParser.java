package org.neo4j.apps.imdb.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>ImdbParser</code> can parse the movie and actor/actress lists from
 * the imdb text data (http://www.imdb.com/interfaces). It uses an
 * {@link ImdbReader} forwarding the parsed information.
 */
public class ImdbParser
{
    private final ImdbReader reader;

    /**
     * Create a new Imdb parser.
     * @param reader
     *            reader this parser will use to forward events to
     */
    public ImdbParser( ImdbReader reader )
    {
        if ( reader == null )
        {
            throw new IllegalArgumentException( "Null ImdbReader" );
        }
        this.reader = reader;
    }

    /**
     * Parsers a tab-separated movie list file, each line containing a movie
     * title and the year the movie was released.
     * @param file
     *            name of movie list file
     * @throws IOException
     *             if unable to open the movie list file
     */
    public void parseMovies( String file ) throws IOException
    {
        // ignores all tv series
        if ( file == null )
        {
            throw new IllegalArgumentException( "Null movie file" );
        }
        BufferedReader fileReader = new BufferedReader( 
            new FileReader( file ) );
        String line = fileReader.readLine();
        reader.beginTransaction();
        try
        {
            int count = 0;
            while ( line != null )
            {
                int yearSep = line.indexOf( '\t' );
                if ( yearSep > 0 )
                {
                    String title = line.substring( 0, yearSep ).trim();
                    String yearString = line.substring( yearSep ).trim();
                    if ( yearString.length() > 4 )
                    {
                        yearString = yearString.substring( 0, 4 );
                    }
                    if ( yearString.length() == 0
                        || yearString.charAt( 0 ) == '?'
                        || title.contains( "{" ) || title.startsWith( "\"" ) )
                    {
                        line = fileReader.readLine();
                        continue;
                    }
                    int year = Integer.parseInt( yearString );
                    reader.newMovie( title, year );
                    count++;
                    if ( count % 5000 == 0 )
                    {
                        reader.endTransaction();
                        reader.beginTransaction();
                    }
                }
                line = fileReader.readLine();
            }
            System.out.println( "\n" + count + " movies parsed and injected." );
        }
        finally
        {
            reader.endTransaction();
        }
    }

    /**
     * Parsers a tab-separated actors list file. A line begins with actor name
     * then followed by a tab and a movie title the actor acted in. Additional
     * movies the current actor acted in are found on the following line that
     * starts with a tab followed by the movie title.
     * @param file
     *            name of actor list file
     * @throws IOException
     *             if unable to open actor list file
     */
    public void parseActors( String file ) throws IOException
    {
        // ignores all tv series
        if ( file == null )
        {
            throw new IllegalArgumentException( "Null actor file" );
        }
        BufferedReader fileReader = new BufferedReader( new FileReader( file ) );
        String line = fileReader.readLine();
        reader.beginTransaction();
        try
        {
            String currentActor = null;
            List<MovieRole> movies = new ArrayList<MovieRole>();
            int count = 0;
            while ( line != null )
            {
                int actorSep = line.indexOf( '\t' );
                if ( actorSep >= 0 )
                {
                    String actor = line.substring( 0, actorSep ).trim();
                    if ( !actor.equals( "" ) )
                    {
                        if ( movies.size() > 0 )
                        {
                            reader.newActor( currentActor, movies
                                .toArray( new MovieRole[movies.size()] ) );
                            movies.clear();
                        }
                        currentActor = actor;
                    }
                    String title = line.substring( actorSep ).trim();
                    if ( title.length() == 0 || title.contains( "{" )
                        || title.startsWith( "\"" ) || title.contains( "????" ) )
                    {
                        line = fileReader.readLine();
                        continue;
                    }
                    int characterStart = title.indexOf( '[' );
                    int characterEnd = title.indexOf( ']' );
                    String character = null;
                    if ( characterStart > 0 && characterEnd > characterStart )
                    {
                        character = title.substring( characterStart + 1,
                            characterEnd );
                    }
                    int creditStart = title.indexOf( '<' );
                    int creditEnd = title.indexOf( '>' );
                    String credit = null;
                    if ( creditStart > 0 && creditEnd > creditStart )
                    {
                        credit = title.substring( creditStart + 1, creditEnd );
                    }
                    if ( characterStart > 0 )
                    {
                        title = title.substring( 0, characterStart ).trim();
                    }
                    else if ( creditStart > 0 )
                    {
                        title = title.substring( 0, creditStart ).trim();
                    }
                    int spaces = title.indexOf( "  " );
                    if ( spaces > 0 )
                    {
                        if ( title.charAt( spaces - 1 ) == ')'
                            && title.charAt( spaces + 2 ) == '(' )
                        {
                            title = title.substring( 0, spaces ).trim();
                        }
                    }
                    movies.add( new MovieRole( title, character ) );
                    count++;
                    if ( count % 5000 == 0 )
                    {
                        reader.endTransaction();
                        reader.beginTransaction();
                    }
                }
                line = fileReader.readLine();
            }
            System.out.println( count + " actors including acts in "
                + "relationship parsed and injected." );
        }
        finally
        {
            reader.endTransaction();
        }
    }
}
