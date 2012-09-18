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
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ShowGISOnMap extends AbstractEvent {

    private static final int DEFAULT_ZOOM = 900;

    private final int zoom;

    private final IGISModel model;

    /**
     * @param status
     */
    public ShowGISOnMap(final IGISModel model, Object source) {
        this(model, DEFAULT_ZOOM, source);
    }

    public ShowGISOnMap(final IGISModel model, final int zoom, Object source) {
        super(EventStatus.SHOW_GIS, true, source);
        this.zoom = zoom;
        this.model = model;
    }

    /**
     * @return Returns the zoom.
     */
    public int getZoom() {
        return zoom;
    }

    /**
     * @return Returns the model.
     */
    public IGISModel getModel() {
        return model;
    }

}
