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

package org.amanzi.awe.wizards.geoptima;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class ProcessDialog extends Dialog implements IPropertyChangeListener {

    private String btnLabel;
    private int status;
    private String title;
    private Shell shell;
    private Combo cData;
    private Combo cCorrelate;
    /**
     * @param parent
     */
    public ProcessDialog(Shell parent, String title, String processBtnLabel) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.title = title;
        btnLabel = processBtnLabel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    }

    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText(getTitle());

        createContents(shell);
        shell.pack();

        // calculate location
        Point size = parentShell.getSize();
        int dlgWidth = shell.getSize().x;
        int dlgHeight = shell.getSize().y;
        shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
        NeoLoaderPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
        beforeOpen();
        shell.open();
        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        NeoLoaderPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
        return status;
    }

    /**
     * @return
     */
    private String getTitle() {
        return title;
    }

    private void beforeOpen() {
        formData();
    }

    protected void formData() {
    }

    private void createContents(final Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, false));
        Label label = new Label(shell, SWT.NONE);
        label.setText("Select data:");
        cData = new Combo(shell, SWT.FILL | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.widthHint = 200;
        layoutData.grabExcessHorizontalSpace = true;
        cData.setLayoutData(layoutData);
        label = new Label(shell, SWT.NONE);
        label.setText("Correlate data:");
        cCorrelate = new Combo(shell, SWT.FILL | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.widthHint = 200;
        cCorrelate.setLayoutData(layoutData);
        Button bCorrelate = new Button(shell, SWT.PUSH);
        bCorrelate.setText(getProcessButtonLabel());
        bCorrelate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                processBtn();
            }

        });

        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("OK");
        GridData gdBtnOk = new GridData();
        gdBtnOk.horizontalAlignment = GridData.END;
        gdBtnOk.widthHint = 70;
        btnOk.setLayoutData(gdBtnOk);
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                shell.close();
            }

        });

    }

    /**
     *
     */
    protected void processBtn() {
    }

    /**
     * @return
     */
    public String getProcessButtonLabel() {
        return btnLabel;
    }

    public void setProcessButtonLabel(String label) {
        btnLabel = label;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
