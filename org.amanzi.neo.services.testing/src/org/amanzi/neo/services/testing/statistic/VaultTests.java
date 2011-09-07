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

package org.amanzi.neo.services.testing.statistic;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.FailedParseValueException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.UnsupportedClassException;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.StatisticsVault;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;
import org.apache.log4j.Logger;
import org.junit.Test;

public class VaultTests {
    /**
     * this class created specially for testing parse method, objects of this class contain the
     * following fields : propName, propType, autoParsePropType, parsePropValue
     * 
     * @author kruglik_a
     */
    public class StoreForTestingParse {
        String propName;
        Class< ? > propType;
        Class< ? > autoParsePropType;
        String parsePropValue;

        /**
         * constructor
         * 
         * @param propName
         * @param propType
         * @param parsePropValue
         */
        public StoreForTestingParse(String propName, Class< ? > propType, String parsePropValue) {
            this.propName = propName;
            this.propType = propType;
            this.parsePropValue = parsePropValue;
            this.autoParsePropType = propType;
        }

        /**
         * constructor
         * 
         * @param propName
         * @param propType
         * @param avtoParsePropType
         * @param parsePropValue
         */
        public StoreForTestingParse(String propName, Class< ? > propType, Class< ? > avtoParsePropType, String parsePropValue) {
            this.propName = propName;
            this.propType = propType;
            this.parsePropValue = parsePropValue;
            this.autoParsePropType = avtoParsePropType;
        }

        public Class< ? > getAutoParsePropType() {
            return autoParsePropType;
        }

        public void setAutoParsePropType(Class< ? > avtoParsePropType) {
            this.autoParsePropType = avtoParsePropType;
        }

        public String getPropName() {
            return propName;
        }

        public void setPropName(String propName) {
            this.propName = propName;
        }

        public Class< ? > getPropType() {
            return propType;
        }

        public void setPropType(Class< ? > propType) {
            this.propType = propType;
        }

        public String getParsePropValue() {
            return parsePropValue;
        }

        public void setParsePropValue(String parsePropValue) {
            this.parsePropValue = parsePropValue;
        }

    }

    private static Logger LOGGER = Logger.getLogger(VaultTests.class);

    private final static String PROPERTIES = "PROPERTIES";
    private final static String NEIGHBOURS = "Neighbours";
    private final static String NETWORK = "Network";

    private final static String STRING_VALUE = "stringValue";
    private final static String BYTE_VALUE = "byteValue";
    private final static String SHORT_VALUE = "shortValue";
    private final static String INTEGER_VALUE = "integerValue";
    private final static String LONG_VALUE = "longValue";
    private final static String FLOAT_VALUE = "floatValue";
    private final static String DOUBLE_VALUE = "doubleValue";
    private final static String BOOLEAN_VALUE = "booleanValue";

    private final static String PROPERTY_NAME_NAME = "name";
    private final static String PROPERTY_NAME_TIME = "time";
    private final static String STRING_PROPERTY_VALUE_NETWORK_1 = "network_1";
    private final static String STRING_PROPERTY_VALUE_NETWORK_2 = "network_2";
    private final static String STRING_PROPERTY_VALUE_NEIGHBOURS_1 = "neighbour_1";
    private final static String EMPTY_STRING = "";
    private final static String STRING_INT_VALUE = "5";
    private final static String STRING_FLOAT_VALUE = "5,5";
    private final static String STRING_BOOLEAN_VALUE = "true";

