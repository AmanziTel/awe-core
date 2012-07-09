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

package org.amanzi.neo.services.impl.statistics.internal;

import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.exceptions.StatisticsConversionException;
import org.amanzi.neo.services.impl.statistics.internal.PropertyVault.ClassType;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyVaultTest extends AbstractMockitoTest {

    /** int LIMIT field */
    private static final int LIMIT = 100;

    /** int TEST_NUMBER_OF_VALUES field */
    private static final int TEST_NUMBER_OF_VALUES = 3;

    /** String UNEXPECTED_SIZE_OF_VALUES_MESSAGE field */
    private static final String UNEXPECTED_SIZE_OF_VALUES_MESSAGE = "unexpected size of values";

    /** String UNEXPECTED_CLASS_MESSAGE field */
    private static final String UNEXPECTED_CLASS_MESSAGE = "Unexpected class";

    private static final String TEST_VALUE = "value";

    private static final String TEST_PROPERTY = "property";

    private static final int[] COUNTS = {1, 1, 2, 2};

    private static final String[] STRING_VALUES = new String[] {"string1", "string2", "string3", "string3"};

    private static final Integer[] INTEGER_VALUES = new Integer[] {1, 2, 3, 3};

    private static final Long[] LONG_VALUES = new Long[] {1l, 2l, 3l, 3l};

    private static final Double[] DOUBLE_VALUES = new Double[] {1d, 2d, 3d, 3d};

    private static final Object[] INT_TO_STRING_VALUES = new Object[] {9, 10, "A"};

    private static final Object[] LONG_TO_STRING_VALUES = new Object[] {9l, 10l, "A"};

    private static final Object[] DOUBLEE_TO_STRING_VALUES = new Object[] {9d, 10d, "A"};

    private static final Object[] INT_TO_LONG_VALUES = new Object[] {1, 2, 3l};

    private static final Object[] INT_TO_DOUBLE_VALUES = new Object[] {1, 2, 3.5};

    private PropertyVault vault;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        vault = new PropertyVault(TEST_PROPERTY);
    }

    @Test
    public void testCheckIsChanged() throws ServiceException {
        vault.setChanged(false);

        vault.index(TEST_VALUE);

        assertTrue("vault should be changed", vault.isChanged());
    }

    @Test
    public void testCheckClassInitialization() throws ServiceException {
        spyVault();

        vault.index(TEST_VALUE);

        verify(vault).defineClass(TEST_VALUE);
    }

    @Test
    public void testCheckStringStatistics() throws ServiceException {
        for (Object o : STRING_VALUES) {
            vault.index(o);
        }

        assertEquals(UNEXPECTED_CLASS_MESSAGE, String.class.getName(), vault.getClassName());
        assertValues(STRING_VALUES);
    }

    @Test
    public void testCheckIntegerStatistics() throws ServiceException {
        for (Object o : INTEGER_VALUES) {
            vault.index(o);
        }

        assertEquals(UNEXPECTED_CLASS_MESSAGE, Integer.class.getName(), vault.getClassName());
        assertValues(INTEGER_VALUES);
    }

    @Test
    public void testCheckLongStatistics() throws ServiceException {
        for (Object o : LONG_VALUES) {
            vault.index(o);
        }

        assertEquals(UNEXPECTED_CLASS_MESSAGE, Long.class.getName(), vault.getClassName());
        assertValues(LONG_VALUES);
    }

    @Test
    public void testCheckFloatStatistics() throws ServiceException {
        for (Object o : DOUBLE_VALUES) {
            vault.index(o);
        }

        assertEquals(UNEXPECTED_CLASS_MESSAGE, Double.class.getName(), vault.getClassName());
        assertValues(DOUBLE_VALUES);
    }

    @Test
    public void testCheckLimit() throws ServiceException {
        for (int i = 0; i < (LIMIT - 1); i++) {
            vault.index(TEST_VALUE + i);
        }

        assertEquals("unexpected size of values set", LIMIT - 1, vault.getValues().size());

        vault.index(TEST_VALUE + LIMIT);
        vault.index(TEST_VALUE + (LIMIT + 1));

        assertEquals("unexpected size of values set", 0, vault.getValues().size());
    }

    @Test
    public void testCheckActionsOnClassChangingIntToString() throws ServiceException {
        spyVault();

        for (Object o : INT_TO_STRING_VALUES) {
            vault.index(o);
        }

        verify(vault).updateToNewClass(ClassType.STRING);
    }

    @Test
    public void testCheckActionsOnClassChangingLongToString() throws ServiceException {
        spyVault();

        for (Object o : LONG_TO_STRING_VALUES) {
            vault.index(o);
        }

        verify(vault).updateToNewClass(ClassType.STRING);
    }

    @Test
    public void testCheckActionsOnClassChangingFloatToString() throws ServiceException {
        spyVault();

        for (Object o : DOUBLEE_TO_STRING_VALUES) {
            vault.index(o);
        }

        verify(vault).updateToNewClass(ClassType.STRING);
    }

    @Test
    public void testCheckActionsOnClassChangingIntToLong() throws ServiceException {
        spyVault();

        for (Object o : INT_TO_LONG_VALUES) {
            vault.index(o);
        }

        verify(vault).updateToNewClass(ClassType.LONG);
    }

    @Test
    public void testCheckActionsOnClassChangingIntToFloat() throws ServiceException {
        spyVault();

        for (Object o : INT_TO_DOUBLE_VALUES) {
            vault.index(o);
        }

        verify(vault).updateToNewClass(ClassType.DOUBLE);
    }

    @Test(expected = StatisticsConversionException.class)
    public void testCheckConversionException() throws ServiceException {
        for (Object o : STRING_VALUES) {
            vault.index(o);
        }

        for (Object o : INTEGER_VALUES) {
            vault.index(o);
        }
    }

    private void spyVault() {
        vault = spy(vault);
    }

    private void assertValues(final Object[] values) {
        assertEquals(UNEXPECTED_SIZE_OF_VALUES_MESSAGE, TEST_NUMBER_OF_VALUES, vault.getValues().size());
        for (int i = 0; i < values.length; i++) {
            Object o = values[i];
            assertEquals("unexpected size of object <" + o + ">", COUNTS[i], vault.getValueCount(o));
        }
    }

}
