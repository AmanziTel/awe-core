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
import org.amanzi.awe.nem.ui.widgets.ExportSeparatorWidget.ISeparatorChangedListener;
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
public class ExportSeparatorWidget extends AbstractComboWidget<String, ISeparatorChangedListener> {
    public interface ISeparatorChangedListener extends AbstractComboWidget.IComboSelectionListener {
        void onSeparatorChanged(String separator);
    }

    private static final Map<String, String> SEPARATOR_MAPPING;

    static {
        SEPARATOR_MAPPING = new HashMap<String, String>();
        SEPARATOR_MAPPING.put("SPACE", " ");
        SEPARATOR_MAPPING.put("TAB", "\t");
        SEPARATOR_MAPPING.put("DOT", "\\.");
        SEPARATOR_MAPPING.put("SEMICOLON", ";");
    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    public ExportSeparatorWidget(final Composite parent, final ISeparatorChangedListener listener) {
        super(parent, listener, NEMMessages.SEPARATOR_COMBO_LABEL);
    }

    @Override
    protected void fireListener(final ISeparatorChangedListener listener, final String selectedItem) {
        listener.onSeparatorChanged(SEPARATOR_MAPPING.get(selectedItem));
    }

    @Override
    protected String getItemName(final String item) {
        return item;
    }

    @Override
    protected Collection<String> getItems() {
        return SEPARATOR_MAPPING.keySet();
    }
}
