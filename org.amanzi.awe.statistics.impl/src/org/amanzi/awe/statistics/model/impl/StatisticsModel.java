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

package org.amanzi.awe.statistics.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsLevel;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.dto.impl.StatisticsCell;
import org.amanzi.awe.statistics.dto.impl.StatisticsGroup;
import org.amanzi.awe.statistics.dto.impl.StatisticsLevel;
import org.amanzi.awe.statistics.dto.impl.StatisticsRow;
import org.amanzi.awe.statistics.filter.IStatisticsFilter;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;
import org.amanzi.awe.statistics.service.IStatisticsService;
import org.amanzi.awe.statistics.service.impl.StatisticsService;
import org.amanzi.awe.statistics.service.impl.StatisticsService.StatisticsRelationshipType;
import org.amanzi.neo.dateformat.DateFormatManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.dto.SourcedElement.ICollectFunction;
import org.amanzi.neo.impl.util.AbstractDataElementIterator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractAnalyzisModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsModel extends AbstractAnalyzisModel<IMeasurementModel> implements IStatisticsModel {

    private final static Logger LOGGER = Logger.getLogger(StatisticsModel.class);

    protected class StatisticsCellFilteredConverter extends DataElementConverter<IStatisticsCell> {

        private final IStatisticsFilter filter;

        /**
         * @param sourceCollection
         */
        public StatisticsCellFilteredConverter(final Iterator<IStatisticsCell> sourceCollection, final IStatisticsFilter filter) {
            super(sourceCollection);
            this.filter = filter;
        }

        @Override
        protected IDataElement getNext(final IStatisticsCell element) {
            return filter == null ? element : ((filter.getCellName().equals(element.getName())) ? element : null);
        }

    }

    private abstract class AbstractStatisticsFilteredIterator<T extends IDataElement> extends AbstractDataElementIterator<T> {

        private final IStatisticsFilter filter;

        protected AbstractStatisticsFilteredIterator(final Iterator<Node> nodeIterator, final IStatisticsFilter filter) {
            super(nodeIterator);
            this.filter = filter;
        }

        @Override
        protected T createDataElement(final Node node) {
            final T result = createDataElementInternal(node);

            if (apply(result, filter)) {
                return result;
            }

            return null;
        }

        protected abstract T createDataElementInternal(Node node);

        protected abstract boolean apply(T element, IStatisticsFilter filter);
    }

    private class StatisticsLevelIterator extends AbstractStatisticsFilteredIterator<IStatisticsLevel> {

        private final DimensionType dimension;

        /**
         * @param nodeIterator
         */
        protected StatisticsLevelIterator(final Iterator<Node> nodeIterator, final IStatisticsFilter filter,
                final DimensionType dimension) {
            super(nodeIterator, filter);

            this.dimension = dimension;
        }

        @Override
        protected IStatisticsLevel createDataElementInternal(final Node node) {
            return createStatisticsLevel(node, dimension);
        }

        @Override
        protected boolean apply(final IStatisticsLevel element, final IStatisticsFilter filter) {
            return filter == null || element.getName().equals(filter.getPeriod());
        }
    }

    private class StatisticsRowIterator extends AbstractDataElementIterator<IStatisticsRow> {

        /**
         * @param nodeIterator
         */
        protected StatisticsRowIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected IStatisticsRow createDataElement(final Node node) {
            return createStatisticsRow(node);
        }

    }

    private class StatisticsCellIterator extends AbstractDataElementIterator<IStatisticsCell> {

        /**
         * @param nodeIterator
         */
        protected StatisticsCellIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected IStatisticsCell createDataElement(final Node node) {
            return createStatisticsCell(node);
        }

    }

    private class StatisticsGroupIterator extends AbstractStatisticsFilteredIterator<IStatisticsGroup> {

        /**
         * @param nodeIterator
         */
        protected StatisticsGroupIterator(final Iterator<Node> nodeIterator, final IStatisticsFilter filter) {
            super(nodeIterator, filter);
        }

        @Override
        protected IStatisticsGroup createDataElement(final Node node) {
            return createStatisticsGroupFromNode(node);
        }

        @Override
        protected IStatisticsGroup createDataElementInternal(final Node node) {
            return createStatisticsGroupFromNode(node);
        }

        @Override
        protected boolean apply(final IStatisticsGroup element, final IStatisticsFilter filter) {
            return filter == null || filter.getGroupNames().contains(element.getPropertyValue());
        }
    }

    @SuppressWarnings("unchecked")
    private final ICollectFunction statisticsGroupSourcesCollectFunction = new ICollectFunction() {

        @Override
        public Iterable<IDataElement> collectSourceElements(final IDataElement element) {
            if (element instanceof IStatisticsGroup) {
                try {
                    final IteratorChain chain = new IteratorChain(getIteratorList((IStatisticsGroup)element));
                    return new Iterable<IDataElement>() {

                        @Override
                        public Iterator<IDataElement> iterator() {
                            return chain;
                        }
                    };
                } catch (final ModelException e) {
                    LOGGER.error("Error on collecting Sources of Statistics Group", e);
                }
            }

            return Iterables.emptyIterable();
        }

        private List<Iterator<IDataElement>> getIteratorList(final IStatisticsGroup group) throws ModelException {
            final List<Iterator<IDataElement>> result = new ArrayList<Iterator<IDataElement>>();

            final Iterable<IStatisticsRow> rows = getStatisticsRows(group.getPeriod());
            if (rows != null) {
                for (final IStatisticsRow sourceRow : rows) {
                    if (sourceRow.getStatisticsGroup().equals(group)) {
                        result.add(statisticsRowSourcesCollectFunction.collectSourceElements(sourceRow).iterator());
                    }
                }
            }

            return result;
        }

    };

    @SuppressWarnings("unchecked")
    private final ICollectFunction statisticsRowSourcesCollectFunction = new ICollectFunction() {

        @Override
        public Iterable<IDataElement> collectSourceElements(final IDataElement element) {
            if (element instanceof IStatisticsRow) {
                try {
                    final IteratorChain chain = new IteratorChain(getIteratorList((IStatisticsRow)element));
                    return new Iterable<IDataElement>() {

                        @Override
                        public Iterator<IDataElement> iterator() {
                            return chain;
                        }
                    };
                } catch (final ModelException e) {
                    LOGGER.error("Error on collecting Sources of Statistics Row", e);
                }
            }

            return Iterables.emptyIterable();
        }

        private List<Iterator<IDataElement>> getIteratorList(final IStatisticsRow row) throws ModelException {
            final List<Iterator<IDataElement>> result = new ArrayList<Iterator<IDataElement>>();

            final Iterable<IStatisticsRow> rows = getSourceRows(row);
            if (rows != null) {
                for (final IStatisticsRow sourceRow : rows) {
                    result.addAll(getIteratorList(sourceRow));
                }
            }

            for (final IStatisticsCell cell : row.getStatisticsCells()) {
                result.add(statisticsCellSourcesCollectFunction.collectSourceElements(cell).iterator());
            }

            return result;
        }

    };

    private final ICollectFunction statisticsCellSourcesCollectFunction = new ICollectFunction() {

        @Override
        public Iterable<IDataElement> collectSourceElements(final IDataElement element) {
            if (element instanceof IStatisticsCell) {
                try {
                    return getIterable((IStatisticsCell)element);
                } catch (final ModelException e) {
                    LOGGER.error("Error on collecting Sources of Statistics Cell", e);
                }
            }

            return Iterables.emptyIterable();
        }

        private Iterable<IDataElement> getIterable(final IStatisticsCell cell) throws ModelException {
            Iterable<IDataElement> result = Iterables.emptyIterable();

            final Iterable<IStatisticsCell> cells = getSourceCells(cell);
            if (cells != null) {
                for (final IStatisticsCell sourceCell : cells) {
                    result = Iterables.concat(result, getIterable(sourceCell));
                }
            }

            result = Iterables.concat(result, getSources(cell));

            return result;
        }

    };

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private final IStatisticsNodeProperties statisticsNodeProperties;

    private String templateName;

    private String aggregatedProperty;

    private final IStatisticsService statisticsService;

    private final Map<Pair<String, String>, IStatisticsGroup> statisticsGroupCache = new HashMap<Pair<String, String>, IStatisticsGroup>();

    private final Map<DimensionType, Map<String, Node>> statisticsLevelCache = new HashMap<DimensionType, Map<String, Node>>();

    private final Map<Pair<String, Long>, IStatisticsRow> statisticsRowCache = new HashMap<Pair<String, Long>, IStatisticsRow>();

    private final Map<Pair<StatisticsRow, String>, Node> statisticsCellNodeCache = new HashMap<Pair<StatisticsRow, String>, Node>();
    private final Map<StatisticsGroup, StatisticsRow> summuryCache = new HashMap<StatisticsGroup, StatisticsRow>();

    private Set<String> columnNames = new LinkedHashSet<String>();

    private final IMeasurementNodeProperties measurementNodeProperties;

    /**
     * @param nodeService
     * @param generalNodeProperties
     * @param measurementNodeProperties
     */
    public StatisticsModel(final IStatisticsService statisticsService, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final ITimePeriodNodeProperties timePeriodNodeProperties,
            final IStatisticsNodeProperties statisticsNodeProperties, final IMeasurementNodeProperties measurementNodeProperties) {
        super(nodeService, generalNodeProperties);
        this.measurementNodeProperties = measurementNodeProperties;
        this.statisticsService = statisticsService;
        this.timePeriodNodeProperties = timePeriodNodeProperties;
        this.statisticsNodeProperties = statisticsNodeProperties;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        try {
            super.initialize(rootNode);

            templateName = getNodeService().getNodeProperty(rootNode, statisticsNodeProperties.getTemplateNameProperty(), null,
                    true);
            aggregatedProperty = getNodeService().getNodeProperty(rootNode,
                    statisticsNodeProperties.getAggregationPropertyNameProperty(), null, true);

            columnNames = new LinkedHashSet<String>(Arrays.asList(getNodeService().getNodeProperty(rootNode,
                    statisticsNodeProperties.getColumnNamesProperty(), ArrayUtils.EMPTY_STRING_ARRAY, false)));
        } catch (final Exception e) {
            processException("An error occured on Statistics Model Initialization", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    public void initialize(final Node parentNode, final String templateName, final String propertyName) throws ModelException {
        assert !StringUtils.isEmpty(templateName);
        assert !StringUtils.isEmpty(propertyName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", parentNode, templateName, propertyName));
        }

        try {
            final String statisticsName = MessageFormat.format(StatisticsService.STATISTICS_NAME_PATTERN, templateName,
                    propertyName);

            super.initialize(parentNode, statisticsName, StatisticsNodeType.STATISTICS);

            this.templateName = templateName;
            this.aggregatedProperty = propertyName;
        } catch (final Exception e) {
            processException("Exception on initializing Statistics Model", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    @Override
    public void finishUp() throws ModelException {
        try {
            getNodeService().updateProperty(getRootNode(), statisticsNodeProperties.getTemplateNameProperty(), templateName);
            getNodeService().updateProperty(getRootNode(), statisticsNodeProperties.getAggregationPropertyNameProperty(),
                    aggregatedProperty);

            getNodeService().updateProperty(getRootNode(), statisticsNodeProperties.getColumnNamesProperty(),
                    columnNames.toArray(new String[columnNames.size()]));
        } catch (final ServiceException e) {
            processException("Exception on finishin up Statistics Model", e);
        }
    }

    @Override
    protected RelationshipType getRelationToParent() {
        return StatisticsRelationshipType.STATISTICS;
    }

    @Override
    public IStatisticsGroup getStatisticsGroup(final String period, final String propertyKey) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getGroup", period, propertyKey));
        }

        // TODO: LN: 15.08.2012, validate input

        final Pair<String, String> groupKey = new ImmutablePair<String, String>(period, propertyKey);

        IStatisticsGroup result = statisticsGroupCache.get(groupKey);

        if (result == null) {
            result = getGroupFromDatabase(period, propertyKey);

            statisticsGroupCache.put(groupKey, result);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getGroup"));
        }

        return result;
    }

    protected IStatisticsGroup getGroupFromDatabase(final String period, final String propertyKey) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getGroupFromDatabase", period, propertyKey));
        }

        IStatisticsGroup result = null;

        try {
            final Node propertyLevel = getStatisticsLevelNode(DimensionType.PROPERTY, propertyKey);
            final Node periodLevel = getStatisticsLevelNode(DimensionType.TIME, period);

            final Node groupNode = statisticsService.getGroup(propertyLevel, periodLevel);

            result = createStatisticsGroup(groupNode, period, propertyKey);
        } catch (final ServiceException e) {
            processException("Error on getting StatisticsGroup from Database", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getGroupFromDatabase"));
        }

        return result;
    }

    protected IStatisticsGroup createStatisticsGroupFromNode(final Node node) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createStatisticsGroup", node));
        }

        IStatisticsGroup group = null;

        try {
            final String period = statisticsService.getStatisticsLevelName(node, DimensionType.TIME);
            final String propertyKey = statisticsService.getStatisticsLevelName(node, DimensionType.PROPERTY);

            group = createStatisticsGroup(node, period, propertyKey);
        } catch (final Exception e) {
            LOGGER.error("Error on getting StatisticsGroup instance from Node", e);
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createStatisticsGroup"));
        }

        return group;
    }

    protected IStatisticsGroup createStatisticsGroup(final Node node, final String period, final String propertyKey)
            throws ModelException {
        StatisticsGroup result = null;

        try {
            final String name = getNodeService().getNodeName(node);

            result = new StatisticsGroup(node, statisticsGroupSourcesCollectFunction);
            result.setNodeType(StatisticsNodeType.GROUP);
            result.setName(name);
            result.setPeriod(period);
            result.setPropertyValue(propertyKey);
        } catch (final ServiceException e) {
            processException("Error on converting node to StatisticsGroup", e);
        }

        return result;
    }

    @Override
    public IStatisticsRow getStatisticsRow(final IStatisticsGroup group, final long startDate, final long endDate)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsRow", group, startDate, endDate));
        }

        final IStatisticsRow result = getStatisticsRow(group, null, startDate, endDate);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsRow"));
        }

        return result;
    }

    @Override
    public IStatisticsRow getStatisticsRow(final IStatisticsGroup group, final IStatisticsRow sourceRow, final long startDate,
            final long endDate) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsRow", group, sourceRow, startDate, endDate));
        }

        // TODO: LN: 15.08.2012, validate input
        final Pair<String, Long> key = new ImmutablePair<String, Long>(group.getName(), startDate);

        IStatisticsRow result = statisticsRowCache.get(key);
        if (result == null) {
            result = getStatisticsRowFromDatabase((StatisticsGroup)group, sourceRow, startDate, endDate);
            statisticsRowCache.put(key, result);
        }
        if (sourceRow != null) {
            try {
                statisticsService.addSourceNode(((StatisticsRow)result).getNode(), ((DataElement)sourceRow).getNode());
            } catch (final ServiceException e) {
                processException("Can't add source to row " + sourceRow, e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsRow"));
        }

        return result;
    }

    protected IStatisticsRow getStatisticsRowFromDatabase(final StatisticsGroup statisticsGroup, final IStatisticsRow sourceRow,
            final long startDate, final long endDate) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsRowFromDatabase", statisticsGroup, sourceRow, startDate, endDate));
        }

        final String name = Long.toString(startDate);

        IStatisticsRow result = null;

        try {
            Node sRowNode = getNodeService().getChildInChainByName(statisticsGroup.getNode(), name, StatisticsNodeType.S_ROW);
            if (sRowNode == null) {
                final Map<String, Object> properties = new HashMap<String, Object>();

                addTimeProperty(properties, timePeriodNodeProperties.getStartDateProperty(),
                        timePeriodNodeProperties.getStartDateTimestampProperty(), startDate);
                addTimeProperty(properties, timePeriodNodeProperties.getEndDateProperty(),
                        timePeriodNodeProperties.getEndDateTimestampProperty(), endDate);

                sRowNode = getNodeService()
                        .createNodeInChain(statisticsGroup.getNode(), StatisticsNodeType.S_ROW, name, properties);
                updateSummury((StatisticsRow)getSummuryRow(statisticsGroup), startDate, endDate);
            }
            result = createStatisticsRow(sRowNode, startDate, endDate);
        } catch (final ServiceException e) {
            processException("Exception on getting Statistics Row from Database", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsRowFromDatabase"));
        }

        return result;
    }

    protected StatisticsRow createStatisticsRow(final Node node, final long startDate, final long endDate) {
        final StatisticsRow row = new StatisticsRow(node, statisticsRowSourcesCollectFunction);

        row.setNodeType(StatisticsNodeType.S_ROW);
        row.setStartDate(startDate);
        row.setEndDate(endDate);

        return row;
    }

    protected IStatisticsRow createStatisticsRow(final Node node) {
        StatisticsRow row = null;
        try {
            final long startDate = getNodeService().getNodeProperty(node, timePeriodNodeProperties.getStartDateTimestampProperty(),
                    null, true);
            final long endDate = getNodeService().getNodeProperty(node, timePeriodNodeProperties.getEndDateTimestampProperty(),
                    null, true);

            row = createStatisticsRow(node, startDate, endDate);
            final boolean isSummury = getNodeService().getNodeProperty(node, statisticsNodeProperties.isSummuryProperty(), false,
                    false);
            final Node groupNode = getNodeService().getChainParent(node);
            final IStatisticsGroup group = createStatisticsGroupFromNode(groupNode);
            row.setSummury(isSummury);
            row.setStatisticsGroup(group);
            row.setStatisticsCells(getStatisticsCells(node));
        } catch (final Exception e) {
            LOGGER.error("Error on getting StatisticsRow Node from Database", e);
            return null;
        }

        return row;
    }

    protected IStatisticsCell createStatisticsCell(final Node node) {
        final StatisticsCell cell = new StatisticsCell(node, statisticsCellSourcesCollectFunction);
        try {
            cell.setName(getNodeService().getNodeName(node));
            cell.setNodeType(getNodeService().getNodeType(node));
            cell.setValue((Number)getNodeService().getNodeProperty(node, statisticsNodeProperties.getValueProperty(), null, false));
        } catch (final ServiceException e) {
            LOGGER.error("Error on getting StatisticsRow Node from Database", e);
            return null;
        } catch (final NodeTypeNotExistsException e) {
            LOGGER.error("Error on getting StatisticsRow Node from Database", e);
            return null;
        }

        return cell;
    }

    private void addTimeProperty(final Map<String, Object> properties, final String timeProperty, final String timestampProperty,
            final long time) {
        final Date date = new Date(time);
        final String dateString = DateFormatManager.getInstance().getDefaultFormat().format(date);

        properties.put(timeProperty, dateString);
        properties.put(timestampProperty, time);
    }

    protected Node getStatisticsLevelNode(final DimensionType dimensionType, final String key) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsLevelNode", dimensionType, key));
        }

        Node result = getStatisticsLevelNodeFromCache(dimensionType, key);
        if (result == null) {
            try {
                result = statisticsService.getStatisticsLevel(getRootNode(), dimensionType, key);

                addStatisticsLevelNodeToCache(dimensionType, key, result);
            } catch (final ServiceException e) {
                processException("Exception on searching for Statistics Level", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsLevelNode"));
        }

        return result;
    }

    protected Node getStatisticsLevelNodeFromCache(final DimensionType dimensionType, final String key) {
        Map<String, Node> dimensionCache = statisticsLevelCache.get(dimensionType);
        if (dimensionCache == null) {
            dimensionCache = new HashMap<String, Node>();
            statisticsLevelCache.put(dimensionType, dimensionCache);
        }

        return dimensionCache.get(key);
    }

    protected void addStatisticsLevelNodeToCache(final DimensionType dimensionType, final String key, final Node value) {
        Map<String, Node> dimensionCache = statisticsLevelCache.get(dimensionType);
        if (dimensionCache == null) {
            dimensionCache = new HashMap<String, Node>();
            statisticsLevelCache.put(dimensionType, dimensionCache);
        }

        dimensionCache.put(key, value);
    }

    @Override
    public boolean updateStatisticsCell(final IStatisticsRow statisticsRow, final String name, final Object value,
            final IDataElement... sourceElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("updateStatisticsCell", statisticsRow, name, value));
        }

        // TODO: LN: 17.08.2012, validate input
        // TODO: LN: 20.08.2012, value can be null

        boolean isCreated = false;;

        Node statisticsCellNode = findStatisticsCellNode((StatisticsRow)statisticsRow, name);

        if (statisticsCellNode == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No Statistics Cell was found by name <" + name + "> in Row <" + statisticsRow + ">. Create new one.");
            }

            statisticsCellNode = createStatisticsCellNode((StatisticsRow)statisticsRow, name);

            isCreated = true;
        }

        try {
            if (value != null) {
                getNodeService().updateProperty(statisticsCellNode, statisticsNodeProperties.getValueProperty(), value);
                for (final IDataElement singleElement : sourceElement) {
                    final DataElement source = (DataElement)singleElement;
                    statisticsService.addSourceNode(statisticsCellNode, source.getNode());
                }
            }
        } catch (final ServiceException e) {
            processException("Error on updating value of Statistics Cell <" + name + "> in Row <" + statisticsRow + ">", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("updateStatisticsCell"));
        }

        return isCreated;
    }

    protected Node findStatisticsCellNode(final StatisticsRow statisticsRow, final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsCellNode", statisticsRow, name));
        }

        final Pair<StatisticsRow, String> key = new ImmutablePair<StatisticsRow, String>(statisticsRow, name);

        Node result = statisticsCellNodeCache.get(key);

        if (result == null) {
            result = getStatisticsCellNodeFromDatabase(statisticsRow, name);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsCellNode"));
        }

        return result;
    }

    protected Node createStatisticsCellNode(final StatisticsRow statisticsRow, final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createStatisticsCellNode", statisticsRow, name));
        }

        Node result = null;

        try {
            result = getNodeService().createNodeInChain(statisticsRow.getNode(), StatisticsNodeType.S_CELL, name);
        } catch (final ServiceException e) {
            processException("Error on creating Statistics Cell Node by name <" + name + "> in Row <" + statisticsRow + ">.", e);
        }

        columnNames.add(name);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createStatisticsCellNode"));
        }

        return result;
    }

    protected Node getStatisticsCellNodeFromDatabase(final StatisticsRow statisticsRow, final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsCellNodeFromDatabase", statisticsRow, name));
        }

        Node result = null;

        try {
            result = getNodeService().getChildInChainByName(statisticsRow.getNode(), name, StatisticsNodeType.S_CELL);
        } catch (final ServiceException e) {
            processException("Error on searching Statistics Cell Node by name <" + name + "> in Row <" + statisticsRow + ">.", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsCellNodeFromDatabase"));
        }

        return result;
    }

    protected Iterable<IStatisticsCell> getStatisticsCells(final Node statisticsRowNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsCells", statisticsRowNode));
        }

        // TODO: LN: 20.08.2012, validate input

        Iterable<IStatisticsCell> statisticsCells = null;

        try {
            final Iterator<Node> nodeIterator = getNodeService().getChildrenChain(statisticsRowNode);

            statisticsCells = new StatisticsCellIterator(nodeIterator).toIterable();
        } catch (final ServiceException e) {
            processException("Error on getting chain of Statistics Cells", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsCells"));
        }

        return statisticsCells;
    }

    @Override
    public Iterable<IStatisticsRow> getStatisticsRows(final String period) throws ModelException {
        return getStatisticsRowsInTimeRange(period, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public Iterable<IStatisticsRow> getStatisticsRowsInTimeRange(final String period, final long startTime, final long endTime)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsRows", period));
        }

        // TODO: LN: 20.08.2012, validate input

        Iterable<IStatisticsRow> statisticsRows = null;

        final Node periodNode = getStatisticsLevelNode(DimensionType.TIME, period);
        try {
            final Iterator<Node> groupNodeIterator = getNodeService().getChildren(periodNode, NodeServiceRelationshipType.CHILD);

            final List<Node> allRowsIterator = new ArrayList<Node>();
            // TODO KV: seems like IteratorUtils.chaindedIterator() works badly, check this
            // solution.
            while (groupNodeIterator.hasNext()) {
                allRowsIterator.addAll(Lists.newArrayList(statisticsService.getRowsInTimeRange(groupNodeIterator.next(), startTime,
                        endTime)));
            }
            statisticsRows = new StatisticsRowIterator(allRowsIterator.iterator()).toIterable();
        } catch (final ServiceException e) {
            processException("Error on getting chain of Statistics Rows", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsRows"));
        }

        return statisticsRows;
    }

    @Override
    public Set<String> getColumns() {
        return columnNames;
    }

    @Override
    public String getAggregatedProperty() {
        return aggregatedProperty;
    }

    @Override
    public boolean containsLevel(final DimensionType dimension, final String levelName) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("containsLevel", dimension, levelName));
        }

        try {
            return statisticsService.findStatisticsLevel(getRootNode(), dimension, levelName) != null;
        } catch (final ServiceException e) {
            processException("Error on checking Statistics Level", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("containsLevel"));
        }
        return false;
    }

    @Override
    public void flush() throws ModelException {
        statisticsCellNodeCache.clear();
        statisticsRowCache.clear();
        summuryCache.clear();
        super.flush();
    }

    @Override
    public void setLevelCount(final DimensionType dimension, final String levelName, final int count) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("setLevelCount", dimension, levelName, count));
        }

        final Node levelNode = getStatisticsLevelNode(dimension, levelName);
        try {
            getNodeService().updateProperty(levelNode, getGeneralNodeProperties().getSizeProperty(), count);
        } catch (final ServiceException e) {
            processException("Error on updating count for level <" + dimension + ":" + levelName + ">.", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("setLevelCount"));
        }
    }

    @Override
    public int getLevelCount(final DimensionType dimension, final String levelName) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getLevelCount", dimension, levelName));
        }

        int result = 0;

        final Node levelNode = getStatisticsLevelNode(dimension, levelName);
        try {
            result = getNodeService().getNodeProperty(levelNode, getGeneralNodeProperties().getSizeProperty(), 0, false);
        } catch (final ServiceException e) {
            processException("Error on getting count for level <" + dimension + ":" + levelName + ">.", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getLevelCount"));
        }

        return result;
    }

    @Override
    public IStatisticsRow getSummuryRow(final IStatisticsGroup statisticsGroup) throws ModelException {
        try {
            StatisticsRow row = summuryCache.get(statisticsGroup);
            if (row == null) {

                row = (StatisticsRow)getStatisticsRowFromDatabase((StatisticsGroup)statisticsGroup, null, Long.MAX_VALUE,
                        Long.MIN_VALUE);
                final Node sRowNode = row.getNode();
                row.setSummury(true);
                getNodeService().updateProperty(sRowNode, statisticsNodeProperties.isSummuryProperty(), true);
                row.setStatisticsGroup(statisticsGroup);
                summuryCache.put((StatisticsGroup)statisticsGroup, row);
            }

            return row;
        } catch (final ServiceException e) {
            processException("can't get syummury row from group" + statisticsGroup, e);
        }
        return null;
    }

    protected void updateSummury(final StatisticsRow summuryRow, final long startTime, final long endTime) throws ServiceException {
        if (endTime > summuryRow.getEndDate()) {
            updateSummuryRowDate(summuryRow, endTime, timePeriodNodeProperties.getEndDateProperty(),
                    timePeriodNodeProperties.getEndDateTimestampProperty());
            summuryRow.setEndDate(endTime);

        }
        if (startTime < summuryRow.getStartDate()) {
            updateSummuryRowDate(summuryRow, startTime, timePeriodNodeProperties.getStartDateProperty(),
                    timePeriodNodeProperties.getStartDateTimestampProperty());
            summuryRow.setStartDate(startTime);

        }
    }

    private void updateSummuryRowDate(final StatisticsRow row, final long currentDate, final String dateProperty,
            final String dateTimestampProperty) throws ServiceException {
        final Date date = new Date(currentDate);
        final String dateString = DateFormatManager.getInstance().getDefaultFormat().format(date);
        getNodeService().updateProperty(row.getNode(), dateProperty, dateString);
        getNodeService().updateProperty(row.getNode(), dateTimestampProperty, currentDate);
    }

    @Override
    public Iterable<IStatisticsGroup> getAllStatisticsGroups(final DimensionType type, final String levelName)
            throws ModelException {
        return getAllStatisticsGroups(type, levelName, null);
    }

    public Iterable<IStatisticsGroup> getAllStatisticsGroups(final DimensionType type, final String levelName,
            final IStatisticsFilter filter) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getAllStatisticsGroups", type, levelName, filter));
        }

        // TODO: LN: 20.08.2012, validate input

        final Node periodNode = getStatisticsLevelNode(type, levelName);
        Iterator<Node> groupNodeIterator = null;
        try {
            groupNodeIterator = getNodeService().getChildren(periodNode, NodeServiceRelationshipType.CHILD);
        } catch (final ServiceException e) {
            processException("can't find groups", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getAllStatisticsGroups"));
        }

        return new StatisticsGroupIterator(groupNodeIterator, filter).toIterable();
    }

    @Override
    public Iterable<IStatisticsLevel> findAllStatisticsLevels(final DimensionType type) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findAllStatisticsLevels", type));
        }

        final Iterable<IStatisticsLevel> result = findAllStatisticsLevels(type, null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findAllStatisticsLevels"));
        }
        return result;
    }

    private boolean hasUnderlineSource(final IDataElement element) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("hasUnderlineSource", element));
        }
        final DataElement statElement = (DataElement)element;
        Iterator<Node> sources = null;
        try {
            sources = statisticsService.getAllSources(statElement.getNode());
            if (sources.hasNext() && element.getNodeType().equals(getNodeService().getNodeType(sources.next()))) {
                return true;
            }
        } catch (final ServiceException e) {
            processException("can't get sources from node " + statElement, e);
        } catch (final NodeTypeNotExistsException e) {
            LOGGER.error("can't get node type for", e);
        }
        return false;
    }

    @Override
    public Iterable<IStatisticsCell> getSourceCells(final IStatisticsCell cell) throws ModelException {
        if (!hasUnderlineSource(cell)) {
            return null;
        }
        final Iterator<Node> sources = getSourcesNodes(cell);
        return new StatisticsCellIterator(sources).toIterable();

    }

    @Override
    public Iterable<IDataElement> getSources(final IDataElement cell) throws ModelException {
        final Iterator<Node> sources = getSourcesNodes(cell);
        if (cell.getNodeType().equals(StatisticsNodeType.S_CELL)) {
            return new DataElementIterator(sources, measurementNodeProperties.getEventProperty()).toIterable();
        }
        return new DataElementIterator(sources).toIterable();
    }

    @Override
    public Iterable<IStatisticsRow> getSourceRows(final IStatisticsRow row) throws ModelException {
        if (!hasUnderlineSource(row)) {
            return null;
        }
        final Iterator<Node> sources = getSourcesNodes(row);
        return new StatisticsRowIterator(sources).toIterable();
    }

    private Iterator<Node> getSourcesNodes(final IDataElement element) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getCellSources", element));
        }

        final DataElement statCell = (DataElement)element;
        Iterator<Node> sources = null;
        try {
            sources = statisticsService.getAllSources(statCell.getNode());
        } catch (final ServiceException e) {
            processException("can't get sources from node " + element, e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getCellSources"));
        }
        return sources;
    }

    @Override
    public IDataElement getParent(final IDataElement childElement, final DimensionType dimension) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getParent", childElement, dimension));
        }

        IDataElement result = null;
        final DataElement child = (DataElement)childElement;

        if (child.getNodeType().equals(StatisticsNodeType.S_CELL)) {
            try {
                final Node parentNode = getNodeService().getChainParent(child.getNode());

                result = createStatisticsRow(parentNode);
            } catch (final ServiceException e) {
                processException("Error on getting parent of SCell", e);
            }
        } else if (child instanceof IStatisticsRow) {
            final IStatisticsRow childRow = (IStatisticsRow)child;

            result = childRow.getStatisticsGroup();
        } else if (child instanceof IStatisticsGroup) {
            final IStatisticsGroup childGroup = (IStatisticsGroup)child;

            String name = null;

            switch (dimension) {
            case PROPERTY:
                name = childGroup.getPropertyValue();
                break;
            case TIME:
                name = childGroup.getPeriod();
                break;
            }

            final Node periodNode = getStatisticsLevelNode(dimension, name);

            result = getDataElement(periodNode, StatisticsNodeType.LEVEL, name);
        } else if (child.getNodeType().equals(StatisticsNodeType.LEVEL)) {
            return asDataElement();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getParent"));
        }

        return result;
    }

    @Override
    public IDataElement getParentElement(final IDataElement childElement) throws ModelException {
        return getParent(childElement, DimensionType.TIME);
    }

    @Override
    public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getChildren", parentElement));
        }

        final Iterable<IDataElement> result = getChildren(parentElement, null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getChildren"));
        }

        return result;
    }

    @Override
    public String getName() {
        final StringBuilder builder = new StringBuilder();

        builder.append(getSourceModel().getName() + " {" + super.getName() + "}");

        return builder.toString();
    }

    private StatisticsLevel createStatisticsLevel(final Node node, final DimensionType dimension) {
        final StatisticsLevel result = new StatisticsLevel(node, dimension);

        try {
            result.setNodeType(StatisticsNodeType.LEVEL);
            result.setName(getNodeService().getNodeName(node));
        } catch (final ServiceException e) {
            LOGGER.error("Error on initializing Statistics Level", e);
        }

        return result;
    }

    @Override
    public Iterable<IDataElement> getChildren(final IDataElement parentElement, final IStatisticsFilter filter)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getChildren", parentElement, filter));
        }

        Iterable<IDataElement> result = null;

        if (parentElement.getNodeType().equals(StatisticsNodeType.LEVEL)) {
            final IStatisticsLevel level = (IStatisticsLevel)parentElement;

            result = new DataElementConverter<IStatisticsGroup>(getAllStatisticsGroups(level.getDimension(), level.getName(),
                    filter).iterator()).toIterable();
        } else if (parentElement.getNodeType().equals(StatisticsNodeType.GROUP)) {
            final IStatisticsGroup group = (IStatisticsGroup)parentElement;

            if (filter != null) {
                result = new DataElementConverter<IStatisticsRow>(getStatisticsRowsInTimeRange(group.getPeriod(),
                        filter.getStartTime(), filter.getEndTime()).iterator()).toIterable();
            } else {
                result = new DataElementConverter<IStatisticsRow>(getStatisticsRows(group.getPeriod()).iterator()).toIterable();
            }
        } else if (parentElement.getNodeType().equals(StatisticsNodeType.S_ROW)) {
            final IStatisticsRow row = (IStatisticsRow)parentElement;

            result = new StatisticsCellFilteredConverter(row.getStatisticsCells().iterator(), filter).toIterable();
        } else if (parentElement.getNodeType().equals(StatisticsNodeType.S_CELL)) {
            final IStatisticsCell cell = (IStatisticsCell)parentElement;

            final Iterable<IStatisticsCell> sourceCells = getSourceCells(cell);

            if (sourceCells != null) {
                result = new DataElementConverter<IStatisticsCell>(sourceCells.iterator()).toIterable();
            } else {
                result = getSources(cell);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getChildren"));
        }
        return result;
    }

    @Override
    public Iterable<IStatisticsLevel> findAllStatisticsLevels(final DimensionType type, final IStatisticsFilter filter)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findAllStatisticsLevels", type, filter));
        }

        Iterable<IStatisticsLevel> result = null;

        try {
            final Iterator<Node> levels = statisticsService.findAllStatisticsLevelNode(getRootNode(), type);

            result = new StatisticsLevelIterator(levels, filter, type).toIterable();
        } catch (final ServiceException e) {
            processException("Error when try to find all levels nodes", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findAllStatisticsLevels"));
        }

        return result;
    }
}
