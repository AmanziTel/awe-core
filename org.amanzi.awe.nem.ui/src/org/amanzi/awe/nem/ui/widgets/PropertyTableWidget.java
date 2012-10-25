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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable.IPropertyTableListener;
import org.amanzi.awe.nem.ui.widgets.PropertyCreationDialog.IPropertyDialogListener;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget.ITableChangedWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

;
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyTableWidget extends AbstractAWEWidget<Composite, ITableChangedWidget>
        implements
            SelectionListener,
            IPropertyTableListener,
            IPropertyDialogListener {

    public interface ITableChangedWidget extends AbstractAWEWidget.IAWEWidgetListener {
        void updateStatus(String message);
    }

    private static final GridLayout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMNS_LAYOUT = new GridLayout(1, false);

    private PropertyTable tableViewer;

    private Button bAdd;

    private Button bRemove;

    private final List<PropertyContainer> propertyContainer;

    private final List<PropertyContainer> requiredProperties;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public PropertyTableWidget(final Composite parent, final ITableChangedWidget listener, final List<PropertyContainer> properties) {
        super(parent, SWT.NONE, listener);
        requiredProperties = new ArrayList<PropertyContainer>(properties);
        this.propertyContainer = properties;
    }

    private Button createButton(final Composite buttonComposite, final String name) {
        final Button button = new Button(buttonComposite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        button.setText(name);
        button.addSelectionListener(this);
        return button;
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        final Composite widgetComposite = new Composite(parent, SWT.NONE);
        widgetComposite.setLayout(TWO_COLUMNS_LAYOUT);
        widgetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Composite tableComposite = new Composite(widgetComposite, SWT.NONE);
        tableComposite.setLayout(ONE_COLUMNS_LAYOUT);
        tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        tableViewer = new PropertyTable(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, propertyContainer, this);

        tableViewer.getTable().addSelectionListener(this);
        tableViewer.initialize();

        final Composite buttonComposite = new Composite(widgetComposite, SWT.NONE);
        buttonComposite.setLayout(ONE_COLUMNS_LAYOUT);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        bAdd = createButton(buttonComposite, NEMMessages.ADD);
        bRemove = createButton(buttonComposite, NEMMessages.REMOVE);
        bRemove.setEnabled(false);
        return widgetComposite;
    }

    /**
     * @return
     */
    public List<PropertyContainer> getProperties() {
        return propertyContainer;
    }

    @Override
    public void onNewItemCreated(final PropertyContainer container) {
        if (propertyContainer.contains(container)) {
            MessageDialog.openWarning(tableViewer.getControl().getShell(), NEMMessages.PROPERTY_DUPLICATED_TITLE,
                    NEMMessages.PROPERTY_DUPLICATED_MESSAGE);
        } else {
            tableViewer.add(container);
            onUpdate(null);
        }

    }

    @Override
    public void onUpdate(final String message) {
        for (final ITableChangedWidget listener : getListeners()) {
            listener.updateStatus(message);
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(bAdd)) {
            final PropertyCreationDialog dialog = new PropertyCreationDialog(getWidget().getShell(), this);
            dialog.open();
        } else if (e.getSource().equals(bRemove)) {
            final IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
            final PropertyContainer container = (PropertyContainer)selection.getFirstElement();
            tableViewer.remove(container);
        } else if (e.getSource().equals(tableViewer.getTable())) {
            final IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
            final PropertyContainer container = (PropertyContainer)selection.getFirstElement();
            if (requiredProperties.contains(container)) {
                bRemove.setEnabled(false);
            } else {
                bRemove.setEnabled(true);
            }
        }

    }

}
