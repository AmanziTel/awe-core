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

package org.amanzi.awe.statistics;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <p>
 * TestsRunner
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.amanzi.awe.statistics.enumeration.PeriodTests.class,
    org.amanzi.awe.statistics.model.StatisticsModelTests.class,
    org.amanzi.awe.statistics.service.StatisticsServiceTests.class,
    org.amanzi.awe.statistics.entities.impl.DimensionTests.class,
    org.amanzi.awe.statistics.entities.impl.StatisticsLevelTests.class,
    org.amanzi.awe.statistics.entities.impl.AggregatedStatisticsTests.class,
    org.amanzi.awe.statistics.entities.impl.StatisticsGroupTests.class,
    org.amanzi.awe.statistics.entities.impl.StatisticsRowTests.class,
    org.amanzi.awe.statistics.entities.impl.StatisticsCellTests.class
    })
public class StatisticsTestsSuite {

}
