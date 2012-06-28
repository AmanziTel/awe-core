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

import java.util.Iterator;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.AbstractModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * period statistics model
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PeriodStatisticsModel extends AbstractModel {

    private Period periodType;

    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    private static StatisticsService statisticService;

    /*
     * initialize statistics services
     */
    private static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    /**
     * Init new period statisticsModel
     * 
     * @param periodNode
     * @throws IllegalArgumentException
     */
    public PeriodStatisticsModel(Node periodNode) throws IllegalArgumentException {
        super(StatisticsNodeTypes.PERIOD_STATISTICS);
        initStatisticsService();
        if (periodNode == null) {
            throw new IllegalArgumentException("Period node cann't be null");
        }
        rootNode = periodNode;
        periodType = Period.findById(periodNode.getProperty(DatasetService.NAME, StringUtils.EMPTY).toString());
        name = periodType.getId();

    }

    /**
     * get sources periods
     * 
     * @return source period if exist -> else return null;
     */
    public PeriodStatisticsModel getSourcePeriod() {
        Iterator<Node> sources = statisticService.getSources(rootNode).iterator();
        if (sources.hasNext()) {
            return new PeriodStatisticsModel(sources.next());
        }
        return null;
    }

    public PeriodStatisticsModel addSourcePeriod(IDataElement source) throws DatabaseException {
        Node node = ((DataElement)source).getNode();
        statisticService.addSource(rootNode, node);
        return new PeriodStatisticsModel(node);
    }
}
