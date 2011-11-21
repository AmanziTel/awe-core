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

package org.amanzi.neo.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <p>
 * Suite for unit tests
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({
		org.amanzi.neo.services.CorrelationServiceTest.class,
		org.amanzi.neo.services.NewDatasetServiceTest.class,
		org.amanzi.neo.services.NewNetworkServiceTest.class,
		org.amanzi.neo.services.NewStatisticsServiceTest.class,
		org.amanzi.neo.services.NodeTypeManagerTest.class,
		org.amanzi.neo.services.ProjectServiceTest.class,
		org.amanzi.neo.services.model.impl.CorrelationModelTest.class,
		org.amanzi.neo.services.model.impl.DataElementTest.class,
		org.amanzi.neo.services.model.impl.DriveModelTest.class,
		org.amanzi.neo.services.model.impl.NetworkModelTest.class,
		org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModelTest.class,
		org.amanzi.neo.services.model.impl.ProjectModelTest.class,
		org.amanzi.neo.services.testing.filters.FilterTest.class,
		org.amanzi.neo.services.testing.statistic.VaultTests.class,
		org.amanzi.neo.services.synonyms.ExportSynonymsServiceTest.class,
		org.amanzi.neo.services.synonyms.ExportSynonymsManagerTest.class,
		org.amanzi.neo.services.model.impl.SelectionModelTest.class,
		org.amanzi.neo.model.distribution.impl.DistributionModelTest.class,
		org.amanzi.neo.services.DistributionServiceTest.class,
		org.amanzi.neo.model.distribution.impl.DistributionManagerTest.class,
		org.amanzi.neo.model.distribution.types.impl.EnumeratedDistributionTest.class,
		org.amanzi.neo.model.distribution.types.impl.NumberDistributionTest.class,
		org.amanzi.neo.model.distribution.xml.DistributionXmlParserTest.class })
public class NeoServiceSuite {

}
