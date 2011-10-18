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

package org.amanzi.neo.model.distribution.types.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class StringDistributionTest extends AbstractNeoServiceTest {
    
    private static final INodeType DEFAULT_NODE_TYPE = DistributionNodeTypes.AGGREGATION_BAR;
    
    private static final String DEFAULT_PROPERTY_NAME = "default_property_name";
    
    private static final String DEFAULT_MODEL_NAME = "model_name";

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
        clearServices();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutModel() throws Exception {
        new StringDistribution(null, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutNodeType() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        new StringDistribution(model, null, DEFAULT_PROPERTY_NAME);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutPropertyName() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        new StringDistribution(model, DEFAULT_NODE_TYPE, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithEmptyPropertyName() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        new StringDistribution(model, DEFAULT_NODE_TYPE, StringUtils.EMPTY);
    }
    
    @Test
    public void checkResultsOfCreation() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        IDistribution distribution = new StringDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        
        assertEquals("Unexpected name of Distribution", StringDistribution.STRING_DISTRIBUTION_NAME, distribution.getName());
        assertEquals("Unexpected class of Distribution", String.class, distribution.getClass());
        assertEquals("Unexpected NodeType of Distribution", DEFAULT_NODE_TYPE, distribution.getNodeType());
        assertNotNull("Initially Range of Distribution should not be null", distribution.getRanges());
        assertTrue("Initially ranges of Distribution should be empty", distribution.getRanges().isEmpty());
        assertTrue("Initially count of Distribution should be zero", distribution.getCount() == 0);
    }
    
    /**
     * Creates mocked DistributionalModel
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private IDistributionalModel getDistributionalModel() {
        IDistributionalModel model = mock(IDistributionalModel.class);
        
        Class clazz = String.class;
        when(model.getPropertyClass(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(clazz);
        when(model.getName()).thenReturn(DEFAULT_MODEL_NAME);
        
        return model;
    }

}
