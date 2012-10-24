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

package org.amanzi.awe.catalog.neo.selection;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MapSelection implements IMapSelection {

    private final IRenderableModel model;

    private final Iterable<IDataElement> elements;

    private final Iterable<ILocationElement> locations;

    /**
     * 
     */
    public MapSelection(final IRenderableModel model, final Iterable<IDataElement> selectedElements,
            final Iterable<ILocationElement> selectedLocations) {
        this.model = model;
        this.elements = selectedElements;
        this.locations = selectedLocations;
    }

    @Override
    public IRenderableModel getModel() {
        return model;
    }

    @Override
    public Iterable<IDataElement> getSelectedElements() {
        return elements;
    }

    @Override
    public Iterable<ILocationElement> getSelectedLocations() {
        return locations;
    }

}
