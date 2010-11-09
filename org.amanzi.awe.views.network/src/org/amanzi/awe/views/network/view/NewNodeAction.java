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

import java.util.HashMap;

import org.amanzi.neo.services.enums.INodeType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Action that called wizard for creating new element of the network
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NewNodeAction extends Action {
    private final INodeType iNodeType;
    private final Node sourceNode;
    protected HashMap<String, Object> defaultProperties = new HashMap<String, Object>();

    public NewNodeAction(INodeType iNodeType, Node sourcedNode) {
        this.iNodeType = iNodeType;
        this.sourceNode = sourcedNode;
        setText(iNodeType.getId());
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
