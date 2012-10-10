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

import org.amanzi.awe.ui.view.widgets.TextWidget.ITextChandedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TextWidget extends AbstractLabeledWidget<Text, ITextChandedListener> implements ModifyListener {

    public interface ITextChandedListener extends AbstractAWEWidget.IAWEWidgetListener {

        void onTextChanged(String text);

    }

    private Text text;
    private int controlStyle;

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected TextWidget(final Composite parent, int controlStyle, final ITextChandedListener listener, final String label) {
        super(parent, listener, label);
        this.controlStyle = controlStyle;
    }

    @Override
    protected Text createControl(final Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text = new Text(parent, controlStyle);
        text.addModifyListener(this);
        return text;
    }

    @Override
    protected GridData getElementLayoutData() {
        return new GridData(GridData.FILL_HORIZONTAL);
    }

    public void setText(final String text) {
        getControl().setText(text);
    }

    @Override
    public void modifyText(ModifyEvent e) {
        for (ITextChandedListener listener : getListeners()) {
            listener.onTextChanged(text.getText());
        }

    }
}
