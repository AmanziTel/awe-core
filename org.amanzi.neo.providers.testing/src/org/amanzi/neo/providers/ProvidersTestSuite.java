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

package org.amanzi.neo.providers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({org.amanzi.neo.providers.context.ProviderContextImplIntegrationTest.class,
        org.amanzi.neo.providers.context.ProviderContextImplTest.class,
        org.amanzi.neo.providers.impl.ProjectModelProviderIntegrationTest.class,
        org.amanzi.neo.providers.impl.ProjectModelProviderTest.class,
        org.amanzi.neo.providers.impl.PropertyStatisticsModelProviderTest.class,
        org.amanzi.neo.providers.impl.internal.AbstractDatasetModelProviderTest.class,
        org.amanzi.neo.providers.impl.internal.AbstractNamedModelProviderTest.class,
        org.amanzi.neo.providers.impl.internal.AbstractModelProviderTest.class})
public class ProvidersTestSuite {

}
