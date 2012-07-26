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

package org.amanzi.awe.statistics.entities.impl;

import org.amanzi.awe.statistics.AbstractMockedTests;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.junit.Before;

/**
 * <p>
 * abstractEntityTest
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AbstractEntityTest extends AbstractMockedTests {

    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * init services
     * 
     * @param statisticsService
     */
    public static void initServices(StatisticsService statisticsService) {
        AbstractEntity.setStatisticsService(statisticsService);
    }

}
