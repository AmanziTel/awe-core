package org.neo4j.apps.imdb.parser;

import java.io.IOException;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.util.index.IndexService;
import org.neo4j.util.index.LuceneIndexService;

public class InjectImdbData
{
    public static ImdbReader getImdbReader( NeoService neo, 
        IndexService indexService )
    {
        // TODO: return your implementation of the ImdbReader
        return new ExampleImdbReader( neo, indexService );
    }
    
    public void create_db(String filename, String targetDir){

		NeoService neo = null;
		IndexService indexService = null;
		try
		{
			//String dir = "/home/amabdelsalam/workspace/org.jruby/lib/ruby/gems/1.8/gems/neo4j-0.2.1/examples/imdb/data";
			System.out.println("1");
            neo = new EmbeddedNeo( targetDir );
            System.out.println("2");
            indexService = new LuceneIndexService( neo );
            System.out.println("3");
            ImdbParser parser = new ImdbParser( 
                getImdbReader( neo, indexService ) );
            System.out.println("4");
            // inject movies
            parser.parseMovies( filename );
            System.out.println("5");
			// inject actors
			//parser.parseActors( dir + "/" + rawdata_filename );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if ( indexService != null )
			{
				indexService.shutdown();                               
			}
			if ( neo != null )
			{
				neo.shutdown();
			}
		}

	}

    public static void main( String[] args ) throws IOException
    {
//    	InjectImdbData iid = new InjectImdbData();
//		iid.create_db();
//        NeoService neo = null;
//        IndexService indexService = null;
//        try
//        {
//        	String dir = "/home/amabdelsalam/workspace/org.jruby/lib/ruby/gems/1.8/gems/neo4j-0.2.1/examples/imdb/data";
//            neo = new EmbeddedNeo( "var/neo" );
//            indexService = new LuceneIndexService( neo );
//            ImdbParser parser = new ImdbParser( 
//                getImdbReader( neo, indexService ) );
//            // inject movies
//            parser.parseMovies( dir + "/test-movies.list" );
//            // inject actors
//            parser.parseActors( dir + "/test-actors.list" );
//        }
//        finally
//        {
//            if ( indexService != null )
//            {
//                indexService.shutdown();                               
//            }
//            if ( neo != null )
//            {
//                neo.shutdown();
//            }
//        }
    }
}
