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

package org.amanzi.neo.services.model;

import java.util.Map;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;

//TODO: LN: comments
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface ICountersModel extends ICorrelatableModel, ITimelineModel, IMeasurementModel, IDistributionalModel {

    /**
     * @return type of counters model
     */
    public ICountersType getCountersType();

    /**
     * just added new node in child-next structure;
     * 
     * @param param
     * @throws DatabaseException
     */
    public IDataElement addMeasurement(Map<String, Object> param) throws AWEException;
}
