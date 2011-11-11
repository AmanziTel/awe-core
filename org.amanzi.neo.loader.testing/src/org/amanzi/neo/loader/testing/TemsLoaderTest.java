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

package org.amanzi.neo.loader.testing;

import java.io.IOException;

import org.amanzi.testing.LongRunning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * Test TEMSLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TemsLoaderTest extends AbstractLoaderTest{
    private static long loadTime;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void init() throws IOException {
    	clearDbDirectory();
    }

    
    
    /**
     * Tests load empty data base.
     */
    @Test
    public void testEmptyLoading()throws IOException{
//    	TEMSLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
//    	assertLoader(loader);        
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    @LongRunning
    public void testCorrectLoading()throws IOException{
//    	TEMSLoader loader = initDataBase(BUNDLE_KEY_CORRECT);
//    	assertLoader(loader);
    }
    
    /**
     * Tests time of load.
     */
    @Test
    @LongRunning
    public void testTimeLoading()throws IOException{
//    	initDataBase(BUNDLE_KEY_TIME);
//    	assertLoadTime(loadTime,BUNDLE_KEY_TIME);
    }
    
    /**
     * Tests load incorrect data bases.
     */
    @Ignore("Unknown reaction, need to be rewrited.")
    @Test
    @LongRunning
    public void testIncorrectLoading()throws IOException{
//    	initDataBase(BUNDLE_KEY_WRONG);
//    	assertLoadTime(loadTime,BUNDLE_KEY_WRONG);
    }

    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
//	private TEMSLoader initDataBase(String aTestKey) throws IOException {
//		initProjectService();
//		loadTime = System.currentTimeMillis();
//        TEMSLoader driveLoader = new TEMSLoader(getNeo(), getFileDirectory() + getDbName(aTestKey),"test",initIndex());
//        driveLoader.setLimit(100);
//        driveLoader.run(null);
//        driveLoader.printStats(true); // stats for this load
//        loadTime = System.currentTimeMillis() - loadTime;
//		return driveLoader;
//	}
	
	/**
     * Execute after even test. 
     * Clear data base.
     */
    @After
    public void finishOne(){
    	doFinish();
    }    

    /**
     *finish 
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

}
