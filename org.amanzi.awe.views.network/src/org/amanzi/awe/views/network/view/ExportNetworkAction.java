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
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Export network sector
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportNetworkAction extends Action {

    @Override
	public void run() {
        // TODO init
        IWorkbench workbench = null;

        Shell shell = Display.getDefault().getActiveShell();
        ExportNetworkWizard wizard = new ExportNetworkWizard();
        wizard.init(workbench, selection);
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.open();
    }


    /** The root. */
    Node root = null;
    private final IStructuredSelection selection;

    /**
     * Instantiates a new export network action.
     *
     * @param selection the selection
     */
    public ExportNetworkAction(IStructuredSelection selection) {
        this.selection = selection;
        setText("Export network sector data in csv file");
        if (selection.size() == 1) {
            Object elem = selection.getFirstElement();
            if (elem instanceof NeoNode) {
                Node node = ((NeoNode)elem).getNode();
                if (NodeTypes.NETWORK.checkNode(node)) {
                    root = node;
                }
            }
        }
        setEnabled(root != null);
    }

}
