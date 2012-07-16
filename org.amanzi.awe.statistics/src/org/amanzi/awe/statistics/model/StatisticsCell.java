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

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsCell entity. Can be instantiated only from {@link StatisticsRow}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsCell extends AbstractEntity {

    private static final Logger LOGGER = Logger.getLogger(StatisticsCell.class);
    private boolean isSelected = false;
    private StatisticsRow row;

    /**
     * constructor for instantiation
     * 
     * @param parent
     * @param cellNode
     */
    StatisticsCell(Node parent, Node cellNode) {
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
     * set or remove flagged is true- set flaggedProperty to group else remove it from group node
     * 
     * @param flagged
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void setFlagged(boolean flagged) throws IllegalNodeDataException, DatabaseException {
        if (flagged) {
            statisticService.setAnyProperty(rootNode, PROPERTY_FLAGGED_NAME, flagged);
        } else {
            statisticService.removeNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);

        }
    }

    /**
     * return flagged value of group
     * 
     * @return
     */
    public boolean isFlagged() {
        Boolean isFlagged = (Boolean)statisticService.getNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);
        if (isFlagged == null) {
            return Boolean.FALSE;
        }
        return isFlagged();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean select) {
        this.isSelected = select;
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

    @Override
    protected void loadChildIfNecessary() {
    }
}
