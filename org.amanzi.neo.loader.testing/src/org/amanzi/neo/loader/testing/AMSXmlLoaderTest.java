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
import java.util.List;

import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSXMLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Tests for AMSAmlLoader.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class AMSXmlLoaderTest extends AMSLoaderTest{
    
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
        AMSXMLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
        assertLoader(loader);
        
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectIndividualLoading()throws IOException{
        AMSXMLoader loader = initDataBase("ind_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectGroupLoading()throws IOException{
        AMSXMLoader loader = initDataBase("group_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectTSMLoading()throws IOException{
        AMSXMLoader loader = initDataBase("tsm_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectSDSLoading()throws IOException{
        AMSXMLoader loader = initDataBase("sds_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectItsiLoading()throws IOException{
        AMSXMLoader loader = initDataBase("itsi_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectEmergencyLoading()throws IOException{
        AMSXMLoader loader = initDataBase("emer_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectHoCcLoading()throws IOException{
        AMSXMLoader loader = initDataBase("ho_cc_"+BUNDLE_KEY_CORRECT);
        assertLoader(loader);
    }
    
    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
    private AMSXMLoader initDataBase(String aTestKey) throws IOException {
        generateDataFiles(aTestKey);
        initProjectService();
        AMSXMLoader loader = new AMSXMLoader(getDataDirectory(),null, "test", "test network", getNeo(), true);
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
        String dataDirectory = getDataDirectory();
        switch (amsType) {
        case INDIVIDUAL:
            generator = DataGenerateManager.getXmlIndividualAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case GROUP:
            generator = DataGenerateManager.getXmlGroupAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5),params.get(6));
            break;
        case TSM:
            generator = DataGenerateManager.getXmlTSMMessagesGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case SDS:
            generator = DataGenerateManager.getXmlSDSMessagesGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case ATTACH:
            generator = DataGenerateManager.getXmlItsiAttachGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
            break;
        case EMERGENCY:
            generator = DataGenerateManager.getXmlEmergencyAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5),params.get(6));
            break;
        case HO_CC:
            generator = DataGenerateManager.getXmlHoCcAmsGenerator(dataDirectory, params.get(1),params.get(2), params.get(3), params.get(4), params.get(5));
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

    /**
     *finish 
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

}
