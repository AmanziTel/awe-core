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
import java.util.List;

import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.NokiaTopologyLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Tests for NokiaTopologyLoader
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaTopologyLoaderTest extends AbstractLoaderTest{
    
private static final String DATA_SAVER_DIR = "nokia_data";
    
    private String dataDirectory;
    
    /**
     * Create new main directory.
     */
    private void initEmptyDataDirectory(){
        File dir = new File(getUserHome());
        if(!dir.exists()){
            dir.mkdir();
        }
        dir = new File(dir,AMANZI_STR);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dir = new File(dir,DATA_SAVER_DIR);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dataDirectory = dir.getPath();
    }
    
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
     * Tests load empty data base.
     */
    @Test
    public void testEmptyLoading()throws IOException{
        NokiaTopologyLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
        assertLoader(loader);
        
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectLoading()throws IOException{
        NokiaTopologyLoader loader = initDataBase(BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
    private NokiaTopologyLoader initDataBase(String aTestKey) throws IOException {
        String dbName = getDbName(aTestKey);
        generateDataFiles(aTestKey,dbName);
        initProjectService();        
        String fileName = new File(dataDirectory,dbName).getPath();
        NokiaTopologyLoader loader = new NokiaTopologyLoader(fileName, "nokia_network", null,initIndex(),getNeo());
        loader.setLimit(5000);
        loader.run(new NullProgressMonitor());
        return loader;
    }
    
    /**
     * Generate data for gets statistics.
     *
     * @param aTestKey
     * @return String
     * @throws IOException (problem in data generation)
     */
    private void generateDataFiles(String aTestKey, String aFileName) throws IOException {
        List<Integer> params = parceStringToIntegerList(getProperty("test_loader.gen_params."+aTestKey));
        List<Float> paramsLoc = parceStringToFloatList(getProperty("test_loader.gen_params.loc."+aTestKey));
        IDataGenerator generator = DataGenerateManager.getNokiaTopologyGenerator(dataDirectory, aFileName, params.get(0), params.get(1), params.get(2), params.get(3),
                new Float[]{paramsLoc.get(0),paramsLoc.get(1)}, new Float[]{paramsLoc.get(2),paramsLoc.get(3)});
        generator.generate();
    }
    
    /**
     * Execute after even test. 
     * Clear data base.
     */
    @After
    public void finishOne(){
        doFinish();
        clearDataDirectory();
    }

    private void clearDataDirectory() {
        File dir = new File(dataDirectory);
        if(dir.exists()){
            clearDirectory(dir);
        }
    }    

    /**
     *finish 
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

}
