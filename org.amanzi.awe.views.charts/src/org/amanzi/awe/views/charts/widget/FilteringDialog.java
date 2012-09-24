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

import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.views.charts.widget.FilteringDialog.IDialogSelectorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

;
/**
 * widget for filtering data of {@link ItemsSelectorWidget}
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FilteringDialog extends AbstractAWEWidget<Shell, IDialogSelectorListener> implements SelectionListener {

    private static final GridLayout THREE_COLUMNS_LAYOUT = new GridLayout(3, false);

    private static final Layout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final Layout ONE_COLUMNS_LAYOUT = new GridLayout();

    private static final String ADD_ALL_LABEL = ">>";

    private static final String ADD_ONE = ">";

    private static final String REMOVE_ALL = "<<";

    private static final String REMOVE_ONE = "<";

    private static final String OK_BTN = "Ok";

    private static final String CANCEL_BTN = "Cancel";

    private static final Point SHELL_SIZE = new Point(400, 400);

    private static final int MIN_LISTS_WIDTH = 175;

    public interface IDialogSelectorListener extends org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener {
        void fireSelection(List<String> selections);

        List<String> getSelected();

        List<String> getAllItems();
    }

    private final IDialogSelectorListener listener;

    private final List<String> selectedItems;

    private final List<String> allItems;

    private Shell shell;

    private org.eclipse.swt.widgets.List notInListItems;

    private org.eclipse.swt.widgets.List inListItems;

    private Button bAddAll;

    private Button bAddOne;

    private Button bRemoveOne;

    private Button bRemoveAll;

    private Button bCancel;

    private Button bOk;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public FilteringDialog(final Composite parent, final int style, final IDialogSelectorListener listener) {
        super(parent, style, listener);
        this.listener = listener;
        this.selectedItems = listener.getSelected();
        this.allItems = listener.getAllItems();
    }

    @Override
    protected Shell createWidget(final Composite parent, final int style) {
        shell = new Shell(parent.getShell(), SWT.SHELL_TRIM);
        shell.setLayout(ONE_COLUMNS_LAYOUT);
        shell.setSize(SHELL_SIZE);

        Composite filtersComposite = new Composite(shell, SWT.FILL);
        filtersComposite.setLayout(THREE_COLUMNS_LAYOUT);
        filtersComposite.setLayoutData(getGridData());

        notInListItems = new org.eclipse.swt.widgets.List(filtersComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = getGridData();
        data.widthHint = MIN_LISTS_WIDTH;
        notInListItems.setLayoutData(data);

        createButtonComposite(filtersComposite);

        inListItems = new org.eclipse.swt.widgets.List(filtersComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        data = getGridData();
        data.widthHint = MIN_LISTS_WIDTH;
        inListItems.setLayoutData(data);

        Composite confirmComposite = new Composite(shell, SWT.NONE);
        confirmComposite.setLayout(TWO_COLUMNS_LAYOUT);
        bOk = createButton(confirmComposite, OK_BTN);
        bCancel = createButton(confirmComposite, CANCEL_BTN);

        initLists();
        shell.open();

        return shell;
    }

    /**
     * default lists initialisation
     */
    private void initLists() {
        for (String item : allItems) {
            if (selectedItems.contains(item)) {
                inListItems.add(item);
            } else {
                notInListItems.add(item);
            }
        }
    }

    /**
     * @param filtersComposite
     */
    private void createButtonComposite(final Composite filtersComposite) {
        Composite buttonsComposite = new Composite(filtersComposite, SWT.NONE);
        buttonsComposite.setLayout(ONE_COLUMNS_LAYOUT);
        buttonsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        bAddAll = createButton(buttonsComposite, ADD_ALL_LABEL);
        bAddOne = createButton(buttonsComposite, ADD_ONE);
        bRemoveOne = createButton(buttonsComposite, REMOVE_ONE);
        bRemoveAll = createButton(buttonsComposite, REMOVE_ALL);
    }

    /**
     * create buttons
     * 
     * @param buttonsComposite
     * @param name
     * @return
     */
    private Button createButton(final Composite buttonsComposite, final String name) {
        Button button = new Button(buttonsComposite, SWT.NONE);
        button.setText(name);
        button.setLayoutData(getGridData());
        button.addSelectionListener(this);
        return button;
    }

    private GridData getGridData() {
        return new GridData(SWT.FILL, SWT.FILL, true, true);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(bAddAll)) {
            changeAllItemPlace(notInListItems, inListItems);
        } else if (e.getSource().equals(bAddOne)) {
            changeOneItemPlace(notInListItems, inListItems);
        } else if (e.getSource().equals(bRemoveAll)) {
            changeAllItemPlace(inListItems, notInListItems);
        } else if (e.getSource().equals(bRemoveOne)) {
            changeOneItemPlace(inListItems, notInListItems);
        } else if (e.getSource().equals(bOk)) {
            fireUpdate();
        } else if (e.getSource().equals(bCancel)) {
            shell.close();
        }
    }

    /**
     * send selected items to {@link ItemsSelectorWidget}
     */
    private void fireUpdate() {
        selectedItems.clear();
        selectedItems.addAll(Arrays.asList(inListItems.getItems()));
        listener.fireSelection(selectedItems);
        shell.close();

    }

    /**
     * change place of single selected item
     * 
     * @param source
     * @param destination
     */
    void changeOneItemPlace(final org.eclipse.swt.widgets.List source, final org.eclipse.swt.widgets.List destination) {
        if (source.getSelectionIndex() < 0) {
            return;
        }
        String selected = source.getItem(source.getSelectionIndex());
        destination.add(selected);
        source.remove(selected);
    }

    /**
     * change place of all selected items
     * 
     * @param source
     * @param destination
     */
    void changeAllItemPlace(final org.eclipse.swt.widgets.List source, final org.eclipse.swt.widgets.List destination) {
        source.removeAll();
        destination.removeAll();
        destination.setItems(allItems.toArray(new String[allItems.size()]));
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }
}
