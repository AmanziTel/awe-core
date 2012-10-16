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

package org.amanzi.awe.statistics.ui.handlers;

import org.amanzi.awe.statistics.model.DimensionType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class DimensionHandler {

    private static final class DimensionInstanceHandler {
        private static volatile DimensionHandler instance = new DimensionHandler();
    }

    private DimensionType dimensionType = DimensionType.TIME;

    /**
     * 
     */
    private DimensionHandler() {
    }

    public static synchronized DimensionHandler getInstance() {
        return DimensionInstanceHandler.instance;
    }

    public synchronized void setDimension(DimensionType type) {
        assert type != null;

        this.dimensionType = type;
    }

    public synchronized DimensionType getDimension() {
        return dimensionType;
    }

}