    private List<StoreForTestingParse> testingList = new ArrayList<VaultTests.StoreForTestingParse>();
    {
        testingList.add(new StoreForTestingParse(BOOLEAN_VALUE, Boolean.class, STRING_BOOLEAN_VALUE));
        testingList.add(new StoreForTestingParse(BYTE_VALUE, Byte.class, Integer.class, STRING_INT_VALUE));
        testingList.add(new StoreForTestingParse(DOUBLE_VALUE, Double.class, Float.class, STRING_FLOAT_VALUE));
        testingList.add(new StoreForTestingParse(FLOAT_VALUE, Float.class, STRING_FLOAT_VALUE));
        testingList.add(new StoreForTestingParse(INTEGER_VALUE, Integer.class, STRING_INT_VALUE));
        testingList.add(new StoreForTestingParse(LONG_VALUE, Long.class, Integer.class, STRING_INT_VALUE));
        testingList.add(new StoreForTestingParse(SHORT_VALUE, Short.class, Integer.class, STRING_INT_VALUE));
        testingList.add(new StoreForTestingParse(STRING_VALUE, String.class, STRING_VALUE));
    }

    /**
     * this method index given value to given vault given number of times
     * 
     * @param vault
     * @param nodeType
     * @param propName
     * @param propValue
     * @param number
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    private void indexProperty(IVault vault, String nodeType, String propName, String propValue, int number)
            throws IndexPropertyException, InvalidStatisticsParameterException {
        for (int i = 0; i < number; i++) {
            vault.indexProperty(nodeType, propName, propValue);
        }
    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * propName already exists as PropertyStatistics instance
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyPositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);

        int expectedPropStatCount = 1;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);
        int expectedNeighbourSubVaultCount = expectedPropStatCount;
        int expectedNetworkSubVaultCount = 0;
        int expectedVaultCount = expectedNeighbourSubVaultCount + expectedNetworkSubVaultCount;

        int actualPropStatCount = propStat.getPropertyMap().get(STRING_PROPERTY_VALUE_NETWORK_1);
        int actualNetworkSubVaultCount = networkSubVault.getCount();
        int actualVaultCount = propVault.getCount();
        int actualNeighbourSubVaultCount = neighboursSubVault.getCount();

        Assert.assertEquals("propStat has wrong count", expectedPropStatCount, actualPropStatCount);
        Assert.assertEquals("networkSubVault has wrong count", expectedNetworkSubVaultCount, actualNetworkSubVaultCount);
        Assert.assertEquals("propVault has wrong count", expectedVaultCount, actualVaultCount);
        Assert.assertEquals("neighbourSubVault has wrong count", expectedNeighbourSubVaultCount, actualNeighbourSubVaultCount);
        LOGGER.debug("finish test indexPropertyPositiveTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * propName does not exist as PropertyStatistics instance
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyNewPropertyIndexPositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyNewPropertyIndexPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);

        int expectedPropStatCount = 1;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        int expectedNetworkSubVaultCount = expectedPropStatCount;
        int expectedNeighbourSubVaultCount = 0;
        int expectedVaultCount = expectedNeighbourSubVaultCount + expectedNetworkSubVaultCount;

        int actualPropStatCount = networkSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME).getPropertyMap()
                .get(STRING_PROPERTY_VALUE_NETWORK_1);
        int actualNetworkSubVaultCount = networkSubVault.getCount();
        int actualVaultCount = propVault.getCount();
        int actualNeighboursSubVaultCount = neighboursSubVault.getCount();

        Assert.assertEquals("propStat has wrong count", expectedPropStatCount, actualPropStatCount);
        Assert.assertEquals("networkSubVault has wrong count", expectedNetworkSubVaultCount, actualNetworkSubVaultCount);
        Assert.assertEquals("propVault has wrong count", expectedVaultCount, actualVaultCount);
        Assert.assertEquals("neighboursSubVault has wrong count", expectedNeighbourSubVaultCount, actualNeighboursSubVaultCount);
        LOGGER.debug("finish test indexPropertyNewPropertyIndexPositiveTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when vault
     * with given type does not exists
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyCreateNewVaultPositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyCreateNewVaultPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);

        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);

        int expectedPropStatCount = 1;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        StatisticsVault actualNeighboursSubVault = (StatisticsVault)propVault.getSubVaults().get(NEIGHBOURS);
        Assert.assertNotNull("NeighboursSubVault not created", actualNeighboursSubVault);

        int actualPropStatCount = actualNeighboursSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME).getPropertyMap()
                .get(STRING_PROPERTY_VALUE_NETWORK_1);
        Assert.assertEquals("propStat has wrong count", expectedPropStatCount, actualPropStatCount);

        int actualNeighboursSubVaultCount = actualNeighboursSubVault.getCount();
        int expectedNeighboursSubVaultCount = expectedPropStatCount;
        Assert.assertEquals("neighboursSubVault has wrong count", expectedNeighboursSubVaultCount, actualNeighboursSubVaultCount);

        int actualNetworkSubVaultCount = networkSubVault.getCount();
        int expectedNetworkSubVaultCount = 0;
        Assert.assertEquals("networkSubVault has wrong count", expectedNetworkSubVaultCount, actualNetworkSubVaultCount);

        int actualPropVaultCount = propVault.getCount();
        int expectedPropVaultCount = expectedNeighboursSubVaultCount + expectedNetworkSubVaultCount;
        Assert.assertEquals("propVault has wrong count", expectedPropVaultCount, actualPropVaultCount);
        LOGGER.debug("finish test indexPropertyCreateNewVaultPositiveTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) and check
     * that sum subVaults counts equally parentVault count
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyPositiveSumTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyPositiveSumTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);
        propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME, String.class);
        networkSubVault.addPropertyStatistics(propStat);

        int expectedNetworkSubVaultCount = 3;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, expectedNetworkSubVaultCount);

        int expectedNeighbourSubVaultCount = 4;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NEIGHBOURS_1, expectedNeighbourSubVaultCount);

        int expectedVaultCount = expectedNeighbourSubVaultCount + expectedNetworkSubVaultCount;

        int actualNetworkSubVaultCount = networkSubVault.getCount();
        int actualVaultCount = propVault.getCount();
        int actualNeighbourSubVaultCount = neighboursSubVault.getCount();

        Assert.assertEquals("networkSubVault has wrong count", expectedNetworkSubVaultCount, actualNetworkSubVaultCount);
        Assert.assertEquals("propVault has wrong count", expectedVaultCount, actualVaultCount);
        Assert.assertEquals("neighbourSubVault has wrong count", expectedNeighbourSubVaultCount, actualNeighbourSubVaultCount);
        Assert.assertEquals("summ subCount not equals parentCount", actualNetworkSubVaultCount + actualNeighbourSubVaultCount,
                actualVaultCount);
        LOGGER.debug("finish test indexPropertyPositiveSumTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * indexing is carried out at the root vault
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyRootVaultPositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyRootVaultPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        int expectedPropStatCount = 1;
        indexProperty(propVault, PROPERTIES, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        int expectedVaultCount = expectedPropStatCount;

        int actualPropStatCount = propVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME).getPropertyMap()
                .get(STRING_PROPERTY_VALUE_NETWORK_1);
        int actualVaultCount = propVault.getCount();
        Assert.assertEquals("propStat has wrong count", expectedPropStatCount, actualPropStatCount);
        Assert.assertEquals("propVault has wrong count", expectedVaultCount, actualVaultCount);
        LOGGER.debug("finish test indexPropertyRootVaultPositiveTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when method
     * is called multiple times under different conditions
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void indexPropertyGeneralPositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyGeneralPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);

        int neighboursPropStatNamePropNumberValue_1 = 2;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1,
                neighboursPropStatNamePropNumberValue_1);

        int neighboursPropStatNamePropNumberValue_2 = 1;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_2,
                neighboursPropStatNamePropNumberValue_2);

        int networkPropStatNamePropNumberValue = 1;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1, networkPropStatNamePropNumberValue);

        int networkPropStatTimePropNumberValue = 3;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_TIME, STRING_VALUE, networkPropStatTimePropNumberValue);

        int expectedNeighboursPropStatNamePropCount = neighboursPropStatNamePropNumberValue_1
                + neighboursPropStatNamePropNumberValue_2;
        int expectedNetworkPropStatNamePropCount = networkPropStatNamePropNumberValue;
        int expectedNetworkPropStatTimePropCount = networkPropStatTimePropNumberValue;
        int expectedNetworkSubVaultCount = expectedNetworkPropStatNamePropCount + expectedNetworkPropStatTimePropCount;
        int expectedNeighboursSubVaultCount = expectedNeighboursPropStatNamePropCount;
        int expectedPropVaultCount = expectedNeighboursSubVaultCount + expectedNetworkSubVaultCount;

        int actualNeighboursPropStatNamePropCount = propStat.getPropertyMap().get(STRING_PROPERTY_VALUE_NETWORK_1)
                + propStat.getPropertyMap().get(STRING_PROPERTY_VALUE_NETWORK_2);
        int actualNetworkPropStatNamePropCount = networkSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME)
                .getPropertyMap().get(STRING_PROPERTY_VALUE_NETWORK_1);
        int actualNetworkPropStatTimePropCount = networkSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_TIME)
                .getPropertyMap().get(STRING_VALUE);
        int actualNetworkSubVaultCount = networkSubVault.getCount();
        int actualVaultCount = propVault.getCount();
        int actualNeighbourSubVaultCount = neighboursSubVault.getCount();

        Assert.assertEquals("network propStat for name property has wrong count", expectedNetworkPropStatNamePropCount,
                actualNetworkPropStatNamePropCount);
        Assert.assertEquals("network propStat for time property has wrong count", expectedNetworkPropStatTimePropCount,
                actualNetworkPropStatTimePropCount);
        Assert.assertEquals("neighbours propStat for name property has wrong count", expectedNeighboursPropStatNamePropCount,
                actualNeighboursPropStatNamePropCount);
        Assert.assertEquals("networkSubVault has wrong count", expectedNetworkSubVaultCount, actualNetworkSubVaultCount);
        Assert.assertEquals("propVault has wrong count", expectedPropVaultCount, actualVaultCount);
        Assert.assertEquals("neighbourSubVault has wrong count", expectedNeighboursSubVaultCount, actualNeighbourSubVaultCount);
        LOGGER.debug("finish test indexPropertyGeneralPositiveTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * propValue type is wrong
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = IndexPropertyException.class)
    public void indexPropertyInvalidPropertyTypeNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyInvalidPropertyTypeNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME, Integer.class);
        networkSubVault.addPropertyStatistics(propStat);

        propVault.indexProperty(NETWORK, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1);
        LOGGER.debug("finish test indexPropertyInvalidPropertyTypeNegativeTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * parameter nodeType = null
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void indexPropertyNullParamNodeTypeNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyNullParamNodeTypeNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.indexProperty(null, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1);
        LOGGER.debug("finish test indexPropertyNullParamNodeTypeNegativeTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * parameter propName = null
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void indexPropertyNullParamPropNameNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyNullParamPropNameNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.indexProperty(NEIGHBOURS, null, STRING_PROPERTY_VALUE_NETWORK_1);
        LOGGER.debug("finish test indexPropertyNullParamPropNameNegativeTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * parameter propValue = null
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void indexPropertyNullParamPropValueNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyNullParamPropValueNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.indexProperty(NEIGHBOURS, PROPERTY_NAME_NAME, null);
        LOGGER.debug("finish test indexPropertyNullParamPropValueNegativeTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * parameter nodeType = ""
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void indexPropertyEmptyParamNodeTypeNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyEmptyParamNodeTypeNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.indexProperty(EMPTY_STRING, PROPERTY_NAME_NAME, STRING_PROPERTY_VALUE_NETWORK_1);
        LOGGER.debug("finish test indexPropertyEmptyParamNodeTypeNegativeTest()");

    }

    /**
     * testing method indexProperty(String nodeType, String propName, Object propValue) when
     * parameter propName = ""
     * 
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void indexPropertyEmptyParamPropNameNegativeTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test indexPropertyEmptyParamPropNameNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.indexProperty(NEIGHBOURS, EMPTY_STRING, STRING_PROPERTY_VALUE_NETWORK_1);
        LOGGER.debug("finish test indexPropertyEmptyParamPropNameNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * propName already exists as PropertyStatistics instance
     * 
     * @throws AWEException
     */
    @Test
    public void parsePositiveTest() throws AWEException {
        LOGGER.debug("start test parsePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);

        for (StoreForTestingParse testObj : testingList) {
            Class< ? > klass = testObj.getPropType();
            NewPropertyStatistics propStat = new NewPropertyStatistics(testObj.getPropName(), klass);
            propVault.addPropertyStatistics(propStat);

            String parseValue = testObj.getParsePropValue();

            Object actualParseValue = propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
            Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
            LOGGER.debug("finish test parsePositiveTest()");

        }

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * propValue impossible convert to required type instance
     * 
     * @throws InvalidStatisticsParameterException
     * @throws FailedParseValueException
     */
    @Test
    public void parseNegativeTest() throws InvalidStatisticsParameterException, FailedParseValueException {
        LOGGER.debug("start test parseNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        int expectedCount = 6;
        int count = 0;
        for (StoreForTestingParse testObj : testingList) {
            Class< ? > klass = testObj.getPropType();
            NewPropertyStatistics propStat = new NewPropertyStatistics(testObj.getPropName(), klass);
            propVault.addPropertyStatistics(propStat);

            String parseValue = "failedValue";

            try {
                propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
            } catch (Exception e) {
                Assert.assertEquals("when parse to " + testObj.getPropType().getCanonicalName()
                        + " type throws not expected exception", FailedParseValueException.class, e.getClass());
                count++;
            }
        }
        Assert.assertEquals("expected exception throws for not all value types", expectedCount, count);
        LOGGER.debug("finish test parseNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * propStat has unsupported type
     * 
     * @throws AWEException
     */
    @Test(expected = UnsupportedClassException.class)
    public void parseUnsupportedClassNegativeTest() throws AWEException {
        LOGGER.debug("start test parseUnsupportedClassNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_TIME, Date.class);
        propVault.addPropertyStatistics(propStat);
        propVault.parse(PROPERTIES, PROPERTY_NAME_TIME, "10/12/2011");
        LOGGER.debug("finish test parseUnsupportedClassNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * propName does not exist as PropertyStatistics instance and invoke avtoParse
     * 
     * @throws AWEException
     */
    @Test
    public void parseAutoParsePositiveTest() throws AWEException {
        LOGGER.debug("start test parseAvtoParsePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        Map<String, NewPropertyStatistics> propStatMap;
        boolean hasPropStat;

        for (StoreForTestingParse testObj : testingList) {
            Class< ? > klass = testObj.getAutoParsePropType();
            String parseValue = testObj.getParsePropValue();

            Object actualParseValue = propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
            Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
            propStatMap = propVault.getPropertyStatisticsMap();
            hasPropStat = propStatMap.containsKey(testObj.getPropName());
            Assert.assertTrue("Property vault not contains expected PropStat", hasPropStat);
        }
        LOGGER.debug("finish test parseAvtoParsePositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when vault
     * with given type does not exists
     * 
     * @throws AWEException
     */
    @Test
    public void parseCreateVaultPositiveTest() throws AWEException {
        LOGGER.debug("start test parseCreateVaultPositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);

        for (StoreForTestingParse testObj : testingList) {
            Class< ? > klass = testObj.getAutoParsePropType();
            String parseValue = testObj.getParsePropValue();

            Object actualParseValue = propVault.parse(NETWORK, testObj.getPropName(), parseValue);
            Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
        }

        boolean hasNetworkSubVault = propVault.getSubVaults().containsKey(NETWORK);
        Assert.assertTrue("Network vault must be subVault PropVault", hasNetworkSubVault);
        Map<String, NewPropertyStatistics> propStatMap = propVault.getSubVaults().get(NETWORK).getPropertyStatisticsMap();

        boolean hasPropStat;
        for (StoreForTestingParse testObj : testingList) {
            hasPropStat = propStatMap.containsKey(testObj.getPropName());
            Assert.assertTrue("Network vault not contains expected PropStat", hasPropStat);
        }
        LOGGER.debug("finish test parseCreateVaultPositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) for
     * avtoParse to Long value
     * 
     * @throws AWEException
     */
    @Test
    public void parseAutoParseLongValuePositiveTest() throws AWEException {
        LOGGER.debug("start test parseAvtoParseLongValuePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        int maxInt = Integer.MAX_VALUE;
        Object actualParseValue = propVault.parse(NETWORK, LONG_VALUE, String.valueOf((long)maxInt + 1));
        Assert.assertTrue("parse value has not expected type", actualParseValue.getClass().equals(Long.class));
        LOGGER.debug("finish test parseAvtoParseLongValuePositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) for
     * avtoParse to Double value
     * 
     * @throws AWEException
     */
    @Test
    public void parseAutoParseDoubleValuePositiveTest() throws AWEException {
        LOGGER.debug("start test parseAvtoParseDoubleValuePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);

        Object actualParseValue = propVault.parse(NETWORK, DOUBLE_VALUE, "1,12345678e-13");
        Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), Double.class);
        LOGGER.debug("finish test parseAvtoParseDoubleValuePositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when string
     * property value is empty
     * 
     * @throws AWEException
     */
    @Test
    public void parseEmptyStrValuePositiveTest() throws AWEException {
        LOGGER.debug("start test parseEmptyStrValuePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        Object parseValue = propVault.parse(PROPERTIES, STRING_VALUE, EMPTY_STRING);
        Assert.assertNull("value must be null", parseValue);
        LOGGER.debug("finish test parseEmptyStrValuePositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when string
     * property value is null
     * 
     * @throws AWEException
     */
    @Test
    public void parseNullStrValuePositiveTest() throws AWEException {
        LOGGER.debug("start test parseNullStrValuePositiveTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        Object actualValue = propVault.parse(PROPERTIES, STRING_VALUE, null);
        Assert.assertNull("value must be null", actualValue);
        LOGGER.debug("finish test parseNullStrValuePositiveTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * parameter nodeType = null
     * 
     * @throws AWEException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void parseNullNodeTypeNegativeTest() throws AWEException {
        LOGGER.debug("start test parseNullNodeTypeNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.parse(null, STRING_VALUE, PROPERTY_NAME_NAME);
        LOGGER.debug("finish test parseNullNodeTypeNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * parameter propertyName = null
     * 
     * @throws AWEException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void parseNullPropNameNegativeTest() throws AWEException {
        LOGGER.debug("start test parseNullPropNameNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.parse(PROPERTIES, null, PROPERTY_NAME_NAME);
        LOGGER.debug("finish test parseNullPropNameNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * parameter nodeType is empty
     * 
     * @throws AWEException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void parseEmptyNodeTypeNegativeTest() throws AWEException {
        LOGGER.debug("start test parseEmptyNodeTypeNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.parse(EMPTY_STRING, STRING_VALUE, PROPERTY_NAME_NAME);
        LOGGER.debug("finish test parseEmptyNodeTypeNegativeTest()");

    }

    /**
     * testing method parse(String nodeType, String propertyName, String propertyValue) when
     * parameter propertyName is empty
     * 
     * @throws AWEException
     */
    @Test(expected = InvalidStatisticsParameterException.class)
    public void parseEmptyPropNameNegativeTest() throws AWEException {
        LOGGER.debug("start test parseEmptyPropNameNegativeTest()");
        StatisticsVault propVault = new StatisticsVault(PROPERTIES);
        propVault.parse(PROPERTIES, EMPTY_STRING, PROPERTY_NAME_NAME);
        LOGGER.debug("finish test parseEmptyPropNameNegativeTest()");

    }

}
