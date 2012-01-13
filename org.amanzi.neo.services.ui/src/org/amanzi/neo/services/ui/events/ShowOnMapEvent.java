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

import java.util.List;

import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * SHOW_ON_MAP event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowOnMapEvent extends AbstractEvent {
    /**
     * list of loaded models;
     */
    private List<IDataModel> modelsList;
    /**
     * zoom degree
     */
    private double zoom;

    /**
     * initialize all necessary parameters
     * 
     * @param modelsList
     * @param zoom
     */
    public ShowOnMapEvent(List<IDataModel> modelsList, double zoom) {
        this.modelsList = modelsList;
        this.zoom = zoom;
        type = EventsType.SHOW_ON_MAP;
    }

    public List<IDataModel> getModelsList() {
        return modelsList;
    }

    public double getZoom() {
        return zoom;
    }

}
