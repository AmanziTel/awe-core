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

import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget.ITableItemSelectionListener;
import org.amanzi.awe.nem.ui.widgets.TypesCreationDialog.ITypesCreationDialogListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TypeControlWidget extends AbstractAWEWidget<Composite, ITableItemSelectionListener>
        implements
            SelectionListener,
            ITypesCreationDialogListener {

    public interface ITableItemSelectionListener extends IAWEWidgetListener {
        void onStatusUpdate(int code, String message);
    }

    private static final int END_SIZE_BORDER = 2;

    private static final GridLayout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMNS_LAYOUT = new GridLayout(1, false);

    private List lStructuredTypes;

    private Button bAdd;

    private Button bUp;

    private Button bDown;

    private Button bRemove;

    private java.util.List<String> defaultNodeTypesId = new ArrayList<String>();

    /**
     * @param parent
     * @param style
     * @param listener
     * @param list
     */
    public TypeControlWidget(Composite parent, int style, ITableItemSelectionListener listener,
            java.util.List<INodeType> defaultNodeTypes) {
        super(parent, style, listener);
        for (INodeType type : defaultNodeTypes) {
            defaultNodeTypesId.add(type.getId());
        }
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite widgetComposite = new Composite(parent, SWT.NONE);
        widgetComposite.setLayout(TWO_COLUMNS_LAYOUT);
        widgetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite elementComposite = new Composite(widgetComposite, SWT.NONE);
        elementComposite.setLayout(ONE_COLUMNS_LAYOUT);
        elementComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        lStructuredTypes = new List(elementComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        lStructuredTypes.setLayoutData(new GridData(GridData.FILL_BOTH));

        initDefaultElements();

        Composite buttonComposite = new Composite(widgetComposite, SWT.NONE);
        buttonComposite.setLayout(ONE_COLUMNS_LAYOUT);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        bAdd = createButton(buttonComposite, NEMMessages.ADD);
        bUp = createButton(buttonComposite, NEMMessages.UP);
        bDown = createButton(buttonComposite, NEMMessages.DOWN);
        bRemove = createButton(buttonComposite, NEMMessages.REMOVE);
        lStructuredTypes.addSelectionListener(this);

        setButtonEnabled(false, bRemove, bUp, bDown);
        return widgetComposite;
    }

    /**
     *
     */
    private void initDefaultElements() {
        lStructuredTypes.setItems(defaultNodeTypesId.toArray(new String[defaultNodeTypesId.size()]));
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
        if (e.getSource().equals(bUp)) {
            int nextIndex = lStructuredTypes.getSelectionIndex();
            changePosition(lStructuredTypes.getSelectionIndex(), --nextIndex);
        } else if (e.getSource().equals(bDown)) {
            int nextIndex = lStructuredTypes.getSelectionIndex();
            changePosition(lStructuredTypes.getSelectionIndex(), ++nextIndex);
        } else if (e.getSource().equals(bRemove)) {
            int nextIndex = lStructuredTypes.getSelectionIndex();
            if (defaultNodeTypesId.get(nextIndex).equals(NetworkElementType.SITE.getId())) {
                int sectorIndex = defaultNodeTypesId.indexOf(NetworkElementType.SECTOR.getId());
                if (sectorIndex > 0) {
                    lStructuredTypes.remove(sectorIndex);
                    defaultNodeTypesId.remove(sectorIndex);
                }
            }
            lStructuredTypes.remove(lStructuredTypes.getSelectionIndex());
            defaultNodeTypesId.remove(nextIndex);

            lStructuredTypes.select(0);
            updateButtons(lStructuredTypes.getSelectionIndex());

        } else if (e.getSource().equals(bAdd)) {
            TypesCreationDialog typesDialog = new TypesCreationDialog(getWidget(), SWT.NONE, this, defaultNodeTypesId);
            typesDialog.initializeWidget();
        } else if (e.getSource().equals(lStructuredTypes)) {
            updateButtons(lStructuredTypes.getSelectionIndex());
        }
        updateDirectionButtons(lStructuredTypes.getSelectionIndex());
    }

    /**
     * @param selectionIndex
     */
    private void updateButtons(int selectionIndex) {
        setButtonEnabled(true, bAdd, bRemove, bUp, bDown);
        String type = defaultNodeTypesId.get(selectionIndex);
        if (type.equals(NetworkElementType.NETWORK.getId())) {
            setButtonEnabled(false, bRemove, bUp, bDown);
        } else if (type.equals(NetworkElementType.SITE.getId()) || type.equals(NetworkElementType.SECTOR.getId())) {
            setButtonEnabled(false, bAdd, bUp, bDown);
        }

    }

    /**
     * @param selectionIndex
     */
    private void updateDirectionButtons(int selectionIndex) {
        setButtonEnabled(isValidOrder(selectionIndex, SWT.UP), bUp);
        setButtonEnabled(isValidOrder(selectionIndex, SWT.DOWN), bDown);

    }

    /**
     * @param b
     */
    private void setButtonEnabled(boolean condition, Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(condition);
        }
    }

    /**
     * @param selectionIndex
     * @param
     */
    private void changePosition(int sourcePosition, int targetPosition) {
        String sourceItem = lStructuredTypes.getItem(sourcePosition);
        String targetItem = lStructuredTypes.getItem(targetPosition);
        lStructuredTypes.setItem(targetPosition, sourceItem);
        lStructuredTypes.setItem(sourcePosition, targetItem);
        lStructuredTypes.select(targetPosition);
    }

    /**
     * check for replace position enabled
     * 
     * @param sourcePosition
     * @param targetPosition
     * @return
     */
    private boolean isValidOrder(int sourcePosition, int direction) {
        if (sourcePosition <= 0) {
            return false;
        }
        int nextPosition;
        boolean result = false;
        switch (direction) {
        case SWT.UP:
            nextPosition = --sourcePosition;
            if (nextPosition < lStructuredTypes.getItemCount() - END_SIZE_BORDER && nextPosition != 0) {
                result = true;
            }
            break;
        case SWT.DOWN:
            nextPosition = ++sourcePosition;
            if (nextPosition < lStructuredTypes.getItemCount() - END_SIZE_BORDER) {
                result = true;
            }
            break;
        default:
            return false;
        }
        return result;

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNewTypeAdded(String newType) {
        int selectedItem = lStructuredTypes.getSelectionIndex();
        if (selectedItem < 1
                || (selectedItem >= lStructuredTypes.getItemCount() - END_SIZE_BORDER && defaultNodeTypesId
                        .contains(NetworkElementType.SITE.getId()))) {
            selectedItem = 0;
        }
        if (newType.equals(NetworkElementType.SITE.getId())) {
            selectedItem = defaultNodeTypesId.size() - END_SIZE_BORDER;
        }
        // shift position of new elements
        ++selectedItem;
        defaultNodeTypesId.add(selectedItem, newType);

        lStructuredTypes.removeAll();
        lStructuredTypes.setItems(defaultNodeTypesId.toArray(new String[defaultNodeTypesId.size()]));
        lStructuredTypes.select(selectedItem);
        updateDirectionButtons(selectedItem);
    }

    public java.util.List<String> getStructure() {
        return defaultNodeTypesId;
    }
}
