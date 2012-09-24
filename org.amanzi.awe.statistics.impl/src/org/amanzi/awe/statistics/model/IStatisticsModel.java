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

import java.util.Set;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IStatisticsModel extends IAnalyzisModel<IMeasurementModel> {

    IStatisticsGroup getStatisticsGroup(String period, String propertyKey) throws ModelException;

    IStatisticsRow getStatisticsRow(IStatisticsGroup group, IStatisticsRow sourceRow, long startDate, long endDate)
            throws ModelException;

    IStatisticsRow getStatisticsRow(IStatisticsGroup group, long startDate, long endDate) throws ModelException;

    boolean updateStatisticsCell(IStatisticsRow statisticsRow, String name, Object value, IDataElement... sourceElements)
            throws ModelException;

    Iterable<IStatisticsRow> getStatisticsRows(String period) throws ModelException;

    Set<String> getColumns();

    String getAggregatedProperty();

    boolean containsLevel(DimensionType dimension, String levelName) throws ModelException;

    void setLevelCount(DimensionType dimension, String levelName, int count) throws ModelException;

    int getLevelCount(DimensionType dimension, String levelName) throws ModelException;

    IStatisticsRow getSummuryRow(IStatisticsGroup statisticsGroup) throws ModelException;

    Iterable<IDataElement> findAllStatisticsLevels(DimensionType type) throws ModelException;

    Iterable<IStatisticsGroup> getAllStatisticsGroups(DimensionType type, String levelName) throws ModelException;

    Iterable<IDataElement> getSources(IDataElement cell) throws ModelException;

    Iterable<IStatisticsCell> getSourceCells(IStatisticsCell cell) throws ModelException;

    Iterable<IStatisticsRow> getSourceRows(IStatisticsRow row) throws ModelException;

    IDataElement getParent(IDataElement childElement, DimensionType dimension) throws ModelException;

    Iterable<IStatisticsRow> getStatisticsRowsInTimeRange(String period, long startTime, long endTime) throws ModelException;

}
