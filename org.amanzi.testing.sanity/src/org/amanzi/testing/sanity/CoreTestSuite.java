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

package org.amanzi.testing.sanity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({
        org.amanzi.awe.scripting.ScriptingTests.class,
        org.amanzi.awe.statistics.StatisticsTestsSuite.class,
        org.amanzi.awe.ui.AWEUITests.class,
        org.amanzi.neo.db.testing.DbTestSuite.class, 
        org.amanzi.neo.CoreTestsSuite.class,
        org.amanzi.neo.loader.core.LoaderCoreTestSuite.class,
        org.amanzi.neo.loader.ui.LoaderUITestSuite.class,
        org.amanzi.neo.models.ModelsTestSuite.class,
        org.amanzi.neo.providers.ProvidersTestSuite.class,
        org.amanzi.neo.services.ServicesTestSuite.class
})
public class CoreTestSuite {

}
