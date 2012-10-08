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

package org.amanzi.awe.distribution.coloring;

import java.awt.Color;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.render.core.coloring.IColoringInterceptor;
import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionColoringInterceptor implements IColoringInterceptor {

    private final IDistributionModel distributionModel;

    public DistributionColoringInterceptor(final IDistributionModel distributionModel) {
        this.distributionModel = distributionModel;
    }

    @Override
    public Color getColor(final IDataElement dataElement) {
        // TODO Auto-generated method stub
        return null;
    }

}
