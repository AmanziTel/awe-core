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

import org.amanzi.awe.nem.properties.manager.NetworkProperty;
import org.amanzi.awe.nem.properties.manager.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable.IPropertyTableListener;
import org.amanzi.awe.nem.ui.widgets.PropertyCreationDialog.IPropertyDialogListener;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget.ITableChangedWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
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
    private static final GridLayout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMNS_LAYOUT = new GridLayout(1, false);

    private PropertyTable tableViewer;

    private Object bAdd;

    private Object bRemove;

    private Iterable<NetworkProperty> properties;

    private List<PropertyContainer> propertyContainer;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public PropertyTableWidget(Composite parent, ITableChangedWidget listener, String type, Iterable<NetworkProperty> properties) {
        super(parent, SWT.NONE, listener);
        this.properties = properties;
    }

    public interface ITableChangedWidget extends IAWEWidgetListener {
        void updateStatus(String message);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite widgetComposite = new Composite(parent, SWT.NONE);
        widgetComposite.setLayout(TWO_COLUMNS_LAYOUT);
        widgetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite tableComposite = new Composite(widgetComposite, SWT.NONE);
        tableComposite.setLayout(ONE_COLUMNS_LAYOUT);
        tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        preparePropertyContainer();

        tableViewer = new PropertyTable(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, propertyContainer, this);

        tableViewer.initialize();

        Composite buttonComposite = new Composite(widgetComposite, SWT.NONE);
        buttonComposite.setLayout(ONE_COLUMNS_LAYOUT);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        bAdd = createButton(buttonComposite, NemMessages.ADD);
        bRemove = createButton(buttonComposite, NemMessages.REMOVE);

        return widgetComposite;
    }

    private Button createButton(Composite buttonComposite, String name) {
        Button button = new Button(buttonComposite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        button.setText(name);
        button.addSelectionListener(this);
        return button;
    }

    private List<PropertyContainer> preparePropertyContainer() {
        propertyContainer = new ArrayList<PropertyContainer>();
        for (NetworkProperty property : properties) {
            propertyContainer.add(new PropertyContainer(property));
        }
        return propertyContainer;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource().equals(bAdd)) {
            PropertyCreationDialog dialog = new PropertyCreationDialog(getWidget().getShell(), this);
            dialog.open();
        } else if (e.getSource().equals(bRemove)) {
            IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
            PropertyContainer container = (PropertyContainer)selection.getFirstElement();
            tableViewer.remove(container);
        }

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(String message) {
        for (ITableChangedWidget listener : getListeners()) {
            listener.updateStatus(message);
        }
    }

    /**
     * @return
     */
    public List<PropertyContainer> getProperties() {
        return propertyContainer;
    }

    @Override
    public void onNewItemCreated(PropertyContainer container) {
        tableViewer.add(container);

    }
}
