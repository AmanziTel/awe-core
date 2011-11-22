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

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for creating of selection list
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class NewSelectionListDialog extends AbstractDialog<Integer> {
    
    private static Logger LOGGER = Logger.getLogger(NewSelectionListDialog.class);

    /** The Constant MIN_FIELD_WIDTH. */
    private static final int MIN_FIELD_WIDTH = 50;

    /** The t new node. */
    private Text tNewSelectionList;

    /** The b ok. */
    private Button bOk;

    /** The b cancel. */
    private Button bCancel;

    /** The shell. */
    private Shell shell;
    
    private INetworkModel network;

   /**
     * Instantiates a new new selection list dialog.
     * 
     * @param parent the parent
     * @param title the title
     * @param style the style
     */
    public NewSelectionListDialog(Shell parent,INetworkModel network, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        status = SWT.CANCEL;
        this.network = network;
    }

    /**
     * Creates the contents.
     * 
     * @param shell the shell
     */
    @Override
    protected void createContents(Shell shell) {

        this.shell = shell;

        shell.setLayout(new GridLayout(2, true));

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;

        Label label = new Label(shell, SWT.NONE);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        label.setText("Name of selection list:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        tNewSelectionList = new Text(shell, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        tNewSelectionList.setLayoutData(layoutData);

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
    }

    /**
     * Perform save.
     */
    protected void perfomrSave() {
        LOGGER.debug("Start method performSave");
        try {
            network.createSelectionModel(tNewSelectionList.getText());
        } catch (AWEException e) {
            LOGGER.error(e.getMessage());
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        LOGGER.debug("SelectionList save");
        
    }

    /**
     * Load.
     */
    private void load() {
        // nothing to load
    }    
}
