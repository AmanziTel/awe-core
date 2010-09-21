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

import java.util.LinkedHashMap;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class NewTypeDialog extends AbstractDialog<Integer> {

    private static final int MIN_FIELD_WIDTH = 50;
    private Node node;
    private Text tNewNode;
    private final LinkedHashMap<String, Node> gisNetworkNodes = new LinkedHashMap<String, Node>();
    private Button bOk;
    private Button bCancel;
    private Shell shell;
    private Label errorLabel;
    private final DatasetService ds;

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
        ds = NeoServiceFactory.getInstance().getDatasetService();
    }

    @Override
    protected void createContents(Shell shell) {
        this.shell = shell;

        shell.setImage(NodeTypes.DATASET.getImage());
        shell.setLayout(new GridLayout(2, true));

        // errorLabel = new Label(shell, SWT.FLAT);
        // errorLabel.setText("no_errors");
        // errorLabel.setForeground(new org.eclipse.swt.graphics.Color(shell.getDisplay(),255,0,0));
        // errorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,2,1));
        // errorLabel.setVisible(false);

        Label label = new Label(shell, SWT.NONE);
        // label.setText("Target network");
        // label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        // cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        // cNetwork.setLayoutData(layoutData);
        //
        // label = new Label(shell, SWT.NONE);
        // label.setText("Parent node");
        // label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        // cNodeTypes = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        // layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // layoutData.minimumWidth = MIN_FIELD_WIDTH;
        // cNodeTypes.setLayoutData(layoutData);

        // label = new Label(shell, SWT.NONE);
        label.setText("New node type");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        tNewNode = new Text(shell, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        tNewNode.setLayoutData(layoutData);

        bOk = new Button(shell, SWT.PUSH);
        bOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bOk.setText("OK");
        bCancel = new Button(shell, SWT.PUSH);
        bCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bCancel.setText("Cancel");
        addListeners();
        load();
    }

    private void addListeners() {
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                perfomSave();
                shell.close();
            }
        });
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }
        });
        tNewNode.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                checkTypeName();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

    /**
     *
     */
    protected void checkTypeName() {
        String newType = tNewNode.getText().toLowerCase().trim();
        if (ds.getNodeType(newType) != null) {
            bOk.setEnabled(false);
        } else {
            bOk.setEnabled(true);
        }
    }

    /**
     *
     */
    protected void perfomSave() {
        ds.saveDynamicNodeType(tNewNode.getText());
        NeoServiceProvider.getProvider().commit();
    }

    private void load() {

    }

}
