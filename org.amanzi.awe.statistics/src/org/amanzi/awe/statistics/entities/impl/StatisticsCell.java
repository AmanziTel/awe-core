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

package org.amanzi.awe.statistics.entities.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.engine.IAggregationFunction;
import org.amanzi.awe.statistics.entities.IAggregatedStatisticsEntity;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.awe.statistics.exceptions.UnableToModifyException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsCell entity. Can be instantiated only from {@link StatisticsRow}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsCell extends AbstractFlaggedEntity implements IAggregatedStatisticsEntity {

    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsCell.class);

    private static final String VALUE_NAME = "value";

    private boolean isSelected = false;
    private StatisticsRow row;
    private IAggregationFunction function;

    /**
     * constructor for instantiation
     * 
     * @param parent
     * @param cellNode
     */
    public StatisticsCell(Node parent, Node cellNode) {
        super(parent, cellNode, StatisticsNodeTypes.S_CELL);
    }

    /**
     * create source relationship between scellNode and list of source nodes
     * 
     * @param scellNode
     * @param sources
     * @throws DatabaseException
     */
    public void addSources(List<IDataElement> sources) throws DatabaseException {
        if (sources == null) {
            LOGGER.error(" sources list cann't be null");
            throw new IllegalArgumentException("sources list cannt be null");
        }
        for (IDataElement element : sources) {
            Node source = ((DataElement)element).getNode();
            statisticService.addSource(rootNode, source);
        }
    }

    /**
     * add single source node
     * 
     * @param source
     * @throws DatabaseException
     */
    public void addSingleSource(IDataElement source) throws DatabaseException {
        if (source == null) {
            LOGGER.error("source node can't be null");
            throw new IllegalArgumentException("source is null");
        }
        statisticService.addSource(rootNode, ((DataElement)source).getNode());
    }

    /**
     * create {@link StatisticsRelationshipTypes#SOURCE} relationship between this source and
     * sourceCell
     * 
     * @param sourceCell
     * @throws DatabaseException
     */
    public void addSourceCell(StatisticsCell sourceCell) throws DatabaseException {
        if (sourceCell == null) {
            LOGGER.error("source cell can't be null");
            throw new IllegalArgumentException("source is null");
        }
        statisticService.addSource(rootNode, sourceCell.getRootNode());
    }

    /**
     * return list of sources elements
     * 
     * @param parentNode
     * @return
     */
    public Iterable<IDataElement> getSources() {
        Iterable<Node> sources = statisticService.getSources(rootNode);
        List<IDataElement> sourcedElements = new ArrayList<IDataElement>();
        for (Node sourceNode : sources) {
            sourcedElements.add(new DataElement(sourceNode));
        }
        return sourcedElements;
    }

    /**
     * check if cell is selected
     */
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean select) {
        this.isSelected = select;
    }

    /**
     * update cell value
     * 
     * @param value
     * @return
     * @throws UnableToModifyException
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    public boolean updateValue(Number value) throws UnableToModifyException, IllegalNodeDataException, DatabaseException {
        if (function == null) {
            LOGGER.error("Unnable to modify node . Probably  reason is read only parameter is true");
            throw new UnableToModifyException("Unnable to modify node . Perhaps the reason is read only parameter is true");
        }
        if (value != null || (value == null && function.acceptsNulls())) {
            statisticService.setAnyProperty(rootNode, VALUE_NAME, function.update(value).getResult());
            return true;
        }
        return false;
    }

    /**
     * return cell value
     * 
     * @return
     */
    public Number getValue() {
        return (Number)statisticService.getNodeProperty(rootNode, VALUE_NAME);
    }

    /**
     * get row ,this cell belongs to;
     * 
     * @return
     */
    public StatisticsRow getParent() {
        if (row == null) {
            row = new StatisticsRow(statisticService.getParentLevelNode(parentNode), parentNode);
        }
        return row;
    }

    /**
     * @param set function
     */
    public void setFunction(IAggregationFunction function) {
        this.function = function;
    }

   
}
