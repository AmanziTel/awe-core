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

package org.amanzi.testing;

import org.amanzi.log4j.LogStarter;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        new LogStarter().earlyStartup();
    }

    protected void assertEquals(String message, Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
    }

    protected void assertNotNull(String message, Object object) {
        Assert.assertNotNull(message, object);
    }

    protected void assertNull(String message, Object object) {
        Assert.assertNull(message, object);
    }

    protected void assertTrue(String message, boolean condition) {
        Assert.assertTrue(message, condition);
    }

    protected void assertFalse(String message, boolean condition) {
        Assert.assertFalse(message, condition);
    }

}
