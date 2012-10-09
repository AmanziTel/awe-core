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

import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable;
import org.amanzi.awe.nem.ui.properties.table.PropertyTable.IPropertyTableListener;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget.ITableChangedWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
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
            IPropertyTableListener {
    private static final GridLayout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMNS_LAYOUT = new GridLayout(1, false);

    private PropertyTable tableViewer;

    private Object bAdd;

    private Object bRemove;

    private String type;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public PropertyTableWidget(Composite parent, ITableChangedWidget listener, String type) {
        super(parent, SWT.NONE, listener);
        this.type = type;
    }

    public interface ITableChangedWidget extends IAWEWidgetListener {

    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite widgetComposite = new Composite(parent, SWT.NONE);
        widgetComposite.setLayout(TWO_COLUMNS_LAYOUT);
        widgetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite tableComposite = new Composite(widgetComposite, SWT.NONE);
        tableComposite.setLayout(ONE_COLUMNS_LAYOUT);
        tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        tableViewer = new PropertyTable(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, type);

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

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource().equals(bAdd)) {
            // TODO KV: handle action
        } else if (e.getSource().equals(bRemove)) {
            // TODO KV: handle action;
        }

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
