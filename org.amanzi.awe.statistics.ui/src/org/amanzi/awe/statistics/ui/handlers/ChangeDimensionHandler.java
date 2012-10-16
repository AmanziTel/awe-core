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
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ChangeDimensionHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String dimensionType = event.getParameter("org.amanzi.statistics.dimension");

        if (!StringUtils.isEmpty(dimensionType)) {
            if (dimensionType.equals("time")) {
                DimensionHandler.getInstance().setDimension(DimensionType.TIME);
            } else if (dimensionType.equals("property")) {
                DimensionHandler.getInstance().setDimension(DimensionType.PROPERTY);
            }

            AWEEventManager.getManager().fireDataUpdatedEvent(this);
        }

        return null;
    }

}
