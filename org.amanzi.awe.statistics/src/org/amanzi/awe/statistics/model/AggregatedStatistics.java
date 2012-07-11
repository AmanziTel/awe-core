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
import java.util.List;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Aggregated statistics consists of:<br>
 * <b>S_GROUP-</b> unique property value <br>
 * S_ROW -</b> list of periods separated in according with {@link DimensionTypes#TIME} dimension
 * (see : {@link Dimension}) level (see : {@link StatisticsLevel}) <br>
 * <b> S_CELL - </b> list of templates kpi's. S_CELL- has a list of sources.
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AggregatedStatistics extends AbstractLevelElement {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(AggregatedStatistics.class);

    private static final String NAME_FORMAT = "%s, %s";

    public AggregatedStatistics(StatisticsLevel firstLevel, StatisticsLevel secondLevel) throws DatabaseException,
            IllegalNodeDataException {
        super(StatisticsNodeTypes.STATISTICS);
        if (firstLevel == null || secondLevel == null) {
            LOGGER.error("can't create aggregated statistics element because of incorrect levels information");
            throw new IllegalArgumentException("StatisticsLevel elements can't be null");
        }
        Dimension firstLevelDimension = new Dimension(firstLevel.getParentNode());
        String firstLevelName = (String)firstLevel.getName();
        String secondLevelName = (String)secondLevel.getName();
        switch (firstLevelDimension.getDimensionType()) {
        case NETWORK:
            name = String.format(NAME_FORMAT, firstLevelName, secondLevelName);
            break;
        default:
            name = String.format(NAME_FORMAT, secondLevelName, firstLevelName);
            break;
        }
        rootNode = statisticService.createAggregatedStatistics(firstLevel.getRootNode(), secondLevel.getRootNode(), name);
    }

    /**
     * @param aggregatedNode
     */
    public AggregatedStatistics(Node aggregatedNode) {
        super(StatisticsNodeTypes.STATISTICS);
        if (aggregatedNode == null) {
            LOGGER.error("can't create aggregated statistics element because of null ");
            throw new IllegalArgumentException("can't create aggregated statistics element because of null ");
        }
        rootNode = aggregatedNode;
        name = (String)statisticService.getNodeProperty(aggregatedNode, DatasetService.NAME);
    }

    /**
     * try to find S_ROW node by timestamp if not exist- create new one;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public IDataElement getSRow(IDataElement sGroup, Long timestamp) throws DatabaseException, IllegalNodeDataException {
        if (sGroup == null) {
            LOGGER.error("group element is null.");
            throw new IllegalArgumentException("S_GROUP element is null");
        }
        if (timestamp == null) {
            LOGGER.error("timestamp element is null.");
            throw new IllegalArgumentException("timestamp element is null");
        }
        if (!sGroup.get(DatasetService.TYPE).equals(StatisticsNodeTypes.S_GROUP.getId())) {
            LOGGER.error("Parent element should have S_GROUP type, now is :" + sGroup.get(DatasetService.TYPE));
            throw new IllegalNodeDataException("Node type doesn't support by this operation");
        }
        Node sGroupd = ((DataElement)(sGroup)).getNode();
        Node srowNode = statisticService.findNodeInChain(sGroupd, DriveModel.TIMESTAMP, timestamp);
        if (srowNode != null) {
            return new DataElement(srowNode);
        }
        return new DataElement(statisticService.createSRow(sGroupd, timestamp, false));
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
     * get s_group data Element or create new one if not exist
     * 
     * @param aggregationElement
     * @param name
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    public IDataElement getSGroup(String name) throws DatabaseException, IllegalNodeDataException {
        Node srowNode = statisticService.findNodeInChain(rootNode, DatasetService.NAME, name);
        if (srowNode != null) {
            return new DataElement(srowNode);
        }
        return new DataElement(statisticService.createSGroup(rootNode, name, false));
    }
}
