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

package org.amanzi.awe.nem.ui.widgets;

import java.util.List;

import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.TypesCreationDialog.ITypesCreationDialogListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.amanzi.neo.models.network.NetworkElementType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TypesCreationDialog extends AbstractAWEWidget<Shell, ITypesCreationDialogListener> implements SelectionListener {

    public interface ITypesCreationDialogListener extends IAWEWidgetListener {
        void onNewTypeAdded(String string);
    }

    private static final Layout TWO_COLUMN_LAYOUT = new GridLayout(2, false);
    private static final Point SHELL_SIZE = new Point(300, 100);
    private Shell shell;
    private Combo cNodeTypesCombo;
    private Button bOk;
    private Button bCancel;
    private List<String> alreadySelectedTypes;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public TypesCreationDialog(Composite parent, int style, ITypesCreationDialogListener listener, List<String> alreadySelected) {
        super(parent, style, listener);
        this.alreadySelectedTypes = alreadySelected;
    }

    @Override
    protected Shell createWidget(Composite parent, int style) {
        shell = new Shell(parent.getShell(), SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setLayout(TWO_COLUMN_LAYOUT);
        shell.setText(NEMMessages.CREATE_TYPE_IN_STRUCTURE);
        shell.setSize(SHELL_SIZE);

        Label label = new Label(shell, SWT.NONE);
        label.setText(NEMMessages.TYPE);
        label.setLayoutData(getGridData());

        cNodeTypesCombo = new Combo(shell, SWT.BORDER);
        cNodeTypesCombo.setLayoutData(getGridData());
        fillCombo();
        bOk = createButton(NEMMessages.OK);
        bCancel = createButton(NEMMessages.CANCEL);
        shell.open();
        return shell;
    }

    /**
     *
     */
    private void fillCombo() {
        boolean isContainSite = alreadySelectedTypes.contains(NetworkElementType.SITE.getId());
        for (NetworkElementType type : NetworkElementType.values()) {
            if (!alreadySelectedTypes.contains(type.getId())) {
                if (type == NetworkElementType.SECTOR && !isContainSite) {
                    return;
                } else if (type == NetworkElementType.SECTOR && isContainSite) {
                    cNodeTypesCombo.add(type.getId());
                    return;
                }
                cNodeTypesCombo.add(type.getId());
            } else if (alreadySelectedTypes.contains(type.getId()) && type == NetworkElementType.SECTOR) {
                return;
            }
        }

    }

    /**
     * @return
     */
    private Object getGridData() {
        return new GridData(GridData.FILL_HORIZONTAL);
    }

    /**
     * @param oK
     * @return
     */
    private Button createButton(String name) {
        Button button = new Button(shell, SWT.PUSH);
        button.setText(name);
        button.setLayoutData(getGridData());
        button.addSelectionListener(this);
        return button;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource().equals(bCancel)) {
            shell.close();
        } else if (e.getSource().equals(bOk)) {
            for (ITypesCreationDialogListener listener : getListeners()) {
                String newType = cNodeTypesCombo.getText();
                if (alreadySelectedTypes.contains(newType)) {
                    MessageDialog.openWarning(shell, NEMMessages.TYPES_DIALOG_WARNING_TITLE,
                            NEMMessages.TYPES_DIALOG_WARNING_MESSAGE);
                } else {
                    listener.onNewTypeAdded(newType);
                }
            }
            shell.close();
        }

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
