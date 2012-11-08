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

package org.amanzi.awe.ui.view.widgets;

import java.nio.charset.Charset;
import java.util.Collection;

import org.amanzi.awe.ui.internal.Messages;
import org.amanzi.awe.ui.view.widgets.CharsetWidget.ICharsetChangedListener;
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
public class CharsetWidget extends AbstractComboWidget<Charset, ICharsetChangedListener> {
    public interface ICharsetChangedListener extends AbstractComboWidget.IComboSelectionListener {
        void onCharsetChanged(Charset charset);
    }

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    protected CharsetWidget(final Composite parent, final ICharsetChangedListener listener) {
        super(parent, listener, Messages.charsetWidgetLabel, 0);
    }

    @Override
    protected void fireListener(final ICharsetChangedListener listener, final Charset selectedItem) {
        listener.onCharsetChanged(selectedItem);

    }

    @Override
    protected Charset getDefaultSelectedItem() {
        return Charset.defaultCharset();
    }

    @Override
    protected String getItemName(final Charset item) {
        return item.name();
    }

    @Override
    protected Collection<Charset> getItems() {
        return Charset.availableCharsets().values();
    }
}
