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

package org.amanzi.awe.views.calls.testing;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.upload.StatisticsDataLoader;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSLoader;
import org.amanzi.neo.loader.AMSXMLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Load and compare 2 statistics in file message.properties: CSV_ROOT - path to csv dir
 * XML_ROOT= path to xml dir
 * LOG_ROOT= path to AMS LOG dir
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class StatisticsTest extends AmsStatisticsTest {
    private long stat1TimeCorrelator = 0;
    private StatisticsCallType cellType;
    private HashSet<Node> handleRow;

    /**
     * Prepare operations before execute test.
     */
    @Before
    public void prepareTests() {
        prepareMainDirectory();
        initProjectService();
        handleRow = new HashSet<Node>();
    }

    @Test
    public void testCompareStatistics() throws IOException, ParseException {
        stat1TimeCorrelator = Long.parseLong(Messages.getString("StatisticsTest.set1_correlation")); //$NON-NLS-1$
        CallStatistics stat1 = createStatistics(loadXMLData());
        CallStatistics stat2 = createStatistics(loadCSVData());
        compareStatistics(stat1, stat2);

    }

    @Test
    public void testCompareLogStatistics() throws IOException, ParseException {
        CallStatistics stat1 = createStatistics(loadLogData());
        CallStatistics stat2 = createStatistics(loadCSVData());
        compareStatistics(stat1, stat2);

    }


    /**
     * Creates the statistics.
     *
     * @param dataset the dataset
     * @return the call statistics
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private CallStatistics createStatistics(Node dataset) throws IOException {
        return new CallStatistics(dataset, getNeo(), true);
    }


    /**
     * Compare statistics.
     *
     * @param stat1 the stat1
     * @param stat2 the stat2
     */
    private void compareStatistics(CallStatistics stat1, CallStatistics stat2) {
        Transaction tx = getNeo().beginTx();
        try {
            StringBuilder errors = new StringBuilder();
            for (StatisticsCallType type : StatisticsCallType.values()) {
                cellType = type;
                Node node1 = stat1.getPeriodNode(CallTimePeriods.HOURLY, type);
                Node node2 = stat2.getPeriodNode(CallTimePeriods.HOURLY, type);
                if (node1 == null && node2 == null) {
                    continue;
                }
                if (node1 == null) {
                    errors
                            .append('\n')
                            .append("Type: ").append(getCallType()).append(" ").append(String.format("AMS Statistic: not found root node for periods=%s and type=%s", CallTimePeriods.HOURLY, type)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    continue;
                } else if (node2 == null) {
                    errors
                            .append('\n')
                            .append("Type: ").append(getCallType()).append(" ").append(String.format("CSV Statistic: not found root node for periods=%s and type=%s", CallTimePeriods.HOURLY, type)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    continue;
                }
                compareRootNode(node1, node2, errors);
            }
            Assert.assertTrue(errors.toString(), errors.length() == 0);
        } finally {
            tx.finish();
        }
    }

    /**
     * Compare root node.
     *
     * @param node1 the root of stat1
     * @param node2 the root of stat2
     * @param errors the errors
     */
    private void compareRootNode(Node node1, Node node2, StringBuilder errors) {
        Assert.assertNotNull(node1);
        Assert.assertNotNull(node2);
        for (Node sRow1 : NeoUtils.getChildTraverser(node1)) {
            String probeName = getProbeName(sRow1);
            long time1 = NeoUtils.getNodeTime(sRow1) + stat1TimeCorrelator;
            Node sRow2 = findSrow(node2, time1, probeName);
            if (sRow2 == null) {
                errors.append('\n')
                        .append("Type: ").append(getCallType()).append(" ").append(String.format("Probe %s, Not found same row in Stst2 for row %s", probeName, NeoUtils.getNodeName(sRow1))); //$NON-NLS-1$ //
                continue;
            }
            handleRow.add(sRow2);
            compareSRow(sRow1, sRow2, errors, probeName);
        }
        for (Node sRow2 : NeoUtils.getChildTraverser(node2)) {
            if (!handleRow.contains(sRow2)) {
                errors.append('\n')
                        .append("Type: ").append(getCallType()).append(" ").append(String.format("Not found in AMS Statistics rows %s", NeoUtils.getNodeName(sRow2))); //$NON-NLS-1$  
            }
        }
        handleRow.clear();

    }

    /**
     * Find srow.
     *
     * @param node2 the stat node
     * @param time1  - sRow time
     * @param probeName the probe name 
     * @return the node
     */
    private Node findSrow(Node node2, final long time1, final String probeName) {
        final boolean notLevel1 = !getCallType().getLevel().equals(StatisticsCallType.FIRST_LEVEL);
        final boolean haveLa = notLevel1 || probeName.contains(" ");
        Iterator<Node> it = NeoUtils.getChildTraverser(node2, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node srow = currentPos.currentNode();
                Long time = NeoUtils.getNodeTime(srow);

                if (time != time1) {
                    return false;
                }
                if (notLevel1) {
                    return true;
                }
                String name = getProbeName(srow);
                if (name == null) {
                    System.out.println("Not found probe for srow " + srow + " " + NeoUtils.getNodeName(srow));
                    return false;
                }
                return haveLa ? probeName.equals(name) : name.startsWith(probeName);
            }
        }).iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Gets the probe name.
     * 
     * @param sRow1 the s row1 - sRow
     * @return the probe name or null if no source with type=probe
     */
    private String getProbeName(Node sRow1) {
        if (StatisticsCallType.FIRST_LEVEL.equals(getCallType().getLevel())) {
            Iterator<Node> it = sRow1.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return NeoUtils.isProbeNode(currentPos.currentNode());
                }
            }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).iterator();
            return it.hasNext() ? NeoUtils.getNodeName(it.next()) : null;
        } else {
            return null;
        }
    }

    /**
     * Compare s row.
     *
     * @param sRow1 the s row1 sRow from statistic1
     * @param sRow2 the s row2 sRow from statistic2
     * @param errors the errors - error collector
     * @param probeName the probe name probe name
     */
    private void compareSRow(Node sRow1, Node sRow2, StringBuilder errors, String probeName) {
        String name1 = NeoUtils.getNodeName(sRow1);
        String name2 = NeoUtils.getNodeName(sRow2);
        HashMap<IStatisticsHeader, Number> map1 = buildCellDataMap(sRow1);
        HashMap<IStatisticsHeader, Number> map2 = buildCellDataMap(sRow2);
        for (IStatisticsHeader header : getCallType().getHeaders()) {
            Number value1 = map1.get(header);
            Number value2 = map2.get(header);
            boolean isEqual = value1 == null ? value2 == null : value1.equals(value2);
            if (!isEqual) {
                errors.append('\n');
                if (getCallType().getLevel().equals(StatisticsCallType.FIRST_LEVEL)) {
                    errors.append("Probe: ").append(probeName).append(" ");
                }
                errors
                        .append("Type: ").append(getCallType()).append(" ").append(String.format("Headers %s is not equals for '%s'=%s, and '%s'=%s", header, name1, value1, name2, value2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

        }

    }

    /**
     * Finish all tests.
     */
    @AfterClass
    public static void finishAll() {
        clearMainDirectory();
    }


    /**
     * Load csv data.
     *
     * @return the call dataset node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Node loadCSVData() throws IOException {
        String dataDir = Messages.getString("CSV_ROOT"); //$NON-NLS-1$
        StatisticsDataLoader loader = new StatisticsDataLoader(dataDir, "test", "test network", getNeo(), true); //$NON-NLS-1$ //$NON-NLS-2$
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
    }

    /**
     * Load xml data.
     *
     * @return the call dataset node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Node loadXMLData() throws IOException {
        String dataDir = Messages.getString("XML_ROOT"); //$NON-NLS-1$
        AMSXMLoader loader = new AMSXMLoader(dataDir, null, "testXML", "testXMLNetwork", getNeo(), true); //$NON-NLS-1$ //$NON-NLS-2$
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
    }

    /**
     * Load log data.
     *
     * @return the call dataset node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Node loadLogData() throws IOException {
        String dataDir = Messages.getString("LOG_ROOT"); //$NON-NLS-1$
        AMSLoader loader = new AMSLoader(dataDir, "testLOG", "testLOGNetwork", getNeo()); //$NON-NLS-1$ //$NON-NLS-2$
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
    }

    /**
     * Finish test.
     */
    @After
    public void finish() {
        shutdownNeo();
    }

    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        return null;
    }

    @Override
    protected StatisticsCallType getCallType() {
        return cellType;
    }

    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance, Integer aProbes, String dataDir) {
        return null;
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        return null;
    }

    @Override
    protected boolean hasSecondLevelStatistics() {
        return false;
    }

}
