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

package org.amanzi.neo.models.impl.distribution;

import java.awt.Color;
import java.util.List;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.distribution.IDistribution;
import org.amanzi.neo.models.distribution.IDistributionBar;
import org.amanzi.neo.models.distribution.IDistributionModel;
import org.amanzi.neo.models.distribution.IDistributionalModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.services.INodeService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.brewer.color.BrewerPalette;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionModel extends AbstractModel implements IDistributionModel {

    /**
     * @param nodeService
     */
    public DistributionModel(INodeService nodeService) {
        super(nodeService);
    }

    @Override
    public void finishUp() throws ModelException {
    }

    @Override
    public IDistribution< ? > getDistributionType() {
        return null;
    }

    @Override
    public List<IDistributionBar> getDistributionBars() throws ModelException {
        return null;
    }

    @Override
    public List<IDistributionBar> getDistributionBars(IProgressMonitor monitor) throws ModelException {
        return null;
    }

    @Override
    public void updateBar(IDistributionBar bar) throws ModelException {
    }

    @Override
    public void setCurrent(boolean isCurrent) throws ModelException {
    }

    @Override
    public Color getRightColor() {
        return null;
    }

    @Override
    public void setRightColor(Color rightBarColor) {
    }

    @Override
    public Color getLeftColor() {
        return null;
    }

    @Override
    public void setLeftColor(Color leftBarColor) {
    }

    @Override
    public Color getMiddleColor() {
        return null;
    }

    @Override
    public void setMiddleColor(Color middleBarColor) {
    }

    @Override
    public int getBarCount() {
        return 0;
    }

    @Override
    public BrewerPalette getPalette() {
        return null;
    }

    @Override
    public void setPalette(BrewerPalette palette) {
    }

    @Override
    public IDistributionalModel getAnalyzedModel() {
        return null;
    }

    @Override
    public Color getBarColorForAggregatedElement(IDataElement element) {
        return null;
    }

}
