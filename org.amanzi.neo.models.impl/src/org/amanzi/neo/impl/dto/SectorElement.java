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

package org.amanzi.neo.impl.dto;

import org.amanzi.neo.models.network.INetworkModel.ISectorElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SectorElement extends DataElement implements ISectorElement {

    private Double azimuth;

    private Double beamwidth;

    public SectorElement(final Node node) {
        super(node);
    }

    /**
     * @return Returns the azimuth.
     */
    @Override
    public Double getAzimuth() {
        return azimuth;
    }

    /**
     * @param azimuth The azimuth to set.
     */
    public void setAzimuth(final Double azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * @return Returns the beamwidth.
     */
    @Override
    public Double getBeamwidth() {
        return beamwidth;
    }

    /**
     * @param beamwidth The beamwidth to set.
     */
    public void setBeamwidth(final Double beamwidth) {
        this.beamwidth = beamwidth;
    }
}
