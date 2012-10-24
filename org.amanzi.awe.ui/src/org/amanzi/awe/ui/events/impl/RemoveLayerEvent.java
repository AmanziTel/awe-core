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

package org.amanzi.awe.ui.events.impl;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.impl.internal.AbstractEvent;
import org.amanzi.neo.models.render.IGISModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class RemoveLayerEvent extends AbstractEvent {

    private final IGISModel gisModel;

    public RemoveLayerEvent(final IGISModel model, final Object source) {
        super(EventStatus.REMOVE_GIS, false, source);
        this.gisModel = model;
    }

    public IGISModel getModel() {
        return gisModel;
    }
}
