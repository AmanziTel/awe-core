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

import java.awt.Color;
import java.util.List;

import org.amanzi.awe.distribution.dto.IAggregationRelation;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.ITreeModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IDistributionModel extends IAnalyzisModel<IPropertyStatisticalModel>, ITreeModel {

    List<IDistributionBar> getDistributionBars() throws ModelException;

    void setCurrent(boolean isCurrent) throws ModelException;

    IDistributionBar createDistributionBar(IRange range) throws ModelException;

    IAggregationRelation createAggregation(IDistributionBar bar, IDataElement element) throws ModelException;

    Color getRightColor();

    Color getLeftColor();

    Color getMiddleColor();

    void setRightColor(Color color);

    void setMiddleColor(Color color);

    void setLeftColor(Color color);

    IDistributionBar findDistributionBar(IDataElement dataElement) throws ModelException;

    IAggregationRelation findAggregationRelation(IDataElement dataElement) throws ModelException;

    void updateAggregationRelation(IDataElement dataElement, IAggregationRelation relation, IDistributionBar bar)
            throws ModelException;

    String getPropertyName();

    INodeType getDistributionNodeType();
}
