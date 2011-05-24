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

package org.amanzi.awe.afp.testing;

import org.amanzi.awe.afp.testing.engine.internal.AfpEngineTest;
import org.amanzi.awe.afp.testing.model.AfpModelTest;
import org.amanzi.awe.afp.wizards.good.FrequenciesListUtils;
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
@SuiteClasses({ AfpEngineTest.class,
                AfpModelTest.class,
                FrequenciesListUtils.class})
public class AFPTestSuite {

}
