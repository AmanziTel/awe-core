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

/**
 * <p>
 * Dialog for creating new (user defined) node types
 * </p>
 * .
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class NewTypeDialog extends AbstractDialog<Integer> {

    /** The Constant MIN_FIELD_WIDTH. */
    private static final int MIN_FIELD_WIDTH = 50;

    /** The t new node. */
    private Text tNewNode;

    /** The b ok. */
    private Button bOk;

    /** The b cancel. */
    private Button bCancel;

    /** The shell. */
    private Shell shell;

    /** The ds. */
    private final DatasetService ds;

    /**
     * Instantiates a new new type dialog.
     * 
     * @param parent the parent
     * @param title the title
     * @param style the style
     */
    public NewTypeDialog(Shell parent, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        status = SWT.CANCEL;
        ds = NeoServiceFactory.getInstance().getDatasetService();
    }

    /**
     * Creates the contents.
     * 
     * @param shell the shell
     */
    @Override
    protected void createContents(Shell shell) {
        this.shell = shell;

        shell.setImage(NodeTypes.DATASET.getImage());
        shell.setLayout(new GridLayout(2, true));

        Label label = new Label(shell, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;

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

    /**
     * Adds the listeners.
     */
    private void addListeners() {
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                perfomrSave();
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
     * Check type name.
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
     * Perform save.
     */
    protected void perfomrSave() {
        ds.saveDynamicNodeType(tNewNode.getText());
        NeoServiceProvider.getProvider().commit();
    }

    /**
     * Load.
     */
    private void load() {
        // nothing to load
    }

}
