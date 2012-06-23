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

package org.amanzi.neo.models.distribution;

import java.awt.Color;
import java.util.List;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
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
    IDistribution< ? > getDistributionType();

    /**
     * Returns List of Distribution Bars
     * 
     * @return
     */
    List<IDistributionBar> getDistributionBars() throws ModelException;

    /**
     * Returns List of Distribution Bars
     * 
     * @param monitor
     * @return
     */
    List<IDistributionBar> getDistributionBars(IProgressMonitor monitor) throws ModelException;

    /**
     * Updates info about Distribution Bar in Database
     * 
     * @param bar
     */
    void updateBar(IDistributionBar bar) throws ModelException;

    /**
     * Set this model as current/not-current distribution model of analyzed model
     * 
     * @param isCurrent is this model a current model
     */
    void setCurrent(boolean isCurrent) throws ModelException;

    /**
     * Get Color of Bar that is right to Selected
     * 
     * @return
     */
    Color getRightColor();

    /**
     * Set Color for Bar that is right to Selected
     */
    void setRightColor(Color rightBarColor);

    /**
     * Get Color of Bar that is left to Selected
     * 
     * @return
     */
    Color getLeftColor();

    /**
     * Set Color for Bar that is left to Selected
     */
    void setLeftColor(Color leftBarColor);

    /**
     * Get Color of Selected Bar
     * 
     * @return
     */
    Color getMiddleColor();

    /**
     * Set Color for Selected Bar
     */
    void setMiddleColor(Color middleBarColor);

    /**
     * Returns number of Bars in this distribution
     */
    int getBarCount();

    /**
     * Returns Palette of this Distribution
     * 
     * @return
     */
    BrewerPalette getPalette();

    /**
     * Set Palette of this Distribution
     * 
     * @param palette
     */
    void setPalette(BrewerPalette palette);

    /**
     * Returns Analyzed Model
     * 
     * @return
     */
    IDistributionalModel getAnalyzedModel();

    /**
     * get bar for current Element
     * 
     * @return existed bar or null if not exist
     * @param required element
     */
    Color getBarColorForAggregatedElement(IDataElement element);
}
