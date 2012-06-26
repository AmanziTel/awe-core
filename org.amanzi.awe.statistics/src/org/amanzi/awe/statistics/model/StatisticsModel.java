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

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Statistics Model
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModel implements IModel {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsModel.class);
    private static final String STATISTICS_POSTFIX = "Statistics";
    private static final String SPACE_SEPARATOR = " ";

    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    static StatisticsService statisticService;
    /*
     * root statistics data
     */
    private Node rootNode;
    private String name;
    private INodeType nodeType = StatisticsNodeTypes.STATISTICS;

    /**
     * initialize statistics services
     */
    private static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    public StatisticsModel(Node parentNode) throws IllegalArgumentException, DatabaseException, DuplicateNodeNameException {
        initStatisticsService();
        if (parentNode == null) {
            LOGGER.error("parentNode is null");
            throw new IllegalArgumentException("parentNode can't be null");
        }
        this.name = (String)parentNode.getProperty(DatasetService.NAME, StringUtils.EMPTY) + SPACE_SEPARATOR + STATISTICS_POSTFIX;
        if (statisticService.findStatistic(parentNode, name) != null) {
            throw new DuplicateNodeNameException(name, StatisticsNodeTypes.STATISTICS);
        }
        rootNode = statisticService.createStatisticsModelRoot(parentNode, name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public INodeType getType() {
        return nodeType;
    }

    @Override
    public void finishUp() throws AWEException {
    }
}
