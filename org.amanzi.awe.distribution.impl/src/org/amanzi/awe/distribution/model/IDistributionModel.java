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

package org.amanzi.awe.distribution.model;

import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IDistributionModel extends IAnalyzisModel<IPropertyStatisticalModel> {

    Iterable<IDistributionBar> getDistributionBars() throws ModelException;

    int getDistributionBarsCount() throws ModelException;

    void setCurrent(boolean isCurrent) throws ModelException;

    void updateBar(IDistributionBar bar) throws ModelException;

    IDistributionBar createDistributionBar(IRange range) throws ModelException;

    void createAggregation(IDistributionBar bar, IDataElement element) throws ModelException;

}
