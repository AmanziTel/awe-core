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
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget.ITableItemSelectionListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.amanzi.neo.models.network.NetworkElementType;
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
public class TypeControlWidget extends AbstractAWEWidget<Composite, ITableItemSelectionListener> implements SelectionListener {

    public interface ITableItemSelectionListener extends IAWEWidgetListener {

    }

    private static final GridLayout TWO_COLUMNS_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMNS_LAYOUT = new GridLayout(1, false);

    private List lStructuredTypes;

    private Button bAdd;

    private Button bUp;

    private Button bDown;

    private Button bRemove;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public TypeControlWidget(Composite parent, int style, ITableItemSelectionListener listener) {
        super(parent, style, listener);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite widgetComposite = new Composite(parent, SWT.NONE);
        widgetComposite.setLayout(TWO_COLUMNS_LAYOUT);
        widgetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite elementComposite = new Composite(widgetComposite, SWT.NONE);
        elementComposite.setLayout(ONE_COLUMNS_LAYOUT);
        elementComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        lStructuredTypes = new List(elementComposite, SWT.BORDER);
        lStructuredTypes.setLayoutData(new GridData(GridData.FILL_BOTH));

        initDefaultElements();

        Composite buttonComposite = new Composite(widgetComposite, SWT.NONE);
        buttonComposite.setLayout(ONE_COLUMNS_LAYOUT);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        bAdd = createButton(buttonComposite, NemMessages.ADD);
        bUp = createButton(buttonComposite, NemMessages.UP);
        bDown = createButton(buttonComposite, NemMessages.DOWN);
        bRemove = createButton(buttonComposite, NemMessages.REMOVE);
        return widgetComposite;
    }

    /**
     *
     */
    private void initDefaultElements() {
        lStructuredTypes.add(NetworkElementType.NETWORK.getId());
        lStructuredTypes.add(NetworkElementType.SITE.getId());
        lStructuredTypes.add(NetworkElementType.SECTOR.getId());

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
            changePosition(lStructuredTypes.getSelectionIndex(), ++nextIndex);
        } else if (e.getSource().equals(bDown)) {
            int nextIndex = lStructuredTypes.getSelectionIndex();
            changePosition(lStructuredTypes.getSelectionIndex(), --nextIndex);
        } else if (e.getSource().equals(bRemove)) {
            lStructuredTypes.remove(lStructuredTypes.getSelectionIndex());
        } else if (e.getSource().equals(bAdd)) {

        }
    }

    /**
     * @param selectionIndex
     * @param
     */
    private void changePosition(int sourcePosition, int targetPosition) {
        if (lStructuredTypes.getItemCount() < targetPosition || lStructuredTypes.getItemCount() > targetPosition) {
            return;
        }

        String sourceItem = lStructuredTypes.getItem(sourcePosition);
        String targetItem = lStructuredTypes.getItem(targetPosition);
        lStructuredTypes.setItem(targetPosition, sourceItem);
        lStructuredTypes.setItem(sourcePosition, targetItem);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
