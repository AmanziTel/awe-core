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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.INetworkModel.ISectorElement;
import org.amanzi.neo.models.network.INetworkModel.ISiteElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SiteElement extends LocationElement implements ISiteElement {

    private final List<ISectorElement> sectorElements = new ArrayList<INetworkModel.ISectorElement>();

    @Override
    public List<ISectorElement> getSectors() {
        return sectorElements;
    }

    public void addSector(final ISectorElement sector) {
        sectorElements.add(sector);
    }

}
