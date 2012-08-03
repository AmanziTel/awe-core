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

package org.amanzi.awe.views.treeview.command.handlers;

import java.util.Iterator;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
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
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractTreeCommandHandler extends AbstractHandler {
    // TODO: LN: 03.08.2012, incorrect name of non-constant variable
    private final IGeneralNodeProperties GENERAL_NODE_PROPERTIES;

    // TODO: LN: 03.08.2012, remove this constructor
    protected AbstractTreeCommandHandler() {
        this(AWEUIPlugin.getDefault().getGeneralNodeProperties());
    }

    /**
     * @param generalNodeProperties
     */
    protected AbstractTreeCommandHandler(IGeneralNodeProperties generalNodeProperties) {
        GENERAL_NODE_PROPERTIES = generalNodeProperties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if ((selection != null) & (selection instanceof IStructuredSelection)) {
            IStructuredSelection strucSelection = (IStructuredSelection)selection;
            Iterator<Object> elements = strucSelection.iterator();
            while (elements.hasNext()) {
                ITreeItem<IModel> element = (ITreeItem<IModel>)elements.next();
                handleElement(element);
            }
            return null;
        }
        return selection;

    }

    protected abstract void handleElement(ITreeItem<IModel> element);

    /**
     * @return Returns the gENERAL_NODE_PROPERTIES.
     */
    protected IGeneralNodeProperties getGeneralNodeProperites() {
        return GENERAL_NODE_PROPERTIES;
    }

}
