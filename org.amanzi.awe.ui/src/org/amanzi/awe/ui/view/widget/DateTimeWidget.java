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

import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.ui.view.widget.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widget.internal.AbstractLabeledWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DateTimeWidget extends AbstractLabeledWidget<Composite, ITimeChangedListener> implements SelectionListener {

    private static final Calendar CALENDAR = Calendar.getInstance();

    public interface ITimeChangedListener extends AbstractAWEWidget.IAWEWidgetListener {

        void onTimeChanged(Date newTime, DateTimeWidget source);

    }

    private Date originalDate;

    private DateTime date;

    private DateTime time;

    private Button skipButton;

    /**
     * @param parent
     * @param listener
     * @param label
     */
    public DateTimeWidget(final Composite parent, final ITimeChangedListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    protected Composite createControl(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));

        date = addDateTime(composite, SWT.BORDER | SWT.DATE);
        time = addDateTime(composite, SWT.BORDER | SWT.TIME);

        skipButton = new Button(composite, SWT.NONE);
        skipButton.setText("X");
        skipButton.addSelectionListener(this);
        skipButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        return composite;
    }

    private DateTime addDateTime(final Composite parent, int style) {
        DateTime result = new DateTime(parent, style);

        result.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        result.setVisible(true);

        return result;
    }

    private void updateDate(final Date newDate) {
        CALENDAR.setTime(newDate);

        date.setDate(CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH), CALENDAR.get(Calendar.DAY_OF_MONTH));

        time.setHours(CALENDAR.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(CALENDAR.get(Calendar.MINUTE));
        time.setSeconds(CALENDAR.get(Calendar.SECOND));
    }

    public void setDate(final Date newDate) {
        this.originalDate = newDate;

        updateDate(newDate);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        updateDate(originalDate);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

}
