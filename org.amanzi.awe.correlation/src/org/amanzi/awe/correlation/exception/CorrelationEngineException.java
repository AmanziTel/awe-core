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

package org.amanzi.awe.correlation.exception;

import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationEngineException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -6414741489027372340L;

    public CorrelationEngineException(final INetworkModel networkModel, final IMeasurementModel measurementModel,
            final String correlationProperty, final String correlatedProperty, final Exception e) {
        super("Error when trying to compute Correlating between " + networkModel + " and " + measurementModel
                + " by network property " + correlatedProperty + " and measurement property " + correlatedProperty, e);
    }
}
