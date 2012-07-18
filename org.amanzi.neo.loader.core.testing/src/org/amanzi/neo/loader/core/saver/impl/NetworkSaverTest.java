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

package org.amanzi.neo.loader.core.saver.impl;

import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkSaverTest extends AbstractMockitoTest {

    private static final String NETWORK_NAME = "network";

    private NetworkSaver saver;

    private IConfiguration configuration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = mock(IConfiguration.class);
        when(configuration.getDatasetName()).thenReturn(NETWORK_NAME);

        saver = spy(new NetworkSaver());
    }

    @Test
    @Ignore
    public void testCheckNetworkCreationOnInitialization() {
        saver.init(configuration);

        verify(saver).createNetworkModel(NETWORK_NAME);
    }

}
