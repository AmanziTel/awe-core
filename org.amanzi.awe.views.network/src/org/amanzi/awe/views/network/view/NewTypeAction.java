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

package org.amanzi.awe.views.network.view;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ViewPluginAction;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NewTypeAction extends Action implements IViewActionDelegate{

    @Override
    public void run(IAction action) {

        ViewPluginAction act = (ViewPluginAction)action;
        TreeSelection selection = (TreeSelection)act.getSelection();
        NeoNode selectedNode = (NeoNode)selection.getFirstElement();
        Node parent = NeoUtils.getParent(NeoServiceProvider.getProvider().getService(), selectedNode.getNode());
        String[] structure = (String[])parent.getProperty(org.amanzi.neo.core.INeoConstants.PROPERTY_STRUCTURE_NAME, new String[] {});

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        NewTypeDialog pdialog = new NewTypeDialog(shell, "title", parent, SWT.OK);;
        if (pdialog.open() == SWT.OK) {
            // formPropertyList();
            // String[] result = propertyLists.keySet().toArray(new String[0]);
            // Arrays.sort(result);
            // cPropertyList.setItems(result);
            // updatePropertyList();
        }

        System.out.println(structure);
        // System.out.println("org.amanzi.awe.views.network.view.NewTypeAction 2\n");
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void init(IViewPart view) {
    }

}
