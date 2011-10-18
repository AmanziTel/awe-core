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

package org.amanzi.neo.model.distribution.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.impl.DistributionManager.DistributionManagerException;
import org.amanzi.neo.model.distribution.types.impl.StringDistribution;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests on Distribution Manager
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionManagerTest extends AbstractNeoServiceTest {
    
    private static final INodeType DEFAULT_NODE_TYPE = DistributionNodeTypes.AGGREGATION_BAR;
    
    private static final String DEFAULT_PROPERTY_NAME = "default_property_name";
    
    private static final int STRING_DISTRIBUTIONS_NUMBER = 1;
    
    private static final String DEFAULT_MODEL_NAME = "model_name";
    
    private static DistributionManager manager;

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
        clearServices();
        
        manager = DistributionManager.getManager();
    }

    @Test(expected = DistributionManagerException.class)
    public void tryToCreateCDFChartForString() throws Exception {
        IDistributionalModel model = getDistributionalModel(String.class);
        
        manager.getDistributions(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, ChartType.CDF);
    }
    
    @Test
    public void checkSizeOfDistributionsForString() throws Exception {
        IDistributionalModel model = getDistributionalModel(String.class);
        
        List<IDistribution> result = manager.getDistributions(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, ChartType.getDefault());
        
        assertEquals("Unexpected size of Distributions created for String type", STRING_DISTRIBUTIONS_NUMBER, result.size());
    }
    
    @Test
    public void checkStringDistributionsSize() throws Exception {
        IDistributionalModel model = getDistributionalModel(String.class);
        
        List<IDistribution> result = manager.getDistributions(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, ChartType.getDefault());
        
        IDistribution stringDistribution = result.get(0);
        
        assertEquals("Unexpected type of Distribution for String type", StringDistribution.class, stringDistribution.getClass());
    }
    
    @Test
    public void checkCacheForStringDistribution() throws Exception {
        IDistributionalModel model = getDistributionalModel(String.class);
        
        List<IDistribution> result = manager.getDistributions(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, ChartType.getDefault());
        IDistribution firstDistribution = result.get(0);
        
        result = manager.getDistributions(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, ChartType.getDefault());
        IDistribution secondDistribution = result.get(0);
        
        assertSame("Distributions should be same", firstDistribution, secondDistribution);
    }
    
    /**
     * Creates mocked DistributionalModel
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private IDistributionalModel getDistributionalModel(Class clazz) {
        IDistributionalModel model = mock(IDistributionalModel.class);
        
        when(model.getPropertyClass(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(clazz);
        when(model.getName()).thenReturn(DEFAULT_MODEL_NAME);
        
        return model;
    }

}
