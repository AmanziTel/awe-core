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

package org.amanzi.awe.ui.view.widget;

import java.util.Date;

import org.amanzi.awe.ui.view.widget.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widget.PeriodWidget.ITimePeriodSelectionListener;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PeriodWidget extends AbstractAWEWidget<Composite, ITimePeriodSelectionListener> implements ITimeChangedListener {

    public interface ITimePeriodSelectionListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private DateTimeWidget minTimestampWidget;

    private DateTimeWidget maxTimestampWidget;

    private final String minTimestampLabel;

    private final String maxTimestampLabel;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    protected PeriodWidget(Composite parent, ITimePeriodSelectionListener listener, String minTimestampLabel,
            String maxTimestampLabel) {
        super(parent, SWT.NONE, listener);
        this.minTimestampLabel = minTimestampLabel;
        this.maxTimestampLabel = maxTimestampLabel;
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(new GridLayout(2, false));

        minTimestampWidget = addDateTimeWidget(composite, minTimestampLabel);
        maxTimestampWidget = addDateTimeWidget(composite, maxTimestampLabel);

        return composite;
    }

    private DateTimeWidget addDateTimeWidget(Composite parent, String label) {
        DateTimeWidget widget = new DateTimeWidget(parent, this, label);
        widget = AWEWidgetFactory.getFactory().initializeWidget(widget);

        widget.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        return widget;
    }

    public void setPeriod(long minTimestamp, long maxTimestamp) {
        minTimestampWidget.setDate(new Date(minTimestamp));
        maxTimestampWidget.setDate(new Date(maxTimestamp));
    }

    @Override
    public void onTimeChanged(Date newTime, DateTimeWidget source) {
    }

}
