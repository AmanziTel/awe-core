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

import org.amanzi.awe.models.catalog.neo.CatalogSuite;
import org.amanzi.neo.db.testing.DbTestSuite;
import org.amanzi.neo.loader.core.saver.SaversSuite;
import org.amanzi.neo.services.NeoServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({ NeoServiceSuite.class,
                SaversSuite.class,
                DbTestSuite.class,
                CatalogSuite.class})
public class CoreTestSuite {

}
