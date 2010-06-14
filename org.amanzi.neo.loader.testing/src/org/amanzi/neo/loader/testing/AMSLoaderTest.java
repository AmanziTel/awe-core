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
import org.amanzi.neo.loader.AMSLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for AMS loader.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class AMSLoaderTest extends AbstractLoaderTest {
    
    private static final String DATA_SAVER_DIR = "neo_calls";
    
    protected static final int INDIVIDUAL = 0;
    protected static final int GROUP = 1;
    protected static final int TSM = 2;
    protected static final int SDS = 3;
    protected static final int ATTACH = 4;
    protected static final int EMERGENCY = 5;
    protected static final int HO_CC = 6;
    
    private String dataDirectory;
    
    /**
     * @return Returns the dataDirectory.
     */
    public String getDataDirectory() {
        return dataDirectory;
    }
    
    /**
     * Create new main directory.
     */
    protected void initEmptyDataDirectory(){
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
        AMSLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
        assertLoader(loader);
        
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectIndividualLoading()throws IOException{
        AMSLoader loader = initDataBase("ind_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectGroupLoading()throws IOException{
        AMSLoader loader = initDataBase("group_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectTSMLoading()throws IOException{
        AMSLoader loader = initDataBase("tsm_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectSDSLoading()throws IOException{
        AMSLoader loader = initDataBase("sds_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectItsiLoading()throws IOException{
        AMSLoader loader = initDataBase("itsi_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
    private AMSLoader initDataBase(String aTestKey) throws IOException {
        generateDataFiles(aTestKey);
        initProjectService();
        AMSLoader loader = new AMSLoader(dataDirectory, "test", "test network", getNeo());
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
    private void generateDataFiles(String aTestKey) throws IOException {
        List<Integer> params = parceStringToIntegerList(getProperty("test_loader.gen_params."+aTestKey));
        IDataGenerator generator;
        Integer amsType = params.get(0);
        switch (amsType) {
        case INDIVIDUAL:
            generator = DataGenerateManager.getIndividualAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case GROUP:
            generator = DataGenerateManager.getGroupAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5),params.get(6));
            break;
        case TSM:
            generator = DataGenerateManager.getTSMMessagesGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case SDS:
            generator = DataGenerateManager.getSDSMessagesGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case ATTACH:
            generator = DataGenerateManager.getItsiAttachGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        default:
            throw new IllegalArgumentException("Unknoun AMS data type "+amsType+".");
        }        
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

    protected void clearDataDirectory() {
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
