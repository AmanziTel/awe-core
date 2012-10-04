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

package org.amanzi.awe.views.charts.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.amanzi.awe.views.charts.widget.FilteringDialog.IDialogSelectorListener;
import org.amanzi.awe.views.charts.widget.ItemsSelectorWidget.ItemSelectedListener;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Lists;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ItemsSelectorWidget extends AbstractLabeledWidget<Composite, ItemSelectedListener>
implements
SelectionListener,
IDialogSelectorListener {

    private static final String ITEM_SEPARATOR = ";";

    private Text field;

    private Button editButton;

    private List<String> allItems = new ArrayList<String>();

    private List<String> selectedItems = new ArrayList<String>();

    public interface ItemSelectedListener extends org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener {
        void onItemSelected();

    }

    public ItemsSelectorWidget(final Composite parent, final ItemSelectedListener listener, final String label) {
        super(parent, listener, label);
    }

    public void setItems(final Collection<String> items) {
        if ((this.allItems == null) || !this.allItems.containsAll(items)) {
            this.allItems = Lists.newArrayList(items);
            changeSelected(allItems);
        }

    }

    /**
     * @param items
     */
    private void changeSelected(final List<String> items) {
        selectedItems = Lists.newArrayList(items);
        field.setText(prepareStringFromCollection(selectedItems));
    }

    /**
     * @param items
     */
    private String prepareStringFromCollection(final Collection<String> items) {
        String text = StringUtils.EMPTY;
        for (String item : items) {
            text += item + ITEM_SEPARATOR;
        }
        return text;
    }

    @Override
    protected Composite createControl(final Composite parent) {
        parent.setLayoutData(getGridData(SWT.FILL));

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));

        field = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        field.setVisible(true);
        field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        editButton = new Button(composite, SWT.NONE);
        editButton.setText("...");
        editButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        editButton.addSelectionListener(this);
        return composite;
    }

    private GridData getGridData(final int fillOrLeft) {
        return new GridData(fillOrLeft, SWT.CENTER, true, false);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(editButton)) {
            FilteringDialog dialog = new FilteringDialog(getWidget(), SWT.NONE, this);
            dialog.initializeWidget();
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fireSelection(final List<String> selections) {
        changeSelected(selections);
        for (ItemSelectedListener listener : getListeners()) {
            listener.onItemSelected();
        }

    }

    @Override
    public List<String> getSelected() {
        return selectedItems;
    }

    @Override
    public List<String> getAllItems() {
        return allItems;
    }
}
