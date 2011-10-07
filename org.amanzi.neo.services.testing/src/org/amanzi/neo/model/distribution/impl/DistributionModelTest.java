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

package org.amanzi.neo.model.distribution.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DistributionService;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * Tests on Distribution Model
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionModelTest extends AbstractNeoServiceTest {
    
    private static final String DEFAULT_DISTRIBUTION_NAME = "distribution";
    
    private static final String DEFAULT_MODEL_NAME = "mocked network";
    
    private static final int NUMBER_OF_DISTRIBUTION_BARS = 5;
    
    private static final String DISTRIBUTION_BAR_NAME_PREFIX = "distribution_bar_";
    
    private static final Color[] DISTRIBUTION_BAR_COLORS = new Color[] {Color.BLACK, Color.WHITE, Color.RED, null, Color.CYAN};
    
    private static final INodeType DISTRIBUTION_NODE_TYPE = NodeTypes.SECTOR;
    
    private static final int NUMBER_OF_NODES_TO_ANALYSE = 40;
    
    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        clearServices();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreatesDistributionWithoutAnalyzedModel() throws Exception {
        new DistributionModel(null, getDistributionType());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithoutDistributionType() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        new DistributionModel(model, null);
    }
    
    @Test
    public void checkActionsWithExistingDistributionModel() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        
        new DistributionModel(model, getDistributionType());
        
        verify(service).findRootAggregationNode(parentDistribution, DEFAULT_DISTRIBUTION_NAME);
        verify(service, never()).createRootAggregationNode(any(Node.class), any(String.class));
    }
    
    @Test
    public void checkPropertiesForExistingDistributionModel() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        
        assertEquals("Incorrect name of Distribution", DEFAULT_DISTRIBUTION_NAME, distribution.getName());
        assertNotNull("Root Node cannot be null", distribution.getRootNode());
        assertEquals("Incorrect distribution type", distributionType, distribution.getDistributionType());
        assertEquals("Incorrect Type of Distribution", DistributionNodeTypes.ROOT_AGGREGATION, distribution.getType());
    }
    
    @Test
    public void checkActionsWithNewDistributionModel() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), false);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        
        new DistributionModel(model, getDistributionType());
        
        verify(service).findRootAggregationNode(parentDistribution, DEFAULT_DISTRIBUTION_NAME);
        verify(service).createRootAggregationNode(parentDistribution, DEFAULT_DISTRIBUTION_NAME);
    }
    
    @Test
    public void checkPropertiesForNewDistributionModel() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), false);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        
        assertEquals("Incorrect name of Distribution", DEFAULT_DISTRIBUTION_NAME, distribution.getName());
        assertNotNull("Root Node cannot be null", distribution.getRootNode());
        assertEquals("Incorrect distribution type", distributionType, distribution.getDistributionType());
        assertEquals("Incorrect Type of Distribution", DistributionNodeTypes.ROOT_AGGREGATION, distribution.getType());
    }
    
    @Test
    public void checkNoWorkWithAnalyzedModelIfDistriubtionAlreadyExist() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        distribution.getDistributionBars();
        
        verify(model).getRootNode();
        verifyNoMoreInteractions(model);
    }
    
    @Test
    public void checkServiceActionsWithExistingDatabase() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        distribution.getDistributionBars();
        
        verify(service).findAggregationBars(rootAggregation);
    }
    
    @Test
    public void checkDistributionBarsSizeWithExistingDatabase() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, getDistributionBarNodes(), true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        assertNotNull("List of distribution bars cannot be null", distributionBars);
        assertEquals("Unexpected size of Bars", NUMBER_OF_DISTRIBUTION_BARS, distributionBars.size());
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            assertNotNull("Distribution bar cannot be null", distributionBars.get(i));
        }
    }
    
    @Test
    public void checkRootOfDistributionBarsExistingDatabase() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            IDataElement rootElement = distributionBars.get(i).getRootElement();
            Node rootNode = ((DataElement)rootElement).getNode();
            
            assertEquals("Incorrect Root node of Distribution bar", distributionBarNodes.get(i), rootNode);
        }
    }
    
    @Test
    public void checkPropertiesOfDistributionBarsExistingDatabase() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            IDistributionBar distributionBar = distributionBars.get(i);
            
            Color color = DISTRIBUTION_BAR_COLORS[i];
            if (color == null) {
                color = DistributionBar.DEFUALT_COLOR;
            }
            
            assertEquals("Incorrect Color of Bar", color, distributionBar.getColor());
            assertEquals("Incorrect count of Bar", i, distributionBar.getCount());
            assertEquals("Incorrect Name of Bar", DISTRIBUTION_BAR_NAME_PREFIX + i, distributionBar.getName());
        }
    }
    
    @Test
    public void checkNumberOfCreatedDistributionBars() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, false);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        assertNotNull("List of Bars cannot be null", distributionBars);
        assertEquals("Incorrect size of Bars", NUMBER_OF_DISTRIBUTION_BARS, distributionBars.size());
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) {
            assertNotNull("Distribution bar cannot be null", distributionBars.get(i));
        }
    }
    
    @Test
    public void checkCreatedDataElement() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, false);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            IDataElement rootElement = distributionBars.get(i).getRootElement();
            Node rootNode = ((DataElement)rootElement).getNode();
            
            assertEquals("Incorrect Root node of Distribution bar", distributionBarNodes.get(i), rootNode);
        }
    }
    
    @Test
    public void checkPropertiesOfCreatedBars() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, false);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        List<IDistributionBar> distributionBars = distribution.getDistributionBars();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            IDistributionBar distributionBar = distributionBars.get(i);
            
            Color color = DISTRIBUTION_BAR_COLORS[i];
            if (color == null) {
                color = DistributionBar.DEFUALT_COLOR;
            }
            
            assertEquals("Incorrect Color of Bar", color, distributionBar.getColor());
            assertEquals("Incorrect count of Bar", i, distributionBar.getCount());
            assertEquals("Incorrect Name of Bar", DISTRIBUTION_BAR_NAME_PREFIX + i, distributionBar.getName());
        }
    }
    
    @Test
    public void checkCreateActionOfService() throws Exception {
        Node parentDistribution = getNode();
        Node rootAggregation = getNode(DistributionNodeTypes.ROOT_AGGREGATION);
        List<Node> distributionBarNodes = getDistributionBarNodes();
        DistributionService service = getDistributionService(parentDistribution, rootAggregation, distributionBarNodes, true);
        DistributionModel.distributionService = service;
        IDistributionalModel model = getDistributionalModel(parentDistribution);
        IDistribution distributionType = getDistributionType();
        
        DistributionModel distribution = new DistributionModel(model, distributionType);
        distribution.getDistributionBars();
        
        Node previous = null;
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) { 
            verify(service).createAggregationBarNode(eq(rootAggregation), eq(previous), any(IDistributionBar.class));
            previous = distributionBarNodes.get(i);
        }
    }
    
    /**
     * Returns mocked list of Distribution Bars
     *
     * @return
     */
    private List<Node> getDistributionBarNodes() {
        ArrayList<Node> result = new ArrayList<Node>();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) {
            result.add(getDistributionBarNode(i));
        }
        
        return result;
    }
    
    /**
     * Returns mocked Aggregation Bar
     *
     * @return
     */
    private Node getDistributionBarNode(int index) {
        Node result = getNode(DistributionNodeTypes.AGGREGATION_BAR);
        
        when(result.getProperty(DistributionModel.BAR_COLOR, null)).thenReturn(getColorArray(DISTRIBUTION_BAR_COLORS[index]));
        when(result.getProperty(DistributionModel.COUNT, null)).thenReturn(index);
        when(result.getProperty(NewAbstractService.NAME, null)).thenReturn(DISTRIBUTION_BAR_NAME_PREFIX + index);
        
        return result;
    }
    
    private int[] getColorArray(Color color) {
        if (color != null) {
            return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
        } else {
            return null;
        }
    }
    
    /**
     * Creates mocked Distribution Service for testing constructor
     *
     * @param distributionModelRoot
     * @param rootAggregationNode
     * @param shouldFind
     * @return
     */
    private DistributionService getDistributionService(Node distributionModelRoot, Node rootAggregationNode, List<Node> distributionBars, boolean shouldFind) {
        DistributionService service = mock(DistributionService.class);
        
        Node aggregationForFind = null;
        if (shouldFind) {
            aggregationForFind = rootAggregationNode;
        }
        when(service.findRootAggregationNode(distributionModelRoot, DEFAULT_DISTRIBUTION_NAME)).thenReturn(aggregationForFind);
        
        Iterable<Node> distributionBarNodes = new ArrayList<Node>();
        if (shouldFind) {
            distributionBarNodes = distributionBars;
        }
        when(service.findAggregationBars(rootAggregationNode)).thenReturn(distributionBarNodes);
        
        if (!shouldFind) {
            when(service.createRootAggregationNode(distributionModelRoot, DEFAULT_DISTRIBUTION_NAME)).thenReturn(rootAggregationNode);
            
            Node previous = null;
            for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) {
                when(service.createAggregationBarNode(eq(rootAggregationNode), eq(previous), any(IDistributionBar.class))).thenReturn(distributionBars.get(i));
                previous = distributionBars.get(i);
            }
        }
        
        return service;
    }
    
    /**
     * Creates mocked Node
     *
     * @return
     */
    private Node getNode(INodeType nodeType) {
        Node result = mock(Node.class);
        
        if (nodeType != null) {
            when(result.getProperty(NewAbstractService.TYPE, StringUtils.EMPTY)).thenReturn(nodeType.getId());
        }
        when(result.getProperty(DistributionModel.COUNT, 0)).thenReturn(NUMBER_OF_DISTRIBUTION_BARS);
        
        return result;
    }
    
    /**
     * Creates mocked Node
     *
     * @return
     */
    private Node getNode() {
        return getNode(null);
    }
    
    /**
     * Create mocked Distributional Model with default Node
     *
     * @return
     */
    private IDistributionalModel getDistributionalModel() {
        Node node = getNode();
        return getDistributionalModel(node);
    }
    
    /**
     * Create mocked Distributional Model
     *
     * @return
     */
    private IDistributionalModel getDistributionalModel(Node rootNode) {
        IDistributionalModel result = mock(IDistributionalModel.class);
        
        when(result.getRootNode()).thenReturn(rootNode);
        when(result.getName()).thenReturn(DEFAULT_MODEL_NAME);
        
        return result;
    }
    
    /**
     * Creates mocked Range
     *
     * @param index
     * @return
     */
    private IRange createRange(int index) {
        IRange result = mock(IRange.class);
        
        when(result.getName()).thenReturn(DISTRIBUTION_BAR_NAME_PREFIX + index);
        when(result.getColor()).thenReturn(DISTRIBUTION_BAR_COLORS[index]);
        
        return result;
    }
    
    /**
     * Creates list of mocked Ranges
     *
     * @return
     */
    private List<IRange> getRanges() {
        ArrayList<IRange> result = new ArrayList<IRange>();
        
        for (int i = 0; i < NUMBER_OF_DISTRIBUTION_BARS; i++) {
            result.add(createRange(i));
        }
        
        return result;
    }
    
    /**
     * Creates mocked Distribution Type
     *
     * @return
     */
    private IDistribution getDistributionType() {
        IDistribution result = mock(IDistribution.class);
        
        when(result.getName()).thenReturn(DEFAULT_DISTRIBUTION_NAME);
        
        List<IRange> range = getRanges();
        when(result.getRanges()).thenReturn(range);
        
        when(result.getNodeType()).thenReturn(DISTRIBUTION_NODE_TYPE);
        when(result.getCount()).thenReturn(NUMBER_OF_NODES_TO_ANALYSE);
                
        return result;
    }

}
