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

package org.amanzi.awe.render.core.handlers;

import org.amanzi.awe.render.core.utils.RenderMenuUtils;
import org.amanzi.awe.ui.events.impl.ShowGISOnMap;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.manager.EventChain;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class RenderHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if (selection instanceof IStructuredSelection) {
            final Pair<IRenderableModel, Iterable<IDataElement>> mapSelection = RenderMenuUtils
                    .getLocationElements((IStructuredSelection)selection);

            if (mapSelection != null) {
                if (mapSelection.getRight() == null) {
                    showModelOnMap(mapSelection.getLeft());
                } else {
                    showElementsOnMap(mapSelection.getLeft(), mapSelection.getRight());
                }
            }
        }

        return null;
    }

    private void showModelOnMap(final IRenderableModel model) {
        final EventChain chain = new EventChain(true);

        for (final IGISModel gis : model.getAllGIS()) {
            chain.addEvent(new ShowGISOnMap(gis, this));
        }

        AWEEventManager.getManager().fireEventChain(chain);
    }

    private void showElementsOnMap(final IRenderableModel model, final Iterable<IDataElement> elements) {
        AWEEventManager.getManager().fireShowOnMapEvent(model, elements, this);
    }
}
