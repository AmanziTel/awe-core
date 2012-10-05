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

import java.awt.Color;

import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;

import org.amanzi.awe.ui.view.widgets.ColorWidget.IColorChangedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
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
public class ColorWidget extends AbstractAWEWidget<Button, IColorChangedListener> implements SelectionListener {

    public interface IColorChangedListener extends AbstractAWEWidget.IAWEWidgetListener {

        void onColorChanged(Color color, ColorWidget source);

    }

    private ColorEditor colorEditor;

    private final String tooltip;

    /**
     * @param parent
     * @param listener
     * @param tooltip TODO
     * @param label
     */
    protected ColorWidget(final Composite parent, final IColorChangedListener listener, final String tooltip) {
        super(parent, SWT.NONE, listener);

        this.tooltip = tooltip;
    }

    @Override
    protected Button createWidget(final Composite parent, final int style) {
        colorEditor = new ColorEditor(parent);

        colorEditor.getButton().setToolTipText(tooltip);
        colorEditor.addSelectionListener(this);

        return colorEditor.getButton();
    }

    public void setColor(final Color color) {
        colorEditor.setColorValue(convertToRGB(color));
    }

    public Color getColor() {
        return convertToColor(colorEditor.getColorValue());
    }

    private RGB convertToRGB(final Color color) {
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    private Color convertToColor(final RGB rgb) {
        return new Color(rgb.red, rgb.green, rgb.blue);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        for (final IColorChangedListener listener : getListeners()) {
            listener.onColorChanged(convertToColor(colorEditor.getColorValue()), this);
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }
}
