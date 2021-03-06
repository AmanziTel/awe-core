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

package org.amanzi.awe.charts.builder.dataset.dto;

import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.data.general.Dataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public interface IChartDatasetContainer {
    /**
     * compute dataset
     * 
     * @throws ModelException
     */
    void computeDatasets() throws ModelException;

    /**
     * get dataset for axis
     */
    Dataset getDataset(IRangeAxis rangeAxis);

    /**
     * check if currrent container is has more than one axis
     * 
     * @return
     */
    boolean isMultyAxis();
}
