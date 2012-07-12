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
     * constructor for instantiation
     * 
     * @param existed
     * @throws DatabaseException
     */
    StatisticsCell(Node existed) throws DatabaseException {
        super(existed, StatisticsNodeTypes.S_CELL);
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
     * return list of sources elements
     * 
     * @param parentNode
     * @return
     */
    public Iterable<IDataElement> getSources() {
        if (parentNode == null) {
            LOGGER.error(" scell node cann't be null");
            throw new IllegalArgumentException("Scell cannt be null");
        }
        Iterable<Node> sources = statisticService.getSources(rootNode);
        List<IDataElement> sourcedElements = new ArrayList<IDataElement>();
        for (Node sourceNode : sources) {
            sourcedElements.add(new DataElement(sourceNode));
        }
        return sourcedElements;
    }
}
