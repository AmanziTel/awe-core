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

import static org.junit.Assert.fail;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests on Distribution Manager
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionManagerTest extends AbstractNeoServiceTest {

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
        clearServices();
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
