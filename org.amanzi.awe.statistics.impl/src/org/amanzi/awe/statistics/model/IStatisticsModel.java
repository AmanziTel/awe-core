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

import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IStatisticsModel extends IModel {

    IStatisticsGroup getStatisticsGroup(String period, String propertyKey) throws ModelException;

    IStatisticsRow getStatisticsRow(IStatisticsGroup group, IStatisticsRow sourceRow, long startDate, long endDate)
            throws ModelException;

    IStatisticsRow getStatisticsRow(IStatisticsGroup group, long startDate, long endDate) throws ModelException;

    boolean updateStatisticsCell(IStatisticsRow statisticsRow, String name, Object value, IDataElement... sourceElements)
            throws ModelException;

    Iterable<IStatisticsRow> getStatisticsRows(String period) throws ModelException;

    Iterable<IStatisticsGroup> getStatisticsGroups() throws ModelException;

    Set<String> getColumns();

    String getAggregatedProperty();

    boolean containsLevel(DimensionType dimension, String levelName) throws ModelException;

    void setLevelCount(DimensionType dimension, String levelName, int count) throws ModelException;

    int getLevelCount(DimensionType dimension, String levelName) throws ModelException;

}
