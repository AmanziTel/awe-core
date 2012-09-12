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

import java.util.Set;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.impl.internal.AbstractEvent;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ShowElementsOnMap extends AbstractEvent {

    private final IRenderableModel model;

    private final Set<IDataElement> elements;

    private final ReferencedEnvelope bounds;

    /**
     * @param status
     * @param isAsync
     */
    public ShowElementsOnMap(final IRenderableModel model, final Set<IDataElement> elements, final ReferencedEnvelope bounds) {
        super(EventStatus.SHOW_ELEMENTS, true);

        this.model = model;
        this.elements = elements;
        this.bounds = bounds;
    }

    public ShowElementsOnMap(final IRenderableModel model, final Set<IDataElement> elements) {
        this(model, elements, null);
    }

    public IRenderableModel getModel() {
        return model;
    }

    public Set<IDataElement> getElements() {
        return elements;
    }

    public ReferencedEnvelope getBounds() {
        return bounds;
    }

}
