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

package org.amanzi.neo.providers.impl;

import org.amanzi.neo.models.IDataModel;
import org.amanzi.neo.models.distribution.IDistributionModel;
import org.amanzi.neo.models.impl.distribution.DistributionModel;
import org.amanzi.neo.providers.IDistributionModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionModelProvider extends AbstractModelProvider<DistributionModel, IDataModel> implements IDistributionModelProvider<DistributionModel> {

    @Override
    public Class< ? > getModel() {
        return IDistributionModel.class;
    }

}
