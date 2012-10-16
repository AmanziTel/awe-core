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

package org.amanzi.awe.ui.view.widgets.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget.IComboSelectionListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractComboWidget<D extends Object, L extends IComboSelectionListener>
        extends
            AbstractLabeledWidget<Combo, L> implements IAWEEventListenter, SelectionListener {

    public interface IComboSelectionListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private static final EventStatus[] SUPPORTED_EVENTS = {EventStatus.DATA_UPDATED, EventStatus.PROJECT_CHANGED};

    private final Comparator<D> itemComparator = new Comparator<D>() {

        @Override
        public int compare(final D o1, final D o2) {
            return getItemName(o1).compareTo(getItemName(o2));
        }

    };

    private final Map<String, D> itemsMap = new HashMap<String, D>();

    private D selectedItem;

    /**
     * @param parent
     * @param label
     */
    protected AbstractComboWidget(final Composite parent, final L listener, final String label, final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);

        AWEEventManager.getManager().addListener(this, SUPPORTED_EVENTS);
    }

    /**
     * @param parent
     * @param label
     */
    protected AbstractComboWidget(final Composite parent, final L listener, final String label) {
        super(parent, listener, label);

        AWEEventManager.getManager().addListener(this, SUPPORTED_EVENTS);
    }

    /**
     * @param parent
     * @param label
     */
    protected AbstractComboWidget(final Composite parent, final L listener, final String label, final int minimalLabelWidth,
            final boolean widthoutListeners) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    public void onEvent(final IEvent event) {
        if (ArrayUtils.contains(SUPPORTED_EVENTS, event.getStatus())) {
            fillCombo();
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public void initializeWidget() {
        super.initializeWidget();
        fillCombo();
    }

    @Override
    protected Combo createControl(final Composite parent) {
        final Combo combo = new Combo(parent, SWT.NONE);

        combo.addSelectionListener(this);

        return combo;
    }

    protected D getSelectedItem() {
        final String itemText = getControl().getText();

        if (!StringUtils.isEmpty(itemText)) {
            selectedItem = itemsMap.get(itemText);
        }

        return selectedItem;
    }

    public void fillCombo() {
        getControl().removeAll();
        itemsMap.clear();

        final Collection<D> items = getItems();
        if (items != null) {
            final List<D> itemList = new ArrayList<D>(items);
            Collections.sort(itemList, itemComparator);
            for (final D item : itemList) {
                final String name = getItemName(item);

                itemsMap.put(name, item);
                getControl().add(name);
            }

            setEnabled(true);
            updateSelection();
        } else {
            setEnabled(false);
        }
    }

    public void skipSelection() {
        selectedItem = null;

        updateSelection();
    }

    public void updateSelection() {
        String text = null;

        if (selectedItem != null) {
            final String selectedItemName = getItemName(selectedItem);

            if (ArrayUtils.contains(getControl().getItems(), selectedItemName)) {
                text = selectedItemName;
            }
        } else {
            if (getDefaultSelectedItem() != null) {
                text = getItemName(getDefaultSelectedItem());
            }
        }

        boolean fireEvent = false;
        if (text == null) {
            text = StringUtils.EMPTY;
            if ((getControl().getItemCount() > 0) && (getDefaultSelectedItemIndex() >= 0)
                    && (getDefaultSelectedItemIndex() < getControl().getItemCount())) {
                text = getControl().getItem(getDefaultSelectedItemIndex());
                fireEvent = true;
            }
        }

        getControl().setText(text);
        if (fireEvent) {
            fireEvent();
        }
    }

    protected D getDefaultSelectedItem() {
        return null;
    }

    protected int getDefaultSelectedItemIndex() {
        return 0;
    }

    protected abstract Collection<D> getItems();

    protected abstract String getItemName(D item);

    @Override
    public void dispose() {
        AWEEventManager.getManager().removeListener(this);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        fireEvent();
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    private void fireEvent() {
        for (final L listener : getListeners()) {
            fireListener(listener, getSelectedItem());
        }
    }

    protected abstract void fireListener(L listener, D selectedItem);

}
