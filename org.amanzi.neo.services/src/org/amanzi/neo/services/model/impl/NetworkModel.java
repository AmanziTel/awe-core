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

package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.geotools.referencing.CRS;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NetworkModel extends RenderableModel implements INetworkModel {

    @Override
    public void updateBounds(double latitude, double longitude) throws DatabaseException {
        super.updateLocationBounds(latitude, longitude);
    }

    @Override
    public double getMinLatitude() {
        return super.getMinLatitude();
    }

    @Override
    public double getMaxLatitude() {
        return super.getMaxLatitude();
    }

    @Override
    public double getMinLongitude() {
        return super.getMinLongitude();
    }

    @Override
    public double getMaxLongitude() {
        return super.getMaxLongitude();
    }

    @Override
    public CRS getCRS() {
        return null;
    }

    @Override
    public ICorrelationModel getCorrelationModel() {
        return null;
    }

    @Override
    public INetworkType getNetworkType() {
        return null;
    }

}
