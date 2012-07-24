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

package org.amanzi.neo.services.impl.statistics.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsVaultTest extends AbstractMockitoTest {

    private static final int TEST_NUMBER_OF_PROPERTIES = 10;

    private static final INodeType TEST_NODE_TYPE = new INodeType() {

        @Override
        public String getId() {
            return "test";
        }
    };

    private static final String TEST_PROPERTY = "TEST";

    private static final String TEST_VALUE = "test";

    private StatisticsVault statisticsVault;

    private NodeTypeVault nodeTypeVault;

    private NodeTypeVault nodeTypeVault2;

    @Before
    public void setUp() {
        nodeTypeVault = mock(NodeTypeVault.class);
        nodeTypeVault2 = mock(NodeTypeVault.class);

        List<NodeTypeVault> vaults = new ArrayList<NodeTypeVault>();
        vaults.add(nodeTypeVault);
        vaults.add(nodeTypeVault2);

        statisticsVault = spy(new StatisticsVault());
        when(statisticsVault.getNodeTypeVaule(TEST_NODE_TYPE)).thenReturn(nodeTypeVault);
        when(statisticsVault.getAllNodeTypeVaults()).thenReturn(vaults);

    }

    @Test
    public void testCheckCountIncreased() throws ServiceException {
        int previousCount = statisticsVault.getCount();

        for (int i = 0; i < TEST_NUMBER_OF_PROPERTIES; i++) {
            statisticsVault.indexElement(TEST_NODE_TYPE, getTestPropertyMap());
        }

        assertEquals("Count increased incorrect", previousCount + TEST_NUMBER_OF_PROPERTIES, statisticsVault.getCount());
    }

    @Test
    public void testCheckActivityOnCountWithNodeType() {
        statisticsVault.getCount(TEST_NODE_TYPE);

        verify(nodeTypeVault).getCount();
    }

    @Test
    public void testCheckActivityOnIndexProperty() throws ServiceException {
        Map<String, Object> properties = getTestPropertyMap();
        statisticsVault.indexElement(TEST_NODE_TYPE, properties);

        verify(nodeTypeVault).indexElement(properties);
    }

    @Test
    public void testCheckActivityOnGetProperties() {
        statisticsVault.getPropertyNames();

        verify(nodeTypeVault).getPropertyNames();
        verify(nodeTypeVault2).getPropertyNames();
    }

    @Test
    public void testCheckActivityOnGetPropertiesWithNodeType() {
        statisticsVault.getPropertyNames(TEST_NODE_TYPE);

        verify(nodeTypeVault).getPropertyNames();
    }

    @Test
    public void testCheckIsChanged() throws ServiceException {
        statisticsVault.setChanged(false);

        statisticsVault.indexElement(TEST_NODE_TYPE, getTestPropertyMap());

        assertTrue("statistics should be changed", statisticsVault.isChanged());
    }

    @Test
    public void testCheckGetValues() {
        statisticsVault.getValues(TEST_NODE_TYPE, TEST_PROPERTY);

        verify(nodeTypeVault).getValues(TEST_PROPERTY);
    }

    @Test
    public void testCheckGetValueCount() {
        statisticsVault.getValueCount(TEST_NODE_TYPE, TEST_PROPERTY, TEST_VALUE);

        verify(nodeTypeVault).getValueCount(TEST_PROPERTY, TEST_VALUE);
    }

    private Map<String, Object> getTestPropertyMap() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(TEST_PROPERTY, TEST_VALUE);

        return result;
    }
}
