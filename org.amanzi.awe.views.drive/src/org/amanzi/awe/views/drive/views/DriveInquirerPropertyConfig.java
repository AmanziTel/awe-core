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

package org.amanzi.awe.views.drive.views;

import org.amanzi.neo.core.utils.AbstractDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DriveInquirerPropertyConfig extends AbstractDialog<Integer> {
    private final Node dataset;
    private Shell shell;
    private CheckboxTableViewer propertyListTable;
    private CheckboxTableViewer propertySlipTable;

    /**
     * @param parent
     * @param title
     */
    public DriveInquirerPropertyConfig(Shell parent, Node dataset) {
        super(parent, "Dataset properties configuration", SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.dataset = dataset;
        status = SWT.CANCEL;
    }

    @Override
    protected void createContents(Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, false));
        // Label label = new Label(shell, SWT.NONE);
        // label.setText("Network:");
        // cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        // GridData layoutData = new GridData();
        // layoutData.grabExcessHorizontalSpace = true;
        // layoutData.minimumWidth = 200;
        // cNetwork.setLayoutData(layoutData);
        propertyListTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        data.heightHint = 200;
        data.widthHint = 200;
        propertyListTable.getControl().setLayoutData(data);
        // contentProvider = new NeoTableContentProvider();

    }

    // private class NeoTableContentProvider implements IStructuredContentProvider {
    // LinkedHashSet<TableElem> elements = new LinkedHashSet<TableElem>();
    //
    // @Override
    // public Object[] getElements(Object inputElement) {
    // return elements.toArray(new TableElem[0]);
    // }
    //
    // @Override
    // public void dispose() {
    // }
    //
    // @Override
    // public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // if (newInput == null) {
    // elements.clear();
    // } else {
    // Transaction tx = service.beginTx();
    // try {
    // String nodeSet = (String)newInput;
    // StringTokenizer st = new StringTokenizer(nodeSet, DataLoadPreferences.CRS_DELIMETERS);
    // while (st.hasMoreTokens()) {
    // String nodeId = st.nextToken();
    // Node node = service.getNodeById(Long.parseLong(nodeId));
    // if (!NodeTypes.NETWORK.checkNode(node)) {
    // elements.add(new TableElem(node, service));
    // }
    // }
    // } finally {
    // tx.finish();
    // }
    // }
    // }
    //
    // }

}
