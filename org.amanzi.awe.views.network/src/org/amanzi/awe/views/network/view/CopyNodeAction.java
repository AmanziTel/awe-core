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
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Action for copying nodes
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class CopyNodeAction extends Action {

    private final INodeType iNodeType;
    private final Node sourceNode;

    public CopyNodeAction(INodeType iNodeType, Node sourcedNode) {
        this.iNodeType = iNodeType;
        this.sourceNode = sourcedNode;
        setText("Copy " + iNodeType.getId());
        setEnabled(true);
    }

    /**
     * @param selection
     */
    public CopyNodeAction(IStructuredSelection selection) {
        Object element = selection.getFirstElement();
        if (!(element instanceof Root) && element instanceof NeoNode) {
            sourceNode = ((NeoNode)element).getNode();
            iNodeType = NeoServiceFactory.getInstance().getDatasetService().getNodeType(sourceNode);
            setText("Copy " + iNodeType.getId());
            setEnabled(true);
        } else {
            sourceNode = null;
            iNodeType = null;
            setEnabled(false);
        }
    }

    @Override
    public void run() {
        INewWizard wizard = new CreateNewNodeWizard(iNodeType, sourceNode);
        wizard.init(null, null);
        Shell parent = Display.getDefault().getActiveShell();
        WizardDialog dialog = new WizardDialog(parent, wizard);
        dialog.create();
        dialog.open();
    }

}