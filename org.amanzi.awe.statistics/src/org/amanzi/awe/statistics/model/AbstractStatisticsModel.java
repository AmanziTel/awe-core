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

import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.impl.AbstractModel;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractStatisticsModel extends AbstractModel {
    protected Node parentNode;

    /**
     * @param nodeType
     */
    protected AbstractStatisticsModel(INodeType nodeType) {
        super(nodeType);
    }

    /*
     * statistics service
     */
    static StatisticsService statisticService;

    /*
     * service instantiation
     */
    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    /*
     * /** initialize statistics services
     */
    static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }
}
