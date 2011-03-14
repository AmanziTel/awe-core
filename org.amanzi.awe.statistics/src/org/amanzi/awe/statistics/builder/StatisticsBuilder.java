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

package org.amanzi.awe.statistics.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.awe.statistics.database.IDENService;
import org.amanzi.awe.statistics.database.RomesService;
import org.amanzi.awe.statistics.database.StatisticsEntityFactory;
import org.amanzi.awe.statistics.database.entity.DatasetStatistics;
import org.amanzi.awe.statistics.database.entity.Dimension;
import org.amanzi.awe.statistics.database.entity.Level;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.awe.statistics.engine.IDatasetService;
import org.amanzi.awe.statistics.engine.KpiBasedHeader;
import org.amanzi.awe.statistics.exceptions.IncorrectInputException;
import org.amanzi.awe.statistics.template.Condition;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.awe.statistics.template.Threshold;
import org.amanzi.awe.statistics.template.Template.DataType;
import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.OssType;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

/**
 * Statistics Builder
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsBuilder {
    private GraphDatabaseService neo;
    private IDatasetService dsService;
    private Node dataset;
    private Ruby ruby;
    private Transaction mainTx;
    private DatasetStatistics datasetStatistics;

    /**
     * @param neo
     * @param dataset
     * @param ruby TODO
     */
    public StatisticsBuilder(GraphDatabaseService neo, Node dataset, Ruby ruby) {
        this.neo = neo;
        this.ruby = ruby;
        this.dataset = dataset;

    }
    /**
     * @param neo
     * @param dataset
     * @param ruby TODO
     */
    public StatisticsBuilder(GraphDatabaseService neo, Node dataset) {
        this.neo = neo;
        this.ruby = KPIPlugin.getDefault().getRubyRuntime();
        this.dataset = dataset;
        
    }

    /**
     * Builds a 3-dimensional statistics for a given dataset, network level and time period based on
     * a template
     * 
     * @param template statistics template
     * @param networkLevelName network level
     * @param timeLevelName time period/level
     * @param monitor progress monitor
     * @param dataset the dataset node
     */
    public Statistics buildStatistics(Template template, String networkLevelName, CallTimePeriods timeLevelName,
            IProgressMonitor monitor) {
        // validate(dataset, template);
        mainTx = neo.beginTx();
        try {
            dsService = getDatasetService(dataset);
//            monitor.beginTask("Building statistics", /(Integer)dataset.getProperty(INeoConstants.PROPERTY_COUNT_NAME,IProgressMonitor.UNKNOWN));
            monitor.beginTask("Building statistics", IProgressMonitor.UNKNOWN);
            datasetStatistics = findOrCreateStatisticsRoot(dataset, template);
            Dimension networkDimension = datasetStatistics.getNetworkDimension();
            Dimension timeDimension = datasetStatistics.getTimeDimension();
            Level nLevel = findOrCreateLevel(networkLevelName, networkDimension);

            Statistics levelStatistics = nLevel.getStatistics(timeLevelName.getId());
            if (levelStatistics == null) {
                long start = System.currentTimeMillis();
                long minTime = (Long)dataset.getProperty(INeoConstants.MIN_TIMESTAMP);
                long maxTime = (Long)dataset.getProperty(INeoConstants.MAX_TIMESTAMP);
                levelStatistics = buildStatisticsForPeriod(template, minTime, maxTime, timeLevelName, networkLevelName,
                        networkDimension, timeDimension, monitor);
                System.out.println(timeLevelName.getId() + "/" + networkLevelName + ": total time in seconds: "
                        + (System.currentTimeMillis() - start) / 1000);
            }

            return levelStatistics;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            monitor.done();
            commit(false);
        }
        return null;
    }

    protected void commit(boolean restart) {
        if (mainTx != null) {
            mainTx.success();
            mainTx.finish();
            if (restart) {
                mainTx = neo.beginTx();
            } else {
                mainTx = null;
            }
        }
    }

    private IDatasetService getDatasetService(Node dataset) {
        String type = (String)dataset.getProperty(INeoConstants.PROPERTY_TYPE_NAME);
        if (dataset.hasProperty(INeoConstants.DRIVE_TYPE) && dataset.getProperty(INeoConstants.DRIVE_TYPE).equals("romes")) {
            return new RomesService(neo, dataset);
        }
        return new IDENService(neo, dataset);
    }

    /**
     * Validates if a dataset matches a template
     * 
     * @param dataset the dataset to be validated
     * @param template the template
     * @throws IncorrectInputException if input dataset does not match template
     */
    private void validate(Node dataset, Template template) throws IncorrectInputException {
        DataType type = template.getType();
        String typeName = type.getTypeName();
        switch (type) {
        case NEMO1:
        case NEMO2:
        case ROMES:
        case TEMS:
            String driveType = dataset.getProperty(INeoConstants.DRIVE_TYPE).toString();
            checkTypes(typeName, driveType);
            break;
        case GPEH:
        case RNC_COUNTERS:
        case PERFORMANCE_COUNTERS:
            String ossType = dataset.getProperty(OssType.PROPERTY_NAME).toString();
            checkTypes(typeName, ossType);
            break;
        }
    }

    /**
     * @param templateType
     * @param driveType
     * @throws IncorrectInputException
     */
    private void checkTypes(String templateType, String driveType) throws IncorrectInputException {
        if (!templateType.equalsIgnoreCase(driveType)) {
            throw new IncorrectInputException("The dataset type ('" + driveType + "') does not match the template type ('"
                    + templateType.toLowerCase() + "')");
        }
    }

    private DatasetStatistics findOrCreateStatisticsRoot(Node dataset, Template template) {
        for (Relationship rel : dataset.getRelationships(GeoNeoRelationshipTypes.ANALYSIS, Direction.OUTGOING)) {
            String templateName = rel.getEndNode().getProperty("template").toString();
            if (templateName.equals(template.getTemplateName())) {
                return new DatasetStatistics(rel.getEndNode());
            }
        }
        return StatisticsEntityFactory.createStatisticsRoot(neo, template, dataset);
    }

    /**
     * Finds appropriated network element for a given dataset node if any.
     * 
     * @param sourceNode a dataset node
     * @param networkLevel a network level
     * @return the node found or null if it was not found
     */
    private Node findNetworkNode(Node sourceNode, final String networkLevel) {
        Iterator<Relationship> iterator = sourceNode.getRelationships(CorrelationRelationshipTypes.CORRELATED, Direction.INCOMING)
                .iterator();
        if (iterator.hasNext()) {
            Node sectorProxyNode = iterator.next().getStartNode();// correlated sector proxy node
            Relationship rel = sectorProxyNode.getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING);
            if (rel != null) {
                Node sectorNode = rel.getEndNode();
                Iterator<Node> iter = sectorNode.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        String type = (String)currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME);
                        if (networkLevel.equals(type)) {
                            return true;
                        }
                        if (NodeTypes.NETWORK.getId().equals(type)) {
                            return true;
                        }
                        return false;
                    }

                }, new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        String type = (String)currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME);
                        return networkLevel.equals(type);
                    }

                }, GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).iterator();
                if (iter.hasNext()) {
                    return iter.next();
                }
            }
        }
        return null;

    }

    private Statistics buildStatisticsForPeriod(Template template, long startTime, long endTime, CallTimePeriods period,
            String networkLevel, Dimension networkDimension, Dimension timeDimension, IProgressMonitor monitor) {

        final CallTimePeriods underlyingPeriod = period.getUnderlyingPeriod();
        if (underlyingPeriod != null) {
            Level nLevel = findOrCreateLevel(networkLevel, networkDimension);

            Statistics uStatistics = nLevel.getStatistics(underlyingPeriod.getId());
            if (uStatistics == null) {
                uStatistics = buildStatisticsForPeriod(template, startTime, endTime, underlyingPeriod, networkLevel,
                        networkDimension, timeDimension, monitor);
            }
            final String task = "Building stats for " + period.getId() + "/" + networkLevel;
            System.out.println(task);
            monitor.subTask(task);
            Statistics statistics = buildHighLevelPeriodStatistics(template, startTime, endTime, period, networkLevel,
                    networkDimension, timeDimension, uStatistics);
            updateFlags(statistics);
            return statistics;

        } else {
            final String task = "Building stats for " + period.getId() + "/" + networkLevel;
            System.out.println(task);
            monitor.subTask(task);
            Level tLevel = findOrCreateLevel(period.getId(), timeDimension);
            Statistics statistics = StatisticsEntityFactory.createStatistics(neo, networkDimension.getLevelByKey(networkLevel),
                    tLevel, datasetStatistics);
            Map<String, StatisticsRow> summaries = new HashMap<String, StatisticsRow>();

            String hash = createScriptForTemplate(template);
            long noUsedNodes=0;
            long currentStartTime = period.getFirstTime(startTime);
            long nextStartTime = getNextStartDate(period, endTime, currentStartTime);
            if (startTime > currentStartTime) {
                currentStartTime = startTime;
            }
            long count = 0;
            int nodesCount = 0;
            int comm = 0;
            do {
                if (comm > 500) {
                    commit(true);
                    comm = 0;
                }
                long startForPeriod = System.currentTimeMillis();
                System.out.println("currentStartTime=" + currentStartTime + "\tnextStartTime=" + nextStartTime + "\tendTime="
                        + endTime);
                // if(monitor.isCanceled()){
                // break;
                // }

                long t = System.currentTimeMillis();
                Collection<Node> nodes = dsService.getNodes(currentStartTime, nextStartTime);
                nodesCount=nodes.size();
                count += nodesCount;
                int total = 0;
                for (Node node : nodes) {
                    boolean isUsed=false;
                    final String EVALUATE = "Neo4j::load_node(%s).instance_eval {%s}";
                    String script = String.format(EVALUATE, node.getId(), hash);
                    long tt = System.currentTimeMillis();
                    RubyHash result = (RubyHash)ruby.evalScriptlet(script);

                    Node networkNode = findNetworkNode(node, networkLevel);
                    // TODO use key property instead of key node name for non-correlated datasets
                    // String keyProperty = dsService.getKeyProperty(node);
                    StatisticsGroup group;
                    if (networkNode != null) {
                        group = findOrCreateGroup(statistics, networkNode);
                    } else {
                        group = findOrCreateGroup(statistics, node.getProperty(networkLevel, "unknown").toString());
                    }
                    // add summary row first
                    StatisticsRow summaryRow = findOrCreateSummaryRow(group, summaries);
                    StatisticsRow row = findOrCreateRow(group, currentStartTime, period);

                    for (Object key : result.keySet()) {
                        comm++;
                        TemplateColumn column = template.getColumnByName(key.toString());
                        StatisticsCell cell = findOrCreateCell(row, column);
                        StatisticsCell summaryCell = findOrCreateCell(summaryRow, column);
                        Object object = result.get(key);
                        Number value = null;
                        if (object instanceof Number) {
                            value = (Number)object;
                        } else if (object instanceof RubyNumeric) {
                            value = ((RubyNumeric)object).getDoubleValue();
                        }
                        if (cell.update(value)) {
                            isUsed=true;
                            cell.addSourceNode(node);
                        }
                        if (summaryCell.update(value)) {
                            isUsed=true;
                            summaryCell.addSourceNode(node);
                        }
                        checkThreshold(group, summaryRow, row, column, cell, summaryCell);
                    }
                    if (isUsed){
                        
                        noUsedNodes++;
                    }
                }

                currentStartTime = nextStartTime;
                nextStartTime = getNextStartDate(period, endTime, currentStartTime);
                System.out.println("total=" + count + "\tCalc for period=" + (System.currentTimeMillis() - startForPeriod)
                        + "\tper node" + (total / (nodes.size() != 0 ? nodes.size() : 1)));
                monitor.worked(1);
//                monitor.worked(nodesCount);
            } while (currentStartTime < endTime);
            datasetStatistics.setUsedNodes(noUsedNodes);
            datasetStatistics.setTotalNodes(count);
            updateFlags(statistics);
            return statistics;
        }
    }

    /**
     * Checks if alert should be generated or not
     * @param group
     * @param summaryRow
     * @param row
     * @param column
     * @param cell
     * @param summaryCell
     */
    private void checkThreshold(StatisticsGroup group, StatisticsRow summaryRow, StatisticsRow row, TemplateColumn column,
            StatisticsCell cell, StatisticsCell summaryCell) {
        Threshold threshold = column.getThreshold();
        if (threshold != null) {
            Number thresholdValue = threshold.getThresholdValue();
            Condition condition = threshold.getCondition();
            switch (condition) {
            case LT:
                cell.setFlagged(cell.getValue()!=null && thresholdValue.doubleValue() >= cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue()!=null && thresholdValue.doubleValue() >= summaryCell.getValue().doubleValue());
                break;
            case LE:
                cell.setFlagged(cell.getValue()!=null && thresholdValue.doubleValue() > cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue()!=null && thresholdValue.doubleValue() > summaryCell.getValue().doubleValue());
                break;
            case GT:
                cell.setFlagged(cell.getValue()!=null && thresholdValue.doubleValue() <= cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue()!=null && thresholdValue.doubleValue() <= summaryCell.getValue().doubleValue());
                break;
            case GE:
                cell.setFlagged(cell.getValue()!=null && thresholdValue.doubleValue() < cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue()!=null && thresholdValue.doubleValue() < summaryCell.getValue().doubleValue());
                break;
            default:
            }
        }
    }

    private void updateFlags(Statistics statistics) {
        Collection<StatisticsGroup> groups = statistics.getGroups().values();
        for (StatisticsGroup group : groups) {
            Collection<StatisticsRow> rows = group.getRows().values();
            for (StatisticsRow row : rows) {
                Collection<StatisticsCell> cells = row.getCells().values();
                for (StatisticsCell cell : cells) {
                    if (cell.isFlagged()) {
                        row.setFlagged(true);
                    }
                }
                if (row.isFlagged()) {
                    group.setFlagged(true);
                }
            }
        }

    }

    /**
     * Creates a script that includes all KPIs from template
     * 
     * @param template the template
     * @return script as string
     */
    private String createScriptForTemplate(Template template) {
        final String hash_pattern = "\"%s\"=>%s(self),\n";
        StringBuffer sb = new StringBuffer("{");
        for (TemplateColumn column : template.getColumns()) {
            KpiBasedHeader header = (KpiBasedHeader)column.getHeader();
            sb.append(String.format(hash_pattern, header.getName(), header.getKpiName()));
        }
        sb.append("}\n");
        String hash = sb.toString();
        return hash;
    }

    /**
     * Builds statistics based on underlying period statistics
     * 
     * @param template the template
     * @param startTime start time
     * @param endTime end time
     * @param period period
     * @param networkLevel network level
     * @param networkDimension network dimension
     * @param timeDimension time dimension
     * @param uStatistics underlying statistics
     * @return statistics built
     */
    private Statistics buildHighLevelPeriodStatistics(Template template, long startTime, long endTime, CallTimePeriods period,
            String networkLevel, Dimension networkDimension, Dimension timeDimension, Statistics uStatistics) {
        Level tLevel = findOrCreateLevel(period.getId(), timeDimension);
        Statistics statistics = StatisticsEntityFactory.createStatistics(neo, networkDimension.getLevelByKey(networkLevel), tLevel, datasetStatistics);
        Map<String, StatisticsRow> summaries = new HashMap<String, StatisticsRow>();

        for (Entry<String, StatisticsGroup> groupWithKey : uStatistics.getGroups().entrySet()) {
            final String keyProperty = groupWithKey.getKey();
            StatisticsGroup uGroup = groupWithKey.getValue();
            StatisticsGroup group = findOrCreateGroup(statistics, keyProperty);

            long currentStartTime = period.getFirstTime(startTime);
            long nextStartTime = getNextStartDate(period, endTime, currentStartTime);

            if (startTime > currentStartTime) {
                currentStartTime = startTime;
            }

            int comm = 0;
            do {
                System.out.println("Period " + currentStartTime + " - " + nextStartTime);
                if (comm > 500) {
                    commit(true);
                    comm = 0;
                }
                StatisticsRow summaryRow = findOrCreateSummaryRow(group, summaries);
                for (Entry<String, StatisticsRow> rowWithKey : uGroup.getRows().entrySet()) {
                    final StatisticsRow uRow = rowWithKey.getValue();
                    if (!uRow.isSummaryNode()) {
                        Long uPeriod = uRow.getPeriod();
                        if (uPeriod >= currentStartTime && uPeriod < nextStartTime) {
                            StatisticsRow row = findOrCreateRow(group, currentStartTime, period);
                            row.addSourceRow(uRow);
                            List<TemplateColumn> columns = template.getColumns();
                            for (TemplateColumn column : columns) {
                                comm++;
                                StatisticsCell uCell = uRow.getCellByKey(column.getName());

                                if (uCell != null) {
                                    StatisticsCell cell = findOrCreateCell(row, column);
                                    StatisticsCell summaryCell = findOrCreateCell(summaryRow, column);

                                    Number value = uCell.getValue();
                                    cell.update(value);
                                    cell.addSourceNode(uCell.getNode());

                                    summaryCell.update(value);
                                    summaryCell.addSourceNode(uCell.getNode());
                                    checkThreshold(group, summaryRow, row, column, cell, summaryCell);
                                }
                                
                            }
                        } else {
                            continue;
                        }
                    }
                }
                currentStartTime = nextStartTime;
                nextStartTime = getNextStartDate(period, endTime, currentStartTime);
            } while (currentStartTime < endTime);
        }

        return statistics;
    }

    private StatisticsGroup findOrCreateGroup(Statistics statistics, Node keyNode) {
        StatisticsGroup group = statistics.getGroupByKey(keyNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
        if (group == null) {
            group = StatisticsEntityFactory.createStatisticsGroup(neo);
            group.setKeyNode(keyNode);
            statistics.addGroup(group);
        }
        return group;
    }

    private StatisticsGroup findOrCreateGroup(Statistics statistics, String keyProperty) {
        StatisticsGroup group = statistics.getGroupByKey(keyProperty);
        if (group == null) {
            group = StatisticsEntityFactory.createStatisticsGroup(neo);
            group.setGroupName(keyProperty);
            statistics.addGroup(group);
        }
        return group;
    }

    private StatisticsRow findOrCreateRow(StatisticsGroup group, long startDate, CallTimePeriods period) {
        String periodName = NeoUtils.getFormatDateStringForSrow(startDate, period.addPeriod(startDate), "HH:mm", period.getId());
        StatisticsRow row = group.getRowByKey(periodName);
        if (row == null) {
            row = StatisticsEntityFactory.createStatisticsRow(neo, group,startDate, period);
            group.addRow(row);
        }
        return row;
    }

    private StatisticsRow findRow(StatisticsGroup group, long startDate, CallTimePeriods period) {
        String periodName = NeoUtils.getFormatDateStringForSrow(startDate, period.addPeriod(startDate), "HH:mm", period.getId());
        return group.getRowByKey(periodName);
    }

    private Level findOrCreateLevel(String levelKey, Dimension dimension) {
        Level level = dimension.getLevelByKey(levelKey);
        if (level == null) {
            level = StatisticsEntityFactory.createStatisticsLevel(neo, levelKey, datasetStatistics);
            dimension.addLevel(level);
        }
        return level;
    }

    private StatisticsRow findOrCreateSummaryRow(StatisticsGroup group, Map<String, StatisticsRow> summaries) {
        String groupName = group.getGroupName();
        StatisticsRow summaryRow = summaries.get(groupName);
        if (summaryRow == null) {
            summaryRow = StatisticsEntityFactory.createSummaryRow(neo,group);
            summaries.put(groupName, summaryRow);
            group.addRow(summaryRow);
        }
        return summaryRow;
    }

    private StatisticsCell findOrCreateCell(StatisticsRow row, TemplateColumn column) {
        StatisticsCell cell = row.getCellByKey(column.getName());
        if (cell == null) {
            cell = StatisticsEntityFactory.createStatisticsCell(neo, row,column);
            row.addCell(cell);
        }
        return cell;
    }

    // copied from CallStatisticsUtills
    public static long getNextStartDate(CallTimePeriods period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(CallTimePeriods.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
}
