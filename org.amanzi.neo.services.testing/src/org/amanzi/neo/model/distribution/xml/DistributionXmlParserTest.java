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

package org.amanzi.neo.model.distribution.xml;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.xml.schema.Bar;
import org.amanzi.neo.model.distribution.xml.schema.Bars;
import org.amanzi.neo.model.distribution.xml.schema.Color;
import org.amanzi.neo.model.distribution.xml.schema.Data;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.model.distribution.xml.schema.Filter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Tests for DistributionXmlParser
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class DistributionXmlParserTest extends AbstractNeoServiceTest {

    private static final String DEF_DATA_DATA_TYPE = "NETWORK";
    private static final String DEF_DATA_NAME = "test_distr";
    private static final String DEF_DATA_NODE_TYPE = "SECTOR";
    private static final String DEF_DATA_PROP_NAME = "azimut";
    private static final String DEF_BAR_NAME = "BAR NAME";
    private static final int DEF_COLOR_RED = 100;
    private static final int DEF_COLOR_GREEN = 150;
    private static final int DEF_COLOR_BLUE = 200;
    private static final String DEF_FILTER_EXP_TYPE = "OR";
    private static final String DEF_FILTER_TYPE = "EQUALS";
    private static final String DEF_FILTER_PROP_NAME = "azimut";
    private static final String DEF_FILTER_VALUE = "13.44";
    private static final String DEF_FILTER_NODE_TYPE = "SECTOR";
    private static final String DEF_UNDERFILTER_TYPE = "MORE";
    private static final String DEF_UNDERFILTER_NODE_TYPE = "SECTOR";
    private static final String DEF_UNDERFILTER_PROP_NAME = "azimut2";
    private static final String DEF_UNDERFILTER_VALUE = "20";

    private static final INodeType DEF_MODEL_TYPE = DatasetTypes.NETWORK;
    private static final INodeType DEF_NODE_TYPE = NetworkElementNodeType.SECTOR;
    private static final INodeType DEF_INC_MODEL_TYPE = DatasetTypes.DRIVE;
    private static final INodeType DEF_INC_NODE_TYPE = NetworkElementNodeType.CITY;
    private static final String DEF_INC_PROP_NAME = "azimut234";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
        clearServices();
        NetworkElementNodeType.BSC.getId();
    }

    private Distribution createCorrectDistribution() {
        Distribution distr = new Distribution();
        Data data = new Data();
        data.setDataType(DEF_DATA_DATA_TYPE);
        data.setName(DEF_DATA_NAME);
        data.setNodeType(DEF_DATA_NODE_TYPE);
        data.setPropertyName(DEF_DATA_PROP_NAME);
        distr.setData(data);
        Bars bars = new Bars();
        Bar bar = new Bar();
        bar.setName(DEF_BAR_NAME);
        bar.setColor(new Color(DEF_COLOR_RED, DEF_COLOR_GREEN, DEF_COLOR_BLUE));
        Filter filter = new Filter();
        filter.setExpressionType(DEF_FILTER_EXP_TYPE);
        filter.setFilterType(DEF_FILTER_TYPE);
        filter.setPropertyName(DEF_FILTER_PROP_NAME);
        filter.setNodeType(DEF_FILTER_NODE_TYPE);
        filter.setValue(DEF_FILTER_VALUE);
        Filter underFilter = new Filter();
        underFilter.setFilterType(DEF_UNDERFILTER_TYPE);
        underFilter.setPropertyName(DEF_UNDERFILTER_PROP_NAME);
        underFilter.setNodeType(DEF_UNDERFILTER_NODE_TYPE);
        underFilter.setValue(DEF_UNDERFILTER_VALUE);
        filter.setUnderlyingFilter(underFilter);
        bar.setFilter(filter);
        bars.getBar().add(bar);
        distr.setBars(bars);

        return distr;
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullDistribution() throws Exception {
        Distribution distr = null;
        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullBars() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.setBars(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullData() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.setData(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullName() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getData().setName(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullDataType() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getData().setDataType(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullNodeType() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getData().setNodeType(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullBarList() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getBars().getBar().clear();

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullBarName() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getBars().getBar().get(0).setName(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullFilterNodeType() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getBars().getBar().get(0).getFilter().setNodeType(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test(expected = DistributionXmlParsingException.class)
    public void checkWithNullFilterPropertyName() throws Exception {
        Distribution distr = createCorrectDistribution();
        distr.getBars().getBar().get(0).getFilter().setPropertyName(null);

        DistributionXmlParser.checkConstraints(distr);
    }

    @Test
    public void checkCompatibility() {
        assertTrue(DistributionXmlParser.checkCompatibility(getDistributionalModel(DEF_MODEL_TYPE), DEF_NODE_TYPE,
                DEF_DATA_PROP_NAME, createCorrectDistribution()));
    }

    @Test
    public void checkCompatibilityWithIncorrectModelType() {
        assertFalse(DistributionXmlParser.checkCompatibility(getDistributionalModel(DEF_INC_MODEL_TYPE), DEF_NODE_TYPE,
                DEF_DATA_PROP_NAME, createCorrectDistribution()));
    }

    @Test
    public void checkCompatibilityWithIncorrectNodeType() {
        assertFalse(DistributionXmlParser.checkCompatibility(getDistributionalModel(DEF_MODEL_TYPE), DEF_INC_NODE_TYPE,
                DEF_DATA_PROP_NAME, createCorrectDistribution()));
    }

    @Test
    public void checkCompatibilityWithIncorrectPropertyName() {
        assertFalse(DistributionXmlParser.checkCompatibility(getDistributionalModel(DEF_MODEL_TYPE), DEF_NODE_TYPE,
                DEF_INC_PROP_NAME, createCorrectDistribution()));
    }

    /**
     * Creates mocked DistributionalModel
     * 
     * @return
     */
    private IDistributionalModel getDistributionalModel(INodeType type) {
        IDistributionalModel model = mock(IDistributionalModel.class);

        when(model.getType()).thenReturn(type);

        return model;
    }

}
