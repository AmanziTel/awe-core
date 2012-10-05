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

import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;

import org.amanzi.awe.ui.view.widgets.ColorWidget.IColorChangedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ColorWidget extends AbstractAWEWidget<Button, IColorChangedListener> {

    public interface IColorChangedListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private ColorEditor colorEditor;

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected ColorWidget(final Composite parent, final IColorChangedListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected Button createWidget(final Composite parent, final int style) {
        colorEditor = new ColorEditor(parent);

        return colorEditor.getButton();
    }
}
