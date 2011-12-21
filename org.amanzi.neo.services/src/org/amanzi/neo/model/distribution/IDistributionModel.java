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

package org.amanzi.neo.model.distribution;

import java.awt.Color;
import java.util.List;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.brewer.color.BrewerPalette;

/**
 * Distribution Model
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDistributionModel extends IModel {

    /**
     * Returns Type of this Distribution
     * 
     * @return
     */
    public IDistribution< ? > getDistributionType();

    /**
     * Returns List of Distribution Bars
     * 
     * @return
     */
    public List<IDistributionBar> getDistributionBars() throws AWEException;

    /**
     * Returns List of Distribution Bars
     * 
     * @param monitor
     * @return
     */
    public List<IDistributionBar> getDistributionBars(IProgressMonitor monitor) throws AWEException;

    /**
     * Updates info about Distribution Bar in Database
     * 
     * @param bar
     */
    public void updateBar(IDistributionBar bar) throws AWEException;

    /**
     * Set this model as current/not-current distribution model of analyzed model
     * 
     * @param isCurrent is this model a current model
     */
    public void setCurrent(boolean isCurrent) throws AWEException;

    /**
     * Get Color of Bar that is right to Selected
     * 
     * @return
     */
    public Color getRightColor();

    /**
     * Set Color for Bar that is right to Selected
     */
    public void setRightColor(Color rightBarColor);

    /**
     * Get Color of Bar that is left to Selected
     * 
     * @return
     */
    public Color getLeftColor();

    /**
     * Set Color for Bar that is left to Selected
     */
    public void setLeftColor(Color leftBarColor);

    /**
     * Get Color of Selected Bar
     * 
     * @return
     */
    public Color getMiddleColor();

    /**
     * Set Color for Selected Bar
     */
    public void setMiddleColor(Color middleBarColor);

    /**
     * Returns number of Bars in this distribution
     */
    public int getBarCount();

    /**
     * Returns Palette of this Distribution
     * 
     * @return
     */
    public BrewerPalette getPalette();

    /**
     * Set Palette of this Distribution
     * 
     * @param palette
     */
    public void setPalette(BrewerPalette palette);

    /**
     * Returns Analyzed Model
     * 
     * @return
     */
    public IDistributionalModel getAnalyzedModel();

    /**
     * get bar for current Element
     * 
     * @return existed bar or null if not exist
     * @param required element
     */
    public IDistributionBar getBarForAggregatedElement(IDataElement element);
}
