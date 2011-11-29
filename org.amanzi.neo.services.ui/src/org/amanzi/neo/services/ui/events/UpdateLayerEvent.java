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

package org.amanzi.neo.services.ui.events;

import org.amanzi.neo.services.model.IDataModel;

/**
 * <p>
 * UPDATE_LAYER event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class UpdateLayerEvent extends AbstractEvent {
    /**
     * selected model;
     */
    private IDataModel model;

    /**
     * initialize event with required parameters
     * 
     * @param model
     */
    public UpdateLayerEvent(IDataModel model) {
        this.model = model;
        type = EventsType.UPDATE_LAYER;
    }

    public IDataModel getModel() {
        return model;
    }

}
