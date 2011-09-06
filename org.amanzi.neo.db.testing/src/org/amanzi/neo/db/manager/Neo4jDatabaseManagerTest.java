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

package org.amanzi.neo.db.manager;

import static org.junit.Assert.fail;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests on DatabaseManager Test
 * @author gerzog
 * @since 1.0.0
 */
public class Neo4jDatabaseManagerTest {
    
    private final static Logger LOGGER = Logger.getLogger(Neo4jDatabaseManagerTest.class);
    
    private final static String USER_HOME = "user.home";
    
    private final static String[] TEST_DIRECTORIES = new String[] {".amanzi", "test"}; 
    
    private static String dbLocation = null;
   
    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        clearDbLocation(new File(getDbLocation()));
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        clearDbLocation(new File(getDbLocation()));
    }
    
    private static String getDbLocation() {
        if (dbLocation == null) {
            String userHome = System.getProperty(USER_HOME);
            
            File testHomeFile = new File(userHome);
            for (String subDir : TEST_DIRECTORIES) {
                testHomeFile = new File(testHomeFile, subDir);
            }
            testHomeFile.mkdirs();
            
            dbLocation = testHomeFile.getAbsolutePath();
            
            LOGGER.info("Test directory initialized on <" + dbLocation + ">");
        }
        
        return dbLocation;
    }
    
    private static void clearDbLocation(File dbLocation) {
        if (dbLocation.exists()) {
            for (File subFile : dbLocation.listFiles()) {
                if (subFile.isDirectory()) {
                    clearDbLocation(subFile);
                } else {
                    subFile.delete();
                }
            }
            dbLocation.delete();
        }
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
