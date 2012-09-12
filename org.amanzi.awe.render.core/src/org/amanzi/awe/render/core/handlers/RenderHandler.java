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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.amanzi.awe.ui.events.impl.ShowGISOnMap;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.manager.EventChain;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class RenderHandler extends AbstractHandler {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();

            boolean isModel = false;

            Set<IDataElement> elements = new HashSet<IDataElement>();
            IRenderableModel model = null;

            while (selectionIterator.hasNext()) {
                Object selectedObject = selectionIterator.next();
                if (selectedObject instanceof ITreeItem) {
                    ITreeItem<?> treeItem = (ITreeItem<?>)selectedObject;

                    if (treeItem.getDataElement() instanceof IRenderableModel) {
                        model = (IRenderableModel)treeItem.getDataElement();
                        isModel = true;
                        break;
                    } else if (treeItem.getDataElement() instanceof IDataElement) {
                        IDataElement element = treeItem.getDataElement();

                        elements.add(element);

                        model = model == null ? (IRenderableModel)treeItem.getParent() : model;
                    }
                }
            }

            if (isModel) {
                showOnMap(model);
            } else {
                showOnMap(model, elements);
            }
        }

        return null;
    }

    private void showOnMap(final IRenderableModel model, final Set<IDataElement> elements) {

    }

    private void showOnMap(final IRenderableModel model) {
        EventChain eventChain = new EventChain(true);

        for (IGISModel gis : model.getAllGIS()) {
            eventChain.addEvent(new ShowGISOnMap(gis));
        }

        AWEEventManager.getManager().fireEventChain(eventChain);
    }

}
