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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.amanzi.neo.loader.TEMSLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.api.core.EmbeddedNeo;

/**
 * <p>
 * Test TEMSLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TemsLoaderTest {
    private static final long MAX_LOAD_TIME = 10 * 1000;
    protected static String filesDir = "files/tems/";
    protected static EmbeddedNeo neo;
    private static String filename = "test.FMT";
    private static long loadTime;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void init() throws IOException {
        neo = new EmbeddedNeo(NeoTestPlugin.getDefault().getDatabaseLocation());
        loadTime = System.currentTimeMillis();
        TEMSLoader driveLoader = new TEMSLoader(neo, filesDir + filename);
        driveLoader.setLimit(100);
        driveLoader.run(null);
        driveLoader.printStats(true); // stats for this load
        loadTime = System.currentTimeMillis() - loadTime;

    }

    @Test
    public void testLoadTime() {
        String message = String.format("Load time(ms) = %d, max time(ms) = %d", loadTime, MAX_LOAD_TIME);
        assertTrue(message, loadTime <= MAX_LOAD_TIME);
    }

    /**
     *finish
     */
    @AfterClass
    public static void finish() {
        neo.shutdown();
    }

}
