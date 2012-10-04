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

import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLabeledWidget<C extends Control, L extends IAWEWidgetListener>
extends
AbstractAWEWidget<Composite, L> {

    private static final int NO_MINIMAL_LABEL_WIDTH = -1;

    private static final GridLayout DEFAULT_LABELED_COMBO_LAYOUT = new GridLayout(2, false);

    private final String label;

    private C control;

    private Label controlLabel;

    private int minialLabelWidth;

    /**
     * @param parent
     * @param style
     */
    protected AbstractLabeledWidget(final Composite parent, final L listener, final String label) {
        this(parent, listener, label, NO_MINIMAL_LABEL_WIDTH);
    }

    protected AbstractLabeledWidget(final Composite parent, final L listener, final String label, final int minimalLabelWidth) {
        super(parent, SWT.NONE, listener);
        this.label = label;
        this.minialLabelWidth = minimalLabelWidth;
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(DEFAULT_LABELED_COMBO_LAYOUT);
        composite.setLayoutData(getControlLayoutData());

        controlLabel = new Label(composite, SWT.NONE);
        controlLabel.setText(label);
        controlLabel.setLayoutData(getLabelLayoutData());

        control = createControl(composite);
        control.setLayoutData(getElementLayoutData());

        return composite;
    }

    private GridData getControlLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, false, false);
    }


    protected abstract C createControl(Composite parent);

    private GridData getElementLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    }

    private GridData getLabelLayoutData() {
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);

        if (minialLabelWidth > 0) {
            gridData.widthHint = minialLabelWidth;
        }

        return gridData;
    }

    protected C getControl() {
        return control;
    }

    @Override
    public void setEnabled(final boolean isEnabled) {
        super.setEnabled(isEnabled);

        control.setEnabled(isEnabled);
        controlLabel.setEnabled(isEnabled);
    }

    @Override
    public void setVisible(final boolean isVisible) {
        super.setVisible(isVisible);

        control.setVisible(isVisible);
        controlLabel.setVisible(isVisible);
    }

}
