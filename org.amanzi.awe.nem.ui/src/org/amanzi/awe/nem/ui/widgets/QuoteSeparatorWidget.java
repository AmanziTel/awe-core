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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.QuoteSeparatorWidget.IQouteChangedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class QuoteSeparatorWidget extends AbstractComboWidget<String, IQouteChangedListener> {
    public interface IQouteChangedListener extends AbstractComboWidget.IComboSelectionListener {
        void onQuoteChanged(Character quote);
    }

    private static final Map<String, Character> QUOTE_MAPPING;

    static {
        QUOTE_MAPPING = new HashMap<String, Character>();
        QUOTE_MAPPING.put("Double quotes (\")", '"');
        QUOTE_MAPPING.put("Singe quote (\')", '\'');
    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    public QuoteSeparatorWidget(final Composite parent, final IQouteChangedListener listener, final int minimalLabelWidth) {
        super(parent, listener, NEMMessages.QUOTE_COMBO_LABEL, minimalLabelWidth);
    }

    @Override
    protected void fireListener(final IQouteChangedListener listener, final String selectedItem) {
        listener.onQuoteChanged(QUOTE_MAPPING.get(selectedItem));
    }

    @Override
    protected String getItemName(final String item) {
        return item;
    }

    @Override
    protected Collection<String> getItems() {
        return QUOTE_MAPPING.keySet();
    }
}
