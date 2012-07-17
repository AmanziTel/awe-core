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

package org.amanzi.awe.statistics.manager;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.scripting.JRubyRuntimeWrapper;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.statistics.StatisticsPlugin;
import org.amanzi.awe.statistics.engine.KpiBasedHeader;
import org.amanzi.awe.statistics.entities.impl.AbstractFlaggedEntity;
import org.amanzi.awe.statistics.entities.impl.AggregatedStatistics;
import org.amanzi.awe.statistics.entities.impl.Dimension;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsLevel;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.exceptions.UnableToModifyException;
import org.amanzi.awe.statistics.factory.EntityFactory;
import org.amanzi.awe.statistics.model.StatisticsModel;
import org.amanzi.awe.statistics.template.Condition;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.awe.statistics.template.Threshold;
import org.amanzi.awe.statistics.utils.StatisticsUtils;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * <p>
 * execute statistics building. store common statistics information
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsManager {

    /*
     * logger
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsManager.class);

    /*
     * constants
     */
    private static final String UNKNOW_NAME = "unknown";
    private static final String DATASET_NAME = "dataset";
    private static final String EVALUATE = "Neo4j::load_node(%s).instance_eval {%s}";
    private static final String DECIMAL_FORMAT = "0.#";
    /*
     * statistics manager singleton instance
     */
    private static StatisticsManager statisticsManager;
    /*
     * entity factory
     */
    private EntityFactory factory = EntityFactory.getInstance();
    /*
     * used models
     */
    private StatisticsModel currentStatisticsModel;
    private IDriveModel aggregatedModel;
    private Map<String, File> availableTemplates;
    private StatisticsUtils utils = StatisticsUtils.getInstance();
    private JRubyRuntimeWrapper runtime;

    /*
     * can't be created directly Just through getInstance.
     */
    private StatisticsManager() {
    }

    /**
     * get instance of {@link StatisticsManager}
     * 
     * @return
     */
    public static StatisticsManager getInstance() {
        if (statisticsManager == null) {
            statisticsManager = new StatisticsManager();
        }
        return statisticsManager;
    }

    /**
     * build statistics.
     * 
     * @param template statistics template
     * @param parentModel model which implements {@link IDriveModel} interface
     * @param propertyName property which should be aggregated
     * @param period period for aggregation
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     * @throws UnableToModifyException
     * @throws DuplicateNodeNameException
     * @throws ScriptingException
     */
    public AggregatedStatistics processStatistics(String templateName, IDriveModel parentModel, String propertyName, Period period,
            IProgressMonitor monitor) throws DatabaseException, IllegalNodeDataException, UnableToModifyException,
            DuplicateNodeNameException, ScriptingException {
        LOGGER.info("Process statistics calculation");
        getAllScripts();
        this.runtime = StatisticsPlugin.getDefault().getRuntimeWrapper();
        Template template = (Template)runtime.executeScript(availableTemplates.get(templateName));
        try {
            currentStatisticsModel = new StatisticsModel(parentModel.getRootNode(), "template");
        } catch (DatabaseException e) {
            LOGGER.error("Can't instantiate statistics model ", e);
        }
        aggregatedModel = parentModel;
        Dimension timeDimension = currentStatisticsModel.getDimension(DimensionTypes.TIME);
        Dimension networkDimension = currentStatisticsModel.getDimension(DimensionTypes.NETWORK);
        StatisticsLevel networkLevel = networkDimension.getLevel(propertyName);
        StatisticsLevel timeLevel = timeDimension.getLevel(period.getId());
        AggregatedStatistics statistics = buildStatistics(timeLevel, networkLevel, template, monitor);
        return statistics;
    }

    /**
     * build statistics.
     * 
     * @param timeLevel
     * @param networkLevel
     * @param template
     * @param monitor
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     * @throws UnableToModifyException
     * @throws DuplicateNodeNameException
     * @throws ScriptingException
     */
    private AggregatedStatistics buildStatistics(StatisticsLevel timeLevel, StatisticsLevel networkLevel, Template template,
            IProgressMonitor monitor) throws DatabaseException, IllegalNodeDataException, UnableToModifyException,
            DuplicateNodeNameException, ScriptingException {
        StatisticsLevel sourceTimeLevel = timeLevel.getSourceLevel();
        if (sourceTimeLevel != null) {
            AggregatedStatistics uStatistics = networkLevel.findAggregatedStatistics(sourceTimeLevel);
            if (uStatistics == null) {
                uStatistics = buildStatistics(sourceTimeLevel, networkLevel, template, monitor);
            }
            final String task = "Building stats for " + timeLevel.getName() + "/" + networkLevel;
            LOGGER.debug(task);
            monitor.subTask(task);
            AggregatedStatistics statistics = buildHighLevelPeriodStatistics(template, sourceTimeLevel, networkLevel, uStatistics,
                    monitor);
            updateFlags(statistics);
            return statistics;

        } else {
            try {
                return buildLowestLevel(timeLevel, networkLevel, template, monitor);
            } catch (IllegalNodeDataException e) {
                LOGGER.error("Exception during low level statistics calculation", e);
            }
        }
        return null;
    }

    /**
     * Recursively create statistics for each underline {@link Period}.
     * 
     * @param template
     * @param timeLevel
     * @param networkLevel
     * @param uStatistics
     * @param monitor
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     * @throws UnableToModifyException
     */
    private AggregatedStatistics buildHighLevelPeriodStatistics(Template template, StatisticsLevel timeLevel,
            StatisticsLevel networkLevel, AggregatedStatistics uStatistics, IProgressMonitor monitor) throws DatabaseException,
            IllegalNodeDataException, UnableToModifyException {
        AggregatedStatistics statistics = networkLevel.getAggregateStatistics(timeLevel);
        Map<String, StatisticsRow> summaries = new HashMap<String, StatisticsRow>();
        Period period = Period.findById(timeLevel.getName());
        for (StatisticsGroup lowerGroup : uStatistics.getAllChild()) {
            final String keyProperty = lowerGroup.getName();
            StatisticsGroup group = findOrCreateGroup(statistics, keyProperty, null);

            long currentStartTime = period.getStartTime(currentStatisticsModel.getMinTimestamp());
            long nextStartTime = getNextStartDate(period, currentStatisticsModel.getMaxTimestamp(), currentStartTime);

            if (currentStatisticsModel.getMinTimestamp() < currentStartTime) {
                currentStartTime = currentStatisticsModel.getMinTimestamp();
            }
            do {
                final String debugInfo = "Period " + currentStartTime + " - " + nextStartTime;
                System.out.println(debugInfo);
                LOGGER.debug(debugInfo);
                StatisticsRow summaryRow = findOrCreateSummaryRow(group, summaries);
                for (StatisticsRow uRow : group.getAllChild()) {
                    if (!uRow.isSummaryNode()) {
                        Long uPeriod = uRow.getTimestamp();
                        if (uPeriod >= currentStartTime && uPeriod < nextStartTime) {
                            StatisticsRow newRow = findOrCreateRow(group, currentStartTime, period);
                            newRow.addSourceRow(uRow);
                            List<TemplateColumn> columns = template.getColumns();
                            for (TemplateColumn column : columns) {
                                StatisticsCell uCell = uRow.findChildByName(column.getName());

                                if (uCell != null) {
                                    StatisticsCell cell = findOrCreateCell(uRow, column);
                                    StatisticsCell summaryCell = findOrCreateCell(summaryRow, column);

                                    Number value = uCell.getValue();
                                    cell.updateValue(value);
                                    cell.addSourceCell(uCell);

                                    summaryCell.updateValue(value);
                                    summaryCell.addSourceCell(uCell);
                                    double[] uBbox = uCell.getBbox();
                                    if (uBbox != null) {
                                        ReferencedEnvelope ure = new ReferencedEnvelope(uBbox[0], uBbox[1], uBbox[2], uBbox[3],
                                                null);
                                        cell.updateBbox(ure);
                                        uRow.updateBbox(ure);
                                        summaryCell.updateBbox(ure);
                                        summaryRow.updateBbox(ure);
                                        group.updateBbox(ure);
                                    }
                                    checkThreshold(group, summaryRow, uRow, column, cell, summaryCell);
                                }
                            }

                        }
                    } else {
                        System.out.println("SKIP: " + uRow.getName() + "uPeriod=" + timeLevel.getName() + "\tcurrentStartTime="
                                + currentStartTime + "\tnextStartTime=" + nextStartTime);
                        continue;
                    }
                }

                currentStartTime = nextStartTime;
                nextStartTime = getNextStartDate(period, currentStatisticsModel.getMaxTimestamp(), currentStartTime);
            } while (currentStartTime < currentStatisticsModel.getMaxTimestamp());
        }
        monitor.worked(1);
        return statistics;
    }

    /**
     * build lowest level statistics. Lowest level it is a level which hasn't underline
     * {@link Period}
     * 
     * @param timeLevel
     * @param networkLevel
     * @param template
     * @param monitor
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     * @throws ScriptingException
     * @throws UnableToModifyException
     */
    @SuppressWarnings("unchecked")
    private AggregatedStatistics buildLowestLevel(StatisticsLevel timeLevel, StatisticsLevel networkLevel, Template template,
            IProgressMonitor monitor) throws DatabaseException, IllegalNodeDataException, DuplicateNodeNameException,
            ScriptingException, UnableToModifyException {
        final String task = "Building stats for " + timeLevel.getName() + "/" + networkLevel;
        LOGGER.debug(task);
        monitor.subTask(task);
        AggregatedStatistics statistics = networkLevel.createAggregatedStatistics(timeLevel);
        Map<String, StatisticsRow> summaries = new HashMap<String, StatisticsRow>();

        String hash = createScriptForTemplate(template);
        long noUsedNodes = 0;
        Period period = Period.findById(timeLevel.getName());
        long currentStartTime = period.getStartTime(currentStatisticsModel.getMinTimestamp());
        long nextStartTime = getNextStartDate(period, currentStatisticsModel.getMaxTimestamp(), currentStartTime);
        long count = 0;
        do {
            long startForPeriod = System.currentTimeMillis();
            String debugInfo = "currentStartTime=" + currentStartTime + "\tnextStartTime=" + nextStartTime + "\tendTime="
                    + currentStatisticsModel.getMaxTimestamp();
            LOGGER.debug(debugInfo);
            if (monitor.isCanceled()) {
                break;
            }

            Iterable<IDataElement> elements = aggregatedModel.findAllElementsByTimestampPeriod(currentStartTime, nextStartTime);
            long cellCalcTime = 0L;
            long startFindGroup = 0L;
            for (IDataElement element : elements) {
                count++;
                boolean isUsed = false;
                IDataElement locationNode = aggregatedModel.getLocation(element);

                String script = String.format(EVALUATE, element.get("id"), hash);
                HashMap<Object, Object> result;
                result = (HashMap<Object, Object>)runtime.executeScript(script);

                // Node networkNode = findNetworkNode(node, networkLevel);
                // TODO use key property instead of key node name for
                // non-correlated datasets
                // String keyProperty = dsService.getKeyProperty(node);
                StatisticsGroup group;
                startFindGroup = System.currentTimeMillis();
                // if (networkNode != null) {
                // group = findOrCreateGroup(statistics, networkNode);
                // } else {
                String defaultValue = networkLevel.equals(DATASET_NAME) ? aggregatedModel.getName() : UNKNOW_NAME;
                group = findOrCreateGroup(statistics, networkLevel.getName(), defaultValue);
                // }
                startFindGroup = System.currentTimeMillis() - startFindGroup;
                // add summary row first
                StatisticsRow summaryRow = findOrCreateSummaryRow(group, summaries);
                StatisticsRow row = findOrCreateRow(group, currentStartTime, period);
                long startCalcTime = System.currentTimeMillis();
                for (Object key : result.keySet()) {
                    TemplateColumn column = template.getColumnByName(key.toString());

                    StatisticsCell cell = findOrCreateCell(row, column);
                    StatisticsCell summaryCell = findOrCreateCell(summaryRow, column);
                    Object object = result.get(key);
                    Number value = null;
                    if (object instanceof Number) {
                        value = (Number)object;
                    } else if (object instanceof String) {
                        try {
                            value = new DecimalFormat(DECIMAL_FORMAT).parse((String)object);
                        } catch (ParseException e) {
                            LOGGER.error("can't parse value " + value);
                        }
                    }
                    if (cell.updateValue(value)) {

                        isUsed = true;
                        cell.addSingleSource(element);
                        if (locationNode != null) {
                            updateBBox(locationNode, cell);
                            updateBBox(locationNode, row);
                            updateBBox(locationNode, group);
                        }
                    }
                    if (summaryCell.updateValue(value)) {
                        isUsed = true;
                        summaryCell.addSingleSource(element);
                        if (locationNode != null) {
                            updateBBox(locationNode, summaryCell);
                            updateBBox(locationNode, summaryRow);
                        }
                    }
                    checkThreshold(group, summaryRow, row, column, cell, summaryCell);
                }
                cellCalcTime += (System.currentTimeMillis() - startCalcTime);
                if (isUsed) {
                    noUsedNodes++;
                }
            }

            currentStartTime = nextStartTime;
            nextStartTime = getNextStartDate(period, currentStatisticsModel.getMaxTimestamp(), currentStartTime);
            debugInfo = "Total no. of nodes processed: " + count + "\tCalc time for period="
                    + (System.currentTimeMillis() - startForPeriod) + "\tTime to update cells: " + cellCalcTime
                    + "\tTime to find a group:" + startFindGroup;
            LOGGER.debug(debugInfo);
        } while (currentStartTime < currentStatisticsModel.getMaxTimestamp());
        currentStatisticsModel.setUsedNodes(noUsedNodes);
        currentStatisticsModel.setTotalNodes(count);
        updateFlags(statistics);
        monitor.worked(1);
        return statistics;
    }

    /**
     * Checks if alert should be generated or not
     * 
     * @param group
     * @param summaryRow
     * @param row
     * @param column
     * @param cell
     * @param summaryCell
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private void checkThreshold(StatisticsGroup group, StatisticsRow summaryRow, StatisticsRow row, TemplateColumn column,
            StatisticsCell cell, StatisticsCell summaryCell) throws IllegalNodeDataException, DatabaseException {
        Threshold threshold = column.getThreshold();
        if (threshold != null) {
            Number thresholdValue = threshold.getThresholdValue();
            Condition condition = threshold.getCondition();
            switch (condition) {
            case LT:
                cell.setFlagged(cell.getValue() != null && thresholdValue.doubleValue() >= cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue() != null
                        && thresholdValue.doubleValue() >= summaryCell.getValue().doubleValue());
                break;
            case LE:
                cell.setFlagged(cell.getValue() != null && thresholdValue.doubleValue() > cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue() != null
                        && thresholdValue.doubleValue() > summaryCell.getValue().doubleValue());
                break;
            case GT:
                cell.setFlagged(cell.getValue() != null && thresholdValue.doubleValue() <= cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue() != null
                        && thresholdValue.doubleValue() <= summaryCell.getValue().doubleValue());
                break;
            case GE:
                cell.setFlagged(cell.getValue() != null && thresholdValue.doubleValue() < cell.getValue().doubleValue());
                summaryCell.setFlagged(summaryCell.getValue() != null
                        && thresholdValue.doubleValue() < summaryCell.getValue().doubleValue());
                break;
            default:
            }
        }
    }

    /**
     * update rows flaggs
     * 
     * @param statistics
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private void updateFlags(AggregatedStatistics statistics) throws IllegalNodeDataException, DatabaseException {
        Iterable<StatisticsGroup> groups = statistics.getAllChild();
        for (StatisticsGroup group : groups) {
            Iterable<StatisticsRow> rows = group.getAllChild();
            for (StatisticsRow row : rows) {
                Iterable<StatisticsCell> cells = row.getAllChild();
                for (StatisticsCell cell : cells) {
                    if (cell.isFlagged()) {
                        row.setFlagged(Boolean.TRUE);
                    }
                }
                if (row.isFlagged()) {
                    group.setFlagged(Boolean.TRUE);
                }
            }
        }

    }

    /**
     * create sRow
     * 
     * @param group
     * @param startDate
     * @param period
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private StatisticsRow findOrCreateRow(StatisticsGroup group, long startDate, Period period) throws DatabaseException,
            IllegalNodeDataException {
        String name = utils.getFormatDateStringForSrow(startDate, period.addPeriod(startDate), period);
        StatisticsRow row = group.findChildByName(name);
        if (row == null) {
            try {
                row = group.addRow(startDate, name);
            } catch (DuplicateNodeNameException e) {
                LOGGER.error("unexpected Exception thrown. can't create summury node");
                return null;
            }
        }
        return row;
    }

    /**
     * create summury row
     * 
     * @param group
     * @param summaries
     * @param period
     * @return
     */
    private StatisticsRow findOrCreateSummaryRow(StatisticsGroup group, Map<String, StatisticsRow> summaries) {
        String groupName = group.getName();
        StatisticsRow summaryRow = summaries.get(groupName);
        if (summaryRow == null) {
            try {
                summaryRow = group.addSummuryRow();
            } catch (Exception e) {
                LOGGER.error("unexpected Exception thrown. can't create summury node");
                return null;
            }
            summaries.put(groupName, summaryRow);
        }
        return summaryRow;
    }

    /**
     * find cell or create new one
     * 
     * @param row
     * @param name
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private StatisticsCell findOrCreateCell(StatisticsRow row, TemplateColumn column) throws DatabaseException,
            IllegalNodeDataException {
        StatisticsCell cell = row.findChildByName(column.getName());
        if (cell == null) {
            try {
                factory.createScell(row, column);
            } catch (DuplicateNodeNameException e) {
                LOGGER.error("Unexpected exteption thrown", e);
            }
        }
        return cell;
    }

    /**
     * find or create group in statistics
     * 
     * @param statistics
     * @param object
     * @param defaultValue
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    private StatisticsGroup findOrCreateGroup(AggregatedStatistics statistics, Object object, String defaultValue)
            throws DatabaseException, IllegalNodeDataException {
        String name = (String)object;
        if (name == null) {
            name = defaultValue;
        }
        StatisticsGroup group = statistics.findChildByName(name);
        if (group == null) {
            try {
                group = statistics.addGroup(name);
            } catch (DuplicateNodeNameException e) {
                // can't be thrown
                LOGGER.error("Unexpected exception. Node is already exists", e);
                return null;
            }
        }

        return group;
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
     * return next required time cut in according to period
     * 
     * @param period
     * @param endDate
     * @param currentStartDate
     * @return
     */
    public long getNextStartDate(Period period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }

    /**
     * Updates BBox from location node
     * 
     * @param locationNode node with coordinates
     * @param nodeToUpdate node which BBox is updated
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private void updateBBox(IDataElement locationNode, AbstractFlaggedEntity nodeToUpdate) throws IllegalNodeDataException,
            DatabaseException {
        Double lat = (Double)locationNode.get(DriveModel.LATITUDE);
        Double lon = (Double)locationNode.get(DriveModel.LONGITUDE);
        if (lat != null && lon != null) {
            ReferencedEnvelope re = new ReferencedEnvelope(lon, lon, lat, lat, null);
            nodeToUpdate.updateBbox(re);
        }
    }

    /**
     * get available file Scripts
     * 
     * @return
     */
    public Collection<String> getAllScripts() {
        if (availableTemplates != null) {
            return availableTemplates.keySet();
        }
        availableTemplates = StatisticsPlugin.getDefault().getAllScripts();
        return availableTemplates.keySet();
    }
}
