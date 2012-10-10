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

package org.amanzi.awe.nem.ui.handlers;

import java.util.Iterator;

import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
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
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractNemHandler extends AbstractHandler {

    private static final MenuUtils MENU_UTILS = MenuUtils.getInstance();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if (selection instanceof IStructuredSelection) {
            Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();
            try {
                while (selectionIterator.hasNext()) {
                    Object selectedObject = selectionIterator.next();
                    if (selectedObject instanceof ITreeItem) {
                        handleItem((ITreeItem)selectedObject, event.getCommand());
                    }
                }
            } catch (Exception e) {
                throw new ExecutionException("can't execute action ", e);
            }
        }
        return null;
    }

    /**
     * @param selectedObject
     * @param command
     */
    protected abstract void handleItem(ITreeItem<IModel, Object> selectedObject, Command command);

    /**
     * @return Returns the menuUtils.
     */
    protected MenuUtils getMenuUtils() {
        return MENU_UTILS;
    }
}
