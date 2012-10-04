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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TextWidget extends AbstractLabeledWidget<Text, ITextChandedListener> {

    public interface ITextChandedListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected TextWidget(final Composite parent, final ITextChandedListener listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Text createControl(final Composite parent) {
        Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);

        return text;
    }
}
