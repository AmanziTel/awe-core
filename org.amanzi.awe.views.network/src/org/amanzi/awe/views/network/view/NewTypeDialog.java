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

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class NewTypeDialog extends AbstractDialog<Integer> {

    private Node node;
    private GraphDatabaseService service;

    // public NewTypeDialog(Shell parent, String title, int style) {
    // super(parent, "Dataset properties configura\tion", SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL |
    // SWT.CENTER);
    // // this.dataset = dataset;
    // status = SWT.CANCEL;
    // service = NeoServiceProvider.getProvider().getService();
    // }

    public NewTypeDialog(Shell parent, String title, Node node, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.node = node;
        status = SWT.CANCEL;
        service = NeoServiceProvider.getProvider().getService();
    }

    @Override
    protected void createContents(Shell shell) {
        shell.setImage(NodeTypes.DATASET.getImage());
        shell.setLayout(new GridLayout(1, false));
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 300;
        data.widthHint = 200;
        shell.setLayoutData(data);
        Text text = new Text(shell, SWT.BORDER);
        // text.
        // text.setLayoutData(data);

    }

}
