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
    private final static String SITE = "Site";
    private final static String SECTOR = "Sector";

    private final static String STRING_VALUE = "stringValue";
    private final static String BYTE_VALUE = "byteValue";
    private final static String SHORT_VALUE = "shortValue";
    private final static String INTEGER_VALUE = "integerValue";
    private final static String LONG_VALUE = "longValue";
    private final static String FLOAT_VALUE = "floatValue";
    private final static String DOUBLE_VALUE = "doubleValue";
    private final static String BOOLEAN_VALUE = "booleanValue";

    private final static String PROPERTY_NAME_NAME_1 = "name";
    private final static String PROPERTY_NAME_NAME_2 = "name2";
    private final static String PROPERTY_NAME_NAME_3 = "name3";
    private final static String PROPERTY_NAME_NAME_4 = "name4";
    private final static String PROPERTY_NAME_NAME_5 = "name5";
    private final static String PROPERTY_NAME_TIME = "time";
    private final static String STRING_PROPERTY_VALUE_NETWORK_1 = "network_1";
    private final static String STRING_PROPERTY_VALUE_NETWORK_2 = "network_2";
    private final static String STRING_PROPERTY_VALUE_NETWORK_3 = "network_3";
    private final static Integer INTEGER_PROPERTY_VALUE_NETWORK = new Integer(100);
    private final static Integer INTEGER_PROPERTY_VALUE_NETWORK_2 = new Integer(200);
    private final static String STRING_PROPERTY_VALUE_NEIGHBOURS_1 = "neighbour_1";
    private final static String STRING_PROPERTY_VALUE_NEIGHBOURS_2 = "neighbour_2";
    
    private final static String EMPTY_STRING = "";
    private final static String STRING_INT_VALUE = "5";
    private final static String STRING_FLOAT_VALUE = "5.5";
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
    
    StatisticsVault propVault = null;
    StatisticsVault neighboursSubVault = null;
    StatisticsVault networkSubVault = null;
    StatisticsVault siteSubVault = null;
    StatisticsVault sectorSubVault = null;

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
    private void indexProperty(IVault vault, String nodeType, String propName, Object propValue, int number)
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
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);

        int expectedPropStatCount = 1;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);
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
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        int expectedNetworkSubVaultCount = expectedPropStatCount;
        int expectedNeighbourSubVaultCount = 0;
        int expectedVaultCount = expectedNeighbourSubVaultCount + expectedNetworkSubVaultCount;

        int actualPropStatCount = networkSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME_1).getPropertyMap()
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
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        StatisticsVault actualNeighboursSubVault = (StatisticsVault)propVault.getSubVaults().get(NEIGHBOURS);
        Assert.assertNotNull("NeighboursSubVault not created", actualNeighboursSubVault);

        int actualPropStatCount = actualNeighboursSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME_1).getPropertyMap()
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
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);
        propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        networkSubVault.addPropertyStatistics(propStat);

        int expectedNetworkSubVaultCount = 3;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, expectedNetworkSubVaultCount);

        int expectedNeighbourSubVaultCount = 4;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_1, expectedNeighbourSubVaultCount);

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
        indexProperty(propVault, PROPERTIES, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, expectedPropStatCount);

        int expectedVaultCount = expectedPropStatCount;

        int actualPropStatCount = propVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME_1).getPropertyMap()
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
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        neighboursSubVault.addPropertyStatistics(propStat);

        int neighboursPropStatNamePropNumberValue_1 = 2;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1,
                neighboursPropStatNamePropNumberValue_1);

        int neighboursPropStatNamePropNumberValue_2 = 1;
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_2,
                neighboursPropStatNamePropNumberValue_2);

        int networkPropStatNamePropNumberValue = 1;
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, networkPropStatNamePropNumberValue);

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
        int actualNetworkPropStatNamePropCount = networkSubVault.getPropertyStatisticsMap().get(PROPERTY_NAME_NAME_1)
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
        NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, Integer.class);
        networkSubVault.addPropertyStatistics(propStat);

        propVault.indexProperty(NETWORK, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
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
        propVault.indexProperty(null, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
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
        propVault.indexProperty(NEIGHBOURS, PROPERTY_NAME_NAME_1, null);
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
        propVault.indexProperty(EMPTY_STRING, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
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

        Object actualParseValue = propVault.parse(NETWORK, DOUBLE_VALUE, "1.12345678e-13");
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
        propVault.parse(null, STRING_VALUE, PROPERTY_NAME_NAME_1);
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
        propVault.parse(PROPERTIES, null, PROPERTY_NAME_NAME_1);
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
        propVault.parse(EMPTY_STRING, STRING_VALUE, PROPERTY_NAME_NAME_1);
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
        propVault.parse(PROPERTIES, EMPTY_STRING, PROPERTY_NAME_NAME_1);
        LOGGER.debug("finish test parseEmptyPropNameNegativeTest()");

    }
    
    /**
     * Creating standart structure of statistics
     * 
     * STRUCTURE:
     * 
     *...........................properties................................
     *...................../..................\............................
     *..................../....................\...........................
     *..................network....................neighbours..............
     *.............../.....|.....\................../.........\............
     *............../......|......\................/...........\...........
     *........name4(S)..name5(I)....name3(S)....name1(2xS).......name2(I)....
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    private void createStandartStructureOfStatistics() throws IndexPropertyException, InvalidStatisticsParameterException {
        propVault = new StatisticsVault(PROPERTIES);
        neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        networkSubVault = new StatisticsVault(NETWORK);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);
        NewPropertyStatistics propStat1 = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        neighboursSubVault.addPropertyStatistics(propStat1);
        NewPropertyStatistics propStat2 = new NewPropertyStatistics(PROPERTY_NAME_NAME_2, Integer.class);
        neighboursSubVault.addPropertyStatistics(propStat2);
        NewPropertyStatistics propStat3 = new NewPropertyStatistics(PROPERTY_NAME_NAME_3, String.class);
        networkSubVault.addPropertyStatistics(propStat3);
        NewPropertyStatistics propStat4 = new NewPropertyStatistics(PROPERTY_NAME_NAME_4, String.class);
        networkSubVault.addPropertyStatistics(propStat4);
        NewPropertyStatistics propStat5 = new NewPropertyStatistics(PROPERTY_NAME_NAME_5, Integer.class);
        networkSubVault.addPropertyStatistics(propStat5);
        
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, 1);
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2, 1);
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_2, INTEGER_PROPERTY_VALUE_NETWORK, 1);
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2, 1);
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3, 1);
        indexProperty(propVault, NETWORK, PROPERTY_NAME_NAME_5, INTEGER_PROPERTY_VALUE_NETWORK_2, 1);
    }
    
    /**
     * testing method getCount() positive tests (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getCountStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getCountStandartStructurePositiveTest()");
        createStandartStructureOfStatistics();
        
        int expectedPropStatCount = 6;
        
        int propCount = propVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with prop vault)", 
        		expectedPropStatCount, propCount);
        
        expectedPropStatCount = 3;
        int networkCount = networkSubVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCount);
        
        int neighboursCount = neighboursSubVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCount);
        
        LOGGER.debug("finish test getCountStandartStructurePositiveTest()");

    }
    
    /**
     * testing method getNodeCount(String nodeType) positive tests (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getNodeCountStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getNodeCountStandartStructurePositiveTest()");
        createStandartStructureOfStatistics();
        
        int expectedPropStatCount = 6;
        
        int propCountProperty = propVault.getNodeCount(PROPERTIES);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with main vault)", 
        		expectedPropStatCount, propCountProperty);
        
        expectedPropStatCount = 0;
        int networkCountProperty = neighboursSubVault.getNodeCount(NETWORK);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCountProperty);
        
        expectedPropStatCount = 3;
        int neighboursCountProperty = neighboursSubVault.getNodeCount(NEIGHBOURS);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCountProperty);
        
        LOGGER.debug("finish test getNodeCountStandartStructurePositiveTest()");
    }
    
    /**
     * testing method getPropertyCount(String nodeType, String propertyName) positive tests (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getPropertyCountStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getPropertyCountStandartStructurePositiveTest()");
        createStandartStructureOfStatistics();
        
        int expectedPropStatCount = 2;
        
        int propCountProperty = propVault.getPropertyCount(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getPropertyCount(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedPropStatCount, propCountProperty);
        
        expectedPropStatCount = 0;
        int networkCountProperty = neighboursSubVault.getPropertyCount(NETWORK, PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getPropertyCount(String nodeType, String propertyName) return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCountProperty);
        
        expectedPropStatCount = 0;
        int neighboursCountProperty = networkSubVault.getPropertyCount(NETWORK, PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCountProperty);
        
        expectedPropStatCount = 1;
        int networkCountProperty2 = propVault.getPropertyCount(NETWORK, PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with main vault)", 
        		expectedPropStatCount, networkCountProperty2);
        
        LOGGER.debug("finish test getPropertyCountStandartStructurePositiveTest()");
    }

    
    /**
     * testing method getAllProperties() (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesStandartStructureTest()");
        createStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 6;
        Map<Object, Integer> allProperties_propVault = propVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allProperties_propVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allProperties_neighboursSubVault = neighboursSubVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with neighbours subVault)", 
        		expectedAllPropStatCount, allProperties_neighboursSubVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allProperties_networkSubVault = networkSubVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with network subVault)", 
        		expectedAllPropStatCount, allProperties_networkSubVault.size());
        LOGGER.debug("finish test getAllPropertiesStandartStructureTest()");
    }
    
    /**
     * testing method getAllProperties(String nodeType) (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithNodeNameStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithNodeNameStandartStructureTest()");
        createStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 3;
        Map<Object, Integer> allPropertiesWithNodeType_propVault = propVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithNodeType_neighboursSubVault = neighboursSubVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with subVault). Expected zero.", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_neighboursSubVault.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithNodeType_networkSubVault = networkSubVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with subVault). Expected zero.", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_networkSubVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allPropertiesWithNodeType_propVault2 = propVault.getAllProperties(NETWORK);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault2.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithNodeType_propVault3 = propVault.getAllProperties(SECTOR);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with main vault). Expected zero.", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault3.size());
        
        LOGGER.debug("finish test getAllPropertiesWithNodeNameStandartStructureTest()");
    }


    /**
     * testing method getAllPropertiesWithName(String propertyName) (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithPropertyNameStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithPropertyNameStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        getAllPropertiesWithPropertyNameStandartAndNotStandartStructureTest();
        LOGGER.debug("finish test getAllPropertiesWithPropertyNameStandartStructureTest()");
    }
    
    private void getAllPropertiesWithPropertyNameStandartAndNotStandartStructureTest() {
        int expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithPropertyName_name0 = neighboursSubVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with subVault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name0.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name1 = propVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name1.size());
        
        expectedAllPropStatCount = 1;
        Map<Object, Integer> allPropertiesWithPropertyName_name2 = propVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_2);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name2.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name3 = propVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name3.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name4 = propVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_4);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name4.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name5 = propVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name5.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name6 = neighboursSubVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_2);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with subVault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name6.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithPropertyName_name7 = neighboursSubVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with subVault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name7.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name8 = neighboursSubVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_4);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with subVault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name8.size());
        
        Map<Object, Integer> allPropertiesWithPropertyName_name9 = neighboursSubVault.getAllPropertiesWithName(PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getAllPropertiesWithName(String propertyName) return wrong count (test with subVault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyName_name9.size());
    }
    
    /**
     * testing method getAllProperties(Class< ? > klass) (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithTypeOfClassStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 4;
        Map<Object, Integer> allPropertiesWithKlass_class1 = propVault.getAllProperties(String.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class1.size());
       
        expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithKlass_class2 = propVault.getAllProperties(Integer.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class2.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithKlass_class3 = propVault.getAllProperties(Double.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with subvault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class3.size());
        
        LOGGER.debug("finish test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
    }
    
    /**
     * testing method getAllProperties(Class< ? > klass) (standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithNodeTypeAndPropertyNameStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
        createStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name1 = propVault.getAllProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name1.size());
        
        expectedAllPropStatCount = 1;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name2 = propVault.getAllProperties(NEIGHBOURS, PROPERTY_NAME_NAME_2);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name2.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name3 = propVault.getAllProperties(SECTOR, PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault). Expected zero", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name3.size());
        
        expectedAllPropStatCount = 1;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name4 = propVault.getAllProperties(NETWORK, PROPERTY_NAME_NAME_4);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name4.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name5 = networkSubVault.getAllProperties(NETWORK, PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with subVault). Expected zero", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name5.size());
        
        LOGGER.debug("finish test getAllPropertiesWithNodeTypeAndPropertyNameStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithNodeTypeStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithNodeTypeStandartStructureTest()");
       
        createStandartStructureOfStatistics();
        int expectedCount = 3;
        propVault.deletePropertiesWithNodeType(NETWORK);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 3;
        propVault.deletePropertiesWithNodeType(NEIGHBOURS);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithNodeTypeStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithPropertyNameStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithPropertyNameStandartStructureTest()");
       
        createStandartStructureOfStatistics();
        int expectedCount = 4;
        propVault.deleteProperties(PROPERTY_NAME_NAME_1);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        networkSubVault.deleteProperties(PROPERTY_NAME_NAME_4);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        neighboursSubVault.deleteProperties(PROPERTY_NAME_NAME_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithPropertyNameStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithNodeTypeAndPropertyNameStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithNodeTypeAndPropertyNameStandartStructureTest()");
       
        createStandartStructureOfStatistics();
        int expectedCount = 4;
        propVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(NETWORK, PROPERTY_NAME_NAME_4);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        networkSubVault.deleteProperties(NETWORK, PROPERTY_NAME_NAME_5);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 5;
        neighboursSubVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithNodeTypeAndPropertyNameStandartStructureTest()");
    }

    @Test
    public void updatePropertiesCountStandartStructureTest() 
    		throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test updatePropertiesCountStandartStructureTest()");
       
        createStandartStructureOfStatistics();
        int expectedCount = 2;
        propVault.updatePropertiesCount(NETWORK, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3, 2);
        int actualCount = propVault.getPropertyValueCount(NETWORK, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createStandartStructureOfStatistics();
        expectedCount = 126;
        propVault.updatePropertiesCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, 126);
        actualCount = propVault.getPropertyValueCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 127;
        propVault.updatePropertiesCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2, 127);
        actualCount = propVault.getPropertyValueCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test updatePropertiesCountStandartStructureTest()");
    }
    
    /**
     * Creating not standart structure of statistics
     * 
     * STRUCTURE:
     * 
     *.....................properties........................
     *................../..............\.....................
     *................./................\....................
     *.............network............neighbours.............
     *............./......\.........../.........\............
     *............/........\........./...........\...........
     *.........site......sector....name1(2xS).......name2(I)...
     *......../...\..........\...............................
     *......./.....\..........\..............................
     *..name4(S)....name5(I)..name3(S).......................
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    private void createNotStandartStructureOfStatistics() throws IndexPropertyException, InvalidStatisticsParameterException {
        propVault = new StatisticsVault(PROPERTIES);
        neighboursSubVault = new StatisticsVault(NEIGHBOURS);
        networkSubVault = new StatisticsVault(NETWORK);
        siteSubVault = new StatisticsVault(SITE);
        sectorSubVault = new StatisticsVault(SECTOR);
        propVault.addSubVault(networkSubVault);
        propVault.addSubVault(neighboursSubVault);
        networkSubVault.addSubVault(siteSubVault);
        networkSubVault.addSubVault(sectorSubVault);
        NewPropertyStatistics propStat1 = new NewPropertyStatistics(PROPERTY_NAME_NAME_1, String.class);
        neighboursSubVault.addPropertyStatistics(propStat1);
        NewPropertyStatistics propStat2 = new NewPropertyStatistics(PROPERTY_NAME_NAME_2, Integer.class);
        neighboursSubVault.addPropertyStatistics(propStat2);
        NewPropertyStatistics propStat3 = new NewPropertyStatistics(PROPERTY_NAME_NAME_3, String.class);
        sectorSubVault.addPropertyStatistics(propStat3);
        NewPropertyStatistics propStat4 = new NewPropertyStatistics(PROPERTY_NAME_NAME_4, String.class);
        siteSubVault.addPropertyStatistics(propStat4);
        NewPropertyStatistics propStat5 = new NewPropertyStatistics(PROPERTY_NAME_NAME_5, Integer.class);
        siteSubVault.addPropertyStatistics(propStat5);
        
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, 1);
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2, 1);
        indexProperty(propVault, NEIGHBOURS, PROPERTY_NAME_NAME_2, INTEGER_PROPERTY_VALUE_NETWORK, 1);
        indexProperty(networkSubVault, SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2, 1);
        indexProperty(networkSubVault, SITE, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3, 1);
        indexProperty(networkSubVault, SITE, PROPERTY_NAME_NAME_5, INTEGER_PROPERTY_VALUE_NETWORK_2, 1);
    }
    
    /**
     * testing method getCount() positive tests (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getCountNotStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getCountNotStandartStructurePositiveTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedPropStatCount = 3;
        
        int propCount = propVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with prop vault)", 
        		expectedPropStatCount, propCount);
        
        int networkCount = networkSubVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCount);
        
        int neighboursCount = neighboursSubVault.getCount();
        Assert.assertEquals("getCount() return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCount);
        
        LOGGER.debug("finish test getCountNotStandartStructurePositiveTest()");

    }
    
    /**
     * testing method getNodeCount(String nodeType) positive tests (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getNodeCountNotStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getNodeCountNotStandartStructurePositiveTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedPropStatCount = 3;
        
        int propCountProperty = propVault.getNodeCount(PROPERTIES);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with main vault)", 
        		expectedPropStatCount, propCountProperty);
        
        expectedPropStatCount = 0;
        int networkCountProperty = neighboursSubVault.getNodeCount(NETWORK);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCountProperty);
        
        expectedPropStatCount = 3;
        int neighboursCountProperty = neighboursSubVault.getNodeCount(NEIGHBOURS);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCountProperty);
        
        LOGGER.debug("finish test getNodeCountNotStandartStructurePositiveTest()");
    }
    
    /**
     * testing method getPropertyCount(String nodeType, String propertyName) positive tests (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getPropertyCountNotStandartStructurePositiveTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getPropertyCountNotStandartStructurePositiveTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedPropStatCount = 2;
        
        int propCountProperty = propVault.getPropertyCount(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getPropertyCount(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedPropStatCount, propCountProperty);
        
        expectedPropStatCount = 0;
        int networkCountProperty = neighboursSubVault.getPropertyCount(SECTOR, PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getPropertyCount(String nodeType, String propertyName) return wrong count (test with subvault)", 
        		expectedPropStatCount, networkCountProperty);
        
        expectedPropStatCount = 1;
        int neighboursCountProperty = networkSubVault.getPropertyCount(SITE, PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getNodeCount(String nodeType) return wrong count (test with subvault)", 
        		expectedPropStatCount, neighboursCountProperty);
        
        LOGGER.debug("finish test getPropertyCountNotStandartStructurePositiveTest()");
    }

    
    /**
     * testing method getAllProperties() (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 6;
        Map<Object, Integer> allProperties_propVault = propVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allProperties_propVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allProperties_neighboursSubVault = neighboursSubVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with neighbours subVault)", 
        		expectedAllPropStatCount, allProperties_neighboursSubVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allProperties_networkSubVault = networkSubVault.getAllProperties();
        Assert.assertEquals("getAllProperties() return wrong count (test with network subVault)", 
        		expectedAllPropStatCount, allProperties_networkSubVault.size());
        LOGGER.debug("finish test getAllPropertiesNotStandartStructureTest()");
    }
    
    /**
     * testing method getAllProperties(String nodeType) (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithNodeNameNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithNodeNameNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 3;
        Map<Object, Integer> allPropertiesWithNodeType_propVault = propVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithNodeType_neighboursSubVault = neighboursSubVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with neighbours subVault). Expected zero.", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_neighboursSubVault.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithNodeType_networkSubVault = networkSubVault.getAllProperties(NEIGHBOURS);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with network subVault). Expected zero.", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_networkSubVault.size());
        
        expectedAllPropStatCount = 3;
        Map<Object, Integer> allPropertiesWithNodeType_propVault2 = propVault.getAllProperties(NETWORK);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault2.size());
        
        expectedAllPropStatCount = 1;
        Map<Object, Integer> allPropertiesWithNodeType_propVault3 = propVault.getAllProperties(SECTOR);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault3.size());
        
        expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithNodeType_propVault4 = propVault.getAllProperties(SITE);
        Assert.assertEquals("getAllProperties(String nodeType) return wrong count (test with prop vault)", 
        		expectedAllPropStatCount, allPropertiesWithNodeType_propVault4.size());
        
        LOGGER.debug("finish test getAllPropertiesWithNodeNameNotStandartStructureTest()");
    }


    /**
     * testing method getAllPropertiesWithName(String propertyName) (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithPropertyNameNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithPropertyNameNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        getAllPropertiesWithPropertyNameStandartAndNotStandartStructureTest();
        LOGGER.debug("finish test getAllPropertiesWithPropertyNameNotStandartStructureTest()");
    }
    
    /**
     * testing method getAllProperties(Class< ? > klass) (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithTypeOfClassNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 4;
        Map<Object, Integer> allPropertiesWithKlass_class1 = propVault.getAllProperties(String.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class1.size());
       
        expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithKlass_class2 = propVault.getAllProperties(Integer.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class2.size());
        
        expectedAllPropStatCount = 0;
        Map<Object, Integer> allPropertiesWithKlass_class3 = propVault.getAllProperties(Double.class);
        Assert.assertEquals("getAllProperties(Class< ? > klass) return wrong count (test with subvault)", 
        		expectedAllPropStatCount, allPropertiesWithKlass_class3.size());
        
        LOGGER.debug("finish test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
    }
    
    /**
     * testing method getAllProperties(Class< ? > klass) (not-standart structure)
     *
     * @throws IndexPropertyException
     * @throws InvalidStatisticsParameterException
     */
    @Test
    public void getAllPropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test getAllPropertiesWithTypeOfClassNotStandartStructureTest()");
        createNotStandartStructureOfStatistics();
        
        int expectedAllPropStatCount = 2;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name1 = propVault.getAllProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name1.size());
        
        expectedAllPropStatCount = 1;
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name2 = propVault.getAllProperties(NEIGHBOURS, PROPERTY_NAME_NAME_2);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name2.size());
        
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name3 = propVault.getAllProperties(SECTOR, PROPERTY_NAME_NAME_3);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name3.size());
        
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name4 = propVault.getAllProperties(SITE, PROPERTY_NAME_NAME_4);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name4.size());
        
        Map<Object, Integer> allPropertiesWithPropertyNameAndNodeType_name5 = propVault.getAllProperties(SITE, PROPERTY_NAME_NAME_5);
        Assert.assertEquals("getAllProperties(String nodeType, String propertyName) return wrong count (test with main vault)", 
        		expectedAllPropStatCount, allPropertiesWithPropertyNameAndNodeType_name5.size());
        
        LOGGER.debug("finish test getAllPropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithNodeTypeNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithNodeTypeNotStandartStructureTest()");
       
        createNotStandartStructureOfStatistics();
        int expectedCount = 3;
        propVault.deletePropertiesWithNodeType(NETWORK);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 4;
        propVault.deletePropertiesWithNodeType(SITE);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deletePropertiesWithNodeType(SECTOR);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 3;
        propVault.deletePropertiesWithNodeType(NEIGHBOURS);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        networkSubVault.deletePropertiesWithNodeType(SITE);
        expectedCount = 4;
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        networkSubVault.deletePropertiesWithNodeType(SECTOR);
        expectedCount = 5;
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithNodeTypeNotStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithPropertyNameNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithPropertyNameNotStandartStructureTest()");
       
        createNotStandartStructureOfStatistics();
        int expectedCount = 4;
        propVault.deleteProperties(PROPERTY_NAME_NAME_1);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(PROPERTY_NAME_NAME_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 0;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(PROPERTY_NAME_NAME_4);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 4;
        neighboursSubVault.deleteProperties(PROPERTY_NAME_NAME_1);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        networkSubVault.deleteProperties(PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 0;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        siteSubVault.deleteProperties(PROPERTY_NAME_NAME_5);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);

        
        LOGGER.debug("finish test deletePropertiesWithPropertyNameNotStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest() throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest()");
       
        createNotStandartStructureOfStatistics();
        int expectedCount = 4;
        propVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 6;
        propVault.deleteProperties(NETWORK, PROPERTY_NAME_NAME_4);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(SECTOR, PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 0;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 6;
        propVault.deleteProperties(SITE, PROPERTY_NAME_NAME_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 4;
        neighboursSubVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest()");
    }
    
    @Test
    public void deletePropertiesWithNodeTypeAndPropertyNameAndPropertyValueNotStandartStructureTest() 
    		throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test deletePropertiesWithNodeTypeAndPropertyNameAndPropertyValueNotStandartStructureTest()");
       
        createNotStandartStructureOfStatistics();
        int expectedCount = 5;
        propVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
        int actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_2, INTEGER_PROPERTY_VALUE_NETWORK);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 6;
        propVault.deleteProperties(NETWORK, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 5;
        propVault.deleteProperties(SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 0;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 6;
        propVault.deleteProperties(SITE, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_3);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 2;
        actualCount = siteSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = sectorSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 4;
        neighboursSubVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
        neighboursSubVault.deleteProperties(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2);
        actualCount = propVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 1;
        actualCount = neighboursSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with subvault)", expectedCount, actualCount);
        expectedCount = 3;
        actualCount = networkSubVault.getAllProperties().values().size();
        Assert.assertEquals("deleting properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test deletePropertiesWithNodeTypeAndPropertyNameNotStandartStructureTest()");
    }
    
    @Test
    public void updatePropertiesCountNotStandartStructureTest() 
    		throws IndexPropertyException, InvalidStatisticsParameterException {
        LOGGER.debug("start test updatePropertiesCountNotStandartStructureTest()");
       
        createNotStandartStructureOfStatistics();
        int expectedCount = 2;
        propVault.updatePropertiesCount(SITE, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3, 2);
        int actualCount = propVault.getPropertyValueCount(SITE, PROPERTY_NAME_NAME_4, STRING_PROPERTY_VALUE_NETWORK_3);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 3;
        propVault.updatePropertiesCount(SITE, PROPERTY_NAME_NAME_5, INTEGER_PROPERTY_VALUE_NETWORK_2, 3);
        actualCount = propVault.getPropertyValueCount(SITE, PROPERTY_NAME_NAME_5, INTEGER_PROPERTY_VALUE_NETWORK_2);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 56;
        propVault.updatePropertiesCount(SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2, 56);
        actualCount = propVault.getPropertyValueCount(SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 1;
        propVault.updatePropertiesCount(SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_3, 56);
        actualCount = propVault.getPropertyValueCount(SECTOR, PROPERTY_NAME_NAME_3, STRING_PROPERTY_VALUE_NETWORK_2);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        createNotStandartStructureOfStatistics();
        expectedCount = 126;
        propVault.updatePropertiesCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1, 126);
        actualCount = propVault.getPropertyValueCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NETWORK_1);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 127;
        propVault.updatePropertiesCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2, 127);
        actualCount = propVault.getPropertyValueCount(NEIGHBOURS, PROPERTY_NAME_NAME_1, STRING_PROPERTY_VALUE_NEIGHBOURS_2);
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        expectedCount = 6;
        actualCount = propVault.getAllProperties().size();
        Assert.assertEquals("updating properties work not correctly (test with main vault)", expectedCount, actualCount);
        
        LOGGER.debug("finish test updatePropertiesCountNotStandartStructureTest()");
    }
}
