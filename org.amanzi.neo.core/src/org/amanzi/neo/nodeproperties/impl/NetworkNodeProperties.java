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

package org.amanzi.neo.nodeproperties.impl;

import org.amanzi.neo.nodeproperties.INetworkNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkNodeProperties implements INetworkNodeProperties {

    private static final String CI_PROPERTY = "ci";

    private static final String LAC_PROPERTY = "lac";

    private static final String BEAMWIDTH_PROPERTY = "beamwidth";

    private static final String AZIMUTH_PROPERTY = "azimuth";

    private static final String STRUCTURE = "structure";

    @Override
    public String getCIProperty() {
        return CI_PROPERTY;
    }

    @Override
    public String getLACProperty() {
        return LAC_PROPERTY;
    }

    @Override
    public String getAzimuthProperty() {
        return AZIMUTH_PROPERTY;
    }

    @Override
    public String getBeamwidthProperty() {
        return BEAMWIDTH_PROPERTY;
    }

    @Override
    public String getStuctureProperty() {
        return STRUCTURE;
    }
}
