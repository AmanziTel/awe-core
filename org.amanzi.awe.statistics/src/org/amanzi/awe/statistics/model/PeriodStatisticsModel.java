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

package org.amanzi.awe.statistics.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.AbstractModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Purposed to work with single period. User <b>must not have</b> possibility to create period model
 * nodes . Access to period statistics model available only from {@link StatisticsModel}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PeriodStatisticsModel extends AbstractModel {

    /*
     * logger instantiation;
     */
    private static final Logger LOGGER = Logger.getLogger(PeriodStatisticsModel.class);
    /*
     * statistics service
     */
    private static StatisticsService statisticService;
    /*
     * period type
     */
    private Period periodType;

    /*
     * service instantiation
     */
    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    /*
     * source period
     */
    private PeriodStatisticsModel sourcePeriod;

    /*
     * initialize statistics services
     */
    private static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    /**
     * instantiation of period model
     * 
     * @param period
     * @throws DatabaseException
     */
    PeriodStatisticsModel(Node parent, Period period) throws DatabaseException, IllegalArgumentException {
        super(StatisticsNodeTypes.PERIOD_STATISTICS);
        initStatisticsService();

        if (parent == null) {
            throw new IllegalArgumentException("Parent node cann't be null");
        }
        if (period == null) {
            throw new IllegalArgumentException("Period node cann't be null");
        }
        rootNode = statisticService.getPeriod(parent, period);
        periodType = Period.findById(rootNode.getProperty(DatasetService.NAME, StringUtils.EMPTY).toString());
        name = periodType.getId();
        initSourcePeriod();
    }

    /**
     * Init new period statisticsModel
     * 
     * @param periodNode
     * @throws IllegalArgumentException
     */
    PeriodStatisticsModel(Node periodNode) throws IllegalArgumentException {
        super(StatisticsNodeTypes.PERIOD_STATISTICS);
        initStatisticsService();
        if (periodNode == null) {
            throw new IllegalArgumentException("Period node cann't be null");
        }
        rootNode = periodNode;
        periodType = Period.findById(periodNode.getProperty(DatasetService.NAME, StringUtils.EMPTY).toString());
        name = periodType.getId();
        initSourcePeriod();

    }

    /**
     * initialize sources period
     */
    private void initSourcePeriod() {
        Iterable<Node> sources = statisticService.getSources(rootNode);
        if (sources == null) {
            return;
        }
        Iterator<Node> sourceIterator = sources.iterator();
        if (sourceIterator.hasNext()) {
            sourcePeriod = new PeriodStatisticsModel(sourceIterator.next());
        }
    }

    /**
     * get sources periods
     * 
     * @return source period if exist -> else return null;
     */
    public PeriodStatisticsModel getSourcePeriod() {
        return sourcePeriod;
    }

    /**
     * add source period
     * 
     * @param source
     * @return
     * @throws DatabaseException
     */
    PeriodStatisticsModel addSourcePeriod(PeriodStatisticsModel source) throws DatabaseException {
        if (source == null) {
            LOGGER.error("source period cann't be null");
            throw new IllegalArgumentException("source period is null");
        }
        statisticService.addSource(rootNode, source.getRootNode());
        sourcePeriod = source;
        return source;
    }

    /**
     * try to find S_ROW node by timestamp if not exist- create new one;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public IDataElement getSRow(Long timestamp) throws DatabaseException, IllegalNodeDataException {
        Node srowNode = statisticService.findNodeInChain(rootNode, DriveModel.TIMESTAMP, timestamp);
        if (srowNode != null) {
            return new DataElement(srowNode);
        }
        return new DataElement(statisticService.createSRow(rootNode, timestamp, false));
    }

    /**
     * try to find S_CELL node by name if not exist- create new one;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public IDataElement getSCell(IDataElement parentSRow, String name) throws DatabaseException, IllegalNodeDataException,
            IllegalArgumentException {
        if (parentSRow == null) {
            IllegalArgumentException e = new IllegalArgumentException("S_ROW node cann't be null");
            LOGGER.error("S_ROW node cann't be null. S_CELL must have a parrent", e);
            throw e;
        }
        String parentType = (String)parentSRow.get(DatasetService.TYPE);
        if (!StatisticsNodeTypes.S_ROW.getId().equals(parentType)) {
            IllegalArgumentException e = new IllegalArgumentException("Unexpected type of parent node");
            LOGGER.error("parent node must have type S_ROW type but actually is " + parentType, e);
            throw e;
        }
        if (name == null || name.isEmpty()) {
            IllegalArgumentException e = new IllegalArgumentException("provided S_CELL name is Incorrect");
            LOGGER.error("name of S_CELL node must have a name. currently name is " + name);
            throw e;
        }
        Node parentSRowNode = ((DataElement)parentSRow).getNode();
        Node srowNode = statisticService.findNodeInChain(parentSRowNode, DatasetService.NAME, name);
        if (srowNode != null) {
            return new DataElement(srowNode);
        }
        return new DataElement(statisticService.createSCell(parentSRowNode, name, false));
    }

    /**
     * create source relationship between scellNode and list of source nodes
     * 
     * @param scellNode
     * @param sources
     * @throws DatabaseException
     */
    public void addSources(IDataElement scellNode, List<IDataElement> sources) throws DatabaseException {
        if (scellNode == null) {
            LOGGER.error(" scell node cann't be null");
            throw new IllegalArgumentException("Scell cannt be null");
        }
        if (sources == null) {
            LOGGER.error(" sources list cann't be null");
            throw new IllegalArgumentException("sources list cannt be null");
        }
        Node scell = ((DataElement)scellNode).getNode();
        for (IDataElement element : sources) {
            Node source = ((DataElement)element).getNode();
            statisticService.addSource(scell, source);
        }
    }

    /**
     * return list of sources elements
     * 
     * @param parentNode
     * @return
     */
    public Iterable<IDataElement> getSources(IDataElement parentNode) {
        if (parentNode == null) {
            LOGGER.error(" scell node cann't be null");
            throw new IllegalArgumentException("Scell cannt be null");
        }
        Node scell = ((DataElement)parentNode).getNode();
        Iterable<Node> sources = statisticService.getSources(scell);
        List<IDataElement> sourcedElements = new ArrayList<IDataElement>();
        for (Node sourceNode : sources) {
            sourcedElements.add(new DataElement(sourceNode));
        }
        return sourcedElements;
    }

    /**
     * @return Returns the periodType.
     */
    public Period getPeriodType() {
        return periodType;
    }
}
