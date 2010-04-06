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

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.loader.UTRANLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.xml.sax.SAXException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class EriccsonTopologyTest extends AbstractLoaderTest {
    private static final String DATA_SAVER_DIR = "utran_data";

    private String dataDirectory;

    /**
     * Create new main directory.
     */
    private void initEmptyDataDirectory() {
        File dir = new File(getUserHome());
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(dir, AMANZI_STR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(dir, DATA_SAVER_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dataDirectory = dir.getPath();
    }

    private Node etalonGis;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        clearDbDirectory();
        initEmptyDataDirectory();
    }
    /**
     * Tests load correct data base.
     * @throws SAXException 
     * @throws TransformerConfigurationException 
     */
    @Test
    public void testCorrectLoading()throws IOException, TransformerConfigurationException, SAXException{
        UTRANLoader loader = initDataBase();
        assertLoader(loader);
    }
    /**
     * Execute after even test. Clear data base.
     */
    @After
    public void finishOne() {
        doFinish();
        clearDataDirectory();
    }

    /**
     * Initialize loader.
     * 
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     * @throws SAXException 
     * @throws TransformerConfigurationException 
     */
    private UTRANLoader initDataBase() throws IOException, TransformerConfigurationException, SAXException {
        initProjectService();
        String fileNameSmall = "utran.xml";
        String fileName = new File(dataDirectory, fileNameSmall).getPath();
        etalonGis = generateEtalonNetwork("etalonUtran",fileName);
        DataGenerateManager.generateEriccsonTopology(fileName,etalonGis);
        UTRANLoader loader = new UTRANLoader(fileName, fileNameSmall, null, initIndex(), getNeo());
        loader.setLimit(5000);
//        loader.run(new NullProgressMonitor());
        return loader;
    }




    private Node generateEtalonNetwork(String networkName, String fileName) {
        return DataGenerateManager.createEtalonNetwork(networkName,fileName,getNeo());
    }

    /**
     *finish
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

    private void clearDataDirectory() {
        File dir = new File(dataDirectory);
        if (dir.exists()) {
            clearDirectory(dir);
        }
    }
}
