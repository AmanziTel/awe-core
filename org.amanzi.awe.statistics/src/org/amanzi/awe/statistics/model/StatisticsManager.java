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

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.ITimelineModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * execute statistics building. store common statistics information
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsManager {

    /*
     * logger
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsManager.class);
    /*
     * statistics manager singleton instance
     */
    private static StatisticsManager statisticsManager;

//    private StatisticsModel currentStatisticsModel;

    /*
     * cann't be created directly. Just through getInstance.
     */
    private StatisticsManager() {
    }

    /**
     * get instance of {@link StatisticsManager}
     * 
     * @return
     */
    public static StatisticsManager getInstance() {
        if (statisticsManager == null) {
            statisticsManager = new StatisticsManager();
        }
        return statisticsManager;
    }

    /**
     * @param template statistics template
     * @param parentModel model which implements {@link ITimelineModel} interface
     * @param propertyName property which should be aggregated
     * @param period period for aggregation
     * @throws DatabaseException
     */
    public void processStatistics(Object template, ITimelineModel parentModel, String propertyName, Period period,
            IProgressMonitor monitor) throws DatabaseException {
        LOGGER.info("Process statistics calculation");
        // try {
        // currentStatisticsModel = new StatisticsModel(parentModel.getRootNode(), "template");
        // } catch (DatabaseException e) {
        // LOGGER.error("Can't instantiate statistics model ", e);
        // }
//        Dimension timeDimension = currentStatisticsModel.getDimension(DimensionTypes.TIME);
//        Dimension networkDimension = currentStatisticsModel.getDimension(DimensionTypes.NETWORK);
    }

}
