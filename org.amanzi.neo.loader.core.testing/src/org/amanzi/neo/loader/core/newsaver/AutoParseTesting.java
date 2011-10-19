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

package org.amanzi.neo.loader.core.newsaver;

import org.amanzi.log4j.LogStarter;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vladislav_Kondratenko
 */
public class AutoParseTesting extends AbstractAWETest {
    private static Logger LOGGER = Logger.getLogger(AutoParseTesting.class);
    private final static String EMPTY_STRING = "";
    private final static String STRING_INT_RANDOM_VALUE = "5";
    private final static String STRING_INT_MAX_VALUE = String.valueOf(Integer.MAX_VALUE);
    private final static String STRING_INT_MIN_VALUE = String.valueOf(Integer.MIN_VALUE);
    private final static String STRING_FLOAT_RANDOM_VALUE = "5.5";
    private final static String STRING_DOUBLE_RANDOM_VALUE = "5.55555555";
    private final static String STRING_FLOAT_MIN_VALUE = String.valueOf(Float.MIN_VALUE);
    private final static String STRING_DOUBLE_MIN_VALUE = String.valueOf(Double.MIN_VALUE);
    private final static String STRING_FLOAT_MAX_VALUE = String.valueOf(Float.MAX_VALUE);
    private final static String STRING_DOUBLE_MAX_VALUE = String.valueOf(Double.MAX_VALUE);
    private final static String STRING_BOOLEAN_VALUE = "true";
    private final static String STRING_VALUE = "string value";
    private static final NewNetworkSaver saver = new NewNetworkSaver();
    private static Long startTime;

    @BeforeClass
    public static void BeforeSetup() {
        new LogStarter().earlyStartup();
        startTime = System.currentTimeMillis();
    }

    @AfterClass
    public static void AfterSetup() {
        new LogStarter().earlyStartup();
        LOGGER.info("AutoParseTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void parseDoubleTest() {
        LOGGER.info("Start parseDoubleTest");
        Object parsedValue = saver.autoParse(STRING_DOUBLE_RANDOM_VALUE);
        Assert.assertTrue("Expected Double object but was " + parsedValue.getClass(), parsedValue instanceof Double);
        Assert.assertEquals(parsedValue.toString().length(), STRING_DOUBLE_RANDOM_VALUE.length());
    }

    @Test
    public void parseFloatTest() {
        LOGGER.info("Start parseFloatTest");
        Object parsedValue = saver.autoParse(STRING_FLOAT_RANDOM_VALUE);
        Assert.assertTrue("Expected Float object but was " + parsedValue.getClass(), parsedValue instanceof Float);
        Assert.assertEquals(parsedValue.toString().length(), STRING_FLOAT_RANDOM_VALUE.length());
    }

    @Test
    public void parseIntegerTest() {
        LOGGER.info("Start parseFloatTest");
        Object parsedValue = saver.autoParse(STRING_INT_RANDOM_VALUE);
        Assert.assertTrue("Expected Integer object but was " + parsedValue.getClass(), parsedValue instanceof Integer);
        Assert.assertEquals(parsedValue.toString().length(), STRING_INT_RANDOM_VALUE.length());
    }

    @Test
    public void parseFloatMinValue() {
        LOGGER.info("Start parseFloatMinValue");
        Object parsedValue = saver.autoParse(STRING_FLOAT_MIN_VALUE);
        Assert.assertTrue("Expected Float object but was " + parsedValue.getClass(), parsedValue instanceof Float);
        Assert.assertEquals(parsedValue.toString().length(), STRING_FLOAT_MIN_VALUE.length());
    }

    @Test
    public void parseDoubleMinValue() {
        LOGGER.info("Start parseDoubleMinValue");
        Object parsedValue = saver.autoParse(STRING_DOUBLE_MIN_VALUE);
        Assert.assertTrue("Expected Double object but was " + parsedValue.getClass(), parsedValue instanceof Double);
        Assert.assertEquals(parsedValue.toString().length(), STRING_DOUBLE_MIN_VALUE.length());
    }

    @Test
    public void parseFloatMaxValue() {
        LOGGER.info("Start parseFloatMaxValue");
        Object parsedValue = saver.autoParse(STRING_FLOAT_MAX_VALUE);
        Assert.assertTrue("Expected Float object but was " + parsedValue.getClass(), parsedValue instanceof Float);
        Assert.assertEquals(parsedValue.toString().length(), STRING_FLOAT_MAX_VALUE.length());
    }

    @Test
    public void parseDoubleMaxValue() {
        LOGGER.info("Start parseDoubleMaxValue");
        Object parsedValue = saver.autoParse(STRING_DOUBLE_MAX_VALUE);
        Assert.assertTrue("Expected Double object but was " + parsedValue.getClass(), parsedValue instanceof Double);
        Assert.assertEquals(parsedValue.toString().length(), STRING_DOUBLE_MAX_VALUE.length());
    }

    @Test
    public void parseIntegerMinTest() {
        LOGGER.info("Start parseIntegerMinTest");
        Object parsedValue = saver.autoParse(STRING_INT_MIN_VALUE);
        Assert.assertTrue("Expected Integer object but was " + parsedValue.getClass(), parsedValue instanceof Integer);
        Assert.assertEquals(parsedValue.toString().length(), STRING_INT_MIN_VALUE.length());
    }

    @Test
    public void parseIntegerMaxTest() {
        LOGGER.info("Start parseIntegerMinTest");
        Object parsedValue = saver.autoParse(STRING_INT_MAX_VALUE);
        Assert.assertTrue("Expected Integer object but was " + parsedValue.getClass(), parsedValue instanceof Integer);
        Assert.assertEquals(parsedValue.toString().length(), STRING_INT_MAX_VALUE.length());
    }

    @Test
    public void parseStringFillTest() {
        LOGGER.info("Start parseStringFillTest");
        Object parsedValue = saver.autoParse(STRING_VALUE);
        Assert.assertTrue("Expected String object but was " + parsedValue.getClass(), parsedValue instanceof String);
        Assert.assertEquals(parsedValue.toString().length(), STRING_VALUE.length());
    }

    @Test
    public void parseStringEmptyTest() {
        LOGGER.info("Start parseStringEmptyTest");
        Object parsedValue = saver.autoParse(EMPTY_STRING);
        Assert.assertTrue("Expected String object but was " + parsedValue.getClass(), parsedValue instanceof String);
        Assert.assertEquals(parsedValue.toString().length(), EMPTY_STRING.length());
    }

    @Test
    public void parseBooleanTest() {
        LOGGER.info("Start parseBooleanTest");
        Object parsedValue = saver.autoParse(STRING_BOOLEAN_VALUE);
        Assert.assertTrue("Expected Boolean object but was " + parsedValue.getClass(), parsedValue instanceof Boolean);
        Assert.assertEquals(parsedValue.toString().length(), STRING_BOOLEAN_VALUE.length());
    }

    @Test(expected = NullPointerException.class)
    public void parseNullValue() {
        LOGGER.info("Start parseDoubleMinValue");
        saver.autoParse(null);
    }
}
