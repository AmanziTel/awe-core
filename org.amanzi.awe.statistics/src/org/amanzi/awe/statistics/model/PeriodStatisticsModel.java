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
import org.amanzi.neo.services.model.impl.AbstractModel;
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

    static StatisticsService statisticService;

    private List<PeriodStatisticsModel> sourcePeriod;

    /*
     * initialize statistics services
     */
    private static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    public PeriodStatisticsModel(Node periodNode) throws IllegalArgumentException {
        super(StatisticsNodeTypes.PERIOD_STATISTICS);
        initStatisticsService();
        if (periodType == null) {
            throw new IllegalArgumentException("Period node cann't be null");
        }
        rootNode = periodNode;
        periodType = Period.findById(periodNode.getProperty(DatasetService.NAME, StringUtils.EMPTY).toString());
        name = periodType.getId();

    }

    public List<PeriodStatisticsModel> getSourcePeriods() {
        if (sourcePeriod == null) {
            sourcePeriod = new ArrayList<PeriodStatisticsModel>();
            initSourcesList();
        }
        return sourcePeriod;
    }

    /**
     *
     */
    private void initSourcesList() {
        Iterator<Node> sources = statisticService.getSources(rootNode).iterator();
        while (sources.hasNext()) {
            sourcePeriod.add(new PeriodStatisticsModel(sources.next()));
        }

    }
}
