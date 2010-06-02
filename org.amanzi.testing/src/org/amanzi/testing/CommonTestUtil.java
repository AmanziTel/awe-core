/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.testing;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * <p>
 *Common test utils
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CommonTestUtil {
    private static final String AMANZI_STR = ".amanzi";
    private static final String USER_HOME = "user.home";
    private static LuceneIndexService lucene=null;
    private final String databaseDir;
    private final String mainDirectory;
    private  GraphDatabaseService neo;
    public CommonTestUtil(String databaseName, String mainDir) {
        File dir = new File(CommonTestUtil.getUserHome());
        dir = new File(dir, AMANZI_STR);
        dir = new File(dir, mainDir);
        this.mainDirectory = dir.getPath();
        dir = new File(mainDirectory, databaseName);
        this.databaseDir = dir.getPath();
        neo=null;
    }
    /**
     * Get name of data base directory.
     * (Create directory if it not exists)
     *
     * @return String
     */
    public  String getMainDirectory(boolean create){
        new File(databaseDir).mkdirs();
        return mainDirectory;
    }
    /**
     * Get name of data base directory.
     * (Create directory if it not exists)
     *
     * @return String
     */
    public  String getDbDirectoryName(){
        new File(databaseDir).mkdirs();
        return databaseDir;
    }
    /**
     * Gets neo service.
     * @return EmbeddedGraphDatabase
     */
    public GraphDatabaseService getNeo(){
        if (neo == null){
            neo = new EmbeddedGraphDatabase(getDbDirectoryName());
        }
        return neo;
    }
    /**
     * Shutdown database service.
     */
    public void shutdownNeo() {
        if (lucene!=null){
            lucene.shutdown();
            lucene=null;
        }
        if(neo!=null){
            neo.shutdown();
            neo = null;
        }
    }
    
    /**
     * Clear directory.
     * 
     * @param directory File (for clear)
     */
public static void clearDirectory(File directory){
    if(directory.exists()){
        for(File file : directory.listFiles()){
            if(file.isDirectory()){
                clearDirectory(file);
            }
            file.delete();
        }
    }
}

public  LuceneIndexService getIndex(){
    if (lucene==null){
        lucene= new LuceneIndexService(getNeo());
    }
    return lucene;
}
/**
 * Get name of %USER_HOME% directory.
 *
 * @return String
 */
public static String getUserHome() {
    return System.getProperty(USER_HOME);
}
}
